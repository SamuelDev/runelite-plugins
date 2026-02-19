package com.betternpchighlight.config;

import com.betternpchighlight.BetterNpcHighlightConfig.lineType;
import java.awt.Color;

import net.runelite.client.config.*;

public interface SouthwestTrueTileConfig extends Config {
	String swTrueTileSectionName = "swTrueTile";

    //------------------------------------------------------------//
    // SW True Tile Section
    //------------------------------------------------------------//
    @ConfigItem(position = 1, keyName = "swTrueTileHighlight", name = "South West True Tile Highlight", description = "Enables highlighting NPCs by their south west true tile", section = swTrueTileSectionName)
    default boolean swTrueTileHighlight() {
      return true;
    }

    @ConfigItem(position = 2, keyName = "swTrueTileNames", name = "South West True Tile Names", description = "List of NPCs to highlight by their south west true tile", section = swTrueTileSectionName)
    default String swTrueTileNames() {
      return "";
    }

    @ConfigItem(keyName = "swTrueTileNames", name = "", description = "")
    void setSwTrueTileNames(String names);

    @ConfigItem(position = 3, keyName = "swTrueTileIds", name = "South West True Tile IDs", description = "List of NPCs to highlight by their south west true tile", section = swTrueTileSectionName)
    default String swTrueTileIds() {
      return "";
    }

    @ConfigItem(keyName = "swTrueTileIds", name = "", description = "")
    void setSwTrueTileIds(String ids);

    @Alpha
    @ConfigItem(position = 4, keyName = "swTrueTileColor", name = "Highlight Color", description = "Sets color of npc highlights", section = swTrueTileSectionName)
    default Color swTrueTileColor() {
      return Color.CYAN;
    }

    @Alpha
    @ConfigItem(position = 5, keyName = "swTrueTileFillColor", name = "Fill Color", description = "Sets the fill color of npc highlights", section = swTrueTileSectionName)
    default Color swTrueTileFillColor() {
      return new Color(0, 255, 255, 20);
    }

    @Range(min = 0, max = 50)
    @ConfigItem(position = 6, keyName = "swTrueTileWidth", name = "Highlight Width", description = "Sets the width of npc highlights", section = swTrueTileSectionName)
    default double swTrueTileWidth() {
      return 2;
    }

    @ConfigItem(position = 7, keyName = "swTrueTileAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for the sw true tile overlays. Makes them smoother.", section = swTrueTileSectionName)
    default boolean swTrueTileAA() {
      return true;
    }

    @ConfigItem(position = 8, keyName = "swTrueTileRave", name = "Enable Rave Mode", description = "Sets all sw true tile overlays to Rave Mode", section = swTrueTileSectionName)
    default boolean swTrueTileRave() {
      return false;
    }

    @ConfigItem(position = 9, keyName = "swTrueTileRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = swTrueTileSectionName)
    @Units(Units.MILLISECONDS)
    default int swTrueTileRaveSpeed() {
      return 6000;
    }

    @ConfigItem(position = 10, keyName = "swTrueTileLines", name = "South West True Tile Line Type", description = "Sets the sw true tile outline to regular, dashed, or corners only", section = swTrueTileSectionName)
    default lineType swTrueTileLines() {
      return lineType.REG;
    }
}
