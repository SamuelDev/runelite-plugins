package com.betternpchighlight.config;

import com.betternpchighlight.BetterNpcHighlightConfig.lineType;
import java.awt.Color;

import net.runelite.client.config.*;

public interface TileConfig extends Config {
  String tileSectionName = "Tile";

  //------------------------------------------------------------//
  // Tile Section
  //------------------------------------------------------------//
  @ConfigItem(position = 1, keyName = "tileHighlight", name = "Tile Highlight", description = "Highlights NPCs by tile", section = tileSectionName)
  default boolean tileHighlight() {
    return true;
  }

  @ConfigItem(position = 2, keyName = "tileNames", name = "Tile Names", description = "List of NPCs to highlight by tile", section = tileSectionName)
  default String tileNames() {
    return "";
  }

  @ConfigItem(keyName = "tileNames", name = "", description = "")
  void setTileNames(String names);

  @ConfigItem(position = 3, keyName = "tileIds", name = "Tile IDs", description = "List of NPCs to highlight by tile", section = tileSectionName)
  default String tileIds() {
    return "";
  }

  @ConfigItem(keyName = "tileIds", name = "", description = "")
  void setTileIds(String ids);

  @Alpha
  @ConfigItem(position = 4, keyName = "tileColor", name = "Highlight Color", description = "Sets color of NPC tile highlights", section = tileSectionName)
  default Color tileColor() {
    return Color.CYAN;
  }

  @Alpha
  @ConfigItem(position = 5, keyName = "tileFillColor", name = "Fill Color", description = "Sets the fill color of npc highlights", section = tileSectionName)
  default Color tileFillColor() {
    return new Color(0, 255, 255, 20);
  }

  @Range(min = 0, max = 50)
  @ConfigItem(position = 6, keyName = "tileWidth", name = "Highlight Width", description = "Sets the width of npc highlights", section = tileSectionName)
  default double tileWidth() {
    return 2;
  }

  @ConfigItem(position = 7, keyName = "tileAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for tile overlays. Makes them smoother.", section = tileSectionName)
  default boolean tileAA() {
    return true;
  }

  @ConfigItem(position = 8, keyName = "tileRave", name = "Enable Rave Mode", description = "Sets all tile overlays to Rave Mode", section = tileSectionName)
  default boolean tileRave() {
    return false;
  }

  @ConfigItem(position = 9, keyName = "tileRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = tileSectionName)
  @Units(Units.MILLISECONDS)
  default int tileRaveSpeed() {
    return 6000;
  }

  @ConfigItem(position = 10, keyName = "tileLines", name = "Tile Line Type", description = "Sets the tile outline to regular, dashed, or corners only", section = tileSectionName)
  default lineType tileLines() {
    return lineType.REG;
  }
}
