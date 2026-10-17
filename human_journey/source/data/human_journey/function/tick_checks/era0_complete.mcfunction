scoreboard players set #we_can_stay hj_civ 1
scoreboard players set #era1 hj_civ 1
scoreboard players set #tech_agriculture hj_civ 1
function human_journey:tech/give/agriculture
tellraw @a[tag=hj_participant] {"text":"ERA 0 COMPLETE — WE CAN STAY","color":"gold","bold":true}
tellraw @a[tag=hj_participant] {"text":"New technology: AGRICULTURE","color":"green"}
function human_journey:sync
