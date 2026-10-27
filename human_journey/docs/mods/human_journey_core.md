<!-- hj-mod-inspection {"filename":"human_journey_core-0.2.0.jar","sha256":"d2fd9fc11f36564cedaf0233afe77c49bf896b8dda6b29654e7d17dc2fbf2cfc","mods":[{"mod_id":"human_journey_core","name":"Human Journey Core","version":"0.2.0","declared_version":"0.2.0"}]} -->
# Human Journey Core

Inspected installed `human_journey_core-0.2.0.jar`, Core **0.2.0**, SHA-256 `d2fd9fc11f36564cedaf0233afe77c49bf896b8dda6b29654e7d17dc2fbf2cfc`. Target: Minecraft 1.21.8, Forge [58.1.18,59), TAN 21.8.0.2, GlitchCore [21.8.0.0,21.9). The installed hash matches the repository's 0.2.0 release checksum. Metadata/class constant pools were inspected read-only and correlated with the published source at [9a04e64](https://github.com/petryshamen/minecraft-education/tree/9a04e64a178b5c3e18ea052770870515ba75c94b/human_journey_core). No JAR was changed or rebuilt.

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
