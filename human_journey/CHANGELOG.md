# Human Journey changelog

## 0.3.14

- Adopted installed Core 0.4.0 water schema: NORMAL 75% Thirst / 30 s; retained DIRTY 100% Thirst / 30 s and Poison I / 8 s. SALT restores no thirst/hydration, adds 12 exhaustion and Nausea I / 8 s, with no Poison or manual treatment route.
- Added ocean-tag classification and the supplied Caspian polygon; enabled instant purifier filling from adjacent NORMAL/DIRTY source water for 100 native filter units. Temperature, manual treatment timing, Rain Collector, progression/technology gates, Water for Life and role recipe initialization are unchanged. No Core changes or gameplay testing.

## 0.3.13

- Migrated water treatment to installed Core 0.3.0 policy: normal 60% Thirst / 30 s; dirty 100% Thirst / 30 s plus Poison I / 8 s; consume one sand/charcoal for DIRTY→NORMAL, then furnace/campfire NORMAL→PURIFIED in 500 ticks. Removed 15 legacy recipes/grants and disabled blanket potion conversion while preserving its function ID.
- Water for Life now accepts purified bottles and all purified canteens, preserving milestone IDs/prerequisites. Temperature policy, Student/Teacher recipe setup, technology gates, Rain Collector and Water Purifier mechanics remain unchanged; no mod JAR changes.

## 0.3.12

- Added Core 0.2.0 datapack temperature policy: extra exhaustion/sec WARM 0.5, Overworld HOT 2.0, Nether HOT 3.0; preserved established water relief 20 / cooling interval 10.
- Enabled Overworld heat damage at 1 HP every 160 ticks after TAN buildup; retained Nether 1 HP / 40 ticks and enabled Nether-only Climate Clemency removal. Cold, progression, roles, recipes and NPC logic are unchanged; Core JAR/configs are untouched.

## 0.3.11

- Student setup now grants the full recipe set and immediately reapplies the existing civilization technology restrictions; existing Students receive a one-time repair on their next tick, including after an offline login. Teacher behavior and all progression IDs remain unchanged.
- Human Journey Core 0.1.0 development is separate from this datapack; no mod binaries or Easy NPC stock workaround are included.

## 0.3.10

- Added six editable Era 0 Wandering Trader barter offers, once-only initialization and a synchronous first-interaction guard; natural spawning remains enabled. Existing #era1 progression controls availability; new traders outside Era 0 have exhausted offers. Easy NPC/Jengo and existing progression are unchanged.
- Offers use the installed Java 1.21.8 format. If a trader is first opened between spawn and the next tick, the guard replaces server offers before a trade action; close/reopen refreshes the rare stale first screen. Processed traders retain stock/uses across reloads.
- Temperature configuration was not changed: its directory is outside current writable permissions. Requested manual edits in .minecraft/config/toughasnails/temperature.toml: immersion.wet_temperature_change=-2 and general.player_temperature_change_delay=50; all other settings stay unchanged.

## 0.3.9
- Reworked bottle purification: charcoal filters dirty water, then purifies normal water; normal bottled water remains boilable.
- Added component-preserving charcoal purification for all six canteen materials; no canteen furnace route.
- Preserved progression/role restrictions. Broad drinkable-potion matching was explicitly accepted for this test.

## 0.3.8
- Imported the supplied datapack as current source, preserving gameplay and progress identifiers.
- Archived all nine original supplied versions unchanged; retained useful original setup notes.

## Historical imports
- Original archives: 0.1, 0.2, 0.3.1, 0.3.2, 0.3.3, 0.3.4, 0.3.5, 0.3.6, 0.3.8. No 0.3.7 archive was supplied.
