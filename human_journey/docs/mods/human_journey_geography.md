# Human Journey NPC geography

## Current release — Asia Coverage v1, 0.5.5

Installed Core **0.9.0**, geography **schema 1**: **790 locals / 19 macros / 16 resources**. Deploy only the grouped `resources/*.json` from the approved Asia archive into `source/data/human_journey/npc_geography/`; fixtures, consolidated snapshots and validators are not runtime resources. Existing 550 IDs remain: 547 records unchanged, three polygon-only updates (`caspian_lowland__near_east_caucasus`, `iranian_plateau`, `kopet_dag`), 240 additions, zero removals. New files: `east_asia_locals.json`, `northern_asia_locals.json`, `southeast_asia_locals.json`; existing grouped files remain authoritative without duplicated macros.

Only six macro polygons change: `near_east_caucasus`, `central_asia`, `south_asia`, `east_asia`, `southeast_asia`, `oceania`. Their other fields and the other 13 macro records are unchanged. Whole-polygon checks prove zero global coverage lost and 49,938 blocks² gained around the Maldives. Earlier European/African coverage, Crete notch, Red Sea division and Upper Nile gap are retained. Coordinates are supplied final X/Z, not reconverted.

One necessary serialization correction: `iranian_plateau` contained consecutive identical vertices `[4095.999999, -2491.999998]` at indices 8/9. Core rejects duplicate vertices; only index 9 was removed. Polygon equality, zero Hausdorff distance and zero symmetric-difference area prove identical geometry. All other records match the supplied payload exactly.

Hainan resolves in East Asia. New Guinea, Raja Ampat, Aru, Palau and Guam use Oceania macro fallback; **no Oceanian locals** are added. Halmahera/Kei/Tanimbar/Timor/Batanes remain Southeast Asian; Upper Assam/Namcha Barwa/Cox Bazar/Aizawl remain South Asian. Local labels prefix new NPC names; macro-only contexts retain generic role names. Existing NPC snapshots and the shared discovery ledger are untouched. Reload applies resources to live context; no automatic migration/reset is introduced. Roll back by restoring the prior datapack ZIP; do not clear SavedData or respawn old NPCs to force discovery.

Validation: the supplied unchanged validator passes 176 physical controls, 40 macro controls, 790 reachable witnesses, 866 unchanged regressions and four intentional changes (1,876 total); 39,636 original local boundary samples. The actual first-party Core 0.9.0 pure parsers/resolver accept production offline: all 1,876 cases, 40,800 normalized local+macro edge/both-side samples, 12,150 NPC compositions, 50 commodities/four families/three Era-0 roles. Existing seven juice definitions and water policy also parse unchanged. No installed mod code was executed; Core was not rebuilt. These checks do not establish Minecraft reload acceptance or gameplay.

Read-only Earth 1500 stored-block evidence covers 216/216 physical/macro controls and 72 selected envelopes. All 34 new island fragments contain sampled non-fluid land, including Lakshadweep and Maldives fragments. A finer one-block check of the tiny `kabul_peshawar_valleys__central_asia2` fragment finds 157 land columns. No corresponding test-save chunks were available at these controls: test-world terrain remains unverified, not absent. Surface samples do not prove complete coastlines, hydrology or navigability; no terrain was generated or edited.

Manual acceptance: `/reload`, `/hj npc status`, `/hj npc context`, `/hj geography status`; inspect HUD and safe-Y X/Z controls: Ashgabat 3985,-2591 (Kopet Dag), Everest 5934,-1911 (Central Himalaya), Kathmandu 5825,-1892 (Nepal Middle Hills), Malé 5018,-285 and Addu 4994,43 (Maldivian Atolls), Hanoi 7226,-1436 (Red River Delta), Timor 8567,614 (Sumba–Timor Islands). Check seams/overlaps, Hainan and New Guinea macro fallback; Europe/Crete/Red Sea/Upper Nile regressions. Spawn fresh Hunter/Fisher/Forager to check context/profile/name; keep old snapshots. Participant-student discovery should occur once; teacher exclusion remains. Do not reset discovery. Water, temperature, juice/canteen behavior and all economy content remain unchanged and need only a normal manual smoke test.

## Historical release — Three Regions & Red Sea, 0.5.3

Installed and reviewed source **Core 0.8.0**, Minecraft 1.21.8 / Forge, geography **schema 1**. Current production has **550 locals / 19 macros / 13 geography resources**. The earlier Europe/Africa sections below describe their original releases; their file layouts and retained African Arabia parents are superseded by this section. No optional macro labels or era labels were added. Core supplies existing live HUD/default macro-label presentation; NPC names still use local prefixes captured at spawn.

The user-approved `HJ_Three_Regions_Red_Sea_v1.zip` is the exact production input: all 366 deployed IDs retained, **184 added**, **12 updated**, **zero removed**. Only `resources/*.json` is deployed. The consolidated payload, baselines, ledgers, scripts and fixtures are review inputs, never additional live resources. Twelve legacy local files were replaced by the twelve grouped files below; only documentation referenced the old filenames, with no code/config migration needed. `global_macros.json` remains the sole macro definition file.

| Local-only file under `data/human_journey/npc_geography/` | Records |
| --- | ---: |
| `arabian_peninsula_locals.json` | 43 |
| `central_asia_locals.json` | 8 |
| `east_africa_horn_locals.json` | 72 |
| `eastern_europe_locals.json` | 38 |
| `mediterranean_europe_locals.json` | 60 |
| `near_east_caucasus_locals.json` | 73 |
| `north_africa_locals.json` | 42 |
| `northern_europe_locals.json` | 38 |
| `south_asia_locals.json` | 2 |
| `southern_africa_locals.json` | 58 |
| `west_central_africa_locals.json` | 55 |
| `western_central_europe_locals.json` | 61 |

### Red Sea decision and stable-ID migration

Only the polygons of `arabian_peninsula`, `north_africa` and `east_africa_horn` change, using the supplied final X/Z vertices without reconversion. The seam is a simplified educational shore division, not a coastline survey or political/maritime boundary. Recovered old-Arabia coverage north of Z -1229 becomes North Africa and south becomes East Africa & Horn. Priorities remain North 50 / Horn 60 / Arabia 80; macro profiles and the other 16 records are identical. The global covered-area union, prior African areas, Crete notch and earlier western EA gap are preserved by whole-polygon checks.

| Existing ID | New parent / split |
| --- | --- |
| `eastern_desert_mountains_east1` | `north_africa` |
| `egyptian_red_sea_coast_east1` | `north_africa` |
| `eastern_sudan_plains_arabia1` | `east_africa_horn` |
| `ethiopian_highlands_arabia1` | `east_africa_horn` |
| `western_ethiopian_lowlands_arabia1` | `east_africa_horn` |
| `afar_depression_arabia1` | `east_africa_horn` |
| `african_red_sea_coast_arabia1` | `east_africa_horn`; added `african_red_sea_coast_arabia1__redsea_1` under `north_africa` |
| `red_sea_hills` | `east_africa_horn`; added `red_sea_hills__redsea_1` under `north_africa` |
| `eritrean_highlands` | `east_africa_horn` |
| `simien_mountains` | `east_africa_horn` |
| `lake_tana_arabia1` | `east_africa_horn` |

Stable suffixes such as `_arabia1` are retained despite new parents. The small unchanged offshore `gulf_aden_coast_arabia1` remains Arabian; Somali mainland Ras Asir resolves to Horn. Assab/Dahlak/Ras Asir are valid macro-only controls, with no fabricated local. These 11 updated records keep their supplied priority, label and profile; the two split additions preserve the intended precedence.

`greater_caucasus` is the twelfth update, keeping its deployed ID and label while adopting the approved polygon, priority and full profile: cold/cool; humid/moderate; forest/grassland/shrubland/barren; river/inland; mountain; mountain_access (no cave_access). This data describes regional context and does not create terrain/resources. `hujrah_plateau` was never deployed and was omitted by the final authoring payload as fully occluded; **`hujrah_plateau_near1`** represents Hujrah Plateau. The final total is 550, superseding the intermediate 549 proposal. The exact earlier rounding corrections in `eastern_sahara_horn1`, `northern_sahel_horn1` and `upper_nile_plains` are unchanged. There are no deviations from the supplied final production records.

### Core, persistence and validation

Installed test JAR: `human_journey_core-0.8.0.jar`, SHA-256 `cbe47eb6f84f6175d8757fe4e120b3758e9626402ddd3b2ed092b168f08aff08`; this matches the reviewed published engine. Schema blob `7ec47465bca618745658047967e681eab13ce870` and [Core geography contract](https://github.com/petryshamen/minecraft-education/blob/25c03fd0c6eba0846f4687642792b993e07d7a54/human_journey_core/GEOGRAPHY.md) blob `a51f69c2cce93fd8bcda01a7062804e8f0b4f00a` were verified. Geography keeps the exact existing resolver/complete-profile contract. HUD/shared discovery are existing Core 0.8.0 features, not implemented by this datapack; matching Core is needed on server/clients.

Participant HUD requires `hj_participant`. Movement discovers only for participant + student + **not teacher**; teacher exclusion wins conflicting tags. Discovery remains one shared Overworld `human_journey_geography` SavedData ledger of stable IDs. No NPC/player/ledger data is rewritten or reset. Historical discoveries, including an old Arabia discovery, remain legitimate history after reparenting. Existing NPC snapshots stay unchanged; newly spawned NPCs use the current context. The normal accepted reload lets Core re-evaluate live HUD context. Era resolver, roles, economy catalog/families/values, water/temperature/fishing, progression/WE CAN STAY/Agriculture, skins/dialogues/commands and datapack filters are unchanged.

The reviewed supplied `validate.py` ran unchanged with Python UTF-8 mode, jsonschema and GEOS C (Windows DLL lookup adapted by a scratch wrapper only). Result: **870 fixtures** (550 reachability + 302 regressions + 18 shore/island controls), **29,088 local boundary samples**, schema/vocabulary/whole-polygon containment, unique IDs/priorities, resource equivalence, retained IDs/profiles and macro coverage checks passed. Separately, unchanged current first-party Core 0.8.0 pure parsers/resolver prepared/accepted the final production resources offline: all 870 fixtures, **29,892 local+macro edge/both-side samples**, **8,550 composed NPC variants**, 50 commodities/four families/three Era-0-only roles. No installed mod JAR was executed and Core was not rebuilt or modified. Generic datapack validation additionally guards source references and progression-sensitive IDs. These are offline checks, not in-game reload or gameplay claims.

### Saved terrain evidence and limits

Read-only Earth 1500 block sections were available at all **320 named regression/shore controls**. Stored block states were decoded even when heightmaps were absent; no chunks were generated or terrain changed. Thirty-eight new island/coastal/wadi/Madagascar envelopes were sampled (16-block lattice for broad/narrow mainland contexts). Full column-center scans confirmed land in every new small-island envelope: Cyprus 3,963 columns; Socotra 1,240; Masirah 212; Bahrain 224; Farasan main 132 and technical fragment 35. A finer check of the small northern Madagascar dry-forest fragment found five land and nine water columns, avoiding a false absence from coarse sampling. Data was retained exactly; no island was dropped or moved.

Surface blocks are sampled physical evidence, not complete hydrology, navigability, seasonal flow or 100% land coverage. Wadi/valley/coastal contexts may include dry land or water. The configured test save differs from Earth 1500 at 12 sampled controls; the existing test-world versus reference-world limitation remains. No world was copied or repaired. Manual terrain/HUD/NPC checks should use safe elevations and account for that difference.

### Manual acceptance still required

`/reload`, `/hj npc status`, `/hj npc context`, `/hj geography status`; inspect actual loaded errors/HUD. X/Z only, choose a safe Y: Port Sudan **2541,-1339** → North Africa / African Red Sea Coast; Massawa **2693,-1066** → Horn / African Red Sea Coast; Assab **2918,-888** → Horn / no local; Jeddah **2674,-1470** → Arabian / Northern Arabian Red Sea Coast; Farasan **2860,-1140** and Socotra **3686,-853** → Arabian island locals. Test both sides and fractional Red Sea/North-Horn seams; also Cape/Namib/Kalahari/Drakensberg, Anatolia/Levant/Caucasus/Zagros, Najd/Rub al Khali/Hajar/Dhofar and London/Kyiv/Crete/Upper Nile regressions.

Spawn new H/F/G NPCs for current labels/profiles. Use an eligible participant-student for first discovery and participant-teacher for exclusion; both may see HUD. Already-discovered areas may not announce again. Do **not** reset shared discovery or refresh old NPCs merely to force a test. Gameplay/reload/HUD appearance and world-save behavior were not tested by this release.

---

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

Engine at the Europe v2 release: **Human Journey Core 0.7.1**, unchanged geography schema 1. Resources remain `data/human_journey/npc_geography/*.json`; only `global_macros.json` defines the 19 macros. Six local-only files use the existing `id`, Overworld `dimension`, unique `priority`, `macro_region`, `display_label`, simple `polygon`, and complete six-array `profile`. No era labels, skins, dialogue or trade patches are added. Highest numeric priority wins; missing locals are valid. Core prefixes new NPCs with the local label only, never the macro name.

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

## Historical Africa Coverage v1 — datapack 0.5.2

Added the accepted **168-record consolidated** North Africa / West & Central Africa / East Africa & Horn payload. Earlier 45-/94-record proposals are not loaded alongside it. All 198 European locals remain byte-for-byte unchanged. Runtime totals: **366 locals, 19 macros, 13 geography resources, 5,790 composed variants**, within unchanged Core 0.7.1 limits. There are no new skins, dialogue, trade overrides, commodity/family rules or progression writes; existing family rules/defaults remain valid. New NPCs capture local label/profile at spawn; existing NPC snapshots are not refreshed.

| Local-only file under `npc_geography/` | Actual macro parent | Records |
| --- | --- | ---: |
| `north_africa.json` | `north_africa` | 35 |
| `west_central_africa.json` | `west_central_africa` | 51 |
| `east_africa_horn.json` | `east_africa_horn` | 57 |
| `africa_mediterranean_europe.json` | `mediterranean_europe` | 7 |
| `africa_arabian_peninsula.json` | `arabian_peninsula` | 12 |
| `africa_southern_africa.json` | `southern_africa` | 6 |

Neighboring-macro fragments retain their supplied technical parent; this does not redesign Mediterranean/Red Sea/Southern Africa macro boundaries or implement the rest of Southern Africa. IDs, labels, priorities and all six profile arrays come from the user-approved Africa Coverage v1 handoff. Local profiles replace the whole macro profile. Dry/arid Turkana, wetland-versus-climate Sudd, Somali interior without blanket rivers, distinct upper/lower Blue Nile and Miombo woodland semantics are retained. These environmental profiles do not change water quality classification.

**Macro change:** only `east_africa_horn.polygon` changes. The old EA area is fully preserved and the complete triangle `[[2048,-1024],[2260.9688644688645,-1024],[2048,-683]]` is added (36,311.191392 blocks²). `upper_nile_plains` covers the entire triangle. EA priority 60 wins shared edges over North 50 / West-Central 40. The other 18 macro records, including Crete’s notch, are unchanged. No new era/scoreboard state is introduced.

**Minimum corrections:** supplied rounded corner coordinates put tiny portions of `eastern_sahara_horn1`, `northern_sahel_horn1` and `upper_nile_plains` outside their actual macro winner. Exact intersection removes 0.000055568 / 0.000008126 / 0.000002833 blocks² respectively, retaining their main IDs and all non-geometric fields. These are sub-milliblock rounding corrections, not a one-block displacement or a general polygon redesign. The supplied 0.0001-block clearances otherwise remain intact; actual Core inclusive/fractional-position checks passed.

Neither `central_african_savanna_east1` nor `congo_basin_forests_east1` existed in the current source, and no references were found, so no deletion or migration was necessary. The main Central African Savanna and Congo Basin Forests are included. The supplied final Senegal/Niger and Nile continuation polygons are retained.

**Terrain evidence:** a read-only 32-block lattice over X -1664…3552 / Z -2560…1024 inspected **18,532 existing Earth 1500 columns**: 11,131 have nonfluid top surfaces. 14,653 sampled chunks lacked heightmaps but contained stored block sections (`liquid_carvers` status); those blocks were decoded directly instead of being mistaken for missing terrain. 157/168 records have a nonfluid lattice sample; lakes, narrow river/coastal fragments and small islands need finer interpretation. All 155 task control positions had stored Earth terrain. This is sampled stored-terrain evidence, not full land coverage or a promise about later chunk generation/hydrology. No chunks were generated, terrain changed or gameplay run.

Small-island checks scanned every block-column center inside all eight supplied island fragment envelopes. Earth 1500 contains land in all: São Tomé/Príncipe 335 columns, Bioko 693, Zanzibar 517, Pemba 312, and four Comoro fragments 367 / 143 / 115 / 66. Thus all 168 supplied records are retained; none was removed as unrepresented. Archipelago sea between islands is intentional. Finer scans also found mapped water in the narrow Senegal/Niger, Blue Nile, Awash, Omo, Shabelle and Rufiji envelopes missed by the coarse lattice. The 10.6-block² Gulf of Aden/Arabia fragment is nearshore water (nearest saved land about eight blocks away), so it remains coastal context. Valley/lake envelopes include catchment/shore land: some nominal lake/river control coordinates are dry, and the small southern Lualaba fragment is land-only; these profiles are regional context, not water-block masks. Water/ice/aquatic-plant top blocks were separated from nonfluid surfaces; vegetation/waterlogged blocks mean this is not a complete navigability or biome classifier.

**Test-save limitation:** the configured `Human Journey Test` save differs from Earth 1500 at 11 sampled controls: Abidjan, Cotonou, Accra, Kumasi, Bamenda, Mount Cameroon, Port Harcourt, Libreville, São Tomé, Príncipe and Malabo. For example, the test save has water where the Earth reference has island/highland land. Geography was reconciled against the existing Earth map, not transplanted onto unrelated test terrain. Deployment still targets the configured test save; no world was replaced. Manual physical-terrain tests should account for this difference.

**Validation:** matching unchanged first-party Core 0.7.1 pure parsers/resolver prepared/accepted all resources offline: 366 reachable local witnesses, 28,236 vertex/edge/both-side samples, all 155 task controls and 20 existing European note controls, 5,790 H/F/G context/season compositions, 50 unchanged commodities and four unchanged families. Whole-polygon macro-winner containment, whole-wedge coverage, unique IDs/priorities, full built-in-vocabulary profiles and reference preservation also passed. The installed Core JAR was not executed/rebuilt/modified. In-game `/reload`, `/hj npc status`, context/labels and both-role behavior remain manual checks.

Useful X/Z manual controls (take a safe Y from the actual map): Dakar -1191,-1003; Khartoum 2223,-1065; gap core 2082,-819; Juba 2158,-331; Addis Ababa 2645,-616; Mogadishu 3092,-140; Kampala 2224,-24; Lake Victoria 2253,102; Kilimanjaro 2550,210; Dar es Salaam 2676,464. Test new Hunter/Fisher/Forager NPCs, and retain Bergen/London/Kyiv plus both Crete regressions. Teacher and Student should resolve identical geography while existing technology gates remain in force.
