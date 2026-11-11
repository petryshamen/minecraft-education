# Geography HUD and shared discovery — Core 0.8.0

**Build verified:** [GitHub Actions run 43119081927](https://github.com/petryshamen/minecraft-education/actions/runs/43119081927), source `4260cd2d84c335662ac820c5bfbf2f03facada6b`. All 174 tests passed (all 144 existing + 30 new geography/discovery tests), with zero failures. All nine no-world Forge checks passed, including native compressed SavedData NBT and context/discovery packet codec round trips. 155 existing classes are byte-identical to Core 0.7.1, including economy/families/payment diversity/safe wandering/fishing/water/temperature; only Core bootstrap and additive NpcGeography parsing changed. Root pack.mcmeta format 64 and Java 21 metadata are verified. Runtime SHA-256: `cbe47eb6f84f6175d8757fe4e120b3758e9626402ddd3b2ed092b168f08aff08`. No world/player/NPC was created, no Minecraft gameplay testing, installation or deployment was performed.

## Installation and scope

Minecraft Java 1.21.8, Java 21, Forge 58.1.18; retain the existing Core dependency stack. Manually replace the previous Core runtime JAR with `human_journey_core-0.8.0.jar` on server and clients. Keep only one Core version installed. Back up the test world before testing. Nothing is automatically installed or deployed. No new balance/example datapack is required: current `npc_geography` resources work as supplied. The new network channel requires Core on clients and server.

## Eligibility and authority

The exact production tags are `hj_participant`, `hj_student`, `hj_teacher`. HUD, discovery snapshots and announcements require `hj_participant`. Creating discoveries requires **participant AND student AND NOT teacher**. Teacher exclusion wins conflicting tags, regardless of permission level. Participant-only and unconfigured players never discover. Teachers can explicitly use permission-level-2 administrative mark/reset commands; this is administration, not discovery through movement.

## Storage and evaluation

One Overworld `SavedData` record, `human_journey_geography`, stores `discoveredMacroRegions`, `discoveredLocalAreas` and a revision counter for the entire world/civilisation. Native SavedData codec storage (`<world>/data/human_journey_geography.dat`) is marked dirty on change and participates in normal server autosave/shutdown. No player-specific progression is stored. Stable logical IDs, not labels, are persisted. Macro and local discovery are independent, including macro-only or local-only contexts. Previously recorded IDs are never removed merely because content was renamed or removed; stale IDs are retained and identified in admin diagnostics. Presentation always comes from current resolved geography, so stale IDs do not generate HUD entries.

At most twice per second, the server evaluates position/dimension, role tags, logical era and accepted context-data revision. Unchanged inputs reuse cached resolution. Joining, movement, respawn, role changes, era changes and accepted datapack reloads re-evaluate as needed. Existing resolver, parent/priority rules, polygons and profiles are reused without modification. If era is unavailable, geographic matching still works with default labels. Ambiguous/invalid resolution produces an empty HUD context and a server warning instead of inventing geography or discovery.

## Labels and content compatibility

Local `display_label` and matching `era_labels` remain authoritative. Macro regions now optionally accept the same fields. Both are optional and validated exactly like existing local labels (48 characters; era keys must exist). Current production macros without display labels continue to load. Blank/missing presentation labels fall back to readable title-cased words from the logical ID (for example `northern_europe` becomes `Northern Europe`); raw identifiers/underscores are not displayed. This HUD-only fallback does not change NPC names or macro/local resolution. No production labels, polygons, priorities or T/M/V/W/R/S profiles are supplied by Java.

## Networking and future map API

The isolated `geography` package owns context DTOs, shared ledger, SavedData, commands and synchronisation. One versioned Forge PLAY channel sends only clientbound, main-thread-handled messages. Clients cannot submit discoveries. Context contains participant visibility and current macro/local ID+label, never polygons. Snapshots are sent on participant join, missed revision or reset; new discoveries/admin marks send deltas when the client is current. Packets contain at most 128 IDs per collection. Larger snapshots are staged and published atomically. Context is sent only when its actual contents change. Moving repeatedly inside one unchanged context sends no recurring discovery snapshots or notifications. A new join concurrent with discovery may receive a redundant initial snapshot; this is bounded, not periodic full-state traffic.

Server state remains authoritative if a send fails; subsequent evaluations retry out-of-date snapshots/context and log one warning per affected connection. Disconnect and role removal clear the client mirror. Public immutable `SavedDiscovery.snapshot()` / `DiscoveryLedger.Snapshot`, `GeographyServer.context(player)` and client `GeographyClient.state()` expose stable IDs for a later map adapter. Full geometry and all-region labels are deliberately absent from the wire; a future world map will need a separate geometry/label protocol. There is no Xaero API usage, hidden terrain integration or map rendering in this release.

## HUD and client options

The physical-client entrypoint registers `AddGuiOverlayLayersEvent` into Forge's `POST_SLEEP_STACK`; removed GUI-overlay events are not used. Rendering uses Minecraft GuiGraphics, font shadow and the 1.21.8 Matrix3x2fStack. Client types remain isolated from common/dedicated-server code. F1 hides the HUD along with normal GUI. No geography or no participant tag hides it.

Default position is **X 170, Y 10 GUI-scaled pixels**, intended to sit right of a normal top-left minimap. Core does not inspect Xaero size/settings. Local text is bold white; macro text is smaller in emphasis (normal-weight muted grey) on the second line. Macro-only context uses one primary line. A black panel with 55% opacity fits text plus five-pixel padding (19-pixel one-line / 30-pixel two-line height). Long text is shortened to fit a compact maximum width, and the panel is clamped to the viewport. Small screens, UI scale and minimap settings can require adjusting the offsets.

Edit `config/human_journey_core-client.toml`, section `[geography_hud]`:

```toml
enabled = true
x_offset = 170
y_offset = 10
scale = 1.0
background_opacity = 0.55
```

Offset range: -8192..8192 (clamped on-screen when rendered); scale: 0.5..3.0; opacity: 0.0..1.0. Apply client config through Forge's normal reload/restart mechanism; `/reload` is for server datapacks, not client settings. Disabling the HUD does not disable discovery or server synchronisation.

## Commands and notifications

All new commands require permission level 2. Existing `/hj npc ...` and `/hjcore ...` commands remain registered.

```text
/hj geography status
/hj geography discovery list macro [page]
/hj geography discovery list local [page]
/hj geography discovery mark macro <id>
/hj geography discovery mark local <id>
/hj geography discovery reset
```

Status is player-executed and read-only: current macro/local IDs and labels, discovery flags, totals, revision, stale counts and exact eligibility. Lists show twelve stable IDs per page and mark removed definitions `(stale)`. Mark accepts only current loaded IDs, offers completion and rejects unknown IDs without changing state. Admin mark synchronises silently; reset clears both collections and synchronises participants. Reset does not immediately rediscover areas under stationary players; move, rejoin or change accepted context to re-evaluate.

Natural first discovery sends concise translated chat lines to all connected participants, including teachers: `[Human Journey] New region discovered: <label>` and/or `New area discovered: <label>`. Both may appear on the same transition. Re-entry is silent; outsiders receive neither snapshots nor announcements. English and Ukrainian message resources are supplied. No rewards, titles, sounds or personal discoveries are added.

## Manual acceptance checklist — not performed by Core

1. Install matching Core on a test server and clients. Join with two correctly tagged students, one teacher and one outsider. Confirm HUD labels for students/teacher only; inspect placement beside the minimap at different UI scales and F1.
2. Student enters an undiscovered macro+local: both chat lines appear once for participants; the second student shares the discovery. Re-entry produces no extra messages. Test macro-only coverage and no-geography hide.
3. Teacher explores new areas: HUD changes but discovery totals stay unchanged. Add both teacher/student tags and confirm teacher exclusion. Participant-only and outsider exploration must not add discoveries.
4. Inspect `/hj geography status` and lists; mark a valid ID; reject an invalid ID; reset then move to rediscover. Confirm permissions prevent student administration.
5. Log out/in, save/restart test server and `/reload`; IDs remain shared. Temporarily remove a test geography definition: its stored ID is reported stale without crashing. Restore it and verify it remains discovered.
6. Tune client enabled/offset/scale/opacity. Check long labels and macro-only panel size. Verify existing NPC geography priority/naming/economy, story content, progression/WE CAN STAY/Agriculture, safe wandering, fishing and water/temperature remain unchanged.

## Validation limits

Automated tests use synthetic geography and mocked players, not a Minecraft gameplay world. A no-world Forge data run exercises real SavedData NBT compression/codec and network codecs. Client GUI appearance, real player movement, loaded-world persistence timing and actual dedicated-server gameplay startup remain manual acceptance checks. Discovery uses twice-per-second sampling, so a very brief crossing between samples may be missed. Storage flush follows native world saving, not synchronous disk writes for every discovery. A crash before the next world save can lose recent discoveries just as it can other unsaved world state.
