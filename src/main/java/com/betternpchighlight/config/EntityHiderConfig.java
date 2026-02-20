package com.betternpchighlight.config;

import net.runelite.client.config.*;

public interface EntityHiderConfig extends Config {
	String entityHiderSectionName = "Entity Hider";

  //------------------------------------------------------------//
	// Entity Hider Section
	//------------------------------------------------------------//
	@ConfigItem(position = 1, keyName = "entityHiderToggle", name = "Entity Hider", description = "Enables hiding of specific NPCs", section = entityHiderSectionName)
	default boolean entityHiderToggle() {
		return false;
	}

	@ConfigItem(position = 2, keyName = "entityHiderCommands", name = "Entity Hider Commands", description = "Enables the use of commands to add/remove NPCs to the Names/IDs list <br>Read the guide in Instructions section", section = entityHiderSectionName)
	default boolean entityHiderCommands() {
		return true;
	}

	@ConfigItem(position = 3, keyName = "entityHiderNames", name = "Entity Hider Names", description = "NPCs by Name to hide", section = entityHiderSectionName)
	default String entityHiderNames() {
		return "";
	}

	@ConfigItem(keyName = "entityHiderNames", name = "", description = "")
	void setEntityHiderNames(String names);

	@ConfigItem(position = 4, keyName = "entityHiderIds", name = "Entity Hider IDs", description = "NPCs by ID to hide", section = entityHiderSectionName)
	default String entityHiderIds() {
		return "";
	}

	@ConfigItem(keyName = "entityHiderIds", name = "", description = "")
	void setEntityHiderIds(String ids);
}
