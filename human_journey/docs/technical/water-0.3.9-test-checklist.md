# Human Journey 0.3.9 water test checklist

Target: Minecraft Java 1.21.8 / Forge with Tough As Nails 21.8.0.2. Test only in Human Journey Test. Reload/reopen manually, check latest.log for recipe/JSON/unknown-ID errors and /datapack list for only the new Human Journey test build. No progress reset is required.

## Bottles (use the 2x2 inventory grid and the crafting table)

1. Fill a vanilla glass bottle. Normal Water Bottle remains normal while held and after save/reload: it is untreated natural water.
2. Water Bottle + one Charcoal -> one Tough As Nails Purified Water Bottle. Coal must not substitute for charcoal.
3. Normal Water Bottle in a fueled Furnace -> Purified Water Bottle after 200 ticks. Check input consumption and output count, including a hopper-fed furnace.
4. Dirty Water Bottle + one Charcoal -> normal Water Bottle (minecraft:potion with potion_contents.potion=minecraft:water), not purified. Craft that intermediate with a second Charcoal -> Purified Water Bottle. Each step consumes exactly one bottle and one charcoal; no duplicate/extra glass bottle should remain.
5. Put Dirty Water Bottle directly into a fueled Furnace and wait at least 200 ticks: no recipe/output. Repeat with a stack/automated input if applicable. Dirty water must not be directly boilable.
6. Empty Glass Bottle, splash water and lingering water must not match the new bottle charcoal recipe. Purified Water Bottle + Charcoal has no recipe. Campfire direct dirty-water purification remains absent.
7. Known inherited/parallel limitation: any drinkable minecraft:potion (including effect potions) matches the furnace item ingredient and the new normal-water charcoal ingredient. Confirm/document this only with disposable test potions; water-only component discrimination is not implemented. No claim is made that these recipes reject effect potions.
8. Verify Tough As Nails' own Water Purifier still performs dirty -> normal -> purified as before; Human Journey does not override its recipes.

## Canteens

For EACH material: leather, copper, iron, gold, diamond, netherite. Use a test-supplied canteen if its original technology is locked; do not unlock technology or change Student permissions to obtain it.

1. Full dirty canteen + one Charcoal -> same-material normal canteen. Another Charcoal -> same-material purified canteen. Normal -> purified also works directly. Test both 2x2 and 3x3 grids, swapped slot positions, normal clicks and shift clicks. One canteen and one charcoal are consumed per step.
2. Repeat with a partially consumed canteen and a canteen with only one drink left. Record damage before each step, compare after: remaining drinks must not increase/reset, material and capacity must be unchanged.
3. Repeat with an anvil-named canteen and a harmless custom_data marker (e.g. an administrator-supplied test item). Compare custom_name, damage, enchantments/custom_data if present before and after crafting and after save/reload. No components should disappear during the recipe itself.
4. Empty and already purified canteens + Charcoal: no new recipe. Coal: no substitute. Two canteens or extra ingredients must not yield duplicate or refill results.
5. Furnace tests for dirty/normal/purified/empty canteens of EVERY material: no recipe. Leather never boils; metal/diamond boiling is deliberately unavailable because static smelting results cannot preserve item state.
6. Enchanted dirty/normal canteens may auto-purify in a player's inventory through existing Tough As Nails behavior. Check this separately before attempting a charcoal test. The upstream mod transformation itself may drop custom names/data; do not confuse that with recipe preservation. Already purified enchanted canteens have no redundant charcoal recipe.

## Roles/progression and regression

1. Teacher: existing preparation bypass remains; water crafting must not advance shared civilization progress.
2. Student before Ready to Eat: obtaining purified bottles must not bypass the existing prerequisite. After Ready to Eat: obtaining a purified bottle must trigger Water for Life and shared synchronization as in 0.3.8. Canteens alone must not add a new advancement trigger.
3. Fresh/progressed Students retain the same metal/recipe locks; Teacher recipes remain available. Check hj_civ, hj_jengo, hj_trader and visible era progress across reload without resets.
4. Confirm recipes are granted to existing participants even with doLimitedCrafting=true, including players who log in after reload. Check existing Jengo/Trader interactions and Era 0 capstone as a regression smoke test.

Static validation and source-diff checks do not execute Minecraft or prove Forge recipe loading or in-game behavior. These gameplay checks remain for the user.
