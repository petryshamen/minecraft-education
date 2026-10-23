# Current dimension only; callers cover all three vanilla dimensions.
execute as @e[type=minecraft:wandering_trader,tag=!hj_wt_processed] run function human_journey:trader/wandering/initialize
