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

## Europe Coverage v2 — datapack 0.5.1

Installed engine: **Human Journey Core 0.7.1**, unchanged geography schema 1. Resources remain `data/human_journey/npc_geography/*.json`; only `global_macros.json` defines the 19 macros. Six local-only files use the existing `id`, Overworld `dimension`, unique `priority`, `macro_region`, `display_label`, simple `polygon`, and complete six-array `profile`. No era labels, skins, dialogue or trade patches are added. Highest numeric priority wins; missing locals are valid. Core prefixes new NPCs with the local label only, never the macro name.

The task supplied 165 proposals, not a required record count. Merging 17 same-feature stitch proposals and retaining the distinct legacy Pripyat Marshes/Azov Coast gives **150 physical areas**, represented by **198 runtime locals** after all-global-macro clipping and removal of fully hidden/marine fragments. All **55 previous technical IDs** remain; 143 IDs are new. Northern Europe’s approved 27 features are all present, including southern fragments under neighboring macros. JSON resources are the authoritative complete ID/label/parent/profile inventory.

| Local resource | Actual macro parent | Records |
| --- | --- | ---: |
| `northern_europe.json` | `northern_europe` | 38 |
| `western_central_europe.json` | `western_central_europe` | 61 |
| `eastern_europe.json` | `eastern_europe` | 38 |
| `mediterranean_europe.json` | `mediterranean_europe` | 47 |
| `europe_near_east_caucasus.json` | `near_east_caucasus` | 6 |
| `europe_central_asia.json` | `central_asia` | 8 |

The 14 Near East/Central Asia records are only clipped European physical-feature extensions (Caucasus, Caspian, Volga/steppe, Urals); their existing macro boundaries/profiles stay unchanged. This is not a new Asian geography layer. Technical fragments use `__<macro>` plus an occasional numeric suffix, with the same physical label/profile. `eastern_europe_background` exists only where Eastern Europe actually wins. Priorities are unique across the entire set; backgrounds yield to meaningful physical features, with Swiss Plateau/Po Plain and coastal/island precedence checked explicitly.

### Stable IDs and merged shapes

Draft → retained ID: `pannonian_plain` → `pannonian_plain_west`; `north_european_plain` → `north_european_plain_west`; `dnipro_valley` → `dnieper_basin`; `dniester_valley` → `dniester_basin`; `lower_don_valley` → `don_basin`. The latter three now carry the approved corridor labels, while broader Dnipro upland/lowland remain separate. Existing `leaves` family rules still reference valid IDs and are byte-for-byte unchanged; new locals use those existing rules/fallbacks. No commodity values, eligibility, prices, stock or economy algorithm changed.

Merged stitch proposals: northern parts of Galician/Portuguese Uplands, Cantabrian Mountains, Pyrenees, Languedoc/Roussillon, Italian Peninsula, Tyrrhenian Hills, Apennines, Corsica, Dinaric Alps, Dalmatian Coast and Morava/Vardar; northern/eastern Balkan Interior and Balkan Mountains; western Transylvania; eastern Southern Carpathians. Each is then split only as required by actual macro winners. Pripyat Marshes remains distinct from broad Polissia, and Azov Coast from Pontic Steppe.

### Geometry and profile decisions

Northern polygons/priorities/profiles are completed here, not represented as previously supplied exact contours. Existing matching shapes were starting points. Norway’s fjords/interior/Oslofjord, Scotland’s three physical zones, Swedish uplands/lowlands/lakes, Finland/Bothnia and Baltic/Karelian zones use simplified physical envelopes. Central Swedish Lakes was narrowed to the Vänern/Vättern country so Central Sweden remains reachable; Irish land is excluded from Scotland’s envelopes. Broad natural backgrounds close meaningful mainland gaps without inventing arbitrary nearest-region labels. Unrepresented fine Baltic/Aegean islet slivers and Corsica’s unsupported northern sliver are omitted, not painted onto mainland. Archipelago water between islands is permitted; reference mainland is excluded.

Every local fully replaces the macro profile: temperature/moisture/vegetation/water/relief/special are explicit, using existing vocabulary. Existing matching profiles were reused as a basis. High mountains are cool/cold (including a corrected Baetic mountain profile), Atlantic Iberia is humid rather than dry Meseta, and broad islands use supported natural mosaics. Pontic Steppe retains water `["inland", "river"]`; no special feature is `[]`.

Clipping considers all 19 macros, including higher-priority Near East/Central Asia. Lower-owned polygons keep a **0.01-block geometric clearance** from higher-owned inclusive boundaries; this is a technical fractional-position tolerance, not a one-block map-scale shift. Exact boundary points and both sides are checked through Core’s continuous X/Z resolver. Polygon intersections may be fractional. No MultiPolygon/holes are passed to Core; fragmented records remain simple and under 256 vertices.

**Crete is the only macro edit.** A connected-component scan of existing one-block OCEAN_FLOOR heightmaps identified the main saved Crete landmass, excluding Gavdos/Antikythera. A small coastline buffer defines `crete` (bounds X 1605–1797, Z -2437–-2382); the Mediterranean perimeter gains only a surrounding notch. Its entire old southern rectangle is not moved. No `crete_south` or North Africa local is created; the African coast’s macro results remain unchanged.

### Evidence and limits

The configured Human Journey Test save has no European chunks. Read-only terrain evidence therefore comes from the existing **Earth 1500** save, not from generated chunks or gameplay: 47,595 existing chunks, 4-block lattice over X -1904…4512 / Z -5120…-2304. 761,520 lattice samples are known and 367,696 unknown. OCEAN_FLOOR above sea-level top is a solid-land proxy, not a perfect shoreline/ice/beach classifier. Small ambiguous fragments were checked at one-block resolution. 191/198 reachability witnesses have this terrain evidence; seven are reference-only: the western Meseta boundary fragment, Caspian/Near East fragment, Northern Forests and the two Urals/foothills pairs. Missing or inconclusive map evidence is not proof those places exist in the current Test save.

Of 262,242 known above-sea samples inside the four macro winners, 251,402 are covered (95.87%). The remaining 10,840 include 5,997 African-coast, 3,173 Anatolian and 1,406 Greenland samples inside the intentionally coarse existing macro rectangles; these must not be relabeled European. The other 264 are small-island/coastal/physical-envelope gaps (including Gotland, Shetland and Man); `none` remains valid there. This is a sampled coverage audit, **not a claim of 100% European land coverage**. No terrain, chunks, map transform or source task file was changed. Named Project Resources unavailable in this session were not invented.

Offline validation used the unchanged first-party Core 0.7.1 pure schema/resolver sources in a standalone scratch harness, without executing the installed mod JAR or building/modifying Core: 198 reachable locals, 17,512 boundary samples, 53 control points, 3,270 archetype/context/season compositions; 50 commodities, four families and three Era-0-only roles. Item references were checked against installed vanilla/TAN item resources. Full polygon containment against the actual macro winner and island-versus-reference-mainland negatives also passed. These checks do not prove in-game reload/UI behavior.

Existing NPCs retain Core’s spawn-time snapshot; use newly spawned NPCs for checks. Era is read-only: `hj_civ / #era1 = 0` → `era_0`, `1` → `era_1`; Era 1 still has no economic roles. Geography does not write progression or touch the separate water/salt classifier. Teacher/Student see the same geographic context; existing technology enforcement remains unchanged.

### Representative coordinates for manual checks

Use a safe known elevation; the transform gives no Y. `/reload`, then `/hj npc status` and `/hj npc context` must be checked manually for resource acceptance, errors and names.

| Place | X, Z | Expected local/label |
| --- | --- | --- |
| Bergen | 363, -4123 | Norwegian Fjords |
| Oslo | 734, -4090 | Oslofjord |
| Finnish Lakeland | 1761, -4260 | Finnish Lakeland |
| Glasgow | -290, -3813 | Central Scotland |
| Southern Scotland | -239, -3779 | Southern Scotland |
| Scania | 901, -3809 | Scania |
| Stockholm | 1234, -4050 | Central Sweden |
| Karelia | 2345, -4218 | Karelia |
| Kola | 2389, -4642 | Kola Peninsula |
| London | 0, -3515 | Thames Basin |
| Zurich | 583, -3234 | Swiss Plateau |
| Kyiv | 2088, -3437 | Dnipro Valley |
| Lisbon | -624, -2643 | Tagus |
| Bologna | 774, -3038 | Po Plain |
| Sarajevo | 1257, -2993 | Dinaric Alps |
| Athens | 1620, -2593 | Mainland Greece |
| Corfu | 1360, -2705 | Ionian Islands |
| Lesbos | 1812, -2676 | Aegean Islands |
| Crete north | 1718, -2410 | Crete |
| Crete south | 1689, -2385 | Crete |

### Sources and assumptions

- User’s Europe Coverage v2 task supplies approved feature names and southern/eastern/western draft envelopes; the experimentally verified transform above is unchanged. Vertices are educational simplifications, not surveyed boundaries.
- [Natural Earth 1:10m land, v5.1.1](https://www.naturalearthdata.com/downloads/10m-physical-vectors/10m-land/) supplies the reference coastline/mainland exclusion; it is not evidence of Minecraft terrain. Saved Earth 1500 heightmaps provide the separate map evidence.
- [BGS Midland Valley](https://webapps.bgs.ac.uk/memoirs/docs/B07005.html) and [Southern Scotland](https://webapps.bgs.ac.uk/memoirs/docs/B07006.html), [NGU bedrock overview](https://www.ngu.no/om-geologi/om-berggrunn), [University of Oslo regional geology](https://www.mn.uio.no/geo/english/services/knowledge/geology-oslo-area/index.html), [SGU geology of Sweden](https://www.sgu.se/en/geology-of-sweden/) and [Finnish national landscapes](https://www.ymparisto.fi/en/nature-waters-and-sea/landscape/national-landscapes) guide the northern physical distinctions/profiles; they do not prescribe every polygon vertex.
- [IGN relief units](https://educativo.ign.es/atlas-didactico/relieve-bach/las_grandes_unidades_del_relieve_peninsular.html) and [LNEG Portugal geomorphology](https://www.lneg.pt/wp-content/uploads/2025/04/26_Lopes-et-al.pdf) support humid Atlantic versus interior Iberia and mountain distinctions. Other supplied Europe contours remain simplified task inputs, reconciled with existing geographic profiles and saved terrain.

## Historical compatibility and future work

Europe Local Areas v1 shipped 55 IDs under Core 0.6.1’s former 64-local limit. Its whole-block boundary insets and older point expectations are superseded by v2 fractional clipping; IDs are retained. Core 0.7.1 supports 4,096 locals, 512 macros, 1,024 resources and 1,048,576 compositions; this release uses 198 / 19 / 7 / 3,270 respectively. No engine change is required.

Future local layers outside Europe, macro boundary redesign (e.g. Anatolia/Caucasus), regional skins/trades and era-sensitive labels remain separate work. No African/Asian geography expansion is implied by the minimum cross-boundary physical fragments. The existing [economy policy](human_journey_economy.md) and Core mechanics remain authoritative.
