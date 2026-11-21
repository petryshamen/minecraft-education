# Human Journey Core 0.15.1 — Macro/Local capacity release

**Verified Core 0.15.1:** [Actions build 44841342335](https://github.com/petryshamen/minecraft-education/actions/runs/44841342335), source `48caf041085bc71755efa2cb421df1dd293fc1d0`. 500 tests passed, zero failures; 15 no-world Forge checks. 329 prior runtime classes byte-identical. JAR SHA-256 `ff05f1206a753b32beb0e0cb4bbf1ae0b85ec0f87236edaad04abfac952532b6`. No Minecraft gameplay testing, installation, production-data or live-server changes.

1. **Version/source/build:** 0.15.1; Minecraft1.21.8 / Forge58.1.18. Source and successful build above; complete source ZIP includes workflow, tests, frozen external fixture and original attribution files. Runtime JAR excludes test fixtures and dependencies.
2. **Area limits:** only schema2 rises to512 rings /16384 vertices, counting every component outer and hole. V1 remains256 points. Geometry format and strict shared topology/containment predicates unchanged; no repair. Maximum-hole polygon and maximum multipart area pass native parser, wire codec and atomic reconstruction;513rings and16385vertices reject with totals/limits.
3. **Catalog limits unchanged:**512Macros /4096Locals; combined1,179,648vertices /65,536rings. Geography resource files4608 unchanged. Indexes and ID lookup remain immutable and constructed on accepted reload, never per tick/frame/player.
4. **Resources/serialization:** geography resource2MiB characters (was512KiB), total64MiB unchanged. This accommodates tested pretty-printed16384point geometry; arbitrary whitespace/metadata still counts. Serialized area288KiB (was80KiB), enough for16384 double-coordinate vertices plus bounded ring/metadata overhead. Non-geography NPC/economy resources stay256KiB /16MiB total. Invalid reload retains complete prior catalog/index; errors retain pack/resource/Area context and capacity totals.
5. **Protocols/fragments:** geography-map only2→3; ordinary geography context/discovery1 and Map Features2 unchanged. Wire layout unchanged.16KiB raw fragment /48KiB packet ceilings unchanged; maximum area17fragments. Unknown geometry excluded by existing discovered-only projection, hidden parents redacted. Full frozen39Macro/1549Local catalog round-trips every1588area atomically.
6. **Staging:**8192parts /24MiB estimated geometry/fragments per transaction /2pending unchanged.72maximum-vertex areas exercise the entire catalog vertex budget using1224fragments below staging quota. This is not a total Java heap promise: existing snapshots, decoded topology, indexes and pending bytes coexist. Incomplete, stale and conflicting transactions retain safe prior state; no partial publication.
7. **Europe blockers:** all190refined candidates pass with original coordinate bytes, totaling147626vertices/3944rings. Exact ten old blockers below. Frozen source review SHA-256 `6ee795b2682247ce0233a87b13b4fae6ee5cedbd75a4e51b3a630a3e4850d1c5`; test-only fixture SHA-256 `7f388f5354ad793bfc88834724b04cc412b836fab5c5af51f3f270158336461c`. Each selected raw entry hash checked automatically. Complete proposed catalog built using review migration manifest, without modifying production files.

| Original review Area ID | Vertices | Rings | Native 0.15.1 result |
|---|---:|---:|---|
| `baltic_archipelagos__northern_europe` | 1669 | 132 | accepted |
| `bothnian_coast` | 3712 | 363 | accepted |
| `central_swedish_lowland` | 3764 | 148 | accepted |
| `finnish_lakeland` | 3908 | 212 | accepted |
| `karelian_lake_country` | 3966 | 228 | accepted |
| `norrland_forest_uplands` | 3021 | 182 | accepted |
| `northern_fennoscandia` | 4821 | 261 | accepted |
| `norwegian_fjord_coast` | 4928 | 175 | accepted |
| `scandinavian_mountains` | 7919 | 236 | accepted |
| `southern_finnish_coastal_lowlands` | 3805 | 209 | accepted |

8. **Performance:** native validation/index/point resolution/transfer preparation results below, exact old source where supported. Shared supported fixture compared directly; new large cases have no successful old counterpart. Component BVH, bounding pruning, exact containment, priority and hole semantics are preserved. Heavy exact topology predicates can still be expensive for adversarial geometry; dense overlap cannot be culled away.
9. **Regression totals:**500 tests, zero failures:488 prior cases retained, three superseded limit assertions migrated;12new cases (11capacity+1benchmark). Separate same-runner old-source benchmark also passes.15no-world Forge checks pass, including16384vertices/512parts native codec+client reconstruction. Bytecode/source parity verification covers unchanged classes, original tests/examples/other schemas and release metadata. Actual unchanged class count is in verification JSON and summary above. No real Minecraft world/gameplay tests performed.
10. **Compatibility/migration:** update server AND all clients manually to0.15.1 before larger resources;0.15.0 geography-map clients are rejected at protocol3 gate. Existing v1/v2 content, stable IDs/parents/profiles/era labels/priorities, discovery SavedData/roles, HUD/buttons, NPC/economy and all unrelated gameplay stay unchanged. No discovery reset, main-datapack migration or automatic installation. Only obsolete quota assertions and geography-v2 schema bounds change.
11. **Limitations:** catalogs remain globally bounded, resources/metadata count toward JSON budgets, staging is an estimate. Point resolution is occasional indexed work; rendering still processes relevant visible vertices. Tests do not approve external review coverage gaps or parent/priority/content decisions, nor verify Minecraft persistence/visuals/TPS/FPS. No production data edited.
12. **Geography handoff:** resume the exact supplied refinement checkpoint; do not restart/redraw/simplify merely for capacity. Retain all IDs/parents/labels/profiles/priorities, use existing schema2 geometry and account for every outer/hole point. Keep global quotas, split catalogs over bounded resources. Planner/Geography separately approve coverage and remaining design issues; Developer integrates only accepted production data. Passing these tests is NOT approval to deploy the review ZIP.
13. **Manual acceptance:** on a separate test world, install matching server/clients; confirm legacy context/NPC/profile/discovery. Load separately approved detailed v2 data and /reload; verify /hj geography status, /hj npc context, holes/islands/edges and disconnected components, priority/fallback. Confirm discovered-only Borders Hover/Locals/All/Off and viewport culling, Feature location HUD, buttons clearing coordinates, teacher exclusion/shared student discovery. Test restart/reconnect retaining IDs, maximum-area full/delta sync and failed reload retaining old state. User performs gameplay acceptance; no live server was changed.

## Files/systems changed

`GeographyGeometry` quota preflight/count diagnostics; `NpcGeography` aggregate error detail only; `NpcContextStore`/`NpcContextReloadListener` per-resource limit/provenance; `MapPackets` per-area serialization limit (decoder recompiles new shared constants); `MapNetwork` geography-map version; `GeographyV2Smoke` maximum native check. Geography-v2 JSON schema/docs,12new tests,3migrated old quota methods, frozen provenance fixture and CI old-source benchmark. Resolver/profile/index implementations, Map Features, rendering/layout/HUD/discovery and unrelated gameplay stay unchanged.

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
