tag @s remove hj_student
tag @s add hj_teacher
tag @s add hj_participant
recipe give @s *
tellraw @s {"text":"Human Journey role: TEACHER (your actions do not advance civilization)","color":"gold"}
function human_journey:sync
