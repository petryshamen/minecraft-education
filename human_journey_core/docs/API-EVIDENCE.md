# Dependency and API evidence — 2026-10-23

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
- No client classes are referenced by Core. Dependency resolution, Java compilation and dedicated-server loading are still unverified.

## Build outcome

Native confined verification attempted using Human Journey Developer: run ID `1792818501189645800`, exit 1, `sandbox_verified=false`.

```text
MXC launcher: cannot locate required Windows platform directory: HRESULT 0x80070003
```

Gradle did not run. JUnit did not run. No runtime JAR was produced or installed. No unrestricted-build fallback was used.
