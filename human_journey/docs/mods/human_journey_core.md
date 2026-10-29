<!-- hj-mod-inspection {"filename":"human_journey_core-0.4.0.jar","sha256":"6677192fa31e4fce85d95386225cfb7d49a7e6d5096c8a7d903676f89cbe9719","mods":[{"mod_id":"human_journey_core","name":"Human Journey Core","version":"0.4.0","declared_version":"0.4.0"}]} -->
# Human Journey Core

Inspected installed `human_journey_core-0.4.0.jar`, Core **0.4.0**, SHA-256 `6677192fa31e4fce85d95386225cfb7d49a7e6d5096c8a7d903676f89cbe9719`. Target: Minecraft 1.21.8, Forge [58.1.18,59), TAN 21.8.0.2, GlitchCore [21.8.0.0,21.9). Installed hash matches the published 0.4.0 checksum. Read-only metadata/class evidence was correlated with [the matching README, water schema and source](https://github.com/petryshamen/minecraft-education/tree/80c3d4a7ae3c1053773aeb4e8e643f53101f24a3/human_journey_core). No JAR was changed or rebuilt.

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
