execute if score #dressed_for_wild hj_civ matches 1 run return 0
scoreboard players set #dressed_for_wild hj_civ 1
tellraw @s {"text":"You discovered: Dressed for the Wild","color":"green"}
function human_journey:sync
