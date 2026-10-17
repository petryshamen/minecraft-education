recipe give @a[tag=hj_participant] human_journey:primitive_water_purification
recipe give @a[tag=hj_participant] human_journey:furnace_dirty_water
recipe give @a[tag=hj_participant] human_journey:furnace_normal_water
advancement grant @a[tag=hj_participant] only human_journey:era0/root
execute if score #first_flame hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/survival/first_flame
execute if score #ready_to_eat hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/survival/ready_to_eat
execute if score #water_for_life hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/survival/water_for_life
execute if score #reliable_source hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/survival/reliable_source
execute if score #rest_at_last hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/survival/rest_at_last
execute if score #first_wood hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/first_wood
execute if score #place_to_work hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/place_to_work
execute if score #first_tool hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/first_tool
execute if score #stone_age hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/stone_age
execute if score #better_edge hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/better_edge
execute if score #dressed_for_wild hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/dressed_for_wild
execute if score #carry_what_you_can hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/tools/carry_what_you_can
execute if score #beyond_camp hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/journey/beyond_camp
execute if score #stranger_from_afar hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/journey/stranger_from_afar
execute if score #land_beyond hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/journey/land_beyond
execute if score #new_continent hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/journey/new_continent
execute if score #we_can_stay hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era0/we_can_stay
execute if score #era1 hj_civ matches 1 run advancement grant @a[tag=hj_participant] only human_journey:era1/root

# JENGO QUEST STAGE
# Easy NPC reads this per-player scoreboard in real time.
#
# 0 = Beyond the Camp not completed
# 1 = first meeting available
# 2 = Jengo's "land beyond" story available
# 3 = Europe quest active; player has not reached Europe yet
# 4 = Europe reached; final/post-quest dialog
scoreboard players set @a[tag=hj_participant] hj_jengo 0
execute if score #beyond_camp hj_civ matches 1 run scoreboard players set @a[tag=hj_participant] hj_jengo 1
execute if score #stranger_from_afar hj_civ matches 1 run scoreboard players set @a[tag=hj_participant] hj_jengo 2
execute if score #land_beyond hj_civ matches 1 run scoreboard players set @a[tag=hj_participant] hj_jengo 3
execute if score #new_continent hj_civ matches 1 run scoreboard players set @a[tag=hj_participant] hj_jengo 4

# TRADER QUEST STAGE
# 0 = first reliable trade not yet confirmed
# 1 = A Reliable Source completed; use a post-trade/fallback dialog
scoreboard players set @a[tag=hj_participant] hj_trader 0
execute if score #reliable_source hj_civ matches 1 run scoreboard players set @a[tag=hj_participant] hj_trader 1
