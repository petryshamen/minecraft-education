# Water systems — Human Journey Core 0.4.1

**Verified release:** [GitHub Actions run 40819079966](https://github.com/petryshamen/minecraft-education/actions/runs/40819079966), source `32885fe806108d87c18f4050ab1517d57f586354`. 51 tests passed, all ten water mixins loaded, and the no-world Forge state checks passed (36 existing canteen conversions plus six-tier salt/purifier checks). Final JAR SHA-256: `bfeacb3ddc1042441b34598e3c48537d1911b1b38a10c19d90b65dcd82c3158d`. All 15 temperature classes and 47 total unrelated engine classes are byte-identical to 0.4.0; root resource-pack format is 64. No gameplay testing or automatic installation was performed.

## Compatibility patch 0.4.1

- Added `classification.ocean_connection_radius` (integer 0–16, default/example 16; 0 disables the connected-water extension). Existing water packs remain valid; all earlier balance values and the Caspian polygon remain unchanged.
- The fallback classifies otherwise NORMAL water as SALT when face-connected WATER fluid reaches a direct configured ocean/salt biome tag within that many water steps. It searches in all six directions, including flowing/waterlogged water; diagonal contact and proximity across dry land are insufficient. Explicit geographic NORMAL/DIRTY/PURIFIED/SALT overrides, TAN DIRTY/PURIFIED and direct salt-tag classifications win before any search. Geographic salt polygons do not become new ocean seeds or expand into neighbors.
- Search only examines loaded chunks and never generates/loads chunks. Radius is path length, not straight-line proximity. At radius 16 a fully wet search checks at most 6,017 positions. A per-level LRU holds at most 256 origin results for fewer than 20 ticks; water/biome/chunk changes may take up to one second to refresh. Any water-policy reload outcome invalidates this cache immediately; server shutdown clears it. There is no persistent quality cache on items, so collected salt still uses its saved marker.
- Purifier intakes, container filling, hand drinking and `/hjcore water` share this classifier. No purifier/treatment/temperature algorithms were changed.
- Fixed the bottle audio delivery: Core 0.4 called `Player.playSound` server-side, which excludes that player. The isolated TAN DrinkItem hook now sends a direct Level broadcast with no excluded listener, so the drinker and nearby players receive one sound per use tick cadence. It stays server-only, skips Consumable-bearing/silent items, has no final-tick sound and does not target vanilla items or TAN canteens. Structural bytecode checks verify the old excluded-player argument and the new null argument. Audibility remains a manual gameplay check.

**Manual patch checklist:** (1) Beach-biome water connected to ocean within 16 steps should show SALT with `/hjcore water` and reject purifier filling. (2) A coastal lake separated by land stays NORMAL; opening a water connection makes it SALT after the cache refresh, closing it restores NORMAL after refresh. (3) Reload radius 0/4/16 and verify the boundary; invalid 17/negative/fractional values retain the last valid policy. (4) Listen as drinker and nearby observer to TAN bottles/juices; compare vanilla Water Bottle/canteen, including interrupted drinks and finish, for duplicates. (5) Recheck Caspian, swamp/mangrove, stored salt canteens, partial-canteen filtering/boiling, purifier capacity, Rain Collector and temperature cooling.



Target: Minecraft Java 1.21.8, Forge 58.1.18, Tough As Nails 21.8.0.2 and GlitchCore 21.8.0.0. Core implements mechanics; external datapacks own balance and geographic definitions. Existing temperature sources and settings are unchanged. No Minecraft gameplay testing was performed; automated results are recorded with the archived release.

## Install manually

Stop your TEST server/client, back up the world and replace the old Core with `human_journey_core-0.4.1.jar`; keep exactly one Core version. Use matching Core/TAN versions on server and clients. Put `HumanJourney-Water-0.4.1.zip` in the TEST world's datapacks folder and enable it above Human Journey and any older water companion. Run `/reload`, then `/hjcore water`. Keep your existing temperature pack and settings; this companion contains no temperature.json. Nothing is installed or deployed automatically.

You can instead merge the example water.json into your Human Journey datapack. A higher-priority file replaces the entire lower-priority file, rather than merging fields. Preserve your own customized Normal/Dirty/boiling values when merging. The companion retains the old `human_journey:water/convert_one` ID as a comments-only migration override: obsolete packs must not turn every vanilla potion back into dirty water. Human Journey 0.3.13 already migrated to Core treatment. No progression, advancement, quest or NPC resources are changed here.

## Classification and persistence

Every natural-water fill, hand drink and purifier intake uses the water block's position and biome:

1. First matching geographic region in JSON list order.
2. `#toughasnails:purified_water_biomes`.
3. `#toughasnails:dirty_water_biomes` (the inspected release includes swamp, mangrove_swamp and mushroom_fields).
4. Any configured salt biome tag; default `#minecraft:is_ocean`, including deep oceans through vanilla tag nesting.
5. Connected-ocean search for otherwise NORMAL water, within `classification.ocean_connection_radius` water steps.
6. NORMAL when no bounded water connection reaches a direct salt-tagged ocean block.

Classification only applies where the water fluid is actually present; land in polygons is irrelevant. Region coordinates are X/Z and compare against block centers. Boundaries are included. Dimensions must match exactly. Simple concave polygons are supported; self-intersections, duplicate vertices and zero-area polygons are rejected. At most 128 regions, 3–256 vertices each, coordinates within ±30,000,000. List order defines overlap priority.

The example contains the four supplied Caspian reference points in perimeter order (Baku → Fort-Shevchenko → Ural mouth → Mahmudabad). These define a coarse initial test polygon, not a surveyed shoreline. Edit this data after checking your Earth map. The fallback has no map-specific regions. Salt tags are resource IDs without `#`; missing/empty tags simply match no biomes. Add tag members through ordinary datapack biome tags at `data/<namespace>/tags/worldgen/biome/<name>.json`.

Salt bottles reuse the vanilla plain-water potion; canteens reuse the corresponding normal TAN tier. They carry `minecraft:custom_data` key `human_journey:water_type = "salt"`. The marker survives inventory transfers, dimension/biome changes, saves and policy reloads. The tooltip adds a clearly visible Salt Water warning while keeping custom names. Unrelated custom data, enchantments and component overrides survive fills/treatment. Salt canteens retain their tier and remaining drinks. Exhausting a salt canteen returns the same-tier empty canteen, preserving its identity and removing the salt marker. Salt sips consume one remaining drink even when enchanted.

Refilling a canteen deliberately replaces its contents and fills it, as TAN normally allows; this is distinct from treating a partially filled canteen. Source classification overrides Water Cleansing on natural fills, so enchanted canteens cannot silently turn seawater into purified water. The v0.3 protection against TAN's enchanted-inventory auto-purification stays active. Rain Collector and cauldron interactions stay native TAN. Water among aquatic plants/waterlogged source blocks also uses the salt classifier; source removal never deletes a waterlogged structure.

## Drinking

- NORMAL: example 60% TAN Thirst chance for 600 ticks.
- DIRTY: example 100% TAN Thirst for 600 ticks and Poison I for 160 ticks (8 seconds).
- PURIFIED: no new penalties; it does not erase existing effects.
- SALT: example restores 0 thirst and 0 hydration, adds 12 TAN exhaustion and Nausea I for 160 ticks. No Poison or unsafe-water Thirst roll is applied to salt. TAN subsequently processes exhaustion using its normal threshold/dehydration mechanics. Repeated salt drinking gives no useful hydration and increases dehydration pressure in Survival. Creative/Peaceful retain their normal global rules.

Salt policy uses actual thirst-bar and hydration units, not TAN's hydration multiplier. Thirst/hydration adjustments may be negative; results clamp to 0–20 thirst and 0–current thirst hydration. Bottles, canteens and the actual TAN hand-drinking packet share this logic. Ordinary non-salt drinks retain TAN's thirst/hydration tags. Disabling TAN thirst disables these water drinking consequences.

Hand drinking keeps server validation of reach, raycast/source, loaded chunk, permission, crouch, empty main hand, eligibility and cooldown. TAN's own client action still has its 60-tick minimum request cadence; lowering server cooldown cannot accelerate that client action.

## Existing treatment controls

- **Filter:** one DIRTY bottle/canteen in main hand; configured material in off hand; sneak-right-click. Example consumes one sand or charcoal. Result is NORMAL with the same fill/components. Creative retains material.
- **Furnace:** place NORMAL container directly in an ordinary furnace input and add fuel. Example 500 ticks (25 seconds). Output PURIFIED preserves fill and components. Smoker/blast furnace are not new boiling methods.
- **Campfire:** right-click a lit campfire to place NORMAL water in a real slot; example 500 lit ticks. Same-state PURIFIED container drops above it. Sneak-right-click empty main hand retrieves stored water early. Normal campfire persistence/food slots remain usable.
- DIRTY cannot boil directly to PURIFIED. SALT cannot filter, boil or use any purifier route. Empty/purified containers cannot gain water from these treatment paths. Non-water potions are protected from broad old furnace recipes.

The v0.3 dynamic crafting-result guard is retained: valid DIRTY + filter → NORMAL is reconstructed from input state; legacy NORMAL + charcoal → PURIFIED and other quality shortcuts are blocked. Six canteen tiers are supported: leather, copper, iron, gold, diamond, netherite. Treatment never resets canteen damage/fill, and rejects transformations that change maximum capacity. No fixed full-canteen recipe result is used.

## Water Purifier

Keep TAN's existing block, crafting recipe, GUI, filter slot, saved capacity and hopper storage. The machine persists; filter capacity is consumed.

Place it next to a free-standing source WATER block directly north/south/east/west/above/below. Flowing water, waterlogged structures, cauldrons and Rain Collectors are not intakes. Source types default to NORMAL or DIRTY. Salt is always rejected. Conservatively, ANY adjacent salt source blocks the intake even when a freshwater source is also adjacent; move the machine so its intake is unambiguous.

Right-click with an empty Glass Bottle or empty TAN canteen: one container fills immediately with PURIFIED water, consuming `filter_cost_per_fill` units. Open the GUI with an empty hand or a non-water item to load filters. Empty containers may also be placed in the GUI input or supplied from above; one affordable fill processes per server tick into the output. Shift-click recognizes empty containers. Filled containers, including dirty/normal/salt, are not purifier inputs; empty them through use first. The old TAN filled-water recipes cannot bypass the new intake or salt checks. Turning the policy off disables processing, rather than restoring the unsafe legacy recipes.

Capacity is read from TAN's `getFilterDuration`, not duplicated as Java balance values. With the example cost 100, native grass 100 → 1 fill, paper 200 → 2, gravel 400 → 4, sand 800 → 8, charcoal 1600 → 16. One canteen fill costs the same as one bottle fill, intentionally configurable per container, not per sip. Remaining units carry across filters; enough filters can be combined for a higher cost. No input/filter is spent if full payment is unavailable, the intake is invalid or the GUI output cannot accept the exact component-bearing result. Capacity never drains while idle. Existing saved capacity/output/input is preserved; old filled inputs stop processing and can be removed. No stored item is deleted on upgrade. The native capacity gauge remains in use (very large custom unit values can exceed its vanilla short-valued GUI synchronization; `/hjcore water` reports exact server capacity).

Rain Collector stays native TAN and continues supplying PURIFIED water. No desalinator is added.

## Full water.json example

Resource: `data/human_journey/core/water.json`. Schema 1 is extended additively, so v0.3 packs remain valid and use fallback values for new fields.

```json
{
  "schema_version": 1,
  "normal": {
    "unsafe_chance": 0.6,
    "thirst_duration_ticks": 600
  },
  "dirty": {
    "unsafe_chance": 1.0,
    "thirst_duration_ticks": 600,
    "illness": {
      "enabled": true,
      "effect": "minecraft:poison",
      "duration_ticks": 160,
      "amplifier": 0
    }
  },
  "filtration": {
    "enabled": true,
    "materials": [
      "minecraft:sand",
      "minecraft:charcoal"
    ],
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
  "hand_drinking": {
    "cooldown_ticks": 60
  },
  "salt": {
    "thirst_restored": 0,
    "hydration_restored": 0,
    "additional_exhaustion": 12,
    "nausea": {
      "enabled": true,
      "duration_ticks": 160,
      "amplifier": 0
    }
  },
  "classification": {
    "salt_biome_tags": [
      "minecraft:is_ocean"
    ],
    "regions": [
      {
        "id": "caspian_sea",
        "dimension": "minecraft:overworld",
        "water_type": "salt",
        "polygon": [
          [
            3404,
            -2758
          ],
          [
            3433,
            -3041
          ],
          [
            3537,
            -3208
          ],
          [
            3564,
            -2497
          ]
        ]
      }
    ],
    "ocean_connection_radius": 16
  },
  "purifier": {
    "enabled": true,
    "filter_cost_per_fill": 100,
    "allowed_source_types": [
      "normal",
      "dirty"
    ]
  }
}
```

All settings reload with `/reload`. A missing file uses safety defaults; all default values match this example except the map-specific regions list, which is empty by default. Invalid JSON, unknown fields, invalid types/ranges, unknown registered filter/illness IDs or invalid geometry reject the whole candidate and retain the last valid snapshot. Errors appear clearly in logs and debug commands; a vanilla reload success message alone does not prove Core accepted the policy. A rejected reload keeps the previous success timestamp/revision. Policies reset on server shutdown.

| Field/group | Accepted values / units |
| --- | --- |
| `schema_version` | 1, optional |
| `normal/dirty.unsafe_chance` | Finite 0–1 |
| `normal/dirty.thirst_duration_ticks` | Integer 0–72000; 20 ticks = 1 second |
| `dirty.illness` | Enabled, registered effect ID, duration 0–72000, amplifier 0–4 (0 = I) |
| `filtration` | Enabled; 1–64 distinct valid non-air item IDs; material_count 1–64 |
| `boiling` | Each method enabled independently; each duration integer 1–72000 |
| Treatment routes | Exactly dirty→normal filtering and normal→purified boiling; incompatible routes rejected |
| `hand_drinking.cooldown_ticks` | Integer 1–1200 |
| `salt.thirst_restored` | Integer −20–20 actual bar units |
| `salt.hydration_restored` | Finite −20–20 actual hydration units |
| `salt.additional_exhaustion` | Finite 0–1000 added TAN exhaustion |
| `salt.nausea` | Enabled, duration integer 0–72000, amplifier 0–4 |
| `classification.salt_biome_tags` | 0–64 distinct namespaced biome tag IDs, without # |
| `classification.regions` | Ordered region objects: unique id, dimension ID, water_type purified/normal/dirty/salt, polygon |
| `purifier.enabled` | Boolean; false disables both interaction and GUI processing |
| `purifier.filter_cost_per_fill` | Integer 1–1,000,000; small values recommended for native GUI gauge |
| `purifier.allowed_source_types` | Nonempty distinct list from normal/dirty/purified; SALT rejected |

## Sound compatibility evidence

The exact TAN 21.8.0.2 `DrinkItem` superclass for water bottles and juices implements use animation/duration/finish but has no `onUseTick` sound handler and no Consumable component. Its `FilledCanteenItem` separately implements sound ticks. This is an upstream omission, not Core's Finish interception: the latter only replaces post-consumption thirst logic and cannot suppress earlier sound ticks.

An isolated `DrinkSoundMixin` delegates to `compat/TanDrinkSounds` for the canteen-style normal GENERIC_DRINK cadence to TAN DrinkItem only. It emits once on the server for all listeners, including the drinker's client, and skips items if another mod supplies a Consumable component. It does not target vanilla items or TAN canteens. Automated checks verify exact upstream method absence and the applied override; audible multiplayer timing remains a manual check.

## Debugging and concise manual checklist

Permission level 2: `/hjcore status [player]` retains temperature/thirst data and a compact water summary. `/hjcore water` reports held quality/tier/remaining drinks; targeted natural-water type; salt settings/tag IDs/region count; purifier rules and targeted machine capacity; policy source/revision/last success/rejection. No command changes gameplay state.

1. Ocean vs river vs swamp/mangrove: fill bottles and all six canteen tiers, then move inland and save/rejoin. Salt tooltip/state must persist; dirty/purified tag behavior must remain correct. Verify Caspian water inside/outside the example polygon.
2. Drink SALT by bottle/canteen/hand in Survival with a depleted thirst bar: no useful restoration, exhaustion rises, short Nausea and no new Poison. Verify last salt sip returns the named/enchanted empty canteen. Compare NORMAL/DIRTY/PURIFIED penalties.
3. Salt must fail filtering, furnace, lit campfire, purifier interaction and GUI input. Recheck partially used named/enchanted DIRTY→NORMAL→PURIFIED treatment; compare remaining drinks before/after.
4. Purifier beside NORMAL and DIRTY source: empty bottle and each empty canteen fill instantly. Cost 100 + one charcoal yields 16 fills total; machine remains. Remove source, use flowing water, salt or mixed salt/fresh adjacency: no fill or capacity loss. Check GUI, incompatible/full output, hoppers and save/restart capacity.
5. Change salt values, tags, region polygon and purifier cost/enabled/source list; `/reload` and confirm debug. Invalid JSON/polygon/SALT purifier source must show rejection and retain previous settings. Existing stored salt must remain salt after reload.
6. Listen to TAN dirty/purified bottles and all juices: one normal drinking cadence. Compare vanilla Water Bottle and canteen; check nearby second client for duplicate/missing sounds.
7. Recheck rain collection remains PURIFIED and existing rapid water cooling/temperature, cold and dehydration behavior. Verify Teacher/Student progression remains unchanged; these mechanics grant no progression bypass.

Automated/structural/datagen checks are not Minecraft gameplay tests. You perform the checklist in your own test world. No client, game server or world is launched by the verification pipeline.
