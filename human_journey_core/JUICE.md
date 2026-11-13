# Juice scaling and custom bottles — Core 0.10.0

**Core 0.10.0 build verified:** [GitHub Actions run 43528787371](https://github.com/petryshamen/minecraft-education/actions/runs/43528787371), source `da4417d46b2fd500801e8100c40d496c330a05bd`. All 256 tests passed (221 prior tests retained/migrated + 35 new), zero failures; all eleven no-world Forge checks passed. Exact-unit planning/removal tests include normal/repeated takes, native quick-craft recipe clearing, split/tag/shifted grids, v1 defaults and custom bottle/canteen source/healing/persistence. Native data checks exercise registered six-tier canteens, shared item/model name/codecs, v2 recipes and bounded resource accounting. 201 existing classes are byte-identical to Core 0.9.0; existing class changes are confined to juice integration. Runtime SHA-256: `ca7324d31ff86d377dddddaf8b57a941438e26169ff1fa4ef404a4a3d6fa40be`. No Minecraft gameplay, automatic installation, main-datapack change or live-server deployment.

Minecraft Java 1.21.8 / Java 21 / Forge 58.1.18 / Tough As Nails 21.8.0.2. Install matching `human_journey_core-0.10.0.jar` manually on server and clients, replacing the previous JAR. Core retains all 0.9 canteen/water behaviour and approved HUD defaults. No Minecraft gameplay testing, automatic installation, live-server change or main-datapack modification is performed.

## Definition location and compatibility

One resource per juice: **`data/human_journey/core/juice/<file>.json`**. Reload with `/reload`. Stable body IDs are independent of filenames; normal datapack resource priority applies. Unknown fields, duplicate IDs, duplicate native-output bottle IDs, invalid items/tags, empty tags, ambiguous ingredient sets and non-finite/negative healing reject the new catalog atomically, retaining the last valid catalog and error diagnostics. Removing all definitions deliberately activates an empty catalog. Existing stored juice IDs are not rewritten.

**Schema v1 needs no startup migration.** Its fields remain `schema_version: 1`, `id`, `bottle_item`, `ingredient`, `canteen_display_name`, `heal_per_drink`. It resolves to native bottle mode, bottle ingredient count **1**, canteen ingredient count **1**. Existing Java fixture constructor/accessors remain compatible. The new generic bottle recipe can also use v1 definitions. Unrelated existing native/production recipes remain independent.

## Schema v2

All top-level fields shown below are required. These are **authoring examples**, not production files; replace the example ingredient with Developer-selected registered content.

```json
{
  "schema_version": 2,
  "id": "example_native",
  "ingredient": { "item": "examplemod:chosen_ingredient" },
  "bottle_ingredient_count": 2,
  "canteen_ingredient_count": 8,
  "bottle": { "type": "native", "item": "toughasnails:apple_juice" },
  "canteen_display_name": "Example Juice Canteen",
  "heal_per_drink": 1.0
}
```

Native mode requires exactly `type` and `item`. The output must remain an exact supported TAN `JuiceItem` without Food/Consumable components. It is the output item and native effect source; no extra identity marker is added to native output bottles. Their own consumption, hydration/thirst, effects and glass return remain native, plus the existing HJ healing hook.

Custom mode replaces only the `bottle` object:

```json
{
  "type": "custom",
  "display_name": "Example Custom Juice",
  "effect_source_item": "toughasnails:apple_juice"
}
```

The displayed source here is illustrative, **not a choice for production Carrot Juice**. Developer chooses the actual source after reviewing native profiles. Custom mode outputs the single **`human_journey_core:juice_bottle`** item and uses the selected native source for both bottled and canteen servings. Multiple custom flavours may share one effect source; their IDs, display names, ingredients, costs and healing remain independent. Native output bottle IDs must remain unique because unmarked native bottles identify their definition by item ID.

Ingredient is exactly one `{"item":"namespace:id"}` or `{"tag":"namespace:tag"}`. Tag contents are resolved against Forge's updated/staged tag lookup at reload. The entire catalog retains physical-item ambiguity protection: one item cannot identify two juices. Mixing different members of the same juice's tag is allowed; mixing ingredients belonging to different juices is rejected.

Both quantity fields are independent **integers 1–64**, measured in actual item units. No ratio or material/tier multiplier exists. Stable IDs use `[a-z0-9_][a-z0-9_.-]{0,63}`. Display names are nonblank literal strings up to 128 characters without control characters. Healing is finite and non-negative; `1.0` is one health point / half a heart, capped to the maximum-health deficit. The existing bounded catalog limits remain 256 definitions and 4096 members per tag. Machine-readable combined v1/v2 schema: `docs/juice.schema.json`; runtime registry/cross-reference checks are authoritative.

## Generic recipes

Core includes two special dynamic capability recipes:

| Resource | Serializer | Input/output |
| --- | --- | --- |
| `data/human_journey/recipe/juice_bottle.json` | `human_journey_core:juice_bottle` | One TAN purified water bottle + configured bottle cost → one native/custom juice bottle |
| `data/human_journey/recipe/juice_canteen.json` | `human_journey_core:juice_canteen` | One completely full purified TAN canteen + configured canteen cost → one same-tier full juice canteen |

No sugar, flavour-specific Java registration or per-tier recipe is required. A carrier stack may contain additional bottles for successive crafts; each completed craft consumes exactly **one** carrier unit. There must be one carrier cell, no unrelated occupied cells, and enough matching ingredient units across the remaining cells. Excess matching units may remain. For cost 8, one stack of 20 becomes 12; 4+4, mixed members of the same tag, or eight separate cells in a 3×3 grid are also valid. Extra matching cells do not increase the price and can remain unconsumed when the budget has already been met.

All six canteen tiers share exactly the same configured cost. Partial/dirty/normal/salt/already-juiced canteens are invalid; bottle recipes require TAN purified water bottles, rejecting normal/dirty/salt/empty/juice bottles. Existing compatible component overrides, names and canteen tier are preserved. Native bottle output uses the configured TAN item; custom output attaches flavour identity to the shared item. Recipes are special recipes with no automatic recipe-book ghost layout; server results are authoritative and clients do not need the full catalog.

**Legacy fixed recipes are independent.** Core does not remove or reprice TAN/production recipes. After migrating production definitions, Developer must intentionally remove/override redundant fixed recipes that would otherwise offer a cheaper alternative. Do not assume the generic cost changes every other recipe using the same output.

## Exact consumption and shift-click

`JuiceCrafting` builds one immutable per-craft unit budget. It resolves the carrier/juice, sums available units, allocates the configured cost in grid order, and records **zero** for valid matching cells not needed for the cost. Minecraft trims empty grid borders; the planner explicitly maps trimmed recipe coordinates back to full 2×2/3×3 container indices.

An isolated `ResultSlot` Mixin preserves the normal native result-taking/shift-click/stat/recipe-award path. For the two HJ quantity recipes only, it replaces native `removeItem(index, 1)` with that cell's exact budget, including zero. Native quick-craft clears its used-recipe holder before `onTake`, so the adapter captures the selected HJ recipe before that clear. Each completed take recomputes its current budget, and native result recalculation stops repeated crafting when a carrier/material is insufficient. Ordinary recipes keep their vanilla per-cell consumption.

Normal ingredient crafting remainders are multiplied by actual consumed units. The carrier leaves no duplicated empty container. Grid remainders respect real max stack size and available merge space; overflow is split into legal stacks and routed through the normal player-inventory/drop fallback after the native take loop. A pickup guard refuses stale/invalid HJ results before taking. No custom global crafting loop or client-side resource deduction is introduced.

Support is for the normal vanilla/Forge inventory and crafting-table `ResultSlot` path. Third-party automated crafting systems that bypass it need a quantity-aware adapter; merely invoking the recipe's `assemble` method does not consume inputs. The normal server take path is covered by synthetic hook tests and no-world native-container checks; actual inventory clicks remain a user gameplay check.

## Shared item state, rendering and drinking

Custom bottles and canteens use native `minecraft:custom_data`, compound **`human_journey:juice`**, with stable `id` and cached `display_name`. The marker is preserved by native item save/network transport through moves, storage, drops and saves. Cached text is presentation only; current server definitions determine all effects and healing. Custom names stay intact; the configured contents label is available in the tooltip. Existing canteen depletion remains one use per successful survival sip, retaining identity until the final matching-tier empty canteen. Creative mode retains native no-depletion convention.

Exactly one Java item, `CustomJuiceBottleItem`, is registered. Modern `assets/human_journey_core/items/juice_bottle.json` selects one shared generated bottle model using the existing vanilla honey-bottle texture. No new flavour textures or client renderer classes are added. An unconfigured generic item cannot drink. Valid custom bottles show the configured bottle display name; canteens show their configured canteen name. Inventory ticks refresh labels after valid reloads.

The custom bottle extends TAN `DrinkItem`, so native 32-tick drink animation, existing TAN sound compatibility, stats/criteria, survival consumption/glass-bottle return and creative convention are reused. Use eligibility comes from the configured source's native `canAlwaysDrink`. On server-side completion, Core calls the same exact TAN `ThirstHandler` on a virtual source bottle, heals once, then delegates physical consumption to native bottle completion. Custom items are excluded from the native-item healing map, and their marked original finish event is suppressed, preventing double hydration/healing. No right-click/tick/interruption/client healing hook exists.

Custom canteens use `effect_source_item` through the existing canteen engine. Native-mode canteens still use their configured native bottle. No thirst/hydration/effect constants are copied. Unknown/removed/invalid custom IDs or a custom bottle whose definition has changed to native mode are blocked without consumption/healing or fallback to water. Restoring a valid matching definition restores use. Water quality, filtration/boiling, purifier, Rain Collector and Water Cleansing protections remain unchanged.

## Diagnostics

Permission level 2 commands remain:

```text
/hjcore juice
/hjcore juice list [page]
/hjcore juice show <id>
```

`show` now includes schema version, mode, native output or custom display name, effect source, independent bottle/canteen counts, ingredient and healing. Main-hand inspection recognises custom bottles (including unconfigured/invalid ones) as well as canteen tier/remaining/max uses. Errors retain the failed reload reason and last valid source/revision.

## Developer migration

Keep existing IDs, filenames, ingredients, canteen names and healing unless intentionally changing production content. Change seven definitions to `schema_version: 2`; replace each old `bottle_item` with `bottle: {type: native, item: <same ID>}` and supply both required costs. Then remove/override redundant fixed bottle recipes deliberately. Core ships no production definitions or balance matrix.

For the eighth future custom juice, Developer supplies its stable ID, ingredient/tag, both costs, bottle display name, canteen display name, healing and a reviewed native `effect_source_item`. Use `bottle.type: custom`; no new Java item or per-flavour recipe is needed. The requested cost range, including 2/5 and 2/8, is supported for every tier. Developer—not Core—chooses Carrot's final profile and production data.

Supported source IDs remain `toughasnails:apple_juice`, `cactus_juice`, `chorus_fruit_juice`, `glow_berry_juice`, `melon_juice`, `pumpkin_juice`, `sweet_berry_juice` (each with the `toughasnails:` namespace). Exact inspected TAN `JuiceItem` classes without Food/Consumable are supported; arbitrary mod drinks/new subclasses require adapter review. External global bottle-finish scripts are not replayed for virtual servings, only TAN's native handler.

## Manual acceptance checklist — not performed by Core

1. Start with existing v1 files unchanged; inspect defaults 1/1 and native healing/canteens. Migrate synthetic tests to v2, reload, and inspect mode/source/costs. Reject missing/invalid counts, source/name errors and ingredient ambiguity while retaining the last valid catalog.
2. Test cost 2/8 with one stack, split stacks, mixed valid tag members, eight occupied ingredient cells, shifted/edge placements, excess units and insufficient units. Check exact before/after counts in inventory and crafting-table grids, normal clicks and shift-click; test full inventory and ingredient remainders.
3. Test native output and shared custom output, bottle names/model/sounds, interrupted and completed drinking, half-heart healing and max-health cap, native source thirst/hydration, creative mode, glass return and removed IDs.
4. Test all six canteen tiers at identical material cost, components/custom names, partial/wrong/salt/already-juiced rejection, custom effect source, one use per sip and final empty tier. Move/store/drop, log out and restart the test server; identity and remaining uses must persist.
5. Recheck ordinary juices/water/treatment/purifier/Rain Collector, Water Cleansing, NPC economy, geography/discovery/HUD, fishing and temperature. Do not deploy production definitions until these checks are accepted.

Automated tests and headless Forge data checks do not claim Minecraft GUI/gameplay or actual loaded-world restart testing. No Era/Agriculture/dessert/progression content, NPC commodities, production prices or main datapack is modified.
