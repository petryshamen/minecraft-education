execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/place_to_work
execute unless entity @s[tag=hj_student] run return 0
execute unless score #first_wood hj_civ matches 1 run advancement revoke @s only human_journey:internal/tools/place_to_work
execute unless score #first_wood hj_civ matches 1 run return 0
execute if score #place_to_work hj_civ matches 1 run return 0
scoreboard players set #place_to_work hj_civ 1
tellraw @s {"text": "You discovered: A Place to Work", "color": "green"}
function human_journey:sync
