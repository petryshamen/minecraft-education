clear @s minecraft:potion 1
give @s toughasnails:dirty_water_bottle 1
execute if items entity @s weapon.mainhand minecraft:potion run function human_journey:water/convert_one
execute if items entity @s weapon.offhand minecraft:potion run function human_journey:water/convert_one
execute if items entity @s hotbar.* minecraft:potion run function human_journey:water/convert_one
execute if items entity @s inventory.* minecraft:potion run function human_journey:water/convert_one
