execute if score #new_continent hj_civ matches 1 run return 0
scoreboard players set #new_continent hj_civ 1
tellraw @s {"text":"You discovered: A New Continent","color":"green"}
function human_journey:sync
