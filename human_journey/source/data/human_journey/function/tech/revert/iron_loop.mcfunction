clear @s minecraft:iron_ingot 1
give @s minecraft:raw_iron 1
execute if items entity @s inventory.* minecraft:iron_ingot run function human_journey:tech/revert/iron_loop
