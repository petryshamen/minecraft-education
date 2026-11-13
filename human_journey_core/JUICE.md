# Juice canteens and healing — Core 0.9.0

**Core 0.9.0 build verified:** [GitHub Actions run 43347893280](https://github.com/petryshamen/minecraft-education/actions/runs/43347893280), source `76967a2c2f77805a9cf9c9a7ab51224ee0dc5c39`. All 221 tests passed (179 existing + 42 new), zero failures; all ten no-world Forge checks passed. Native checks cover six TAN canteen tiers, seven native JuiceItem registrations, dynamic recipe JSON/network codecs, item NBT/component persistence and depletion. Tests execute TAN's real thirst/hydration/effect handler on mocked player dependencies; no gameplay world/player was constructed. 183 existing classes are byte-identical to Core 0.8.1, including NPC/economy, geography/discovery/HUD, fishing, temperature and unmodified water mechanics. Five existing class files change only for bootstrap and marked-juice guards, including WaterContainers' generated switch helper. Runtime SHA-256: `6135de5fdaaf75624ab045dba7263a331790359eb80e42c60f08b0019e39b721`. No automatic installation or live-server/main-datapack modification.

## Target and installation

Minecraft Java 1.21.8 / Java 21 / Forge 58.1.18 / Tough As Nails 21.8.0.2. Base is Core 0.8.1, retaining approved HUD defaults. Install `human_journey_core-0.9.0.jar` manually on server and clients, replacing the prior Core JAR. Existing dependency stack remains unchanged. No Minecraft gameplay testing, automatic installation, live-server change or main-datapack deployment is performed.

## Developer data contract

Create one definition per juice under **`data/human_journey/core/juice/<file>.json`** in the Human Journey datapack. Resource files are selected using normal datapack priority. The stable body `id` is independent of the filename; duplicates across different files are rejected. Core ships no definitions or production ingredients. Reload with `/reload`.

```json
{
  "schema_version": 1,
  "id": "your_stable_juice_id",
  "bottle_item": "toughasnails:your_existing_juice_item",
  "ingredient": { "item": "your_namespace:your_chosen_ingredient" },
  "canteen_display_name": "Your chosen player-facing name",
  "heal_per_drink": 1.0
}
```

This is an authoring template, not an installable definition. Replace the bottle/ingredient placeholders with registered IDs. For an item-tag ingredient use `"ingredient": { "tag": "your_namespace:your_item_tag" }` instead. Tags use Minecraft 1.21.8's `data/<namespace>/tags/item/<path>.json` path.

| Field | Contract |
| --- | --- |
| `schema_version` | Required integer 1. Unknown fields are rejected. |
| `id` | Required stable 1–64 character logical ID: `[a-z0-9_][a-z0-9_.-]*`. |
| `bottle_item` | Required registered exact TAN `JuiceItem`; no Consumable/Food component or unsupported item subclass. Unique across the catalog. |
| `ingredient` | Exactly one registered non-air item or non-empty item tag. Tag contents are resolved from the reload's staged registry lookup, not stale pre-reload tags. |
| `canteen_display_name` | Required nonblank literal text, at most 128 characters, no control characters. No names are derived from technical IDs. |
| `heal_per_drink` | Required finite non-negative number. `1.0` means one health point / half a heart. No global juice-healing default is imposed. |

At most 256 definitions and 4096 members per ingredient tag are supported. Ingredient sets cannot overlap between juices: a single ingredient must identify exactly one juice for this generic two-input recipe. Empty/unknown tags, malformed fields, duplicate juice IDs/bottle IDs and ambiguous ingredients reject the whole new catalog. Last-valid catalog, source and revision are retained and the resource/reason is logged and shown by diagnostics. Removing all definition files is a valid empty catalog; previously filled removed juices are blocked until their definitions return. Validation schema: `docs/juice.schema.json`; registry/tag/overlap checks remain authoritative at runtime.

The seven existing TAN bottled juice IDs in this exact version are:

```text
toughasnails:apple_juice
toughasnails:cactus_juice
toughasnails:chorus_fruit_juice
toughasnails:glow_berry_juice
toughasnails:melon_juice
toughasnails:pumpkin_juice
toughasnails:sweet_berry_juice
```

These are integration references, not a production catalog. HJ Developer supplies seven stable IDs, display names, actual ingredients/tags and the approved `heal_per_drink: 1.0` values. Core does not assign those ingredients or create bottled-juice recipes. Existing TAN bottle recipes are unchanged. Developer may independently override bottle recipes later if design calls for it.

## Generic canteen crafting

Core registers one `human_journey_core:juice_canteen` special crafting serializer and includes one capability recipe at `data/human_journey/recipe/juice_canteen.json`:

```json
{ "type": "human_journey_core:juice_canteen", "category": "misc" }
```

Developer does **not** need seven recipes, fixed outputs, component-copy directives or new Java items for canteens. Put one completely full purified TAN canteen and one accepted ingredient into a 2×2 or 3×3 grid, in any positions. No other occupied cells are allowed. One ingredient is consumed per full canteen conversion. There is no sugar requirement. Normal ingredient crafting remainders apply, but the input canteen does not also leave an empty duplicate.

DIRTY, NORMAL, SALT, empty, partly used, already-juiced or invalid canteens are rejected. The result keeps the exact purified carrier item, material/tier, full capacity and existing component overrides. The native six tiers are leather, copper, iron, gold, diamond and netherite. Special recipes have no automatic recipe-book layout/ghost placement; server-authoritative crafting results are used. The local client does not need the definition catalog for rendering or prediction.

## Item state and presentation

The carrier remains the existing `<tier>_purified_water_canteen`; juice is a separate content identity, not a fifth water quality. The native `minecraft:custom_data` component contains a namespaced compound:

```text
human_journey:juice: {id: "stable_id", display_name: "Configured Juice Canteen"}
```

`display_name` is a cached presentation label, not authority for gameplay. Current server definitions supply all drink properties/healing. Only this compound is added/removed; unrelated custom data, enchantments and other components are preserved. Native item/component persistence and inventory packets carry the marker through inventory/container moves, drops, logout and saves. No juice/tier item classes or custom component registration are needed.

An isolated ItemStack name hook uses the stored configured name. Existing custom names take precedence, with the configured juice name also shown as a concise tooltip so contents remain identifiable. Player-inventory ticks refresh the cached label after a valid display-name reload without changing identity or uses. Chest/drop labels refresh when returned to an inventory. No custom model/texture is introduced.

## Native behaviour and completion hooks

Exact TAN 21.8.0.2 inspection found all seven juices use `JuiceItem`, whose item-specific behaviour is native drink eligibility plus the base `DrinkItem` consumption/stat/advancement/glass-bottle handling. Thirst, hydration and unsafe-drink effect are applied by `ThirstHandler.onItemUseFinish` using live TAN drink tags. TAN juice items do not have extra per-flavour finish effects in this version.

For a canteen, the completed-use hook constructs a one-serving stack of the configured bottle and calls **that exact TAN handler** using a synthetic GlitchCore Finish event. It does not consume/spawn a real bottle, broadcast a second global event or copy thirst/hydration/effect constants. TAN's current tags, precedence, thirst-enabled guard and effect duration/probability remain native. The original marked-canteen TAN finish event is suppressed, preventing an additional purified-water serving. Canteen eligibility uses the configured native bottle's `canAlwaysDrink()` value. Native canteen use duration and sounds remain intact.

The same completed canteen call restores configured health once and advances damage by exactly one use in survival. Unbreaking does not grant extra juice servings: one successful sip always spends one use. Enchantments including Water Cleansing remain on the item, without converting juice back to water. Creative mode follows native no-depletion convention. The final sip transmutates to the matching empty canteen and removes only juice state and used-fill damage, preserving other component overrides.

Configured bottled juices receive healing at **RETURN from native `DrinkItem.finishUsingItem`**, server-side only. Their native stack consumption, glass-bottle return, stats/criteria, thirst/hydration and finish effects remain untouched. No healing occurs on right-click, use ticks, interrupted use, the client, or unrelated drinks. The canteen bridge calls only TAN's thirst handler, so it cannot trigger a second bottle-healing hook. Health restoration is capped to the native maximum-health deficit; zero and large finite data values are safe.

## Water compatibility and invalid state

Marked juice canteens are excluded from water quality classification, filtration, conversion/refilling and boiling. Heat guards prevent old fixed furnace/campfire recipes from stripping the marker. An isolated crafting guard blocks unrelated fixed recipes from consuming a juice carrier and silently producing water or refilling it. Ordinary unmarked water logic is unchanged. No Rain Collector, purifier, salt classification or treatment balance is redesigned.

Unknown, malformed or removed juice IDs and invalid carrier states cannot drink, heal or spend uses; the canteen remains intact and the player receives a clear error directing them to diagnostics. Such items never fall back to purified water merely because their carrier item is purified. Restoring a valid definition restores their usability.

## Diagnostics

Permission level 2:

```text
/hjcore juice
/hjcore juice list [page]
/hjcore juice show <id>
```

The root command reports catalog source/revision/reload error and the main-hand canteen's tier, juice ID, remaining/max uses and validity. `show` reports bottle ID, ingredient/tag, configured display name and healing. IDs have tab completion; lists are paginated. Existing Core/NPC/geography commands remain registered.

## Automated evidence and known limits

Tests use synthetic catalogs/ingredients, real item/component codecs, mocked players and the real TAN thirst handler. The no-world Forge data run exercises the exact six registered canteen tiers, seven registered juice bottle classes, native dynamic recipe/remainders, custom name/enchantment/data preservation, repeated depletion, unknown IDs and water guards. Mixin targets are loaded under ModLauncher. These checks are not Minecraft gameplay, GUI, world restart or dedicated-server gameplay startup testing.

Integration deliberately accepts exact TAN `JuiceItem` bottles for the inspected version. Unrelated mod drinks, subclasses, Food/Consumable-driven drinks or future TAN versions with new special effects need an adapter review and are rejected instead of silently approximated. Third-party scripts listening to arbitrary global bottle-finish events are not replayed for virtual canteen servings; only TAN's own native handler is invoked. A separate future adapter would be needed for such hooks.

## Manual checklist for the user / Developer

1. Supply test definitions with chosen ingredients; `/reload`; inspect `juice list/show`. Confirm invalid/duplicate definitions log clear errors and keep the last valid catalog. Confirm missing definitions disable only juice support.
2. Craft with full purified leather and another-tier canteen. Verify same tier, full uses, chosen name and consumed ingredient. Reject partial/dirty/normal/salt canteens, wrong ingredients and extra occupied cells. Try inventory and crafting-table grids, normal take and shift-click.
3. Drink while thirsty/injured; compare against the corresponding native bottle. Expect configured healing (1.0 = half heart), native thirst/hydration/effects and exactly one canteen use. Interrupt consumption and confirm no bonus/use. Check maximum health, creative convention and final matching empty tier.
4. Test all six tiers and the seven eventual definitions. Preserve names, enchantments/Water Cleansing and custom components; move/drop/store items, log out and restart the test server. Verify juice identity and remaining uses persist.
5. Remove/restore a test definition and inspect blocked/restored old canteens. Reload a name/heal change and inspect updated behaviour. Confirm no juice filtration/boiling/refill through legacy fixed recipes.
6. Recheck ordinary DIRTY/NORMAL/PURIFIED/SALT water, native bottles, filtering, furnace/campfire, Rain Collector, purifier, temperature, NPC economy, geography/discovery, approved HUD defaults and progression. Nothing is automatically deployed to the main datapack or server.
