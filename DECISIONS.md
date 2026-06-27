# 設計判断ログ

> チーム名：（記入してください）

設計中の「なぜそうしたか」を ADR 形式で記録する。日時は便宜上すべて 2026-06-27（ハッカソン当日）。

---

## 判断ログ一覧

### [ADR-001] 神メソッドを Ports & Adapters + コンストラクタDI で分解

**日時**：2026-06-27

**状況**：`register()` 1メソッドにバリデーション/重複/ハッシュ/保存/送信/ログが密結合。
`new Database()` / `new EmailClient()` のハードコードでユニットテスト不能。

**決定**：依存を `UserRepository` / `PasswordEncoder` / `EmailSender` の interface（ポート）に
切り出し、コンストラクタDIで注入。具象はアダプタとして分離。

**理由**：テスト時に fake へ差し替え可能になり、責務境界が明確になる。後続の OAuth 拡張で同じ
後続処理を再利用するため境界が必須。

**トレードオフ**：クラス数が増える。小規模では過剰に見えるが、テスト容易性と再利用性で回収する。

---

### [ADR-002] 重複検出を Optional<User> で表現

**日時**：2026-06-27

**状況**：元コードは `findByEmail(...) != null` の null 比較で、null 安全でなく意図も不明瞭。

**決定**：`UserRepository.findByEmail` の戻り値を `Optional<User>` にする。

**理由**：null スメルを除去し、存在判定の意図を明確にする。

**トレードオフ**：特になし（実質的に改善のみ）。

---

### [ADR-003] InMemoryUserRepository を実際に永続化させる

**日時**：2026-06-27

**状況**：元の Database モックは save が id を振るだけで保存せず、findByEmail は常に null を返す
ため、重複チェックが事実上機能していなかった。

**決定**：`InMemoryUserRepository` は Map に保存し、findByEmail で実際に検索する。

**理由**：重複チェックを機能させ、後続スライスのテストの土台にする。

**トレードオフ**：元の「常に null」挙動からの変更だが、これは潜在バグの修正と位置づける。

---

### [ADR-004] パスワードハッシュを SHA-256 に置換（偽ハッシュ廃止）

**日時**：2026-06-27

**状況**：元コードは `password + "_hashed"`。ハッシュでもソルトでもなく平文同然のセキュリティ欠陥。

**決定**：`PasswordEncoder` ポート越しに `Sha256PasswordEncoder`（JDK 標準 MessageDigest, hex 出力）を
採用。生パスワードは User に保存しない（テストで保証）。

**理由**：外部依存を増やさず JDK 標準で明確な改善を入れられる。

**トレードオフ**：ソルトなし SHA-256 はレインボーテーブルに弱い。本番はソルト付き **bcrypt / argon2** が
必要。ポート化済みのため実装差し替えのみで移行できる。

---

### [ADR-005] throws Exception を unchecked 型付き例外階層に置換

**日時**：2026-06-27

**状況**：`register()` が `throws Exception`。呼び出し側が入力エラー・重複・基盤障害を区別できない。

**決定**：`RegistrationException`（基底, RuntimeException）の下に `ValidationException`(400 相当)、
`DuplicateEmailException`(409 相当)、`InfrastructureException`(500 相当, cause 保持) を定義。

**理由**：全シグネチャから `throws Exception` を排除しつつ、catch を意味別に分離できるようにする。

**トレードオフ**：unchecked のため compiler が処理を強制しない。登録ユースケースに現実的なリカバリ手段が
ないため許容し、基底型での catch-all を用意して補う。

---

### [ADR-006] 後続処理を共通コア UserRegistrar に集約

**日時**：2026-06-27

**状況**：後続処理（重複チェック・保存・確認メール・ログ）が register に密結合。OAuth 追加時に同じ処理を
二重に書くと分岐・不整合のリスクがある。

**決定**：`UserRegistrar.register(User)` に後続処理を集約し、パスワード入口・OAuth 入口の両方がここを通す。

**理由**：「同じ後続処理を通す」という要件を構造で保証し、将来の登録経路追加を低コストにする。

**トレードオフ**：入口とコアで責務を2分割するぶんクラスが増えるが、再利用性で回収する。

---

### [ADR-007] 保存→メールの順序とメール失敗時の方針

**日時**：2026-06-27

**状況**：元コードは保存後にメール送信。送信失敗時の挙動が未定義でトランザクション境界がない。

**決定**：validate→重複→encode→保存→**その後**メール送信。メール送信失敗は登録成功扱いとし警告ログを残す。

**理由**：永続化が source of truth。確認メールは再送可能な best-effort であり、失敗で登録を巻き戻すと
かえって UX と整合性を損なう。

**トレードオフ**：確認メール未達の利用者が一時的に発生しうる。本番は outbox / transactional messaging /
saga 補償で確実な再送を担保すべき。

---

### [ADR-008] バリデーションは Validator クラスに抽出（値オブジェクト不採用）

**日時**：2026-06-27

**状況**：バリデーションが register に inline で、SRP 違反かつテストしづらい。

**決定**：`UserRegistrationValidator` に抽出し明示的なルールメソッドに分割。Email/Password の値オブジェクトは
採用しない。

**理由**：抽出が SRP の主要な改善。値オブジェクトは時間制約に対して得られる安全性が小さく過剰設計になりやすい。

**トレードオフ**：型レベルの保証は得られない。将来必要なら値オブジェクト化や複数エラー集約に拡張できる。

---

### [ADR-009] OAuth サインアップを Strategy パターンで構成

**日時**：2026-06-27

**状況**：GitHub 登録を追加し、将来 Google/LINE も追加予定。後続処理はパスワード登録と共通化したい。

**決定**：`OAuthIdentityProvider`（ポート）＋プロバイダ別アダプタ＋`OAuthProviderRegistry`（enum 解決）。
`OAuthRegistrationService` が認可コード→正規化 `OAuthProfile`→User を組み立て、共通 `UserRegistrar` に委譲。
User に `provider` / `providerUserId` を追加し password を nullable にした。

**理由**：Open/Closed の原則。新プロバイダはアダプタ実装＋レジストリ登録のみで、コア・入口は無変更。後続処理は
ADR-006 のコアで自動的に共通化される。

**トレードオフ**：抽象が増える。プロバイダ1個なら過剰だが、複数追加予定のため妥当と判断。

---

### [ADR-010] OAuth はモックアダプタで実装

**日時**：2026-06-27

**状況**：実 GitHub OAuth は認可リダイレクト・トークン交換・シークレット管理が必要。

**決定**：`GitHubOAuthProvider` は authorizationCode から擬似プロフィールを返すモック実装にとどめる。

**理由**：ハッカソンの時間・シークレット制約。評価対象は設計（拡張ポイント）であり、実 HTTP はポート差し替えで
後付けできる。

**トレードオフ**：実認可フローは未実装。実トークン交換・state 検証・メール未提供時の扱いは今後の改善点。

---

### [ADR-011] Maven 標準レイアウト採用とビルド基盤の非導入

**日時**：2026-06-27

**状況**：README はデリバラブルを `src/UserRegistrationService.java` と指定。一方で複数クラス分割と JUnit には
パッケージ構成が望ましい。実行環境に JDK/Maven は未導入。

**決定**：`src/main/java/com/youtrust/hackathon/`（テストは `src/test/java`）の Maven 標準レイアウトを採用。
ただしビルド基盤（pom.xml・JDK/Maven 導入）は今回のスコープ外とし、JUnit テストは作成のみで実行しない。

**理由**：idiomatic な構成でレビューしやすく、後から pom.xml を足せば即ビルド可能。サービス本体は `src/` 配下に
存在し、README の趣旨は満たす。

**トレードオフ**：README の厳密なファイルパスからは逸脱する。テストをローカル実行できないため、検証はコード
レビュー中心になる。
