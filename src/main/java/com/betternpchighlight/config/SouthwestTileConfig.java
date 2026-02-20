package com.betternpchighlight.config;

import com.betternpchighlight.BetterNpcHighlightConfig.lineType;
import java.awt.Color;

import net.runelite.client.config.*;

public interface SouthwestTileConfig extends Config {
	String swTileSectionName = "Southwest Tile";

  //------------------------------------------------------------//
	// SW Tile Section
	//------------------------------------------------------------//
	@ConfigItem(position = 1, keyName = "swTileHighlight", name = "South West Tile Highlight", description = "Highlights npc's south west tile", section = swTileSectionName)
	default boolean swTileHighlight() {
		return true;
	}

	@ConfigItem(position = 2, keyName = "swTileNames", name = "South West Tile Names", description = "List of npc's to highlight south west tile", section = swTileSectionName)
	default String swTileNames() {
		return "";
	}

	@ConfigItem(keyName = "swTileNames", name = "", description = "")
	void setSwTileNames(String names);

	@ConfigItem(position = 3, keyName = "swTileIds", name = "South West Tile IDs", description = "List of npc's to highlight south west tile", section = swTileSectionName)
	default String swTileIds() {
		return "";
	}

	@ConfigItem(keyName = "swTileIds", name = "", description = "")
	void setSwTileIds(String ids);

	@Alpha
	@ConfigItem(position = 4, keyName = "swTileColor", name = "Highlight Color", description = "Sets color of npc highlights", section = swTileSectionName)
	default Color swTileColor() {
		return Color.CYAN;
	}

	@Alpha
	@ConfigItem(position = 5, keyName = "swTileFillColor", name = "Fill Color", description = "Sets the fill color of npc highlights", section = swTileSectionName)
	default Color swTileFillColor() {
		return new Color(0, 255, 255, 20);
	}

	@Range(min = 0, max = 50)
	@ConfigItem(position = 6, keyName = "swTileWidth", name = "Highlight Width", description = "Sets the width of npc highlights", section = swTileSectionName)
	default double swTileWidth() {
		return 2;
	}

	@ConfigItem(position = 7, keyName = "swTileAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for the sw tile overlays. Makes them smoother.", section = swTileSectionName)
	default boolean swTileAA() {
		return true;
	}

	@ConfigItem(position = 8, keyName = "swTileRave", name = "Enable Rave Mode", description = "Sets all sw tile overlays to Rave Mode", section = swTileSectionName)
	default boolean swTileRave() {
		return false;
	}

	@ConfigItem(position = 9, keyName = "swTileRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = swTileSectionName)
	@Units(Units.MILLISECONDS)
	default int swTileRaveSpeed() {
		return 6000;
	}

	@ConfigItem(position = 10, keyName = "swTileLines", name = "South West Tile Line Type", description = "Sets the sw tile outline to regular, dashed, or corners only", section = swTileSectionName)
	default lineType swTileLines() {
		return lineType.REG;
	}
}
