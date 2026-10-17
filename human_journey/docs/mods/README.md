# Human Journey mod integration notes

These are our observations about exact installed mods, not redistributed mod files. Never commit JARs, classes or decompiled source. The plugin reads the configured mods folder only; project documentation writes stay here.

Each note records parsed metadata, filename and SHA-256 in a machine-readable identity comment. Use mod_compare_notes before integration changes; a different filename, version or hash requires reinspection. Missing evidence is an unresolved question, not permission to guess.

Current notes: tough_as_nails.md, easy_npc.md, packedup.md and only_hammers_and_excavators.md. Generic inspection tools support future mods without hardcoded filenames. Start with metadata and resources; use constant-pool inspection and, only when necessary and configured, trusted CFR decompilation. Never execute a mod.

Recipe/resource references and asset-derived candidate item IDs are different evidence levels. Preserve stateful items and current Human Journey progression; explain any potential state loss before changing recipes. Live-server deployment remains manual.
