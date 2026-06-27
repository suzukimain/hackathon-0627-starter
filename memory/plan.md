# Macro Plan

The architectural design. Updated when direction changes. Vertical slices
only — build full-stack features end-to-end, not horizontal layers
(all DBs, then all APIs, then all UIs).

## Current Phase

問題点を重要度別に解決 → GitHub OAuth で肉付け。スライス: S1完了 / S2着手。
詳細プランは `~/.claude/plans/tranquil-singing-abelson.md`。
スライス: S1 ポート＋DI / S2 例外階層 / S3 ハッシュ / S4 共通コア＋メール順序 /
S5 Validator / S6 OAuth / S7 ドキュメント。

## Vertical Slices

<!--
One slice = one user-facing outcome, built top-to-bottom.
Add slices as you plan them. Execute one at a time.

### Slice 1: <user-facing outcome>
- **UI**: what the user sees/clicks
- **API**: routes/handlers needed
- **Data**: schema changes, migrations
- **Tests**: what proves it works
- **Verify**: how the human confirms (link to memory/verify.md criteria)
-->

## Deferred / Out of Scope

<!-- What we are explicitly NOT building right now. Prevents scope creep. -->

## Open Questions

<!-- Ambiguities that must be resolved with the human before implementation. -->
