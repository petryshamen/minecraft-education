scoreboard objectives add hj_civ dummy
scoreboard objectives add hj_jengo dummy
scoreboard objectives add hj_trader dummy
gamerule doLimitedCrafting true
execute unless score #first_flame hj_civ matches 0.. run scoreboard players set #first_flame hj_civ 0
execute unless score #ready_to_eat hj_civ matches 0.. run scoreboard players set #ready_to_eat hj_civ 0
execute unless score #water_for_life hj_civ matches 0.. run scoreboard players set #water_for_life hj_civ 0
execute unless score #reliable_source hj_civ matches 0.. run scoreboard players set #reliable_source hj_civ 0
execute unless score #rest_at_last hj_civ matches 0.. run scoreboard players set #rest_at_last hj_civ 0
execute unless score #first_wood hj_civ matches 0.. run scoreboard players set #first_wood hj_civ 0
execute unless score #place_to_work hj_civ matches 0.. run scoreboard players set #place_to_work hj_civ 0
execute unless score #first_tool hj_civ matches 0.. run scoreboard players set #first_tool hj_civ 0
execute unless score #stone_age hj_civ matches 0.. run scoreboard players set #stone_age hj_civ 0
execute unless score #better_edge hj_civ matches 0.. run scoreboard players set #better_edge hj_civ 0
execute unless score #dressed_for_wild hj_civ matches 0.. run scoreboard players set #dressed_for_wild hj_civ 0
execute unless score #carry_what_you_can hj_civ matches 0.. run scoreboard players set #carry_what_you_can hj_civ 0
execute unless score #beyond_camp hj_civ matches 0.. run scoreboard players set #beyond_camp hj_civ 0
execute unless score #stranger_from_afar hj_civ matches 0.. run scoreboard players set #stranger_from_afar hj_civ 0
execute unless score #land_beyond hj_civ matches 0.. run scoreboard players set #land_beyond hj_civ 0
execute unless score #new_continent hj_civ matches 0.. run scoreboard players set #new_continent hj_civ 0
execute unless score #we_can_stay hj_civ matches 0.. run scoreboard players set #we_can_stay hj_civ 0
execute unless score #era1 hj_civ matches 0.. run scoreboard players set #era1 hj_civ 0
execute unless score #tech_agriculture hj_civ matches 0.. run scoreboard players set #tech_agriculture hj_civ 0
execute unless score #tech_iron hj_civ matches 0.. run scoreboard players set #tech_iron hj_civ 0
execute unless score #tech_copper hj_civ matches 0.. run scoreboard players set #tech_copper hj_civ 0
execute unless score #tech_gold hj_civ matches 0.. run scoreboard players set #tech_gold hj_civ 0
execute unless score #tech_diamond hj_civ matches 0.. run scoreboard players set #tech_diamond hj_civ 0
execute unless score #tech_redstone hj_civ matches 0.. run scoreboard players set #tech_redstone hj_civ 0
execute unless score #tech_mass_mining hj_civ matches 0.. run scoreboard players set #tech_mass_mining hj_civ 0
execute unless score #tech_backpacks hj_civ matches 0.. run scoreboard players set #tech_backpacks hj_civ 0
execute unless score #tech_late_game hj_civ matches 0.. run scoreboard players set #tech_late_game hj_civ 0
schedule function human_journey:tech/enforce 1s replace

# Human Journey controls offers, not the vanilla spawning schedule or llamas.
gamerule doTraderSpawning true
function human_journey:trader/wandering/pools/era0
function human_journey:trader/wandering/tick
tellraw @a[tag=hj_participant] {"text":"[Human Journey] v0.3.13 loaded.","color":"dark_green"}
function human_journey:sync
