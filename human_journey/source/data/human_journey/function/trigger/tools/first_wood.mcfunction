execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/first_wood
execute unless entity @s[tag=hj_student] run return 0
execute if score #first_wood hj_civ matches 1 run return 0
scoreboard players set #first_wood hj_civ 1
tellraw @s {"text": "You discovered: First Wood", "color": "green"}
function human_journey:sync
