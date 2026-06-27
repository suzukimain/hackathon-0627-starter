# 設計ドキュメント

> チーム名：（記入してください）
> メンバー：（記入してください）

---

## 1. 課題の整理

スターターコードの問題点を重要度別に整理した。

**最重要（正確性・セキュリティ・全ての土台）**
- パスワードハッシュが偽物（`password + "_hashed"`）で平文同然。
- 依存を `new Database()` / `new EmailClient()` で直接生成しており、差し替え不能＝ユニットテスト不能。
- 例外が `throws Exception` で粒度が粗く、入力エラー・重複・基盤障害を呼び出し側が区別できない。

**高（保守性・整合性）**
- `register()` 1メソッドにバリデーション・重複チェック・ハッシュ化・保存・メール送信・ログが密集
  （単一責任の崩壊）。
- 保存後にメール送信が失敗したときの挙動が未定義（トランザクション境界なし）。

**中（堅牢性・検証）**
- バリデーションが貧弱（email は `@` 確認のみ）でロジックが散在。
- テストが存在しない。

---

## 2. 設計方針

- **Ports & Adapters（ヘキサゴナル）+ コンストラクタDI** — 外部依存（DB・メール・ハッシュ・OAuth）を
  interface（ポート）で抽象化し、具象をアダプタとして分離。テスト時は fake に差し替える。
- **後続処理の共通コア化** — 「重複チェック→保存→ウェルカムメール→ログ」を `UserRegistrar` に集約し、
  パスワード登録・OAuth 登録の両入口が必ず同じコアを通る。
- **型付き例外階層（unchecked）** — `throws Exception` を廃し、意味別の例外で呼び出し側が分岐可能に。
- **拡張点の明示（Strategy）** — OAuth プロバイダを enum + アダプタ + レジストリで構成し、Google/LINE は
  アダプタ追加だけで有効化できる構造にした。
- **過剰設計の回避** — 値オブジェクトや DI コンテナ（Spring）等は今回採用せず、`createDefault()` の
  簡易合成ルートで完結させた。採用しなかった選択肢は「今後の改善点」と DECISIONS.md に記録。

---

## 3. クラス・メソッド構成

```
（入口 / facade）
UserRegistrationService     // パスワード登録: バリデーション→ハッシュ化→User組み立て→委譲
OAuthRegistrationService    // OAuth登録: 認可コード→正規化プロフィール→User組み立て→委譲

（共通コア）
registrar/UserRegistrar     // 重複チェック→保存→ウェルカムメール→ログ（両入口が通る）

（バリデーション）
validation/UserRegistrationValidator

（ポート = interface）
port/UserRepository  PasswordEncoder  EmailSender  OAuthIdentityProvider

（アダプタ = 具象）
adapter/InMemoryUserRepository  Sha256PasswordEncoder  ConsoleEmailSender  GitHubOAuthProvider

（OAuth 拡張）
oauth/OAuthProvider(enum: GITHUB, GOOGLE, LINE)  OAuthProviderRegistry(Strategy 解決)

（モデル）
model/User  RegisterInput  RegisterResult  OAuthProfile

（例外階層）
exception/RegistrationException(基底)
          ├ ValidationException        (入力不正 / 400 相当)
          ├ DuplicateEmailException    (重複 / 409 相当)
          └ InfrastructureException    (基盤障害 / 500 相当, cause 保持)
```

処理の流れ（両経路とも `UserRegistrar` に合流）:

```
パスワード: validate → encode → User生成 ┐
                                          ├→ UserRegistrar: 重複→保存→ウェルカムメール→ログ
GitHub:     code→OAuthProfile → User生成 ┘
```

---

## 4. 工夫したポイント

- **「同じ後続処理を通す」を構造で保証** — 要件（ウェルカムメール・ログをパスワード/OAuth で共通化）を
  ドキュメント上の約束ではなく `UserRegistrar` への委譲という設計で強制した。
- **null の排除** — `findByEmail` の戻り値を `Optional<User>` にし、`!= null` 比較を撤廃。
- **意図で命名** — `EmailSender.sendConfirmation(User)` のように手段でなく目的で命名し、件名・本文の
  組み立てをアダプタ内に閉じ込めた。
- **セキュリティをテストで保証** — 「生パスワードを永続化しない」ことを捕捉 fake のテストで明示。
- **メール失敗の方針を明確化** — 保存後のメール送信失敗は登録成功扱い＋警告ログ（確認メールは再送可能な
  best-effort）と定義し、暗黙の挙動をなくした。
- **拡張容易性** — OAuth プロバイダ追加はアダプタ実装＋レジストリ登録のみ（Open/Closed）。

---

## 5. できなかったこと・今後の改善点

- **パスワードハッシュ** — 現状はソルトなし SHA-256。本番はソルト付き **bcrypt / argon2** に
  すべき（`PasswordEncoder` 差し替えのみで移行可能）。
- **実 OAuth フロー** — `GitHubOAuthProvider` はモック。実際は認可リダイレクト・state 検証・
  トークン交換・GitHub API 呼び出し、メール未提供時の扱いが必要。
- **トランザクション整合性** — メール失敗時は現状ログのみ。本番は outbox / transactional messaging /
  saga 補償で確実な再送を担保したい。
- **バリデーション強化** — 値オブジェクト（Email/Password 型）や複数エラーの集約は未対応。
- **Google / LINE 対応** — enum とレジストリは準備済み。アダプタを実装すれば追加できる。
- **検証実行** — JDK/Maven 未導入のため JUnit テストはローカル実行していない（コードレビューで確認）。
