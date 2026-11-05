# Core 0.6.1 release

- Isolated fishing wait initialization policy with datapack reload, strict validation, last-valid retention and vanilla fallback when absent.
- Example: 20–60s base wait, Lure I/II/III 15–55/10–50/5–45s. Vanilla rain/cover, approach/bite phases and server tick rate affect elapsed time. Existing waits change only at their next initialization; recast after reload for comparison.
- No fishing loot/enchantment resources, retrieval/durability/open-water logic or NPC/temperature/water/audio mechanics changed.

**Build verified:** [GitHub Actions run 42119156083](https://github.com/petryshamen/minecraft-education/actions/runs/42119156083), source `5405a9d7b5c73c852977192b9483715ccbbafb1f`. All 93 unit/structural tests passed and all six no-world Forge data/Mixin checks passed. 107 pre-existing classes are byte-identical to Core 0.6.0, including all NPC/context/adapter, all 15 temperature classes and all water/audio integration. Only main bootstrap and Core status changed among existing classes. Final JAR metadata uses Core 0.6.1, Java 21 and root resource-pack format 64. Runtime JAR SHA-256: `c73b9b8e7944cbfe3d32768a8a1a5abf81c8dc358976f4f94965e8bc3c73db62`. No Minecraft world/bobber was created, no gameplay or real timing test was performed, and nothing was installed automatically.

Manual checklist: install the JAR plus separate fishing example pack in a test world, reload, inspect `/hjcore status`; compare repeated ordinary/Lure fishing under clear/open-sky conditions; check vanilla loot/treasure/rod behavior. Change/reject/remove the policy and recast to test reload/last-valid/vanilla fallback. Recheck existing NPC/water/temperature/audio behavior. Full details and schema are in FISHING.md in the source project.

Installed automatically: NO. Main Human Journey datapack modified: NO. Live server modified: NO. Minecraft gameplay testing: NO.

