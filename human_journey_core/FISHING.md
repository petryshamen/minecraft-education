# Fishing timing — Human Journey Core 0.6.1

**Build verified:** [GitHub Actions run 42119156083](https://github.com/petryshamen/minecraft-education/actions/runs/42119156083), source `5405a9d7b5c73c852977192b9483715ccbbafb1f`. All 93 unit/structural tests passed and all six no-world Forge data/Mixin checks passed. 107 pre-existing classes are byte-identical to Core 0.6.0, including all NPC/context/adapter, all 15 temperature classes and all water/audio integration. Only main bootstrap and Core status changed among existing classes. Final JAR metadata uses Core 0.6.1, Java 21 and root resource-pack format 64. Runtime JAR SHA-256: `c73b9b8e7944cbfe3d32768a8a1a5abf81c8dc358976f4f94965e8bc3c73db62`. No Minecraft world/bobber was created, no gameplay or real timing test was performed, and nothing was installed automatically.

Minecraft Java 1.21.8 / Forge 58.1.18. This optional, server-controlled policy changes only initialization of the normal pre-approach fishing wait. It is global while enabled; it does not infer or restrict an era. Core supplies the mechanism; the separate datapack supplies the Human Journey balance. Without the policy file, the hook delegates to the original vanilla random draw and native Lure subtraction unchanged.

## Manual installation

Replace Core 0.6.0 with exactly one `human_journey_core-0.6.1.jar` in the stopped test client/server profiles. Keep the existing dependency stack and NPC/water/temperature packs. Enable `HumanJourney-Fishing-0.6.1.zip` separately in the test world's datapacks and `/reload`. No main Human Journey datapack integration or automatic installation is required. To return to vanilla timing, disable/remove only this example fishing policy and `/reload`.

Resource: `data/human_journey/core/fishing.json`

```json
{
  "schema_version": 1,
  "bite_time": {
    "minimum_seconds": 20,
    "maximum_seconds": 60,
    "lure_reduction_seconds_per_level": 5
  }
}
```

All fields are required. Schema version is exactly 1. Times are whole seconds; `1 <= minimum_seconds <= maximum_seconds <= 3600`. Lure reduction is an integer from 0 through 3600. Unknown keys, nulls, strings in numeric fields, fractional/out-of-range values, missing fields and unsupported schemas are rejected. Resource size is bounded to 16,384 characters. Formal authoring reference: [docs/fishing.schema.json](docs/fishing.schema.json); runtime validation is authoritative. Ordinary datapack priority selects the highest-priority file at this exact path.

Valid `/reload` replaces the policy atomically. Invalid data preserves the last valid policy, source, revision and last-success timestamp, logs the problem and shows `REJECTED` in `/hjcore status [player]`. On an initial invalid load the retained state is vanilla fallback. A missing file is a successful transition to vanilla fallback, including after previously enabling the policy. Server stop clears policy state.

## Exact interception and Lure semantics

The actual 1.21.8 `FishingHook.catchingFish(BlockPos)` bytecode was inspected before implementation. It has three `Mth.nextInt(RandomSource,int,int)` calls: bite window, fish approach, then the 100–600 tick normal wait. The third draw stores `timeUntilLured`, immediately followed by native `timeUntilLured -= lureSpeed`. `lureSpeed` is **already a tick reduction**, not an enchantment level. `FishingRodItem.use` obtains the effect through `EnchantmentHelper.getFishingTimeReduction(...)`, multiplies seconds by 20 and passes it to the hook. Vanilla `data/minecraft/enchantment/lure.json` supplies five seconds per level.

One required Mixin redirect targets only the third random call in that method. It samples the configured, adjusted inclusive tick range once and compensates for the immediately following unchanged native subtraction, so Lure is applied exactly once. With no policy it calls the original draw with the original arguments and consumes the same random sequence. There is no fishing tick loop, entity replacement, loot interception or rod-level reimplementation.

For a native enchantment effect `T` ticks and policy reduction `R` seconds per vanilla Lure level, reduction = `floor(max(0,T) * R / 5)` ticks. Bounds are `max(1, configured_seconds * 20 - reduction)`, independently for minimum and maximum. This preserves the native enchantment-derived effect, including custom fishing-time modifiers, scaled relative to vanilla's five-second unit. It does not read a fixed Lure level from the held rod or disable enchanted rods. Very high reductions safely clamp to a one-tick wait; arithmetic uses wide intermediates. The compensating Java int addition/subtraction cancels even for extreme custom native values, covered by tests.

| Rod (vanilla effects) | Configured pre-approach wait |
|---|---|
| No Lure | 20–60 seconds |
| Lure I | 15–55 seconds |
| Lure II | 10–50 seconds |
| Lure III | 5–45 seconds |

These are **base wait ticks at 20 TPS**, not a guaranteed wall-clock bite window. Vanilla rain can accelerate the timer, blocked sky can slow it, server lag stretches elapsed time, and the subsequent 20–80 tick approach phase remains. Casting, landing, missed bites and normal state transitions also affect measurement. Existing countdowns/approaches/bites are not rewritten by `/reload`; new wait initializations, including a new cast, use the latest policy. Rod enchantment effects remain the native cast-time values.

Forge 58.1.18's FishingHook patch was also inspected: rod action checks, projectile-impact handling and `ItemFishedEvent` retrieval/rod damage do not provide a bite-delay hook. None is altered. Fishing loot resources, fish/junk/treasure probabilities and items, open-water checks, Luck of the Sea, rod durability/retrieval, bobber physics, weather and nibble/approach logic remain untouched. Core 0.6 NPC/context/season, water/salt/purifier/shoreline/audio and temperature mechanics are preserved.

## Read-only diagnostic

`/hjcore status` (or `/hjcore status <player>` from console), permission level 2, adds one fishing line:

```text
Fishing bite time: 20–60s | Lure reduction: 5s/level | source=<pack / resource> | revision=... | reload=OK
```

Missing policy reports `vanilla (no override)`. No per-tick logs or extra commands are added.

## Manual acceptance checklist

1. Install the JAR and separate fishing pack manually in a backed-up test world; `/reload`. Confirm the effective 20–60s policy and source in `/hjcore status` as an admin. Ordinary students should retain normal fishing access without admin diagnostics.
2. Fish repeatedly with an ordinary rod in open water under clear sky. Allow for casting and the unchanged fish approach; compare the noticeably longer wait with Core 0.6 or disabled policy. Do not judge a random range from one catch.
3. Repeat with Lure I/II/III under comparable conditions. Check that enchanted rods work and waits tend to shorten. Rain/cover should still have vanilla effects.
4. Check fish/junk/treasure, Luck of the Sea, open-water eligibility, reeling and durability as normal. Core adds/removes no loot; existing datapack/mod loot changes still apply.
5. Change the policy and `/reload`, then cast again. Try a negative value, reversed range and bad schema: diagnostic/log should reject it and retain the last valid policy. Disable the example pack and reload: confirm vanilla fallback. Re-enable it afterward if desired.
6. Recheck existing NPC context/spawns/story templates, temperature water cooling, salt/shoreline classification, partial-canteen filtration/boiling, purifier and TAN bottle sound behavior.

Automated tests and no-world Mixin checks do not establish real fishing timing or gameplay behavior. The user performs gameplay acceptance. No live server, installed mods or main Human Journey datapack is modified by this release.
