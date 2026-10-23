# This trigger runs synchronously after interaction opens the menu and before another
# trade packet can execute. It closes the spawn-between-ticks server-side offer window.
# Existing processed traders are NEVER rewritten, so uses and stock remain persistent.
advancement revoke @s only human_journey:internal/wandering_trader_interaction
execute unless entity @e[type=minecraft:wandering_trader,tag=!hj_wt_processed] run return 0
function human_journey:trader/wandering/scan
# The first screen may contain stale vanilla display data; the server has new Offers.
tellraw @s {"text":"[Human Journey] Trader offers updated. Close and reopen trading to refresh the list.","color":"dark_green"}
