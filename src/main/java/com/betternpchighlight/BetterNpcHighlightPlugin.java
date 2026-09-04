/*
 * Copyright (c) 2022, Buchus <http://github.com/MoreBuchus>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.betternpchighlight;

import com.betternpchighlight.config.migrators.ConfigMigrator;
import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;
import com.betternpchighlight.managers.ConfigTransformManager;
import com.betternpchighlight.managers.MenuManager;
import com.betternpchighlight.managers.RespawnManager;
import com.betternpchighlight.managers.SlayerPluginManager;
import com.betternpchighlight.overlays.BetterNpcHighlightOverlay;
import com.betternpchighlight.overlays.BetterNpcMinimapOverlay;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Better NPC Highlight", description = "A more customizable NPC highlight", tags = { "npc", "highlight",
		"indicators", "respawn", "hide", "entity", "custom", "id", "name" })
@PluginDependency(SlayerPlugin.class)
public class BetterNpcHighlightPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BetterNpcHighlightOverlay overlay;

	@Inject
	private BetterNpcHighlightConfig config;

	@Inject
	private BetterNpcMinimapOverlay mapOverlay;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Hooks hooks;

	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SlayerPluginManager slayerPluginIntegration;

	@Inject
	private MenuManager menuManager;

	@Inject
	private ConfigTransformManager configTransformManager;

	@Inject
	private NameAndIdContainer nameAndIdContainer;

	@Inject
	private RespawnManager respawnManager;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Provides
	BetterNpcHighlightConfig providesConfig(ConfigManager configManager) {
		ConfigMigrator.migrateOldConfigs(configManager);
		return configManager.getConfig(BetterNpcHighlightConfig.class);
	}

	protected void startUp() {
		clientThread.invokeLater(() -> {
			reset();
			overlayManager.add(overlay);
			overlayManager.add(mapOverlay);

			configTransformManager.reloadLists();

			hooks.registerRenderableDrawListener(drawListener);
			slayerPluginIntegration.enableSlayerPlugin();

			if (client.getGameState() == GameState.LOGGED_IN)
			{
				configTransformManager.recreateNPCInfoList();
			}
		});
	}

	protected void shutDown() {
		reset();
		overlayManager.remove(overlay);
		overlayManager.remove(mapOverlay);
		hooks.unregisterRenderableDrawListener(drawListener);
	}

	private void reset() {
		nameAndIdContainer.getNpcList().clear();
		nameAndIdContainer.setCurrentTask("");
		nameAndIdContainer.clearAll();
		nameAndIdContainer.setConfirmedWarning(false);
		respawnManager.reset();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals(BetterNpcHighlightConfig.CONFIG_GROUP))
		{
			configTransformManager.updateConfig(event);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			nameAndIdContainer.getNpcList().clear();
			respawnManager.onGameStateChanged();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		menuManager.onMenuEntryAdded(event);
	}

	@Subscribe(priority = -1)
	public void onNpcSpawned(NpcSpawned event) {
		NPC npc = event.getNpc();

		NPCInfo npcInfo = configTransformManager.createNpcInfo(npc);
		if (npcInfo != null)
		{
			nameAndIdContainer.getNpcList().add(npcInfo);

			if (!client.isInInstancedRegion())
			{
				respawnManager.onNpcSpawned(npc);
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		NPC npc = event.getNpc();

		respawnManager.onNpcDespawned(npc);
		nameAndIdContainer.getNpcList().removeIf(n -> n.getNpc().getIndex() == npc.getIndex());
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
		respawnManager.onGraphicsObjectCreated(event);
	}

	@Subscribe(priority = -1)
	public void onNpcChanged(NpcChanged event) {
		NPC npc = event.getNpc();

		nameAndIdContainer.getNpcList().removeIf(n -> n.getNpc().getIndex() == npc.getIndex());

		NPCInfo npcInfo = configTransformManager.createNpcInfo(npc);
		if (npcInfo != null)
		{
			nameAndIdContainer.getNpcList().add(npcInfo);
		}
	}

	@Subscribe(priority = -1)
	public void onGameTick(GameTick event) {
		if (slayerPluginIntegration.checkSlayerPluginEnabled() && !nameAndIdContainer.getCurrentTask().equals(slayerPluginService.getTask()))
		{
			configTransformManager.recreateNPCInfoList();
		}

		respawnManager.onGameTick();
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;

			if (config.entityHiderToggle())
			{
				return !nameAndIdContainer.getHiddenIds().contains(String.valueOf(npc.getId()))
						&& (npc.getName() != null && !nameAndIdContainer.getHiddenNames().contains(npc.getName().toLowerCase()));
			}
		}
		return true;
	}
}