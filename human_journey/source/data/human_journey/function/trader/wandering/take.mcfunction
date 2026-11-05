# Native data paths; each selected offer is copied, then removed from this trader's scratch pool.
$data modify storage human_journey:wandering_trader selected.Recipes append from storage human_journey:wandering_trader work.$(pool)[$(index)]
$data remove storage human_journey:wandering_trader work.$(pool)[$(index)]
