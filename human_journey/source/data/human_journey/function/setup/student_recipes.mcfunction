# Grant the complete recipe set, then immediately subtract current technology locks.
# Never run this as a Teacher; no progression or quest state is changed.
execute unless entity @s[tag=hj_student] run return 0
recipe give @s *
function human_journey:tech/enforce
tag @s add hj_recipes_initialized
