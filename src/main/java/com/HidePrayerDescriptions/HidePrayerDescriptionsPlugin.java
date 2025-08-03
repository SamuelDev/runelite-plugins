package com.HidePrayerDescriptions;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Hide Prayer Descriptions",
	description = "Hides the descriptive text when hovering over prayers",
	tags = {"prayer", "interface", "tooltip", "description"}
)
public class HidePrayerDescriptionsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HidePrayerDescriptionsConfig config;

	@Provides
	HidePrayerDescriptionsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HidePrayerDescriptionsConfig.class);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		// Prayer tab widget loaded
		if (widgetLoaded.getGroupId() == WidgetInfo.PRAYER_GROUP_ID)
		{
			// if (config.hideInPrayerTab())
			// {
			// 	hidePrayerTabDescriptions();
			// }
		}
		// Quick prayer widget loaded
		else if (widgetLoaded.getGroupId() == WidgetInfo.QUICK_PRAYER_GROUP_ID)
		{
			// if (config.hideInQuickPrayers())
			// {
			// 	hideQuickPrayerDescriptions();
			// }
		}
	}

	// @Subscribe
	// public void onGameTick(GameTick gameTick)
	// {
	// 	// Continuously apply hiding if enabled
	// 	if (config.hidePrayerDescriptions())
	// 	{
	// 		hidePrayerDescriptions();
	// 	}
	// }

	// private void hidePrayerDescriptions()
	// {
	// 	if (config.hideInPrayerTab())
	// 	{
	// 		hidePrayerTabDescriptions();
	// 	}
		
	// 	if (config.hideInQuickPrayers())
	// 	{
	// 		hideQuickPrayerDescriptions();
	// 	}
	// }

	// private void hidePrayerTabDescriptions()
	// {
	// 	Widget prayerContainer = client.getWidget(WidgetInfo.PRAYER_CONTAINER);
	// 	if (prayerContainer != null)
	// 	{
	// 		// TODO: Implement prayer tab description hiding logic
	// 		// This will involve iterating through prayer widgets and modifying their tooltip text
	// 		log.debug("Hiding prayer tab descriptions");
	// 	}
	// }

	// private void hideQuickPrayerDescriptions()
	// {
	// 	Widget quickPrayerContainer = client.getWidget(WidgetInfo.QUICK_PRAYER_CONTAINER);
	// 	if (quickPrayerContainer != null)
	// 	{
	// 		// TODO: Implement quick prayer description hiding logic
	// 		// This will involve modifying quick prayer tooltip text
	// 		log.debug("Hiding quick prayer descriptions");
	// 	}
	// }

	// private void restorePrayerDescriptions()
	// {
	// 	// TODO: Implement restoration logic
	// 	// This should restore original prayer description text when plugin is disabled
	// 	log.debug("Restoring prayer descriptions");
	// }
}