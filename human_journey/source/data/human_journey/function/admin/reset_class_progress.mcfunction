tellraw @a[tag=hj_participant] {"text":"[Human Journey] Resetting Human Journey class progress only...","color":"yellow"}
scoreboard players set #first_flame hj_civ 0
scoreboard players set #ready_to_eat hj_civ 0
scoreboard players set #water_for_life hj_civ 0
scoreboard players set #reliable_source hj_civ 0
scoreboard players set #rest_at_last hj_civ 0
scoreboard players set #first_wood hj_civ 0
scoreboard players set #place_to_work hj_civ 0
scoreboard players set #first_tool hj_civ 0
scoreboard players set #stone_age hj_civ 0
scoreboard players set #better_edge hj_civ 0
scoreboard players set #dressed_for_wild hj_civ 0
scoreboard players set #carry_what_you_can hj_civ 0
scoreboard players set #beyond_camp hj_civ 0
scoreboard players set #stranger_from_afar hj_civ 0
scoreboard players set #land_beyond hj_civ 0
scoreboard players set #new_continent hj_civ 0
scoreboard players set #we_can_stay hj_civ 0
scoreboard players set #era1 hj_civ 0
scoreboard players set #tech_agriculture hj_civ 0
scoreboard players set #tech_iron hj_civ 0
scoreboard players set #tech_copper hj_civ 0
scoreboard players set #tech_gold hj_civ 0
scoreboard players set #tech_diamond hj_civ 0
scoreboard players set #tech_redstone hj_civ 0
scoreboard players set #tech_mass_mining hj_civ 0
scoreboard players set #tech_backpacks hj_civ 0
scoreboard players set #tech_late_game hj_civ 0
advancement revoke @a[tag=hj_participant] from human_journey:era0/root
advancement revoke @a[tag=hj_participant] only human_journey:internal/survival/first_flame
advancement revoke @a[tag=hj_participant] only human_journey:internal/survival/ready_to_eat
advancement revoke @a[tag=hj_participant] only human_journey:internal/survival/water_for_life
advancement revoke @a[tag=hj_participant] only human_journey:internal/survival/rest_at_last
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/first_wood
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/place_to_work
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/first_tool
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/stone_age
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/better_edge
advancement revoke @a[tag=hj_participant] only human_journey:internal/tools/carry_what_you_can
scoreboard players set @a[tag=hj_participant] hj_jengo 0
scoreboard players set @a[tag=hj_participant] hj_trader 0
function human_journey:tech/enforce
function human_journey:sync
tellraw @a[tag=hj_participant] {"text":"[Human Journey] Class progress reset complete. Other Minecraft/mod advancements were preserved.","color":"green"}
