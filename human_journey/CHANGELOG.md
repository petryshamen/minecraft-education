# Human Journey changelog

## 0.3.9

- Water Bottle + Charcoal -> Purified Water Bottle; normal bottled water remains boilable.
- Dirty Water Bottle + Charcoal -> normal Water Bottle; a second charcoal step produces purified water. Removed direct dirty-water smelting.
- Added component-preserving charcoal recipes for leather/copper/iron/gold/diamond/netherite canteens, using dirty -> normal -> purified stages.
- Canteen furnace purification is intentionally unavailable: vanilla static smelting results cannot preserve remaining drinks, names, enchantments or other item data.
- Inspected the installed Tough As Nails 21.8.0.2 JAR and recorded state evidence, source corroboration, limitations and a concrete test checklist.
- User explicitly approved broad minecraft:potion matching for this test: normal-water furnace and charcoal recipes also accept effect potions.
- Progress identifiers, technology locks, advancements, NPC logic, Teacher/Student roles and era logic are preserved. Water recipe grants and version text only were adjusted in existing functions. No progress reset or migration required.

## 0.3.8

- Imported the supplied v0.3.8 as current development source without gameplay edits or identifier changes.
- Added development version metadata; retained pack format 81 and Minecraft target 1.21.8.
- Preserved all nine supplied releases as original ZIPs with SHA-256 checksums on GitHub.
- Retained bundled historical notes under docs/imported/v0.3.8; only the newest version is built for the configured test world.

## Historical imports

Original archives supplied: 0.1, 0.2, 0.3.1, 0.3.2, 0.3.3, 0.3.4, 0.3.5, 0.3.6, 0.3.8. No 0.3.7 archive was supplied. Historical packages are preserved unchanged and have not been certified by current validation.

## Unreleased

- Established source, test-build and documentation structure before source import.
