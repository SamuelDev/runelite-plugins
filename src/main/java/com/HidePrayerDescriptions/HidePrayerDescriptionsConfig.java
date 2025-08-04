package com.HidePrayerDescriptions;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hideprayerdescriptions")
public interface HidePrayerDescriptionsConfig extends Config
{
	@ConfigItem(
		keyName = "hidePrayerLevel",
		name = "Hide Prayer Level",
		description = "Hide the level requirement in the prayer tooltip.",
		position = 0
	)
	default boolean hidePrayerLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hidePrayerName",
		name = "Hide Prayer Name",
		description = "Hide the name in the prayer tooltip.",
		position = 1
	)
	default boolean hidePrayerName()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "hidePrayerDescription",
		name = "Hide Prayer Description",
		description = "Hide the description in the prayer tooltip.",
		position = 2
	)
	default boolean hidePrayerDescription()
	{
		return true;
	}
	
	@ConfigItem(
		keyName = "hidePrayerDrain",
		name = "Hide Prayer Drain Rate",
		description = "Hide the drain rate in the prayer tooltip.",
		position = 3
	)
	default boolean hidePrayerDrain()
	{
		return true;
	}
}