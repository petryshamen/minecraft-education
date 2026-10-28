# Human Journey

Educational Minecraft Java datapack. Current version: see version.json. Target: Minecraft Java 1.21.8 Forge, format 81.

- source/: current editable datapack.
- releases/original/: one final ZIP per completed version, plus the original user-supplied historical archives. Never overwrite or delete an older ZIP.
- docs/mods/: reusable mod IDs, versions, item state, recipe behavior and integration limitations.
- CHANGELOG.md: short release summaries. docs/imported/ retains original historical setup notes.

Workflow: edit source, validate, increment version metadata, build one final ZIP, install those exact bytes into Human Journey Test, then commit source/changelog and releases/original/Human_Journey_v<version_with_underscores>.zip to GitHub. Do not use builds/test/ in GitHub or generate reports for normal updates.

Preserve progression IDs and distinct Teacher/Student behavior. Mods are inspected read-only. Keep live-server deployment manual; Minecraft does not need GitHub during play.

Core 0.3.0 owns water treatment; the datapack policy is data/human_journey/core/water.json. Filter DIRTY water with sand/charcoal, then boil NORMAL water in a furnace or lit campfire. See docs/mods/human_journey_core.md for controls, state preservation and reload checks.
