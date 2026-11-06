# Human Journey NPC geography

## Earth map calibration

The user experimentally calibrated this Earth map against London, Quito, Singapore, New York, Kyiv, Baku, Mogadishu, Cape Town, Buenos Aires, Tokyo, Sydney, Honolulu, Reykjavík, Anchorage, Ushuaia, Oslo and Mexico City. Generate future locations from real longitude/latitude (decimal degrees; east/north positive):

```text
X = round(longitude × 1024 / 15)
Z = round(-latitude × 1024 / 15)
```

The supplied decimal approximation is **68.2666667 blocks per degree**; prefer the exact scale **1024/15** and round only the final X/Z to the nearest block. North is negative Z, south positive Z; west is negative X. This conversion does not determine elevation Y.

| Real coordinate | Minecraft coordinate |
| --- | --- |
| 180°W / 0° / 180°E longitude | X -12288 / 0 / 12288 |
| 90°N / Equator / 90°S latitude | Z -6144 / 0 / 6144 |

The supplied integer polygons below are authoritative for this release; do not recalculate or nudge them from their approximate real-world descriptions. This is a broad context layer, not a political-border or coastline database. It intentionally leaves uncovered positions (including some Pacific islands/polar areas) resolving to `none`; there is no automatic nearest-region fallback or longitude wrap.

## Core geography resource

`data/human_journey/npc_geography/global_macros.json` uses installed Core 0.6.1's unchanged Core 0.6 geography schema: `schema_version: 1`, `macro_regions[]` with `id/dimension/priority/polygon`, and empty `local_areas: []`. Every region is in `minecraft:overworld`. Other dimensions resolve to `none`.

Macro labels in this document are educational/editor labels only: the Core schema does **not** accept `display_label` on a macro region. Highest numeric priority wins intentional overlaps and inclusive shared boundaries. Priority has no historical meaning. An equal highest-priority tie is an explicit Core context error, not an ordering fallback.

| ID | Documentation label | Priority |
| --- | --- | ---: |
| `northern_europe` | Northern Europe | 60 |
| `western_central_europe` | Western & Central Europe | 52 |
| `eastern_europe` | Eastern Europe | 50 |
| `mediterranean_europe` | Mediterranean Europe | 60 |
| `north_africa` | North Africa | 50 |
| `west_central_africa` | West & Central Africa | 40 |
| `east_africa_horn` | East Africa & Horn | 60 |
| `southern_africa` | Southern Africa | 50 |
| `near_east_caucasus` | Near East & Caucasus | 70 |
| `arabian_peninsula` | Arabian Peninsula | 80 |
| `central_asia` | Central Asia | 60 |
| `northern_asia` | Northern Asia | 40 |
| `south_asia` | South Asia | 75 |
| `east_asia` | East Asia | 65 |
| `southeast_asia` | Southeast Asia | 80 |
| `oceania` | Oceania | 50 |
| `north_america` | North America | 30 |
| `mesoamerica_caribbean` | Mesoamerica & Caribbean | 70 |
| `south_america` | South America | 80 |

The JSON is the single editable source of polygon vertices. Validation covers all supplied calibration points (London through Ushuaia), simple non-degenerate polygons, and shared-boundary/overlap precedence. This is structural validation, not a claim of gameplay tests.

## Names, content and context

No local areas, era-dependent geographic labels, regional skins, macro trade sets or seasonal changes are introduced. `/hj npc context` reports a macro ID inside its polygon, while `Local area` and `Location label` remain `none`. Hunter/Fisher/Forager keep their generic visible names and identical base content in every macro. Macro regions never automatically prefix NPC names.

Context is captured once at spawn by Core; existing NPCs are not refreshed by a datapack reload. Era stays read-only: `hj_civ / #era1 = 0` → `era_0`, `1` → `era_1`; economic roles remain Era 0 only. This geography neither writes progression nor affects Core's separate water/salt geography.

## Next geography stage (not implemented)

Add `local_areas[]` polygons inside these macros, referencing the parent `macro_region` and supplying `display_label` (optionally `era_labels`). Planned examples:

- Eastern Europe: Dnieper Basin, Carpathians, Danube Basin, Black Sea Coast, Pontic Steppe.
- East Africa & Horn: Somali Coast, Ethiopian Highlands, Rift Valley.

Local areas will supply educational visible labels such as **Dnieper Fisher**. Macros remain primarily internal context for broad skins, trade availability and cultural/environmental content. Future local boundaries should agree with the highest-priority resolved macro; Core rejects a conflicting declared parent.
