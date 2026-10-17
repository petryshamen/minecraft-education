# Era/technology recipe enforcement.
execute unless score #tech_agriculture hj_civ matches 1 run function human_journey:tech/lock/agriculture
execute if score #tech_agriculture hj_civ matches 1 run function human_journey:tech/give/agriculture
execute unless score #tech_iron hj_civ matches 1 run function human_journey:tech/lock/iron
execute if score #tech_iron hj_civ matches 1 run function human_journey:tech/give/iron
execute unless score #tech_copper hj_civ matches 1 run function human_journey:tech/lock/copper
execute if score #tech_copper hj_civ matches 1 run function human_journey:tech/give/copper
execute unless score #tech_gold hj_civ matches 1 run function human_journey:tech/lock/gold
execute if score #tech_gold hj_civ matches 1 run function human_journey:tech/give/gold
execute unless score #tech_diamond hj_civ matches 1 run function human_journey:tech/lock/diamond
execute if score #tech_diamond hj_civ matches 1 run function human_journey:tech/give/diamond
execute unless score #tech_redstone hj_civ matches 1 run function human_journey:tech/lock/redstone
execute if score #tech_redstone hj_civ matches 1 run function human_journey:tech/give/redstone
execute unless score #tech_mass_mining hj_civ matches 1 run function human_journey:tech/lock/mass_mining
execute if score #tech_mass_mining hj_civ matches 1 run function human_journey:tech/give/mass_mining
execute unless score #tech_backpacks hj_civ matches 1 run function human_journey:tech/lock/backpacks
execute if score #tech_backpacks hj_civ matches 1 run function human_journey:tech/give/backpacks
execute unless score #tech_late_game hj_civ matches 1 run function human_journey:tech/lock/late_game
execute if score #tech_late_game hj_civ matches 1 run function human_journey:tech/give/late_game
schedule function human_journey:tech/enforce 1s replace
