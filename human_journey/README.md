# Human Journey

Educational Minecraft Java project. Source of truth and history: petryshamen/minecraft-education.

Target test environment: Minecraft Java 1.21.8 with Forge; datapack format 81.

## Import required

No current Human Journey datapack was present in this repository when this structure was created. This is a project scaffold, not a playable release. Import the existing datapack into source/ before building. Do not invent a new starting version or replace existing progress identifiers.

After import, source/ must contain pack.mcmeta and data/ at its root. Record the real datapack version in version.json and source/pack.mcmeta human_journey.version, then create the matching changelog entry. Do not include this source placeholder README in a build.

## Layout

- source/: actual datapack root, with data/human_journey/ and data/minecraft/.
- builds/test/: locally generated verified test ZIPs.
- docs/eras/: progression design and stable advancement IDs.
- docs/npc/: data schemas, stable NPC and quest IDs, dialogs and trades.
- docs/technical/: migration notes, scoreboard inventory and validation rules.

GitHub is not required during Minecraft gameplay. Build and deploy only to an explicitly configured LOCAL TEST WORLD. The live class server is always a manual deployment step.

Teacher bypasses for lesson preparation and Student progression restrictions must remain distinct. Preserve advancement IDs, hj_civ fake-player names, technology and quest scores, and functions referenced by NPCs.
