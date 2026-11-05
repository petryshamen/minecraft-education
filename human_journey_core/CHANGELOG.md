# 0.6.0

- Added datapack era predicates, era-specific economic archetypes and current-era Tab completion; Hunter/Fisher require no manual context arguments.
- Added optional dimension-aware polygon macro/local geography, era-sensitive local labels, generic role naming and isolated optional Serene Seasons 21.8.0.0 context.
- Added deterministic base/era/macro/local/season composition, atomic last-valid reload, `/hj npc context`, and context reload status. Context is a spawn-time snapshot.
- Added a separate no-geography integration datapack, JSON schemas and automated/no-world structural checks. Preserved 0.5 static/story format, Easy NPC adapter/identity/native persistence and all temperature/water/audio integration. Accepted native stock reload behavior remains unchanged.

# 0.5.0

- Added reloadable NPC JSON templates and `/hj npc spawn <template> [name]` with admin permission, dynamic suggestions, clear validation/status feedback and last-valid-set retention.
- Added an isolated Easy NPC 6.5.2 fresh-entity adapter for remote skins, dialogue/buttons, scoreboard conditions/actions, commands, Advanced trades and per-instance identities; separate Hunter/Story Test example datapack included.
- Preserved all existing temperature/water integration. Documented native stored/runtime trade-stock limitation; no persistence workaround or gameplay test claimed.

# 0.4.1

- Added reloadable, bounded connected-ocean detection for shoreline water; preserved explicit/TAN classifications, salt persistence, treatment, purifier capacity, Rain Collector and temperature behavior.
- Fixed TAN bottle/juice sound delivery to include the drinker, using one isolated server broadcast with no added final sound. Added path-connectivity, precedence, radius and audio-routing regression checks.

# 0.4.0

- Added persistent Salt Water containers, ocean-tag/ordered-polygon classification, shared salt drinking consequences and reloadable datapack policies; example Caspian region included.
- Reworked TAN Water Purifier around adjacent freshwater sources, empty-container instant fills and saved consumable filter capacity; salt cannot use existing treatment paths. Rain Collector and temperature integration unchanged.
- Added isolated TAN bottle/juice drink sound compatibility, richer water diagnostics and automated geometry/state/capacity/contract regression checks. Manual gameplay testing remains pending.

# 0.3.0

- Added reloadable water policy, unified item/direct-drinking hazards, dynamic state-preserving filtration and furnace/lit-campfire boiling, with legacy recipe guards and optional migration datapack.
- Added water status/held-container diagnostics; preserved temperature integration, TAN purifier and rain collector.

# 0.2.0

- Added admin `/hjcore status [player]` and reloadable datapack temperature policies, with strict validation, last-valid-policy retention on errors and legacy server config fallback.
- Added independent Nether HOT exhaustion, dimension heat-damage intervals/amounts and optional Nether Climate Clemency removal. Kept the established Overworld water-cooling algorithm and fallback values unchanged.

# 0.1.1

- Added root `pack.mcmeta` with resource pack format 64 for Minecraft 1.21.8; runtime JAR metadata is now checked by Gradle `check`/`build`.
- Temperature behavior, server config defaults and TAN integration remain unchanged.

# 0.1.0

- Added server-configurable WARM/HOT thirst exhaustion via TAN API, dimension-specific hyperthermia damage policy and rapid Overworld water heat relief.
- Added Gradle 9.3.1 wrapper, ForgeGradle 7.0.3 build project, policy tests, build script, GitHub Actions workflow and installation/test instructions. GitHub build succeeded with 6 passing tests; runtime JAR archived for manual installation. In-game validation pending.
