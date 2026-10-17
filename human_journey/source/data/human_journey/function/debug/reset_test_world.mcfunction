tellraw @a[tag=hj_participant] {"text":"[Human Journey] Civilization progress RESET.","color":"red"}
scoreboard players reset #first_flame hj_civ
scoreboard players reset #ready_to_eat hj_civ
scoreboard players reset #water_for_life hj_civ
scoreboard players reset #reliable_source hj_civ
scoreboard players reset #rest_at_last hj_civ
scoreboard players reset #first_wood hj_civ
scoreboard players reset #place_to_work hj_civ
scoreboard players reset #first_tool hj_civ
scoreboard players reset #stone_age hj_civ
scoreboard players reset #better_edge hj_civ
scoreboard players reset #dressed_for_wild hj_civ
scoreboard players reset #carry_what_you_can hj_civ
scoreboard players reset #beyond_camp hj_civ
scoreboard players reset #stranger_from_afar hj_civ
scoreboard players reset #land_beyond hj_civ
scoreboard players reset #new_continent hj_civ
scoreboard players reset #we_can_stay hj_civ
scoreboard players reset #era1 hj_civ
scoreboard players reset #tech_agriculture hj_civ
scoreboard players reset #tech_iron hj_civ
scoreboard players reset #tech_copper hj_civ
scoreboard players reset #tech_gold hj_civ
scoreboard players reset #tech_diamond hj_civ
scoreboard players reset #tech_redstone hj_civ
scoreboard players reset #tech_mass_mining hj_civ
scoreboard players reset #tech_backpacks hj_civ
scoreboard players reset #tech_late_game hj_civ
advancement revoke @a[tag=hj_participant] from human_journey:era0/root
advancement revoke @a everything
function human_journey:sync
function human_journey:tech/enforce
