<!-- hj-mod-inspection {"filename":"ToughAsNails-forge-1.21.8-21.8.0.2.jar","sha256":"b7a0d16be6f713a84e3e45c688fc71fcf77621f1a8d40b0d3246a7a9bfc15611","mods":[{"mod_id":"toughasnails","name":"Tough As Nails","version":"21.8.0.2","declared_version":"21.8.0.2"}]} -->
# Tough As Nails

Initial read-only inspection of the installed test-instance JAR. Re-inspect relevant resources before editing an integration if filename, version or SHA-256 changes.

## Identity

- Mod ID/version: `toughasnails` / `21.8.0.2`
- Inspected JAR: `ToughAsNails-forge-1.21.8-21.8.0.2.jar`
- SHA-256: `b7a0d16be6f713a84e3e45c688fc71fcf77621f1a8d40b0d3246a7a9bfc15611`
- Human Journey test target: Minecraft Java 1.21.8 Forge.
- Metadata source: `META-INF/mods.toml`

Dependencies declared by the installed JAR:

```json
{
  "toughasnails": [
    {
      "modId": "forge",
      "mandatory": true,
      "versionRange": "[58.0.0,)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "glitchcore",
      "mandatory": true,
      "versionRange": "[21.8.0.0,)",
      "ordering": "AFTER",
      "side": "BOTH"
    },
    {
      "modId": "sereneseasons",
      "mandatory": false,
      "versionRange": "[21.8.0.0,)",
      "ordering": "AFTER",
      "side": "BOTH"
    }
  ]
}
```

## Relevant identifiers and mechanics

- `data/toughasnails/recipe/leather_purified_water_canteen.json` explicitly references input `toughasnails:leather_water_canteen` and result `toughasnails:leather_purified_water_canteen`, using recipe type `toughasnails:water_purifying` and purifytime 200.
- `data/toughasnails/recipe/netherite_purified_water_canteen_smithing.json` explicitly references `toughasnails:diamond_purified_water_canteen` and `toughasnails:netherite_purified_water_canteen`, using `minecraft:smithing_transform`.
- These resources demonstrate separate canteen item references for water/material variants. They do not establish how remaining sips, damage, names, custom components or other state are transferred at runtime.
- Before introducing purification without the purifier, inspect the custom serializer and item behavior. A normal crafting recipe may discard important state; do not substitute one without evidence and a warning.
- Unresolved: exact runtime state layout, empty/partial/full handling, purifier transfer logic and viability of an alternative datapack-only mechanic.

## Resource inventory for follow-up

- recipes: 45 bundled members.
- tags: 65 bundled members.
- items: 49 bundled members.
- languages: 18 bundled members.

Relevant recipe members from the targeted scan:

- `data/toughasnails/recipe/gold_purified_water_canteen.json` — type `toughasnails:water_purifying`.
- `data/toughasnails/recipe/netherite_purified_water_canteen_smithing.json` — type `minecraft:smithing_transform`.
- `data/toughasnails/recipe/leather_purified_water_canteen.json` — type `toughasnails:water_purifying`.
- `data/toughasnails/recipe/gold_water_canteen.json` — type `toughasnails:water_purifying`.
- `data/toughasnails/recipe/empty_iron_canteen.json` — type `minecraft:crafting_shaped`.
- `data/toughasnails/recipe/empty_gold_canteen.json` — type `minecraft:crafting_shaped`.
- `data/toughasnails/recipe/iron_purified_water_canteen.json` — type `toughasnails:water_purifying`.
- `data/toughasnails/recipe/netherite_dirty_water_canteen_smithing.json` — type `minecraft:smithing_transform`.
- `data/toughasnails/recipe/diamond_purified_water_canteen.json` — type `toughasnails:water_purifying`.
- `data/toughasnails/recipe/empty_diamond_canteen.json` — type `minecraft:crafting_shaped`.
- `data/toughasnails/recipe/empty_leather_canteen.json` — type `minecraft:crafting_shaped`.
- `data/toughasnails/recipe/leather_water_canteen.json` — type `toughasnails:water_purifying`.

Relevant tag members:

- `data/toughasnails/tags/item/hydration/80_hydration_drinks.json`.
- `data/toughasnails/tags/item/hydration/40_hydration_drinks.json`.
- `data/toughasnails/tags/item/hydration/10_hydration_drinks.json`.
- `data/toughasnails/tags/item/enchantable/canteen.json`.
- `data/toughasnails/tags/item/poison_chance/75_poison_chance_drinks.json`.
- `data/toughasnails/tags/item/poison_chance/25_poison_chance_drinks.json`.
- `data/toughasnails/tags/item/thirst/1_thirst_drinks.json`.
- `data/toughasnails/tags/item/thirst/3_thirst_drinks.json`.
- `data/toughasnails/tags/item/thirst/5_thirst_drinks.json`.

## Human Journey restrictions and tests

- No gameplay changes or technology restrictions were introduced by this inspection. Reconcile the configured local copy with current GitHub source before integration edits.
- Preserve all advancement IDs, hj_civ fake players, technology/quest score names and NPC-called functions.
- Keep Teacher preparation bypasses distinct from Student progression restrictions.
- Before a future integration release, test fresh and progressed Students and Teacher behavior, blocked/unlocked recipes, inventory persistence after save/reload, and conflicts with existing recipes.
- For stateful items, test empty/partial/full contents, names, damage, enchantments, upgrades and stored inventories; confirm no state is silently destroyed.
- No JAR was changed, installed, renamed or executed. No trusted CFR decompiler is configured; optional decompilation has not been exercised against this mod.

## v0.3.9 water integration inspection

Identity rechecked against the installed JAR: filename, metadata version 21.8.0.2 and SHA-256 match the identity above. Mods remain read-only; the path guard is an application-level check, not an OS sandbox. No mod JAR/class or decompiled source is committed or executed.

### Confirmed references and state

The installed recipe resources data/toughasnails/recipe/<material>_water_canteen.json use dirty-water -> normal-water item stacks; <material>_purified_water_canteen.json use normal -> purified. The installed tags/item/enchantable/canteen.json enumerates all 24 references. Materials: leather, copper, iron, gold, diamond, netherite.

For each material M the exact IDs are:
- toughasnails:empty_M_canteen
- toughasnails:M_dirty_water_canteen
- toughasnails:M_water_canteen
- toughasnails:M_purified_water_canteen

Here M is substituted with each material name, e.g. toughasnails:leather_water_canteen and toughasnails:iron_dirty_water_canteen. Water quality is selected by item ID, not a water-type NBT field. Installed ModItems.class constants and bundled recipe inputs/results corroborate these names. FilledCanteenItem.class references getDamageValue/setDamageValue, hurtAndBreak and enchantment copying: strings alone are not control-flow proof.

No trusted CFR is configured; the guarded decompile request returned unavailable. To resolve the state mechanism without running the JAR, inspected the author's 1.21.8 source pinned at f63d0095d2a89237c701ee8795c96819a2cc8127 (supplemental source evidence, not a claim of byte-identical reproduction). ModItems registers separate water variants with equal capacity within each material; FilledCanteenItem.finishUsingItem consumes damage until it returns an empty canteen. Remaining sips are represented by minecraft:damage against minecraft:max_damage, alongside ordinary stack components such as custom_name and enchantments. No custom fluid inventory component is used by these inspected classes. Source capacities: leather 5, copper 6, iron 7, gold 10, diamond 15, netherite 25.

Author source references:
- https://github.com/Glitchfiend/ToughAsNails/blob/f63d0095d2a89237c701ee8795c96819a2cc8127/common/src/main/java/toughasnails/init/ModItems.java
- https://github.com/Glitchfiend/ToughAsNails/blob/f63d0095d2a89237c701ee8795c96819a2cc8127/common/src/main/java/toughasnails/item/FilledCanteenItem.java
- https://github.com/Glitchfiend/ToughAsNails/blob/f63d0095d2a89237c701ee8795c96819a2cc8127/common/src/main/java/toughasnails/item/EmptyCanteenItem.java

### Datapack implementation and limits

Minecraft crafting_transmute changes the item type while retaining the input component patch; no result component overrides are added to the canteen recipes. Damage/remaining drinks, name, enchantments and custom components therefore carry across the charcoal step. Each canteen material has dirty + one charcoal -> normal, then normal + another charcoal -> purified. Empty and already purified canteens have no charcoal recipe. Diamond is supported as an additional installed tier even though it is not a metal.

A vanilla furnace result is a static new stack, which cannot copy input damage/name/enchantments. No canteen furnace recipe is introduced for any material. This avoids refilling partially used canteens and erasing custom data. Leather is never boilable. Metal boiling needs a custom state-preserving recipe implementation; this release does not install or modify any mod.

The mod's existing enchanted-canteen auto-purification is not changed. Upstream FilledCanteenItem.inventoryTick creates a purified stack, carrying damage/enchantments; unrelated custom names/data can be lost during that mod-driven transformation. Test charcoal preservation with ordinary named, partially consumed canteens; if testing enchantments, distinguish the mod tick from the crafting result. Emptying/refilling is also the mod's own behavior and may reset data independently of our recipes.

Minecraft primary references: https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-2 (transmute preserves components); https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5 (result component patches).

Bottle ingredients limitation: normal water and effect potions share minecraft:potion. The existing v0.3.8 furnace ingredient matches that item ID, including effect potions. Minecraft 1.21.8 vanilla ingredients cannot constrain potion_contents; Forge 1.21.8's StrictNBTIngredient and PartialNBTIngredient inspect CUSTOM_DATA, not POTION_CONTENTS. Do not falsely label those as a water-component filter. Retain the inherited furnace matching; normal-water charcoal matching has the same limitation in this test release. The user explicitly approved broad bottle matching for this test. Do not use valuable effect potions in these recipes. A strict water-only recipe needs additional recipe-code support, not an invented JSON component ingredient.

### Existing behavior preserved

No technology lock/give/revert, advancement, NPC event, role-setup or era-completion file is changed. sync only grants the new water recipes to existing hj_participant recipients and stops granting the removed furnace_dirty_water recipe. load only updates the visible version text. The existing Water for Life trigger remains bottle-based and Student-only after Ready to Eat; canteen purification does not add a new progression trigger. Existing TAN purifier recipes and bottle filling behavior are unchanged.

## Human Journey Core 0.3.0 water engine (when installed)

- Rechecked exact TAN identity above. Core's registered-item Forge smoke verifies all six tiers and damage/capacity equality when copying the full component patch with `ItemStack.transmuteCopy`. Named/enchanted/custom-data full, partial and one-drink-left canteens retain state through treatment and NBT serialization; real campfire slot/timer persistence is checked. Empty/malformed canteens are not refilled.
- Core provides dynamic DIRTY→NORMAL filtration and NORMAL→PURIFIED ordinary-furnace/lit-campfire boiling. Water policy is `data/human_journey/core/water.json`, reloadable; bottles/canteens/TAN hand-drinking share the same hazard helper. Purifier and Rain Collector recipes/classes remain untouched. See `human_journey_core/WATER.md`.
- The older datapack-only limits above are historical. With Core 0.3.0, enchanted filled-canteen inventory auto-replacement is suppressed to prevent direct purification/state loss; enchantments themselves remain. Legacy unsafe quality-changing crafting outputs are guarded, and the companion water pack overrides the old blanket `water/convert_one` potion-clearing function without removing its ID. Install that companion above the legacy Human Journey pack, or merge the equivalent data/function migration.
- [Verified build](https://github.com/petryshamen/minecraft-education/actions/runs/40630024751): 28 unit tests, six actual Mixin targets, 36 registered TAN canteen conversions plus furnace outputs/campfire persistence/bottle chain. All 15 Core temperature classes match 0.2.0. Player packet/interaction/timing acceptance tests remain manual; no live installation was performed.


## Core 0.4 water integration facts

- Natural fills/hand drinks/purifier intakes share block-position classification: ordered geographic overrides, TAN purified tag, TAN dirty tag, configurable salt tags (default `minecraft:is_ocean`), then normal. Exact TAN dirty tag includes swamp/mangrove_swamp/mushroom_fields; purified tag is empty in this release.
- Salt bottles/canteens reuse normal item IDs and preserve a namespaced `human_journey:water_type="salt"` CUSTOM_DATA marker. Biome changes do not change collected quality. Tooltip warns Salt Water. Treatment cannot remove this marker; an actual empty/refill changes contents deliberately.
- TAN purifier stores `FilterTimeRemaining`/`FilterDuration` and exposes `getFilterDuration(ItemStack)`. Core 0.4 reuses these saved units/GUI for instant empty-container fills, requiring an adjacent source freshwater block. Old filled-water recipe processing is disabled; salt is never an intake or treatment input. Rain Collector/crafting recipe remain native.
- Exact TAN `DrinkItem` (bottles/juices) has no `onUseTick` sound handler/Consumable component; canteens have their own sound handler. An isolated compatibility mixin supplies bottle/juice sound ticks only. Manual audible/multiplayer checks remain necessary.
