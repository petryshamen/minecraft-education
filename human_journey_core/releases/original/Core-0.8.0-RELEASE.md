# Core 0.8.0 release — Geography HUD & Shared Discovery

Manual runtime installation on server and clients. No required datapack migration or new example pack. Existing production geography/profile/polygon/priority content is unchanged. Detailed architecture, client settings and manual acceptance checklist: GEOGRAPHY.md; additive macro label schema: docs/npc-geography.schema.json.

**Build verified:** [GitHub Actions run 43119081927](https://github.com/petryshamen/minecraft-education/actions/runs/43119081927), source `4260cd2d84c335662ac820c5bfbf2f03facada6b`. All 174 tests passed (all 144 existing + 30 new geography/discovery tests), with zero failures. All nine no-world Forge checks passed, including native compressed SavedData NBT and context/discovery packet codec round trips. 155 existing classes are byte-identical to Core 0.7.1, including economy/families/payment diversity/safe wandering/fishing/water/temperature; only Core bootstrap and additive NpcGeography parsing changed. Root pack.mcmeta format 64 and Java 21 metadata are verified. Runtime SHA-256: `cbe47eb6f84f6175d8757fe4e120b3758e9626402ddd3b2ed092b168f08aff08`. No world/player/NPC was created, no Minecraft gameplay testing, installation or deployment was performed.

- World-wide independent macro/local stable IDs use native Overworld SavedData, shared across players and dimensions. Removed IDs remain stored/stale-safe. Only hj_participant AND hj_student AND NOT hj_teacher create discoveries; teachers/participant-only/outsiders cannot contribute via movement.
- Server evaluates at 2Hz with cached context, clientbound changed-context messages, batched snapshots (128 IDs/category per packet) and discovery deltas. No full geometry or Xaero API. Disconnect/role removal clears client state. Natural first discoveries send concise participant chat; re-entry is silent.
- HUD: modern Forge GUI layer, bold white local / secondary grey macro, fitted black panel 55% opacity, X170/Y10, scale1 in GUI pixels. Enabled/offsets/scale/opacity live in config/human_journey_core-client.toml. Macro-only one line; no context hidden. Client types isolated from dedicated/common paths.
- Permission2: /hj geography status; /hj geography discovery list macro|local [page]; /hj geography discovery mark macro|local <id>; /hj geography discovery reset. Mark validates loaded IDs; lists flag stale IDs. Reset waits for movement/context re-evaluation before rediscovery.
- Future map integration can read immutable server/client stable-ID snapshots; it still needs a separate geometry/label protocol. No Xaero world-map integration is implemented.

Manual checks: two students share first discoveries; teacher and conflicting teacher/student do not discover; macro-only/no-geography HUD; repeat-entry silence; saved test-world restart and reload; admin invalid-ID/mark/reset; client positioning/F1/scale; unchanged NPC/economy, progression, safe movement, fishing, water and temperature.

Limitations: real HUD appearance, actual world restart/dedicated gameplay startup and minimap layout require user testing. Very brief crossings may occur between 0.5-second samples. Disk flush follows native autosave/shutdown. No personal discoveries or rewards. Matching Core is required on server and clients.

Installed automatically: NO. Main datapack/live server changed: NO. Minecraft gameplay testing: NO.
