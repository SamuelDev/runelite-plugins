# AGENTS.md

Context for LLM/agent sessions working in this repository.

## Project

A [RuneLite](https://runelite.net/) external plugin called **Better NPC Highlight** — a more customizable
NPC-highlighting plugin that acts as a replacement for the built-in NPC Indicators plugin. It supports
multiple highlight styles (tile, true tile, SW tile, SW true tile, hull, area, outline, clickbox), slayer-task
highlighting, entity hiding, respawn timers, presets, and rich right-click tagging.

The plugin package root is `com.betternpchighlight`, and all source lives under
`src/main/java/com/betternpchighlight/`.

- `src/test/java/.../BetterNpcHighlightTest.java` is the RuneLite dev-mode launcher, not a unit test.

## Build / run

- Java 11, Gradle 7.4 (`./gradlew` on Windows, `gradlew` elsewhere).
- `./gradlew build` compiles and packages the plugin jar.
- There are no unit tests; `build` runs `compileJava` + `compileTestJava`.
- RuneLite is run in dev mode through the VS Code launch config (`.vscode/launch.json`), which passes
  `--developer-mode --debug --profile=plugintesting` to `RuneLite.main`. The `--profile` argument is a RuneLite
  runtime flag (separate settings profile), not a Gradle flag.

## Package map

- `config/` — config interfaces, one per feature. `BetterNpcHighlightConfig` composes them all.
- `config/migrators/ConfigMigrator` — one-shot config migrations invoked from `providesConfig`.
- `data/` — plain data holders: `HighlightColor`, `NPCInfo`, `NameAndIdContainer`, `MemorizedNpc`.
- `managers/` — feature logic: `ColorManager`, `ConfigTransformManager`, `MenuManager`, `RespawnManager`,
  `SlayerPluginManager`.
- `overlays/` — `BetterNpcHighlightOverlay` (scene) and `BetterNpcMinimapOverlay`.
- `service/ConfigReaderService` — CSV-style config string parsing/serialization with comma escaping.
- `BetterNpcHighlightPlugin` — the `@PluginDescriptor` entry point; wires up events and delegates to managers.

## Critical conventions and gotchas

- **Do not change `keyName` values.** Config values are persisted by `keyName` under the config group
  `BetterNpcHighlight`. Renaming keys silently wipes user settings. Section names are display-only and safe to
  change.
- **Stateful managers must be `@Singleton`.** `NameAndIdContainer`, `ConfigReaderService`, and `RespawnManager`
  hold state and are `@Singleton`. Stateless managers (`ColorManager`, `MenuManager`, `ConfigTransformManager`,
  `SlayerPluginManager`) are intentionally not singletons — they only reference singletons/config, so multiple
  instances are harmless. If you add mutable state to a manager, mark it `@Singleton`.
- **The `tagStyleMode` enum lives in `config/GlobalConfig`**, not `BetterNpcHighlightConfig`. Other enums live
  with their owning feature config (`presetColorAmount` in `PresetsConfig`, `background`/`renderDistance`/
  `respawnTimerMode`/`npcMinimapMode` in `MiscellaneousConfig`, `lineType` in `BetterNpcHighlightConfig`).
- **Menu target strings**: RuneLite menu targets contain embedded color tags and an optional `(level-N)` suffix.
  `MenuManager.getMenuEntryString` (regex `MENU_TARGET_PATTERN`) is the single place that rebuilds a target's
  name/level coloring — reuse it rather than re-parsing tags elsewhere.
- **Respawn timers** mirror the RuneLite `npchighlight` plugin (`MemorizedNpc` keyed by NPC index, buffered
  spawn/despawn events validated on game tick, `SpotanimID.SMOKEPUFF` teleport detection). If respawn behavior
  is being changed, compare against RuneLite's `NpcIndicatorsPlugin`/`MemorizedNpc`/`NpcRespawnOverlay` first.
- **Comma escaping**: name/ID lists are comma-separated in config. `ConfigReaderService.parseList`/`listToCsv`
  escape commas (`\,`) so names containing commas round-trip. Keep list serialization going through
  `ConfigReaderService`, not `Text.toCSV` or naive `split(",")`.

## Removed features (by design)

- Turbo mode (a highlight type).
- Rave mode — all `*Rave`/`*RaveSpeed` options and logic.
- Chat commands — `!tag`/`!untag`/`!hide`/`!unhide` (`ChatCommandManager`, `tagCommands`, and
  `entityHiderCommands` config). Right-click tagging remains.
- The config "Instructions" help-text section.

## Where future changes go

- New highlight style → new config interface + add to `BetterNpcHighlightConfig` (both the `extends` list and a
  `@ConfigSection`), extend `ConfigTransformManager` list handling + `createNpcInfo`, and add a case in
  `BetterNpcHighlightOverlay.renderNpcOverlay`.
- New right-click option / tag behavior → `MenuManager` (see its section comments; it is structured for
  additions).
- Respawn behavior → `RespawnManager` + `MemorizedNpc`.