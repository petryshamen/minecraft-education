# Human Journey

Educational Minecraft Java datapack. Current version: see version.json. Target: Minecraft Java 1.21.8 Forge, format 81.

- source/: current editable datapack.
- releases/original/: one final ZIP per completed version, plus the original user-supplied historical archives. Never overwrite or delete an older ZIP.
- docs/mods/: reusable mod IDs, versions, item state, recipe behavior and integration limitations.
- CHANGELOG.md: short release summaries. docs/imported/ retains original historical setup notes.

Workflow: edit source, validate, increment version metadata, build one final ZIP, install those exact bytes into Human Journey Test, then commit source/changelog and releases/original/Human_Journey_v<version_with_underscores>.zip to GitHub. Do not use builds/test/ in GitHub or generate reports for normal updates.

Preserve progression IDs and distinct Teacher/Student behavior. Mods are inspected read-only. Keep live-server deployment manual; Minecraft does not need GitHub during play.

Core 0.4.0 owns water treatment and salt-aware purifier filling; the datapack policy is data/human_journey/core/water.json. Filter DIRTY water with sand/charcoal, then boil NORMAL water in a furnace or lit campfire. See docs/mods/human_journey_core.md for controls, state preservation and reload checks.

Progression state: objective `hj_civ`, fake player `#era1` is the existing authoritative era flag (0 = Era 0, 1 = Era 1; not a general era counter). `#tech_agriculture` independently gates Agriculture recipes; `#we_can_stay` is the retained Era 0 milestone. Future NPC integration should read this shared state, not create per-player era scores.

Temporary rollback after loading 0.3.15: `/function human_journey:admin/return_to_era0` once as admin. It never runs automatically and introduces no permanent lock. Existing Agriculture recipe locks apply to Students only (wooden/stone hoes, bread, composter, hay block, wheat); they do not remove existing items or prevent their use. The unfinished Era 1 root retains its ID/criterion without a display; sync revokes stale completion while #era1 is 0. A future transition can set #era1 and #tech_agriculture to 1 normally and restore the root display.
