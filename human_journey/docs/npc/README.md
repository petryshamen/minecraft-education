# NPC architecture

Keep dialogs, trades, conditions and quests in versioned datapack data files with stable IDs and a schema_version. The future Human Journey NPC mod should read this data as an engine, keeping content outside its JAR.

Preserve existing NPC function references and quest-stage objectives. Separate role checks and progression state from rendering or interaction adapters. Isolate Easy NPC integration when necessary; do not make it the owner of new quest logic.

Record the actual current IDs and schemas during source import; no speculative quest format is imposed on existing content.
