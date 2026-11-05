# Called as one unprocessed vanilla Wandering Trader; Easy NPCs are untouched.
execute if entity @s[tag=hj_wt_processed] run return 0
# Scratch storage is used synchronously for one trader at a time; the source pool stays immutable.
data modify storage human_journey:wandering_trader work set from storage human_journey:wandering_trader pools
data modify storage human_journey:wandering_trader selected set value {Recipes:[]}
data modify storage human_journey:wandering_trader args set value {pool:"common"}
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
data modify storage human_journey:wandering_trader args.pool set value "rare"
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
execute store result score #bonus hj_wt run random value 1..20
data modify storage human_journey:wandering_trader args.pool set value "common"
execute if score #bonus hj_wt matches 1 run data modify storage human_journey:wandering_trader args.pool set value "exceptional"
function human_journey:trader/wandering/draw with storage human_journey:wandering_trader args
# Nonempty offers suppress vanilla lazy generation before the next trade packet.
data modify entity @s Offers set from storage human_journey:wandering_trader selected
# Keep unsupported-era offers nonempty but exhausted, including across native entity save/reload.
execute unless score #era1 hj_civ matches 0 run data modify entity @s Offers.Recipes[].uses set value 3
execute if data entity @s Offers.Recipes[5].sell run tag @s add hj_wt_processed
data remove storage human_journey:wandering_trader work
data remove storage human_journey:wandering_trader selected
data remove storage human_journey:wandering_trader args
