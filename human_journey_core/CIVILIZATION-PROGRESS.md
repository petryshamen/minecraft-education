# Civilization Progress — Core 0.16.0 integration contract

This release adds a read-only progression graph. It does not implement progression, unlocks, an advancement replacement, or a second ledger. Shared fake-player scores remain authoritative. No existing progression IDs/functions/advancement resources, roles, saved state, NPC economy, geography, map buttons or other gameplay were changed.

## Access

An additive **HJ Progress** button appears in the normal vanilla advancements screen for `hj_participant` clients when there is safe top-header space (GUI at least320×220, no visible-widget collision). Its tooltip says Human Journey Civilization Progress. Vanilla tabs, listeners, grants/revokes, screen and Back behavior remain unchanged; no advancement Mixin is added. Where another mod/tiny layout occupies that header, the button is omitted safely. In Controls → Human Journey, assign **Civilization Progress** to a key. It is deliberately unbound by default, so it cannot introduce a default mod-key conflict. It opens from normal gameplay for participants. Teachers and Students receive the same shared view; no role assignment changes. A teacher without `hj_participant` is still a non-participant, consistent with current HJ tagging.

Drag left/right mouse to pan, wheel over canvas to zoom (.35–2×, cursor anchored), click a node to select, scroll the detail panel for long content. Arrow keys pan; Home/Fit shows an overview; Back/Escape returns to the originating screen. Initial view starts at .85× for readable labels, not a compressed overview of every Era. Small GUIs put details below the canvas; larger GUIs use a right pane. Only **Civilization Progress** is visible. The current host/tab-ID contract and isolated canvas permit future Core tab implementations; no Technologies/Statistics/Scorecard tabs or arbitrary analytics contract is supplied.

## Resource and version

The single overridden resource is `data/human_journey/core/civilization_progress.json`. Schema1 only. Higher-priority datapacks replace this resource in the normal resource stack; files are not implicitly merged. `/reload` loads and validates atomically. Missing resource clears this presentation and shows a helpful unconfigured-graph message; invalid resource logs pack/resource/error and retains the last complete valid graph. `/hj progress status` is permission-level2 and reports node/connection counts, source, revision, unresolved-reference count and last reload error. No edit/reset/grant command is added.

See `docs/civilization-progress.schema.json` and `example-civilization-progress-datapack/`. The JSON Schema is an editor aid; Core also performs the cross-reference, topology and byte-budget checks listed there. Keep stable namespaced node IDs when editing; existing selection is retained if the node remains. Stable graph reloads do not reset pan or shared progression.

## Nodes and state references

Required root fields: `schema_version:1`, `title`, `eras`, `nodes`, `connections`.

Every node has `id`, `era`, `title`, `description`, an existing `icon` item ID, manual graph `x`/`y`, `category` (`main`, `side`, `discovery`) and a `completion` reference. Optional `requires` is an array of node IDs; `available_when` and `visible_when` are arrays of references. Empty/omitted arrays mean no extra condition. **Every array uses AND. OR is intentionally unsupported and unknown fields such as `requires_any` are rejected.** Categories are visual metadata and add no quests or rules.

Supported references:

```json
{"type":"score","objective":"hj_civ","holder":"#we_can_stay","min":1,"max":1}
```

This matches an existing shared fake-player score inclusively; objective and holder are configurable. Only explicit `#fake_player` holders are supported—no selectors, player UUIDs or per-player advancement completion. Other supported reference types are `{"type":"always"}` and `{"type":"never"}`. There is deliberately no per-player `advancement` evaluator in this first shared-state contract. Advancement IDs remain untouched and may still be displayed through vanilla; Developer maps a milestone to its authoritative shared score. Unsupported reference types reject the resource.

Missing objectives/holders are **unresolved**, not created or silently interpreted as0. The server logs the missing objective/holder set when it changes, and affected nodes stay locked with an uninitialised-state message. This permits normal first-load datapack score initialization. Names not present in a world cannot be inferred from syntax alone; use the logs/admin status to find typos. Core never calls `getOrCreatePlayerScore`, writes an objective, or grants an advancement.

Server state evaluation:
1. If `completion` matches, state is Completed, even if the declared presentation prerequisites are inconsistent with that actual score.
2. Otherwise, if the completion source is known, ALL `requires` node completion references match and ALL `available_when` references match, state is Available.
3. Otherwise it is Locked.

Availability is a server-evaluated read-only presentation of the author-declared gates. It is not a technology unlock and cannot change the datapack's actual progression rules. Additional gameplay conditions must be represented explicitly by Developer if they should appear in this UI. Current numeric value/target interval and visible prerequisite completion are sent for the selected-node details; Core does not invent task counters the existing progression does not store.

`placeholder:true` requires `completion:{"type":"never"}` and a `never` availability condition. The example Era1 placeholder remains locked even after WE CAN STAY. It neither reads nor sets `#era1` or `#tech_agriculture`. `#era1` remains a binary legacy flag, not a general Era index.

## Connections and routing

`connections` entries contain `from`, `to`, `kind`, and optional `route:[[x,y],...]` intermediate waypoints. Nodes may have many incoming and outgoing edges; graph identity is not constrained to a vanilla parent.

`kind:"prerequisite"` draws a solid arrow and must also exist in the target's explicit `requires` array. Every `requires` entry must have its matching prerequisite edge. `kind:"visual"` draws a dashed arrow and never adds any prerequisite. Core does not infer rules from line shape, node position or Era boundary. Self-links, unknown IDs, duplicate same-kind edges and cycles in combined dependencies/connections reject. Default routing uses deterministic orthogonal lanes; author waypoints provide junctions and clearance for dense graphs. Waypoints may be shared to show deliberate convergence. Selection highlights incident arrows. Routes are bounded; dashed rendering uses at most24dash segments per route segment, with viewport pruning. Automatic universal crossing avoidance is not provided; manually route dense graphs.

## Era styling

Each Era has stable `id`, `title`, `color` and `background` as `#RRGGBB`, `x_start`, `x_end` (start<end), integer `order`, and optional AND `visible_when`. Eras are background sections of ONE canvas, not tabs. They have no built-in numeric progression meaning or fixed count. Order sorts section painting deterministically; manual X coordinates decide placement. Keep ranges and nodes sensibly spaced; authors can create overlaps, but Core does not relocate them. Node borders retain Era color independently of Locked/Available/Completed card fills and labels. Cross-Era arrows do not automatically unlock that Era.

## Visibility and confidentiality

The server evaluates all visibility conditions. A node is sent only when both its Era and node conditions match. Hidden node IDs, title, description, icon, coordinates and incident connections are excluded from snapshots. Empty/unrevealed Eras are omitted, and their visibility conditions are never sent. Requirements on a hidden node appear only as an opaque **Unrevealed prerequisite**, without its identity/content/status. Keep intended future placeholder content explicitly visible; do not place secrets in titles of already-visible nodes.

The client receives only sanitized visible content plus server-computed states. It cannot recompute completion from private graph rules and has no serverbound progress action. Client selection/panning is local only. Role revocation sends a new empty view. Login/logout clears the client mirror. New participant connections receive the current shared snapshot, independent of their personal visible advancement history.

## Synchronization and bounds

Separate S2C-only `human_journey_core:civilization_progress` channel, protocol1; other HJ channels unchanged. Server samples required scores once per second and prepares one shared projection. It compares immutable content, sends only on visible content/progress changes, join/reconnect or participant-role changes. There is no per-tick full snapshot traffic and no persistent UI ledger. Progress resets/reloads update the same mirror from authoritative scores, without reconnecting. Ordinary updates preserve camera/selection where possible.

512nodes,2048connections,32Eras;32node prerequisites,16conditions per list,16route points per edge. Text limits: title128, description2048, node ID96, icon128 characters. Coordinates/ranges finite within±100000. Source≤1MiB characters and JSON nesting≤32. A conservative all-revealed UTF-8 snapshot envelope must fit2MiB, including repeated prerequisite labels; otherwise reload rejects even when some content is currently hidden. Do not rely on visibility to bypass byte limits.

16KiB raw fragments,≤24KiB packets,≤128parts,≤2MiB raw snapshot, one pending client transaction; session+monotonic sequence and atomic publication. Identical duplicates are idempotent; incomplete, stale, conflicting, malformed or oversized input retains the previous complete view. Retired sessions bounded to8; reconnect clears mirror state. Staging/raw limits are not total JVM heap limits: decoded records, strings, GUI data and current snapshot coexist. Forge's reliable ordered connection transports a complete valid transaction; this MVP has no periodic heartbeat/resync request. A failed/invalid snapshot recovers on the next content/role update or reconnect, not by changing progression authority.

## Example and Developer handoff

The separate example reproduces the actual Survival(5), Tools & Clothing(7), Journey(4) branches and WE CAN STAY, plus one locked future Era1 placeholder. Names/descriptions/icons and shared-score bindings are frozen from repository commit6d834a4d8ce65693b12d8b788312a106711496e6, datapack0.5.17 (newer than requested0.5.15). Test-only reference bytes retain that checkpoint. No production graph or progression resource is installed/edited. The example adds no functions, rewards, achievements, side quests or score writes.

Developer integration:
1. Review this schema, then add ONLY the graph resource to the main datapack in a separate content release. Do not copy example test references into production.
2. Map each node completion to its existing `hj_civ` flag and explicitly mirror real prerequisite conditions. Use actual shared counters only when they already exist; don't infer them from visual advancements.
3. Keep the three endpoint IDs in WE CAN STAY's `requires` and its three solid edges. Leave Era1 placeholder hard-locked. Do not equate WE CAN STAY with Agriculture or Era1.
4. Author manual positions, Era colors/ranges and optional route waypoints; stable IDs survive graph edits. Add future Era content/side/discovery categories as data only after design approval.
5. Put spoiler conditions on nodes AND Era metadata as appropriate; inspect sanitized output tests before shipping.
6. `/reload`, inspect `/hj progress status` and unknown-score logs. Invalid data must retain the old complete graph. Existing gameplay functions/advancements remain untouched.
7. Test using two Students and a Teacher in a separate test world. Remove the example pack before enabling a production graph so normal pack precedence is unambiguous.

## Manual visual acceptance

- Assign a Controls key; open via it and the advancement button. Verify vanilla advancements/back navigation stay intact; small-header collision uses the key fallback.
- Inspect three branches, pan/zoom, icons, long text, hover/selection and panel scrolling at actual GUI scales. Fit view shows one continuous graph and two differently colored Era sections.
- On a separate test world only, use the existing authorized test/admin progression controls to reach none/one/two/three completed endpoints, then WE CAN STAY; verify correct states. No UI click can grant anything.
- WE CAN STAY completion must leave Era1 placeholder locked and Agriculture unchanged. Check the production `#era1` state independently.
- Have another Student join and compare shared state with a Teacher; progress changes update within roughly1second. Role removal clears presentation; relog/restart reconstructs it from existing scores.
- Test hidden content visibility/revocation, valid graph reload and invalid graph rollback; no secret descriptions/geometry/IDs should arrive early.
- Confirm existing HJ systems and Xaero controls behave unchanged. Actual appearance, UI interaction, world persistence/TPS/FPS are manual tests, not claims made by unit/native checks.

## Verified automated result

Verified0.16.0: [Actions build45300539036](https://github.com/petryshamen/minecraft-education/actions/runs/45300539036), source `26a10502424fc0bce0cb9cc0f9249d0cf7c51976`.550tests pass (504unchanged prior+46new), zero failures;16no-world Forge checks. 337prior runtime classes byte-identical; only existing bootstrap class adds one isolated registration. Runtime SHA-256 `705bf8a2aa837fb953e5f796459acadfd7f4b741158ab36b4fbd7c6fe064649e`. No live Minecraft gameplay/visual testing, installation or production progression/data changes.

512nodes /2048connections: parse+validate+conservative envelope median28.925ms; projection3.373ms; transfer preparation5.354ms; sanitized snapshot354366UTF-8 bytes in22fragments. Two warmups/median5 on GitHub runner; no gameplay/TPS/FPS/real UI rendering measurement.
