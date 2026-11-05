# Manual state migration only. Never call from load/tick; safe to repeat before Era 1 is redesigned.
# Preserve all Era 0 milestones, roles, story/NPC data and unrelated technology flags.
scoreboard players set #era1 hj_civ 0
scoreboard players set #tech_agriculture hj_civ 0
advancement revoke @a only human_journey:era1/root
function human_journey:tech/enforce
function human_journey:sync
tellraw @a[tag=hj_participant] {"text":"[Human Journey] Returned to Era 0; Agriculture is locked. Era 0 progress is preserved.","color":"gold"}
