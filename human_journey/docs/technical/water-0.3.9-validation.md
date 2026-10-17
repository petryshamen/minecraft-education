# v0.3.9 validation record

- Local source reconciled with GitHub head b7b7c999a590da7a9ff1e527ee762f4fdae838d7 before editing; no differences in source or tracked documentation.
- Plugin static validation: valid; no errors and no removed identifiers. Only its standard in-game compatibility warning remains.
- Independent comparison against the original v0.3.8 ZIP: 125 existing source files remain byte-identical. Tick gameplay commands are identical; load differs only in release text; sync differs only in water-recipe grants.
- Checked dirty-bottle charcoal output includes minecraft:potion_contents.potion=minecraft:water, normal charcoal/furnace output is the TAN purified bottle, and the removed dirty smelting recipe has no remaining function reference.
- Checked all 12 canteen transitions use minecraft:crafting_transmute with one charcoal and no component overrides; each input/output agrees with the exact installed JAR's item tag and native purifier routes. No empty/purified canteen crafting input or canteen cooking route is added.
- Rechecked Tough As Nails JAR SHA-256; native cooking recipe conflicts were not found among its bundled 45 recipes.
- Broad bottle matching, including effect potions, was explicitly accepted by the user for this test.
- Build workflow validates matching metadata, ZIP root, source bytes and archive hash; deployment repeats validation. Additional independent ZIP CRC/file-set/source-byte checks are run after the build.

Not executed: Minecraft/Forge runtime, gameplay, recipe-screen matching, actual component transfer, mod compatibility, or role scenarios. No claim of in-game testing. Follow water-0.3.9-test-checklist.md, including partial/named canteens and save/reload. Installed mod code was never executed by inspection; no trusted decompiler is configured. Public author source supplements installed-JAR resource and constant-pool evidence.
