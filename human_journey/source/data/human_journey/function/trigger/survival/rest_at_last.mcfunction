execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/survival/rest_at_last
execute unless entity @s[tag=hj_student] run return 0
execute unless score #reliable_source hj_civ matches 1 run advancement revoke @s only human_journey:internal/survival/rest_at_last
execute unless score #reliable_source hj_civ matches 1 run return 0
execute if score #rest_at_last hj_civ matches 1 run return 0
scoreboard players set #rest_at_last hj_civ 1
tellraw @s {"text": "You discovered: Rest at Last", "color": "green"}
function human_journey:sync
