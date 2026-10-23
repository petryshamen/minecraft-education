# Human Journey changelog

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
