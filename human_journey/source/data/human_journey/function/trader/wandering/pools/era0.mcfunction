# Edit the six barter offers here; /reload refreshes the pool for NEW traders only.
# Minecraft Java 1.21.8: lower-case item count; MerchantOffers uses Offers.Recipes.
# No emeralds, metals, agriculture, redstone, Nether/End or future-tech items.
data modify storage human_journey:wandering_trader era0 set value {Recipes:[]}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:stick","count":16},"sell":{"id":"minecraft:flint","count":2},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:stick","count":16},"sell":{"id":"minecraft:charcoal","count":2},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:flint","count":12},"sell":{"id":"minecraft:leather","count":2},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:leather","count":4},"sell":{"id":"minecraft:white_wool","count":2},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:stick","count":8},"sell":{"id":"minecraft:string","count":4},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
data modify storage human_journey:wandering_trader era0.Recipes append value {"buy":{"id":"minecraft:leather","count":2},"sell":{"id":"minecraft:cooked_rabbit","count":4},"uses":0,"maxUses":8,"rewardExp":false,"xp":0,"priceMultiplier":0.0f,"specialPrice":0,"demand":0}
