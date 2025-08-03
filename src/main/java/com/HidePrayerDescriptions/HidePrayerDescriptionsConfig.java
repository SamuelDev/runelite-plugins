package com.HidePrayerDescriptions;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hideprayerdescriptions")
public interface HidePrayerDescriptionsConfig extends Config
{
	@ConfigItem(
		keyName = "hidePrayerDescriptions",
		name = "Hide Prayer Descriptions",
		description = "Hide the descriptive text that appears when hovering over prayers"
	)
	default boolean hidePrayerDescriptions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideInPrayerTab",
		name = "Hide in Prayer Tab",
		description = "Hide prayer descriptions in the prayer tab interface"
	)
	default boolean hideInPrayerTab()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hideInQuickPrayers",
		name = "Hide in Quick Prayers",
		description = "Hide prayer descriptions in the quick prayer interface"
	)
	default boolean hideInQuickPrayers()
	{
		return true;
	}
}