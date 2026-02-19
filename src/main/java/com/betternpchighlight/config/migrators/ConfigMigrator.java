package com.betternpchighlight.config.migrators;

import java.util.Set;

import com.betternpchighlight.BetterNpcHighlightConfig;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

@Slf4j
public final class ConfigMigrator {
  public static void migrateOldConfigs(ConfigManager configManager) {
  	migrateTagStyleMode(configManager);
  }

  // migrate old tag style mode config into the new set version of config if possible
  private static void migrateTagStyleMode(ConfigManager configManager) {
  	try
  	{
  		String oldValue = configManager.getConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleMode");
  		String newValue = configManager.getConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleModeSet");
  		if (oldValue != null && newValue == null)
  		{
  			log.debug("BNPC: Migrating old tag style mode config to new set version");

  			// Parse the old enum string value
  			BetterNpcHighlightConfig.tagStyleMode oldMode = BetterNpcHighlightConfig.tagStyleMode.valueOf(oldValue.toUpperCase());

  			// Convert to Set format
  			Set<BetterNpcHighlightConfig.tagStyleMode> newModeSet = Set.of(oldMode);

  			// Save the properly serialized Set to the new config
  			configManager.setConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleModeSet", newModeSet);
  		}
  	}
  	catch (Exception e)
  	{
  		// Could not migrate, ignore
  		log.debug("BNPC: Could not migrate old tag style mode config to new set version", e);
  	}
  }
}
