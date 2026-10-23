# Keep natural spawning enabled. Catch loaded/new traders before ordinary interaction.
execute in minecraft:overworld run function human_journey:trader/wandering/scan
execute in minecraft:the_nether run function human_journey:trader/wandering/scan
execute in minecraft:the_end run function human_journey:trader/wandering/scan
