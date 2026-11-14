# Discovered geography on Xaero World Map — Core 0.11.0

Minecraft 1.21.8 / Forge 58.1.18. The optional client adapter supports **exactly Xaero World Map 1.39.13 Forge 1.21.8**. Install Core manually on server and participating clients; keep Xaero client-side. No geography datapack migration, new production content, Minimap API dependency or server Xaero installation is required. All gameplay/visual interaction acceptance remains manual.

## Inspected dependency and render hook

Public CurseForge project 317780, file 6786823; Maven coordinate `curse.maven:xaeros-world-map-317780:6786823`. The inspected public JAR is 971120 bytes, SHA-256 `d0ff53b5039b61cd793fb6fe090d04f9807da9cf7f434957ff66b20c8a3cee33`, identical to the installed read-only Xaero artifact. Core does not redistribute its classes.

The public artifact exposes no suitable arbitrary-polygon render callback; chunk/region highlighters do not implement these boundaries. The isolated optional `integration.xaero.mixin.GuiMapMixin` injects at `GuiMap.renderPreDropdown(GuiGraphics,int,int,float)` HEAD, after terrain rendering and before native controls/dropdowns. It does not intercept input or replace screens, terrain, waypoints or exploration storage. The adapter reads cached private `cameraX`, `cameraZ`, `scale`, `viewed`, `rightClickMenu`, `overWaypointsMenu` and `overPlayersMenu`; public APIs provide `getMapProcessor()`, current MapWorld/MapDimension, `getDimId()`, `getFutureDimension()` and `Misc.getMouseX/Y`.

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

Only discovered Local outlines render, low priority first/high priority last. Lines are clipped in world space before float conversion, use constant GUI-pixel thickness and preserve authoritative vertices. No Macro outlines, polygon fill, fog-of-war, permanent map labels or Minimap rendering. Defaults are a muted pale outline at 40% opacity and 1 GUI-pixel width. Hover uses highest-priority discovered Local; its discovered parent Macro wins, otherwise an independently containing discovered Macro is used. Macro-only and Local-only results are valid. Equal-priority ambiguity fails closed and is represented internally in hover diagnostics. The native HJ boundary/edge containment implementation is reused.

Hover text uses bold primary and subdued secondary labels on a compact translucent background near the cursor, clamped to screen bounds. Extremely long labels are truncated visually. The overlay avoids native widgets, right-click menus, waypoint/player menus and hovered native elements; normal native controls/dropdowns draw later. No input is captured. Real-world pan/zoom alignment, native GUI interactions and visual appearance require the manual checklist below.

## Client configuration

In `human_journey_core-client.toml`:

```toml
[world_map_geography]
world_map_geography_enabled = true
world_map_local_outlines_enabled = true
world_map_hover_enabled = true
world_map_outline_opacity = 0.4 # valid 0..1
world_map_outline_width = 1.0 # valid 0.5..4 GUI pixels
```

Existing valid settings are respected. HUD defaults remain enabled=true, X=85, Y=10, scale=0.8, background opacity=0.35; all previous ranges are unchanged. Changing map presentation does not alter discovery.

## Commands and diagnostics

- `/hjmap status` — client-only: Xaero detected/version/supported/active/reason; received Macro/Local geometry counts; map sequence and discovery/data/presentation revisions; overlay toggle; last sync problem.
- `/hjmap resync` — request shared discovered geometry again (server checks eligibility/rate).
- `/hj geography status` — existing permission-2 server diagnostic, now also reports resolvable Macro/Local geometry, data/discovery revisions and stale/unresolvable counts; existing context/tag/discovery totals remain.
- `/hj geography discovery list macro [page]` and `list local [page]` — existing shared IDs, stale markers.
- `/hj geography discovery mark macro <id>` / `mark local <id>` — validates ID, syncs immediately.
- `/hj geography discovery reset` — resets shared knowledge and clears map geometry immediately. Stationary players do not instantly rediscover; movement re-evaluates normally.

## Automated validation boundary

Synthetic tests cover privacy/projection, labels/reloads, roles, new discovery/admin mark/reset, change-only network traffic, snapshot/revision handling, malformed packets, native 256-vertex codec, 4608-area indexing/batching, hover priority/parents/dimensions/boundaries, exact camera transform and version/API bytecode structure. The existing regression suite is retained. Forge `runData` adds a no-world packet/privacy/common-class-loading smoke check with Xaero absent from runtime. Test-runtime-only Xaero is inspected through ASM without constructing its screen. This does not constitute gameplay testing or a full dedicated-server world startup test.

## Manual acceptance checklist

Use a separate test world; reset is destructive to shared discoveries in that world.

**A — Known area:** open a discovered Local. Check subtle boundary alignment and Local + Macro hover.

**B — Unknown neighbour:** verify ordinary terrain remains and no unknown Local outline/name appears. Already discovered Macro-only hover is allowed.

**C — Student exploration:** cross into an undiscovered Local as participant+student; check existing chat notification, immediate shared outline and hover.

**D — Shared knowledge:** open the same map as a second student who has not visited; verify the discovery is visible, including after joining later.

**E — Teacher exclusion:** travel through an unknown Local as teacher, including conflicting teacher+student tags. HUD can identify it, but map knowledge must not grow. After a student discovers it, the teacher should see it too. Check non-participants have no overlay.

**F — Overlap:** check broad discovered Local before specific Local discovery; discover the higher-priority Local and verify hover changes. Check undiscovered parent redaction and Macro-only coverage.

**G — Camera/interactions:** pan, drag, zoom, vary GUI scale and viewed dimension; verify alignment, stable line width, accurate hover, buttons, waypoints, native tooltips and map opening/closing. Overworld geometry must not appear in another dimension. Adjust each client option and verify HUD remains unchanged.

**H — Restart/reload:** restart client/server, reconnect, change discovered geometry/labels in a test datapack then `/reload`, and test an Era-sensitive label change. Existing discoveries persist, shapes/labels refresh, removed geography disappears. Check `/hjmap status` and server status. Test clients without Xaero and unsupported Xaero versions; only map integration should disable.

**I — Admin reset:** mark a valid Macro/Local and verify immediate appearance; reset and verify immediate HJ overlay removal while native terrain stays. Move a student to rediscover and check no delayed pre-reset geometry returns.

## Future adapters and limitations

ClientMapState and MapSpatialIndex are Xaero-independent reusable discovered-knowledge interfaces. Future map adapters can consume that state without receiving undiscovered polygons or changing discovery persistence. Exact private Xaero field/render-hook dependence is intentionally limited to version 1.39.13; other versions require inspection and a new adapter/gate. Geometry is not a general map export API. This release does not change Xaero terrain/exploration, synchronise undiscovered data, or claim successful in-world appearance or interaction before manual testing.
