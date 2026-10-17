execute unless entity @s[tag=hj_student] run tellraw @s {"text":"Only a STUDENT can advance civilization. Use /function human_journey:setup/student when testing.","color":"red"}
execute unless entity @s[tag=hj_student] run return 0
execute unless score #water_for_life hj_civ matches 1 run tellraw @s {"text": "Previous milestone required before A Reliable Source.", "color": "yellow"}
execute unless score #water_for_life hj_civ matches 1 run return 0
execute if score #reliable_source hj_civ matches 1 run return 0
scoreboard players set #reliable_source hj_civ 1
tellraw @s {"text": "You discovered: A Reliable Source", "color": "green"}
function human_journey:sync
