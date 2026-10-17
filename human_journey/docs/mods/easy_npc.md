<!-- hj-mod-inspection {"filename":"easy_npc-forge-1.21.8-6.5.2.jar","sha256":"147c1c8836cc62d552ba6ef58cbf84882565d8e370d1eca1a1ab604965a11859","mods":[{"mod_id":"easy_npc","name":"Easy NPC","version":"6.5.2","declared_version":"${file.jarVersion}"}]} -->
# Easy NPC

Initial read-only inspection of the installed test-instance JAR. Re-inspect relevant resources before editing an integration if filename, version or SHA-256 changes.

## Identity

- Mod ID/version: `easy_npc` / `6.5.2`
- Inspected JAR: `easy_npc-forge-1.21.8-6.5.2.jar`
- SHA-256: `147c1c8836cc62d552ba6ef58cbf84882565d8e370d1eca1a1ab604965a11859`
- Human Journey test target: Minecraft Java 1.21.8 Forge.
- Metadata source: `META-INF/mods.toml`

Dependencies declared by the installed JAR:

```json
{
  "easy_npc": [
    {
      "modId": "forge",
      "mandatory": true,
      "versionRange": "[58.1.0,)",
      "ordering": "NONE",
      "side": "BOTH"
    },
    {
      "modId": "minecraft",
      "mandatory": true,
      "versionRange": "[1.21.8]",
      "ordering": "NONE",
      "side": "BOTH"
    }
  ]
}
```

## Relevant identifiers and mechanics

- The installed main mod reports mod ID `easy_npc`; this is distinct from companion bundle/configuration UI JARs in the same instance. Inspect those separately when an integration depends on them.
- A quest-related bundled resource was found at `data/easy_mob_farm/simplequests/kill_example.json`, with reference `simplequests:entity`. Its namespace and content do not prove that a corresponding quest engine is installed or that this is the correct Human Journey integration API.
- No relevant recipe or item-state behavior was established by this quest-focused resource scan. Inspect the exact required NPC command/action/dialog classes or resources before making integration claims.
- Keep Human Journey quest/dialog/trade/condition data outside mod JARs, with stable IDs and an isolated interaction adapter. Do not couple new quest progression to Easy NPC unless necessary.
- Unresolved: installed command/action API, quest-stage storage and current Human Journey NPC-to-function references, which must be inspected before an integration.

## Resource inventory for follow-up

- recipes: 0 bundled members.
- tags: 13 bundled members.
- items: 51 bundled members.
- languages: 10 bundled members.

Relevant recipe members from the targeted scan:

- None established by this scan.

Relevant tag members:

- None established by this scan.

## Human Journey restrictions and tests

- No gameplay changes or technology restrictions were introduced by this inspection. Reconcile the configured local copy with current GitHub source before integration edits.
- Preserve all advancement IDs, hj_civ fake players, technology/quest score names and NPC-called functions.
- Keep Teacher preparation bypasses distinct from Student progression restrictions.
- Before a future integration release, test fresh and progressed Students and Teacher behavior, blocked/unlocked recipes, inventory persistence after save/reload, and conflicts with existing recipes.
- For stateful items, test empty/partial/full contents, names, damage, enchantments, upgrades and stored inventories; confirm no state is silently destroyed.
- No JAR was changed, installed, renamed or executed. No trusted CFR decompiler is configured; optional decompilation has not been exercised against this mod.
