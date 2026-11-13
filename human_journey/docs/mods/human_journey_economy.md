# Human Journey Era 0 dynamic economy

Economy content from 0.4.1 remains unchanged and is compatible with installed Core 0.8.0 and Easy NPC 6.5.2. Core owns atomic reload, family input matching, legal barter quantities, offer persistence/XP and bounded movement. This datapack owns the approved catalog, profiles and family output rules. No Java algorithm or mod JAR is changed.

- `data/human_journey/npc_commodity/*.json`: 50 approved Era 0 commodities. Values, rarity metadata, H/F/G bindings and seller maxUses are copied from the task. Rarity never controls appearance. Core requires maxUses even for input-only goods; those use inert **1**, with empty sellers. All approved seller maxima stay 3/2/1 as specified.
- `data/human_journey/npc_family/{wood_log,leaves,boat,wool}.json`: lowercase resource IDs for the approved WOOD_LOG/LEAVES/BOAT/WOOL names. Members are 9 normal Overworld logs, 9 corresponding leaves, 9 normal boats plus bamboo raft, all 16 wool colours. No stripped wood, stems, chest boats or chest rafts. White wool remains the initial wool output. Brown/red mushrooms are independent item commodities, each value 4, Limited, maxUses 2, Forager seller, no runtime Traveller binding and unrestricted sell geography.
- `npc_geography/*.json`: 19 macros and 550 Europe/Africa/Near East/Arabia locals have complete six-dimension profiles. Local replaces macro, never merges; gaps use the existing macro fallback. Empty special means none; Pontic water is inland plus river. Geography payloads and terrain limitations are documented in [the geography note](human_journey_geography.md); catalog values, eligibility rules and family output rules are unchanged.
- The three generic archetypes opt in through `base.economy`: enabled=true, wandering=true, look_at_players=true, wander_radius=10, family_outputs={}. Invulnerability is retained, stationary=false, reset_minutes=0, and obsolete static offer arrays are removed. Dialogue and the trade button remain. Static/story templates are untouched. Native generated offers use initial uses 0, commodity maxUses, xp=10 and rewardExp=true. xp=10 is a native parameter, not a fixed ten-point reward.

## Leaves output mapping

Core family rules accept era/macro/local/season context rather than profile predicates. Therefore this catalog derives explicit context rules from each approved complete profile: macro priority 100; local priority 200; both require era_0. Local rules override macro output and can later be edited independently. No per-role regional trade arrays are created.

1. warm/hot + humid/wet + coast/ocean → mangrove_leaves
2. warm/hot + humid/wet → jungle_leaves
3. warm/hot + dry/moderate → acacia_leaves
4. temperate → oak_leaves
5. cool → birch_leaves
6. cold → spruce_leaves

Polar/arid profiles fail Leaves sell availability, so no leaf offer reaches runtime output resolution there. The valid default oak member is only a family fallback/validation value; it does not create missing-geography goods. Warm/dry Crete and Azov produce acacia; cold Finnish Lakeland/Alps produce spruce. Both brown and red mushroom outputs are generated independently; neither resolves through a family.

## Integration conventions and limits

Traveller T metadata is omitted at runtime: Core validates seller/buyer references against real archetypes available in that Era. Natural Wandering Trader remains separate and unchanged. No fake Traveller archetype is introduced. The runtime keeps every mandatory H/F/G binding; buying is never geography-filtered.

Every matching seller commodity is requested from Core, without probability or a manual offer cap. Unsolvable goods are reported, never deleted or repriced. Existing NPCs retain their spawn-time concrete offers/membership/behaviour; spawn new generic NPCs to test this catalog. Data reloads do not refresh existing NPCs. Native exhausted-stock restoration after reload remains accepted. Empty economic NPCs are valid, but Easy NPC cannot open an empty trade screen; no filler is added. Native pathfinding can be blocked by terrain.

After manual `/reload`, inspect `/hj npc status`, `/hj npc context`, `/hj npc economy hunter|fisher|forager` and `/hj npc economy_match <commodity>`. Commands are read-only previews and must not rewrite progression. Vanilla reload success alone does not prove atomic Core policy acceptance. Automated standalone parser/solver checks do not prove live reload, UI, movement, XP delivery or world persistence; gameplay checks belong to the user.

Validation is performed against the unchanged accepted Core 0.7.1 pure Java parsers/composition/diversity generator in a temporary standalone checker. No mod JAR is rebuilt or loaded on its classpath, and Minecraft is not launched. Item IDs are checked against installed 1.21.8 registration bytecode; current input stack defaults are retained. Live reload, native merchant UI, XP, navigation and world persistence remain user tests.

## Core 0.7.1 compatibility / mushroom split

No production schema migration is needed. The generic mushroom commodity and production family are removed. Other four families and every other commodity/profile are unchanged. Per-NPC random payment selection and buyer repetition avoidance come entirely from Core; values and ±10% tolerance are not changed. Both mushroom outputs appear in the same new Era 0 Forager, with independent native stock. Existing NPCs retain their saved offers and require a new spawn to use the split. Native stock restoration after reload remains accepted.
