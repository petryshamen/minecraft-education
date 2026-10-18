# Human Journey

Educational Minecraft Java datapack. Current version: see version.json. Target: Minecraft Java 1.21.8 Forge, format 81.

- source/: current editable datapack.
- releases/original/: one final ZIP per completed version, plus the original user-supplied historical archives. Never overwrite or delete an older ZIP.
- docs/mods/: reusable mod IDs, versions, item state, recipe behavior and integration limitations.
- CHANGELOG.md: short release summaries. docs/imported/ retains original historical setup notes.

Workflow: edit source, validate, increment version metadata, build one final ZIP, install those exact bytes into Human Journey Test, then commit source/changelog and releases/original/Human_Journey_v<version_with_underscores>.zip to GitHub. Do not use builds/test/ in GitHub or generate reports for normal updates.

Preserve progression IDs and distinct Teacher/Student behavior. Mods are inspected read-only. Keep live-server deployment manual; Minecraft does not need GitHub during play.

Water integration limits and state evidence are in docs/mods/tough_as_nails.md. Canteens use component-preserving charcoal recipes; no canteen furnace route is installed. Broad drinkable-potion bottle matching was explicitly approved for v0.3.9.
