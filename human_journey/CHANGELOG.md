# Human Journey changelog

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
