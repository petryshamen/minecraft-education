clear @s minecraft:gold_ingot 1
give @s minecraft:raw_gold 1
execute if items entity @s inventory.* minecraft:gold_ingot run function human_journey:tech/revert/gold_loop
