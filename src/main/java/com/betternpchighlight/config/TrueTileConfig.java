package com.betternpchighlight.config;

import com.betternpchighlight.BetterNpcHighlightConfig.lineType;
import java.awt.Color;

import net.runelite.client.config.*;

public interface TrueTileConfig extends Config {
  String trueTileSectionName = "True Tile";

  //------------------------------------------------------------//
  // True Tile Section
  //------------------------------------------------------------//
  @ConfigItem(position = 1, keyName = "trueTileHighlight", name = "True Tile Highlight", description = "Highlights npc's true tile", section = trueTileSectionName)
  default boolean trueTileHighlight() {
    return true;
  }

  @ConfigItem(position = 2, keyName = "trueTileNames", name = "True Tile Names", description = "List of npc's to highlight true tile", section = trueTileSectionName)
  default String trueTileNames() {
    return "";
  }

  @ConfigItem(keyName = "trueTileNames", name = "", description = "")
  void setTrueTileNames(String names);

  @ConfigItem(position = 3, keyName = "trueTileIds", name = "True Tile IDs", description = "List of npc's to highlight true tile", section = trueTileSectionName)
  default String trueTileIds() {
    return "";
  }

  @ConfigItem(keyName = "trueTileIds", name = "", description = "")
  void setTrueTileIds(String ids);

  @Alpha
  @ConfigItem(position = 4, keyName = "trueTileColor", name = "Highlight Color", description = "Sets color of npc highlights", section = trueTileSectionName)
  default Color trueTileColor() {
    return Color.CYAN;
  }

  @Alpha
  @ConfigItem(position = 5, keyName = "trueTileFillColor", name = "Fill Color", description = "Sets the fill color of npc highlights", section = trueTileSectionName)
  default Color trueTileFillColor() {
    return new Color(0, 255, 255, 20);
  }

  @Range(min = 0, max = 50)
  @ConfigItem(position = 6, keyName = "trueTileWidth", name = "Highlight Width", description = "Sets the width of npc highlights", section = trueTileSectionName)
  default double trueTileWidth() {
    return 2;
  }

  @ConfigItem(position = 7, keyName = "trueTileAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for true tile overlays. Makes them smoother.", section = trueTileSectionName)
  default boolean trueTileAA() {
    return true;
  }

  @ConfigItem(position = 8, keyName = "trueTileRave", name = "Enable Rave Mode", description = "Sets all true tile overlays to Rave Mode", section = trueTileSectionName)
  default boolean trueTileRave() {
    return false;
  }

  @ConfigItem(position = 9, keyName = "trueTileRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = trueTileSectionName)
  @Units(Units.MILLISECONDS)
  default int trueTileRaveSpeed() {
    return 6000;
  }

  @ConfigItem(position = 10, keyName = "trueTileLines", name = "True Tile Line Type", description = "Sets the true tile outline to regular, dashed, or corners only", section = trueTileSectionName)
  default lineType trueTileLines() {
    return lineType.REG;
  }
}
