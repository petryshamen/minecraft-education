# Release and migration rules

1. Read current source and reconcile GitHub/local changes.
2. Preserve migration-sensitive identifiers; explain progress impact and obtain explicit approval before breaking changes.
3. Use a patch version for fixes and a minor version for major progression additions. Keep the imported version history.
4. Parse pack.mcmeta and all JSON; reject duplicate keys. Validate local function references, important resource references, expected namespaces, version metadata and clean ZIP root.
5. On any validation failure, do not install.
6. Update changelog, commit/push source to petryshamen/minecraft-education, then install a verified ZIP only into the configured test world.
7. Back up replacements. Remove only tool-managed older Human Journey test ZIPs; preserve unrelated and unrecognized datapacks.
8. Report version, changes, test-world installation YES/NO, GitHub commit YES/NO and specific Minecraft tests.

Static validation cannot prove Forge/mod compatibility, macro resolution, role permissions or full command behavior. Test the relevant Teacher and Student flows in Minecraft.

Never automatically deploy to the live class server. No live-server folder belongs in plugin configuration.
