# Human Journey Core 0.6.0 NPC context

Core resolves mechanics/context; datapacks own roles, availability, labels, skins, dialogue and trade balance. Context is captured **once at spawn**. Existing NPCs are never refreshed on era/season changes or `/reload`.

## Manual installation and first use

Use Minecraft Java 1.21.8, Forge 58.1.18, TAN 21.8.0.2 and Easy NPC 6.5.2. Replace Core 0.5.0 with exactly one `human_journey_core-0.6.0.jar`, manually, in the test client/server. Optional Serene Seasons support is verified against **21.8.0.0**. Missing, unsupported or unavailable season API resolves to `none` with a diagnostic, not a Core crash.

Enable `HumanJourney-NPC-Integration-0.6.0.zip` in a test world's datapacks and run `/reload`. Leave the real Human Journey 0.3.15 progression and existing temperature/water packs unchanged. Disable the old 0.5 NPC **example** pack to avoid confusing two sources for Hunter. This new integration pack writes no progression scores and requires no geography database. It includes Era 0 Hunter/Fisher and the unchanged static Story Test. It deliberately has no Era 1 economic roles.

As an operator with permission level 2:

```text
/hj npc context
/hj npc spawn hunter
/hj npc spawn fisher
/hj npc spawn story_test
/hj npc status
```

In an initialized Era 0 world the first command should report `Era: era_0`, season or `none`, and geography/label `none`. The next commands should create `Hunter` and `Fisher`. Tab shows available current-era roles plus static templates. If the scoreboard is missing, Core reports an unknown era and **does not create or initialize scores**. In a fresh disposable world, initialize progression through the normal Human Journey setup before testing. Teachers specify only the role; economic archetypes reject custom-name arguments. Static/story templates retain `/hj npc spawn <template> [name]` and work independently of era resolution.

## Resource paths and reload

| Resource | Meaning |
|---|---|
| `data/human_journey/core/npc_context.json` | Authoritative era resolver |
| `data/<namespace>/npc_archetype/<role>.json` | Economic archetype; ID is `<namespace>:<role>` |
| `data/<namespace>/npc_geography/<file>.json` | Optional macro/local definitions |
| `data/<namespace>/npc/<template>.json` | Unchanged 0.5 static/story format |

Vanilla resource-pack priority selects each resource path. Context, all geography and archetypes validate as **one atomic set**, including all supported era/location/season compositions and registered trade items. Invalid data logs its resource/pack and preserves the last successful context set/revision. `/hj npc status` and `/hj npc context` show rejected reloads. A valid reload that removes resources removes them from the new set. Static templates retain their independent 0.5 atomic loader. An archetype takes precedence over a static template with the same ID, including wrong-era rejection; there is no Era 0 fallback.

Limits: 32 eras, 16 AND conditions per era, 64 archetypes, 64 geography resources, 64 macro regions and 64 local areas, 256 vertices per polygon, 256 KiB characters per resource, and 16,384 validated composition variants in total. This is intentionally a bounded first release. Missing optional geography is valid. Missing context configuration is valid only when there are no archetypes/geography; static/story spawning still works.

## Era resolver

```json
{"schema_version":1,"eras":[
  {"id":"era_0","conditions":[{"objective":"hj_civ","holder":"#era1","operator":"EQUALS","value":0}]},
  {"id":"era_1","conditions":[{"objective":"hj_civ","holder":"#era1","operator":"EQUALS","value":1}]}
]}
```

`#era1` is a binary flag in the current example, **not a general era counter**. Future rules may use other objectives/holders and multiple conditions. Operators: `EQUALS`, `NOT_EQUALS`, `GREATER_THAN`, `GREATER_THAN_OR_EQUALS`, `LESS_THAN`, `LESS_THAN_OR_EQUALS`. Conditions in each rule are ANDed. Missing scores never match, even NOT_EQUALS. Highest `priority` wins (default 0); a top-priority tie or no match is an explicit error. Logical IDs are lowercase letters/digits/underscore followed by lowercase letters/digits/underscore/dot/hyphen, up to 64 characters; `none`/`unknown` are reserved.

## Archetype and composition

```json
{
  "schema_version":1,
  "role_label":"Fisher",
  "base":{
    "skin_url":"https://example.org/fisher.png",
    "behavior":{"stationary":true,"invulnerable":true},
    "dialogue":{"main":"main","pages":[{"id":"main","text":"I trade fish."}]},
    "trading":{"reset_minutes":0,"offers":[]}
  },
  "eras":{
    "era_0":{
      "content":{},
      "macro_regions":{},
      "local_areas":{},
      "seasons":{"winter":{"dialogue":{"pages":[{"id":"main","text":"Winter fishing is hard."}]}}}
    }
  }
}
```

The example URL above is illustrative; the provided integration pack uses a public pinned example skin. No secret or name/skin pool is needed. Add an era entry explicitly to enable a role there; absence means unavailable. Roles are never listed in Java. Full trade/page/action details use the unchanged [static format](NPC-STATIC.md) and [template schema](docs/npc-template.schema.json).

Exact order: root `role_label` → **base → current era.content → that era.macro_regions[current macro] → that era.local_areas[current local] → that era.seasons[current season]**. Missing layers do nothing. Scalars replace, objects recursively merge, arrays replace completely; input data is copied. No deletion/null operators or scripting exist. Override maps live inside each era and may reference only configured geography IDs or seasons `spring/summer/autumn/winter/none`.

Supported content fields are `role_label`, `skin_url`, `behavior`, `dialogue`, `trading`. Patches cannot override generated name/title/role/identity. Arrays must contain complete valid entries. The fully composed result is validated by the unchanged 0.5 parser, including dialogue references, URL constraints and item registry/stack limits. Role label is 1–48 characters; final name is at most 64. Normal name = optional local label + space + role label, otherwise role label alone. Macro names are never prefixed. Story templates bypass this naming/composition path entirely.

## Optional geography

```json
{
  "schema_version":1,
  "macro_regions":[{"id":"example_region","dimension":"minecraft:overworld","priority":0,"polygon":[[0,0],[100,0],[100,100],[0,100]]}],
  "local_areas":[{"id":"example_river","dimension":"minecraft:overworld","macro_region":"example_region","display_label":"River","era_labels":{"era_0":"River","era_1":"River Valley"},"polygon":[[10,10],[50,10],[50,50],[10,50]]}]
}
```

These are illustrative coordinates, **not Earth-map claims**, and are not enabled by the integration pack. Polygons use X/Z positions including boundaries, are checked for finite valid non-self-intersecting geometry, and apply in their dimension (default Overworld). Coordinates outside all definitions resolve to `none`. IDs must be unique within each geographic level across resources. Parent macros must exist in the same dimension; local areas may omit the parent. A matched local area supplies its declared parent macro when no macro polygon matches. If another macro matches instead, resolution fails clearly. Highest priority wins independently per level; equal top-priority overlaps are rejected at the queried position (not an expensive whole-map overlap scan). Author non-ambiguous polygons or assign priorities. Optional `era_labels` selects an era-specific local label, falling back to `display_label`; blank means no visible geographic prefix.

## Seasons, persistence and compatibility

Only the isolated Serene Seasons adapter references its API: `SeasonHelper.getSeasonState(Level)` → `ISeasonState.getSeason()` → SPRING/SUMMER/AUTUMN/WINTER. This reports the world's four-season cycle, not tropical wet/dry sub-seasons. No balance decision is inferred from the season. Missing provider/state, unsupported version or API error becomes `none`; a `none` content override can be authored explicitly. Season is queried at the command player's dimension/position context and captured at spawn.

The 0.5 Easy NPC 6.5.2 adapter is unchanged: fresh entity UUID, fresh preset UUID, per-instance dialogue/button identifiers, native dialogues/actions/conditions/trades/remote skin and native entity persistence. Resource/template IDs stay stable; runtime identities are fresh per NPC. See [identity and native trade details](NPC-STATIC.md). `reset_minutes=0` disables timed resets only. The accepted Easy NPC behavior restoring exhausted stock across observed reloads remains; no custom persistence replacement is added. Spawning does not rewrite existing NPCs.

## Manual checklist — user gameplay testing only

1. Install one Core 0.6 JAR and the separate integration pack in a disposable test world with normal Era 0 progression initialized. `/reload`; inspect `/hj npc status` for no rejected data.
2. `/hj npc context`: confirm `era_0`, actual season or explanatory `none`, and all geography `none`. Tab after `/hj npc spawn ` should include hunter/fisher/story_test.
3. Spawn Hunter/Fisher twice. Check visible generic names, skin, stationary/invulnerable behavior, main dialogue, trade buttons/offers/Max Uses and independent identities. No editor should be needed.
4. Move the disposable world to Era 1 using its normal progression: hunter/fisher disappear from Tab and explicit requests fail clearly; Story Test still spawns with fixed content/name. Restore Era 0 for further tests.
5. Change an example dialogue/trade/season layer and `/reload`: new NPCs should use new content; existing NPCs should retain their snapshot. Try an invalid item/URL/override: last valid data should stay active and commands/log explain the rejection.
6. Optionally add the illustrated test geography at suitable test coordinates: inside a local polygon check label prefix and parent macro; outside check fallback. Test dimensions, era labels and priority/tie diagnostics.
7. With installed Serene Seasons 21.8.0.0 compare `/hj npc context` with the world's season. In a separate dependency test without it expect `none` and working unrelated systems.
8. Check native chunk unload/world/server restart persistence and accepted stock behavior. Recheck temperature cooling, salt/shoreline classification, stored salt containers, filtering/boiling, purifier, Rain Collector and TAN bottle sounds.

Automated and no-world structural tests do not prove appearance, UI interaction, actual-world season behavior or persistence. No Minecraft gameplay testing, installation, live-server change or main datapack modification is performed by this release.
