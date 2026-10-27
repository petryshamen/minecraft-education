# Human Journey Core 0.2.0

Stable engine for Minecraft **1.21.8**, Forge **58.1.18**, Tough As Nails **21.8.0.2**, GlitchCore **21.8.0.0**. This release adds reloadable datapack balance and an admin status command. Compilation/testing results are recorded after the GitHub build. Minecraft startup, multiplayer command output and actual reload/gameplay still need manual verification.

## Install manually

Stop the test server/client, back up the world and replace the old Core JAR with `human_journey_core-0.2.0.jar` in the test profiles/server. Keep exactly one Core version. Teacher and students should use the same Core/TAN dependency stack. Nothing in this project installs into Minecraft or edits live mods, worlds or the Human Journey progression datapack automatically.

## Debug command

```text
/hjcore status
/hjcore status <player>
```

Permission level **2** required. The first form reports the executing player; server console should use the second form. Output includes Core version, dimension, actual TAN temperature and target, thirst, hydration, exhaustion, hyperthermia exposure/threshold, Climate Clemency, active policy source/revision/reload result and all effective balance values. It only reads state. Teacher/Student tags do not bypass the admin permission check.

## Datapack balance

Place the JSON at exactly:

```text
<datapack>/data/human_journey/core/temperature.json
```

The external `example-datapack/` directory is a minimal optional datapack for this API. Its `pack.mcmeta` uses **data pack format 81** for 1.21.8. The Core JAR's root `pack.mcmeta` remains **resource pack format 64**. The example JSON is deliberately NOT embedded under the JAR's data directory, so missing external data really uses the compatibility fallback.

Either copy only `data/human_journey/core/temperature.json` into the Human Journey datapack, or install the standalone example pack in a TEST world's datapacks directory manually. The example reproduces the existing default balance. If your server TOML has custom values, copy those into the JSON before enabling the example pack. Existing world progress, advancement/function IDs and NPC data are not changed.

```json
{
  "schema_version": 1,
  "thirst": {
    "warm_exhaustion_per_second": 0.10,
    "hot_exhaustion_per_second": 0.50,
    "nether_hot_exhaustion_per_second": 0.50
  },
  "overworld": {
    "hyperthermia_damage": false,
    "heat_damage_interval_ticks": 40,
    "heat_damage_amount": 1.0
  },
  "nether": {
    "hyperthermia_damage": true,
    "heat_damage_interval_ticks": 40,
    "heat_damage_amount": 1.0,
    "remove_climate_clemency": false
  },
  "water": {
    "heat_relief_ticks_per_tick": 20,
    "cooling_interval_ticks": 10
  }
}
```

Change JSON and run **`/reload`**. Use `/hjcore status` afterward to confirm the source, revision, effective numbers and `last reload=OK`. Players do not need another JAR or reconnect for a successful balance reload. Changes to these numbers/policies normally require only a datapack update; new engine capabilities/schema extensions can still require a Core update.

### Field rules and units

| Field | Meaning / accepted range |
| --- | --- |
| `schema_version` | 1; may be omitted |
| `thirst.warm_exhaustion_per_second` | Extra TAN exhaustion/sec at WARM in Overworld and Nether; 0–20 |
| `thirst.hot_exhaustion_per_second` | Extra exhaustion/sec at HOT; 0–20 |
| `thirst.nether_hot_exhaustion_per_second` | Independent HOT rate for Nether; 0–20; inherits supplied HOT if omitted |
| `overworld.hyperthermia_damage` | Enable Overworld heat health damage; boolean |
| `nether.hyperthermia_damage` | Enable Nether heat health damage; boolean |
| `overworld/nether.heat_damage_interval_ticks` | Independent interval for each dimension; integer 20–72000 |
| `overworld/nether.heat_damage_amount` | Damage health points per scheduled hit; 0–20 (2 points = 1 heart) |
| `nether.remove_climate_clemency` | Remove TAN Climate Clemency in Nether from active Survival/Adventure players; boolean |
| `water.heat_relief_ticks_per_tick` | Net heat exposure relief in Overworld water; integer 0–140 |
| `water.cooling_interval_ticks` | Body-temperature step interval; integer 1–200 |

Rates assume 20 game ticks/sec. Extra exhaustion is not a direct decrement of thirst icons: TAN still processes hydration/exhaustion normally. Zero rates disable only Core's extra costs. Heat damage retains normal Minecraft protection and invulnerability; the minimum 20-tick interval avoids promising damage through hurt immunity. Core uses TAN's full-hyperthermia condition, suppresses native TAN hyperthermia pulses in Overworld/Nether and schedules only one matching TAN damage-type pulse per configured interval. Default Nether timing remains 40 ticks, 1 health point. Intervals follow `player.tickCount`, with no persistent damage counters.

Climate Clemency removal defaults to **false**. Enabling it removes the actual effect; switching it off does not recreate an already removed potion effect. No temperature is artificially forced to HOT. The command reports the real TAN level/effect.

### Water behavior preserved

The existing pre-player-tick exposure reduction and post-server-tick net relief/temperature stepping are preserved. Default relief is **20**, step interval **10**. HOT → WARM → NEUTRAL remains the cooling path. Core never warms COLD/ICY, never pushes below NEUTRAL, and does not change cold targets, wetness, dehydration, drinks, fire/lava damage or other dimensions. Ordinary TAN cooling into cold remains possible. Overworld water still protects from direct heat damage while Core water relief is enabled. Relief **0** disables every additional Core water adjustment/protection; TAN's own water behavior continues.

### Precedence, fallback and errors

- The highest-priority enabled datapack supplying the exact JSON path wins as one file; fields are not merged between multiple packs.
- Present JSON fields override the old `<world>/serverconfig/human_journey_core-server.toml` settings. Missing fields inherit those settings; fields without a legacy equivalent use compatibility defaults (40-tick damage, amount 1, no Nether Clemency removal).
- Missing/disabled/deleted JSON resets to the existing server config/defaults on the next reload. It does not leave stale overrides behind. Without a pack, the old water settings remain effective.
- Wrong types, unknown fields, unsupported schema and out-of-range numbers reject the entire new policy. The last valid policy remains active. On first load, fallback remains active. `/hjcore status` shows `REJECTED` and the server log explains the error. Vanilla `/reload` may report success even when this custom policy was rejected; inspect status.
- Publication is one immutable policy snapshot. Server shutdown clears policy state so another singleplayer world cannot inherit the previous world's overrides.
- Keep using the existing TOML only as a compatibility fallback. JSON is the intended normal balance interface. Editing fallback TOML still follows Forge's restart requirements.

## Build and validation

Use JDK 21. Windows: `./build.ps1 -JavaHome 'C:\path\to\jdk-21'`. Linux/macOS: `chmod +x gradlew` then `./gradlew --no-daemon test build`. The included wrapper is Gradle 9.3.1; ForgeGradle is pinned to 7.0.3. Expected runtime output: `build/libs/human_journey_core-0.2.0.jar` (the `-sources.jar` is not an installable mod).

Repository workflow: `.github/workflows/human-journey-core.yml` runs from the `human_journey_core` folder on Core pushes or manual dispatch. `check/build` verifies root resource-pack metadata in the actual JAR. Unit tests cover strict policy validation, sparse fallback, independent Nether rate, damage intervals, disabled damage, reload replacement/removal, invalid reload retention and the existing water/cold policy invariants.

## Manual acceptance checks

1. Start a dedicated TEST server and matching client. Check `/hjcore status` on yourself and another player (console included), permission rejection for non-admin students and correct live TAN values.
2. With no policy JSON, compare water cooling to 0.1.1, including any custom TOML values. Test entering/exiting water, HOT→WARM→NEUTRAL, COLD/ICY, death/respawn and dimension changes.
3. Enable the example policy, `/reload`, confirm source/revision. Change only thirst rates (including independent Nether HOT), reload and check effective values and timed exhaustion.
4. Change water numbers in JSON, reload and verify; restore **20/10** for the established default behavior. Relief=0 should leave only TAN's native water behavior.
5. Test fully hyperthermic Overworld/Nether players at 20/40/100-tick intervals with normal thirst to isolate heat damage. Confirm no double hits; disabled dimension damage must protect only against heat. Recheck dehydration and fire/cold behavior.
6. Set `remove_climate_clemency` true/false, reload and test the effect in Nether versus Overworld. Creative/Spectator stay exempt from active Core mechanics.
7. Try malformed JSON, wrong types and invalid bounds; confirm `REJECTED`, previous values retained, and useful log output. Correct it and reload; then delete/disable the JSON and confirm fallback is restored.
8. If multiple packs define the path, change their order and confirm the winning pack name. Restart/switch worlds to check no stale overrides. Repeat Survival checks for Teacher and Student; progression tags do not alter these mechanics.

Source/API evidence and previous build history remain in `docs/API-EVIDENCE.md`. Old release JARs are preserved under `releases/original/`. No Minecraft files are modified automatically.
