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

The supplied global macro polygons remain authoritative and unchanged; do not recalculate them from approximate real-world descriptions. Europe local polygons follow the supplied coordinates except the minimal parent-boundary corrections documented below. This is a broad context layer, not a political-border or coastline database. It intentionally leaves uncovered positions (including some Pacific islands/polar areas) resolving to `none`; there is no automatic nearest-region fallback or longitude wrap.

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

Europe Local Areas v1 adds local display labels only. `/hj npc context` reports the highest-priority matching local ID and label, or `none` in intentional gaps. Core prefixes new NPC names using only that local label (e.g. **Dnieper Fisher**, **Alps Hunter**, **Finnish Lakeland Forager**). Without a local label the names remain **Hunter**, **Fisher**, **Forager**. Macro names never become prefixes. Trades, skins and all other archetype content are identical in every location; no era labels, regional content overrides or seasonal changes are added.

Context is captured once at spawn by Core; existing NPCs are not refreshed by a datapack reload. Era stays read-only: `hj_civ / #era1 = 0` → `era_0`, `1` → `era_1`; economic roles remain Era 0 only. This geography neither writes progression nor affects Core's separate water/salt geography.

## Europe Local Areas v1

Five geography resources are now loaded together: the unchanged `global_macros.json` and four files containing only `schema_version: 1` / `local_areas[]`:

| Resource under `data/human_journey/npc_geography/` | Parent macro | Areas |
| --- | --- | ---: |
| `northern_europe.json` | `northern_europe` | 16 |
| `western_central_europe.json` | `western_central_europe` | 13 |
| `eastern_europe.json` | `eastern_europe` | 9 |
| `mediterranean_europe.json` | `mediterranean_europe` | 17 |

All 55 local IDs and priorities are unique. Priority is technical precedence only; the highest-priority matching local wins, independently of macro priority. Each local declares Overworld dimension, parent macro, `display_label` and polygon. No `era_labels` are supplied. The set uses 55/64 local slots, 19/64 macros, 5/64 geography resources, and 1,125/16,384 composed NPC variants (3 archetypes × 75 context locations × 5 season states). Era 1 remains recognized but has no economic roles.

### Minimal boundary corrections

Every original supplied polygon is simple/non-degenerate. However Core checks the winning local's parent against the independently resolved macro; valid polygon geometry alone does not prevent runtime context conflicts:

- `eastern_baltic_coast` reached Z -3741 and `danish_lowlands` reached Z -3748, south of Northern Europe's -3755 edge, where Western/Central Europe wins. Clip only those southern slivers to **Z <= -3755**.
- `eastern_carpathians`, `dniester_basin`, `pripyat_marshes` and `pontic_steppe` touched **X = 1707**, owned by the higher-priority Western/Central macro on the inclusive boundary. Clip them to **X >= 1708**, the minimum whole-block inset. Some conflicts were masked by higher-priority neighboring locals; parent containment is corrected consistently for all four.

Half-plane clipping preserves unaffected vertices/edges and stores new intersection coordinates to six decimal places where necessary. IDs, labels, priorities and macros are unchanged. The source JSON contains the exact corrected vertices. All other 49 local polygons remain exactly as supplied.

### Representative structural checks

These X/Z points are useful for manual context checks; choose a safe elevation rather than teleporting to an assumed Y.

| Point | X, Z | Expected local |
| --- | --- | --- |
| Western Norway / Bergen | 363, -4123 | `norwegian_fjord_coast` |
| Inland Norway/Sweden border | 819, -4369 | `scandinavian_mountains` |
| Central Finland | 1758, -4249 | `finnish_lakeland` |
| Northern Bothnia / Oulu | 1739, -4438 | `bothnian_coast` |
| Iceland interior | -1263, -4431 | `icelandic_highlands` |
| Reykjavik | -1498, -4379 | `iceland` |
| London | 0, -3515 | `thames_basin` |
| Paris | 160, -3336 | `seine_basin` |
| Swiss/Austrian alpine zone | 583, -3235 | `alps` |
| Rhine corridor | 485, -3461 | `rhine_valley` |
| Central Hungary | 1344, -3202 | `pannonian_plain_west` |
| Kyiv | 2088, -3437 | `dnieper_basin` |
| Western Polesia wetland | 1755, -3505 | `pripyat_marshes` |
| Southern Pontic steppe strip | 2200, -3095 | `pontic_steppe` |
| Pyrenean border | 20, -2912 | `pyrenees` |
| Central Apennines | 922, -2854 | `apennines` |
| Sicily | 912, -2602 | `sicily` |
| Sardinia | 621, -2737 | `sardinia` |
| Western Greek mountains | 1423, -2708 | `pindus_mountains` |
| Northern Europe gap | 0, -4500 | `none` |
| Western Europe gap | 100, -3200 | `none` |
| Eastern Europe gap | 2800, -3850 | `none` |
| Mediterranean Europe gap | -700, -2700 | `none` |
| Northern Italy / Bologna | 774, -3038 | `none` |

Overlaps intentionally resolve by the supplied priorities: many river/mountain zones take precedence over broader plains/steppe. The supplied Apennines polygon describes the central peninsula; northern Italy/Bologna remains an intentional gap under the unchanged macro boundaries, rather than expanding Mediterranean Europe. These are structural results, not gameplay claims.

## Future geography (not implemented)

Additional local areas outside Europe require capacity planning: only nine global slots remain in Core 0.6.1. A separate Core scaling update is planned before broader expansion. Later candidates include Somali Coast, Ethiopian Highlands and Rift Valley in East Africa & Horn. Do not add them to this release.

Future eras may supply `era_labels` for educational name changes. Regional skins/trade availability will later primarily use macros, with selected local refinements. The current archetypes require no explicit 55-area override maps: Core supplies their name prefix automatically. Parent macros must agree with independently resolved macro context. Missing local areas remain valid.
