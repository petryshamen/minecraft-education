execute unless entity @s[tag=hj_student] run tellraw @s {"text":"Only a STUDENT can advance civilization. Use /function human_journey:setup/student when testing.","color":"red"}
execute unless entity @s[tag=hj_student] run return 0
execute unless score #beyond_camp hj_civ matches 1 run tellraw @s {"text": "Previous milestone required before A Stranger from Afar.", "color": "yellow"}
execute unless score #beyond_camp hj_civ matches 1 run return 0
execute if score #stranger_from_afar hj_civ matches 1 run return 0
scoreboard players set #stranger_from_afar hj_civ 1
tellraw @s {"text": "You discovered: A Stranger from Afar", "color": "green"}
function human_journey:sync
