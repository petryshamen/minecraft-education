# Human Journey Core 0.15.2 — fullscreen map buttons

Build: https://github.com/petryshamen/minecraft-education/actions/runs/44871786464
Source: `2da36e2a0d143d3ed61b1bdc226dd85e6ce33f78`
Runtime SHA-256: `1efd008f07fa1486e350f13d6b58e5f722372a9798dc871130fc387b16561699`

Small placement patch only. Both HJ Borders and HJ Features buttons are centred at75%of the GUI width, between centre and right edge, with top edges y64/88 below the coordinate strip. At1000GUI width they begin at x691 and have118×20GUI-pixel bounds. On a narrow GUI, placement is clamped inside the screen and avoids the compass. Nearby right-half columns and bounded lower-row attempts recover when the preferred location is occupied. A fully occupied extreme right rail no longer forces both buttons to disappear. Tiny/fully obstructed screens may still have no safe position; native menus, unsupported/unready map and disabled client settings still hide controls as before.

Only runtime class changed: BoundaryControlLayout. All other337prior runtime classes are byte-identical: HUD, Xaero integration/render/input hooks, modes, geography/discovery/protocol3, geometry budgets, NPC/economy, water/juice/fishing/temperature remain unchanged. Config/datapack migration not required. No production geography, server, installed mod or live world changed.

Validation:503tests pass,0failures (500prior cases retained,2obsolete position assertions migrated,3new layout cases);15no-world Forge checks pass. Tests cover75%placement, a fully occupied right edge, preferred-column fallback, normal GUI sizes, compass/coordinate separation and safe failure when no space exists. All unmodified test/example/schema/source files are byte-identical. Root pack format64 and Java21 bytecode verified.

Manual checklist:
1. Replace the old Core JAR manually and open Xaero's fullscreen map as an HJ participant; both buttons should appear between centre and right edge, below coordinates.
2. Click both buttons and confirm the existing Borders/Features modes still cycle and save; normal map clicks and dragging outside buttons still work.
3. Resize the window/change GUI scale; confirm buttons remain visible without covering coordinates or compass. Native menus temporarily hide them as before.

No Minecraft gameplay/visual testing was performed. Actual appearance and input acceptance remain for the user. Source includes existing examples/tests and frozen test-only Europe fixture; no new datapack is needed. Install manually; this is not a production geography approval.
