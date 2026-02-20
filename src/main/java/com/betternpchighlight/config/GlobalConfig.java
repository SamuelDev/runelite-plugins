package com.betternpchighlight.config;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;
import java.awt.Color;

public interface GlobalConfig extends Config {
  
  String globalTagSectionGroupName = "Global Tag Style";
  //------------------------------------------------------------//
  // Global tag style section
  //------------------------------------------------------------//
  public static final Set<tagStyleMode> defaultTagStyle = Set.of(tagStyleMode.TILE);

  @ConfigItem(position = 0, keyName = "tagStyleModeSet", name = "Tag Style", description = "Sets which highlight styles to apply to an NPC when tagged from the right click menu. Select none to hide the right click menu option.", section = "globalTagStyle")
  default Set<tagStyleMode> tagStyleModeSet() {
    return defaultTagStyle;
  }

  @ConfigItem(position = 1, keyName = "useGlobalTileColor", name = "Use Global Tile Color", description = "Forces tile, true tile, SW tile, and SW true tile to use the same colors. Will not override highlights using a preset.", section = "globalTagStyle")
  default boolean useGlobalTileColor() {
    return false;
  }

  @Alpha
  @ConfigItem(position = 2, keyName = "globalTileColor", name = "Global Tile Color", description = "Overrides all other tag style outlines.", section = "globalTagStyle")
  default Color globalTileColor() {
    return Color.CYAN;
  }

  @Alpha
  @ConfigItem(position = 3, keyName = "globalFillColor", name = "Global Fill Color", description = "Overrides all other tag style fill colors.", section = "globalTagStyle")
  default Color globalFillColor() {
    return new Color(0, 255, 255, 20);
  }

  @Getter
	@RequiredArgsConstructor
	enum tagStyleMode {
		NONE("None", "none"), TILE("Tile", "tile"), TRUE_TILE("True Tile", "trueTile"), SW_TILE("SW Tile", "swTile"),
		SW_TRUE_TILE("SW True Tile", "swTrueTile"), HULL("Hull", "hull"), AREA("Area", "area"), OUTLINE("Outline", "outline"),
		CLICKBOX("Clickbox", "clickbox"),;

		@Getter
		private final String group;

		@Getter
		private final String key;

		@Override
		public String toString() {
			return group;
		}
	}
}
