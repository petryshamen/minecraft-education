<!-- hj-mod-inspection {"filename":"packedup-1.1.0-forge-mc1.21.6.jar","sha256":"8059a575e2d5d036253daea48018cd2932b33a5d0a81b8c3f93575dd75deb9f9","mods":[{"mod_id":"packedup","name":"Packed Up","version":"1.1.0","declared_version":"1.1.0"}]} -->
# Packed Up

Initial read-only inspection of the installed test-instance JAR. Re-inspect relevant resources before editing an integration if filename, version or SHA-256 changes.

## Identity

- Mod ID/version: `packedup` / `1.1.0`
- Inspected JAR: `packedup-1.1.0-forge-mc1.21.6.jar`
- SHA-256: `8059a575e2d5d036253daea48018cd2932b33a5d0a81b8c3f93575dd75deb9f9`
- Human Journey test target: Minecraft Java 1.21.8 Forge.
- Metadata source: `META-INF/mods.toml`

Dependencies declared by the installed JAR:

```json
{
  "packedup": [
    {
      "modId": "forge",
      "mandatory": true,
      "versionRange": "[56.0.9,)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "minecraft",
      "mandatory": true,
      "versionRange": "[1.21.6,1.22)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "supermartijn642corelib",
      "mandatory": true,
      "versionRange": "[1.1.17,1.2.0)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "supermartijn642configlib",
      "mandatory": true,
      "versionRange": "[1.1.6,)",
      "ordering": "NONE",
      "side": "BOTH"
    }
  ]
}
```

## Relevant identifiers and mechanics

- `data/packedup/recipe/iron_from_basic.json` explicitly references `packedup:basicbackpack` and `packedup:ironbackpack`.
- The outer type is `supermartijn642corelib:conditional`; its nested recipe uses `packedup:upgrade_backpack`. Conditions include `packedup:is_backpack_enabled`; ingredients reference `#c:ingots/iron` and `#c:leathers`.
- The presence of a custom upgrade serializer means inventory/component transfer must be checked in its implementation. Do not replace it with ordinary shaped crafting and assume contents survive.
- No relevant state fields were established by the resource-only inspection. Unresolved: inventory storage, backpack identity/ownership, state transfer, nesting restrictions and configuration-dependent recipe availability.
- A strict JSON parse error was found in bundled `assets/packedup/lang/pl_pl.json` (line 11, column 3). This is a third-party resource finding, not a Human Journey validation error. The JAR was left unchanged.

## Resource inventory for follow-up

- recipes: 26 bundled members.
- tags: 1 bundled members.
- items: 7 bundled members.
- languages: 19 bundled members.

Relevant recipe members from the targeted scan:

- `data/packedup/recipe/basic_from_chest.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/copper_from_basic.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/copper_from_chest.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_basic.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_chest.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_copper.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_gold.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_iron.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/diamond_from_silver.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/gold_from_basic.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/gold_from_chest.json` — type `supermartijn642corelib:conditional`.
- `data/packedup/recipe/gold_from_copper.json` — type `supermartijn642corelib:conditional`.

Relevant tag members:

- None established by this scan.

## Human Journey restrictions and tests

- No gameplay changes or technology restrictions were introduced by this inspection. Reconcile the configured local copy with current GitHub source before integration edits.
- Preserve all advancement IDs, hj_civ fake players, technology/quest score names and NPC-called functions.
- Keep Teacher preparation bypasses distinct from Student progression restrictions.
- Before a future integration release, test fresh and progressed Students and Teacher behavior, blocked/unlocked recipes, inventory persistence after save/reload, and conflicts with existing recipes.
- For stateful items, test empty/partial/full contents, names, damage, enchantments, upgrades and stored inventories; confirm no state is silently destroyed.
- No JAR was changed, installed, renamed or executed. No trusted CFR decompiler is configured; optional decompilation has not been exercised against this mod.
