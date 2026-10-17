execute unless entity @s[tag=hj_student] run advancement revoke @s only human_journey:internal/tools/carry_what_you_can
execute unless entity @s[tag=hj_student] run return 0
execute unless score #dressed_for_wild hj_civ matches 1 run advancement revoke @s only human_journey:internal/tools/carry_what_you_can
execute unless score #dressed_for_wild hj_civ matches 1 run return 0
execute if score #carry_what_you_can hj_civ matches 1 run return 0
scoreboard players set #carry_what_you_can hj_civ 1
tellraw @s {"text": "You discovered: Carry What You Can", "color": "green"}
function human_journey:sync
