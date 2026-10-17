clear @s minecraft:copper_ingot 1
give @s minecraft:raw_copper 1
execute if items entity @s inventory.* minecraft:copper_ingot run function human_journey:tech/revert/copper_loop
