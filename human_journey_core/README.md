# Human Journey Core 0.10.0

**Core 0.10.0 build verified:** [GitHub Actions run 43528787371](https://github.com/petryshamen/minecraft-education/actions/runs/43528787371), source `da4417d46b2fd500801e8100c40d496c330a05bd`. All 256 tests passed (221 prior tests retained/migrated + 35 new), zero failures; all eleven no-world Forge checks passed. Exact-unit planning/removal tests include normal/repeated takes, native quick-craft recipe clearing, split/tag/shifted grids, v1 defaults and custom bottle/canteen source/healing/persistence. Native data checks exercise registered six-tier canteens, shared item/model name/codecs, v2 recipes and bounded resource accounting. 201 existing classes are byte-identical to Core 0.9.0; existing class changes are confined to juice integration. Runtime SHA-256: `ca7324d31ff86d377dddddaf8b57a941438e26169ff1fa4ef404a4a3d6fa40be`. No Minecraft gameplay, automatic installation, main-datapack change or live-server deployment.

Juice schema v2 adds separate bottle/canteen ingredient-unit costs and one reusable custom juice bottle. V1 remains loadable with native mode and one-unit costs. **[Current juice reference, migration and checklist](JUICE.md)**. No production juice definitions, ingredient prices or Carrot behaviour profile are supplied. Manual installation only.


**Core 0.9.0 baseline:** [GitHub Actions run 43347893280](https://github.com/petryshamen/minecraft-education/actions/runs/43347893280), source `76967a2c2f77805a9cf9c9a7ab51224ee0dc5c39`. All 221 tests passed (179 existing + 42 new), zero failures; all ten no-world Forge checks passed. Native checks cover six TAN canteen tiers, seven native JuiceItem registrations, dynamic recipe JSON/network codecs, item NBT/component persistence and depletion. Tests execute TAN's real thirst/hydration/effect handler on mocked player dependencies; no gameplay world/player was constructed. 183 existing classes are byte-identical to Core 0.8.1, including NPC/economy, geography/discovery/HUD, fishing, temperature and unmodified water mechanics. Five existing class files change only for bootstrap and marked-juice guards, including WaterContainers' generated switch helper. Runtime SHA-256: `6135de5fdaaf75624ab045dba7263a331790359eb80e42c60f08b0019e39b721`. No automatic installation or live-server/main-datapack modification.

Reloadable juice canteens and configured bottled-juice healing. **[Juice schema, native TAN integration, Developer setup and manual checklist](JUICE.md)**. No production juice catalog/ingredients are bundled. Core 0.8.1 HUD defaults and all existing systems are retained. Manual installation only.


**Core 0.8.1 baseline:** [GitHub Actions run 43343851204](https://github.com/petryshamen/minecraft-education/actions/runs/43343851204), source `8e1d148007abffd3bd4fb91dfeabbf56100cd381`. All 179 tests passed (174 existing plus five config-default/correction/range tests), with zero failures; all nine no-world Forge checks passed. Only GeographyClientConfig.class differs from the prior runtime classes; rendering, discovery and other systems are byte-identical. Forge's actual correction path preserves valid custom values and fills only missing/invalid settings. Runtime SHA-256: `5306950b4f296d63af7dbba8e87690c483cd9c83d27d192e4dd295fc2126e484`. No gameplay testing or automatic installation.

Core 0.8.1 changes only geography HUD defaults: enabled=true, X=85, Y=10, scale=0.8, opacity=0.35. Existing custom configs and all ranges are retained; no config migration or rendering/discovery change. Manual installation only. The 0.8.0 build evidence below documents the unchanged geography implementation.


**Core 0.8.0 baseline build:** [GitHub Actions run 43119081927](https://github.com/petryshamen/minecraft-education/actions/runs/43119081927), source `4260cd2d84c335662ac820c5bfbf2f03facada6b`. All 174 tests passed (all 144 existing + 30 new geography/discovery tests), with zero failures. All nine no-world Forge checks passed, including native compressed SavedData NBT and context/discovery packet codec round trips. 155 existing classes are byte-identical to Core 0.7.1, including economy/families/payment diversity/safe wandering/fishing/water/temperature; only Core bootstrap and additive NpcGeography parsing changed. Root pack.mcmeta format 64 and Java 21 metadata are verified. Runtime SHA-256: `cbe47eb6f84f6175d8757fe4e120b3758e9626402ddd3b2ed092b168f08aff08`. No world/player/NPC was created, no Minecraft gameplay testing, installation or deployment was performed.

Geography HUD and persistent civilisation-wide exploration. **[Geography architecture, commands, client settings and manual checklist](GEOGRAPHY.md)**. Install the runtime JAR manually on both server and participating clients. No Xaero dependency, production geography rewrite or main-datapack change is needed. Existing NPC/economy, safe wandering, fishing, temperature and water integrations remain unchanged.

The server resolves context using the existing datapack geography and production participant tags. Students discover stable macro/local IDs for everyone; teachers can view the same HUD but never contribute through movement. No Minecraft gameplay testing or automatic deployment is performed.

Data-driven NPC economy engine: commodity/family/profile schemas, deterministic value barter, native stack limits, frozen family payments, spawn-time persisted offers, native XP and bounded generic movement. **[Economy integration, schema reference, limitations and checklist](ECONOMY.md)**. Core supplies no production prices/catalogs or real regional mappings. The separate economy pack is synthetic test-only. Existing progression, story/WE CAN STAY, NPC context, fishing, temperature and water systems remain intact.


Optional fishing timing policy: the separate `HumanJourney-Fishing-0.6.1.zip` configures a 20–60s base wait and 5s/level Lure reduction. Missing policy preserves vanilla behavior. **[Fishing installation, JSON reference, exact timing semantics and manual checklist](FISHING.md)**. Existing NPC/water/temperature content and dependencies are unchanged.


Spawn-time NPC context: `/hj npc spawn hunter` and `/hj npc spawn fisher` resolve a datapack-defined era, optional geography and optional Serene Seasons before passing the composed template to the unchanged Easy NPC 6.5.2 adapter. Permission level 2 required. Generic economic role names; static/story templates remain compatible. No automatic refresh or stock-persistence replacement.

**[NPC installation, complete context reference and manual checklist](NPC.md)** · [Static/story format](NPC-STATIC.md) · [Changelog](CHANGELOG.md).

Install `human_journey_core-0.10.0.jar` and the separate `HumanJourney-NPC-Integration-0.6.0.zip` manually in a test world. Keep the real Human Journey 0.3.15 progression and existing temperature/water settings unchanged. The integration pack needs no geographic database and writes no scores. Era 1 is recognized but has no demo economic roles. `/hj npc context` diagnoses era/season/geography; `/hj npc status` reports load outcomes.

Schemas: [era resolver](docs/npc-context.schema.json), [archetypes](docs/npc-archetype.schema.json), [geography](docs/npc-geography.schema.json), [unchanged static templates](docs/npc-template.schema.json). Core 0.5 temperature/water/audio and native adapter behavior are preserved. No Minecraft gameplay testing or automatic deployment.

## Compatibility patch 0.4.1

- Added `classification.ocean_connection_radius` (integer 0â€“16, default/example 16; 0 disables the connected-water extension). Existing water packs remain valid; all earlier balance values and the Caspian polygon remain unchanged.
- The fallback classifies otherwise NORMAL water as SALT when face-connected WATER fluid reaches a direct configured ocean/salt biome tag within that many water steps. It searches in all six directions, including flowing/waterlogged water; diagonal contact and proximity across dry land are insufficient. Explicit geographic NORMAL/DIRTY/PURIFIED/SALT overrides, TAN DIRTY/PURIFIED and direct salt-tag classifications win before any search. Geographic salt polygons do not become new ocean seeds or expand into neighbors.
- Search only examines loaded chunks and never generates/loads chunks. Radius is path length, not straight-line proximity. At radius 16 a fully wet search checks at most 6,017 positions. A per-level LRU holds at most 256 origin results for fewer than 20 ticks; water/biome/chunk changes may take up to one second to refresh. Any water-policy reload outcome invalidates this cache immediately; server shutdown clears it. There is no persistent quality cache on items, so collected salt still uses its saved marker.
- Purifier intakes, container filling, hand drinking and `/hjcore water` share this classifier. No purifier/treatment/temperature algorithms were changed.
- Fixed the bottle audio delivery: Core 0.4 called `Player.playSound` server-side, which excludes that player. The isolated TAN DrinkItem hook now sends a direct Level broadcast with no excluded listener, so the drinker and nearby players receive one sound per use tick cadence. It stays server-only, skips Consumable-bearing/silent items, has no final-tick sound and does not target vanilla items or TAN canteens. Structural bytecode checks verify the old excluded-player argument and the new null argument. Audibility remains a manual gameplay check.

**Manual patch checklist:** (1) Beach-biome water connected to ocean within 16 steps should show SALT with `/hjcore water` and reject purifier filling. (2) A coastal lake separated by land stays NORMAL; opening a water connection makes it SALT after the cache refresh, closing it restores NORMAL after refresh. (3) Reload radius 0/4/16 and verify the boundary; invalid 17/negative/fractional values retain the last valid policy. (4) Listen as drinker and nearby observer to TAN bottles/juices; compare vanilla Water Bottle/canteen, including interrupted drinks and finish, for duplicates. (5) Recheck Caspian, swamp/mangrove, stored salt canteens, partial-canteen filtering/boiling, purifier capacity, Rain Collector and temperature cooling.



Adds persistent Salt Water, geographic classification, instant filter-capacity Water Purifier filling and TAN bottle/juice sound compatibility, retaining data-driven treatment and state-preserving canteens for Minecraft 1.21.8 / Forge 58.1.18 / TAN 21.8.0.2. The temperature integration below is unchanged.

**Water setup, JSON schema, controls, compatibility migration and tests: [WATER.md](WATER.md).** Install the companion water datapack above the old Human Journey pack to neutralize its obsolete blanket potion-to-dirty conversion. Keep existing temperature balance settings.

## Install manually

Stop the test server/client, back up the world and replace the old Core JAR with `human_journey_core-0.10.0.jar` in the test profiles/server. Keep exactly one Core version. Teacher and students should use the same Core/TAN dependency stack. Nothing in this project installs into Minecraft or edits live mods, worlds or the Human Journey progression datapack automatically.

## Debug command

```text
/hjcore status
/hjcore status <player>
/hjcore water
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
| `thirst.warm_exhaustion_per_second` | Extra TAN exhaustion/sec at WARM in Overworld and Nether; 0Ã¢â‚¬â€œ20 |
| `thirst.hot_exhaustion_per_second` | Extra exhaustion/sec at HOT; 0Ã¢â‚¬â€œ20 |
| `thirst.nether_hot_exhaustion_per_second` | Independent HOT rate for Nether; 0Ã¢â‚¬â€œ20; inherits supplied HOT if omitted |
| `overworld.hyperthermia_damage` | Enable Overworld heat health damage; boolean |
| `nether.hyperthermia_damage` | Enable Nether heat health damage; boolean |
| `overworld/nether.heat_damage_interval_ticks` | Independent interval for each dimension; integer 20Ã¢â‚¬â€œ72000 |
| `overworld/nether.heat_damage_amount` | Damage health points per scheduled hit; 0Ã¢â‚¬â€œ20 (2 points = 1 heart) |
| `nether.remove_climate_clemency` | Remove TAN Climate Clemency in Nether from active Survival/Adventure players; boolean |
| `water.heat_relief_ticks_per_tick` | Net heat exposure relief in Overworld water; integer 0Ã¢â‚¬â€œ140 |
| `water.cooling_interval_ticks` | Body-temperature step interval; integer 1Ã¢â‚¬â€œ200 |

Rates assume 20 game ticks/sec. Extra exhaustion is not a direct decrement of thirst icons: TAN still processes hydration/exhaustion normally. Zero rates disable only Core's extra costs. Heat damage retains normal Minecraft protection and invulnerability; the minimum 20-tick interval avoids promising damage through hurt immunity. Core uses TAN's full-hyperthermia condition, suppresses native TAN hyperthermia pulses in Overworld/Nether and schedules only one matching TAN damage-type pulse per configured interval. Default Nether timing remains 40 ticks, 1 health point. Intervals follow `player.tickCount`, with no persistent damage counters.

Climate Clemency removal defaults to **false**. Enabling it removes the actual effect; switching it off does not recreate an already removed potion effect. No temperature is artificially forced to HOT. The command reports the real TAN level/effect.

### Water behavior preserved

The existing pre-player-tick exposure reduction and post-server-tick net relief/temperature stepping are preserved. Default relief is **20**, step interval **10**. HOT Ã¢â€ â€™ WARM Ã¢â€ â€™ NEUTRAL remains the cooling path. Core never warms COLD/ICY, never pushes below NEUTRAL, and does not change cold targets, wetness, dehydration, drinks, fire/lava damage or other dimensions. Ordinary TAN cooling into cold remains possible. Overworld water still protects from direct heat damage while Core water relief is enabled. Relief **0** disables every additional Core water adjustment/protection; TAN's own water behavior continues.

### Precedence, fallback and errors

- The highest-priority enabled datapack supplying the exact JSON path wins as one file; fields are not merged between multiple packs.
- Present JSON fields override the old `<world>/serverconfig/human_journey_core-server.toml` settings. Missing fields inherit those settings; fields without a legacy equivalent use compatibility defaults (40-tick damage, amount 1, no Nether Clemency removal).
- Missing/disabled/deleted JSON resets to the existing server config/defaults on the next reload. It does not leave stale overrides behind. Without a pack, the old water settings remain effective.
- Wrong types, unknown fields, unsupported schema and out-of-range numbers reject the entire new policy. The last valid policy remains active. On first load, fallback remains active. `/hjcore status` shows `REJECTED` and the server log explains the error. Vanilla `/reload` may report success even when this custom policy was rejected; inspect status.
- Publication is one immutable policy snapshot. Server shutdown clears policy state so another singleplayer world cannot inherit the previous world's overrides.
- Keep using the existing TOML only as a compatibility fallback. JSON is the intended normal balance interface. Editing fallback TOML still follows Forge's restart requirements.

## Build and validation

Use JDK 21. Windows: `./build.ps1 -JavaHome 'C:\path\to\jdk-21'`. Linux/macOS: `chmod +x gradlew` then `./gradlew --no-daemon test build`. The included wrapper is Gradle 9.3.1; ForgeGradle is pinned to 7.0.3. Expected runtime output: `build/libs/human_journey_core-0.10.0.jar` (the `-sources.jar` is not an installable mod).

Repository workflow: `.github/workflows/human-journey-core.yml` runs from the `human_journey_core` folder on Core pushes or manual dispatch. `check/build` verifies root resource-pack metadata in the actual JAR. Unit tests cover strict policy validation, sparse fallback, independent Nether rate, damage intervals, disabled damage, reload replacement/removal, invalid reload retention and the existing water/cold policy invariants.

## Manual acceptance checks

1. Start a dedicated TEST server and matching client. Check `/hjcore status` on yourself and another player (console included), permission rejection for non-admin students and correct live TAN values.
2. With no policy JSON, compare water cooling to 0.1.1, including any custom TOML values. Test entering/exiting water, HOTÃ¢â€ â€™WARMÃ¢â€ â€™NEUTRAL, COLD/ICY, death/respawn and dimension changes.
3. Enable the example policy, `/reload`, confirm source/revision. Change only thirst rates (including independent Nether HOT), reload and check effective values and timed exhaustion.
4. Change water numbers in JSON, reload and verify; restore **20/10** for the established default behavior. Relief=0 should leave only TAN's native water behavior.
5. Test fully hyperthermic Overworld/Nether players at 20/40/100-tick intervals with normal thirst to isolate heat damage. Confirm no double hits; disabled dimension damage must protect only against heat. Recheck dehydration and fire/cold behavior.
6. Set `remove_climate_clemency` true/false, reload and test the effect in Nether versus Overworld. Creative/Spectator stay exempt from active Core mechanics.
7. Try malformed JSON, wrong types and invalid bounds; confirm `REJECTED`, previous values retained, and useful log output. Correct it and reload; then delete/disable the JSON and confirm fallback is restored.
8. If multiple packs define the path, change their order and confirm the winning pack name. Restart/switch worlds to check no stale overrides. Repeat Survival checks for Teacher and Student; progression tags do not alter these mechanics.

Source/API evidence and previous build history remain in `docs/API-EVIDENCE.md`. Old release JARs are preserved under `releases/original/`. No Minecraft files are modified automatically.
