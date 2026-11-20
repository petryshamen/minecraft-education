# Macro/Local Geography geometry v2 — Core0.15.0

Resources remain `data/<namespace>/npc_geography/<file>.json`. Root `schema_version` defaults1; schema1 uses the unchanged `polygon` field and256point cap. A resource may instead select `schema_version:2`, and every area in that resource uses `geometry` (not `polygon`). V1/v2 files coexist. Metadata is unchanged: id, dimension, priority, macro_region for Locals, display_label, era_labels, profile. There is no third layer or production data.

```json
{"schema_version":2,"macro_regions":[{"id":"synthetic_macro","display_label":"Synthetic Macro","geometry":{"type":"polygon","outer":[[0,0],[100,0],[100,100],[0,100]],"holes":[[[20,20],[40,20],[40,40],[20,40]]]}}],"local_areas":[{"id":"synthetic_local","macro_region":"synthetic_macro","display_label":"Synthetic Local","geometry":{"type":"multipolygon","polygons":[{"outer":[[50,50],[60,50],[60,60],[50,60]]},{"outer":[[150,50],[160,50],[160,60],[150,60]],"holes":[]}]}}]}
```

All coordinates are explicit X/Z, finite within±30million; no wrapping. Outer boundaries contain; hole interiors AND boundaries exclude. Multipolygon uses ANY-part union, with holes scoped to each part; overlapping components do not become separate identities. Each ring must be simple,≥3distinct points, no repeated closing endpoint, duplicate/backtracking/zero-length/self-intersection. Holes must lie strictly inside outer without touching/crossing/overlap/nesting. No automatic repair. Reuses FeatureGeometry Ring/Part/segment-BVH predicates exactly; independent geography budget wrapper and IDs/discovery remain separate from Map Features.

| Budget | Value |
|---|---:|
| Schema1 points/area |256|
| V2 total vertices/area |4096|
| V2 total rings/area (outers+holes) |128|
| Macros / Locals |512 /4096|
| Aggregate Macro+Local vertices |1,179,648|
| Aggregate Macro+Local rings |65,536|
| Geography resource files |4608|
| JSON characters/geography resource |512KiB|
| JSON characters/geography reload |64MiB|
| Non-geography NPC/economy JSON |original256KiB/resource,16MiB total|
| Serialized map area |80KiB|
| Raw fragment / packet |16KiB /48KiB|
| Transfer parts |8192|
| Estimated staged geometry/fragments |24MiB/transaction,2pending|

The vertex aggregate equals4608×256, preserving the theoretical existing v1 maximum. V2 areas share aggregate budgets, not4608×4096. JSON limits apply to encoded content as well as geometry; split large resource catalogs over files. The new geography file-count/resource limits allow one-file-per-area and ordinary4096point geometry. Pending staging estimates do not represent total Java heap: current snapshots, reconstructed objects, point/segment/component BVHs and staged bytes coexist.

## Authoritative indexed resolution

Accepted reload validates the complete context/economy/geography set, increments geometry quotas during parsing, validates parents, creates immutable ID maps and per-dimension/kind component BVHs, then publishes one complete state. Invalid data retains previous catalog/index. NpcGeography.resolve queries MACRO and LOCAL bounding candidates, exact-tests shared hole-aware component geometry, deduplicates stable IDs, then selects highest priority. Distinct equal-priority matches throw/fail closed. A Local's configured parent is used if no Macro contains the position; a conflicting independently matched Macro fails as before. Profile rules and era labels unchanged. GeographyContext, NPC and economy use this authoritative resolver; profile/context ID lookup uses immutable maps. No runtime index rebuilding.

The discovered client MapSpatialIndex uses the same low-level component BVH. Its established discovered-parent tooltip fallback remains unchanged (it can show a known parent even outside that parent's containment); a parent with no matching component is not spuriously highlighted. Ordinary context packets stay IDs/labels only. Map projections include known IDs only and redact unknown parent references.

## Map transport and presentation

**Update server and clients together:** only `human_journey_core:geography_map` advances1→2 for nested topology and bounded fragments. Ordinary geography context/discovery channel remains1; Map Feature protocol remains2. No mixed geography-map1/2 compatibility. Small≤256vertex areas batch≤8; larger areas serialize≤80KiB into≤16KiB kind+ID/index/count/length fragments. Max4096vertex geometry uses5fragments. Whole/delta/clear headers retain session/sequence/discovery/data/presentation revisions and exact base. Completed geometry/topology/aggregate/parent validation precedes atomic state/index publication; incomplete/stale/revoked/conflicting transactions do not expose partial areas. Existing resync/reconnect protection remains.

Hover resolves holes and individual components. Hover highlights matching component's outer+holes; Locals/All viewport-cull components then render every outer/hole as separate closed paths. No connecting lines, fills, colour/width/opacity or tooltip changes. Features/HUD precedence unchanged. SavedData still stores stable Macro/Local IDs, not geometry; no discovery reset/migration. Schema1 old production resources stay valid. No production polygons/profiles/priorities edited.

## Map buttons

The two buttons now stay on the far-right rail,2GUI pixels from the edge, beginning below compass at y108/132. If native widgets occupy that slot, they move down the SAME right rail; there is no leftward fallback over Xaero's centred coordinate readout. No safe right slot means controls hide for that frame and config remains usable. Single-button legacy helper is unchanged. At1000GUI width, x880 (previous preferred874, but previous fallback could shift much farther left). Test actual GUI scales manually; no live visual testing performed.

## Geography handoff / Europe refinement pilot

1. Accept/install Core0.15.0 manually on server/all clients first. Keep existing stable IDs, hierarchy, parents, labels/era labels, profiles and priorities. Do not reset discovery.
2. Keep valid v1 files unchanged. For detailed pilot areas, use a separate schema_version2 file and replace polygon with geometry.outer/holes or geometry.polygons. A file has one schema version; do not mix area field formats within it.
3. Count every outer+hole toward128rings and all points toward4096 per area; enforce1,179,648/65,536 global totals and512/4096area caps. No coordinate wrapping, Local-as-water-mask or production repair is provided.
4. `/reload`; inspect server resource/area/component/ring errors and `/hj geography status` candidate counts. Invalid reload keeps complete previous state. Check `/hj npc context`, profiles and fixed NPC spawn-time context.
5. Manually test water/land hole positions, disconnected parts, overlapping priorities, known parent fallback/conflicts, discovered-only hover, all Borders/Features modes and Map Feature location HUD. A known area migrated v1→v2 should keep discovery after restart/reconnect; teachers still cannot discover.
6. Geography/Developer separately author and approve Europe polygons. This engine release supplies only synthetic tests, no Europe/Japan edits, no profile/priority changes.

## Known limits / manual checklist

Dense overlap returns many real bounding candidates; an index cannot remove actual overlap. Topology has bounded but potentially quadratic adversarial exact-predicate work. Frame rendering still processes vertices of relevant visible components. Full known-world transfers remain event-driven bursts. Discovery and location describe configured X/Z geometry, not terrain/Y. Parent fallback intentionally need not geometrically contain a Local. Gameplay/FPS/TPS/restart/visual testing is manual.

Manual: legacy context/profile/NPC names unchanged; pilot hole interior/edge excludes; either multipart component yields one ID; priority/tie and parent rules unchanged; unknown geography absent from map packets; large reload publishes atomically; Hover/Locals/All/Off show separate coastlines; Features HUD still takes precedence; buttons clear coordinates/compass across resize; students share IDs, teachers do not discover; restart/reconnect/resync preserve state. No automatic installation/live-server change.
