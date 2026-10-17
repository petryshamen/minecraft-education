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
