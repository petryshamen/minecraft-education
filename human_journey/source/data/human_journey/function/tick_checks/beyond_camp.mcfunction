execute if score #beyond_camp hj_civ matches 1 run return 0
scoreboard players set #beyond_camp hj_civ 1
tellraw @s {"text":"You discovered: Beyond the Camp","color":"green"}
function human_journey:sync
