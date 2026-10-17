execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/first_tool
execute unless entity @s[tag=hj_student] run return 0
execute unless score #place_to_work hj_civ matches 1 run advancement revoke @s only human_journey:internal/tools/first_tool
execute unless score #place_to_work hj_civ matches 1 run return 0
execute if score #first_tool hj_civ matches 1 run return 0
scoreboard players set #first_tool hj_civ 1
tellraw @s {"text": "You discovered: First Tool", "color": "green"}
function human_journey:sync
