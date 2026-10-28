# Water treatment — Core 0.3.0

Exact target: Minecraft 1.21.8, Forge 58.1.18, TAN 21.8.0.2, GlitchCore 21.8.0.0. The existing Core temperature policy/adapter is unchanged. No salt water, custom container items, purifier redesign or rain-collector changes are included.

## Manual setup and gameplay

Stop/back up the TEST game/server. Replace the old Core JAR with `human_journey_core-0.3.0.jar`, keeping one Core version. Clients and server need the same mod version/dependency stack. Install the companion water datapack on the server/test world, with **higher priority than the old Human Journey datapack**, then run `/reload`. Keep your existing temperature datapack/settings: the water companion contains no temperature policy.

The companion is also an example you can integrate into your own Human Journey datapack. Normal balancing belongs in its JSON, not Java. It contains one migration override for the old `human_journey:water/convert_one` function: that old function cleared potion stacks and replaced them with dirty bottles, which would undo the NORMAL water produced by filtration. The override preserves the function ID as a no-op. Other progression, NPC and advancement/function IDs are untouched. No files are installed automatically.

- **Filter:** hold one DIRTY bottle/canteen in the main hand, the configured filter material in the off hand, then sneak-right-click. The filter count is consumed in Survival/Adventure. The same container becomes NORMAL. Creative retains materials as usual. Default materials: sand or charcoal, one item per container.
- **Furnace boil:** manually put a NORMAL bottle/canteen in an ordinary furnace input slot and supply regular fuel. Default duration: 500 ticks / 25 seconds. The output is PURIFIED with the same remaining drinks and data. No new recipe-book entry is needed; normal fuel/remainders, furnace slots, blocking/full-output behavior and saved furnace inventory remain vanilla. Shift-click recipe-book placement is not required; place the item directly in the input slot. Smoker/blast furnace are not new boiling methods.
- **Campfire boil:** right-click a LIT campfire with a NORMAL bottle/canteen to place one into a free real campfire slot. Mixed food/water slots are supported. After 500 lit ticks by default, the same-state PURIFIED container drops above the campfire. Sneak-right-click with an empty main hand to retrieve a water container early. Unlit campfires do not boil. Their ordinary cooldown behavior applies. Stored water and timers use the campfire's own persistence; breaking it returns its stored items normally.
- DIRTY water is refused by both boiling paths. PURIFIED water and empty canteens cannot be refilled by treatment. Effect potions are not normal water and are protected from broad legacy furnace water ingredients.

The supported canteen tiers are leather, copper, iron, gold, diamond and netherite. TAN represents quality by item ID and remaining drinks by damage against capacity. Core uses the input's component patch when replacing only its item type and refuses a canteen conversion if damage or maximum capacity changes. Custom name, enchantments, custom data and other component overrides travel with the stack. Count is one input container to one output. No static full-canteen result is used.

**Enchanted canteen compatibility:** TAN 21.8.0.2 auto-replaces an enchanted dirty/normal canteen with a purified one during inventory tick and only copies some data. Core suppresses that auto-replacement for these managed canteens so it cannot bypass the treatment chain or erase the name/custom data. It does not remove enchantments. TAN's explicit refill/emptying mechanics and the purifier/rain collector remain their own systems.

Old crafting recipes that change these water qualities are checked before a result can be taken. Only a valid configured DIRTY + filter → NORMAL result is admitted, and its result is rebuilt dynamically from the input. NORMAL + charcoal → PURIFIED and DIRTY → PURIFIED crafting shortcuts are blocked. This prevents the old Human Journey recipes coexisting as a bypass. Actual TAN Water Purifier recipes/block behavior and Rain Collector are not intercepted.

## Drinking

The same server-side hazard policy is used for water bottles, filled TAN canteens and TAN's actual direct-hand-drinking packet. The latter uses TAN's own biome water classification; it is not left at TAN's separate fixed probability. Core keeps TAN thirst/hydration restoration and the normal container-use/drink consumption behavior, replacing only the water penalty path. Other drinks/potions are not modified. Disabling TAN thirst disables these drinking penalties.

- NORMAL: initially 60% chance of TAN Thirst, 600 ticks.
- DIRTY: initially 100% TAN Thirst, 600 ticks, plus Poison I for 160 ticks (8 seconds).
- PURIFIED: no new unsafe effects. It does not cure/remove an existing unrelated Thirst/Poison effect.

Direct drinking additionally validates server-side reach, line of sight, loaded source water, interaction permission, crouching/empty main hand, thirst eligibility and cooldown. The existing TAN client action is retained. The server cooldown can be extended by policy; reducing it cannot make TAN's client send requests faster than its own action cooldown.

## JSON interface

Exact resource: `data/human_journey/core/water.json`. See the complete example in `example-water-datapack/`.

```json
{
  "schema_version": 1,
  "normal": { "unsafe_chance": 0.60, "thirst_duration_ticks": 600 },
  "dirty": {
    "unsafe_chance": 1.0,
    "thirst_duration_ticks": 600,
    "illness": { "enabled": true, "effect": "minecraft:poison", "duration_ticks": 160, "amplifier": 0 }
  },
  "filtration": {
    "enabled": true,
    "materials": ["minecraft:sand", "minecraft:charcoal"],
    "material_count": 1,
    "from": "dirty",
    "to": "normal"
  },
  "boiling": {
    "furnace_enabled": true,
    "campfire_enabled": true,
    "furnace_ticks": 500,
    "campfire_ticks": 500,
    "from": "normal",
    "to": "purified"
  },
  "hand_drinking": { "cooldown_ticks": 60 }
}
```

All fields may be omitted to use safe fallback values. Highest-priority resource wins as one file. `/reload` publishes one immutable policy; file removal restores defaults. Invalid types, unknown fields, unsupported schema, unknown item/effect IDs and invalid ranges reject the whole new water policy and retain the last valid one. Initial invalid data leaves fallback active. Errors are reported in the server log and status; a successful vanilla `/reload` alone does not prove this custom policy was accepted. Water reload state is independent of temperature reload state and is reset on server shutdown.

| Setting | Bounds / meaning |
| --- | --- |
| Unsafe chance | Finite number 0–1; 0 = never, 1 = always |
| Effect duration | Integer ticks 0–72000; 0 disables that application |
| Dirty illness effect | Registered effect ID; defaults to `minecraft:poison` |
| Amplifier | Integer 0–4; 0 means level I |
| Filter materials | 1–64 distinct registered item IDs; no air |
| Filter count | Integer 1–64 consumed per container (not per remaining sip) |
| Boiling duration | Integer 1–72000 ticks for each method independently |
| Hand cooldown | Integer 1–1200 server ticks |
| Transitions | Schema 1 supports exactly dirty→normal filtration and normal→purified boiling; incompatible routes are rejected |

Numbers, effects, enabled methods and filter materials can change without a new JAR. New mechanical capabilities/quality types still require an engine update. Resource-pack format in the JAR is 64; the external datapack format is 81.

## Debug and acceptance testing

`/hjcore status [player]` (permission level 2) adds three compact water lines: policy source, last successful revision/reload outcome, chances, illness settings and enabled processes. `/hjcore water` reports your held supported container, quality/tier, remaining capacity and name. Both commands only read state.

Before classroom use, test on a dedicated TEST server with matching clients:

1. For every tier, test full, partially used and one-drink-left canteens; use named, enchanted and custom-data-bearing examples. Confirm filter/boil changes quality only. Compare `/hjcore water` before and after. Empty and purified containers must not yield extra water.
2. Test sand and charcoal separately, correct count consumption, missing/insufficient filter, filter disable and material changes after reload. Old crafting filters must preserve components; old charcoal purification must have no takeable output.
3. Furnace: normal bottle/canteens, dirty refusal, no fuel, valid fuels/remainders, incompatible/full output slot, repeated batches, hopper behavior, save/restart mid-process and input removal. Do not use valuable effect potions; verify with a disposable one that they are not boiled.
4. Campfire: normal bottle/canteens placed by right-click, all four slots, mixed food, extinguish/relight, early retrieval, break campfire, chunk unload and save/restart mid-process. Confirm names, enchantments and remaining drinks on the dropped output.
5. Test NORMAL, DIRTY and PURIFIED across bottle/canteen/direct drinking, including water TAN classifies DIRTY. Set chance temporarily to 0 or 1 for deterministic checks, then restore 0.60/1.0. Check Poison duration/amplifier and that pre-existing effects are not erased.
6. Change each policy group then reload; confirm status revision and values. Try malformed JSON/unknown item/effect IDs and verify `REJECTED` with previous values retained. Remove/disable the file and verify defaults.
7. With the companion pack above the legacy datapack, confirm a filtered normal bottle remains normal in inventory; old `water/convert_one` callers must not erase its components. Recheck Teacher/Student progression independently; these mechanics do not grant advancement or technology bypasses.
8. Recheck the working temperature/water cooling, dehydration and cold behavior. Test TAN Water Purifier and Rain Collector as controls; they should retain their existing behavior.

Compilation/unit tests and the Forge datagen smoke run are recorded with the release. A datagen run is not a multiplayer gameplay test: real player interaction, direct-drinking packets and timing still require the acceptance checks above. No live mod, world or datapack is automatically installed.
