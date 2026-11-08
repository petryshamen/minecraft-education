# Core 0.7.1 — payment diversity and safe voluntary wandering

Narrow compatibility patch for Minecraft Java 1.21.8 / Forge 58.1.18 / Easy NPC 6.5.2. Existing commodity, family, geography, Era and template JSON remain valid. No production data migration is required. No catalog values, buyers/sellers, profiles, polygons, mushroom content, progression, Agriculture, WE CAN STAY, fishing or water mechanics are changed. No gameplay testing or automatic installation is performed.

## Payment grouping and selection

Every current seller commodity is still selected deterministically by Era, role and sell geography. The buyer side remains geography-independent. For each output, consider all unordered sets of one or two distinct eligible buyer commodity IDs, excluding the offered commodity itself as before. A same-good split across two slots belongs to the singleton group, not a second weighted entry. A family commodity is one commodity ID; its concrete members are not separate random candidates.

Each group is eligible if any legal quantity recipe exists: one or two input slots, positive counts within actual stack sizes, output count within actual resolved output stack size, and `abs(input value - output value) * 10 <= output value`. No price/rarity randomization, extra tolerance or seller lottery exists. One-slot exact, two-slot exact, one-slot approximate and two-slot approximate groups **all compete**; there is no global priority between them.

For a singleton, search legal total quantities through twice its stack limit and materialize one slot where possible, otherwise a legal split. For two distinct goods, search output and first-slot quantities; check the clamped floor/ceiling second-slot quantities nearest the value target. This finds the best value match for that fixed pair without weighting each raw permutation. Keep one representative recipe per group, preferring lower absolute error, fewer total items, simpler legal slots/splits, then the existing NPC-favourable tie preference and stable IDs. Base values are untouched. The old `BarterSolver.solve` remains a deterministic quantity-regression compatibility helper; runtime generation calls `candidates` and the diversity selector instead.

Generation processes seller IDs in the existing stable order and creates a fresh usage map for **one NPC only**. Group score = sum of existing usage counts over its distinct buyer commodity IDs. Score zero means every good is unused, so these groups are preferred whenever any exist. Otherwise select the minimum score. Uniformly select one group among the equally preferred groups using one `nextInt(pool size)` call. After selection increment each distinct commodity once for that offer, regardless of quantity or whether it occupies both slots. Repetition is allowed when needed, never forced uniqueness. There is no one-slot/exact preference in this selection stage.

Production spawn generation uses Java's normal ThreadLocalRandom. Generator/spawn overloads accept `RandomGenerator` for deterministic seeds or scripted index sources in tests. Equivalent new NPCs may select different payments; they need not differ every time. Diagnostic previews use their own fixed seed 0 and never consume the NPC runtime random stream. Previews are illustrative, not a promise that the next random spawn will match.

## Persistence and diagnostics

Selected quantities, concrete outputs and frozen family predicates become normal native MerchantOffers once at spawn. The existing Easy NPC/NBT/network save path is unchanged. Reloading data, reopening UI, moving, changing season/context or restarting never calls Core generation on an existing NPC. Old 0.7.0 NPCs keep their old offers; to see new diversity spawn new NPCs. Native stock restoration behaviour, maxUses and native xp=10/rewardExp remain unchanged.

Existing `/hj npc economy <role>` adds bounded pool-count/selected-group/prior-usage and final buyer-usage information for a seeded preview. Admins can inspect the saved actual generation trace with:

```text
/hj npc economy inspect <entity selector or UUID>
```

For example select the nearest Easy NPC with a standard single-entity selector. The command reads Forge entity data and does not reroll or alter the trader. Traces are saved for newly spawned NPCs only, show at most eight offers and sixteen buyer counters, and are character-bounded; full native offers are not truncated. Pre-0.7.1 NPCs report that no generation trace was stored.

## Safe land targets and paths

The existing saved safe-spawn home, radius (default 10), AI, player looking, native navigation and independent invulnerability remain. Only Core-marked economic NPC movement is affected; static/story NPCs retain their behaviour.

Voluntary random destinations must be within the home boundary, in loaded terrain, fluid-free throughout the NPC bounding box, supported by ground on which the native entity can stand, collision-free for its actual body, and classified by the exact 1.21.8 native walking evaluator as a non-dangerous land node. Water, lava and any other nonempty fluid are rejected. Unsafe support, fire/damage/powder-snow/trapdoor hazards and blocked space are rejected. Conservative full support can exclude partial block surfaces such as some stairs/slabs even if a player could use them; an idle cycle is preferable to an unsafe forced target.

A reachable native path is checked as well: every node must be bounded, on safe land and of a permitted land type. Each transition must be adjacent and have vertical difference at most one block: level, +1 and -1 pass; -2 or more fails. Diagonal flanks and inside corners are also checked conservatively to reduce native corner-cutting across water or ledges. During voluntary navigation, remaining path nodes are revalidated when the goal checks continuation, including a native replanned path.

There are at most **16 proposals per wander cycle**. No safe proposal means no movement starts in that cycle. No retry-until-success loop or custom pathfinder is introduced. No chunks are intentionally loaded by terrain validation; unloaded positions are rejected.

Recovery is evaluated **before** voluntary filtering: outside radius, unsafe current terrain (including water), or a position more than one block below saved home height triggers an attempt toward the saved home. The strict voluntary land/drop/boundary path filter does not veto that recovery path. This also conservatively returns traders that reached lower terrain instead of leaving them in a pit. Failed recovery path searches retry at most once per 20 ticks. Native navigation still must find a usable route: Core does not teleport, build stairs or guarantee escape from a sealed/deep pit. Externally applied movement, gravity, flowing fluids or terrain changes can still cause physical accidents; validated planned routes are not an invisible barrier or universal terrain AI.

## Automated checks and manual acceptance

Synthetic tests verify eligibility of all four recipe categories, unique group weighting, strict tolerance boundaries, stack splits/non-stackables/output limits, unused/least-used selection, deterministic RNG variation, safe land predicates, fluid/hazard/path rejection, ±1 transitions, diagonal/corner guards, bounded no-target behaviour and recovery separation. Existing 0.7.0 tests remain unchanged. A native data-only smoke check selects a non-default two-commodity payment (family plus concrete item), passes it through actual Easy NPC trade-data save/load hooks, and compares the exact resulting offers. This is structural persistence verification, not a gameplay/world persistence test.

Manual checklist for the user:

1. Back up a test world and replace the old Core with exactly one `human_journey_core-0.7.1.jar` on server and clients. Keep the accepted production data unchanged; restart normally. Do not install synthetic fixtures over production context data.
2. Spawn several new equivalent economic NPCs. Seller outputs should remain geography-driven; compare payment goods across NPCs and within one NPC. Repetition may remain where the legal pool is small. Inspect a role preview and an actual NPC trace if needed.
3. Check ±10% maths, one/two slots, family alternatives, non-stackables, actual stack limits, maxUses and native XP. Restart/reload/reopen and confirm exact selected payments remain; separately retain the accepted native stock-reset limitation.
4. Observe land wandering near a shore, shallow channel, lava, pit and cliff. Safe targets across unsafe paths should be rejected; a trader surrounded by unsafe proposals should wait. Check flat land and one-block rises/descents, looking and home radius.
5. Externally displace a trader into water/outside radius or onto reachable lower ground. Check that it still attempts recovery. Impassable terrain remains a native navigation limitation.
6. Recheck static/story NPCs, context/macrolocal fallback, existing family output resolution, fishing, water/temperature/audio and progression. No changes to the Forge/Crawl on Demand startup incident are included.

HJ Developer: no schema change is needed. Production mushroom changes remain a separate content task. Selection groups **commodity IDs**, so a family stays one payment commodity; splitting content into independent commodities later affects new spawns only. Existing NPCs retain frozen offers and family membership, including after catalog edits. Do not expect automatic migration or reroll.
