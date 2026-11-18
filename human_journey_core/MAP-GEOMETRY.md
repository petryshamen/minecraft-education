# Map Feature geometry v2 — Core 0.14.0

**Verified Core 0.14.0:** [GitHub Actions build 44214582660](https://github.com/petryshamen/minecraft-education/actions/runs/44214582660), source `44d2b5dedacf258024e0de20ed2054b780e6472e`. All 424 tests passed (384 prior tests byte-identical + 40 new), zero failures; all fourteen no-world Forge checks passed. 288 prior runtime classes are byte-identical, including geography/HUD/controls, feature SavedData, NPC/economy and unrelated gameplay. Existing changes are confined to feature geometry/index/catalog/discovery/transport/diagnostics and the Xaero feature renderer. Runtime SHA-256 `39ac922532501161bdbcbd614aeb8e8a76bb6fe88d08f3010e2dc71378b1067c`. No Minecraft gameplay testing, installation, production pilot/main-datapack or live-server change.

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

Every ring is finite/within±30million,≥3 distinct points, non-degenerate, without duplicate vertices, zero-length/backtracking edges or self-intersections. Signed numeric zero is treated as the same position for duplicate validation; coordinates themselves are not rewritten. Segment bounding boxes and an X-sorted broad phase prune self-intersection checks.

Holes must be strictly inside the outer ring with disjoint boundaries. Touching/crossing the outer ring, overlapping/touching/nested holes, malformed data and unsupported fields reject the **entire feature/catalog reload**, with resource/pack/feature/part/ring context. No clipping/repair is attempted. A single contained hole point plus absence of any ring crossing proves full containment for validated simple rings, including concave outers. Existing last-valid catalog semantics remain.

| Limit | Final value |
|---|---:|
| Effective feature definitions | 2048 |
| Schema1 points/feature | 512 |
| Schema2 total vertices/feature | 4096 |
| Total polygon rings/feature (outer + holes) | 128 |
| Polygon parts/feature | 128, constrained by total rings |
| Catalog vertices | 1,048,576 |
| Catalog polygon rings | 65,536 |
| JSON characters/resource | 512KiB |
| JSON characters/reload | 64MiB |
| Serialized feature | 80KiB |
| Raw fragment payload | 16KiB |
| Feature network packet | 48KiB |
| Transfer parts | 4096 |
| Estimated geometry/fragment staging budget | 24MiB/transaction |
| Pending transactions | 2 |

The128ring target supports128disconnected outers, or one outer +127holes, sharing the4096vertex budget. Tests exercise both at the limit. Catalog quotas are enforced incrementally while parsing and before building/publishing indexes; this avoids accepting2048maximum-size objects without a global budget. V2 open lines may also use4096points. No multipolyline support is introduced.

## Part-aware index and rendering

The immutable per-dimension BVH has a separate entry per polygon part, or one entry for an open line, each retaining feature ID and part index. Broad combined feature bounds remain metadata only, not a world-spanning index entry. Point lookup checks holes only after an outer-part candidate contains the point. Discovery uses relevant part candidates and deduplicates successful IDs. Wrong dimensions match nothing. Large distant/seam components therefore do not create hits in the gap.

Hover selection carries the matching part index. Hover highlights **that component's outer + holes**, not disconnected components elsewhere. In All, each viewport-visible part renders normally, then the selected component is stronger. Outer and hole rings are separate closed draw paths; polylines remain open; no paths connect parts. Ring bounds are culled before existing world-space clipping and the unchanged Xaero transform. Colours/config stay #609FB8/1.0/.35 normal, #76DAFF/2.0/.85 selected. No fill, per-part colours, permanent labels or tooltip/control redesign.

## Feature protocol2 and atomic publication

The feature channel alone advances to2. ID/label/type/dimension/priority/topology presentation data remain discovered-only; no radius, profiles, hidden catalog or progression is sent. Small features≤512vertices batch up to4per packet as before. Larger features serialize to≤80KiB and split into indexed≤16KiB fragments with stable ID/count/length metadata. Each fragment packet contains one fragment, staying below48KiB. Full transaction headers retain session/sequence/discovery/catalog revision and exact delta base.

The client stages at most2bounded transactions, validates fragment lengths/indices/identity, reconstructs each complete feature, checks topology and catalog quotas, then atomically publishes the complete snapshot/index. Partial large features are never rendered. Missing/conflicting/duplicate fragments, extra bytes, stale/revision-regressing data or invalid geometry preserve the last valid state. Reset/reconnect/resync protections remain. Header overhead and whole-feature staging are bounded separately by feature/part counts; the24MiB budget tracks geometry estimates and fragment payload, not the complete Java heap footprint. Current visible state plus staging may coexist. Full known-world transfer is still event-driven rather than bandwidth-budgeted per tick.

No geometry or schema number is added to SavedData. `human_journey_map_features.dat` still stores stable IDs + revision, byte-compatible with0.13.0. Changing a known ID's definition fromv1 tov2 retains knowledge. Valid reload refreshes holes/parts on connected clients; invalid reload retains the full prior catalog. No reset or personal discovery migration.

## Diagnostics

`/hj mapfeature status` retains its fields and adds schema-v1/v2 counts, polygon/multipolygon/polyline counts, total polygon parts, holes, vertices and largest feature/vertex count. `/hj mapfeature show <id>` (permission2) shows schema, geometry kind, part/hole/ring/vertex counts, priority and radius. Existing mark/list/reset commands, `/hjmap status` and combined `/hjmap resync` remain. All legacy UI modes, compass-safe controls, roles/notifications and seven water types are retained.

## Geography handoff: migrate the pilot without losing discoveries

1. Keep each production feature's **id**, label, type, dimension, priority and radius. Do not edit/reset the discovery ledger.
2. For a simple polygon needing island exclusions, set schema_version2, replace geometry.points with geometry.outer containing the same outer ring, and add geometry.holes from Geography's own approved simplified island coordinates. Do not repeat ring endpoints. Do not add island Local Areas as masks.
3. For genuinely disconnected/seam geometry, use multipolygon.polygons with one outer/holes object per component under the same feature ID. Choose explicit world coordinates; Core never wraps longitude or joins components.
4. Check every hole strictly inside its outer and not touching/nesting/crossing another ring. Keep total feature≤128rings/4096vertices and catalog quotas. Simplify to HJ scale; importing every tiny island is not required or recommended.
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

Validation is planar world X/Z with the existing edge tolerance; no spherical/wrapping/terrain repair. Ring topology validation uses bounded broad-phase pruning and can approach quadratic work for adversarial geometry. Production simplification matters. Equal-priority distinct features still fail closed. Overlapping parts are union semantics, not an automatic dissolve or cross-part hole subtraction. Very dense overlap increases candidates; maximal full-state transfer/staging remains bounded but can create an event-driven CPU/network burst. Actual UI and world restart acceptance remain manual.
