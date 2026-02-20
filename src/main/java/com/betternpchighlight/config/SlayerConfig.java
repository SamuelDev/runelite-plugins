package com.betternpchighlight.config;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;

import com.betternpchighlight.config.GlobalConfig.tagStyleMode;

import net.runelite.client.config.*;

public interface SlayerConfig extends Config {
	String slayerSectionName = "Slayer";

  //------------------------------------------------------------//
	// Slayer Section
	//------------------------------------------------------------//
	@ConfigItem(position = 1, keyName = "slayerHighlight", name = "Slayer Task Highlight", description = "Highlights NPCs that are assigned as your slayer task <br>Uses the 'Slayer' plugin. Keep it on!", section = slayerSectionName)
	default boolean slayerHighlight() {
		return false;
	}

	@ConfigItem(position = 2, keyName = "taskHighlightStyle", name = "Slayer Highlight Style", description = "Picks the highlight style you want for NPCs on your slayer task", section = slayerSectionName)
	default Set<tagStyleMode> taskHighlightStyle() {
		return Collections.emptySet();
	}

	@Alpha
	@ConfigItem(position = 4, keyName = "taskColor", name = "Highlight Color", description = "Sets color of slayer task npc highlights", section = slayerSectionName)
	default Color taskColor() {
		return new Color(224, 60, 49, 255);
	}

	@Alpha
	@ConfigItem(position = 5, keyName = "taskFillColor", name = "Fill Color", description = "Sets the fill color of slayer task npc highlights", section = slayerSectionName)
	default Color taskFillColor() {
		return new Color(224, 60, 49, 20);
	}

	@ConfigItem(position = 6, keyName = "slayerAA", name = "Anti-Aliasing", description = "Turns on anti-aliasing for the slayer highlights. Makes them smoother.", section = slayerSectionName)
	default boolean slayerAA() {
		return true;
	}

	@ConfigItem(position = 7, keyName = "slayerRave", name = "Enable Rave Mode", description = "Sets all slayer overlays to Rave Mode", section = slayerSectionName)
	default boolean slayerRave() {
		return false;
	}

	@ConfigItem(position = 8, keyName = "slayerRaveSpeed", name = "Rave Speed", description = "Sets the speed the overlays rave at", section = slayerSectionName)
	@Units(Units.MILLISECONDS)
	default int slayerRaveSpeed() {
		return 6000;
	}
}
