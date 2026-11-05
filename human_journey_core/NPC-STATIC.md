# NPC Template MVP — Core 0.5.0

**Build verified:** [GitHub Actions run 41145562029](https://github.com/petryshamen/minecraft-education/actions/runs/41145562029), source `2f5677aaf4272967e55800644fbcc81e7ea0e42b`. 63 unit/structural tests passed; the no-world Forge data checks loaded exact Easy NPC 6.5.2 and verified two templates, native data codecs and instance isolation. All 64 pre-existing engine classes, including all 15 temperature classes and the complete water/shoreline/sound integration, are byte-identical to Core 0.4.1. Runtime JAR SHA-256: `ddf6f064a90d45dc03e83c8034e111bad3650de156347bd551cb29f101ba0a33`. No NPC entity/world was created, no appearance/UI/gameplay/persistence test was performed and nothing was installed automatically.

Minecraft Java 1.21.8 / Forge 58.1.18 / Easy NPC Core 6.5.2. Existing Core 0.4.1 temperature, water, salt, shoreline, sound and purifier classes are retained. No Minecraft gameplay testing or automatic installation is performed. Easy NPC handles entity rendering, remote textures, dialogues, conditions, actions, merchant UI and normal entity persistence. Core supplies a reusable content loader and isolated 6.5.2 adapter.

## Manual installation and first spawn

1. Stop and back up your TEST world/server. Replace the existing Core JAR with `human_journey_core-0.5.0.jar`, keeping exactly one Core version. Use matching Core/Easy NPC 6.5.2 and existing TAN/GlitchCore versions on server and clients. Easy NPC Config UI/Bundle need no changes; this adapter uses the Core dependency directly.
2. Place `HumanJourney-NPC-Examples-0.5.0.zip` in the TEST world's datapacks directory, enable it, then `/reload`. It contains no water/temperature policy and does not replace the main Human Journey datapack. Keep all your existing balance packs/settings.
3. As an operator with permission level 2 or higher, stand on open solid ground and run `/hj npc status`, then `/hj npc spawn hunter`. Tab completes both `hunter` and `human_journey:hunter`. `/hj npc spawn hunter Ada` overrides only the new NPC's given name. Console can inspect status, but spawning requires a player location.
4. Right-click the NPC normally with an empty main hand, not the editor wand. Expected content is Kito — Hunter, one dialogue page and a Show your goods button leading to two Advanced merchant offers. Appearance/UI/trading are manual acceptance tests, not established by compilation.

Teachers require operator permission level 2; Teacher datapack tags alone do not grant spawn permission. Ordinary students can interact/trade but cannot use `/hj npc spawn`. Core never grants operator permissions or progression bypasses. Example trades are demonstration balance only.

The Easy NPC dependency is optional for Core's existing mechanics. `/hj npc status` and spawn errors identify an absent or unsupported version; only exact 6.5.2 is accepted by this adapter. Nothing opens/automates Easy NPC's editor or native import/export GUI.

## Resources and reload

Template resource: `data/<namespace>/npc/<path>.json`, yielding `<namespace>:<path>`. The example is `data/human_journey/npc/hunter.json` → `human_journey:hunter`. Resource paths define IDs; an optional JSON `id` must match exactly. Within the same resource ID, ordinary datapack priority selects the highest-priority file. The effective set cannot contain duplicate IDs. Give other systems their own resource paths; this loader owns the `npc/` template resource directory.

`/reload` parses and validates the entire effective set before publishing it. Invalid JSON, unknown fields, bad item IDs/counts, unsupported action/condition types, duplicate page/button/template IDs, unresolved dialogue references and invalid skin URL syntax reject the candidate set. The last valid set and success timestamp remain active; on initial failure the set remains empty. Logs and `/hj npc status` report the offending template/source and reason. Removing all templates validly loads an empty set. At most 256 templates, each at most 256 KiB characters, are accepted. No URL/image is downloaded by the loader; Easy NPC resolves remote images on clients, subject to its URL/cache rules.

Changes affect **future spawns only**. Existing spawned entities keep their configured native Easy NPC data. Reload does not find/rewrite/recreate them. No refresh command, pools, layers, settlement/era/region detection or NPC evolution is implemented. `role` is free Human Journey metadata, never an Easy NPC profession enum or a restriction to Era 0.

## Full Hunter example

```json
{
  "schema_version": 1,
  "name": "Kito",
  "role": "hunter",
  "title": "Hunter",
  "skin_url": "https://raw.githubusercontent.com/MarkusBordihn/BOs-Easy-NPC/f929f51540778bb8bcbb43989e6ff4a4c816ce85/core/Common/src/main/resources/assets/easy_npc/textures/entity/humanoid/humanoid_template.png",
  "behavior": {
    "stationary": true,
    "invulnerable": true
  },
  "dialogue": {
    "main": "main",
    "pages": [
      {
        "id": "main",
        "text": "I hunt animals and trade what I bring back, @initiator.",
        "buttons": [
          {
            "id": "trade",
            "text": "Show your goods",
            "actions": [
              {
                "type": "OPEN_TRADING_SCREEN"
              }
            ]
          }
        ]
      }
    ]
  },
  "trading": {
    "reset_minutes": 0,
    "offers": [
      {
        "input": {
          "item": "minecraft:flint",
          "count": 3
        },
        "output": {
          "item": "minecraft:leather",
          "count": 2
        },
        "uses": 0,
        "maxUses": 4
      },
      {
        "input": {
          "item": "minecraft:stick",
          "count": 8
        },
        "second_input": {
          "item": "minecraft:flint",
          "count": 1
        },
        "output": {
          "item": "minecraft:beef",
          "count": 2
        },
        "uses": 0,
        "maxUses": 3
      }
    ]
  }
}
```

The public, pinned upstream humanoid template texture is an editable example URL, not a project secret or a skin pool. Replace `skin_url` with your own direct remote PNG URL, then reload and spawn a new NPC. It is not a claim that this texture was visually tested in-game.

## Field reference

Formal authoring schema: [`docs/npc-template.schema.json`](docs/npc-template.schema.json). Runtime parsing and registered-item validation remain authoritative.

| Field | MVP meaning |
| --- | --- |
| `schema_version` | 1; optional, other versions rejected |
| `id` | Optional, must equal namespace/path-derived ID |
| `name` | Required 1–64 visible characters; optional command name replaces it for that spawn |
| `role` | Optional string up to 64 characters; saved Human Journey metadata only |
| `title` | Optional string up to 64 characters, appended as `name — title` |
| `skin_url` | Required absolute http(s) URL with host, no credentials/fragment, maximum 2048 characters; direct skin image expected |
| `behavior.stationary` | Default true; native Mob NoAI flag, no custom pathfinding/schedule |
| `behavior.invulnerable` | Default true; native invulnerable flag |
| `dialogue.main` | Local main page ID, default `main`; must exist |
| `dialogue.pages` | 1–16 pages; local IDs use 1–32 lowercase letters/digits/underscores |
| Page `text` | Required, up to 8192 characters; native `@npc`, `@initiator`, `@score(objective)` passed through unchanged |
| Page/button/action `conditions` | Up to 16 SCOREBOARD conditions; all native condition evaluation |
| Page `buttons` | Up to 12; unique local IDs, visible text up to 128 characters, 1–16 actions |
| `trading.offers` | Up to 64; `input`, optional `second_input`, `output` each contain registered non-air `item` ID and `count` (1–64, not above item's actual stack limit) |
| Offer `uses` | Default 0; integer 0–maxUses. Production examples start at 0; explicit nonzero useful for diagnostics |
| Offer `maxUses` | Required integer 1–1,000,000, per-offer Advanced stock limit |
| `trading.reset_minutes` | Default 0 disables timed restock; integer 0–525600, delegated to native Easy NPC reopen-time policy |

Item components/NBT/custom equipment, independent era/region/story layers, multiple model types, variable expansion and execution-limit condition configuration are outside this MVP. Ordinary plain items suffice for the Hunter proof. Native substitution syntax is not evaluated by Core, and text not displayed by Easy NPC until gameplay cannot be validated here.

### SCOREBOARD condition

```json
{"type":"SCOREBOARD","objective":"hj_npc_test","operator":"LESS_THAN","value":3}
```

Operators: EQUALS, NOT_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS. Objective names use 1–16 letters/digits/underscore/dot/hyphen. Conditions can be attached to pages, buttons or actions; missing objectives follow Easy NPC's native behavior. Pages other than main are manual-only and reached by named-dialogue actions. The main page has explicit positive priority so instance labels do not depend on Easy NPC's special words such as `default`.

### Supported actions

```json
{"type":"OPEN_TRADING_SCREEN"}
{"type":"OPEN_NAMED_DIALOG","dialogue":"done"}
{"type":"SCOREBOARD","objective":"hj_npc_test","operation":"INCREASE","value":1}
{"type":"COMMAND","command":"function human_journey:npc/example","execute_as_player":true,"permission_level":2}
```

SCOREBOARD operations: SET, INCREASE, DECREASE. Values/condition thresholds are integers −1,000,000..1,000,000. COMMAND is one line up to 2048 characters, default executes as the initiating player, permission level 0–2; false uses native NPC execution context. Templates are trusted admin-authored content and their commands use those permissions. Conditions may also be attached to any action. Named-dialogue references are local to the same template/NPC and validated before publication. Native action execution remains Easy NPC's responsibility; Core does not execute dialogue actions itself.

## Identity and persistence

- **Template-stable:** resource ID, Human Journey role, local page/button IDs and local dialogue references. Names are content, not identity.
- **Instance-specific:** fresh entity UUID and a separate fresh preset UUID; no source NPC/preset identity is copied. Runtime dialogue/button labels are 32-character deterministic hashes of the full new entity UUID plus local kind/path, fitting Easy NPC's persistence limit. Native dialogue/button UUIDs derive from these scoped labels. Their labels/IDs survive native data save/load and differ across instances.
- **Actions:** each native ActionDataEntry includes the instance entity UUID as its target. This prevents identical template actions across NPCs from sharing the same content-derived native action ID. For named dialogue it deliberately targets that same NPC. Identical actions within one NPC remain identical native actions. Native action IDs use the library's record hash and are not a new Core-managed durable execution-limit system; only SCOREBOARD conditions are exposed here. A future execution-limit extension must account for native hash/cross-JVM behavior rather than assume a stronger guarantee.
- Each spawn creates fresh mutable dialogues, buttons, actions, merchant offers and config objects. Data is applied once. The entity is marked persistent, assigned its native home position and owner, and saved through Easy NPC/Minecraft as an ordinary entity. Core's persistent metadata stores its template ID and role. No custom renderer, recreation loop or every-tick adapter is installed.

Spawn search checks loaded positions on a radius 2–4 ring and up to two blocks above/below the player; it requires full solid non-dangerous support, air space, a liquid-free/entity-free bounding box and world-border compliance. It does not force-load chunks. If no candidate is safe, it reports failure without adding an NPC. Configure/add failures discard the unadded candidate. It does not import native presets or clone NBT.

## Trades: exactly what is initialized and what is not fixed

Core constructs each native MerchantOffer with explicit first/optional second cost, output count, supplied uses/maxUses, zero merchant XP and zero price multiplier. It sets TradingType.ADVANCED and reset minutes/last-reset time, then calls `setTradingOffers`. That setter writes synchronized/stored offers and creates the native runtime merchant COPY. Both initially have the intended uses and maxUses. Each spawned NPC receives independent offers. MaxUses is an NPC-wide transaction quota shared by players, not per-player; stack output is one transaction.

**Known Easy NPC 6.5.2 limitation:** trading increments runtime offers, while save reads stored offers. Uses can therefore roll back after the native runtime list is rebuilt, chunk/entity unload or world/server restart. Advanced mode is not a persistence fix. This MVP deliberately adds no sync mixin, stock workaround or custom trading engine. Existing exhausted stock is not guaranteed durable. `reset_minutes=0` disables intentional timed restock but does not fix that stored/runtime bug. Positive minutes use real elapsed time when trading opens, potentially including downtime. Do not rely on scarcity until your tests establish the required behavior or a separate persistence fix is designed.

## Optional Story Test

The same small example pack also contains `human_journey:story_test`, a second page and native scoreboard/command/named-dialog actions. Before testing, as admin:

```text
/scoreboard objectives add hj_npc_test dummy
/scoreboard players set @s hj_npc_test 0
/hj npc spawn story_test
```

The Test action button is conditioned on score <3, increments it, calls the included `human_journey:npc/example` function as the player, and opens the Done page. Its function only prints a confirmation. This is a disposable adapter check, not a new quest/progression or Jengo system. Existing Human Journey quest objectives/functions are untouched.

## Concise manual gameplay checklist

1. Verify admin `/hj npc status`, dynamic Tab suggestions and `/hj npc spawn hunter`; confirm a normal student cannot spawn. Check a blocked/liquid location reports no safe spawn.
2. Confirm Kito — Hunter, configured remote skin, stationary/invulnerable behavior and normal right-click dialogue without the editor. Check native substitutions.
3. Open both trades; confirm inputs/counts, fresh uses=0 and explicit MaxUses 4/3. Exhaust one offer, try a second player, close/reopen, chunk unload/reload, save/reload and server restart separately. Record the known stock issue rather than treating automated codec checks as gameplay proof.
4. Spawn two Hunters; inspect distinct UUID/preset/dialogue identities; trades/actions should not be shared mutable objects. Test optional name override.
5. Edit name/skin/dialogue/trade/maxUses, `/reload`, spawn again: new content applies only to the new NPC. Invalid item/skin/reference/JSON must retain the previous set and appear in status/logs.
6. Test Story Test setup/button/condition/function/Done/Back pages. Test enabled reset minutes by waiting and reopening a disposable trader.
7. Recheck temperature, cooling, salt containers, shoreline detection, purifier, boiling/filtering and TAN bottle sounds with existing datapacks. No old engine behavior was intentionally changed.

Automated tests establish parsing, contracts, data transformations/codecs and registration checks. They do not establish actual NPC appearance, UI, interaction, stock persistence or safe spawning in a real world. You perform those tests manually.
