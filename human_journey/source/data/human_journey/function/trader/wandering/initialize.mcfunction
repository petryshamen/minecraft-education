# Called as one unprocessed vanilla Wandering Trader. Never modify Easy NPC entities.
execute if entity @s[tag=hj_wt_processed] run return 0
# Install NONEMPTY Offers, so vanilla lazy generation cannot replace them on first open.
data modify entity @s Offers set from storage human_journey:wandering_trader era0
# Existing civilization progression: Era 0 iff #era1 is zero. No new era scoreboard.
# Unsupported eras keep nonempty exhausted offers: an empty list is omitted on save,
# which would otherwise allow vanilla offers to regenerate after reloading the entity.
execute unless score #era1 hj_civ matches 0 run data modify entity @s Offers.Recipes[].uses set value 8
# Tag only after the replacement is present; failed initialization remains retryable.
execute if data entity @s Offers.Recipes[5].sell run tag @s add hj_wt_processed
