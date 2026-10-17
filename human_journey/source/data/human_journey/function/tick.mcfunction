# Keep visible shared progress synchronized.
function human_journey:sync

# WATER:
# Water chemistry is left to Tough As Nails.
# Normal Water Bottle remains normal. Dirty Water remains dirty.
# Charcoal: dirty -> normal -> purified, using two stages.
# Normal bottled water can also be boiled; dirty water cannot.
# Canteen charcoal recipes preserve components; no canteen smelting is provided.

# PLAYER-LEVEL METAL LOCK:
# Recipe-book locking is only the UI layer. This is the hard metallurgy gate.
# If a STUDENT somehow obtains a worked ingot before the technology is unlocked,
# it is immediately returned to raw material. Explicit slot checks cover survival hotbar.
execute as @a[tag=hj_student] unless score #tech_iron hj_civ matches 1 if items entity @s weapon.mainhand minecraft:iron_ingot run function human_journey:tech/revert/iron
execute as @a[tag=hj_student] unless score #tech_iron hj_civ matches 1 if items entity @s weapon.offhand minecraft:iron_ingot run function human_journey:tech/revert/iron
execute as @a[tag=hj_student] unless score #tech_iron hj_civ matches 1 if items entity @s hotbar.* minecraft:iron_ingot run function human_journey:tech/revert/iron
execute as @a[tag=hj_student] unless score #tech_iron hj_civ matches 1 if items entity @s inventory.* minecraft:iron_ingot run function human_journey:tech/revert/iron

execute as @a[tag=hj_student] unless score #tech_copper hj_civ matches 1 if items entity @s weapon.mainhand minecraft:copper_ingot run function human_journey:tech/revert/copper
execute as @a[tag=hj_student] unless score #tech_copper hj_civ matches 1 if items entity @s weapon.offhand minecraft:copper_ingot run function human_journey:tech/revert/copper
execute as @a[tag=hj_student] unless score #tech_copper hj_civ matches 1 if items entity @s hotbar.* minecraft:copper_ingot run function human_journey:tech/revert/copper
execute as @a[tag=hj_student] unless score #tech_copper hj_civ matches 1 if items entity @s inventory.* minecraft:copper_ingot run function human_journey:tech/revert/copper

execute as @a[tag=hj_student] unless score #tech_gold hj_civ matches 1 if items entity @s weapon.mainhand minecraft:gold_ingot run function human_journey:tech/revert/gold
execute as @a[tag=hj_student] unless score #tech_gold hj_civ matches 1 if items entity @s weapon.offhand minecraft:gold_ingot run function human_journey:tech/revert/gold
execute as @a[tag=hj_student] unless score #tech_gold hj_civ matches 1 if items entity @s hotbar.* minecraft:gold_ingot run function human_journey:tech/revert/gold
execute as @a[tag=hj_student] unless score #tech_gold hj_civ matches 1 if items entity @s inventory.* minecraft:gold_ingot run function human_journey:tech/revert/gold

# Journey: 500+ blocks from Mogadishu (3092, 66, -192).
execute as @a[tag=hj_student,x=3092,y=66,z=-192,distance=500..] unless score #beyond_camp hj_civ matches 1 run function human_journey:tick_checks/beyond_camp

# Clothing: any allowed leather / wool / leaf piece actually equipped.
execute as @a[tag=hj_student] if score #better_edge hj_civ matches 1 unless score #dressed_for_wild hj_civ matches 1 if items entity @s armor.head #human_journey:early_clothing run function human_journey:tick_checks/dressed
execute as @a[tag=hj_student] if score #better_edge hj_civ matches 1 unless score #dressed_for_wild hj_civ matches 1 if items entity @s armor.chest #human_journey:early_clothing run function human_journey:tick_checks/dressed
execute as @a[tag=hj_student] if score #better_edge hj_civ matches 1 unless score #dressed_for_wild hj_civ matches 1 if items entity @s armor.legs #human_journey:early_clothing run function human_journey:tick_checks/dressed
execute as @a[tag=hj_student] if score #better_edge hj_civ matches 1 unless score #dressed_for_wild hj_civ matches 1 if items entity @s armor.feet #human_journey:early_clothing run function human_journey:tick_checks/dressed

# Europe approximation, only after Jengo's Land Beyond event.
execute as @a[tag=hj_student,x=-1700,y=-64,z=-6500,dx=5800,dy=450,dz=4150] if score #land_beyond hj_civ matches 1 unless score #new_continent hj_civ matches 1 run function human_journey:tick_checks/europe

# Era 0 capstone: ALL THREE branch endpoints are required.
execute if score #rest_at_last hj_civ matches 1 if score #carry_what_you_can hj_civ matches 1 if score #new_continent hj_civ matches 1 unless score #we_can_stay hj_civ matches 1 run function human_journey:tick_checks/era0_complete
