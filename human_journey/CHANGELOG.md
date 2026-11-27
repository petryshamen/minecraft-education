# Human Journey changelog

## 0.5.26

- Integrated 194 exact geometry-only South/East Asia Local replacements, replacing two aggregates with per-area schema-v2 resources and preserving source attribution/licenses. Totals remain 39 Macros / 1,549 Locals in 934 geography resources; IDs, profiles, labels, priorities, parents and saved discovery remain unchanged.
- Core 0.18.0 accepts the complete catalogue and reproduces all 49,060 supplied baseline/result pairs: 43,393 -> 46,350 covered land-grid samples (+2,957, zero sampled losses/errors). Target dry-land coverage 93.00664% -> 99.69105%; approximately 1.45 blocks² of exact source sliver losses remain. Residual coastal/parent gaps remain deferred. All unrelated source, prior geometry and 435 Water Features are byte-identical; no Minecraft gameplay testing.

## 0.5.25

- Integrated 182 exact geometry-only Central/Northern Asia replacements (74 Central, 108 Northern including five western-dateline records), replacing three aggregates; 39 Macros and 1,549 Local IDs remain in 742 geography resources. Metadata, profiles, all 435 Water Features and unrelated content remain unchanged.
- Offline Core 0.17.0 accepts all 37,951 fixtures: 33,712 -> 36,033 resolved land-grid points (+2,321; zero sampled losses or resolution errors). Supplied candidate geometry is unchanged; residual source/coastal/parent-interface gaps and approximately 8.95 square blocks of vector sliver loss remain documented in geography notes. No Minecraft gameplay or terrain testing.

## 0.5.24

- Renamed only the native root display title and matching Era region title to “Human Journey — Era 0 — The First Camp”. Evenly spaced Survival at X=62/146/230/314/398 and Journey at X=62/174/286/398; Tools stays 62/118/174/230/286/342/398. Rows, root/capstone coordinates, moss styling, custom waypoints and all advancement parents/criteria/rewards remain unchanged.
- Core 0.17.0 schema/parser and 470 structural checks pass, including title-only advancement diff, exact allowed presentation diff and two-extra-plus-one-native capstone convergence. All unrelated progression, roles, gameplay, geography and dormant graph bytes are preserved. User verified the preceding 0.5.23 interface in Minecraft; this polish patch has automated validation only and awaits manual visual acceptance.

## 0.5.23

- Added Core 0.17.0 native advancement presentation under the existing Era 0 root: three parallel branches, 18 positioned native widgets (root plus 17 milestones), two extra links and the unchanged Journey parent converge at WE CAN STAY. One gold-bordered Era 0 region uses the built-in moss block texture as requested; no new assets, Era 1 region or separate screen.
- Core parser/model checks pass 578 assertions across 119 visibility scenarios; schema, actual advancement source ancestry, duplicate suppression, hidden-endpoint filtering, empty/invalid fallback store, native pixel routing/header/viewport geometry and built-in moss asset checks pass. Existing advancement definitions, gameplay/functions, world-state contracts, geography/economy/survival data and dormant civilization_progress.json remain byte-identical. No Minecraft gameplay/UI/registry reload testing; manual visual acceptance pending. Core 0.17.0 server/client and Forge 58.1.18 / Minecraft 1.21.8 required for the native enhancement.

## 0.5.22

- Integrated 117 exact geometry-only replacements (74 Near East/Caucasus, 43 Arabia), replacing two aggregates with schema-v2 per-area resources and preserving attribution/licenses. All 39 Macros, 1,549 Local IDs and metadata remain; 563 geography resources. Six prior European-stage Near East records, all 227 Africa files including the Nile correction, all 435 Water Features, progression/UI/economy and Core remain unchanged.
- Core 0.16.0 accepts the integrated catalogue: all 23,846 supplied resolutions match; 21,521 → 22,613 resolved land-grid points (+1,092, zero sampled losses), zero resolution errors. Africa regression: 87,932 fixtures / 85,138 protected matches plus two Nile controls; two Farasan island-interior controls pass. Target-Macro vector coverage 93.45791% → 99.27984%; tiny exact source sliver losses approximately 1.72 blocks² remain.
- No geometry corrections or discovery reset. Supplied Farasan named control is approximately 0.51 block offshore in the source mask and intentionally resolves none; use actual island land for manual acceptance. Residual mainland/parent/interface proposals remain deferred. No Minecraft terrain, reload or gameplay testing performed.

## 0.5.21

- Integrated 130 exact East Africa/Horn (72) and Southern Africa (58) geometry replacements as schema-v2 per-area resources, retiring two superseded aggregates and retaining source/license attribution. All 39 Macros, 1,549 Local IDs and metadata remain; 448 geography resources. Prior 97 North/West files including the approved Nile correction, all 435 Water Features, progression, NPC economy and Core remain unchanged.
- Core 0.16.0 accepts the integrated catalogue: 32,997 East/South fixtures, 54,935 North/West regression fixtures (53,692 protected matches), and two Nile controls; zero resolution errors. Against deployed 0.5.20, resolved land-grid points rise 29,902 → 30,991 (+1,089, zero losses). Target-Macro vector coverage 95.91166% → 99.65769%; tiny exact sliver losses total approximately 0.64 blocks². No geometry corrections, discovery reset or Minecraft gameplay testing; residual island/mainland proposals and eight legacy lake Local identities are retained for separate review.

## 0.5.20

- Refined geometry for 97 existing North Africa (42) and West/Central Africa (55) Locals, replacing two aggregates with schema-v2 per-area resources and retaining source/license attribution. All 39 Macros, 1,549 Local IDs and non-geometry metadata, 435 Water Features, progression UI/gameplay and NPC economy remain unchanged.
- User-approved Nile correction: restrict Delta to north of 30.1°N near Cairo (Z=-2054.826666666667); transfer its southern footprint to Nile Valley. Core-required numerical ring cleanup changes under 0.00001 blocks²; combined coverage is preserved within that tolerance. The other 95 candidate definitions remain exact.
- Core 0.16.0 accepts the integrated catalog: 54,935 resolution fixtures plus seven Nile controls, zero resolution errors; 52,667 old resolved land-grid points retained and 211 gained. Target-Macro vector coverage 99.25560% → 99.66537%; tiny source sliver losses total approximately 7.06 blocks² despite zero sampled grid losses. No Minecraft gameplay testing; remaining source/parent/island gaps and five legacy lake Local identities are retained, not redesigned.

## 0.5.19

- Added the Core 0.16.0 Civilization Progress presentation: 17 existing Era 0 shared-score milestones in three parallel branches, converging through three explicit prerequisites into WE CAN STAY, plus one permanently locked presentation-only Era 1 placeholder. All existing advancement definitions, gameplay functions, progression and other content remain byte-identical.
- Core schema/parser, 1,101 native assertions, 27 state scenarios, 17 missing-reference cases, icon resources, routed layout and Fit viewport checks pass. No Minecraft gameplay/UI testing; manual acceptance pending. Matching Core 0.16.0 required on server/clients.

## 0.5.18

- Added the 62 exact approved South America water Map Features (25 marine/estuary, 37 inland), 86 holes / 13,731 vertices, plus supplied source/license notices. Combined catalog 435; all 373 old water definitions, 225 Macro/Local files, profiles, progression and discovery data remain unchanged. Review-only Itaipu/Sobradinho/Jupia drafts are excluded; Drake/Scotia are shared global IDs for future Antarctica reuse. No geometry edits or engine changes.
- Structural checks pass 1,502 assertions, 85 independent land/island samples and nine overlap checks; released Core 0.15.3 accepts the complete catalog with 1,258 fixtures / 2,766 assertions. Retained source limitations: six coarse GSHHG lakes, variable/dry basins, and old Caribbean coverage of Maracaibo (new lake wins hover; broad-sea discovery may persist). No Forge reload or Minecraft gameplay/terrain/network testing; manual acceptance pending. Matching Core 0.15.3 required on server/clients.

## 0.5.17

- Added the 119 exact North America water Map Features (63 marine, 56 inland), 1,320 holes / 94,419 vertices, plus supplied source/license notices. Combined catalog 373 features; all 254 previous definitions, including completed Oceania and Japan, and all 225 Macro/Local files remain byte-identical. Shared Bering/Chukchi/Greenland/Denmark and Caribbean basin IDs stay unique. No profiles, NPC/economy, water-quality, progression or discovery-state changes.
- Structural checks pass 10,572 assertions, 1,195 independent land/island samples and 60 overlap pairs. Released Core 0.15.3 accepts all 373 definitions: 7,749 fixtures / 18,328 assertions, including shared multipart projection and prior antimeridian waters. No geometry edits, Core changes, Forge reload, network or Minecraft gameplay/terrain test. Manual acceptance remains pending; matching Core 0.15.3 required on server/clients.

## 0.5.16

- Added the 48 exact approved Oceania water Map Features (19 marine, 29 inland), 313 island holes / 21,443 vertices, plus supplied source/license notices. Combined catalog 254 features; all 206 previous files, all seven Japan definitions, all 225 Macro/Local files and unrelated gameplay/discovery state remain unchanged. Timor/Arafura are reused, Australian Lake Albert has a distinct ID, and Koro Sea/Lake Eyre retain their two-part geometry. Australian ephemeral salt-lake features represent named lake beds, not guaranteed wet terrain.
- Structural checks pass 3,710 assertions, 302 independent source-land samples and 17 overlap pairs. Released Core 0.15.3 accepts the full catalog: 3,017 fixtures / 6,730 behavior assertions, including multipart discovery and Fiji seam checks. No geometry corrections, Core changes, Forge reload or Minecraft gameplay/terrain/network test; manual acceptance remains pending. Matching Core 0.15.3 required on server and clients.

## 0.5.15

- Added the 93 exact approved Asia water Map Features (59 marine, 34 inland), 1,729 island holes / 92,275 vertices, plus supplied source/license notices. Combined catalog 206 features. Reconciled the older review baseline with integrated Africa 0.5.14; all 113 existing water files, including all seven Japan resources, remain byte-identical. Seto Inland Sea is a new ID; shared cross-continent IDs stay unique. No Macro/Local, water-quality, economy, progression or discovery-state changes.
- Schema/topology checks pass 15,842 assertions, 1,651 independent land/island samples and 91 overlap pairs. Released Core 0.15.3 accepts the full catalog: 12,194 fixtures / 28,041 assertions including antimeridian parts and shared multipart projection. All coordinates/metadata preserved exactly; no seam bridge, geometry repair, mod modification, Forge reload or gameplay/terrain/network test. Manual acceptance pending; matching Core 0.15.3 required on server/clients.

## 0.5.14

- Added the 33 exact approved Africa water Map Features (13 marine bodies, 20 lakes/reservoirs), 193 island holes / 20,557 vertices, plus supplied source/license notices. Combined catalog 113 features; all 80 Japan/Europe definitions and all 225 Macro/Local geography resources remain byte-identical. Shared Red Sea/Aden/Suez/Aqaba IDs are global; no river, ocean, canal, discovery reset or unrelated gameplay change.
- Verified installed/released Core 0.15.3 identity. Structural checks pass 3,207 assertions, 188 independent island samples and 11 overlap pairs; complete released-Core catalog passes 2,743 fixtures / 5,952 behavior assertions. No coordinate edits, Forge reload, network session or Minecraft terrain/gameplay test; manual acceptance pending. Server and participating clients require matching Core 0.15.3.

## 0.5.13

- Migrated exactly 190 existing European Local geometries to the approved schema-v2 resources, preserving all IDs/metadata/profiles/priorities/parents. Replaced five aggregates with 190 one-area files and two residual aggregates (87 unchanged records); final 39 Macros / 1,549 Locals / 225 resources. All 80 Water Features and unrelated gameplay/discovery data remain unchanged; no reset or new geography IDs.
- Requires Core 0.15.1 / geography-map protocol 3 on server and clients. Exact candidate hashes, schema/topology/budgets, metadata protection and full released-Core load pass: 52,366 coordinate checks, zero resolution errors; all 39,837 old resolved land-grid points retained, 18 gained. Same-domain dry-land coverage 83.56889% → 83.61541%; approved 0.593 block² sub-grid loss remains within the net improvement. Inherited northern/island/interface gaps remain deferred. No Forge reload or gameplay/terrain/UI test.

## 0.5.12

- Added all 73 exact approved Europe water Map Features, including 1,274 island holes and 121,066 vertices, plus required geography source/license notices. Combined catalog: 80 definitions; all seven Japan files/IDs remain byte-identical. No Macro/Local, discovery-history or gameplay changes; no migration/reset.
- Requires Core 0.14.2 on server and participating clients (expanded geometry limits despite unchanged protocol 2). Exact hashes/schema/topology/budgets and offline released-Core audit pass: 3,970 fixtures / 11,888 behavior assertions. No Forge reload or Minecraft gameplay/terrain/UI/network test; manual acceptance pending.

## 0.5.11

- Added the seven exact approved Japan water Map Features: six schema-v2 polygons, 292 island holes / 298 rings and the unchanged 110-point schema-v1 Shinano open polyline; 11,614 vertices total. Stable IDs, priorities and discovery radii are preserved; no geometry or source-gap correction, Macro/Local edits or gameplay changes. Requires Core 0.14.0 / feature protocol 2 on server and participating clients.
- Verified the manually installed 0.14.0 JAR and exact resource/provenance hashes. Supplied Python checks pass 21,038 assertions; released-Core content audit passes 896 fixtures / 2,672 assertions. Extra sweep confirms 14,158 corresponding-feature hole-boundary exclusions; documented 172 secondary-sea shoreline hits (56 on the supplied land proxy, at most 0.135 block inland), plus the unchanged ~0.393-block Shinano source gap. Terrain/rendered UI/gameplay remain manual checks.

## 0.5.10

- Added Amazonia/Orinoco/Guiana (39 locals), Brazilian Highlands/East (50), and Southern Plains/Patagonia (55): 144 additions, eight polygon-only Andes replacements, no removed IDs; final 1,549 locals / 39 technical macros / 38 resources. Kept all 1,405 old IDs, 1,397 old objects unchanged and the prior Mesoamerica repair. Added ten macros; changed only Andes main geometry and South America fallback priority 57 → 50.
- Under the standing Core-compatibility authorization, removed two duplicate consecutive Andes vertices and shifted two shared Amazonia/Andes clipping coordinates by 0.00000001 block, synchronizing three parent/base pairs. Recorded exact corrections and one more robust diagnostic witness in the geography note; no Core, profile, identity, discovery or unrelated gameplay changes.
- Integrated supplied checks pass: 165 stage controls, 514 prior named assertions, 15,920 normalized boundary probes and full reachability. Offline Core 0.13.0 accepts all 1,549 locals/39 macros, zero skipped: 2,114 deduplicated cases, 83,358 whole-pack boundary samples, 23,835 NPC compositions. All new macro footprints have zero polygon gaps; numerical union loss ~0.0000002164 blocks² is below 0.00001 tolerance. No Minecraft gameplay or terrain testing.

## 0.5.9

- Added Central/Eastern North America, Mesoamerica/Caribbean and Andes/Pacific West: 42 locals each, 126 additions total; final 1,405 locals / 29 technical macros / 28 resources. All 1,279 existing locals and 23 existing local files are preserved. Added three macros; applied approved Mesoamerica priority 60 and South America priority 57 with their final polygons; retained both broad fallback IDs.
- Applied the user-approved Core compatibility correction to Mesoamerica macro/base vertex 36: X -6949.546666493 -> -6949.54666648, Z unchanged. This ~0.000000013-block adjustment avoids Core’s near-segment self-intersection rejection; footprint difference is ~0.000001331 square blocks. No other payload-record deviations, Core changes or gameplay changes.
- Integrated handoff checks pass: 1,405 reachable locals, 64 final/351 earlier named controls, 13,380 trio boundary probes and zero regional gaps/global coverage loss. Offline Core 0.12.0 accepts all records with zero skipped: 1,820 control/witness checks, 75,048 whole-pack boundary samples and 21,525 NPC compositions. Geography/persistence regressions are structural only; no Minecraft gameplay or terrain testing.

## 0.5.8

- Added Arctic America/Greenland (36 locals), Boreal North America (45) and Western North America (42 across three technical parents). Final geography: 1,279 locals / 26 macros / 24 resources; all 1,156 existing locals and their 18 source files remain unchanged. Added five macros and modified only Northern Europe, Northern Asia and Mesoamerica polygons; retained old North America fallback. Juice 0.5.7 and all other gameplay/persistence remain intact.
- Removed only a consecutive duplicate vertex at [-10308.266667, -4324.693333] (second occurrence/index 93) from the new Boreal macro and matching base local for Core compatibility. Both footprints are mathematically identical; no other payload-record deviation.
- Supplied and integrated validators pass 4,424 assertions (11,070 original / 11,050 normalized boundary probes). Offline Core 0.11.0 accepts 26 macros/1,279 locals with zero skipped, 67,116 whole-pack boundary samples and 19,590 NPC compositions. Global union loses zero area; region footprints have no gaps. No Core changes, terrain edits, discovery reset or Minecraft gameplay testing; Svalbard/Big Diomede and older over-budget regions remain separate follow-ups.

## 0.5.7

- Migrated all seven existing juices to Core 0.10.0 schema v2 and added custom Carrot Juice using Pumpkin Juice effects. Bottle/full-canteen costs: apple/cactus/chorus 1/3, glow/sweet berry 2/8, melon/carrot 2/5, pumpkin 1/1; every juice keeps 1.0 HP healing. Stable IDs/native outputs/names and all canteen tiers remain intact; Core owns exact-unit/split-stack crafting.
- Retained all seven TAN sugar-free bottle overrides; corrected glow berry, melon and sweet berry to two ingredient entries (the other four already match). Verified the manually installed Core 0.10.0 release hash and offline eight-definition parser/catalog/recipe checks. No old schema-v1 or cheap bottle bypass remains; geography/HUD, economy, water/temperature, progression and technology locks are unchanged. No Core changes or Minecraft gameplay/reload testing.

## 0.5.6

- East / Northern Asia & Oceania v1: 1,156 locals / 21 technical macros (19 physical regions) / 19 resources. Added 366 local IDs, updated 11 existing polygons with four approved Central-to-Northern Asia parent changes; retained all 790 existing IDs and immutable fields. Five macro polygons updated and two separate dateline fragments added. Existing economy, progression, water/temperature, juices, NPC snapshots and discovery data remain untouched.
- Core 0.9.0 required eight lossless consecutive-vertex removals: altai_mountains, kulunda_plain__central_asia1, eastern_mongolian_steppe, northeast_china_uplands, hokkaido_highlands, transbaikal_highlands__east_asia1, daurian_steppe_basins__east_asia1, guangxi_karst_uplands__southeast_asia1. Identical mathematical footprints verified; no other payload deviations. Existing Iranian Plateau repair retained.
- Supplied checks passed; offline first-party Core parsing accepted all 21 macros/1,156 locals with zero skipped, 2,860 cases, 57,456 boundary samples and 17,670 NPC compositions. Macro union added 24,728,622.368036833 blocks²; numerical loss 0.00005954990349661237, within approved 0.001 tolerance. No Core changes or gameplay testing; terrain and 42 small-feature reviews remain manual.

## 0.5.5

- Asia Coverage v1: 790 locals / 19 macros / 16 resources; 240 additions, three existing polygon updates, no IDs removed. Six approved macro polygons revised; global coverage loses nothing and gains 49,938 blocks² around the Maldives. Existing economy, juices, progression and other gameplay remain unchanged.
- Removed one consecutive duplicate vertex from `iranian_plateau` required by Core validation; mathematical footprint, remaining coordinates and all non-polygon fields are identical. No other supplied-data deviation.
- Supplied checks and offline Core 0.9.0 checks pass: 1,876 cases, 40,800 boundary samples and 12,150 NPC compositions. Read-only stored terrain checks cover 216 controls and land in all 34 new island fragments. No gameplay testing, Core changes or discovery/NPC resets.

## 0.5.4

- Added seven Core schema-1 juice definitions (apple, cactus, chorus_fruit, glow_berry, melon, pumpkin, sweet_berry), each restoring 1.0 HP per successful drink. Uses Core’s generic six-tier, state-preserving canteen engine; no per-tier recipes/items, Agriculture/era gate or juice commodities were added.
- Overrode the seven exact TAN bottle recipe IDs with purified water + the corresponding ingredient, removing sugar while preserving native outputs/count/category/group. Native thirst/hydration/tags, water production, technology enforcement, geography/HUD and all other gameplay data remain unchanged.
- Verified completed and installed Core 0.9.0 with the exact published JAR hash. In-game reload acceptance and gameplay remain manual tests; no mod was installed by this datapack task. Offline catalog/schema/recipe and recipe-initialization checks passed; no gameplay or mod installation was performed.

## 0.5.3

- Integrated the exact approved Southern Africa, Near East/Caucasus and Arabian datasets: 550 locals, 184 additions, 12 existing-record updates, no deployed IDs removed. Regrouped 12 local-only files without duplicate snapshots; Greater Caucasus is refined under its stable ID and Hujrah remains `hujrah_plateau_near1`.
- Corrected only North Africa/Horn/Arabia macro polygons and 11 existing Red Sea records, including two new north-side split fragments. All macro priorities/profiles, 16 other macros, global coverage, Crete notch and prior Upper Nile gap remain protected. Existing NPC/discovery state, economy and unrelated gameplay are untouched.
- Verified installed/source Core 0.8.0 and exact schema 1. Supplied checks: 870 fixtures/29,088 local boundary samples; first-party offline checks: 29,892 local+macro boundary samples/8,550 compositions. Read stored Earth terrain at 320 control positions and island/coast/wadi envelopes; all new small islands are represented. No gameplay testing or Core changes.

## 0.5.2

- Africa Coverage v1: added 168 supplied complete-profile local records for North, West/Central and East Africa/Horn, including neighboring-macro fragments. Extended only the East Africa/Horn polygon to cover the approved 36,311-block² wedge; all 198 European locals and Crete’s macro notch remain unchanged.
- Applied three minimal rounding-edge clips (`eastern_sahara_horn1`, `northern_sahel_horn1`, `upper_nile_plains`) to eliminate actual Core parent conflicts; IDs, labels, priorities and profiles are preserved. Economy/families, progression, roles, skins/dialogues, water and temperature are unchanged.
- Offline Core 0.7.1 validation: 366 reachable locals, 5,790 compositions, 28,236 boundary samples and 175 control checks. Read existing Earth 1500 block states, including small islands; the configured test save differs at 11 sampled controls. No Core changes, terrain writes or Minecraft gameplay testing.

## 0.5.1

- Europe Coverage v2: completed Northern 27 and full six-axis profiles; merged 17 duplicate stitch proposals, retained Pripyat Marshes/Azov Coast and all 55 old IDs. 150 physical areas become 198 reachable simple-polygon records under the actual winning macros, including 14 Caucasus/Central Asia boundary fragments.
- Extended only the Mediterranean macro around the actual saved Crete landmass. Corrected coarse coverage/overlaps and removed unsupported marine slivers; Northern contours/profiles are authored geographic simplifications. Existing NPC snapshots, economic content/Leaves, progression and all other gameplay are unchanged.
- Validated offline with unchanged Core 0.7.1 parsers/resolver: 3,270 compositions, 198 reachability witnesses, 17,512 boundary samples and 53 reference points. Read-only saved-terrain checks; seven witnesses remain reference-only. No Minecraft gameplay testing.

## 0.4.1

- Replaced the generic mushroom family commodity with independent Brown/Red Mushroom outputs: value 4, Limited, maxUses 2 each, Forager seller, unrestricted geography. Removed the now-unused production mushroom family; Traveller metadata remains omitted under the existing role contract.
- Verified compatibility with installed Core 0.7.1; no schema migration or datapack recreation of diversity/safe-wandering mechanics. Other families, commodity values/bindings/stock, profiles/Leaves, archetypes and protected gameplay remain unchanged. No gameplay testing or Core JAR changes.

## 0.4.0

- Integrated the approved 49 Era 0 commodities, five native payment families and complete profiles on all 19 macros/55 European local areas using Core 0.7.0. Profiles filter selling only; approved values/stock remain unchanged.
- Hunter/Fisher/Forager now use Core-generated offers, native XP, look controls and bounded 10-block wandering; removed their obsolete static offer arrays. Regional leaves use family context rules derived from the approved profiles; no custom pricing/restock logic.
- Traveller bindings are omitted because Core requires archetype-backed roles; mandatory input-only maxUses is inert 1. Family resource IDs use lowercase. Static/story content, polygons/priorities, progression, locks, Traveller, fishing, water and temperature are unchanged. No Core changes or gameplay testing.

## 0.3.18

- Added Europe Local Areas v1: 55 uniquely prioritized areas in four resources (Northern 16, Western/Central 13, Eastern 9, Mediterranean 17). Local display labels prefix new NPC names automatically; no archetype trade/skin overrides or era labels.
- All supplied shapes pass the geometry validator. Corrected parent-boundary conflicts only: clipped eastern_baltic_coast and danish_lowlands to Z <= -3755; clipped eastern_carpathians, dniester_basin, pripyat_marshes and pontic_steppe to X >= 1708, a one-block inset from the Western/Central-owned edge. Macro polygons/priorities remain byte-identical.
- Structurally validated geography, names and 1,125 context compositions within Core 0.6.1 limits. Economy/Max Uses, Traveller, fishing, progression/rollback, roles, locks, water and temperature are unchanged; Core is untouched and no gameplay testing was performed.

## 0.3.17

- Added the supplied 19 broad Overworld NPC macro-region polygons and technical priorities; no local areas, regional names, skins or trade overrides. Documented the calibrated Earth conversion X = round(longitude × 1024/15), Z = round(-latitude × 1024/15).
- Structurally checked the 15 supplied calibration points, polygon geometry and boundary precedence. Existing archetypes/economy, progression/rollback, roles, water, temperature and fishing are unchanged; Core is untouched and gameplay testing remains manual.

## 0.3.16

- Integrated Core 0.6.1 read-only era context and Era 0 Hunter/Fisher/Forager archetypes with fixed placeholder skins, shared scarce stock and no timed resets; geography/season overrides stay empty.
- New Wandering Traders select six distinct offers from 27 candidates: 3 common + 2 rare + a common bonus (95%) or exceptional gold/specific potion bonus (5%). Existing processed traders are unchanged; technology gates remain authoritative.
- Added Core fishing timing policy (20–60 s, Lure 5 s/level) and vanilla 1.21.8 root fishing weights Fish 255 / Junk 30 / Treasure 5, preserving predicates, quality and child tables. Progression/rollback, roles, water and temperature are unchanged; no Core changes or gameplay testing.

## 0.3.15

- WE CAN STAY remains a late Era 0 milestone and no longer starts Era 1 or unlocks Agriculture; unfinished Era 1 root is displayless and impossible.
- Manual migration: `/function human_journey:admin/return_to_era0` resets only #era1/#tech_agriculture, revokes the Era 1 root and reapplies existing Student locks; all Era 0/story/role/NPC state is preserved. Offline root completion clears on next join. No Core changes or gameplay testing.

## 0.3.14

- Adopted installed Core 0.4.0 water schema: NORMAL 75% Thirst / 30 s; retained DIRTY 100% Thirst / 30 s and Poison I / 8 s. SALT restores no thirst/hydration, adds 12 exhaustion and Nausea I / 8 s, with no Poison or manual treatment route.
- Added ocean-tag classification and the supplied Caspian polygon; enabled instant purifier filling from adjacent NORMAL/DIRTY source water for 100 native filter units. Temperature, manual treatment timing, Rain Collector, progression/technology gates, Water for Life and role recipe initialization are unchanged. No Core changes or gameplay testing.

## 0.3.13

- Migrated water treatment to installed Core 0.3.0 policy: normal 60% Thirst / 30 s; dirty 100% Thirst / 30 s plus Poison I / 8 s; consume one sand/charcoal for DIRTY→NORMAL, then furnace/campfire NORMAL→PURIFIED in 500 ticks. Removed 15 legacy recipes/grants and disabled blanket potion conversion while preserving its function ID.
- Water for Life now accepts purified bottles and all purified canteens, preserving milestone IDs/prerequisites. Temperature policy, Student/Teacher recipe setup, technology gates, Rain Collector and Water Purifier mechanics remain unchanged; no mod JAR changes.

## 0.3.12

- Added Core 0.2.0 datapack temperature policy: extra exhaustion/sec WARM 0.5, Overworld HOT 2.0, Nether HOT 3.0; preserved established water relief 20 / cooling interval 10.
- Enabled Overworld heat damage at 1 HP every 160 ticks after TAN buildup; retained Nether 1 HP / 40 ticks and enabled Nether-only Climate Clemency removal. Cold, progression, roles, recipes and NPC logic are unchanged; Core JAR/configs are untouched.

## 0.3.11

- Student setup now grants the full recipe set and immediately reapplies the existing civilization technology restrictions; existing Students receive a one-time repair on their next tick, including after an offline login. Teacher behavior and all progression IDs remain unchanged.
- Human Journey Core 0.1.0 development is separate from this datapack; no mod binaries or Easy NPC stock workaround are included.

## 0.3.10

- Added six editable Era 0 Wandering Trader barter offers, once-only initialization and a synchronous first-interaction guard; natural spawning remains enabled. Existing #era1 progression controls availability; new traders outside Era 0 have exhausted offers. Easy NPC/Jengo and existing progression are unchanged.
- Offers use the installed Java 1.21.8 format. If a trader is first opened between spawn and the next tick, the guard replaces server offers before a trade action; close/reopen refreshes the rare stale first screen. Processed traders retain stock/uses across reloads.
- Temperature configuration was not changed: its directory is outside current writable permissions. Requested manual edits in .minecraft/config/toughasnails/temperature.toml: immersion.wet_temperature_change=-2 and general.player_temperature_change_delay=50; all other settings stay unchanged.

## 0.3.9
- Reworked bottle purification: charcoal filters dirty water, then purifies normal water; normal bottled water remains boilable.
- Added component-preserving charcoal purification for all six canteen materials; no canteen furnace route.
- Preserved progression/role restrictions. Broad drinkable-potion matching was explicitly accepted for this test.

## 0.3.8
- Imported the supplied datapack as current source, preserving gameplay and progress identifiers.
- Archived all nine original supplied versions unchanged; retained useful original setup notes.

## Historical imports
- Original archives: 0.1, 0.2, 0.3.1, 0.3.2, 0.3.3, 0.3.4, 0.3.5, 0.3.6, 0.3.8. No 0.3.7 archive was supplied.
