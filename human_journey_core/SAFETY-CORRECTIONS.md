# World Wrap Safety Corrections — 0.18.1

Status: implementation under automated validation; manual acceptance and production enablement are not claimed.

Independent per-world server configuration (restart required):
```toml
[world_wrap]
enabled = false
log_crossings = true
arrival_inset_blocks = 0
[earth_protection]
enabled = false
```

Both default off, including upgrades. Protection is existing-FULL-only throughout the opted-in Overworld: outside requests, missing/interior incomplete chunks and generation steps are denied; existing complete chunks remain editable/savable. It is not appropriate for a blank/generated world. Native storage supplies bounded asynchronous existing-data proofs; temporary wrap tickets are independently owned and reference-counted. No existing chunks are deleted or intentionally replaced. Outside reads are unavailable air/empty fluid and outside writes rejected; no placeholder chunk is registered or saved.

Wrapping remains inactive unless protection has passed its exact-runtime hook/startup checks. Protection without wrapping contains players and riding groups at the Earth edges. It does not wrap north/south. Movement packets are checked after main-thread dispatch and server-side Entity.move is clipped as well. All riders are retained; no dismount/recreate. Outside direct teleports use a validated inside anchor or existing-only canonical recovery. If no safe recovery exists, the server stops rather than allowing outside exploration or guessing a landing position.

Normal exact desired seam destinations are preferred with arrival_inset_blocks=0. Missing edge dependencies cause a safe hold. For separate manual testing only, setting arrival_inset_blocks=32 allows an explicitly reported inward adjustment; this is a safety fallback and is NOT exact 24576-block displacement. Y/Z, native movement snapshots, orientation, UUIDs and inventories are retained on successful transfer. Holding stops outward X movement, not the survival simulation. Diagnostics show canonical versus actual arrival and adjustment. The whole-group footprint and native loading dependency radius are validated before tickets or transfer.

Limits: at most4 pending groups,16 entities/group,25 target chunks/81 dependency chunks/job,200-tick deadline;32 active disk reads/1024 queued/4096 cached proofs per map;10-second per-read timeout. Temporary registered tickets last241ticks maximum, are nonpersistent and removed on success/failure/cancel/stop. Mount/source changes cancel preparation. No waiter intentionally remains unresolved. A missing dependency may prevent an exact crossing; the group remains inside. Legacy players outside without any safe recovery may require restoring/repositioning in an expendable copy before enabling protection.

`/hj wrap status` (admin level2): protection requested/active/ready, effective wrap availability, pending work/tickets and LOADING / HELD / CROSSED / FAILED with crossed=true/false, actual/source/target, canonical destination and any fallback adjustment. Xaero visual wrapping is absent and unchanged. No production datapack/geography/progression migration.

The isolated CI harness creates a NEW flat fixture world under build/safety-server, prepares known pools/chunks, shuts it down, enables protection only there, then starts a real dedicated Forge server to exercise native chunk scheduling/storage/tickets and actual vehicles with simulated player connections. It compares region chunk-entry inventories before/after prohibited requests. The harness is excluded from the runtime JAR. Those tests are not real-client gameplay acceptance. See the release report for executed results and remaining limits.

Manual checklist: use an expendable Earth copy. With both settings off, vanilla behavior remains. Enable protection only: test foot/boat groups cannot leave canonical bounds and outside fluid/block queries create no new chunk records. Enable wrapping with exact target first: solo, two-seat boat and chest boat, both directions, fractions, blocked/missing/corrupt target, immediate return and concurrent riders; every failure must hold all members. If exact edge dependencies are absent, test the explicitly configured32-block inward fallback and inspect reported adjustment. Test horse/donkey and inventories, movement/health/TAN state, logout/rejoin/restart, packet latency and mount changes; no duplication or lost items. Check no new outside records using before/after chunk-header inventories, not file size alone. Preserve existing generated terrain; no cleanup is performed. Recheck advancements, geography/discovery/teacher exclusion, NPC/economy and survival. Xaero remains non-repeating. Any data loss, uncontrolled boundary escape, unexpected generation, deadlock or server failure blocks production acceptance.
