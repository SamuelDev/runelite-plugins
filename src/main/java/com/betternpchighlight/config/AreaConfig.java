package com.betternpchighlight.config;

import java.awt.Color;

import net.runelite.client.config.*;

public interface AreaConfig {
  String areaSectionName = "area";
  
  //------------------------------------------------------------//
  // Area Section
  //------------------------------------------------------------//
  @ConfigItem(position = 1, keyName = "areaHighlight", name = "Area Highlight", description = "Highlights npc's area", section = areaSectionName)
  default boolean areaHighlight() {
    return true;
  }

  @ConfigItem(position = 2, keyName = "areaNames", name = "Area Names", description = "List of npc's to highlight area", section = areaSectionName)
  default String areaNames() {
    return "";
  }

  @ConfigItem(keyName = "areaNames", name = "", description = "")
  void setAreaNames(String names);

  @ConfigItem(position = 3, keyName = "areaIds", name = "Area IDs", description = "List of npc's to highlight area", section = areaSectionName)
  default String areaIds() {
    return "";
  }

  @ConfigItem(keyName = "areaIds", name = "", description = "")
  void setAreaIds(String ids);

  @Alpha
  @ConfigItem(position = 4, keyName = "areaColor", name = "Highlight Color", description = "Sets color of npc highlights", section = areaSectionName)
  default Color areaColor() {
    return new Color(0, 255, 255, 50);
  }

  @ConfigItem(position = 5, keyName = "areaRave", name = "Enable Rave Mode", description = "Sets all area overlays to Rave Mode", section = areaSectionName)
  default boolean areaRave() {
    return false;
  }

  @ConfigItem(position = 6, keyName = "areaRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = areaSectionName)
  @Units(Units.MILLISECONDS)
  default int areaRaveSpeed() {
    return 6000;
  }
}
