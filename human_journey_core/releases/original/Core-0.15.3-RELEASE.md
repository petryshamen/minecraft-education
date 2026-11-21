# Human Journey Core 0.15.3 — top-aligned fullscreen buttons

Build: https://github.com/petryshamen/minecraft-education/actions/runs/44961394509
Source: `49dd222b6f7aaeb0291971fbbb627c569a6e48a6`
Runtime SHA-256: `dd5c6387f292bb4628ea3ad9a6dedd6a1f3f58bf0a531b0bbbad56457be637a9`

HJ Borders/Features are now anchored to the top at y8/32GUI units, beside the centred coordinates toward the right. At1000GUI width x703; at the supplied screenshot-equivalent472×248GUI layout x307. The paired controls use a compact36×36GUI-unit compass guard instead of the oversized112×104legacy reservation that pushed them downward. Horizontal collision fallbacks remain; there is no downward search, so they cannot drift into the middle of the map. The separate legacy single-control helper remains unchanged. Tiny/fully obstructed screens may still lack safe space; native menus/map readiness/config gates remain as before.

Only BoundaryControlLayout runtime bytecode changed; all other337classes are byte-identical, including Xaero input/render hooks, HUD, discovery, protocols, geometry budgets and every gameplay system. No config/datapack migration. No installed JAR, production data or live world modified.

Validation:504tests pass, zero failures (503prior cases retained with updated position/compass expectations;1new screenshot-size regression);15no-world Forge checks pass. Screenshot-size regression verifies both buttons stay at top, to the right of a centred130GUI-unit coordinate readout, outside the corner compass. All unmodified source/test/example/schema files, Java21 bytecode/root pack64 metadata and source JAR parity verified. Actual Minecraft visual/gameplay testing was not performed.

Manual check: open fullscreen map at the supplied GUI scale; both buttons should be at the top right beside coordinates, not halfway down. Click/cycle both modes. Resize/change GUI scale; check coordinate/compass clearance and normal map input. Existing mode preferences remain. Long coordinate strings, custom fonts and unusual UI layouts remain manual visual acceptance. Install manually; source and checksums archived with this report.
