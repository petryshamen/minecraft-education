# Map Feature geometry v2 — Core 0.14.2

**Verified Core 0.14.2:** [GitHub Actions build 44783613526](https://github.com/petryshamen/minecraft-education/actions/runs/44783613526), source `d93a10f7acd02d8fd6f756adbbb33973bf93730a`. All 462 tests passed (444 retained +18new; one old quota method migrated to expanded limits), zero failures, plus14 no-world Forge checks and an identical benchmark harness against exact0.14.1 source. 316 prior runtime classes are byte-identical, including HUD/buttons/discovery/transport state/Xaero and unrelated systems. Runtime SHA-256 `89228db1e711913b99903de897821c88cffdb9ae1829710a3d561c621c0d035a`. No Minecraft gameplay testing, installation, production geography or live-server changes.

Capacity update0.14.2: schema-v2 now supports512rings/16384total vertices per feature, including multipart and open polylines. Schema1 remains512points. Catalog limits stay1,048,576vertices/65,536rings/2048features. JSON resource ceiling is2MiB characters (reload64MiB unchanged); serialized feature288KiB; fragments16KiB, packets48KiB, transfer staging24MiB and4096parts unchanged. The maximum synthetic holed polygon serializes to262712bytes (about257KiB) and uses17fragments; the288KiB ceiling includes metadata/topology headroom. Protocol/schema/SavedData identities unchanged. No production water data supplied or edited.

Geography can resume Europe Water using existing schema_version2 and stable IDs. Mediterranean/Baltic-like156–250island synthetic cases and the512ring/16384vertex extremes are tested. Count every outer and hole toward512rings and all their vertices toward16384; keep catalog totals bounded. Preserve approved island selection, priorities, labels and discovery_radius. Validate strict disjoint interior holes, `/reload`, inspect `/hj mapfeature status` and `/hj mapfeature show <id>`, then manually test holes/HUD/hover after updating all participating clients. No discovery reset or schema migration is required. Do not integrate production data until this Core build is accepted.


**Core 0.14.0 baseline:** [GitHub Actions build 44214582660](https://github.com/petryshamen/minecraft-education/actions/runs/44214582660), source `44d2b5dedacf258024e0de20ed2054b780e6472e`. All 424 tests passed (384 prior tests byte-identical + 40 new), zero failures; all fourteen no-world Forge checks passed. 288 prior runtime classes are byte-identical, including geography/HUD/controls, feature SavedData, NPC/economy and unrelated gameplay. Existing changes are confined to feature geometry/index/catalog/discovery/transport/diagnostics and the Xaero feature renderer. Runtime SHA-256 `39ac922532501161bdbcbd614aeb8e8a76bb6fe88d08f3010e2dc71378b1067c`. No Minecraft gameplay testing, installation, production pilot/main-datapack or live-server change.

Minecraft1.21.8 / Forge58.1.18 / optional Xaero World Map1.39.13. Update Core on server and participating clients together: the **feature channel is now protocol2**, required for topology and fragmented geometry. The Macro/Local geography protocol remains1. Schema-v1 JSON and ID-only SavedData stay compatible; no production migration/reset is needed just to load0.14.0. No Japan pilot/production content is changed or supplied.

## JSON contract

Resources remain `data/<namespace>/core/map_feature/<file>.json`. Existing metadata is unchanged: stable id, label, seven feature types, dimension, priority and discovery_radius. Root `schema_version` is1 or2. All schema1 polygon/polyline definitions retain their512point cap and old meaning. For schema2:

```json
{
  "schema_version": 2,
  "id": "synthetic_sea",
  "label": "Synthetic Sea",
  "feature_type": "sea",
  "dimension": "minecraft:overworld",
  "priority": 20,
  "discovery_radius": 8,
  "geometry": {
    "type": "polygon",
    "outer": [[0,0],[200,0],[200,200],[0,200]],
    "holes": [[[50,50],[100,50],[100,100],[50,100]]]
  }
}
```

`holes` is optional and defaults to[]. Polygon rings close last→first; do not repeat the first point at the end. Ring orientation is unrestricted. These are world X/Z coordinates, with no runtime wrapping/conversion/repair. A multipolygon uses:

```json
"geometry": {
  "type": "multipolygon",
  "polygons": [
    {"outer": [[-200,0],[-100,0],[-100,200],[-200,200]], "holes": []},
    {"outer": [[0,0],[200,0],[200,200],[0,200]],
     "holes": [[[50,50],[100,50],[100,100],[50,100]]]}
  ]
}
```

Each part has outer + optional holes; no per-part ID/type/label/dimension. All parts form **one feature ID**, label, priority, notification and discovery. Explicit parts on either side of X≈−12288/+12288 work without a connecting edge or longitude wrapping. Schema2 polyline remains `{ "type":"polyline", "points":[[0,0],[100,50]] }`; no holes/multipolyline are added.

JSON schema: `docs/map-feature.schema.json`, common metadata plus conditional v1/v2 geometry definitions. Core additionally enforces geometric topology and aggregate budgets that JSON Schema cannot express.

## Containment, distance and priority

A polygon part contains a point iff outer contains it (edge-inclusive) AND no hole contains it (hole edge-inclusive exclusion). Thus hole interiors and hole edges are **not water containment**. A multipolygon contains a point if ANY part contains it. Parts may overlap; union semantics apply, and an overlapping second part can intentionally contain a point excluded by the first. Prefer simple/disjoint production parts when practical. Holes are scoped to their own part and do not subtract from other features.

Discovery is water containment OR horizontal distance≤discovery_radius to any outer/hole boundary. A student deep inside an island hole does not discover by containment; near its coast, boundary distance can legitimately discover the water. Either qualifying part discovers the whole single feature. No Y/terrain/biome/Local-area mask is consulted.

Priority is unchanged: highest-priority discovered containing feature wins; distinct equal-priority feature ties fail closed. Several containing parts of the same feature do not create a tie; lowest matching part index selects a deterministic relevant component. Direct river hit still precedes polygon/multipolygon matches, with the unchanged5GUIpixel tolerance. Existing feature-versus-Local/Macro tooltip precedence remains; when no feature matches an island hole, ordinary geography may resume. A Sea hole does not automatically mask a lower-priority Ocean: each overlapping water feature needs accurate exclusions of its own.

## Validation and complexity bounds

Every ring is finite/within±30million,≥3 distinct points, non-degenerate, without duplicate vertices, zero-length/backtracking edges or self-intersections. Signed numeric zero is treated as the same position for duplicate validation; coordinates themselves are not rewritten. Immutable per-ring segment BVHs prune self-intersection and inter-ring crossing checks at construction. Exact predicates, adjacent-edge checks and hole topology requirements remain unchanged; indexes are never rebuilt per frame.

Holes must be strictly inside the outer ring with disjoint boundaries. Touching/crossing the outer ring, overlapping/touching/nested holes, malformed data and unsupported fields reject the **entire feature/catalog reload**, with resource/pack/feature/part/ring context. No clipping/repair is attempted. A single contained hole point plus absence of any ring crossing proves full containment for validated simple rings, including concave outers. Existing last-valid catalog semantics remain.

| Limit | Final value |
|---|---:|
| Effective feature definitions | 2048 |
| Schema1 points/feature | 512 |
| Schema2 total vertices/feature | 16384 |
| Total polygon rings/feature (outer + holes) | 512 |
| Polygon parts/feature | 512, constrained by total rings |
| Catalog vertices | 1,048,576 |
| Catalog polygon rings | 65,536 |
| JSON characters/resource | 2MiB |
| JSON characters/reload | 64MiB |
| Serialized feature | 288KiB |
| Raw fragment payload | 16KiB |
| Feature network packet | 48KiB |
| Transfer parts | 4096 |
| Estimated geometry/fragment staging budget | 24MiB/transaction |
| Pending transactions | 2 |

The512ring target supports512disconnected outers, or one outer +511holes, sharing the16384vertex budget. Tests exercise both at the limit. Catalog quotas are enforced incrementally while parsing and before building/publishing indexes; this avoids accepting2048maximum-size objects without a global budget. V2 open lines may also use16384points. No multipolyline support is introduced.

## Part-aware index and rendering

The immutable per-dimension BVH has a separate entry per polygon part, or one entry for an open line, each retaining feature ID and part index. Broad combined feature bounds remain metadata only, not a world-spanning index entry. Point lookup checks holes only after an outer-part candidate contains the point. Discovery uses relevant part candidates and deduplicates successful IDs. Wrong dimensions match nothing. Large distant/seam components therefore do not create hits in the gap.

Hover selection carries the matching part index. Hover highlights **that component's outer + holes**, not disconnected components elsewhere. In All, each viewport-visible part renders normally, then the selected component is stronger. Outer and hole rings are separate closed draw paths; polylines remain open; no paths connect parts. Ring bounds are culled before existing world-space clipping and the unchanged Xaero transform. Colours/config stay #609FB8/1.0/.35 normal, #76DAFF/2.0/.85 selected. No fill, per-part colours, permanent labels or tooltip/control redesign.

## Feature protocol2 and atomic publication

The feature channel remains protocol2; the wire structure is unchanged. Update Core server and clients to0.14.2 before enabling expanded geometry: older protocol2 clients enforce their old capacity and cannot reconstruct larger features. ID/label/type/dimension/priority/topology presentation data remain discovered-only; no radius, profiles, hidden catalog or progression is sent. Small features≤512vertices batch up to4per packet as before. Larger features serialize to≤288KiB and split into indexed≤16KiB fragments with stable ID/count/length metadata. Each fragment packet contains one fragment, staying below48KiB. Full transaction headers retain session/sequence/discovery/catalog revision and exact delta base.

The client stages at most2bounded transactions, validates fragment lengths/indices/identity, reconstructs each complete feature, checks topology and catalog quotas, then atomically publishes the complete snapshot/index. Partial large features are never rendered. Missing/conflicting/duplicate fragments, extra bytes, stale/revision-regressing data or invalid geometry preserve the last valid state. Reset/reconnect/resync protections remain. Header overhead and whole-feature staging are bounded separately by feature/part counts; the24MiB budget tracks geometry estimates and fragment payload, not the complete Java heap footprint. Current visible state plus staging may coexist. Full known-world transfer is still event-driven rather than bandwidth-budgeted per tick.

No geometry or schema number is added to SavedData. `human_journey_map_features.dat` still stores stable IDs + revision, byte-compatible with0.13.0. Changing a known ID's definition fromv1 tov2 retains knowledge. Valid reload refreshes holes/parts on connected clients; invalid reload retains the full prior catalog. No reset or personal discovery migration.

## Diagnostics

`/hj mapfeature status` retains its fields and adds schema-v1/v2 counts, polygon/multipolygon/polyline counts, total polygon parts, holes, vertices and largest feature/vertex count. `/hj mapfeature show <id>` (permission2) shows schema, geometry kind, part/hole/ring/vertex counts, priority and radius. Existing mark/list/reset commands, `/hjmap status` and combined `/hjmap resync` remain. All legacy UI modes, compass-safe controls, roles/notifications and seven water types are retained.

## Geography handoff: migrate the pilot without losing discoveries

1. Keep each production feature's **id**, label, type, dimension, priority and radius. Do not edit/reset the discovery ledger.
2. For a simple polygon needing island exclusions, set schema_version2, replace geometry.points with geometry.outer containing the same outer ring, and add geometry.holes from Geography's own approved simplified island coordinates. Do not repeat ring endpoints. Do not add island Local Areas as masks.
3. For genuinely disconnected/seam geometry, use multipolygon.polygons with one outer/holes object per component under the same feature ID. Choose explicit world coordinates; Core never wraps longitude or joins components.
4. Check every hole strictly inside its outer and not touching/nesting/crossing another ring. Keep total feature≤512rings/16384vertices and catalog quotas. Simplify to HJ scale; importing every tiny island is not required or recommended.
5. Overlapping Ocean/Sea/Strait features each need geometrically appropriate exclusions; priority only chooses among true matches. It cannot repair an island left inside another feature's outer.
6. Test copies via `/reload`, status/show and manual checklist below, then let Developer integrate approved content separately. Existingv1 rivers/polygons can stay unchanged. This Core task supplies no Japan/real-world holes or priority choices and never modifies the pilot/main datapack.

`example-map-features-v2` is a separate synthetic-only pack with a holed Sea, overlapping Strait, distant components and a seam-style feature. Existingv1 example-map-features remains unchanged. Neither pack is bundled as runtime feature definitions or installed automatically.

## Manual checklist A–E

Use a separate test world/unknown feature IDs for natural-discovery checks; resetting feature discovery is unnecessary for a production migration.

**A — Island hole:** synthetic Sea water shows Sea tooltip; deep hole interior/edge does not match water, so geography can resume. Before discovery, a student deep inside the hole learns nothing; near its coast within8blocks can discover the Sea. Hover has no Sea hit on the hole edge even though distance discovery there is valid.

**B — Strait over Sea:** higher-priority Strait wins its actual water; Sea elsewhere. Hole exclusions apply independently; no Local masking or priority changes.

**C — Multipolygon:** both disconnected components show the same feature label/ID. Encountering either gives one discovery/notification, one stored ID and knowledge of the whole feature. Hover highlights the relevant component and its holes; All shows visible known parts.

**D — Seam fixture:** inspect components near both X edges under one ID. No connecting world-spanning line, no gap hover hit, same identity/knowledge at either end. Pan/zoom and GUI scales retain alignment.

**E — Regression/migration:** keepv1 simple polygons/rivers; all HJ Borders/Features modes, compass-safe controls and cursor tooltips work. Teacher/conflicting roles cannot discover; shared student knowledge, restart/reconnect/resync and mark/reset behave as before. Migrate a known synthetic ID v1→v2, `/reload` and verify knowledge remains and holes update; invalid hole retains prior catalog. Update server/clients to0.14 together. No live Minecraft gameplay/UI/persistence testing was performed by the agent.

## Known limits

Validation is planar world X/Z with the existing edge tolerance; no spherical/wrapping/terrain repair. Segment-BVH topology validation remains bounded by per-feature/catalog quotas; adversarial heavily overlapping segment bounds can still approach quadratic exact-predicate work. Production simplification matters. Equal-priority distinct features still fail closed. Overlapping parts are union semantics, not an automatic dissolve or cross-part hole subtraction. Very dense overlap increases candidates; maximal full-state transfer/staging remains bounded but can create an event-driven CPU/network burst. Actual UI and world restart acceptance remain manual.

## Synthetic same-runner benchmark

Exact0.14.1 source (commitdf297f47da828ee40ca8796bd00a028a637db461) and0.14.2 run the identical harness on the same GitHub runner/JDK. Two warmups, median of seven samples; lookup is median batch of2000 alternating water/hole positions. Validation includes Gson parsing and topology; indexing is a separate immutable FeatureIndex build; serialization is raw feature serialization plus fragmentation (not network latency). No performance thresholds depend on timing luck.

| Synthetic case | Holes / vertices | Validation ms | Index ms | Serialization ms | Lookup us | Bytes / fragments |
|---|---:|---:|---:|---:|---:|---:|
| 0.14.1 shared baseline | 100 / 3712 | 9.076 | 0.004 | 0.860 | 2.254 | 59549 / 4 |
| shared_baseline | 100 / 3712 | 17.504 | 0.003 | 0.728 | 4.500 | 59549 / 4 |
| mediterranean_like | 156 / 5504 | 17.169 | 0.002 | 0.500 | 5.300 | 88278 / 6 |
| baltic_like | 250 / 9024 | 27.693 | 0.003 | 0.714 | 7.250 | 144692 / 9 |
| maximum | 511 / 16384 | 25.384 | 0.003 | 1.040 | 2.781 | 262712 / 17 |

On the shared3712vertex case, new/old elapsed-time ratios (lower is faster): validation: 1.93x, indexing: 0.82x, serialization: 0.85x, lookup: 2.00x. The shared small case was slower in validation/lookup in this run; segment BVHs have an up-front construction cost, and lookup/index bytecode itself is unchanged. This is not evidence of a general speedup. Production-like larger cases and the maximum are rejected by exact0.14.1 ring/vertex limits, so no successful old pipeline or meaningful timing ratio exists for those sizes. They are now supported without relaxing catalog quotas. Synthetic circles/grid islands approximate complexity, not real coastline shape. Shared-runner scheduling, JVM warmup and GC can affect these measurements; these are unit benchmarks, not Minecraft gameplay/FPS/server-tick performance.
