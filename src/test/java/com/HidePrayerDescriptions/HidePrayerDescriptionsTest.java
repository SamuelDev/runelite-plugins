package com.HidePrayerDescriptions;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HidePrayerDescriptionsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HidePrayerDescriptionsPlugin.class);
		RuneLite.main(args);
	}
}