# 0.18.1 notice

This page records the historical0.18.0 prototype. Use SAFETY-CORRECTIONS.md for current containment, loading, protection and configuration.

# World wrap prototype — Core 0.18.0

Status: disabled-by-default **physical prototype**, not a seamless chunk topology. Xaero horizontal repetition is **not implemented or demonstrated**. Existing Xaero controls/overlays are unchanged. No automatic installation or production-world edits.

## Inspected baseline and seam

Repository `eccefe83bf55dd5045ba190590262e0513783f68`: Core 0.17.0, newer datapack **0.5.24** (Era 0 title/spacing work) preserved. Minecraft 1.21.8, Forge 58.1.18, installed Xaero 1.39.13 SHA-256 `d0ff53b5039b61cd793fb6fe090d04f9807da9cf7f434957ff66b20c8a3cee33`; inspected read-only, never used installed mods as build inputs.

`Earth 1500/settings.xml`: BlocksPerTile=3072, TilesPerMap=45, mapOffset=0. Selection covers W180 through E135 tiles. This agrees with X=longitude×1024/15 and Z=−latitude×1024/15. Read-only Anvil header checks at the equator: chunks at X=−12288 and +12287 present, X=−12289 and +12288 absent. Thus canonical Earth X interval is **[−12288,+12288)**, period **24576**. Runtime uses modulo normalization for fast/multi-period overshoot, retaining fractional X. Latitude gate Z∈[−6144,+6144], no Z wrapping; Overworld only. This confirms coordinate/grid convention, not terrain continuity at every latitude.

Both inspected level.dat borders are far wider than Earth (~60 million blocks). Existing world generation remains normal Minecraft noise outside imported terrain. Core never changes border or generation settings. Normal Minecraft player view-distance loading can still generate outside-map chunks near a seam; this prototype does not prevent that vanilla behavior. Test only in an expendable copy.

## Physical architecture and safety

Per server tick after movement, each unique player root vehicle is considered once. Root must be a player, vanilla boat/chest boat, or vanilla AbstractHorse (including normal horse/donkey/mule family). Complete existing hierarchy ≤16 members is gathered; custom modded entity classes are refused. Sleeping/dead/removed/changing or pending-ack players defer the entire operation. Group positions, passenger links, UUIDs, inventories and all other data are retained on the same entity instances. No serialize/recreate, dismount/remount or transfer to another dimension occurs.

All members are checked before any position write: loaded full chunks for their bounding boxes plus one-block collision margin; current world border and build-height bounds; collisions; hazardous blocks/non-water fluids. Boats need water beneath the sampled footprint and an unsubmerged centre. Horses need immediate supported land. Grounded/non-flying solo players need ground/water; active creative flight/elytra may preserve air altitude. Destination authoritative server Y/Z/orientation and each entity native `getKnownMovement()` snapshot are retained; this movement value can differ from stored delta on client-controlled mounts. no upward rescue/heightmap teleport changes the requested position. Refusal is safer than changing Y or moving just one passenger.

**Loaded destinations only:** engine calls `getChunkNow`, never requests chunks, tickets or generation. An unloaded destination leaves everyone at their current source location; retry is automatic while still outside. It does not freeze the group or move it back to a guessed safe point. Obstacles, missing support or world border also defer without movement. This is a deliberate prototype limitation: unattended crossings into an unloaded opposite coast are not operationally complete. Prepare destination chunks in the test copy using a second player or temporary vanilla `/forceload`, only where terrain is known to exist. `/forceload` itself may generate missing terrain; Core does not call it. Remove test tickets afterwards.

Native bytecode audit: `Entity.teleportSameDimension` recursively calls passenger teleport; Forge-patched `ServerPlayer.teleport(TeleportTransition)` invokes cancellable onTravelToDimension even for a same-level move. Native recursion ignores a rejected passenger result, so invoking it for a group risks partial transfer. Core instead uses the narrower public `Entity.teleportSetPosition(PositionMoveRotation, emptyRelatives)` for all preflighted members in one tick-thread transaction, then the native acknowledged `connection.teleport`/`resetPosition` primitives for players and root teleport packets for riders. It never invokes cross-dimension travel hooks. Each member's absolute snapshot uses actual server position/angles and native known movement, avoiding any client-interpolation position. Runtime position exceptions trigger whole-group position rollback; packet publication happens only after all positions commit. A publication/rollback failure stops the engine until server restart and logs the exact failure. Normal tracking/join supplies canonical state; fatal mod callback/runtime failures cannot be made a universal ACID transaction.

An exact-version isolated common-side connection Mixin resets vehicle movement baselines and ignores old vehicle packets until the wrap's player teleport acknowledgement. Guard executes **after** vanilla main-thread dispatch, not on the network thread. No relaxed speed checks or generic anti-cheat bypass. Vanilla acknowledgement handling remains authoritative; severe packet loss/disconnection and third-party movement/anti-cheat mods still need manual acceptance. Generic ticks deduplicate root identity, so two riders cannot trigger two moves. No cooldown prevents an intentional immediate reverse crossing once the previous teleport is acknowledged. No independent persistent wrap ledger exists; native entity position/mount persistence is the source of truth after save/restart/reconnect/death/respawn. These live persistence paths have not been gameplay-tested.

## Isolated configuration and diagnostics

On the separate test world only, after first startup/shutdown edit `<test-world>/serverconfig/human_journey_core-world-wrap.toml`:
```toml
[world_wrap]
enabled = true
log_crossings = true
```
Normal Forge config generation defaults `enabled=false`; existing valid settings remain respected. Restart the test world for reproducible activation. No datapack migration, production config changes or client options. Exact Minecraft/Forge version gate; unsupported target remains inactive. `/hj wrap status` (permission 2) reports enablement, runtime compatibility, canonical interval, loaded-only policy, last result and visual-wrap status. Success logs include direction/source/destination Y/Z, group UUID and member count; failures/deferred groups log at most once per 200 ticks per group. No ordinary per-tick log spam.

## Xaero 1.39.13 feasibility — independent finding

Inspected exact bytecode (read-only), not just class names:
- `GuiMap.render(GuiGraphics,int,int,float)` requests coordinate-keyed leaf/branch regions, schedules `MapSaveLoad.requestLoad`, uses region X/Z for texture placement and dispatches texture renderers; mutable camera and mouse block coordinates are separate fields. `renderTexturedModalRectWithLighting3` is a low-level texture helper, not a complete periodic map provider.
- `MapProcessor.getLeveledRegion(int,int,int,int)` resolves the current dimension's layered manager; `getLeafMapRegion(int,int,int,boolean)` can construct `MapRegion`, transfer/change region detection and insert it via `LayeredRegionManager.putLeaf`. It is not a harmless image-only read API. Global coordinate remapping here could change cache/loading identity used outside fullscreen rendering.
- `LeveledRegion` stores regionX/regionZ/level plus cacheFile/worldId/dimId; camera, cursor, tile selection, branch caches, map-object/waypoint drawing and HJ overlay hover are separate paths. A texture-only translation leaves these logical interactions in the wrong coordinates.

The existing HJ overlay hooks draw discovered geometry; they are not terrain-provider hooks. No validated API was found for requesting periodic render instances of the same cached tile while preserving canonical storage keys at every zoom level. This does **not** prove repetition impossible; it means a safe complete pinned integration has not been demonstrated. No render-time replacement, repeated map tiles, cloned caches, wrapped camera or canonicalized click/waypoint behavior is shipped. Unexplored map tiles remain under unchanged Xaero behavior. No visual-wrap switch is advertised for absent functionality.

Recommended next step: separate client-only experimental branch with a rendering-instance coordinate layer (canonical tile key + visual horizontal offset), a small canonical discovered/blank tile fixture, and bounded copies at each zoom. Demonstrate GPU rendering at both seams without touching save/cache identities before integrating canonical cursor/waypoint/teleport clicks and repeating discovered HJ overlays. Evaluate waypoints and minimap bridge separately; do not enable a texture-only prototype as complete visual wrap. Existing canonical geography/discovery IDs need no changes.

## Tests and honest limits

Full prior regression suite plus new arithmetic, transactional fixture, mocked-world safety, connection/side-isolation structural, existing seam-geometry/discovery and exact Xaero bytecode feasibility checks. Forge datagen additionally loads the actual connection Mixin and exercises native position/rotation/velocity math. These are **not** an actual dedicated-server crossing, real boat/horse transfer, network acknowledgement session, saved-entity persistence or rendered repeated-map demonstration. No live world/server/client was started or modified for these checks. Actual counts/build evidence are in the release report.

Physical prototype is suitable for controlled manual acceptance with loaded known terrain, not declared production-ready. Unloaded-destination refusal and normal engine view-distance generation near the imported map edge remain significant limitations. Other dimensions, ordinary unoccupied entities, leashes/projectiles, custom mod vehicles and true across-chunk physics are outside this prototype. Existing geography/NPC/survival/Xaero code is unchanged.

## Manual acceptance (separate expendable Earth copy)

1. Keep wrap disabled first: verify normal movement, native Advancements, geography, discovery, NPC, water/juice/temperature/fishing and Xaero controls. Record coordinates/inventory/health/TAN state.
2. Enable only the test world's config. Inspect `/hj wrap status`; ensure vanilla border encloses both seam destinations. Preload existing destination and collision-margin chunks with a second teacher player.
3. Test east→west and west→east at several latitudes: fractional X, exact Y/Z, orientation and velocity; immediate intentional return after acknowledgement; stationary arrival does not oscillate. Inspect hazards/ground/water and logs.
4. Test boat with one player, two players, chest boat with named inventory; horse/donkey with equipment/inventory and rider. Check all UUIDs, rider links, inventory, survival state, and no duplicate/stranded entities. Repeat concurrent groups and watch movement-warning logs.
5. Unload/obstruct destination, test submerged boat/dry boat target, missing support and narrowed border: **whole group must stay at source** with concise reason. Do not force a partial transfer. Remove obstacles/preload known chunks, then verify retry.
6. Save/restart, reconnect while mounted, death/respawn, creative/elytra and fast travel; inspect pending-ack recovery under latency. Any separation, duplication or data loss blocks production acceptance.
7. Nether/End remain unchanged. Recheck multipart antimeridian Macro/Local/features use the canonical arrival coordinates and existing shared IDs; teacher discovery exclusion still holds.
8. Xaero must remain its current ordinary non-repeating map. Verify no new tiles/discovery records, broken hover, waypoints or moved HJ buttons. **Do not expect Google-Maps-style repetition in this build.**
