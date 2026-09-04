# Changelog

## 2026-09-04

- **Rewrite**: rebuilt the plugin core into a package-based structure under `src/main/java/com/betternpchighlight/` —
  `data/` (`HighlightColor`, `NPCInfo`, `NameAndIdContainer`, `MemorizedNpc`), `managers/` (`ColorManager`,
  `ConfigTransformManager`, `MenuManager`, `RespawnManager`, `SlayerPluginManager`), `overlays/`
  (`BetterNpcHighlightOverlay`, `BetterNpcMinimapOverlay`), `service/` (`ConfigReaderService`), and
  `config/migrators/` (`ConfigMigrator`).
- **Rewrite**: split the monolithic `BetterNpcHighlightConfig` into focused per-feature config interfaces
  (`GlobalConfig`, `TileConfig`, `TrueTileConfig`, `SouthwestTileConfig`, `SouthwestTrueTileConfig`,
  `HullConfig`, `AreaConfig`, `OutlineConfig`, `ClickboxConfig`, `SlayerConfig`, `EntityHiderConfig`,
  `PresetsConfig`, `MiscellaneousConfig`).
- **Rewrite**: broke the `ConfigTransformManager -> Plugin` dependency cycle by moving NPC construction into
  `ConfigTransformManager.createNpcInfo(...)`.
- **Rewrite**: fixed `ConfigMigrator` to reference the `tagStyleMode` enum in its new home (`GlobalConfig`).
- **Rewrite**: dropped the in-panel `Instructions` config section (help text) — command abbreviations are now
  documented in code rather than the config UI.
- **Fix**: NPC names containing commas now survive the comma-separated config round trip; `ConfigReaderService`
  escapes commas (`\,`) on write and treats an escaped comma as part of the entry (not a delimiter) on read.
- **Fix**: reworked the respawn timer to mirror RuneLite's NPC Indicators implementation (`MemorizedNpc` +
  `RespawnManager`). The spawn tile is learned from the first observed spawn (including the tile "behind" an
  NPC that moves on its spawn tick), so the timer no longer requires multiple kills to appear or draws off by
  a tile.
  - https://github.com/riktenx/better-npc-highlight/issues/1
  - https://github.com/SamuelDev/runelite-plugins/issues/22
- **Refactor**: restructured `MenuManager` into a layered, single-responsibility set of methods (entry point,
  NPC-interaction coloring, examine/tag handling, color resolution, preset loading, and target-string
  building) with named constants and a precompiled target regex.
- **Added**: `drawBeneathPerformanceMode` option (ported from the `better-npc-highlight` branch) that erases
  NPC models via their convex hull instead of per-triangle projection, and raised `drawBeneathLimit` max from
  20 to 30.
  - Originally implemented by @Robbejj https://github.com/SamuelDev/runelite-plugins/pull/18
- **Added**: `slayerDeprioritizeHighlight` option (ported from PR #7) that lets custom highlight colors/styles
  override the slayer task highlight on task NPCs, rather than the slayer highlight always winning.
  - Originally implemented by @Infinitay https://github.com/riktenx/better-npc-highlight/pull/7
- **Removed**: rave mode — all `*Rave` / `*RaveSpeed` config options and associated color logic.
- **Removed**: turbo mode as a highlight type.
- **Removed**: chat commands (`!tag` / `!untag` / `!hide` / `!unhide`) — `ChatCommandManager` deleted, and the
  `tagCommands` and `entityHiderCommands` config toggles removed. Right-click tagging is unaffected.