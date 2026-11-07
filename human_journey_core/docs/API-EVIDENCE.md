# Dependency and API evidence

## Core 0.7.0 economy integration

- Exact target remains Easy NPC 6.5.2, using the previously verified public artifact whose class entries match the read-only installed target. Re-inspected `TradingDataCapable`: private static sanitizeTradingOffers rebuilds one-input ItemCosts via two getItemCost call sites, stored/synchronized offers are copied into runtime merchant offers, save/load use native MerchantOffers.CODEC, and native rewardMerchantTradeXp uses rewardExp plus `3 + nextInt(xp)`. The isolated redirect preserves only Core-marked cost predicates through these native rebuilds; unmarked costs retain native semantics.
- Actual Minecraft 1.21.8 ItemCost.test checks holder plus DataComponentExactPredicate; native MerchantMenu payment autofill already calls that test and merges only identical actual item/component stacks. Core encodes frozen members in the native custom-data cost predicate, intercepts only marked costs, and leaves native count checks/two payment slots intact. Native MerchantOffers stream codec uses ByteBufCodecs.collection; automated native packet round trips include 81 offers without a Core count cap.
- Native Easy NPC trade-data save/load hooks are exercised on a synthetic data proxy through TagValueOutput/TagValueInput, alongside NBT and merchant network codecs. Concrete output/components, payment quantities/frozen membership, maxUses and native xp=10/rewardExp configuration survive. This is structural trade-data persistence evidence, not full entity/world/server gameplay persistence testing. The accepted runtime/stored stock-use restoration limitation remains unchanged.
- HumanoidNPC native movement-speed attribute and Easy NPC objective/goal access were inspected. Dynamic Core-marked NPCs use an empty custom-objective set, native goal selectors/navigation/look controls and a saved fixed home. Forge Entity patch persists getPersistentData through ForgeData. No stock generator runs on reload or movement ticks; static/story movement is preserved. Actual appearance/UI/pathfinding/persistence remains manual acceptance.
- Optional native hook gate uses the inspected Forge 58.1.18 FMLLoader.getLoadingModList().getMods() API, before target transformation, accepting exact easy_npc 6.5.2. Movement registration is also gated. Missing/unsupported version conditions have structural tests; a real dependency-absent launch was not performed.
- [GitHub Actions run 42418468802](https://github.com/petryshamen/minecraft-education/actions/runs/42418468802), source `a100a47d6bee20a8e9ba51120bde03515172c23f`. All 123 unit/structural tests passed (30 new economy tests plus all 93 previous tests). All seven no-world Forge data/Mixin checks passed, including native family matching, Easy NPC sanitizer/trade-data save/load, NBT and network codecs, XP/stock configuration and actual non-stackable items. Scale checks cover 600 local areas/patches and 521 geography resources. 105 old classes are byte-identical to Core 0.6.1; all 74 existing non-NPC classes, including all 15 temperature classes and complete fishing/water/audio integration, are unchanged. Runtime JAR SHA-256: `2c39ce6e711f48809395280462420a7039e34f2b04102f85c01c3fc45998880d`. No Minecraft entity/world was created, gameplay-tested, installed or deployed.

- Downloaded build ZIP matched artifact SHA-256 `b4d88397321d931582c9a1d2d08b7a75d0b3d47801155e11e3c9f9b5ac60bbb5`. Main Java files match the successful sources JAR; test files match the uploaded source revision. Final root resource-pack format 64, Java 21 classes, version 0.7.0 and all three isolated Mixin configurations were checked. No production catalog/geography data or third-party classes are embedded. The separate example pack has only seven synthetic data/metadata files, a private test Era/objective and no progression-writing functions/tags.


## Core 0.6.1 fishing integration

**Build verified:** [GitHub Actions run 42119156083](https://github.com/petryshamen/minecraft-education/actions/runs/42119156083), source `5405a9d7b5c73c852977192b9483715ccbbafb1f`. All 93 unit/structural tests passed and all six no-world Forge data/Mixin checks passed. 107 pre-existing classes are byte-identical to Core 0.6.0, including all NPC/context/adapter, all 15 temperature classes and all water/audio integration. Only main bootstrap and Core status changed among existing classes. Final JAR metadata uses Core 0.6.1, Java 21 and root resource-pack format 64. Runtime JAR SHA-256: `c73b9b8e7944cbfe3d32768a8a1a5abf81c8dc358976f4f94965e8bc3c73db62`. No Minecraft world/bobber was created, no gameplay or real timing test was performed, and nothing was installed automatically.

Downloaded build ZIP matched GitHub artifact SHA-256 `701ea747bcde59d4bbcea9e228968f2442ede9035d5aabcdf59a0de50eed3e02`. Main Java sources match the successful sources JAR. Final runtime JAR contains both separate Mixin configurations and no bundled third-party classes, Minecraft loot/enchantment resources or embedded fishing balance policy. The example pack contains only pack metadata and the policy JSON.


- Inspected the actual installed Forge 1.21.8-58.1.18 client archive read-only, SHA-256 `b24b4bf17178f1b36b272d550cb1fb65e887f760fa74e72aa59d45eda9261db7`. Parsed class bytes without executing the installed JAR: exact `FishingHook.catchingFish(BlockPos)`, final `lureSpeed` tick field, three Mth.nextInt calls, last 100–600 draw and immediately subsequent native subtraction; also `FishingRodItem.use` native enchantment conversion and vanilla Lure JSON. Public build classpath is checked against the same contracts in tests.
- Inspected the public Forge 58.1.18 sources FishingHook patch: no bite-time event, only rod action/impact/retrieval hooks. The new separate required Mixin redirects one initial wait draw; no native loot/treasure/durability code or resource is edited. No installed mod is executed, used as a build classpath, uploaded or modified.
- [Fishing reference](../FISHING.md) documents inclusive tick ranges, native-effect scaling, lower-bound protection, vanilla weather/approach effects, reload boundaries and manual acceptance.


## Core 0.6.0 context integration

- Progression reference was inspected read-only at Human Journey 0.3.15: `hj_civ/#era1` is initialized as a binary flag, not an era counter. Java contains no fixed role list or scoreboard-era mapping; the integration JSON provides the two current predicates. No main progression source or real datapack was modified.
- Exact installed season target inspected read-only: `SereneSeasons-forge-1.21.8-21.8.0.0.jar`, SHA-256 `b50110cc760fde4bb653bb91b81cbc5931219066aafa833e87c08f10af947f56`. Public dependency `com.github.glitchfiend:SereneSeasons-forge:1.21.8-21.8.0.0`, SHA-256 `5afffec5862d73e98dd46515003781791bcc3641318ea86b189f7c5b9f17122b`: all 66 class entries byte-identical to the installed target, despite archive metadata differences. Installed mods were never executed, used as a build classpath, modified or uploaded.
- Upstream source [revision 1638beb3](https://github.com/Glitchfiend/SereneSeasons/tree/1638beb30717084683da5ed64541f28fc78c8b42), common API `SeasonHelper.getSeasonState(Level)`, public `dataProvider`, `ISeasonState.getSeason()`, and four enum constants were inspected before implementing the isolated typed adapter. Exact binary descriptors are checked automatically. The optional-dependency gate returns `none` without calling the adapter for absent/unsupported versions; null state/provider and runtime/linkage failures fall back with diagnostics. Tests simulate absent/error conditions and do not claim a real-world season/absence launch test.
- Context tests cover the binary and future multi-objective era rules, missing/tied scores, era role discovery/rejection and actual Brigadier suggestions, generic naming, optional/matched/dimension-restricted polygons, parent/label/priority rules, deterministic scalar/object/array merge and season selection, invalid composition rejection, last-valid reload retention and static/story compatibility. Existing identity and native adapter tests remain unchanged. No custom stock persistence was introduced.
- [GitHub Actions run 41960596385](https://github.com/petryshamen/minecraft-education/actions/runs/41960596385), source `c65ebfb3cad48c4116bd6e5e2fc8bbb2d5be0763`. All 81 unit/structural tests passed and all five no-world Forge data markers passed. The final runtime JAR has root resource-pack format 64, Core version 0.6.0 and Java 21 classes; its SHA-256 is `94b09d161ce5f8cb66d7bc490c66f0e8ba91114b11b7d9c7a9d305d962c9327a`. 83 pre-existing classes are byte-identical to Core 0.5.0, including all 15 temperature classes, all water/audio integration and the complete native Easy NPC adapter/parser/identity/static loader. Only NpcCommands and NpcBootstrap changed among existing classes. No NPC/world was created or gameplay/persistence/appearance/UI testing performed. Nothing was installed or deployed.

- Downloaded artifact ZIP SHA-256 `9ad6dd388385cbf27fdff8d402aed35427b319c533ef14f441af76d747c544dc` matched GitHub's artifact digest. All main Java source files match the successful sources JAR. Formal JSON schemas are authoring references; Java parser, registry, cross-reference and composition checks remain authoritative.


## Core 0.5.0 Easy NPC adapter MVP

- Installed target inspected read-only: `easy_npc-forge-1.21.8-6.5.2.jar`, SHA-256 `147c1c8836cc62d552ba6ef58cbf84882565d8e370d1eca1a1ab604965a11859`. Public build dependency `curse.maven:easy-npc-core-1308987:7402995` SHA-256 is `89c9bd175243e0b894d56e3dc4e5d7544f279005ace703510d34454cded6e317`. The archives differ, but every class entry in the public JAR was byte-compared with the installed target: zero class differences. The installed JAR was never executed, used as a classpath, modified or uploaded.
- Source reference: [upstream revision f929f515](https://github.com/MarkusBordihn/BOs-Easy-NPC/tree/f929f51540778bb8bcbb43989e6ff4a4c816ce85). Exact binary contracts were checked for `SkinDataEntry.createRemoteSkin`, dialogue/condition/action records, `setTradingOffers`, `setPresetUUID` and `easy_npc:humanoid`. No native GUI/export/import automation or copied preset NBT is used.
- Fresh entity UUID and separate preset UUID, scoped 32-character runtime page/button labels, own-NPC target UUID in action records, independent mutable native data sets and explicit main-page priority are applied once at spawn. Dialog/button identity survives native NBT data round trips. Native action hash limitations are documented; no execution-limit condition system or stock-persistence workaround is added.
- Trades are Advanced native MerchantOffers with explicit uses/maxUses, optional second costs, reset interval and current last-reset timestamp. `setTradingOffers` initializes the stored/synchronized collection and its runtime copy. Native runtime usage can diverge from the saved collection; this release does not claim to fix that 6.5.2 bug.
- [GitHub Actions run 41145562029](https://github.com/petryshamen/minecraft-education/actions/runs/41145562029), source `2f5677aaf4272967e55800644fbcc81e7ea0e42b`. 63 unit/structural tests passed; the no-world Forge data checks loaded exact Easy NPC 6.5.2 and verified two templates, native data codecs and instance isolation. All 64 pre-existing engine classes, including all 15 temperature classes and the complete water/shoreline/sound integration, are byte-identical to Core 0.4.1. Runtime JAR SHA-256: `ddf6f064a90d45dc03e83c8034e111bad3650de156347bd551cb29f101ba0a33`. No NPC entity/world was created, no appearance/UI/gameplay/persistence test was performed and nothing was installed automatically.

- Downloaded artifact ZIP matched SHA-256 `e6b877ae24e8a0d2007a2317dd3167da419c94b006a556301d41cc4abdd01299`. Actual JAR root resource pack metadata format 64, expanded mod version 0.5.0, Java 21 classes, optional Easy NPC dependency, no bundled third-party classes and no embedded NPC content were verified. Main Java source files match the successful sources JAR. The new formal authoring schema is additive documentation; runtime Java validation/cross-reference checks remain authoritative.


## Core 0.4.1 compatibility checks

- Connected-water search is a pure six-neighbor BFS bounded by 0–16 path steps, with at most 6,017 distinct positions at the maximum radius. Loaded-water predicates do not load chunks. Per-level origin results use a 256-entry LRU and less-than-20-tick lifetime, invalidated by water reload snapshots/shutdown. Geographic/TAN/direct-ocean precedence remains lazy and unchanged.
- Exact compiled Minecraft 1.21.8 Player.playSound bytecode forwards ALOAD 0 (the player) as the Level sound broadcast's excluded listener. Core 0.4 used this method server-side. The corrected isolated compatibility helper supplies ACONST_NULL as the excluded listener instead. ASM source-value analysis verifies both arguments directly. Cadence tests reject client, Consumable, silent and final-tick paths; only TAN DrinkItem.onUseTick is targeted. No canteen/vanilla sound hook was added.
- The complete final source project's main Java files were compared to the successful build's sources JAR. Final runtime JAR inspection verifies all untouched engine class bytes, including temperature, canteen marker/consumption, water drinking, purifier transactions, filter capacity, furnace and campfire hooks. The example policy differs only by its added ocean_connection_radius=16 and pack version; prior balance values and Caspian coordinates are identical.
- [GitHub Actions run 40819079966](https://github.com/petryshamen/minecraft-education/actions/runs/40819079966), source `32885fe806108d87c18f4050ab1517d57f586354`. 51 tests passed, all ten water mixins loaded, and the no-world Forge state checks passed (36 existing canteen conversions plus six-tier salt/purifier checks). Final JAR SHA-256: `bfeacb3ddc1042441b34598e3c48537d1911b1b38a10c19d90b65dcd82c3158d`. All 15 temperature classes and 47 total unrelated engine classes are byte-identical to 0.4.0; root resource-pack format is 64. No gameplay testing or automatic installation was performed.

- Downloaded Actions ZIP SHA-256 matched `48db730e617409349241b6ad2232df4bb02177df35d3f276877f4cd09889df2f`. All dependencies remain exact Minecraft 1.21.8 / Forge 58.1.18 / TAN 21.8.0.2. Installed mod hashes rechecked read-only; no installed JAR was used for execution/build.


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
