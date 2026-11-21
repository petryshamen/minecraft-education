# Macro/Local Geography geometry v2 — Core0.15.1

**Verified Core 0.15.1:** [Actions build 44841342335](https://github.com/petryshamen/minecraft-education/actions/runs/44841342335), source `48caf041085bc71755efa2cb421df1dd293fc1d0`. 500 tests passed, zero failures; 15 no-world Forge checks. 329 prior runtime classes byte-identical. JAR SHA-256 `ff05f1206a753b32beb0e0cb4bbf1ae0b85ec0f87236edaad04abfac952532b6`. No Minecraft gameplay testing, installation, production-data or live-server changes.

Capacity release0.15.1: v2 now permits512rings/16384vertices per area. Schema1 stays256points. Catalog counts512Macros/4096Locals and1,179,648vertices/65,536rings remain unchanged. JSON resource limit2MiB characters, total64MiB unchanged; serialized map area288KiB, raw fragments16KiB, packets48KiB,8192transaction parts,24MiB staging and2pending transactions. Only geography-map protocol advances2→3, rejecting older0.15.0 clients at the channel gate; ordinary context/discovery stays1 and Map Features stays2. Update server/all clients together BEFORE using larger geometry. Wire layout/schema/SavedData identities remain, with no discovery reset.

The attached Europe review is frozen external regression data, not production content approval. Its190refined IDs (147626vertices/3944rings) and full39Macro/1549Local proposed catalog are exercised without changing coordinates; bytes and SHA-256 provenance are retained in a test-only fixture ZIP. All ten old capacity blockers are explicitly enumerated in the release report. Review coverage gaps, geography/priority/parent decisions and terrain acceptance remain separate. Do not deploy the review simply because Core now accepts it.

Geography resume: continue from the exact supplied checkpoint; no redraw/restart/reduced-detail workaround needed for capacity. Keep IDs, parents, profiles, priorities, labels and existing schema2. Count all outers+holes toward512rings and all points toward16384, respect aggregate quotas, and validate normal formatted per-area resources under2MiB. Resource/Area quota errors now show actual vertex/ring totals and limits. Planner/Geography separately resolve coverage and approve the content migration; Developer integrates only accepted data. No main datapack or review coordinates were modified by Core.


**Core0.15.0 baseline:** [GitHub Actions build 44800372832](https://github.com/petryshamen/minecraft-education/actions/runs/44800372832), source `ea34c69737ce4e8bbc92b7c0ae3a4921d2c5dd01`. 488tests passed (462retained+26new; two superseded packet-limit/button-position assertions updated), zero failures;15no-world Forge checks and exact0.14.2 same-runner reference benchmark passed. 300prior runtime classes are byte-identical, including all Map Feature/HUD classes, SavedData/discovery, water/juice/temperature/fishing and NPC economy/adapter. Runtime SHA-256 `8cda56aec9f1e1af6c2a439902a88cf25f51f9722f68f800dbc21b118f17c092`. No Minecraft gameplay testing, automatic installation, production geography or live-server changes.

Resources remain `data/<namespace>/npc_geography/<file>.json`. Root `schema_version` defaults1; schema1 uses the unchanged `polygon` field and256point cap. A resource may instead select `schema_version:2`, and every area in that resource uses `geometry` (not `polygon`). V1/v2 files coexist. Metadata is unchanged: id, dimension, priority, macro_region for Locals, display_label, era_labels, profile. There is no third layer or production data.

```json
{"schema_version":2,"macro_regions":[{"id":"synthetic_macro","display_label":"Synthetic Macro","geometry":{"type":"polygon","outer":[[0,0],[100,0],[100,100],[0,100]],"holes":[[[20,20],[40,20],[40,40],[20,40]]]}}],"local_areas":[{"id":"synthetic_local","macro_region":"synthetic_macro","display_label":"Synthetic Local","geometry":{"type":"multipolygon","polygons":[{"outer":[[50,50],[60,50],[60,60],[50,60]]},{"outer":[[150,50],[160,50],[160,60],[150,60]],"holes":[]}]}}]}
```

All coordinates are explicit X/Z, finite within±30million; no wrapping. Outer boundaries contain; hole interiors AND boundaries exclude. Multipolygon uses ANY-part union, with holes scoped to each part; overlapping components do not become separate identities. Each ring must be simple,≥3distinct points, no repeated closing endpoint, duplicate/backtracking/zero-length/self-intersection. Holes must lie strictly inside outer without touching/crossing/overlap/nesting. No automatic repair. Reuses FeatureGeometry Ring/Part/segment-BVH predicates exactly; independent geography budget wrapper and IDs/discovery remain separate from Map Features.

| Budget | Value |
|---|---:|
| Schema1 points/area |256|
| V2 total vertices/area |16384|
| V2 total rings/area (outers+holes) |512|
| Macros / Locals |512 /4096|
| Aggregate Macro+Local vertices |1,179,648|
| Aggregate Macro+Local rings |65,536|
| Geography resource files |4608|
| JSON characters/geography resource |2MiB|
| JSON characters/geography reload |64MiB|
| Non-geography NPC/economy JSON |original256KiB/resource,16MiB total|
| Serialized map area |288KiB|
| Raw fragment / packet |16KiB /48KiB|
| Transfer parts |8192|
| Estimated staged geometry/fragments |24MiB/transaction,2pending|

The vertex aggregate equals4608×256, preserving the theoretical existing v1 maximum. V2 areas share aggregate budgets, not4608×16384. JSON limits apply to encoded content as well as geometry; split large resource catalogs over files. The new geography file-count/resource limits allow one-file-per-area and ordinary16384point geometry. Pending staging estimates do not represent total Java heap: current snapshots, reconstructed objects, point/segment/component BVHs and staged bytes coexist.

## Authoritative indexed resolution

Accepted reload validates the complete context/economy/geography set, increments geometry quotas during parsing, validates parents, creates immutable ID maps and per-dimension/kind component BVHs, then publishes one complete state. Invalid data retains previous catalog/index. NpcGeography.resolve queries MACRO and LOCAL bounding candidates, exact-tests shared hole-aware component geometry, deduplicates stable IDs, then selects highest priority. Distinct equal-priority matches throw/fail closed. A Local's configured parent is used if no Macro contains the position; a conflicting independently matched Macro fails as before. Profile rules and era labels unchanged. GeographyContext, NPC and economy use this authoritative resolver; profile/context ID lookup uses immutable maps. No runtime index rebuilding.

The discovered client MapSpatialIndex uses the same low-level component BVH. Its established discovered-parent tooltip fallback remains unchanged (it can show a known parent even outside that parent's containment); a parent with no matching component is not spuriously highlighted. Ordinary context packets stay IDs/labels only. Map projections include known IDs only and redact unknown parent references.

## Map transport and presentation

**Update server and clients together:** only `human_journey_core:geography_map` uses protocol3 for expanded capacity; wire layout remains the bounded nested-topology format. Ordinary geography context/discovery channel remains1; Map Feature protocol remains2. No mixed geography-map2/3 compatibility. Small≤256vertex areas batch≤8; larger areas serialize≤288KiB into≤16KiB kind+ID/index/count/length fragments. Max16384vertex geometry uses17fragments. Whole/delta/clear headers retain session/sequence/discovery/data/presentation revisions and exact base. Completed geometry/topology/aggregate/parent validation precedes atomic state/index publication; incomplete/stale/revoked/conflicting transactions do not expose partial areas. Existing resync/reconnect protection remains.

Hover resolves holes and individual components. Hover highlights matching component's outer+holes; Locals/All viewport-cull components then render every outer/hole as separate closed paths. No connecting lines, fills, colour/width/opacity or tooltip changes. Features/HUD precedence unchanged. SavedData still stores stable Macro/Local IDs, not geometry; no discovery reset/migration. Schema1 old production resources stay valid. No production polygons/profiles/priorities edited.

## Map buttons

The two buttons now stay on the far-right rail,2GUI pixels from the edge, beginning below compass at y108/132. If native widgets occupy that slot, they move down the SAME right rail; there is no leftward fallback over Xaero's centred coordinate readout. No safe right slot means controls hide for that frame and config remains usable. Single-button legacy helper is unchanged. At1000GUI width, x880 (previous preferred874, but previous fallback could shift much farther left). Test actual GUI scales manually; no live visual testing performed.

## Geography handoff / Europe refinement pilot

1. Accept/install Core0.15.1 manually on server/all clients first. Keep existing stable IDs, hierarchy, parents, labels/era labels, profiles and priorities. Do not reset discovery.
2. Keep valid v1 files unchanged. For detailed pilot areas, use a separate schema_version2 file and replace polygon with geometry.outer/holes or geometry.polygons. A file has one schema version; do not mix area field formats within it.
3. Count every outer+hole toward512rings and all points toward16384 per area; enforce1,179,648/65,536 global totals and512/4096area caps. No coordinate wrapping, Local-as-water-mask or production repair is provided.
4. `/reload`; inspect server resource/area/component/ring errors and `/hj geography status` candidate counts. Invalid reload keeps complete previous state. Check `/hj npc context`, profiles and fixed NPC spawn-time context.
5. Manually test water/land hole positions, disconnected parts, overlapping priorities, known parent fallback/conflicts, discovered-only hover, all Borders/Features modes and Map Feature location HUD. A known area migrated v1→v2 should keep discovery after restart/reconnect; teachers still cannot discover.
6. Geography/Developer separately author and approve Europe polygons. This engine release supplies synthetic tests and frozen external regression data, no production Europe/Japan edits, no profile/priority changes.

## Known limits / manual checklist

Dense overlap returns many real bounding candidates; an index cannot remove actual overlap. Topology has bounded but potentially quadratic adversarial exact-predicate work. Frame rendering still processes vertices of relevant visible components. Full known-world transfers remain event-driven bursts. Discovery and location describe configured X/Z geometry, not terrain/Y. Parent fallback intentionally need not geometrically contain a Local. Gameplay/FPS/TPS/restart/visual testing is manual.

Manual: legacy context/profile/NPC names unchanged; pilot hole interior/edge excludes; either multipart component yields one ID; priority/tie and parent rules unchanged; unknown geography absent from map packets; large reload publishes atomically; Hover/Locals/All/Off show separate coastlines; Features HUD still takes precedence; buttons clear coordinates/compass across resize; students share IDs, teachers do not discover; restart/reconnect/resync preserve state. No automatic installation/live-server change.

## Core0.15.0 baseline benchmark (historical)

The identical GeographyIndexBenchmarkTest harness runs against new source and exact0.14.2 commitc38e2e688b9ef218d7c53bf6b744d8e33be26d35 on the same GitHub runner/JDK21. Two warmups and median of five measured samples; repeated lookup batches256. Parsing includes JSON parsing, geography validation, ID maps and construction of the ready immutable index; it excludes full NPC/economy composition validation and client reconstruction. Existing composition safety caps remain unchanged. Index duration is recorded separately during construction (a single constructor sample, not a repeated-timing median); baseline has no index. Single lookup timings are noise-sensitive. Coarse areas have4vertices/1ring; detailed v2 areas64vertices/2rings including a hole. Fixtures split over32area resources and remain within quotas.

| Version / fixture | Locals | Points/rings per area | Parse+validate+publish ms | Index ms | Single lookup us | Repeated lookup us | Candidates |
|---|---:|---:|---:|---:|---:|---:|---:|
| 0.14.2 / 1500_v1_coarse_distributed | 1500 | 4/1 | 19.899 | 0.000 | 275.075 | 17.937 | 1500 |
| 0.14.2 / 1500_v1_coarse_dense | 1500 | 4/1 | 11.169 | 0.000 | 221.595 | 59.618 | 1500 |
| 0.14.2 / 4096_v1_coarse_distributed | 4096 | 4/1 | 16.029 | 0.000 | 86.972 | 47.576 | 4096 |
| 0.14.2 / 4096_v1_coarse_dense | 4096 | 4/1 | 9.655 | 0.000 | 93.872 | 87.879 | 4096 |
| 0.15.0 / 1500_v1_coarse_distributed | 1500 | 4/1 | 27.131 | 13.087 | 4.958 | 1.498 | 1 |
| 0.15.0 / 1500_v1_coarse_dense | 1500 | 4/1 | 16.865 | 4.284 | 307.773 | 59.562 | 1500 |
| 0.15.0 / 1500_v2_detailed_distributed | 1500 | 64/2 | 120.279 | 1.574 | 21.592 | 1.166 | 1 |
| 0.15.0 / 1500_v2_detailed_dense | 1500 | 64/2 | 86.506 | 0.756 | 251.640 | 237.763 | 1500 |
| 0.15.0 / 4096_v1_coarse_distributed | 4096 | 4/1 | 21.797 | 2.849 | 0.151 | 0.107 | 1 |
| 0.15.0 / 4096_v1_coarse_dense | 4096 | 4/1 | 30.176 | 1.010 | 296.928 | 197.360 | 4096 |
| 0.15.0 / 4096_v2_detailed_distributed | 4096 | 64/2 | 338.249 | 2.763 | 0.401 | 0.194 | 1 |
| 0.15.0 / 4096_v2_detailed_dense | 4096 | 64/2 | 329.067 | 0.898 | 776.983 | 640.716 | 4096 |

Distributed queries inspect exactly1component candidate at1500and4096Locals, versus all1500/4096in the exact legacy linear resolver. Dense-overlap fixtures genuinely contain all areas, so candidates remain1500/4096and can be slower with index/validation/deduplication overhead; the index cannot eliminate real overlap. Selection is single-pass by priority after containment, with duplicate-ID handling only for multipart matches, not a sort of all matches. Exact0.14.2 cannot parse v2, so detailed-v2 timings have no successful legacy counterpart. No TPS/FPS/gameplay performance is inferred; JVM warmup, scheduling and GC affect these synthetic measurements. Source harness, fixture generation and workflow are in the complete source ZIP; raw measurements are also in the Actions artifact.

## Capacity benchmark

Identical harness and frozen fixture bytes on the same GitHub runner/JDK21, comparing exact 0.15.0 archive commit 2810f7c240af0e0086cedd5439dfbff994b1fcee. Two warmups, median five measurements; point queries batched256. Parsing includes JSON, topology validation and ready index construction. *Index time is a single constructor measurement, not a median. Transfer preparation measures serialization/fragment creation, not network latency or client reconstruction. Lookup samples use one outer-ring vertex and are not a representative whole-world route. Unsupported cases are rejected by old capacity, not timed as successful loads. JVM/GC/scheduling affect timings; no gameplay TPS/FPS claim.

| Version / fixture | Vertices / rings | Parse+validate+index ms | Index ms* | Lookup us | Candidates | Transfer preparation ms | Fragments |
|---|---:|---:|---:|---:|---:|---:|---:|
| 0.15.0 / shared_100_holes | 3712 / 101 | 11.121 | 1.429 | 5.673 | 1 | 0.673 | 4 |
| 0.15.0 / synthetic_250_holes | 9024 / 251 | unsupported | — | — | — | — | — |
| 0.15.0 / synthetic_maximum | 16384 / 512 | unsupported | — | — | — | — | — |
| 0.15.0 / bothnian_coast | 3712 / 363 | unsupported | — | — | — | — | — |
| 0.15.0 / scandinavian_mountains | 7919 / 236 | unsupported | — | — | — | — | — |
| 0.15.1 / shared_100_holes | 3712 / 101 | 12.763 | 1.519 | 6.455 | 1 | 0.387 | 4 |
| 0.15.1 / synthetic_250_holes | 9024 / 251 | 21.402 | 0.049 | 3.640 | 1 | 1.110 | 9 |
| 0.15.1 / synthetic_maximum | 16384 / 512 | 28.387 | 0.031 | 3.455 | 1 | 0.933 | 17 |
| 0.15.1 / bothnian_coast | 3712 / 363 | 9.792 | 4.666 | 1.611 | 1 | 0.282 | 4 |
| 0.15.1 / scandinavian_mountains | 7919 / 236 | 14.374 | 0.382 | 0.863 | 1 | 0.296 | 8 |
