# Atomic Progress Log

Your temporal anchor. Tick atomic tasks as you complete them. Never mark a
task done unless `memory/verify.md` criteria are met.

The `state-enforcement.sh` hook blocks task completion if source files
changed but this file wasn't updated.

## In Progress

- [ ] （なし — 全スライス完了）

## Completed (this session)

- [x] S7: DESIGN.md（課題・方針・構成・工夫・今後）と DECISIONS.md（ADR-001〜011）執筆。

- [x] S6: GitHub OAuth サインアップ。OAuthProvider enum / OAuthIdentityProvider ポート /
  GitHubOAuthProvider(モック) / OAuthProviderRegistry(Strategy) / OAuthRegistrationService。
  User に provider/providerUserId 追加。GitHub 登録が共通 UserRegistrar を通る（ウェルカムメール・
  保存・重複）テスト追加。

- [x] S5: UserRegistrationValidator 抽出（明示ルールメソッド・値オブジェクト不採用）。
  Service に注入。境界テスト（7/8文字等）追加。

- [x] S4: 共通コア UserRegistrar 抽出。register が委譲する形に。save→email 順序、メール失敗は
  成功＋警告ログ。UserRegistrarTest（失敗時成功・重複）追加。Service コンストラクタを
  (UserRegistrar, PasswordEncoder) に変更し PasswordSecurityTest も更新。

- [x] S3: 偽ハッシュ廃止 — Sha256PasswordEncoder 導入、createDefault を差し替え、Legacy 削除。
  InfrastructureException 追加。生パスワード非永続を捕捉 fake テストで保証。

- [x] S1: ポート＋DI骨格 — UserRepository/PasswordEncoder/EmailSender をポート化し
  コンストラクタDIへ。model/adapter を抽出。挙動保存の characterization テスト作成（非実行）。
  旧 src/UserRegistrationService.java を Maven 標準レイアウトへ移行・削除。
- [x] S2: 例外階層 — RegistrationException(基底)/ValidationException/DuplicateEmailException
  を導入。register から IllegalArgumentException を型付き例外へ置換。テストも型別に更新。

## Backlog (next up)
- [ ] S4: 共通コア UserRegistrar 抽出 / save→email 順序 / メール失敗時 成功＋警告ログ
- [ ] S5: UserRegistrationValidator 抽出
- [ ] S6: GitHub OAuth サインアップ（Strategy・共通コア再利用）
- [ ] S7: DESIGN.md / DECISIONS.md 執筆

## Blocked

<!--
- [ ] <task> — waiting on: <reason or person>
-->
