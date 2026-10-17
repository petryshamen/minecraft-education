tag @s remove hj_teacher
tag @s add hj_student
tag @s add hj_participant
tellraw @s {"text":"Human Journey role: STUDENT","color":"green"}
function human_journey:tech/enforce
function human_journey:sync
