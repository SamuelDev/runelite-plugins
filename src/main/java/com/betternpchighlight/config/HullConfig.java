package com.betternpchighlight.config;

import java.awt.Color;

import net.runelite.client.config.*;

public interface HullConfig extends Config {
  String hullSectionName = "Hull";

  //------------------------------------------------------------//
  // Hull Section
  //------------------------------------------------------------//
  @ConfigItem(position = 1, keyName = "hullHighlight", name = "Hull Highlight", description = "Highlight npc's hull", section = hullSectionName)
  default boolean hullHighlight() {
    return true;
  }

  @ConfigItem(position = 2, keyName = "hullNames", name = "Hull Names", description = "List of npc's to highlight hull", section = hullSectionName)
  default String hullNames() {
    return "";
  }

  @ConfigItem(keyName = "hullNames", name = "", description = "")
  void setHullNames(String names);

  @ConfigItem(position = 3, keyName = "hullIds", name = "Hull IDs", description = "List of npc's to highlight hull", section = hullSectionName)
  default String hullIds() {
    return "";
  }

  @ConfigItem(keyName = "hullIds", name = "", description = "")
  void setHullIds(String ids);

  @Alpha
  @ConfigItem(position = 4, keyName = "hullColor", name = "Highlight Color", description = "Sets color of npc highlights", section = hullSectionName)
  default Color hullColor() {
    return Color.CYAN;
  }

  @Alpha
  @ConfigItem(position = 5, keyName = "hullFillColor", name = "Fill Color", description = "Sets the fill color of npc highlights", section = hullSectionName)
  default Color hullFillColor() {
    return new Color(0, 255, 255, 20);
  }

  @Range(min = 0, max = 50)
  @ConfigItem(position = 6, keyName = "hullWidth", name = "Highlight Width", description = "Sets the width of npc highlights", section = hullSectionName)
  default double hullWidth() {
    return 2;
  }

  @ConfigItem(position = 7, keyName = "hullAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for hull overlays. Makes them smoother.", section = hullSectionName)
  default boolean hullAA() {
    return true;
  }

  @ConfigItem(position = 8, keyName = "hullRave", name = "Enable Rave Mode", description = "Sets all hull overlays to Rave Mode", section = hullSectionName)
  default boolean hullRave() {
    return false;
  }

  @ConfigItem(position = 9, keyName = "hullRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = hullSectionName)
  @Units(Units.MILLISECONDS)
  default int hullRaveSpeed() {
    return 6000;
  }  
}
