# Core 0.7.0 release

- Universal opt-in context-aware NPC economy: commodity/family/profile schemas, controlled vocabulary extensions, sell-only geography matching and deterministic four-priority exact/10% barter with actual stack limits. No production values, role catalogs or real regional profiles added.
- Frozen native family payment predicates, resolved output/components, spawn-only offer generation, native XP/stock and bounded AI-enabled home wandering/look. Existing static/story and all non-NPC mechanics preserved.
- Limits: 512 macro/4096 local areas, 1024 geography files, 4096 geography patches per era layer and 1,048,576 validated variants. No arbitrary dynamic offer-count cap.

**Build verified:** [GitHub Actions run 42418468802](https://github.com/petryshamen/minecraft-education/actions/runs/42418468802), source `a100a47d6bee20a8e9ba51120bde03515172c23f`. All 123 unit/structural tests passed (30 new economy tests plus all 93 previous tests). All seven no-world Forge data/Mixin checks passed, including native family matching, Easy NPC sanitizer/trade-data save/load, NBT and network codecs, XP/stock configuration and actual non-stackable items. Scale checks cover 600 local areas/patches and 521 geography resources. 105 old classes are byte-identical to Core 0.6.1; all 74 existing non-NPC classes, including all 15 temperature classes and complete fishing/water/audio integration, are unchanged. Runtime JAR SHA-256: `2c39ce6e711f48809395280462420a7039e34f2b04102f85c01c3fc45998880d`. No Minecraft entity/world was created, gameplay-tested, installed or deployed.

Deliverables: runtime JAR, full Gradle source ZIP with schemas/reference/checklist, and a separate SYNTHETIC-only economy fixture pack. The fixture pack uses era_test/hj_economy_test and arbitrary coordinates; use only in an isolated disposable world with other NPC/context packs disabled. It is not a production HJ integration; HJ Developer supplies the real catalog/profiles later.

Manual acceptance: after manual test setup, inspect /hj npc context, /hj npc status, /hj npc economy test_trader and /hj npc economy_match human_journey:test_output, then spawn test_trader inside the synthetic river region. Check family alternatives, counts/components, native XP/stock, independent NPCs, saved offer identity after restart, bounded look/wandering/home return and static/story compatibility. Test invalid reload retention and no-output geography; recheck previous systems. Full checklist is in ECONOMY.md.

Native limitations retained: Easy NPC runtime stock uses can restore on reload; empty traders have no native trade screen; family UI shows a representative member; native pathfinding cannot guarantee return through impassable terrain; native network packet limits remain. Native xp=10 is an offer parameter, not a custom fixed reward. No full gameplay/UI/navigation/world persistence test is claimed.

Installed automatically: NO. Main Human Journey datapack/progression/WE CAN STAY changed: NO. Live server changed: NO. Minecraft gameplay testing: NO.

