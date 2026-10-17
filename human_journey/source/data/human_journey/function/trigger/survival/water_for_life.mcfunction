execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/survival/water_for_life
execute unless entity @s[tag=hj_student] run return 0
execute unless score #ready_to_eat hj_civ matches 1 run advancement revoke @s only human_journey:internal/survival/water_for_life
execute unless score #ready_to_eat hj_civ matches 1 run return 0
execute if score #water_for_life hj_civ matches 1 run return 0
scoreboard players set #water_for_life hj_civ 1
tellraw @s {"text": "You discovered: Water for Life", "color": "green"}
function human_journey:sync
