execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/better_edge
execute unless entity @s[tag=hj_student] run return 0
execute unless score #stone_age hj_civ matches 1 run advancement revoke @s only human_journey:internal/tools/better_edge
execute unless score #stone_age hj_civ matches 1 run return 0
execute if score #better_edge hj_civ matches 1 run return 0
scoreboard players set #better_edge hj_civ 1
tellraw @s {"text": "You discovered: A Better Edge", "color": "green"}
function human_journey:sync
