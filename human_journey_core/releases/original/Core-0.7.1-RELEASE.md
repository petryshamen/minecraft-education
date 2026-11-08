# Core 0.7.1 release

- Payment diversity uses one recipe per unordered commodity combination, uniformly selected from unused/minimum-summed-usage groups. Runtime ThreadLocalRandom and injectable seeded/scripted RNG; values, ±10% and stack limits preserved.
- Saved random native MerchantOffers stay frozen. Seller filtering, families, native stock/XP, schemas and production data unchanged. Bounded new generation diagnostics support /hj npc economy inspect <entity>.
- Voluntary wandering requires supported fluid-free clear land and safe bounded native nodes, adjacent ±1 height transitions, conservative corner safety and at most 16 proposals. Recovery is separate and can attempt native home paths from unsafe/outside/depressed positions; no teleport is added.

**Build verified:** [GitHub Actions run 42450759482](https://github.com/petryshamen/minecraft-education/actions/runs/42450759482), source `800dc2c95cda1bd38abce19136dd4c56fca8b562`. All 144 unit/structural tests passed: all 123 previous tests plus 21 new diversity/safety tests. All eight no-world Forge data/Mixin checks passed, including a non-default selected family+concrete payment through native Easy NPC trade save/load. 139 existing classes are byte-identical to Core 0.7.0, including all 74 non-NPC classes (fishing, temperature, water, salt/purifier/shoreline/audio) and unchanged family integration. All JSON schemas and example data are byte-identical to 0.7.0. Runtime JAR SHA-256: `90754af47ea97fe20c2d3af4e193046d28dc1ba712f8de1c82f7b53232fbc0ef`. No NPC/world was created, gameplay-tested, installed or deployed.

Manual installation/testing only. Spawn new equivalent traders and compare payment goods while seller commodities stay the same; inspect diversity traces, maths, stacks/families/XP/stock and native save/reload identity. Observe shore/lava/cliff wandering, no-safe-target waiting and displaced recovery on reachable ground. Existing NPC offers do not reroll. Full checklist is in DIVERSITY-SAFETY.md.

Native limitations: conservative support checks may reject partial-block routes; physics/external movement/terrain changes can still cause accidents; native pathfinding cannot guarantee escape from a sealed pit. Existing Easy NPC stock restoration remains unchanged. HJ Developer needs no data migration; production mushroom edits remain separate, and content/family changes affect newly spawned NPCs rather than old frozen offers.

No new example pack is shipped: schemas and all previous example data are byte-identical. Runtime JAR and complete source ZIP archived with exact checksums.

Installed automatically: NO. Production commodities/geography/progression/Agriculture/WE CAN STAY changed: NO. Live server changed: NO. Minecraft gameplay testing: NO. Forge/Crawl on Demand startup incident: NOT addressed.

