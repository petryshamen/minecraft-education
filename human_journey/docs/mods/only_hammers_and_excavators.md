<!-- hj-mod-inspection {"filename":"onlyhammersandexcavators-1.21.8-0.2.jar","sha256":"963ebed5c202e8ab9083334ab7da220d58614864f6a77cf0c80162538b48bb6a","mods":[{"mod_id":"onlyhammersandexcavators","name":"OnlyHammersAndExcavators","version":"1.21.8-0.2","declared_version":"1.21.8-0.2"}]} -->
# OnlyHammersAndExcavators

Initial read-only inspection of the installed test-instance JAR. Re-inspect relevant resources before editing an integration if filename, version or SHA-256 changes.

## Identity

- Mod ID/version: `onlyhammersandexcavators` / `1.21.8-0.2`
- Inspected JAR: `onlyhammersandexcavators-1.21.8-0.2.jar`
- SHA-256: `963ebed5c202e8ab9083334ab7da220d58614864f6a77cf0c80162538b48bb6a`
- Human Journey test target: Minecraft Java 1.21.8 Forge.
- Metadata source: `META-INF/mods.toml`

Dependencies declared by the installed JAR:

```json
{
  "onlyhammersandexcavators": [
    {
      "modId": "forge",
      "mandatory": true,
      "versionRange": "[58,)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "minecraft",
      "mandatory": true,
      "versionRange": "[1.21.8,1.22)",
      "ordering": "NONE",
      "side": "BOTH"
    }
  ]
}
```

## Relevant identifiers and mechanics

- `data/onlyhammersandexcavators/recipe/iron_hammer.json` explicitly produces `onlyhammersandexcavators:iron_hammer` using `minecraft:crafting_shaped`.
- The inspected recipe consumes `minecraft:iron_ingot`, `minecraft:iron_block` and `minecraft:stick`. Other hammer/excavator recipes and tags are listed below for targeted follow-up.
- Recipe JSON is a candidate datapack override surface; runtime behavior, mod configuration and existing Human Journey restrictions still need review before changing it.
- Unresolved: custom mining behavior, durability/enchantments, progression gates and state-preserving upgrade behavior. Resource-only inspection does not establish these mechanics.

## Resource inventory for follow-up

- recipes: 20 bundled members.
- tags: 38 bundled members.
- items: 20 bundled members.
- languages: 2 bundled members.

Relevant recipe members from the targeted scan:

- `data/onlyhammersandexcavators/recipe/diamond_excavator.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/diamond_hammer.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/emerald_excavator.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/emerald_hammer.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/gold_excavator.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/gold_hammer.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/iron_excavator.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/iron_hammer.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/lapis_excavator.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/lapis_hammer.json` — type `minecraft:crafting_shaped`.
- `data/onlyhammersandexcavators/recipe/netherite_excavator.json` — type `minecraft:smithing_transform`.
- `data/onlyhammersandexcavators/recipe/netherite_hammer.json` — type `minecraft:smithing_transform`.

Relevant tag members:

- `data/c/tags/item/tools/mining_tool.json`.
- `data/c/tags/item/tools.json`.
- `data/minecraft/tags/item/breaks_decorated_pots.json`.
- `data/minecraft/tags/item/cluster_max_harvestables.json`.
- `data/minecraft/tags/item/enchantable/durability.json`.
- `data/minecraft/tags/item/enchantable/mining.json`.
- `data/minecraft/tags/item/enchantable/mining_loot.json`.
- `data/minecraft/tags/item/enchantable/vanishing.json`.
- `data/minecraft/tags/item/pickaxes.json`.
- `data/minecraft/tags/item/shovels.json`.
- `data/onlyhammersandexcavators/tags/item/diamond_excavators.json`.
- `data/onlyhammersandexcavators/tags/item/diamond_hammers.json`.

## Human Journey restrictions and tests

- No gameplay changes or technology restrictions were introduced by this inspection. Reconcile the configured local copy with current GitHub source before integration edits.
- Preserve all advancement IDs, hj_civ fake players, technology/quest score names and NPC-called functions.
- Keep Teacher preparation bypasses distinct from Student progression restrictions.
- Before a future integration release, test fresh and progressed Students and Teacher behavior, blocked/unlocked recipes, inventory persistence after save/reload, and conflicts with existing recipes.
- For stateful items, test empty/partial/full contents, names, damage, enchantments, upgrades and stored inventories; confirm no state is silently destroyed.
- No JAR was changed, installed, renamed or executed. No trusted CFR decompiler is configured; optional decompilation has not been exercised against this mod.
