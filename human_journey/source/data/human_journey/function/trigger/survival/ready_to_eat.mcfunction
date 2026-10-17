execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/survival/ready_to_eat
execute unless entity @s[tag=hj_student] run return 0
execute unless score #first_flame hj_civ matches 1 run advancement revoke @s only human_journey:internal/survival/ready_to_eat
execute unless score #first_flame hj_civ matches 1 run return 0
execute if score #ready_to_eat hj_civ matches 1 run return 0
scoreboard players set #ready_to_eat hj_civ 1
tellraw @s {"text": "You discovered: Ready to Eat", "color": "green"}
function human_journey:sync
