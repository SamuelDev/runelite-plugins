package com.betternpchighlight.config;

import java.awt.Color;

import net.runelite.client.config.*;

public interface ClickboxConfig extends Config {
	String clickboxSectionName = "clickbox";

    //------------------------------------------------------------//
    // Clickbox Section
    //------------------------------------------------------------//
    @ConfigItem(position = 1, keyName = "clickboxHighlight", name = "Clickbox Highlight", description = "Highlights NPCs by clickbox", section = clickboxSectionName)
    default boolean clickboxHighlight() {
      return true;
    }

    @ConfigItem(position = 2, keyName = "clickboxNames", name = "Clickbox Names", description = "List of NPCs to highlight by clickbox", section = clickboxSectionName)
    default String clickboxNames() {
      return "";
    }

    @ConfigItem(keyName = "clickboxNames", name = "", description = "")
    void setClickboxNames(String names);

    @ConfigItem(position = 3, keyName = "clickboxIds", name = "Clickbox IDs", description = "List of NPCs to highlight by clickbox", section = clickboxSectionName)
    default String clickboxIds() {
      return "";
    }

    @ConfigItem(keyName = "clickboxIds", name = "", description = "")
    void setClickboxIds(String ids);

    @Alpha
    @ConfigItem(position = 4, keyName = "clickboxColor", name = "Highlight Color", description = "Sets color of NPC clickbox highlights", section = clickboxSectionName)
    default Color clickboxColor() {
      return Color.CYAN;
    }

    @Alpha
    @ConfigItem(position = 5, keyName = "clickboxFillColor", name = "Fill Color", description = "Sets the fill color of NPC clickbox highlights", section = clickboxSectionName)
    default Color clickboxFillColor() {
      return new Color(0, 255, 255, 20);
    }

    @ConfigItem(position = 6, keyName = "clickboxAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for the clickboxes. Makes them smoother.", section = clickboxSectionName)
    default boolean clickboxAA() {
      return true;
    }

    @ConfigItem(position = 7, keyName = "clickboxRave", name = "Enable Rave Mode", description = "Sets all clickbox overlays to Rave Mode", section = clickboxSectionName)
    default boolean clickboxRave() {
      return false;
    }

    @ConfigItem(position = 8, keyName = "clickboxRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = clickboxSectionName)
    @Units(Units.MILLISECONDS)
    default int clickboxRaveSpeed() {
      return 6000;
    }
}
