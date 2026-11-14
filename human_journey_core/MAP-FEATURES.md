# Discoverable water map features — Core 0.13.0

Minecraft 1.21.8 / Forge 58.1.18; optional client adapter exactly Xaero World Map 1.39.13. Manual installation only. No real-world water catalog, production coordinates or gameplay/progression changes are supplied.

## Separate cartography system

Map Features are independent named cartographic objects, not a third Macro/Local layer. They do not become a Local, economic profile, NPC context, commodity filter or water-quality rule. `mapfeature` owns its catalog, geometry index, world SavedData, discovery and transport. Existing geography discovery and protocol 1 remain intact. Resetting either ledger never resets the other; encountering a Macro/Local does not automatically discover a feature, and feature discovery does not create geography discoveries.

## Resource contract

One feature per JSON resource under `data/<namespace>/core/map_feature/<file>.json`; recommended namespace `human_journey`. The stable `id` in the body is authoritative, independent of filename. Normal resource-pack precedence applies to identical resource paths; duplicate body IDs across effective resources reject the entire reload. All matching namespaces are supported.

```json
{
  "schema_version": 1,
  "id": "synthetic_sea",
  "label": "Synthetic Sea",
  "feature_type": "sea",
  "dimension": "minecraft:overworld",
  "priority": 50,
  "discovery_radius": 8,
  "geometry": {
    "type": "polygon",
    "points": [[100,100],[200,100],[200,200],[100,200]]
  }
}
```

Types are exactly `ocean`, `sea`, `gulf`, `bay`, `strait`, `lake`, `river`, displayed as Ocean/Sea/Gulf/Bay/Strait/Lake/River. Geometry/type are independent; rivers normally use `polyline`. Polygons have at least 3 distinct vertices, implicitly final→first. Polylines have at least 2 distinct points and never implicitly close. Do not repeat vertices; the engine rejects duplicates, degenerate/self-intersecting polygons, zero-length paths and malformed points. Coordinates are finite world X/Z, each within ±30,000,000. No runtime latitude/longitude conversion occurs. Developer may use the established Earth calibration offline: X=round(longitude×68.2666667), Z=round(-latitude×68.2666667).

Required fields: schema_version=1, id, label, feature_type, dimension, geometry. Priority defaults to 0, must be integer ±1,000,000; content should choose it deliberately. Discovery radius defaults to 8, accepts finite horizontal blocks 1..64. ID is a lowercase stable bare ID matching `[a-z0-9_][a-z0-9_.-]{0,63}`, excluding reserved `none`/`unknown`. Label is nonblank, maximum128 characters, with no control characters. Dimension is a syntactically valid lowercase resource location; it must match the actual level for discovery/rendering, including custom dimensions. Unknown schema fields are rejected. Polygon mathematical constraints are checked by Core beyond the JSON schema in `docs/map-feature.schema.json`.

Limits: **2048 definitions**, **512 points per feature**, **64 KiB characters per resource**, **64 MiB characters total reload input**. These support the requested scale without unlimited allocations. Definitions use complete feature geometry; encountering one segment discovers the whole configured feature. Grouped/multiple feature objects in one file are not supported in schema1. There is no hardcoded sea/ocean/strait type ranking.

## Reload and validation

`/reload` prepares the whole catalog off-thread with contextual resource/pack errors, validates it, builds an immutable index and publishes it on the game executor. A valid missing/empty catalog becomes empty safely. Any invalid definition preserves the complete last valid catalog/revision and logs a clear error; it never partially replaces it. Accepted reload increments catalog revision, and discovered geometry/labels/types update without restart. Removed definitions disappear from client geometry; their IDs remain known/stale in SavedData. Reintroducing the same ID restores its known status automatically.

## Exploration and persistence

Natural discovery requires `hj_participant` AND `hj_student` AND NOT `hj_teacher`. Teacher exclusion wins conflicting tags. Teachers receive shared student knowledge but cannot create discoveries by travel; outsiders receive no usable feature map state.

Server discovery runs at the existing 2 Hz cadence with per-player entity/dimension/X/Z/roles/catalog-revision caches, so stationary unchanged context is not recomputed. A dimension BVH first queries a 64-block bounding square, then checks each candidate's own radius. Polygons match inside OR within radius of any boundary segment; polylines match within radius of any open-path segment. Proper clamped point-to-segment distance is used, not vertex-only distance. Y is ignored. This is geometric exploration, not a test for actual water blocks/biomes; production shapes must accurately describe the water body.

Every qualifying unknown feature is discovered independently of hover priority. Multiple students' simultaneous discoveries are aggregated into one chat line per evaluation: `[Human Journey] Discovered: ...` with at most four names and a remaining-count suffix. All matching IDs still become known and sync. Re-entry, teacher/outsider travel and admin inspection/mark/reset produce no natural-discovery notification.

World-wide Overworld SavedData `human_journey_map_features.dat` stores `discoveredMapFeatures` (stable ID list) and `revision`; separate from `human_journey_geography.dat`. The data is shared, not per player. IDs and revision survive native NBT save/load; invalid legacy IDs are filtered, stale valid IDs remain. Admin reset clears only feature knowledge and syncs immediately; stationary players do not immediately rediscover until horizontal movement/context changes. Existing server/world restart handling creates a fresh transport session while keeping SavedData.

## Private bounded transport

Separate channel `human_journey_core:map_features`, protocol1. The existing Macro/Local channel and MapArea.Kind enum are unchanged. The feature presentation DTO contains **only** id, label, feature type, dimension, priority and polygon/polyline geometry. Discovery radius and all unrelated server data are absent. Projection enumerates discovered IDs and includes only currently resolvable definitions. Undiscovered labels/types/priorities/geometry/radius never enter feature packets; there is no hidden client catalog.

FULL/DELTA/CLEAR carry session UUID, monotonic sequence, discovery revision, catalog revision and exact DELTA base. Limits: **4 features/packet**, **64 removals/packet**, **48 KiB**, **512 parts/transaction**, **2048 published features**. Full snapshots publish atomically; the last complete immutable client state remains visible during bounded out-of-order staging. Stale/component-regressing frames, malformed geometry/counts, conflicting duplicates or impossible deltas cannot replace valid state. At most two transactions are staged; missing bases request a throttled full resync. Reset/role loss clears state; old connection callbacks are rejected and reconnect resets session state.

Join/respawn/explicit resync gets current discovered features; new discovery/admin mark sends deltas; reset sends CLEAR; accepted catalog reload sends a replacement FULL, purging removed geometry. No unchanged full per-tick sends. Server projection is cached across participants. Parts are bounded but dispatched during one update; a fully known maximum-point catalog can produce a large event-driven transfer, as with existing geography. Serverbound requests require actual participant eligibility and are rate-limited to once/60ticks; client requests are throttled to3seconds.

## Index and hover

Server and client use independent immutable per-dimension feature BVHs with precomputed feature/polyline bounding boxes. Viewport and point/near-line queries return bounding candidates before precise tests; no whole-catalog point-in-polygon pass every render frame. A candidate polyline uses proper segment distance. GUI hover tolerance is **5 GUI pixels**, converted to world distance as `5 * GUI scale / native Xaero rawScale`, retaining a five-pixel hit target across zoom levels.

Direct polyline hits take precedence over containing polygons. Within the chosen class, highest discovered priority wins; equal-priority ambiguity fails closed without guessing another feature. Polygon containment uses the same boundary-inclusive crossing/on-segment semantics as existing HJ geography, extended to512 points. Discovery does not use hover priority.

When a discovered feature is selected, its tooltip wins: **feature label / readable type**. Leaving it immediately restores the unchanged Local/Macro tooltip, according to existing settings. Feature Off disables both its outlines and tooltip. Feature ambiguity selects no feature; ordinary geography may still display. The approved geography fitting/drawing function remains unchanged. Feature tooltip uses the same +12 GUI-pixel cursor offset, screen clamp, compact translucent panel, text shadow and primary/secondary hierarchy. Native widget/menu/element suppression applies equally.

## Independent presentation

| Feature mode | Geometry | Feature tooltip |
|---|---|---|
| hover (default) | selected discovered feature only | selected feature |
| all | visible discovered features, then stronger selected one | selected feature |
| off | none | none |

Features use muted cyan/blue **#609FB8**, default width1.0 GUI px / opacity0.35. Selected feature uses **#76DAFF**, width2.0 / opacity0.85. Polygons are outlines only; river paths draw only their actual segments. No fills, permanent labels, waypoints or terrain changes. Draw order: persistent features → existing persistent Macro/Local and geography highlights → selected feature → near-cursor tooltip; existing native menu/control layering remains, with final HJ button redraw. Geometry uses the unchanged Xaero world transform and segment clipping.

```toml
[world_map_features]
world_map_features_enabled = true
world_map_feature_mode = "hover" # hover | all | off
world_map_feature_opacity = 0.35
world_map_feature_width = 1.0
world_map_feature_hover_opacity = 0.85
world_map_feature_hover_width = 2.0
```

All opacity ranges0..1; width0.5..4 GUI pixels. Invalid mode corrects to hover. Existing `[world_map_geography]` settings and legacy migration remain separate: the geography master controls geography, the feature master controls features. Neither presentation mode changes server discovery or sends resync traffic. HUD defaults remain unchanged.

## Dual controls and compass fix

`HJ Borders: Hover/Locals/All/Off` remains. `HJ Features: Hover/All/Off` is added; both are client-only, immediate and saved through Forge config for restart. No permission needed. Native-style 118×20 GUI-pixel buttons stack with a4pixel gap.

The exact-version adapter's shared layout now explicitly reserves a **112×104 GUI-pixel top-right compass/cardinal envelope**, including non-widget visual space. Initial placement is112GUIpixels farther left than0.12.0,8pixels from the top. Both button rectangles and their gap must clear reserved compass and visible native widgets. Bounded alternate rows/columns adapt to screen dimensions/GUI scale; below the compass they can use freed space. No collision-free slot hides the controls with diagnostics rather than covering native UI. Existing native-menu hiding, control-only failure isolation and scoped gesture capture remain: only gestures beginning inside an enabled HJ control are consumed, never clicks elsewhere. Test actual UI scales manually, particularly unusual/custom Xaero layouts.

## Commands and diagnostics

All server commands below require permission2:

- `/hj mapfeature status`: loaded count, counts by all seven types, discovered count, stale count, catalog/discovery revision, last successful reload/error.
- `/hj mapfeature discovery list [page]`: paginated stable known IDs, with stale markers.
- `/hj mapfeature discovery mark <id>`: validates current ID, marks/syncs shared feature knowledge; no natural-discovery message.
- `/hj mapfeature discovery reset`: clears only feature discoveries and client feature geometry.

`/hjmap status` retains all existing geography diagnostics and adds feature enabled/mode/count/revisions/sync problem/control availability. `/hjmap resync` requests both existing geography and feature snapshots through their separate rate-limited channels. It does not change knowledge. No duplicate command is introduced.

## Developer / Geography integration

Provide one version1 JSON per named physical feature with stable bare ID, readable label, one of seven types, dimension, explicit content priority, radius1..64 and final world X/Z polygon or open polyline. Use full known-object geometry intentionally: one visit reveals the whole definition. Split a long river into separate named features only when that is the intended content model, not because of partial discovery. Keep each feature≤512points and catalog≤2048objects; do not repeat a polygon's first point at the end or include duplicate vertices. Files need no relation to NPC/geography profile resources and no economic fields. Choose overlap priorities as content; Core supplies no hardcoded hierarchy. Renames should keep the stable ID, or existing shared knowledge will not follow the new ID.

`example-map-features` is a **separate synthetic-only test datapack** with a square sea and intersecting open river. It is not bundled as runtime definitions or deployed anywhere. Its artificial [100..200] coordinates are test content, not real-world mapping. Relocate its points to a test coastline if desired; inspect its labels/IDs before integrating. No production examples or real feature coordinates are supplied.

## Manual checklist

**A — Synthetic sea:** manually install/relocate the separate test datapack, `/reload`, approach its polygon shore as participant+student (within8blocks). Check one shared notification, known feature, Hover outline only under cursor and cursor-following Sea tooltip.

**B — Teacher:** teacher/conflicting teacher+student approaches unknown feature: no discovery. Student then approaches: teacher receives the shared feature. Outsider has no usable feature map state.

**C — River:** approach a long segment's middle away from vertices. It discovers within radius; test map selection across zoom/GUI scales and confirm no closing edge.

**D — Overlap:** at Sea+River overlap, direct river hit wins; off the river, Sea wins. Test content priorities/ties with synthetic data and make sure all qualifying features can be discovered together.

**E — Feature modes:** Hover → All → Off → Hover updates immediately and persists. All shows only discovered viewport geometry; Off removes feature tooltip and lines while geography resumes normally.

**F — Existing geography:** exercise all four HJ Borders modes, approved near-cursor tooltip, privacy, shared student discovery, teacher exclusion, NPC/economy/HUD and unrelated mechanics. Feature mark/reset must not alter Macro/Local knowledge.

**G — Controls:** test both controls at multiple window sizes/GUI scales. Compass/cardinal area stays unobstructed; no overlapping controls/native widgets. Test drag/zoom/waypoints/right-click menus and clicks outside controls. Open native menus may hide the controls as before.

**H — Restart/reload:** restart client/server, reconnect: shared feature IDs and client mode persist independently. `/hjmap status` shows counts/revisions. Change/remove/restore discovered definitions then `/reload`; geometry refreshes/stale IDs survive. Invalid definition retains the last catalog. Admin mark/reset and `/hjmap resync` work without changing geography knowledge. Also check Xaero-absent/unsupported-version and dedicated operation manually as appropriate.

Automated/unit/structural tests and Forge no-world data checks do not constitute Minecraft gameplay, rendered UI, real restart or full dedicated-world startup testing.
