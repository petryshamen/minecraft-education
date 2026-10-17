# Human Journey

Current source: version 0.3.9 water-system test, based on imported Human_Journey_v0_3_8.zip. Target: Minecraft Java 1.21.8 / Forge, datapack format 81.

- source/: current editable datapack; v0.3.8 gameplay is preserved except the water changes documented in CHANGELOG.md. pack.mcmeta adds human_journey.version for the development tools.
- releases/original/: all nine user-supplied ZIPs, preserved byte for byte; SHA256SUMS.txt records their checksums. Historical versions are archival and are not installed.
- docs/imported/v0.3.8/: original bundled notes, moved outside the runtime pack. Several note headings describe older releases; treat them as historical context and verify against current source.
- builds/test/: verified local builds. Only the newest test version is deployed to the configured Human Journey Test world.

Minecraft requires no GitHub connection during play. No live-class deployment or progress reset is performed. Preserve advancement IDs, hj_civ names, technology scores, NPC stage objectives and NPC function IDs.

Testing: reload/reopen the test world, inspect the log and /datapack list. Compare Teacher and Student roles; check Student metallurgy locks, charcoal/furnace water purification, Jengo stages 1–4, Trader stage and Era 0 capstone. Static validation does not prove mod or command compatibility.

Format reference: https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-7 (81); 1.21.8 hotfix: https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-8.

Water changes and exact manual tests: [v0.3.9 checklist](docs/technical/water-0.3.9-test-checklist.md). Dirty water now requires charcoal filtering before final purification. Canteens use component-preserving charcoal recipes; no canteen furnace route is installed. Broad bottle matching is explicitly accepted for this test (effect potions also match minecraft:potion ingredients).
