package com.betternpchighlight.managers;

import java.util.Optional;

import javax.inject.Inject;

import com.betternpchighlight.BetterNpcHighlightConfig;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;

@Slf4j
public class SlayerPluginManager {
	@Inject
	private PluginManager pluginManager;

	@Inject
	private BetterNpcHighlightConfig config;

	public boolean checkSlayerPluginEnabled() {
		final Optional<Plugin> slayerPlugin = pluginManager.getPlugins().stream().filter(p -> p.getName().equals("Slayer")).findFirst();
		return slayerPlugin.isPresent() && pluginManager.isPluginEnabled(slayerPlugin.get());
	}

	public void enableSlayerPlugin() {
		try
		{
			final Optional<Plugin> slayerPlugin = pluginManager.getPlugins().stream().filter(p -> p.getName().equals("Slayer")).findFirst();
			if (slayerPlugin.isPresent() && !pluginManager.isPluginEnabled(slayerPlugin.get()) && config.slayerHighlight())
			{
				pluginManager.setPluginEnabled(slayerPlugin.get(), true);
				pluginManager.startPlugin(slayerPlugin.get());
			}
		}
		catch (PluginInstantiationException ex)
		{
			log.error("error starting slayer plugin", ex);
		}
	}
}