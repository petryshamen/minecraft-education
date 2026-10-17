execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/survival/first_flame
execute unless entity @s[tag=hj_student] run return 0
execute if score #first_flame hj_civ matches 1 run return 0
scoreboard players set #first_flame hj_civ 1
tellraw @s {"text": "You discovered: The First Flame", "color": "green"}
function human_journey:sync
