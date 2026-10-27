# 0.2.0

- Added admin `/hjcore status [player]` and reloadable datapack temperature policies, with strict validation, last-valid-policy retention on errors and legacy server config fallback.
- Added independent Nether HOT exhaustion, dimension heat-damage intervals/amounts and optional Nether Climate Clemency removal. Kept the established Overworld water-cooling algorithm and fallback values unchanged.

# 0.1.1

- Added root `pack.mcmeta` with resource pack format 64 for Minecraft 1.21.8; runtime JAR metadata is now checked by Gradle `check`/`build`.
- Temperature behavior, server config defaults and TAN integration remain unchanged.

# 0.1.0

- Added server-configurable WARM/HOT thirst exhaustion via TAN API, dimension-specific hyperthermia damage policy and rapid Overworld water heat relief.
- Added Gradle 9.3.1 wrapper, ForgeGradle 7.0.3 build project, policy tests, build script, GitHub Actions workflow and installation/test instructions. GitHub build succeeded with 6 passing tests; runtime JAR archived for manual installation. In-game validation pending.
