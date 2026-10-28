<!-- hj-mod-inspection {"filename":"human_journey_core-0.3.0.jar","sha256":"6cbe9b1c95ea3a9eab476c574d4474cc41e58eebbf49ca8ea707b9b61169b34b","mods":[{"mod_id":"human_journey_core","name":"Human Journey Core","version":"0.3.0","declared_version":"0.3.0"}]} -->
# Human Journey Core

Inspected installed `human_journey_core-0.3.0.jar`, Core **0.3.0**, SHA-256 `6cbe9b1c95ea3a9eab476c574d4474cc41e58eebbf49ca8ea707b9b61169b34b`. Target: Minecraft 1.21.8, Forge [58.1.18,59), TAN 21.8.0.2, GlitchCore [21.8.0.0,21.9). Installed hash matches the published 0.3.0 checksum. Read-only metadata/class evidence was correlated with [the matching release source and schema](https://github.com/petryshamen/minecraft-education/tree/95d43bc76c3e29ea6378aec81259aa85c8aabb22/human_journey_core). No JAR was changed or rebuilt.

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

## Water treatment API (schema 1)

Exact policy: `data/human_journey/core/water.json` (source: `source/data/human_journey/core/water.json`). Temperature remains at `data/human_journey/core/temperature.json`, unchanged by this migration. [Core water reference](https://github.com/petryshamen/minecraft-education/blob/95d43bc76c3e29ea6378aec81259aa85c8aabb22/human_journey_core/WATER.md).

Core owns container recognition, drinking hazards (bottles/canteens/TAN hand drinking), filter interaction/material consumption, ordinary-furnace/lit-campfire processing, stack-state preservation and bypass guards. The datapack owns probabilities, effects, durations, filter IDs/count and enabled methods/timings.

| Water JSON section | Human Journey settings |
| --- | --- |
| `normal` | `unsafe_chance: 0.60`, `thirst_duration_ticks: 600` (30 s TAN Thirst) |
| `dirty` | `unsafe_chance: 1.0`, `thirst_duration_ticks: 600`; `illness: {enabled: true, effect: "minecraft:poison", duration_ticks: 160, amplifier: 0}` (Poison I, 8 s) |
| `filtration` | `enabled: true`, `materials: ["minecraft:sand","minecraft:charcoal"]`, `material_count: 1`, `from: "dirty"`, `to: "normal"` |
| `boiling` | `furnace_enabled: true`, `campfire_enabled: true`, `furnace_ticks: 500`, `campfire_ticks: 500`, `from: "normal"`, `to: "purified"` |
| `hand_drinking` | `cooldown_ticks: 60` (Core default) |

PURIFIED adds no unsafe effect and does not cure pre-existing effects. Quality routes are fixed by schema: DIRTY → NORMAL filtration, NORMAL → PURIFIED boiling; no dirty boiling or manual charcoal purification. Effect chances are 0–1; durations 0–72000 ticks; amplifier 0–4; filters 1–64 distinct registered item IDs/count 1–64; process durations 1–72000 ticks; hand cooldown 1–1200 ticks.

Filter by holding one DIRTY container in main hand, filter in offhand, then sneak-right-click; one material is consumed in Survival/Adventure. Boil NORMAL water by placing it manually in an ordinary furnace with fuel or right-clicking a lit campfire; both take 500 ticks / 25 s. Sneak-right-click campfire with empty main hand retrieves water early. Core supports leather and all installed canteen tiers, retaining remaining drinks, name, enchantments and component patch; no static result recipes or recipe-book unlocks are needed. Empty/invalid canteens and effect potions are not treatment inputs.

Legacy Human Journey water recipes/grants are removed. `human_journey:water/convert_one` remains a no-op compatibility ID; no blanket potion clearing occurs. Student grant-all-then-enforce initialization and technology locks remain; leather canteen/Rain Collector are Era 0, copper canteen/Water Purifier require Copper, other tiers keep existing gates. Rain Collector and Water Purifier mechanics/recipes are untouched. Water for Life retains its IDs, Student/prerequisite checks and shared progress, recognizing purified bottles or any purified canteen.

After `/reload`, admin `/hjcore status [player]` must show water policy source/revision and successful reload independently of temperature. `/hjcore water` reports held quality/tier/remaining drinks/name. Highest-priority exact water resource wins; removal restores Core safety defaults. Invalid fields/types, unknown item/effect IDs or out-of-range data retain the last valid water policy and report rejection. No separate companion water datapack is needed alongside this integrated release; another pack at this path could override it.
