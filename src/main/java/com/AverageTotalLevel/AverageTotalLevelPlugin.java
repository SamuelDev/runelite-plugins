package com.AverageTotalLevel;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.Experience;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.PluginChanged;
import java.text.DecimalFormat;

@Slf4j
@PluginDescriptor(
	name = "Average Total Level"
)
public class AverageTotalLevelPlugin extends Plugin
{
	private static final String TTL_LEVEL_TEXT_PREFIX = "Ttl: ";
	private static final String AVG_LEVEL_TEXT_PREFIX = "Avg: ";

	@Inject
	private Client client;

	@Inject
	private AverageTotalLevelConfig config;

	@Inject
	private ClientThread clientThread;

	@Provides
	AverageTotalLevelConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AverageTotalLevelConfig.class);
	}

	@Override
	protected void shutDown()
	{
		clientThread.invoke(this::simulateSkillChange);
	}

	@Subscribe
	public void onPluginChanged(PluginChanged pluginChanged)
	{
		// this is guaranteed to be called after the plugin has been registered by the eventbus. startUp is not.
		if (pluginChanged.getPlugin() == this)
		{
			clientThread.invoke(this::simulateSkillChange);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals("averageTotalLevel"))
		{
			return;
		}

		clientThread.invoke(this::simulateSkillChange);
	}

	@Subscribe(priority = -1.0f)
	public void onScriptCallbackEvent(ScriptCallbackEvent e)
	{
		final String eventName = e.getEventName();

		final Object[] objStack = client.getObjectStack();
		final int objStackSize = client.getObjectStackSize();

		if (!config.averageTotalLevel())
		{
			return;
		}

		if (eventName.equals("skillTabTotalLevel"))
		{
			// In order to be compatible with virtual total levels the total level already stored in the string stack
			// is retrieved and concatenated into this plugin's string. This plugin's load priority is lower than
			// Virtual total level's so the correct total should always be displayed
			int displayedTotalLevel = Integer.parseInt(((String) objStack[objStackSize - 1]).split("<br>")[1]);
			
			objStack[objStackSize  - 1] =
				TTL_LEVEL_TEXT_PREFIX +
				displayedTotalLevel +
				"<br>" +
				AVG_LEVEL_TEXT_PREFIX +
				getAverageSkillLevel(displayedTotalLevel);
		}
	}

	private void simulateSkillChange()
	{
		// this fires widgets listening for all skill changes
		for (Skill skill : Skill.values())
		{
			client.queueChangedSkill(skill);
		}
	}

	private String getAverageSkillLevel(int displayedTotal)
	{
		int skillCount = Skill.values().length;
		float average = (float) (config.virtualAverageTotalLevel() ? displayedTotal : client.getTotalLevel()) / skillCount;

		if (config.useDecimals())
		{
			return new DecimalFormat("#.##").format(average);
		}
		else
		{
			return new DecimalFormat("#").format(Math.floor(average));
		}
	}
}
