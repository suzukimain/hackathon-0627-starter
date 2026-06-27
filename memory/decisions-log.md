# Decisions Log（DECISIONS.md 編集用の素材）

最終的に `DECISIONS.md` へ ADR 形式でまとめる。ここはスライスごとの判断の蓄積。

## ADR-001: 神メソッドを Ports & Adapters + コンストラクタDI で分解
- 状況: `register()` 1メソッドにバリデーション/重複/ハッシュ/保存/送信/ログが密結合。
  `new Database()`/`new EmailClient()` のハードコードでユニットテスト不能。
- 決定: 依存を `UserRepository`/`PasswordEncoder`/`EmailSender` の interface（ポート）に切り出し、
  コンストラクタDIで注入。具象はアダプタとして分離。
- 理由: テスト時に fake へ差し替え可能になり、責務境界が明確になる。
- トレードオフ: クラス数が増える。小規模では過剰に見えるが、肉付け(OAuth)で同じ後続処理を
  再利用するため境界が必須。

## ADR-002: 重複検出を Optional<User> で表現
- 状況: 元コードは `findByEmail(...) != null` の null 比較。
- 決定: `UserRepository.findByEmail` の戻り値を `Optional<User>` にする。
- 理由: null スメル除去、存在判定の意図が明確。
- トレードオフ: なし（実質的に改善のみ）。

## ADR-003: InMemoryUserRepository を実際に永続化させる
- 状況: 元の Database モックは save が id を振るだけで保存せず、findByEmail は常に null。
  → 重複チェックが事実上機能していなかった。
- 決定: `InMemoryUserRepository` は Map に保存し、findByEmail で実際に検索する。
- 理由: 重複チェックを機能させ、後続スライスのテストの土台にする。
- トレードオフ: 元の「常に null」挙動からの変更だが、これは潜在バグの修正。

## ADR-009: OAuth サインアップを Strategy パターンで構成
- 状況: GitHub 登録を追加。将来 Google/LINE も追加予定。後続処理はパスワード登録と共通化したい。
- 決定: `OAuthIdentityProvider`(ポート)＋プロバイダ別アダプタ＋`OAuthProviderRegistry`(enum 解決)。
  `OAuthRegistrationService` が認可コード→正規化 `OAuthProfile`→User を組み立て、共通
  `UserRegistrar` に委譲。User に provider/providerUserId を追加し password は nullable。
- 理由: Open/Closed。新プロバイダはアダプタ実装＋レジストリ登録のみでコア・入口を変更しない。
  後続処理（ウェルカムメール・ログ）は ADR-006 のコアで自動的に共通化。
- トレードオフ: 抽象が増える。プロバイダ1個なら過剰だが、複数追加予定なので妥当。

## ADR-010: OAuth はモックアダプタで実装
- 状況: 実 GitHub OAuth は認可リダイレクト・トークン交換・シークレット管理が必要。
- 決定: `GitHubOAuthProvider` は authorizationCode から擬似プロフィールを返すモック実装。
- 理由: ハッカソンの時間・シークレット制約。設計（拡張ポイント）が評価対象であり、実 HTTP は
  ポート差し替えで後付け可能。
- トレードオフ: 実認可フローは未実装。実トークン交換・state 検証・メール未提供時の扱いは「次の一手」。

## ADR-005: throws Exception を unchecked 型付き例外階層に置換
- 状況: `register()` が `throws Exception`。呼び出し側が入力エラーと衝突と基盤障害を区別できない。
- 決定: `RegistrationException`(基底, RuntimeException) の下に
  `ValidationException`(400相当) / `DuplicateEmailException`(409相当) を定義。
  基盤障害用の `InfrastructureException`(500相当) は S4 で追加。
- 理由: 全シグネチャから `throws Exception` を排除しつつ、catch を意味別に分離可能にする。
- トレードオフ: unchecked のため compiler が処理を強制しない。登録ユースケースに呼び出し側の
  現実的なリカバリ手段がないため許容し、基底型での catch-all を用意して補う。

## ADR-008: バリデーションは Validator クラスに抽出（値オブジェクト不採用）
- 状況: バリデーションが register に inline。SRP 違反でテストしづらい。
- 決定: `UserRegistrationValidator` に抽出し明示ルールメソッド化。Email/Password の値オブジェクトは
  採用しない。
- 理由: 抽出が SRP の主要な改善。値オブジェクトは時間制約に対し得る安全性が小さく過剰設計。
- トレードオフ: 型レベルの保証は得られない。将来必要なら値オブジェクト化／複数エラー集約へ拡張可能
  （「次の一手」）。

## ADR-006: 後続処理を共通コア UserRegistrar に集約
- 状況: 後続処理（重複チェック・保存・確認メール・ログ）が register に密結合。OAuth 追加時に
  同じ処理を二重に書くと分岐・不整合のリスク。
- 決定: `UserRegistrar.register(User)` に後続処理を集約。パスワード入口/OAuth 入口の両方がここを通す。
- 理由: 「同じ後続処理を通す」という要件を構造で保証し、将来の登録経路追加を低コスト化。
- トレードオフ: 入口とコアで責務を2分割するぶんクラスが増えるが、再利用性で回収。

## ADR-007: 保存→メールの順序とメール失敗時の方針
- 状況: 元コードは保存後にメール送信。送信失敗時の挙動が未定義（トランザクション境界なし）。
- 決定: validate→重複→encode→保存→**その後**メール。メール送信失敗は登録成功扱いとし警告ログを残す。
- 理由: 永続化が source of truth。確認メールは再送可能な best-effort で、失敗で登録を巻き戻すと
  かえって UX/整合性を損なう。
- トレードオフ: 確認メール未達の利用者が一時的に発生しうる。本番は outbox / transactional
  messaging / saga 補償が必要 → 「次の一手」。

## ADR-004: パスワードハッシュを SHA-256 に置換（偽ハッシュ廃止）
- 状況: 元コードは `password + "_hashed"`。ハッシュでもソルトでもなく平文同然。
- 決定: `PasswordEncoder` ポート越しに `Sha256PasswordEncoder`（JDK MessageDigest, hex）を採用。
  S1 の暫定 `LegacyPasswordEncoder` は削除。生パスワードは User に保存しない（テストで保証）。
- 理由: 外部依存を増やさず JDK 標準で明確な改善を入れる。
- トレードオフ: ソルトなし SHA-256 はレインボーテーブルに弱い。本番は**ソルト付き bcrypt/argon2**
  が必要 → 「次の一手」。ポート化済みなので実装差し替えのみで移行可能。
