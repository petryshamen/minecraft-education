# Macro pool is one of the three fixed internal keys; removal prevents duplicates.
$execute store result score #size hj_wt run data get storage human_journey:wandering_trader work.$(pool)
scoreboard players remove #size hj_wt 1
execute store result storage human_journey:wandering_trader args.max int 1 run scoreboard players get #size hj_wt
function human_journey:trader/wandering/random_index with storage human_journey:wandering_trader args
function human_journey:trader/wandering/take with storage human_journey:wandering_trader args
