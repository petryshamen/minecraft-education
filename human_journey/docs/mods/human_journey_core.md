<!-- hj-mod-inspection {"filename":"human_journey_core-0.15.3.jar","sha256":"dd5c6387f292bb4628ea3ad9a6dedd6a1f3f58bf0a531b0bbbad56457be637a9","mods":[{"mod_id":"human_journey_core","name":"Human Journey Core","version":"0.15.3","declared_version":"0.15.3"}]} -->
# Human Journey Core

Inspected installed `human_journey_core-0.15.3.jar`, Core **0.15.3**, SHA-256 `dd5c6387f292bb4628ea3ad9a6dedd6a1f3f58bf0a531b0bbbad56457be637a9`. Target: Minecraft 1.21.8, Forge [58.1.18,59), TAN 21.8.0.2, GlitchCore [21.8.0.0,21.9). Mod installation remains manual. Datapack 0.5.14 adds only approved Africa water Map Features; existing land geography, economy, temperature, water-treatment/quality and progression policies remain unchanged. Server and participating clients need matching Core 0.15.3. Earlier integration sections below record their historical releases.

Core is the engine/API; this datapack owns gameplay balance. The Core loader reads exactly `data/human_journey/core/temperature.json` (source: `source/data/human_journey/core/temperature.json`). Use `schema_version: 1`; unknown fields, wrong types, non-finite/out-of-range numbers reject the entire update.

| JSON field | Meaning / range |
| --- | --- |
| `thirst.warm_exhaustion_per_second` | Extra TAN exhaustion/sec at WARM in Overworld/Nether; 0–20 |
| `thirst.hot_exhaustion_per_second` | Extra exhaustion/sec at Overworld HOT; 0–20 |
| `thirst.nether_hot_exhaustion_per_second` | Independent Nether HOT rate; 0–20; inherits supplied HOT if omitted |
| `overworld.hyperthermia_damage`, `nether.hyperthermia_damage` | Enable each dimension's direct heat damage; boolean |
| `overworld.heat_damage_interval_ticks`, `nether.heat_damage_interval_ticks` | Each dimension's heat pulse interval; integer 20–72000 |
| `overworld.heat_damage_amount`, `nether.heat_damage_amount` | Health points per pulse; 0–20 (1 HP = half a heart) |
| `nether.remove_climate_clemency` | Remove actual TAN effect each active Nether Survival/Adventure tick; boolean |
| `water.heat_relief_ticks_per_tick` | Net exposure ticks removed/tick in Overworld water; integer 0–140; zero disables Core water adjustments |
| `water.cooling_interval_ticks` | Body-temperature step interval toward neutral; integer 1–200 |

Rates assume 20 ticks/sec and ADD to TAN's normal exhaustion through `IThirst.addExhaustion`; they do not directly subtract thirst icons. Hydration buffers visible thirst loss. Core adds none at NEUTRAL/COLD/ICY. Survival/Adventure Teacher and Student use the same mechanics; Creative/Spectator are exempt.

Heat pulses require TAN's fully-hyperthermic state, preserving its normal grace/buildup. Core suppresses native TAN heat pulses in Overworld/Nether and admits its scheduled pulses, avoiding double damage. Scheduling follows `player.tickCount % interval`, so the first hit after grace need not wait a complete interval. Normal protection/hurt immunity still applies. Other damage types, including cold/freezing and dehydration, are untouched.

Water relief **20** and cooling interval **10** preserve the Test World's existing TOML values: rapid HOT → WARM → NEUTRAL, without forcing cold players warmer or forcing below neutral. TAN remains free to cool further. Core water protection affects heat damage only.

Nether Clemency removal leaves Overworld Clemency enabled, but does not restore an effect already removed in the Nether. It does not force HOT; actual temperature still comes from TAN.

Run `/reload`, then admin `/hjcore status` or `/hjcore status <player>` (permission level 2). Confirm policy source is the Human Journey ZIP, effective values, revision and `last reload=OK`; output also includes dimension, actual/target TAN stage, thirst, hydration, exhaustion, hyperthermia exposure/threshold and Clemency. Ordinary /reload success alone does not prove this policy was accepted.

The highest-priority pack providing this exact path wins as one file. Missing fields inherit `<world>/serverconfig/human_journey_core-server.toml` or Core compatibility defaults. Removing/disabling the file restores fallback on reload. Invalid data retains the last valid policy (fallback on first load), with rejection visible in status/logs. No duplicate scoreboard, mod update or manual student TAN-config edit is needed.

## Water API (extended schema 1)

Policy: `data/human_journey/core/water.json`. Temperature remains at `data/human_journey/core/temperature.json`, byte-for-byte unchanged by this migration. [Exact Core water reference](https://github.com/petryshamen/minecraft-education/blob/80c3d4a7ae3c1053773aeb4e8e643f53101f24a3/human_journey_core/WATER.md).

Core owns natural filling/hand drinking, persistent container quality, drinking consequences, state-preserving treatment, purifier intake/filling/filter-capacity transactions and bypass guards. The datapack owns risks, effects, timings, filters, geographic regions and purifier cost/allowed sources.

| Water JSON section | Human Journey policy |
| --- | --- |
| `normal` | `unsafe_chance: 0.75`, `thirst_duration_ticks: 600` (30 s TAN Thirst); no Poison/Nausea |
| `dirty` | `unsafe_chance: 1.0`, `thirst_duration_ticks: 600`; `illness: {enabled: true, effect: "minecraft:poison", duration_ticks: 160, amplifier: 0}` |
| `salt` | `thirst_restored: 0`, `hydration_restored: 0`, `additional_exhaustion: 12`; `nausea: {enabled: true, duration_ticks: 160, amplifier: 0}`; no Poison/unsafe-water roll |
| `filtration` | Enabled; `materials: ["minecraft:sand","minecraft:charcoal"]`, `material_count: 1`, `from: "dirty"`, `to: "normal"` |
| `boiling` | `furnace_enabled: true`, `campfire_enabled: true`, `furnace_ticks: 500`, `campfire_ticks: 500`, `from: "normal"`, `to: "purified"` |
| `hand_drinking` | `cooldown_ticks: 60` |
| `classification` | `salt_biome_tags: ["minecraft:is_ocean"]`; ordered `regions` list |
| `purifier` | `enabled: true`, `filter_cost_per_fill: 100`, `allowed_source_types: ["normal","dirty"]` |

PURIFIED adds no Thirst/Poison/Nausea and retains TAN restoration; it does not cure pre-existing effects. NORMAL, DIRTY and SALT rules apply consistently to bottles, canteens and direct drinking. Salt restoration values are actual bar/hydration units; exhaustion is added per drink, not per second.

Classification applies only where water fluid exists, using the water block's dimension and X/Z center. Priority is first matching region → TAN purified tag → TAN dirty tag → configured ocean tag → NORMAL. TAN swamp, mangrove_swamp and mushroom_fields remain DIRTY outside explicit overrides; vanilla ocean tag nesting covers deep oceans. No TAN/Minecraft biome tags are replaced.

Region `caspian_sea` uses `dimension: "minecraft:overworld"`, `water_type: "salt"`, and polygon vertices in perimeter order: Baku [3404,-2758] → Fort-Shevchenko [3433,-3041] → Ural mouth [3537,-3208] → Mahmudabad [3564,-2497]. This is the supplied coarse test boundary, not a surveyed shoreline. Polygon boundaries count as inside; append more ordered region objects later. Core accepts simple concave polygons, rejects self-intersections/duplicate vertices/zero area, and permits at most 128 regions with 3–256 vertices and coordinates within ±30,000,000.

SALT reuses the normal bottle/TAN canteen item with `minecraft:custom_data` key `human_journey:water_type = "salt"`; Core preserves the marker across moves/saves and displays a warning. Do not infer quality solely from the base item ID. Emptying/refilling replaces contents normally; treatment never refills a partly used canteen.

Filter: DIRTY container in main hand, sand/charcoal in offhand, sneak-right-click; consumes one material in Survival/Adventure. Boil NORMAL in an ordinary fueled furnace or lit campfire for 500 ticks / 25 s. Core preserves remaining drinks, names, enchantments and component patches. No recipe-book unlock is required. SALT cannot filter, boil or enter any purifier route; DIRTY cannot boil directly. No charcoal crafting shortcut is supplied. Legacy recipes/grants remain absent; `human_journey:water/convert_one` stays a no-op compatibility ID.

Purifier: empty glass bottle/empty same-tier TAN canteen fills immediately to PURIFIED by interaction; GUI/hopper processing fills up to one container per server tick. Intake needs a free-standing source-water block on any of six adjacent faces, classified NORMAL or DIRTY. Flowing/waterlogged water, cauldrons and Rain Collectors are not intakes. Any adjacent salt source blocks processing even if fresh water is also adjacent. Filled containers are not inputs. TAN's native replaceable-filter capacity supplies cost 100 per container: e.g. charcoal 1600 units gives 16 fills. The machine does not break; idle/failed transactions consume no capacity. No desalination is added.

Rain Collector remains native TAN and PURIFIED. Copper still gates Water Purifier/copper canteen; leather canteen/Rain Collector/manual treatment are Era 0; later tiers retain existing locks. Student grant-all-then-enforce and Teacher recipes are unchanged. Water for Life keeps its IDs/prerequisites and accepts purified bottles/canteens; Core salt fills use normal base items and cannot satisfy that predicate.

Admin `/hjcore water` reports held quality/fill, targeted natural-water classification, region/tag settings, purifier cost/capacity and water policy source/revision/reload result. `/hjcore status [player]` retains temperature/thirst diagnostics. Both are read-only. After `/reload`, verify water acceptance independently of temperature. Highest-priority exact water file wins; invalid fields/types/ranges/IDs/geometry retain the last valid snapshot; missing file restores Core defaults. Defaults have no geographic regions. This integrated release needs no separate companion water pack.

## NPC context and fishing (Core 0.6.1)

[Accepted schemas/examples and runtime source](https://github.com/petryshamen/minecraft-education/tree/10b91a3bce94a5b1347c14cd2530a06bc2b45bd7/human_journey_core) match the installed release hash. Core owns atomic loading/composition, read-only context resolution, Easy NPC 6.5.2 spawning/identity/actions/trades and fishing wait initialization. Datapack content owns roles, availability, dialogue, prices, stock, skins and timing values.

- `data/human_journey/core/npc_context.json`: `schema_version: 1`, `eras[].conditions[]` reads `objective: hj_civ`, `holder: #era1`, `operator: EQUALS`, `value: 0/1` → `era_0/era_1`. No score creation/write or fallback era. Missing/ambiguous state fails explicitly.
- `data/human_journey/npc_archetype/{hunter,fisher,forager}.json`: `role_label`, `base` content, `eras.era_0` only. Empty `macro_regions`, `local_areas` and `seasons` maps; NPC macro definitions now live in `data/human_journey/npc_geography/global_macros.json`, with 55 European local areas in four regional geography files. No Era 1 roles. Fisher logs/boat and Forager leaves use oak IDs because this Core item schema accepts exact items, not tags.
- Shared native stock: `trading.reset_minutes: 0`, per-offer `uses: 0` and `maxUses: 1/2/3`. Accepted reload stock behavior remains Easy NPC's; no custom restock/persistence layer. Context/content is a spawn-time snapshot; existing NPCs are never rewritten on reload.
- All three use the same pinned public Easy NPC humanoid template PNG temporarily. Commands (permission 2): `/hj npc context`, `/hj npc status`, `/hj npc spawn hunter|fisher|forager`. Economic archetypes do not accept custom names. Inspect rejection diagnostics after `/reload`; static schema validation does not prove live loading/UI/rendering.
- `data/human_journey/core/fishing.json`: `schema_version: 1`, `bite_time.minimum_seconds: 20`, `maximum_seconds: 60`, `lure_reduction_seconds_per_level: 5`. Core owns timing; this is the pre-approach wait, not a guaranteed wall-clock bite time. Weather/sky, later approach and TPS remain vanilla. `/hjcore status` reports source/reload/effective policy.
- Datapack separately overrides only vanilla root `minecraft:gameplay/fishing` weights (Fish 255/Junk 30/Treasure 5). Original quality values, open-water condition, rolls/random sequence and all fish/junk/treasure child tables remain native. Luck remains effective, with its original quality values against the new base weights.

Traveller remains a native Wandering Trader, independent of Core archetypes. Candidate offers live in `function/trader/wandering/pools/era0.mcfunction`; six distinct selections are sampled without replacement (3 common, 2 rare, then common 95% / exceptional 5%). Gold nugget and three specific potions each occupy one exceptional entry (about 1.25% of new Travellers each). Rare/exceptional Max Uses is 1. The once-only tag/interaction guard is retained; existing processed traders retain their old offers. `hj_wt` holds scratch sampling scores only and never controls progression.

Installed vanilla 1.21.8 codec bytecode was inspected as data: MerchantOffer `buy/buyB` use ItemCost (`id/count/components` exact component predicate); `sell` uses ItemStack (`id/count/components` patch). Potion outputs explicitly set `minecraft:potion_contents: {potion: minecraft:healing|night_vision|water_breathing}`; the horn sets `minecraft:instrument: minecraft:ponder_goat_horn`. Water payments use the registered separate `toughasnails:purified_water_bottle` item, never a generic potion cost. Salt/normal/dirty containers do not match it. These are native codecs, not inventory-rewriting workarounds.

NPC geography calibration and macro IDs/priorities: [Human Journey geography](human_journey_geography.md). Macro context alone never prefixes visible NPC names; only local-area labels do that (Europe Local Areas v1 now supplies 55).

## Production dynamic economy (0.4.0 content / Core 0.7.0)

Required installed Core: `human_journey_core-0.7.1.jar`, SHA-256 `90754af47ea97fe20c2d3af4e193046d28dc1ba712f8de1c82f7b53232fbc0ef`. The 0.6.1 sections above retain the earlier water/temperature/NPC integration history; those mechanics remain unchanged. Production economy resources require 0.7.0 on server and clients. See [the economy content note](human_journey_economy.md).

Core 0.7.1 is schema-compatible with 0.7.0. Payment candidate diversity, per-NPC buyer-use preference, spawn-only random choices and safer fluid/drop/path handling are engine behaviour; the datapack adds no equivalent functions/settings. Existing NPC offers do not reroll. Content 0.4.1 splits mushrooms into two item commodities and removes the unused production mushroom family; see the economy note.

## Geography HUD/shared discovery — Core 0.8.0

`npc_geography/*.json` keeps schema 1 and complete replace-only profiles. Optional macro `display_label`/`era_labels` are now accepted but this production release does not add them. Core supplies dynamic HUD context and readable macro-ID fallbacks; NPC labels remain local-prefixed spawn-time snapshots. Current geography content/filenames and migration are in [the geography note](human_journey_geography.md).

Participant HUD requires `hj_participant`. Natural shared discovery requires participant + `hj_student` + not `hj_teacher`, with teacher exclusion winning conflicting tags. Stable IDs are stored in the shared Overworld `human_journey_geography` SavedData. Reparenting does not erase historical discoveries; no datapack reset or per-player duplicate scoreboard is added. `/hj geography status` is read-only and permission-level 2; do not reset/mark discoveries just to force manual acceptance. Matching Core 0.8.0 is required on server and clients. Live reload, HUD, notifications and persistent-save gameplay remain user tests.

## Era 0 juices — datapack 0.5.7, requires Core 0.10.0

The exact completed **Core 0.10.0** contract was reviewed in [JUICE.md](https://github.com/petryshamen/minecraft-education/blob/96a168f575531b718327d88de7943e9be12ded6c/human_journey_core/JUICE.md). The user manually installed the test JAR; metadata and SHA-256 match the published release (`ca7324d31ff86d377dddddaf8b57a941438e26169ff1fa4ef404a4a3d6fa40be`). Server/clients need matching Core 0.10.0 / TAN 21.8.0.2 / Forge 58.1.18 / Minecraft 1.21.8. This datapack task did not modify, build or install a mod.

Eight definitions at **`data/human_journey/core/juice/<id>.json`** use **schema 2**: `schema_version`, `id`, `ingredient` (exactly item OR tag), `bottle_ingredient_count`, `canteen_ingredient_count`, `bottle`, `canteen_display_name`, `heal_per_drink`. Counts are independent integers 1–64 actual units. Native `bottle` is `{type: native, item: <unchanged TAN juice ID>}`. Custom `bottle` is `{type: custom, display_name, effect_source_item}`. Old `bottle_item` fields are removed; no duplicate v1 production catalog remains. IDs/ingredients/canteen names and native outputs are retained. All juices heal **1.0 HP (half a heart)** per completed drink, capped by missing health.

| ID | Ingredient | Bottle / full canteen units | Bottle output / effect source |
| --- | --- | --- | --- |
| `apple` | `minecraft:apple` | 1 / 3 | `toughasnails:apple_juice` |
| `cactus` | `minecraft:cactus` | 1 / 3 | `toughasnails:cactus_juice` |
| `chorus_fruit` | `minecraft:chorus_fruit` | 1 / 3 | `toughasnails:chorus_fruit_juice` |
| `glow_berry` | `minecraft:glow_berries` | 2 / 8 | `toughasnails:glow_berry_juice` |
| `melon` | `minecraft:melon_slice` | 2 / 5 | `toughasnails:melon_juice` |
| `pumpkin` | `minecraft:pumpkin` | 1 / 1 | `toughasnails:pumpkin_juice` |
| `sweet_berry` | `minecraft:sweet_berries` | 2 / 8 | `toughasnails:sweet_berry_juice` |
| `carrot` | `minecraft:carrot` | 2 / 5 | custom; `toughasnails:pumpkin_juice` effect source |

Carrot uses the registered shared **`human_journey_core:juice_bottle`**, named **Carrot Juice**, and **Carrot Juice Canteen**. Pumpkin Juice supplies native thirst/hydration/eligibility/effects for both forms; no copied effect constants, separate Java flavour item, texture, agriculture gate or NPC commodity is added. Ingredients alone control availability; all definitions exist in Era 0, including Chorus Fruit.

Core ships the special/server-authoritative recipes **`data/human_journey/recipe/juice_bottle.json`** (`human_journey_core:juice_bottle`) and **`juice_canteen.json`** (`human_journey_core:juice_canteen`). The datapack does not duplicate them. They require one purified TAN bottle or one completely full purified TAN canteen plus the configured units. Core supports single/split/excess matching stacks, exact consumption and normal/shift-click takes through vanilla/Forge ResultSlot; automated crafting which bypasses that path requires its own adapter. The canteen price is identical across leather/copper/iron/gold/diamond/netherite, with no slot-layout rule or tier multiplier. Partial/normal/dirty/salt/empty/already-juiced carriers are rejected by Core.

All seven native `data/toughasnails/recipe/<id>_juice.json` overrides remain to suppress TAN's sugar recipes. Glow berry/melon/sweet berry now repeat the ingredient twice in vanilla shapeless JSON; the four existing one-unit recipes already match. IDs, result/count, category and group remain unchanged. These fixed vanilla recipes require separate ingredient entries/slots; the generic Core route handles a single stack containing multiple units. Carrot has no fixed native override. No cheap berry/melon route remains.

Core preserves canteen tier/components/names, the `human_journey:juice` marker inside `minecraft:custom_data`, native TAN drinking and glass/empty-tier returns, one use per survival sip, persistence and completed-drink healing. Existing juice IDs remain valid on held/saved items; costs affect new crafting only. Student initialization remains `recipe give @s *` immediately followed by technology enforcement; Teacher remains unrestricted. Existing recipe IDs are preserved; Core's special recipes need no new unlock advancement. No water/temperature, geography/HUD, economy, food recipes or progression files change.

Offline checks use the hash-verified Core 0.10.0 strict parser/catalog/store: eight accepted v2 definitions, seven native outputs plus custom carrot, unique IDs/ingredients, exact costs/healing/source and seven valid overrides. Item references are checked against inspected vanilla/TAN resources and the published native-registration evidence; the local running registry/reload is not exercised. No mod JAR is executed or used as a classpath. Actual Minecraft reload and gameplay remain manual: `/reload`, `/hjcore juice list` (eight), `/hjcore juice show carrot` and other IDs; inspect source/revision/error. Test insufficient/exact/excess costs, stacked/split ingredients and shift-click, named/component-bearing low/high-tier canteens, one sip/final empty, interrupted drinking, half-heart healing/native source effects, save/restart persistence and ordinary water regressions as Student and Teacher.

## Japan water Map Features v2 — datapack 0.5.11 / Core 0.14.0

Exact contract: [Core 0.14.0 geometry](https://github.com/petryshamen/minecraft-education/blob/f4f28dd88819232a21e2991895a51d89dfda9228/human_journey_core/MAP-GEOMETRY.md), [Map Features](https://github.com/petryshamen/minecraft-education/blob/f4f28dd88819232a21e2991895a51d89dfda9228/human_journey_core/MAP-FEATURES.md). The user manually installed the matching 0.14.0 JAR during preparation; its SHA-256 is `39ac922532501161bdbcbd614aeb8e8a76bb6fe88d08f3010e2dc71378b1067c`. Server and participating teacher/student clients need matching **feature protocol 2**. Optional Xaero adapter is **World Map 1.39.13**. This task does not install/rebuild Core or change map settings.

One definition per file at **`data/human_journey/core/map_feature/<id>.json`**. Body IDs are authoritative and duplicate effective IDs reject the catalog. The seven resources are byte-for-byte copies of the approved candidate; no combined arrays, audit/source/preview/schema files or dependencies are packaged. This is the first production catalog, not a claimed deployed v1 migration. No conflicting feature resources were found in the configured test packs or installed mod resources.

| ID | Schema | Holes | Vertices | Priority | Discovery radius |
| --- | ---: | ---: | ---: | ---: | ---: |
| `sea_of_japan` | 2 | 105 | 4091 | 50 | 8 |
| `east_china_sea` | 2 | 93 | 4086 | 51 | 8 |
| `korea_strait` | 2 | 91 | 2646 | 60 | 8 |
| `tsugaru_strait` | 2 | 0 | 77 | 61 | 8 |
| `tokyo_bay` | 2 | 0 | 95 | 62 | 8 |
| `lake_biwa` | 2 | 3 | 509 | 63 | 3 |
| `shinano_river` | 1 | 0 | 110 | 64 | 3 |

Totals: **seven definitions, six v2 polygons/parts, one v1 open polyline, zero multipolygons, 292 holes, 298 polygon rings, 11,614 vertices**. Largest feature is 4,091 vertices, within the 4,096-vertex/128-ring per-feature budget. Core owns validation, hover/priority, open-line hits, boundary-distance discovery, transport, SavedData and rendering. Content owns geometry, labels/types, priorities and radii. Map Features are independent of Macro/Local NPC/economy profiles and water potability. All existing geography/gameplay source is byte-for-byte unchanged apart from release metadata/load banner.

Hole interiors **and edges** exclude that feature’s containment; discovery may still occur within the configured radius of an island/outer coast. Direct river hits keep Core precedence and its existing five-GUI-pixel hit target. Test fixture tolerances are not new settings. Discovery remains participant + student + NOT teacher; teachers receive shared knowledge but do not create it by travel. Known IDs survive geometry replacement/reintroduction; neither `human_journey_map_features.dat` nor `human_journey_geography.dat` is reset or edited. Existing NPC snapshots remain intact.

Validation: all 54 manifest entries, source-input hashes and seven candidate hashes verified; supplied schema matches the pinned Core schema. Exact Shapely 2.2.0/jsonschema 4.26.0 checks pass **21,038 assertions / 896 fixtures**, with **zero foreign-water fallback at all 292 island interior witnesses**. The supplied unchanged `CoreDataAudit.java` / `validate_core.ps1` run against the hash-pinned **released artifact outside mods**, Gson 2.11.0 and JDK 21: **896 fixtures / 2,672 behavior assertions**, complete seven-feature catalog accepted. No installed JAR is executed; no Forge startup/gameplay is performed. An additional content sweep passes **14,158 own-feature hole-vertex/midpoint exclusions and proximity checks**.

**Extra review finding:** a stronger, global all-water shoreline sweep finds **172 secondary-sea selections** on hole-edge probes across ten hole rings. Most are outside the supplied land proxy; **56 lie on that proxy, maximum 0.134936534 block inland**. The corresponding excluded feature is absent, but a differently generalized overlapping sea can still appear. This is a documented sub-block coast limitation, not a claim that every boundary of every island is excluded from every other sea. The four named critical controls (Sado, Oki Dogo, Tsushima, Okishima), supplied overlap tests and all 292 interior witnesses pass. The approved files/hashes remain exact; no silent geographic correction is made. Small omitted islets and proxy/simplification limitations remain those of the handoff.

**Shinano limitation:** its exact 110-point v1 open path is retained. The mapped endpoint is approximately **0.393 blocks landward** of the refined source sea outline, with no literal line/polygon overlap. Do not extend the river to hide the gap. Preview/source proxy evidence is not saved Minecraft terrain evidence.

Manual acceptance is pending: `/reload`, `/hj npc status`, `/hj geography status`, `/hj mapfeature status` (feature diagnostics contract), then inspect actual logs/revision/catalog and Hover/All/Off with known features. Safe-Y X/Z controls: Sado **9447.424,-2597.547**; Oki Dogo **9096.533,-2473.301**; Tsushima **8828.245,-2349.739**; Okishima **9288.567,-2403.328**. Corresponding water hover must be absent on the islands/hole edges; coast discovery may still occur. Compare Korea Strait water **8806.400,-2355.200**, Tsugaru **9605.120,-2834.432**, Tokyo Bay **9545.728,-2423.467**, Biwa **9287.680,-2406.400**. Inspect Shinano endpoint **9493.641,-2590.560** and sea approach **9493.386,-2590.981** at multiple zooms. Verify teacher exclusion/student discovery once, same-ID known-state retention, reconnect and separate outer/hole rendering without bridge lines. Do not reset production knowledge to force a test. Existing NPC/trades/juices/water/temperature/geography need only a normal smoke test. Terrain, rendered UI, networking and actual save/reload/gameplay remain user checks; no live-server deployment.

## Europe water Map Features v1 — datapack 0.5.12 / Core 0.14.2

Adds 73 exact schema-v2 polygon resources under `data/human_journey/core/map_feature/`: 22 seas, 18 straits/channels, 5 gulfs, 3 bays and 25 lakes, with 1,274 holes / 121,066 vertices. Seven Japan resources remain byte-identical: combined 80 definitions, 79 polygons, one open river, 1,566 holes / 132,680 vertices. No new rivers; Saimaa, Päijänne and Little Belt are deliberately withheld. Constance means Obersee, Peipus means its northern basin, Prespa means Great Prespa. Required source/license notices ship at the pack root.

**Server and every participating client require Core 0.14.2**, SHA-256 `89228db1e711913b99903de897821c88cffdb9ae1829710a3d561c621c0d035a`: schema-v2 capacity is now 512 rings / 16,384 vertices per feature; catalog 2,048 definitions / 1,048,576 vertices / 65,536 rings; JSON resource 2 MiB characters; serialized feature 288 KiB. Feature protocol remains 2, but older clients lack the expanded capacities. Largest production feature: Mediterranean / 16,223 vertices; largest ring count: Baltic / 255. Core still owns discovery/holes/priority/transport/rendering; no datapack processing or third geography level is added. Existing Macro/Local context, discovery ledgers and NPC snapshots are untouched.

Offline hash-pinned released-Core `FeatureCatalog.prepare`, geometry/index and ID projection validation passes 3,970 fixtures / 11,888 behavior assertions against the final content. Wire sizes are static estimates, not a client roundtrip. Source coastal generalization, omitted small islets and excluded detached lagoons remain deliberate handoff limitations. No Forge reload/gameplay/terrain/rendering/networking result is claimed. User checks: `/reload`, `/hj mapfeature status`; Student discovery versus Teacher exclusion, Mediterranean/Baltic retained islands, Danish/Turkish narrow passages, lake islands and basin labels, Hover/All/Off at several zooms, reconnect and preserved Japan knowledge. Do not reset production knowledge for testing.

## Africa water Map Features v1 — datapack 0.5.14 / Core 0.15.3

Adds 33 exact `data/human_journey/core/map_feature/<id>.json` resources: 7 gulfs, 2 straits/channels, 1 sea, 3 bays and 20 lakes/reservoirs. All are schema-v2 polygons, with 193 holes / 20,557 vertices. Combined catalog: **113 definitions**, 112 polygons, unchanged Shinano open polyline, 1,759 holes / 153,237 vertices. Preserves all 80 Japan/Europe feature bytes and all 225 Macro/Local files. `red_sea`, `gulf_of_aden`, `gulf_of_suez`, `gulf_of_aqaba` are shared global Africa/Asia IDs; later content must reuse them. No oceans, rivers or Suez Canal. Source acknowledgement is `AFRICA_WATER_FEATURES_SOURCES.md`, retained GSHHG notices are `licenses/africa_water/` at pack root.

Contract and capacities are unchanged: schema v2, 512 rings / 16,384 vertices per feature; catalog 2,048 definitions / 1,048,576 vertices / 65,536 rings. Exact hash-pinned released Core 0.15.3 outside `mods` accepts the combined catalog and passes 2,743 fixtures / 5,952 assertions, including hidden/single-ID projection; structural audit passes 3,207 assertions, 188 independent island samples and 11 overlap pairs. No installed JAR is executed/modified. No Forge startup/reload, client wire/UI or Minecraft gameplay/terrain test is claimed.

Core owns discovery, projection, holes, hover priority, SavedData, HUD and rendering. Content owns only approved names/types, geometry, radii and priorities. Features neither classify drinking water nor change NPC geography/economy profiles. Teacher exclusion, participant/student eligibility and both discovery ledgers remain untouched. Island interiors/edges exclude containment; approaching an island coast may legitimately discover its surrounding water by radius. Chad/Rukwa/Natron/Bangweulu are static source footprints, not changing water levels. Generalized offshore limits and tiny coastal simplification slivers remain source limitations, not exact surveyed/terrain boundaries.

Manual controls (safe Y from actual terrain): Bab western passage **2951.168,-858.795**, eastern **2966.187,-865.621**; Perim **2962.910,-863.778** excluded. Suez **2273.280,-1938.773**, Aqaba **2372.949,-1959.253** beat Red Sea; Tunis **716.800,-2525.867**, Sidra **1208.320,-2150.400**, Gabes **730.453,-2331.307** beat Mediterranean. Victoria water **2252.800,68.267** discovers the whole feature; Ukerewe **2251.435,139.264** and Idjwi **1982.464,145.408** exclude lake hover. Mayotte **3081.557,875.179** excludes Mozambique water. Check Student discovery versus Teacher-only approach, shared knowledge/reconnect, map modes and preserved Japan/Europe features without resetting discoveries. Terrain and gameplay acceptance remain pending user.
