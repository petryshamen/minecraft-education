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

## Trade stock (6.5.2 / Minecraft 1.21.8)

The installed core/config UI/bundle are 6.5.2. Installed class-pool inspection was read-only; no decompiler is configured and no live trade reproduction was performed. The corresponding official source revision [f929f51](https://github.com/MarkusBordihn/BOs-Easy-NPC/tree/f929f51540778bb8bcbb43989e6ff4a4c816ce85) declares version 6.5.2 and Minecraft 1.21.8. Source-correlated findings:

- Max Uses counts completed trade transactions, shared by all players using that NPC, not a per-player quota. A stack output still counts as one transaction. Only one trading customer is supported at a time.
- Basic Trading uses a global maximum for its offers. Its `updateBasicTradingOffers` returns immediately when the type IS BASIC; changing the global value can therefore leave existing offers at their previous maximum. Rebuilding Basic offers from item slots starts their uses at zero.
- Advanced Trading stores a maximum per offer. Editing that maximum reconstructs the offer with zero uses; its item editor otherwise preserves existing per-offer usage. Both modes use the same merchant stock mechanism, so Advanced is not a persistence fix.
- Runtime merchant offers are a COPY of the synchronized/stored offers. `notifyTrade` increments the runtime offer; saving serializes the stored offers. Recreating the merchant list, chunk/entity unload/reload, or world/server restart can consequently restore stale usage counts. Merely closing/reopening should retain the current runtime list unless a reset/rebuild occurs. A datapack `/reload` alone does not intentionally reload NPC entities.
- Reset Uses resets all offer counts and records a last-reset timestamp. Min for Reset is real elapsed minutes, evaluated when a player opens trading. Zero disables timed resets; leaving the screen open is not a continuous restock timer. LastReset is serialized. Downtime can count toward elapsed time.
- Upstream [issue #729](https://github.com/MarkusBordihn/BOs-Easy-NPC/issues/729) reports trades resetting after entity unload/reload; [#689](https://github.com/MarkusBordihn/BOs-Easy-NPC/issues/689) concerns maximum-use behavior. Later fixes on other Minecraft branches do not establish a fixed compatible 1.21.8 release.

Recommended next step: use a disposable Advanced trader with one offer, Max Uses 1 and reset minutes 0; test two players, close/reopen, chunk reload and restart separately. Then test a one-minute reset by waiting and reopening. Seek a verified 1.21.8-compatible upstream stock-persistence fix/backport before relying on scarcity. A Core adapter could synchronize runtime stock to persistent state; datapack NBT edits risk conflicting with the runtime cache and open screens. Neither workaround is implemented.

Source entry points at the pinned revision: `TradingDataCapable`, `TradingOfferHandler`, `TradingDataSet`, config UI `TradingContainerHandler` and `ChangeBasicTradingMessage`.


## Core 0.5.0 template adapter entry points

The isolated Core adapter targets `easy_npc:humanoid` via the native EntityType factory. It uses `EasyNPCBase` setters for owner/home/preset identity, `SkinDataEntry.createRemoteSkin`, `DialogDataSet`/`DialogDataEntry`/`DialogButtonEntry`, `ActionDataEntry`/`ConditionDataEntry`, and Advanced `TradingDataSet`/`MerchantOffers`. Core reads reloadable `data/<namespace>/npc/*.json`; no GUI or native preset import is used. Local content page/button IDs become 32-character per-entity runtime labels before Easy NPC derives UUIDs; actions target their own fresh entity UUID. Native hash-based action IDs are not a new durable execution-limit engine. Only SCOREBOARD conditions and the limited command/scoreboard/trading/named-dialog actions are exposed.

The public Forge build dependency `curse.maven:easy-npc-core-1308987:7402995` has SHA-256 `89c9bd175243e0b894d56e3dc4e5d7544f279005ace703510d34454cded6e317`, differing from the installed archive hash above, but every public class entry is byte-identical to the installed 6.5.2 target. Installed JARs remain read-only and are never used for execution/classpaths. The adapter initializes stored/runtime trade offers through `setTradingOffers`; the stock-persistence limitation documented above remains. No stock sync workaround or gameplay persistence test was introduced.
