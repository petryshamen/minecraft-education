# Core 0.6.0 release

Datapack-driven spawn-time NPC context: generic Hunter/Fisher, logical scoreboard eras, optional polygon geography and optional Serene Seasons. Composition: base → era → macro → local → season. Static Story Test and native Easy NPC adapter remain unchanged. No timed-reset/persistence redesign.

**Build verified:** [GitHub Actions run 41960596385](https://github.com/petryshamen/minecraft-education/actions/runs/41960596385), source `c65ebfb3cad48c4116bd6e5e2fc8bbb2d5be0763`. All 81 unit/structural tests passed and all five no-world Forge data markers passed. The final runtime JAR has root resource-pack format 64, Core version 0.6.0 and Java 21 classes; its SHA-256 is `94b09d161ce5f8cb66d7bc490c66f0e8ba91114b11b7d9c7a9d305d962c9327a`. 83 pre-existing classes are byte-identical to Core 0.5.0, including all 15 temperature classes, all water/audio integration and the complete native Easy NPC adapter/parser/identity/static loader. Only NpcCommands and NpcBootstrap changed among existing classes. No NPC/world was created or gameplay/persistence/appearance/UI testing performed. Nothing was installed or deployed.

Manual test: install the JAR and separate NPC integration datapack; `/reload`; run `/hj npc context`, `/hj npc spawn hunter`, `/hj npc spawn fisher`; inspect Tab, names, dialogue/trades/Max Uses. Verify Era 1 rejects the demo roles, invalid reload keeps last data, Story Test works and old NPC snapshots remain unchanged. Compare season context in-world, test optional polygons if desired, then native save/reload and temperature/water/audio regressions. See NPC.md for the complete checklist.

The source ZIP includes the complete Gradle project, wrapper, schemas, examples, tests, reference and standalone build workflow. No main datapack or live server was changed; all installation/testing remains manual.

