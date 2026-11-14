# Discovered geography on Xaero World Map — Core 0.12.0

Core 0.13.0 adds a separate [Map Features system](MAP-FEATURES.md); Macro/Local protocol and discovery remain unchanged. The two HJ controls now stack in a shared compass-safe layout, explicitly reserving112×104GUIpixels at top-right rather than using the original8pixel-right position. Feature settings and discovery are independent. The Core0.12 boundary-mode contract below remains authoritative for geography.

**Verified Core 0.12.0:** [GitHub Actions build 43621360320](https://github.com/petryshamen/minecraft-education/actions/runs/43621360320), source `63c43b90af0ed659813efef074691774c8b9c170`. All 330 tests passed (296 retained + 34 new), zero failures; all twelve no-world Forge checks passed. 253 existing runtime classes are byte-identical to 0.11.0; eight changed class files are confined to presentation/configuration, shared hover selection and client diagnostics. Map protocol/transport/server sync/discovery state and all unrelated gameplay classes are unchanged. Runtime SHA-256 `cd6f282030c15b9a86191d715617b027337b918e3a06cb8da584cd6fb9215631`. No gameplay testing, automatic installation, live-server or production datapack modification.

**Core 0.11.0 baseline:** [GitHub Actions build 43557161882](https://github.com/petryshamen/minecraft-education/actions/runs/43557161882), source `e9b2150633ac9575f3eef1b82bf1e115855b8b90`. All 296 tests passed (256 existing + 40 new), zero failures; all twelve no-world Forge checks passed. 217 existing runtime classes are byte-identical to 0.10.0; six changed class files are limited to geography bootstrap, transport integration, diagnostics and client config. All old tests, examples, schemas and existing packaged data/assets are unchanged. Runtime SHA-256 `ee96bf39d8e675300e300b2478b9caee35ebe0a1045fbcc83c04082734908519`. No Minecraft gameplay testing, automatic installation, live-server or main-datapack modification.

Minecraft 1.21.8 / Forge 58.1.18. The optional client adapter supports **exactly Xaero World Map 1.39.13 Forge 1.21.8**. Install Core manually on server and participating clients; keep Xaero client-side. No geography datapack migration, new production content, Minimap API dependency or server Xaero installation is required. All gameplay/visual interaction acceptance remains manual.

## Inspected dependency and render hook

Public CurseForge project 317780, file 6786823; Maven coordinate `curse.maven:xaeros-world-map-317780:6786823`. The inspected public JAR is 971120 bytes, SHA-256 `d0ff53b5039b61cd793fb6fe090d04f9807da9cf7f434957ff66b20c8a3cee33`, identical to the installed read-only Xaero artifact. Core does not redistribute its classes.

The public artifact exposes no suitable arbitrary-polygon render callback; chunk/region highlighters do not implement these boundaries. The isolated optional `integration.xaero.mixin.GuiMapMixin` injects at `GuiMap.renderPreDropdown(GuiGraphics,int,int,float)` HEAD, after terrain and the ordinary widget pass, before native dropdowns. The unchanged Mixin does not intercept input or replace screens, terrain, waypoints or exploration storage. The new client control uses public Forge screen events; only gestures beginning in its clickable bounds are consumed. The adapter reads cached private `cameraX`, `cameraZ`, `scale`, `viewed`, `rightClickMenu`, `overWaypointsMenu` and `overPlayersMenu`; public APIs provide `getMapProcessor()`, current MapWorld/MapDimension, `getDimId()`, `getFutureDimension()` and `Misc.getMouseX/Y`.

The exact inspected transform is:

```
GUI x = (floor(raw framebuffer width / 2) + (world x - cameraX) * rawScale) / GUI scale
GUI y = (floor(raw framebuffer height / 2) + (world z - cameraZ) * rawScale) / GUI scale
```

The inverse uses the same raw-pixel mouse source as native Xaero. There is no second geography coordinate system, LOD-snapped hover or polygon simplification. Dimension comes from Xaero's actual active map dimension; absent/transitioning/paused state withholds rendering instead of guessing the player's dimension.

The Mixin plugin checks physical client and exact version before applying its client Mixin. The supported adapter is loaded reflectively only after client mod/version detection. Unsupported/absent Xaero leaves HUD and all common mechanics operational. Integration failure disables only this adapter for the session and logs a warning. `active=false` before opening a usable map is normal; `/hjmap status` explains it. The Mixin root package contains only the Mixin, avoiding normal helper-class loading restrictions.

## Server knowledge and privacy

`geography.map.MapProjection` projects the existing world-wide SavedDiscovery ledger. It constructs DTOs lazily for discovered IDs only. Each record contains kind, stable ID, current readable/Era-sensitive label, dimension, priority, exact polygon and optional Local parent ID. No profile, economic data, NPC mappings, era rules or production catalog travels in map packets. An undiscovered/unresolvable parent is redacted even from a discovered Local. Stale IDs remain in the existing persistent ledger but produce no geometry.

The existing current-location HUD is deliberately independent: it may identify a teacher's current undiscovered location, as before. That HUD exception does not add map geometry or disclose the undiscovered map catalog. Student discovery still requires `hj_participant` AND `hj_student` AND NOT `hj_teacher`. Conflicting teacher/student tags never contribute. Teachers receive students' shared knowledge; non-participants receive no usable map geometry. This release adds no discovery persistence format and no changes to discovery notifications.

## Bounded synchronisation

Separate common channel `human_journey_core:geography_map`, protocol 1. Serverbound traffic is only a participant-checked, 60-tick-rate-limited full-resync request. Hover never contacts the server.

Each clientbound transaction carries server-session UUID, monotonic sequence, shared discovery revision, accepted geography-data revision and presentation/Era epoch. Modes are FULL, DELTA with exact base sequence, or immediate CLEAR. Packet limits: **8 areas**, **64 removals**, **48 KiB**, **256 vertices/area**, at most 1024 parts; catalog limit **512 Macros + 4096 Locals**. Packets contain part index/count; no unbounded whole-world packet. Full maximum catalogs with maximum polygons can be large in aggregate, but transfer is event-driven, not continuous. Current implementation dispatches bounded parts in one server update rather than spreading bandwidth across multiple ticks.

`ClientMapState` retains the last complete immutable snapshot and index until every part validates. Out-of-order parts are supported; identical duplicates are harmless. Conflicting duplicates, malformed metadata/polygons, excessive counts, invalid parent references and revision regressions cannot replace the valid state. At most two bounded transactions are staged. Missing delta bases request a rate-limited full resync. Resets immediately clear old geometry; delayed older transactions cannot revive it. Logout/login clears connection/session state, and callbacks from an old connection are rejected. Role loss immediately hides local map state and server revokes it.

Join/respawn/resync obtains FULL; new discovery/admin mark uses DELTA; empty reset uses CLEAR; accepted geography reload or active Era change uses FULL. Removed geometry is therefore purged on reload and labels refresh without restart. Invalid geography reload retains the existing accepted catalog. Existing 2 Hz geography polling compares cached player/discovery/data/Era keys; no unchanged geometry is rebuilt or resent. Map projections are shared/cached across participants with the same context. The map engine never creates discoveries itself.

## Rendering, hover and scale

`MapSpatialIndex` builds immutable per-dimension/per-kind bounding-volume hierarchies once per committed snapshot. Viewport queries cull Local polygons before rendering; point queries run native HJ polygon containment only on bounding-box candidates. Pathological overlapping polygons can still require many candidates, but unrelated distant geometry is pruned.

### Boundary presentation modes

`world_map_boundary_mode` is a lowercase string with four validated values:

| Mode | Persistent outlines | Hover outlines |
|---|---|---|
| `hover` (new default) | none | selected discovered Macro, then Local |
| `locals` | visible discovered Locals | selected Macro, then Local |
| `all` | visible discovered Macros, then Locals | selected Macro, then Local |
| `off` | none | none; tooltip still works independently |

The master toggle disables all HJ map presentation. `world_map_hover_enabled` controls the tooltip only; disabling the tooltip does not disable boundary selection/highlighting. Mode switching never sends packets or requests additional geography.

`MapSpatialIndex.Hover` retains its labels/candidate/ambiguity accessors and now exposes immutable selected `localArea`/`macroArea` plus nullable `localKey()`/`macroKey()`. One hover lookup drives both existing tooltip and draw selection. Highest-priority discovered Local and discovered-parent/independent-Macro rules are unchanged. Equal-priority ambiguity still fails closed for the ambiguous layer. A stale/unavailable selection cannot be substituted with another area.

`BoundaryPresentation.plan` chooses ordered layers from the current immutable snapshot. Persistent modes use per-kind/dimension BVH viewport queries; Hover draws at most two selected polygons. Polygon vertices, native containment, screen transform and segment clipping remain unchanged; no polygon fill or simplification. Low-priority persistent polygons draw before more specific ones.

| Style | RGB | Default GUI width | Default opacity |
|---|---|---:|---:|
| Normal Local | #D6E6CE | 1.0 | 0.25 |
| Normal Macro | #7EA8D8 | 1.5 | 0.40 |
| Hover Local | #FFFFFF | 2.0 | 0.90 |
| Hover Macro | #7FC3FF | 2.0 | 0.60 |

The tooltip's fit/drawing method is identical to approved Core 0.11.0: cursor +12 GUI-pixel offset, screen clamping, bold Local/primary and subdued Macro/secondary, same translucent panel and text shadow. No fixed panel or permanent geography labels. Native widget, waypoint/player-menu and hovered-element suppression remains. Boundary highlights share that suppression.

### Fullscreen map mode control

The isolated `XaeroBoundaryControl` attaches a native-style **118 × 20 GUI-pixel** button using Forge `ScreenEvent.Init.Post`, gated behind supported Xaero adapter loading. It starts **8 GUI pixels from top/right** and searches bounded alternate slots to avoid visible native widgets (4 rows, at most 32 columns, with spacing). On very small/occupied screens it hides and reports why rather than covering controls. It also hides while native dropdown/right-click/waypoint/player menus are open, the map is unavailable, the master toggle is off, or the player is not a participant. A final redraw keeps the button above geography outlines. No additional Xaero Mixin is added.

Label/cycle: **HJ Borders: Hover → Locals → All → Off → Hover**. Selection applies immediately, is written through Forge's client config save, and persists across restart. Saving failure restores the previous in-memory preference and reports the problem. No permission or server request is involved. Mouse press handling captures only gestures that start inside a usable button: left click cycles; right/other clicks are consumed without creating a waypoint behind it; matching drag/release remains captured. Normal map clicks/drags outside it pass through. The button remains a normal navigable widget for keyboard use.

Control initialization/access/placement failures are independent of boundary rendering. They hide the control and expose a diagnostic while config-based presentation remains usable. Rendering failure retains the existing session-disable mechanism for the adapter alone.

## Client configuration

In `human_journey_core-client.toml`:

```toml
[world_map_geography]
world_map_geography_enabled = true
world_map_hover_enabled = true
world_map_boundary_mode = "hover" # hover | locals | all | off
world_map_local_outlines_enabled = true # legacy migration input; ignored once mode exists
world_map_outline_opacity = 0.25
world_map_outline_width = 1.0
world_map_macro_opacity = 0.4
world_map_macro_width = 1.5
world_map_hover_local_opacity = 0.9
world_map_hover_local_width = 2.0
world_map_hover_macro_opacity = 0.6
world_map_hover_macro_width = 2.0
```

All opacity ranges remain 0..1; all boundary widths use 0.5..4 GUI pixels. Colours are stable presentation constants, not per-region data. Existing Local width/opacity keys and valid values are retained: a Core 0.11.0 opacity of 0.4 stays 0.4, while fresh/missing settings use 0.25. HUD defaults remain enabled=true, X=85, Y=10, scale=0.8, background opacity=0.35; old ranges and custom values are retained.

### Legacy migration

Before Forge registration/loading of **this client file**, `MapConfigMigration` checks the existing `human_journey_core-client.toml` through NightConfig, preserving comments/other values. If the mode key is absent, old outlines=false writes `off`; true/missing writes `hover`. No fresh file is created by migration; Forge generates it normally. Once any explicit new mode exists, migration does nothing, so the old boolean cannot override it. Invalid explicit modes are corrected to `hover` by Forge validation, even when the old boolean is false. Deleting a mode during an already-running config hot reload follows Forge's normal default correction; legacy upgrade migration runs at client startup. A malformed/unreadable file logs a migration warning and is left to Forge's existing loading/error handling.

This release changes no map network protocol (still protocol 1), payload, discovery storage/rules, production data or server synchronization. No Developer datapack migration is needed.

## Commands and diagnostics

- `/hjmap status` — client-only: Xaero detected/version/supported/active/reason; received Macro/Local geometry counts; map sequence and discovery/data/presentation revisions; overlay toggle; boundary mode; tooltip enabled; persistent Local/Macro activity; control availability/position; last sync problem.
- `/hjmap resync` — request shared discovered geometry again (server checks eligibility/rate).
- `/hj geography status` — existing permission-2 server diagnostic, now also reports resolvable Macro/Local geometry, data/discovery revisions and stale/unresolvable counts; existing context/tag/discovery totals remain.
- `/hj geography discovery list macro [page]` and `list local [page]` — existing shared IDs, stale markers.
- `/hj geography discovery mark macro <id>` / `mark local <id>` — validates ID, syncs immediately.
- `/hj geography discovery reset` — resets shared knowledge and clears map geometry immediately. Stationary players do not instantly rediscover; movement re-evaluates normally.

## Automated validation boundary

Synthetic tests cover privacy/projection, labels/reloads, roles, new discovery/admin mark/reset, change-only network traffic, snapshot/revision handling, malformed packets, native 256-vertex codec, 4608-area indexing/batching, hover priority/parents/dimensions/boundaries, exact camera transform and version/API bytecode structure. The existing regression suite is retained. Forge `runData` adds a no-world packet/privacy/common-class-loading smoke check with Xaero absent from runtime. Test-runtime-only Xaero is inspected through ASM without constructing its screen. This does not constitute gameplay testing or a full dedicated-server world startup test.

## Manual acceptance checklist

**A — Default/migration:** fresh client config or old outlines=true with no new key starts `HJ Borders: Hover`; no full line network when cursor is outside discovered geography. Old outlines=false migrates to Off. Existing explicit mode/custom HUD values remain.

**B — Hover Local:** hover a discovered Japan Local. Check bright white selected Local, stronger-blue discovered Macro, and unchanged near-cursor tooltip that follows movement and clamps at screen edges.

**C — Move:** cross several discovered areas with the cursor. Previous highlight disappears immediately; Hover leaves no unrelated boundaries.

**D — Unknown neighbour:** hover an undiscovered Korea/neighbouring Local. No unknown Local name/outline. Already discovered Macro-only hover/highlight remains allowed. Repeat teacher/student/shared discovery checks as needed.

**E — Locals:** click the control once from Hover. Discovered Local network appears; no persistent Macro network. Hover Local/Macro are stronger.

**F — All:** cycle again. Muted-blue discovered Macro network, pale detailed Locals and strongest hovered Local on top. Check boundaries align throughout pan/zoom and viewed-dimension changes.

**G — Off:** cycle again. No outlines; tooltip still follows cursor when enabled. Disable tooltip separately and confirm Hover boundaries still work. Master off hides all HJ map presentation/control.

**H — Persistence:** select Locals/All/Off, close Minecraft, restart and reopen the map. Mode and unrelated settings persist; `/hjmap status` reports the selection.

**I — Xaero interactions:** in each mode test drag, zoom, waypoint hover/click, native buttons and right-click menus. Clicking the HJ control must neither pan nor place a waypoint behind it; clicks outside remain normal. Check resized/GUI-scaled screens, native menus (control hides), and absence/unsupported-Xaero safety. Discovery/admin reset/reload must retain the prior protocol behaviour and privacy.

## Future adapters and limitations

ClientMapState and MapSpatialIndex are Xaero-independent reusable discovered-knowledge interfaces. Future map adapters can consume that state without receiving undiscovered polygons or changing discovery persistence. Exact private Xaero field/render-hook dependence is intentionally limited to version 1.39.13; other versions require inspection and a new adapter/gate. Geometry is not a general map export API. This release does not change Xaero terrain/exploration, synchronise undiscovered data, or claim successful in-world appearance or interaction before manual testing.
