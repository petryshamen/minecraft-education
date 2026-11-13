<!-- hj-mod-inspection {"filename":"human_journey_core-0.9.0.jar","sha256":"6135de5fdaaf75624ab045dba7263a331790359eb80e42c60f08b0019e39b721","mods":[{"mod_id":"human_journey_core","name":"Human Journey Core","version":"0.9.0","declared_version":"0.9.0"}]} -->
# Human Journey Core

Inspected installed `human_journey_core-0.9.0.jar`, Core **0.9.0**, SHA-256 `6135de5fdaaf75624ab045dba7263a331790359eb80e42c60f08b0019e39b721`. Target: Minecraft 1.21.8, Forge [58.1.18,59), TAN 21.8.0.2, GlitchCore [21.8.0.0,21.9). Metadata/hash matches the [verified Core 0.9.0 release report](https://github.com/petryshamen/minecraft-education/blob/54e2b77ea43c13f182646c7b9cc0af3a6e48942e/human_journey_core/releases/original/Core-0.9.0-RELEASE.md). Existing water/temperature/fishing/economy policies and geography/HUD are unchanged by this datapack; Core supplies the juice state/guards/healing engine. No mod was changed, rebuilt or installed by this datapack task.

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

## Era 0 juices — datapack 0.5.4, requires Core 0.9.0

**Verified dependency:** the test instance now has the exact published Core 0.9.0 JAR. Juice canteen/healing runtime support requires **Core 0.9.0 on server and clients**; the user manages installation and no mod was replaced by this datapack task. Its [completed release report](https://github.com/petryshamen/minecraft-education/blob/54e2b77ea43c13f182646c7b9cc0af3a6e48942e/human_journey_core/releases/original/Core-0.9.0-RELEASE.md) and [JUICE contract](https://github.com/petryshamen/minecraft-education/blob/54e2b77ea43c13f182646c7b9cc0af3a6e48942e/human_journey_core/JUICE.md) were inspected, not inferred from the original task. Expected runtime JAR `human_journey_core-0.9.0.jar`, SHA-256 `6135de5fdaaf75624ab045dba7263a331790359eb80e42c60f08b0019e39b721`. Core’s published native checks confirm all seven exact TAN JuiceItem registrations and six canteen tiers; those upstream checks are not gameplay tests performed by this datapack task.

Definitions load from **`data/human_journey/core/juice/<id>.json`**. Exact schema 1 fields: `schema_version`, `id`, `bottle_item`, `ingredient` containing exactly one `item` or `tag`, `canteen_display_name`, `heal_per_drink`. Production uses explicit items, one distinct ingredient/bottle per ID and **heal_per_drink: 1.0** throughout. No native hydration/thirst numbers are copied into policy. All seven exist immediately, including Chorus Fruit; acquisition controls availability, with no new Agriculture/era requirement.

| Definition ID | TAN bottle / overridden recipe ID | Ingredient | Canteen display name |
| --- | --- | --- | --- |
| `apple` | `toughasnails:apple_juice` | `minecraft:apple` | Apple Juice Canteen |
| `cactus` | `toughasnails:cactus_juice` | `minecraft:cactus` | Cactus Juice Canteen |
| `chorus_fruit` | `toughasnails:chorus_fruit_juice` | `minecraft:chorus_fruit` | Chorus Fruit Juice Canteen |
| `glow_berry` | `toughasnails:glow_berry_juice` | `minecraft:glow_berries` | Glow Berry Juice Canteen |
| `melon` | `toughasnails:melon_juice` | `minecraft:melon_slice` | Melon Juice Canteen |
| `pumpkin` | `toughasnails:pumpkin_juice` | `minecraft:pumpkin` | Pumpkin Juice Canteen |
| `sweet_berry` | `toughasnails:sweet_berry_juice` | `minecraft:sweet_berries` | Sweet Berry Juice Canteen |

The seven bottle overrides are `data/toughasnails/recipe/<id>_juice.json`, each vanilla shapeless **purified_water_bottle + ingredient → one existing TAN juice bottle**, no sugar. Installed TAN 21.8.0.2 has exactly these seven native juice-producing recipe resources, with matching registration constants/classes and item/tag references. Overrides retain native IDs, outputs, group and category instead of adding parallel recipe IDs. Recipe initialization remains `recipe give @s *` followed immediately by existing Student technology enforcement; Teacher remains unrestricted. Native juice recipe advancement IDs remain valid. Core’s CustomRecipe is special/server-authoritative and needs no new datapack unlock advancement or per-tier recipe grants.

Core already ships **one** `data/human_journey/recipe/juice_canteen.json`, type **`human_journey_core:juice_canteen`**; this datapack does not duplicate it. It accepts exactly one full purified TAN canteen plus one configured ingredient, in either grid size. Leather/copper/iron/gold/diamond/netherite retain their original purified carrier item and components, with `minecraft:custom_data` compound `human_journey:juice` containing stable `id` and cached `display_name`. There are no separate juice/tier items or fixed-output canteen recipes. Normal/dirty/salt/empty/part-used/already-juiced inputs and extra occupied cells are rejected by Core.

Core owns identity, presentation, validated state, native TAN serving delegation, completed-drink healing/depletion, tier-preserving final empty return, persistence and water-treatment guards. Every successful survival sip spends one use; identity persists until empty. Native bottle consumption/remainders/thirst/hydration remain TAN’s, with Core adding only configured completed-drink healing (capped at missing health). Interrupting a drink gives no bonus. Creative no-depletion is native. Existing unmarked water/Rain Collector/purifier/salt mechanics and all technology gates remain unchanged; no Food/Consumable/custom hydration components or native drink tags are overridden.

After manually installing Core 0.9.0 and reloading, permission-2 diagnostics are `/hjcore juice`, `/hjcore juice list`, `/hjcore juice show apple`. Confirm seven definitions and no reload error. Definition/catalog parsing is verified offline; actual server reload and gameplay still require the manual checks even though the required engine is installed. Test Apple bottle/canteen healing and native thirst while injured/thirsty; full purified Leather plus another tier; one use per sip and correct final empty; Cactus/Pumpkin differences; invalid qualities/partial fills; component/persistence and ordinary water regressions. Chorus Fruit’s definition/recipe is checked structurally, without requiring Era 0 acquisition. No mod JAR was executed/rebuilt/installed and no Minecraft gameplay testing was performed.
