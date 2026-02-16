package com.AverageTotalLevel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("averageTotalLevel")
public interface AverageTotalLevelConfig extends Config
{
	@ConfigItem(
		keyName = "averageTotalLevel",
		name = "Average Total Level",
		description = "Display average total level on the skill tab",
		position = 10
	)
	default boolean averageTotalLevel()
	{
		return true;
	}

		@ConfigItem(
		keyName = "averageTotalExperience",
		name = "Average Total Experience",
		description = "Display average total experience when hovering over total level",
		position = 20
	)
	default boolean averageTotalExperience()
	{
		return true;
	}

	@ConfigItem(
			keyName = "virtualAverageTotalLevel",
			name = "Use virtual level",
			description = "If virtual total level plugin is enabled, use virtual level when calculating average level",
			position = 30
	)
	default boolean virtualAverageTotalLevel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "useDecimals",
			name = "Use decimals in average level",
			description = "Choose whether or not to show decimal places in the average level",
			position = 40
	)
	default boolean useDecimals()
	{
		return true;
	}
}
