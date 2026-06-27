# Sub-Agents & Tooling Registry

Update this file when sub-agents, MCPs, or core dependencies change.
Read at every session start.

## Active Sub-Agents

<!--
List each sub-agent the orchestrator may delegate to. Example:

- **general-purpose** — research, multi-file search, open-ended questions
- **code-reviewer** — second-opinion reviews before merging
- **explorer** — fast codebase mapping for unfamiliar directories
-->

## MCPs / External Services

<!--
List MCP servers or external tools the agent can invoke. Example:

- Playwright — visual validation (screenshots, click automation)
- Anthropic API — VLM for screenshot review
- Postgres MCP — read-only DB introspection
-->

## Tech Stack

- Language: Java（package `com.youtrust.hackathon`）
- Layout: Maven 標準（`src/main/java` / `src/test/java`）。ただし **ビルド基盤は未導入**。
- Test: JUnit5（**作成のみ・ローカル実行はしない**。JDK/Maven 未インストール）
- Build/Type-check/Lint: 未設定（環境構築は本タスク対象外）
- 検証方針: コードレビュー中心（`throws Exception` 残存なし / `new Xxx()` 直接生成なし /
  両登録経路が同一後続処理を通る / 生パスワード非永続 を目視確認）

## Agent Runtime Policy

<!--
Use this when the project itself builds with AI APIs or model routing.
Example:

- Default model / effort: <routine execution choice>
- Expensive reasoning reserved for: <architecture, high-risk changes, debugging>
- Context policy: <what gets loaded, summarized, cached, or excluded>
- Tool result contract: <JSON schema, key-value format, or validation rule>
- Retry / fallback policy: <if API-backed tools are part of the app>
-->

## Forbidden Patterns

<!--
Project-specific patterns this agent must not introduce. Example:

- No `any` types. Use `unknown` + type guards.
- No dynamic imports. Static imports only.
- No default exports on shared utilities.
- No silent catches. Errors hard-crash or are explicitly re-thrown.
-->
