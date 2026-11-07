# Human Journey Era 0 dynamic economy

Content 0.4.0 requires the accepted Core 0.7.0 engine and Easy NPC 6.5.2. Core owns atomic reload, family input matching, legal barter quantities, offer persistence/XP and bounded movement. This datapack owns the approved catalog, profiles and family output rules. No Java algorithm or mod JAR is changed.

- `data/human_journey/npc_commodity/*.json`: 49 approved Era 0 commodities. Values, rarity metadata, H/F/G bindings and seller maxUses are copied from the task. Rarity never controls appearance. Core requires maxUses even for input-only goods; those use inert **1**, with empty sellers. All approved seller maxima stay 3/2/1 as specified.
- `data/human_journey/npc_family/{wood_log,leaves,boat,wool,mushroom}.json`: lowercase resource IDs for the approved WOOD_LOG/LEAVES/BOAT/WOOL/MUSHROOM names. Members are 9 normal Overworld logs, 9 corresponding leaves, 9 normal boats plus bamboo raft, all 16 wool colours, and brown/red mushrooms. No stripped wood, stems, chest boats or chest rafts. White wool and brown mushroom are deterministic initial outputs.
- `npc_geography/global_macros.json` and the four existing European files: all 74 areas have complete six-dimension profiles exactly as approved. Local replaces macro, never merges; no local uses the macro profile only as fallback. Empty special means none; Pontic water is inland plus river. Polygons, priorities, labels and parents are unchanged.
- The three generic archetypes opt in through `base.economy`: enabled=true, wandering=true, look_at_players=true, wander_radius=10, family_outputs={}. Invulnerability is retained, stationary=false, reset_minutes=0, and obsolete static offer arrays are removed. Dialogue and the trade button remain. Static/story templates are untouched. Native generated offers use initial uses 0, commodity maxUses, xp=10 and rewardExp=true. xp=10 is a native parameter, not a fixed ten-point reward.

## Leaves output mapping

Core family rules accept era/macro/local/season context rather than profile predicates. Therefore this catalog derives explicit context rules from each approved complete profile: macro priority 100; local priority 200; both require era_0. Local rules override macro output and can later be edited independently. No per-role regional trade arrays are created.

1. warm/hot + humid/wet + coast/ocean → mangrove_leaves
2. warm/hot + humid/wet → jungle_leaves
3. warm/hot + dry/moderate → acacia_leaves
4. temperate → oak_leaves
5. cool → birch_leaves
6. cold → spruce_leaves

Polar/arid profiles fail Leaves sell availability, so no leaf offer reaches runtime output resolution there. The valid default oak member is only a family fallback/validation value; it does not create missing-geography goods. Warm/dry Crete and Azov produce acacia; cold Finnish Lakeland/Alps produce spruce. Forager mushroom output starts brown (red remains a valid payment member).

## Integration conventions and limits

Traveller T metadata is omitted at runtime: Core validates seller/buyer references against real archetypes available in that Era. Natural Wandering Trader remains separate and unchanged. No fake Traveller archetype is introduced. The runtime keeps every mandatory H/F/G binding; buying is never geography-filtered.

Every matching seller commodity is requested from Core, without probability or a manual offer cap. Unsolvable goods are reported, never deleted or repriced. Existing NPCs retain their spawn-time concrete offers/membership/behaviour; spawn new generic NPCs to test this catalog. Data reloads do not refresh existing NPCs. Native exhausted-stock restoration after reload remains accepted. Empty economic NPCs are valid, but Easy NPC cannot open an empty trade screen; no filler is added. Native pathfinding can be blocked by terrain.

After manual `/reload`, inspect `/hj npc status`, `/hj npc context`, `/hj npc economy hunter|fisher|forager` and `/hj npc economy_match <commodity>`. Commands are read-only previews and must not rewrite progression. Vanilla reload success alone does not prove atomic Core policy acceptance. Automated standalone parser/solver checks do not prove live reload, UI, movement, XP delivery or world persistence; gameplay checks belong to the user.

Validation: the unchanged accepted Core 0.7.0 pure Java parsers/composition/solver were compiled only into a temporary standalone checker (no mod JAR, Forge launch, installed-mod classpath or world). Registry IDs were checked against installed 1.21.8 item registration bytecode; default stack limits use tools/bows/rods/boats/rafts = 1, honey bottle = 16, other catalog items = 64. TAN purified bottle = 1, confirmed in the matching TAN registration source. The full production scan passed 1,125 compositions and 225 geography/role plans (1,857 offers), covering all 30 seller/role bindings with no infeasible seller. Live reload/native UI/XP/pathfinding/persistence remain manual.
