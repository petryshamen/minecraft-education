# Human Journey Core 0.7.0 — NPC economy engine

Core supplies schemas, matching, solver, native offer integration and bounded behaviour. Content authors supply commodities, values, roles, families, Era availability and complete environmental profiles. **No production commodity catalog, prices, real regional profiles or wood mappings are supplied.** Existing Era progression, Story Test, Traveller and WE CAN STAY content are unchanged. Fishing, water, temperature and audio mechanics remain isolated.

## Install and integration boundary

Use Minecraft Java 1.21.8, Forge 58.1.18, exact Easy NPC 6.5.2 and the existing mod stack. Replace the Core JAR manually with `human_journey_core-0.7.0.jar`; keep one Core version and matching server/client Core. Core is required on clients too because native merchant cost matching is extended there as well as server-side. The native sanitizer hook and movement registration are gated to the exact optional Easy NPC version; absent/unsupported dependencies still produce the existing spawn diagnostic while unrelated Core systems remain available. This gate is structurally tested; a dependency-absent game launch is not claimed. Existing static templates and old archetypes remain valid and keep their existing trade/movement semantics. Nothing automatically installs, deploys or edits the main datapack.

New data paths (all JSON, schema version 1):

| Path | Purpose |
|---|---|
| `data/<namespace>/npc_commodity/<id>.json` | One commodity; resource path defines ID |
| `data/<namespace>/npc_family/<id>.json` | One interchangeable item family |
| `data/<namespace>/npc_geography/<file>.json` | Existing macro/local polygons, extended with profile |
| `data/<namespace>/npc_archetype/<role>.json` | Existing base/era/macro/local/season layers, extended with economy settings |
| `data/human_journey/core/economy_vocabulary.json` | Optional additions to environmental vocabulary |

The authoritative era resolver remains the existing `core/npc_context.json`; no new era/progression convention exists. Commodity Era and role references must exist and roles must be available in that Era. Bare role/family references mean `human_journey:<id>`; explicit namespaces are supported. Highest datapack priority selects each file. Context, geography, archetypes, vocabulary, families and commodities validate and publish **as one atomic set**. Any invalid file preserves the previous successful context/economy set and revision. Static/story `npc/` templates keep their independent existing loader.

## Opt in an economic archetype

Inside any supported content layer (base, era.content, regional/local/season patch):

```json
"economy": {
  "enabled": true,
  "wandering": true,
  "look_at_players": true,
  "wander_radius": 10,
  "family_outputs": {}
}
```

The existing layer merge rules remain: scalar replacement, recursive object merge, complete array replacement. Economy is disabled when absent. When enabled, dynamically generated offers **replace** that archetype's static trade array; they are not appended. `trading.reset_minutes` still configures native timed restocking. Static/story templates cannot opt into dynamic economy and continue using their configured arrays, fixed identities/content and independent `behavior.stationary`. A dynamic NPC's AI movement/look settings come from economy; `behavior.invulnerable` remains independent. There is no era inference, fixed role catalog, refresh command or market system.

## Commodity format

The following is a **synthetic authoring fixture**, not a production price or catalog entry:

```json
{
  "schema_version": 1,
  "era": "era_test",
  "value": 5,
  "rarity": "test",
  "maxUses": 7,
  "sellers": ["test_trader"],
  "buyers": [],
  "item": "minecraft:flint",
  "sellAvailability": {
    "temperature": "any", "moisture": "any", "vegetation": "any",
    "water": ["river"], "relief": "any", "special": "any"
  },
  "components": {"minecraft:custom_name": "Synthetic Output"}
}
```

`id` may be included but must match the namespaced resource path. `era`, `value`, `rarity`, `maxUses`, both explicit role arrays, exactly one of `item`/`family`, and complete `sellAvailability` are required. Empty sellers = input-only; empty buyers = output-only; both empty is invalid. Rarity is a required free metadata label and never affects selection/probability. Every era/role reference is validated. `maxUses` is an integer 1..1,000,000 and applies per generated offer, per NPC. No discounts are added.

Value is abstract worth, **not quantity**. Decimal values are represented exactly as integer millionths: positive, at most six decimal places, maximum 1,000,000,000. No float-price rounding or random price multipliers occur. Optional `components` are native Minecraft **output** components, validated with the current reload's registry provider through ItemStack.CODEC. They are not input component restrictions. Unknown item/component IDs, invalid component combinations or illegal stack sizes reject reload. All family output variants are checked with the supplied components. Concrete/family input matching follows native item identity semantics (extra player-item components are not required/removed by the cost definition).

## Environmental profiles on existing geography

Add `profile` to an existing macro or local definition without adding/changing polygons:

```json
"profile": {
  "temperature": ["temperate"], "moisture": ["dry"],
  "vegetation": ["grassland"], "water": ["inland", "river"],
  "relief": ["plain"], "special": []
}
```

All six dimensions are required. Each uses a unique string array; the first five must be nonempty, special may be empty. If a local area resolves, use its complete profile; otherwise use the macro profile. **Never merge local and macro**, including special tags. When commodities are loaded, configured geographic areas require complete profiles. Old non-economic geography without profiles remains compatible. With no matching geographic area, the effective profile contains empty sets: `any` constraints pass, restricted constraints fail. No biome is used and no substitute goods are invented.

Built-in vocabulary:

| Dimension | Values |
|---|---|
| temperature | polar, cold, cool, temperate, warm, hot |
| moisture | arid, dry, moderate, humid, wet |
| vegetation | barren, tundra, grassland, shrubland, woodland, forest, rainforest, wetland |
| water | inland, river, lake, coast, fjord, ocean |
| relief | lowland, plain, hills, upland, mountain, plateau, valley, island |
| special | cave_access, deep_water_access, mountain_access |

Unknown values reject reload. Content can extend these sets without a Java rebuild:

```json
{"schema_version":1,"additions":{"special":["test_access"]}}
```

Only the same six dimensions are allowed; extension tokens use 1..64 lowercase letters/digits/underscores, excluding `any`.

Each sellAvailability dimension is either the exact string `any` or a nonempty array from that vocabulary. All six dimensions are ANDed; inside one dimension any overlap passes. Thus water `[inland, river]` matches `[river, lake]`. Special uses the same intersection rule. **Only selling is geographically filtered.** Payments come from every same-Era commodity naming the role in buyers, regardless of that commodity's sellAvailability.

## Families and concrete resolution

Synthetic family:

```json
{
  "schema_version": 1,
  "members": ["minecraft:oak_log", "minecraft:spruce_log", "minecraft:birch_log"],
  "default_member": "minecraft:oak_log",
  "output_overrides": [
    {"priority": 1, "when": {"local_area": "test_local"}, "item": "minecraft:birch_log"}
  ]
}
```

Members must be distinct valid items; 1..256 members are supported. Default is the specified member or first member in data order. Output resolution: archetype `economy.family_outputs[family ID]` explicit item first; otherwise highest-priority matching family rule; otherwise default. `when` supports AND conditions for `era`, `macro_region`, `local_area`, `season`; all referenced IDs are checked. Same-priority rule conditions that can overlap are rejected conservatively: use distinct priorities for overlapping/general-specific rules. No real regional mapping is inferred.

Player payments accept **any frozen member**, not only the displayed representative. Each native ItemCost carries a private serialized custom-data predicate containing that offer's member IDs. A scoped ItemCost.test hook recognizes only Core-marked costs; unmarked costs retain vanilla behavior. An isolated Easy NPC sanitizer hook preserves this predicate when native single-input costs are reconstructed. Native payment-slot autofill already calls ItemCost.test, so no merchant menu replacement is needed. One slot still holds one concrete item variant; different variants cannot be combined into one stack. Two slots can accept different valid family variants.

Family membership is saved **inside the offer**, not looked up through the current catalog/tag. Changing a family file later does not change existing NPC payments. Concrete-item payments use the same frozen mechanism with exactly one member. Internal markers stay on cost display/predicates and are not added to received player goods or consumed payment items. Display shows a representative item, not a cycling family icon; actual acceptance is broader. Keep the private `human_journey:payment_v1` marker reserved for Core.

## Generation and barter solver

At spawn, resolve context and inspect every commodity in the current Era where the role is a seller. Apply sellAvailability and attempt one offer for every passing output. No rarity roll, random catalog reduction, arbitrary generated-offer cap or fallback goods exist. Zero matching sellers is valid. An unsolvable output is skipped with a diagnostic rather than an illegal trade.

For each output, search all legal output quantities and payment quantities in this strict order:

1. Exact one-slot payment.
2. Exact two-slot payment (same commodity may occupy both slots).
3. Within ±10% using one slot.
4. Within ±10% using two slots.

Tolerance is `abs(input_value - output_value) * 10 <= output_value`. Within a priority group compare absolute error, total item count including output, fewer distinct input kinds, fewer slots, then NPC-favourable payment on an otherwise equal error; stable IDs/stack split resolve remaining ties. Generation is deterministic, which is permitted; prices are never randomized. The solver indexes complementary payment values instead of brute-forcing every pair of quantities. It excludes the currently offered commodity as its own payment to avoid no-op/XP-loop trades; that commodity remains eligible as payment for other outputs.

Synthetic exact example: value 2 input versus value 5 output yields 5 inputs → 2 outputs. Stack limits are read from actual Minecraft ItemStacks, including configured output components. Input-family count uses the lowest member stack limit so every accepted member fits. Minecraft 1.21.8 legal component stack sizes 1..99 are supported; ordinary defaults are not assumed to be 64. A non-stackable item cannot have count above 1 per slot, hence two slots can hold at most two. Larger stackable payments split legally (e.g. 64+36), never a stack of 100. Output search is bounded by the resolved output's actual maxStackSize. Oversized/nonpositive/invalid definitions fail clearly.

## Persistence, stock and XP

Only the spawn command calls generation. Native MerchantOffers contain resolved output item/components, concrete counts, frozen family membership, initial uses 0, commodity maxUses, xp 10, rewardExp true, zero price multiplier/demand. Easy NPC owns the merchant UI, shared per-NPC stock, interaction, reset timer and normal entity persistence. No custom pricing/trading engine or stock persistence replacement exists. The spawn sets a fresh entity/preset identity through the existing adapter.

Native Easy NPC trade-data save/load hooks and native NBT/network codecs preserve the generated trade identities. Reopening the UI, restart, world reload, later geography/season or data reload never calls Core generation for an existing NPC. Core's home/context/count metadata lives in Forge's persisted entity data. A newly spawned NPC gets a new offer object set; it cannot share mutable offers with another NPC.

**Accepted native stock limitation:** Easy NPC 6.5.2 stores/synchronizes offers separately from its runtime merchant copy; exhausted runtime uses may restore after entity/world/server reload, as observed previously. This release preserves trade identities and family predicates but does not replace that stock behavior. `reset_minutes=0` means no timed reset, not guaranteed durable exhaustion. Native xp=10 is an offer parameter, not a fixed ten-orb reward: Easy NPC's existing reward method uses `3 + nextInt(offer.xp)` when reward is enabled. No custom XP reward is implemented. Native Easy NPC refuses to open its trading UI for an empty offer set; a valid zero-output NPC still has its dialogue and no invented trades.

## Bounded generic behaviour

For dynamic economic NPCs only, AI remains enabled. Home is the actual safe spawn block, persisted with radius and look/wander settings. Defaults: radius 10 (configurable integer 1..64), look enabled, wander enabled, native movement speed .6. Existing stationary/story settings remain unchanged.

Core installs look-at-player and look-around goals plus one bounded home goal. Native unrestricted MOVE/LOOK goals are removed only on Core-marked dynamic NPCs, including after native objective refresh; generic objective data is empty. Wander destinations and every planned path node must lie inside the saved home sphere. NPCs do not move the home as they walk. A displaced NPC attempts a native path back toward home. No teleportation, custom pathfinder or schedules are added; impassable obstacles can prevent return. Trading pauses navigation. Disabling wandering still permits return to the fixed home; disabling looking is independent. Invulnerability remains the existing behavior setting. Saved home/settings reinstall goals on the first server living tick after load, without reading current economy/context or regenerating offers.

## Geography scaling and bounded validation

The old 64-area/file/patch restrictions are replaced by: 512 macro regions, 4096 local areas, 1024 geography files, 256 archetypes, 4096 region/local overrides per era layer, 1,048,576 fully validated context variants. Era count remains 32 and polygon vertices remain 3..256. Each context/economy resource is at most 256 KiB characters and the whole context/economy load at most 16,777,216 characters. Split large worldwide datasets across files. Exceeding limits rejects the entire load; nothing is silently truncated. Runtime geography is an occasional bounded scan of existing polygons. Tests include 600 local areas/patches and more than 500 separate geography resources, plus native network round trips above 64 offers.

## Diagnostics and manual acceptance

Permission level 2 required:

```text
/hj npc context
/hj npc status
/hj npc economy <role>
/hj npc economy_match <commodity>
```

Context adds the selected profile. Status reports catalog/family counts and last-valid/rejected reload. Economy is a read-only **new-spawn preview** of resolved context/profile, matching seller count, generated count and a bounded list of skipped reasons. It does not inspect or rewrite existing NPCs. economy_match reports all six per-dimension matches and commodity/current Era; the buy side is never geography-filtered. Existing spawn/Tab/static/story commands remain available.

`HumanJourney-Economy-Synthetic-0.7.0.zip` is a separate **synthetic test fixture pack**, not a production HJ integration. It uses a private test objective and `era_test`, and should be enabled only in a disposable world with other NPC/context packs disabled. It does not contain load/tick functions or write scores. If testing this standalone pack manually, create objective `hj_economy_test` (dummy) and set holder `#era` to 0 there; this is test setup, not a change to real hj_civ progression. It defines Test Trader, TEST_LOG and an abstract output/payment using Minecraft items. Its coordinates 0..100 are arbitrary test geometry, not real geography. HJ Developer should integrate the accepted schema separately; do not install this over the real production NPC catalog.

Manual checks (not performed by Core tooling):

1. Back up a disposable test world, manually install one Core 0.7 JAR and matching Easy NPC 6.5.2; enable only the synthetic NPC pack for this test and initialize its private score. Stand inside synthetic river geometry, reload, run context/status/economy preview, spawn `test_trader`.
2. Check generic name/dialogue and trade UI. The payment should accept each TEST_LOG member with the same count; nonmembers should fail. Trade XP should follow Easy NPC's native behavior. Verify stock is shared across two players and independent across two spawned traders.
3. Check native autofill, manual slot placement, repeated trades and non-stackable/two-slot fixtures added through test data. Check components and output quantity.
4. Move the NPC or change context/data, reopen trade UI, save/unload/restart. Verify exact offer identity remains; separately observe the already accepted stock reload limitation. Changing family data should not alter old NPC membership.
5. Observe look behaviour and bounded wandering around its original safe spawn; displace it onto reachable ground outside the radius and check return attempts. Check saved home after restart, invulnerability and independent story/static behavior.
6. Edit a profile, commodity or family and reload: only new spawns should use new data. Invalid vocabulary, incomplete profiles, roles/Eras/items, family members, component data or values should reject atomically and retain last valid data. Test no valid local output: no fallback goods.
7. Restore the normal NPC data after the isolated test; recheck original Era/static/story commands and WE CAN STAY, fishing, temperature, water/salt/shoreline/purifier/audio. Core does not change any of those production data files.

Automated checks use synthetic data only. Native data proxy save/load and no-world codec/Mixin checks do not claim in-game UI, appearance, navigation or full world/server persistence gameplay testing.
