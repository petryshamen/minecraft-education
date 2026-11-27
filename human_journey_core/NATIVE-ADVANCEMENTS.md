# Native Minecraft advancement presentation — Core0.17.0

## Scope and access

Open normal Minecraft Advancements. Human Journey keeps its existing native root tabs,26pixel frames/icons, completion visuals, hover tooltips, tabs, toasts and input. No extra HJ Progress click or custom progression screen is used. No scores, criteria, parents, advancement IDs, technologies or saved progression are changed. Only explicitly configured `human_journey` native roots are eligible; ordinary Minecraft/mod tabs are untouched.

Core0.16's HJ Progress header button and keybinding are retired. The old screen/tab host, viewport/routing math, bounded loader/model/mirror/transport and examples remain in source as a dormant Dashboard foundation. Old civilization_progress.json still loads/validates for data compatibility, but has no active score polling, screen entry or student display. `/hj progress status` explicitly labels it retired. No statistics, deaths, water/food counts or history tracking/persistence was added. An approved future Dashboard needs a separately defined data contract; old arbitrary graph data does not become analytics automatically.

## Exact native integration and compatibility

Inspected actual named Minecraft1.21.8 / Forge58.1.18 class bytecode and Forge patches. Optional production Mixin config `human_journey_core.advancements.mixins.json` applies only on that exact version pair; client hooks are in its client-only list. Unsupported versions leave this enhancement inactive. Re-audit/recompile/retest before widening support.

- `AdvancementTab.drawContents` HEAD prepares manual positions before native centering/bounds use. Existing widget x/y fields are changed client-side only; DisplayInfo and parent relationships are never edited.
- A second injection immediately before the FIRST `AdvancementWidget.drawConnectivity(GuiGraphics,int,int,boolean)` runs after the vanilla tiled background and native tab-origin translation/scissor. It draws Era panels and added links behind both native connection passes and native widgets. Native viewport234×113, floored scroll offsets and native widget centre offsets13 match the inspected implementation.
- `AdvancementWidget` bridge exposes exact native visibility predicate `!display.isHidden() || progress!=null && progress.isDone()`, native parent and pixel fields. Only final pixel x/y fields become mutable. Native draw/drawHover/isMouseOver/input methods are NOT overwritten, cancelled or replaced; they naturally read the same moved fields. Scrolling bounds use native minimum(x,y) and maximum(x+28,y+27) conventions; root screen anchoring is preserved during updates.
- A common read-only `PlayerAdvancements` Mixin exposes unmodifiable wrappers of vanilla `visible` and `progress`. No award/revoke/getOrStartProgress or score access occurs. It filters metadata to nodes vanilla already makes drawable; client checks presence/visibility again to handle packet timing.
- Datagen's Mixin side is UNKNOWN even when Forge reports physical CLIENT. A separate **smoke-only** config forces the two client transformations for headless validation in that environment. It is NOT in the runtime manifest and its plugin additionally requires the smoke property and UNKNOWN side. No Screen, world, player or OpenGL session is created by checks.

The feature does **not merge separate native root tabs** or reparent existing advancements. Multiple Era sections work inside one existing vanilla root. Cross-root references reject clearly and fall back to vanilla. Current Era1 root/criteria stay untouched; synthetic style labels use existing Era0 nodes and create no Era1 achievements. Existing native parent links keep vanilla routing; custom route waypoints apply only to additional links. A definition matching the actual native widget parent is suppressed, including a displayless-ancestor parent resolved by native attachToParent.

Other mods replacing the same native classes/render path require manual compatibility testing. Hooks require the exact invocation sites/fields; failure is scoped to this enhancement. Missing presentation or bad data preserves vanilla. Client asset/drawing errors restore original positions; drawing failure disables that tab enhancement until reopening. No global access transformer is installed.

## Resource contract

`data/human_journey/core/advancement_presentation.json`, schema_version1. Normal datapack resource priority replaces this one file; no implicit merge. `/reload` validates atomically. Absent resource sends an empty presentation. Invalid syntax/reference/schema clears active native presentation and logs pack/resource/error, rather than retaining possibly stale layouts; clients restore vanilla positions. Existing advancement progress is unaffected.

Editor schema: `docs/native-advancements.schema.json`. Root has `schema_version` and `tabs`. Each tab has actual displayed vanilla `root` ID and optional `positions`, `connections`, `eras`.

```json
{"schema_version":1,"tabs":[{"root":"human_journey:era0/root","positions":[{"advancement":"human_journey:era0/we_can_stay","x":490,"y":80}],"connections":[{"from":"human_journey:era0/survival/rest_at_last","to":"human_journey:era0/we_can_stay","route":[{"x":450,"y":23},{"x":450,"y":93}]}],"eras":[{"id":"era_0","title":"Era 0","x":0,"y":0,"width":540,"height":195,"color":"#30291D","border":"#E4B767","members":["human_journey:era0/root"]}]}]}
```

Positions and routes are **native canvas GUI pixels**, NOT Minecraft world X/Z, old Dashboard156pixel cards, or advancement DisplayInfo grid units. Native widget size/scale stay vanilla. Positions are optional; all undefined nodes use constructor/native layout coordinates. Zero-based integer x/y within0..8192. Original per-widget positions are retained only while the tab exists, then discarded through weak references; nothing is written to saves. Removing the resource/revoking eligibility restores them.

Connections support many incoming/outgoing edges. No prerequisites are inferred or represented by this schema; fields such as completion/requires are rejected. Native parent connections are NOT drawn again; reverse duplicate physical links and self-links reject. Missing/hidden/not-yet-drawable endpoints yield no extra line. Visible but unobtained native frames may have normal connections: availability here means native widget drawability, not the retired score-based Available state. Default route is orthogonal; optional intermediate `{x,y}` points permit explicit junctions. Lines use vanilla-like black outline/white centre behind native icons. Source order and waypoints decide routing; automatic crossing avoidance is not promised.

## Era style regions

Each region defines stable id/title, integer x/y/width/height, required nonempty `members` advancement IDs, optional solid `color`, optional16×16 tiled client `texture`, optional tint overlay and border/title RGB. Colors accept #RRGGBB or integer0..16777215; defaults dark solid/background gray border. Optional tint is a fixed translucent overlay, not a custom shader. Regions may overlap and paint in JSON order. Labels should have space above/around frames; Core never changes frame or icon rendering.

`texture` is a local client resource such as `minecraft:textures/block/moss_block.png`. Custom PNGs need a separate installed client resource pack/mod asset; a server datapack does not distribute textures. No URLs or file paths are accepted. Missing assets are logged on the client and trigger vanilla fallback for the affected tab, never a crash. Only viewport-intersecting16pixel tiles are drawn, bounding loop cost. No custom textures are required for solid styles.

A region is sent/rendered only if at least one member is currently drawable according to vanilla. Hidden region metadata, hidden positions and links are filtered server-side; hidden/unavailable client widgets are checked again. A deliberately public current Era region may cover a larger authored range, but never reveals hidden advancement titles/criteria. Use appropriate members if a later section title itself is a spoiler. Native advancement packet visibility remains authoritative.

## Synchronization, limits and diagnostics

Independent S2C-only `human_journey_core:native_advancements`, protocol1. Once-per-second per-connection view comparison, sending on visibility/content/connection changes; no full-state traffic every tick. No client requests can unlock anything. Source≤512KiB characters, nesting≤32;8tabs,512aggregate referenced IDs,2048connections,32Era regions,16waypoints/link. Snapshot≤1MiBUTF-8;16KiB raw fragments,24KiB packets,64parts, one pending transaction. Atomic reconstruction; incomplete/stale/conflicting/malformed input retains the previous complete snapshot. Logout/login clears client state. Staging limits are not total JVM heap limits.

Validation checks source advancement JSON resources exist, then checks actual loaded registry display/root ancestry on the server after reload. Data-driven advancements supplied through normal datapack resources are supported; dynamically generated definitions with no resource are not assumed. Each positioned/linked/member advancement must have a native display and belong to that configured root. Internal criteria-only advancements are not widgets. Native tabs exceeding2048actual widgets disable this overlay as a safety bound.

`/hj advancements status` (permission2) reports active source, tab count, compatibility and last native validation error. Client log reports missing textures/hook/draw failures. `/hj progress status` remains a retired-foundation diagnostic, not an active progress UI. Existing commands/permissions are preserved.

## Examples and Developer handoff

The optional one-Era example uses actual existing Era0 advancement IDs. It positions the three real branches and defines three capstone connections. Rendering is **two new links plus the unchanged existing Journey-parent link**, because the third definition intentionally proves duplicate suppression. This produces the requested three-way convergence without double drawing or changing parents.

The separate synthetic two-Era style example reuses those SAME existing Era0 IDs: two colored regions, one with builtin moss texture/tint. Labels are explicitly synthetic A/B; they do not invent/unlock Era1 achievements. Install only ONE example pack at a time in a separate test world. The full source includes the equivalent regression fixture.

Developer should:
1. Review schema/examples, then author the new resource separately in production. Do not modify advancement IDs, parent/criteria, rewards, hj_civ, WE CAN STAY, #era1 or Agriculture for this presentation task.
2. Use full actual advancement IDs, not Core0.16node IDs such as human_journey:first_flame. Choose displayed nodes under one native root; no automatic old-graph conversion is performed.
3. Author pixel positions, extra links/waypoints and member-gated Era rectangles. Leave unspecified positions vanilla. Keep native parent duplicates out of production data even though Core safely suppresses them.
4. Use local built-in16×16textures or solid colors initially. Custom assets require a separately distributed resource pack.
5. `/reload`; inspect `/hj advancements status` and client logs. Invalid/missing resources should restore usable vanilla UI, and old Dashboard entry should stay absent even if civilization_progress.json remains.
6. Test actual native tooltips, frame completion, panning, reopening, root-tab switching and hidden advancement reveal/revoke. All game progression remains the existing datapack's responsibility.

## Manual Minecraft checklist

- Install matching server/client JARs manually; open normal Advancements directly. No HJ Progress button/key or empty Dashboard should be presented.
- Without the new presentation pack, check ordinary vanilla HJ tree, frames, tooltips, completion/toasts, tabs and input. Ordinary Minecraft tabs must remain unchanged.
- Enable the one-Era example on a separate test world. With endpoints made visible through existing approved test/admin progression controls, check three-way WE CAN STAY convergence (2extra+1native), no duplicate Journey link, and moved native hitboxes/tooltips.
- Pan/scroll, resize/change GUI scale, switch tabs and reopen: extra lines and regions stay aligned, native input remains standard.
- Swap to the synthetic two-Era example. Verify textured/tinted A and solid B backgrounds under native frames; no Era1 achievement or unlock is created.
- Check hidden/revoked/missing widgets: no extra lines/secret section labels. Remove/invalidly edit the new resource and /reload: restore vanilla, preserve progress. Check missing client texture diagnostics.
- Verify Students/Teachers and existing HJ survival, NPC/economy, geography/Xaero controls remain unchanged. No progress reset/migration is needed.

Automated/native structural checks are not live Minecraft visual/gameplay verification. User performs the checklist above.
