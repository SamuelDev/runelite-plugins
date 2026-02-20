package com.betternpchighlight.config;

import java.awt.Color;

import net.runelite.client.config.*;

public interface OutlineConfig extends Config {
	String outlineSectionName = "Outline";

  //------------------------------------------------------------//
	// Outline Section
	//------------------------------------------------------------//
	@ConfigItem(position = 1, keyName = "outlineHighlight", name = "Outline Highlight", description = "Highlights npc's outline", section = outlineSectionName)
	default boolean outlineHighlight() {
		return true;
	}

	@ConfigItem(position = 2, keyName = "outlineNames", name = "Outline Names", description = "List of npc's to highlight outline", section = outlineSectionName)
	default String outlineNames() {
		return "";
	}

	@ConfigItem(keyName = "outlineNames", name = "", description = "")
	void setOutlineNames(String names);

	@ConfigItem(position = 3, keyName = "outlineIds", name = "Outline IDs", description = "List of npc's to highlight outline", section = outlineSectionName)
	default String outlineIds() {
		return "";
	}

	@ConfigItem(keyName = "outlineIds", name = "", description = "")
	void setOutlineIds(String ids);

	@Alpha
	@ConfigItem(position = 4, keyName = "outlineColor", name = "Highlight Color", description = "Sets color of npc highlights", section = outlineSectionName)
	default Color outlineColor() {
		return Color.CYAN;
	}

	@Range(min = 0, max = 50)
	@ConfigItem(position = 5, keyName = "outlineWidth", name = "Outline Width", description = "Sets the width of outline highlights", section = outlineSectionName)
	default int outlineWidth() {
		return 2;
	}

	@Range(min = 0, max = 5)
	@ConfigItem(position = 6, keyName = "outlineFeather", name = "Outline Feather", description = "Sets the feather of the outline highlights", section = outlineSectionName)
	default int outlineFeather() {
		return 2;
	}

	@ConfigItem(position = 7, keyName = "outlineRave", name = "Enable Rave Mode", description = "Sets all outline overlays to Rave Mode", section = outlineSectionName)
	default boolean outlineRave() {
		return false;
	}

	@ConfigItem(position = 8, keyName = "outlineRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = outlineSectionName)
	@Units(Units.MILLISECONDS)
	default int outlineRaveSpeed() {
		return 6000;
	}
}
