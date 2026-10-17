execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/stone_age
execute unless entity @s[tag=hj_student] run return 0
execute unless score #first_tool hj_civ matches 1 run advancement revoke @s only human_journey:internal/tools/stone_age
execute unless score #first_tool hj_civ matches 1 run return 0
execute if score #stone_age hj_civ matches 1 run return 0
scoreboard players set #stone_age hj_civ 1
tellraw @s {"text": "You discovered: Stone Age", "color": "green"}
function human_journey:sync
