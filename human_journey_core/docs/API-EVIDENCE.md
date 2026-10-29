# Dependency and API evidence

## Core 0.4.0 salt/purifier/audio integration

- Exact installed TAN identity/hash rechecked read-only and still matches the independently downloaded public build dependency. No installed JAR was executed, modified or used as a build classpath.
- Pinned upstream author source plus compiled-descriptor/ASM checks identify `EmptyCanteenItem.fillCanteen`, `ThirstHandler.onPlayerUseItem`, `WaterPurifierBlockEntity.serverTick/getTotalPurifyTime/getFilterDuration`, saved integer capacity fields and `WaterPurifierMenu.canPurify`. Core adapts those paths without copying upstream classes into its JAR.
- Exact dependency resources confirm vanilla `minecraft:is_ocean` (including the deep-ocean tag), TAN dirty tag swamp/mangrove_swamp/mushroom_fields, and the TAN purified-water tag (empty by default). Runtime holder/tag checks use those exact IDs and water-block coordinates.
- Salt uses an additive namespaced CUSTOM_DATA marker; no new item registrations or duplicated tier classes. Real registered TAN canteen/bottle NBT codec round trips preserve it independently of biome policy. Existing conversion checks retain all 36 tier/fill/quality cases, dynamic furnace outputs and real campfire saved slots/timers.
- Native purifier filter durations come from TAN, with stored units charged atomically per completed empty-container fill. The actual block entity's capacity, input/output items and NBT round trips were tested without a Level/world. Invalid source and blocked-output transactions spend nothing. Filled/salt inputs cannot use legacy purifier recipes.
- Upstream `DrinkItem` has no sound-tick override and no Consumable component on its bottles/juices; `FilledCanteenItem` has a separate working sound-tick override. The isolated compatibility mixin adds server-only bottle/juice cadence and skips Consumable-bearing stacks, with no vanilla/canteen target. Structural checks establish method absence in the unmodified dependency; actual Forge bootstrap confirms the new override is applied. Listening/packet timing is a manual test.
- Datagen's isolated item registry is frozen and supplied a real sand-tag fixture before querying the unmodified TAN filter lookup. This fixture exists only in the opt-in no-world verification provider; runtime policies/tag registries are not changed by it.
- [GitHub Actions run 40812517738](https://github.com/petryshamen/minecraft-education/actions/runs/40812517738), source commit `acdcc5c96c07dd74d34f6783ceb194b0d9a683d6`. 39 unit/structural tests passed; all ten required water mixin targets loaded. The no-world Forge datagen smoke passed 36 v0.3 canteen conversions and salt/purifier persistence/transactions across all six tiers. Final JAR: 116,569 bytes, SHA-256 `6677192fa31e4fce85d95386225cfb7d49a7e6d5096c8a7d903676f89cbe9719`. Root pack format 64 verified; all 15 compiled temperature classes are byte-for-byte identical to Core 0.3.0. No Minecraft gameplay testing was performed.

- Artifact ZIP digest `764ebd81f0727d284af6b048a0a39c1cee1b38c1cdf4cc3e939b35b696a760df` matched the downloaded bytes. No third-party classes, internal balance policy or Rain Collector hooks are bundled. Purifier crafting recipe is unchanged.


## Core 0.3.0 water integration

- Installed TAN identity rechecked read-only: `ToughAsNails-forge-1.21.8-21.8.0.2.jar`, SHA-256 `b7a0d16be6f713a84e3e45c688fc71fcf77621f1a8d40b0d3246a7a9bfc15611`. The independently downloaded public CurseMaven artifact used for builds has identical bytes. Installed mod JARs were never used as executables/classpaths or modified.
- Existing integration notes under `human_journey/docs/mods/tough_as_nails.md` and pinned author source confirm separate item IDs for quality, six material tiers and damage-based fill. The new real-Forge datagen smoke checks the registered items directly and confirms capacities 5/6/7/10/15/25.
- [Successful run 40630024751](https://github.com/petryshamen/minecraft-education/actions/runs/40630024751), source `14d6ca0b78697f73ce488332af73a960a549c621`: 28 passing unit tests and successful actual ModLauncher/Mixin + registered TAN item integration smoke. State checks use a canonical frozen enchantment fixture registry and NBT (the actual world-save format), not lossy JSON conversion of custom numeric NBT tags.
- All six required water mixin targets were loaded and validated under Forge 58.1.18. Core's item/direct-drink path replaces only TAN water penalties while retaining thirst/hydration; furnace callbacks retain vanilla fuel/container logic. Campfire handling retains real slot/timer persistence and skips only managed water in native cooking. Legacy crafting quality changes are guarded before taking the result.
- Runtime JAR: 77736 bytes, SHA-256 `6cbe9b1c95ea3a9eab476c574d4474cc41e58eebbf49ca8ea707b9b61169b34b`. Downloaded artifact ZIP matched GitHub digest `06b0e9480e3754fb531e0c4d88e892b389824e06f4c47a3376a3c4b7f93f47ff`.
- Root pack format 64 and MixinConfigs verified in final JAR; no bundled third-party classes or internal balance JSON. All 15 temperature classes match 0.2.0. TAN purifier/rain-collector classes and recipes are not patched.
- Manual gameplay/packet/heat-timing tests remain necessary. No client/server world or live mod/datapack installation was performed. See `WATER.md` for controls, policy schema, compatibility migration and acceptance checks.

## Core 0.2.0 datapack interface

- [Successful build](https://github.com/petryshamen/minecraft-education/actions/runs/40440241042), source commit `448713fa984379c3f36fb52d6ce388eda56cb939`; 17 tests, 0 failures, packaged resource metadata check passed.
- Runtime JAR SHA-256: `d2fd9fc11f36564cedaf0233afe77c49bf896b8dda6b29654e7d17dc2fbf2cfc`, 33006 bytes. Artifact ZIP digest: `3c084cb1305c79cb65613d17354ed9b249ade83ea20d778a48bcaaa2c68dae33`.
- Forge 58.1.18 sources confirm `AddReloadListenerEvent.BUS`, `RegisterCommandsEvent.BUS` and `ServerStoppedEvent.BUS`. Compilation verifies the four-argument `PreparableReloadListener.reload` interface, resource lookup/source-pack access, command registration and public registry-backed `DamageSource` construction.
- Additional public TAN API used: thirst/hydration/exhaustion getters, full-hyperthermia check/threshold and `TANEffects.CLIMATE_CLEMENCY`. The installed TAN JAR remains read-only and is not used as an executable/classpath.
- Existing `HeatPolicy` class/enum bytes match 0.1.1. The post-tick water cooling source block matches after substituting config accessors. New tests cover parser bounds/types, independent rates/intervals, fallback, reload replacement/removal and invalid-policy retention.
- External sample datapack uses data pack format 81; root JAR resource metadata remains format 64. No balance JSON is embedded in the runtime JAR, so the absent-file fallback can work.
- No dedicated Minecraft server/client was launched by this build. Actual command/reload lifecycle and gameplay acceptance checks remain manual; no mod or datapack was installed automatically.

## Earlier evidence — 0.1.x

## Exact installed target (read-only inspection)

- `ToughAsNails-forge-1.21.8-21.8.0.2.jar`
- SHA-256: `b7a0d16be6f713a84e3e45c688fc71fcf77621f1a8d40b0d3246a7a9bfc15611`
- Mod ID `toughasnails`, version `21.8.0.2`, Forge >=58, GlitchCore >=21.8.0.0.
- An independent download from `https://cursemaven.com/curse/maven/tough-as-nails-246391/7063978/tough-as-nails-246391-7063978.jar` has the same SHA-256. Builds use this public distribution, never the installed JAR.

Upstream reference: [Glitchfiend/ToughAsNails, 1.21.8 commit f63d0095](https://github.com/Glitchfiend/ToughAsNails/tree/f63d0095d2a89237c701ee8795c96819a2cc8127). This source reference alone is not proof of the release binary's implementation; class declarations in the matching public artifact are also checked separately without executing the mod.

Public API used:

- `toughasnails.api.temperature.TemperatureHelper`: `isTemperatureEnabled`, `getTemperatureData`.
- `toughasnails.api.temperature.ITemperature`: `getLevel`, `setLevel`, `getTargetLevel`, `setTargetLevel`, `getHyperthermiaTicks`, `setHyperthermiaTicks`.
- `TemperatureLevel`: ICY, COLD, NEUTRAL, WARM, HOT.
- `toughasnails.api.thirst.ThirstHelper`: `isThirstEnabled`, `getThirst`; `IThirst.addExhaustion(float)`.
- `toughasnails.api.damagesource.TANDamageTypes.HYPERTHERMIA`; actual damage resource `data/toughasnails/damage_type/hyperthermia.json`.

The upstream TemperatureHandler ticks hyperthermia, checks its damage type, and synchronizes data using TAN's normal player lifecycle. Core adjusts state through the API, does not call internal TAN methods, and does not patch or execute the installed mod. Gameplay/synchronization remains to be verified in Minecraft.

## Forge and Gradle

Official [Forge 1.21.8 downloads](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.21.8.html), [58.1.18 MDK](https://maven.minecraftforge.net/net/minecraftforge/forge/1.21.8-58.1.18/forge-1.21.8-58.1.18-mdk.zip).

- This exact MDK uses ForgeGradle >=7.0.3 and Gradle 9.3.1. This project pins ForgeGradle 7.0.3; the plugin marker and implementation POM were resolved separately.
- Wrapper JAR, `gradlew`, and `gradlew.bat` were copied from that official MDK, not recreated. Third-party notices are included.
- Distribution SHA-256 from [Gradle checksum](https://services.gradle.org/distributions/gradle-9.3.1-bin.zip.sha256): `b266d5ff6b90eada6dc3b20cb090e3731302e553a27c5d3e4df1f0d76beaff06`.
- Forge sources for exactly 58.1.18 confirm `TickEvent.PlayerTickEvent.Pre.BUS`, `TickEvent.ServerTickEvent.Post.BUS`, `getServer()`, and predicate-based cancellation in `LivingAttackEvent.BUS`.
- No client classes are referenced by Core. Dependency resolution and Java compilation passed in GitHub Actions; dedicated-server loading still needs an in-game test.

## Earlier local build attempt

Native confined verification attempted using Human Journey Developer: run ID `1792818501189645800`, exit 1, `sandbox_verified=false`.

```text
MXC launcher: cannot locate required Windows platform directory: HRESULT 0x80070003
```

Gradle did not run locally. No unrestricted local build fallback was used.

## Successful GitHub Actions build

- [Run 40066494993](https://github.com/petryshamen/minecraft-education/actions/runs/40066494993), source commit `06e795f597619f0056bc375cc239de4563e980b0`.
- Java 21, Gradle 9.3.1, ForgeGradle 7.0.3, Minecraft 1.21.8 / Forge 58.1.18.
- `compileJava`, `compileTestJava`, `test`, `jar`, `sourcesJar`, `build` passed; 6 tests, 0 failures, 0 skipped.
- Runtime JAR: `human_journey_core-0.1.0.jar`, 11348 bytes, SHA-256 `531c7b52eac25796e717f772731623320fa73373a2cba8f042818404a703fd74`.
- Artifact ZIP ID `12548609621`, SHA-256 `08df3af1eddb517fdb44cbe197d30f51a09dcf1811d6ea4f520e5ad774fedde4`. Downloaded archive matched this digest.
- JAR inspection confirmed expanded mod version 0.1.0, six Java 21 class files under `org/humanjourney/core/`, and no bundled third-party mod classes.
- Exact runtime JAR archived under `releases/original/`. Installation remains manual. Forge runtime startup and gameplay have not yet been tested.
