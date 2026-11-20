# Human Journey Core 0.14.1 — Map Feature location HUD

**Verified Core 0.14.1:** [GitHub Actions build 44653798462](https://github.com/petryshamen/minecraft-education/actions/runs/44653798462), source `c32acc0c18ac3df2915b88d2d92104c1a5fa89d8`. All 444 tests passed (424 retained +20new), zero failures, and14 no-world Forge checks passed. 323 prior runtime classes are byte-identical; only GeographyHud and BoundaryControlLayout changed, plus the new pure FeatureLocationHud resolver. Runtime SHA-256 `86e50b3c2463d1ba7db058e972d81d8b5373cf37f1f44c19c2670058ea4bcb02`. No Minecraft gameplay testing, automatic installation, production datapack or live-server change.

- Implementation: the existing Forge geography HUD reads the already synchronized discovered-only FeatureClient snapshot and queries its existing component BVH/priority selection at the player's current dimension/X/Z. New FeatureLocationHud is pure presentation logic with no client/Xaero dependencies. Polygon/multipolygon containment is exact and excludes hole interiors/edges; existing direct-river precedence and equal-priority ambiguity are retained. Feature label is primary; current Local label (otherwise Macro, otherwise empty) is secondary. No feature match gives the exact previous HudPresentation. No technical type suffix is added.
- Rivers: fixed2.0 horizontal world blocks, inclusive, independent of discovery_radius, zoom and GUI scale. No near-coast tolerance is applied to polygons. Geometry represents X/Z only, not actual water blocks or elevation.
- Privacy/compatibility: only synchronized discovered geometry is available. Fullscreen Hover/All/Off and enabled drawing flags do not gate this HUD. No protocol, SavedData, discovery, teacher/student eligibility, priority, Macro/Local/NPC/economy, HUD style/config or fullscreen hover changes. No new datapack/schema migration required. Feature protocol2 and Macro/Local protocol1 remain unchanged from0.14.0.
- Buttons: HJ Borders and HJ Features prefer right edge,8GUI-pixel margin, directly below reserved compass at y108/132. At1000x600 they move112GUI pixels right (x762→874). Native-widget collision checks and old safe fallback remain; blocked/tiny screens can use alternate placement or hide controls as before. Exact readability at unusual GUI scales requires manual verification.
- Automated:20new tests cover discovered/hidden geometry, Local/Macro/no-geography fallback, holes and edges, priorities/ambiguity, multipart gaps,2-block river boundary and discovery-radius independence, wrong dimensions/invalid positions, client snapshot publication/clear, participant exclusion, Off independence, render isolation and right-edge/collision-safe controls. All424old tests remain byte-identical and pass.14no-world checks pass. Existing map geometry/transport/ledger/discovery classes and every Xaero integration class are byte-identical; HUD render and shared pair layout are the only changed existing runtime classes. Existing assets, mixins, examples and schemas unchanged. Root pack.mcmeta64 and Java21 verified.

Manual checklist (not performed):

1. At the tested discovered Sea of Japan location: HUD shows Sea of Japan / East Asia.
2. Enter an excluded island: sea disappears; land Local/Macro returns, unless a different valid feature independently covers that position.
3. Enter discovered higher-priority Korea Strait: Korea Strait replaces the sea.
4. Leave known Map Features: previous Local/Macro HUD is restored exactly.
5. Check a discovered river within/outside2blocks; fullscreen HJ Features Off must not suppress the gameplay HUD.
6. Open Xaero map: both buttons clear coordinates/compass and still cycle; check resize/GUI scale. Teacher sees known location normally without contributing discovery; unknown features stay hidden.

Limitations: depends on accepted geometry/priorities and completed discovered-only synchronization; no terrain/biome authority, automatic production hole repair or new server context. At equal-priority ambiguity location falls back to normal geography. No production catalog/Japan data edits. No gameplay or visual testing is claimed.

Install manually. No automatic installation or server deployment. Complete source and runtime JAR are archived with SHA-256 checksums; existing example packs are unchanged in the source bundle.

