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

import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;
import com.betternpchighlight.data.NpcSpawn;
import com.betternpchighlight.managers.ChatCommandManager;
import com.betternpchighlight.managers.ConfigTransformManager;
import com.betternpchighlight.managers.MenuManager;
import com.betternpchighlight.managers.SlayerPluginManager;
import com.betternpchighlight.overlays.BetterNpcHighlightOverlay;
import com.betternpchighlight.overlays.BetterNpcMinimapOverlay;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
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
import java.time.Instant;
import java.util.Set;

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
	private ChatCommandManager chatCommandManager;

	public Instant lastTickUpdate;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Provides
	BetterNpcHighlightConfig providesConfig(ConfigManager configManager) {
		migrateOldConfig(configManager);
		return configManager.getConfig(BetterNpcHighlightConfig.class);
	}

	protected void startUp() {
		clientThread.invokeLater(() -> {
			reset();
			overlayManager.add(overlay);
			overlayManager.add(mapOverlay);
			
			nameAndIdContainer.tileNames = configTransformManager.getList(config.tileNames());
			nameAndIdContainer.tileIds = configTransformManager.getList(config.tileIds());

			nameAndIdContainer.trueTileNames = configTransformManager.getList(config.trueTileNames());
			nameAndIdContainer.trueTileIds = configTransformManager.getList(config.trueTileIds());

			nameAndIdContainer.swTileNames = configTransformManager.getList(config.swTileNames());
			nameAndIdContainer.swTileIds = configTransformManager.getList(config.swTileIds());

			nameAndIdContainer.swTrueTileNames = configTransformManager.getList(config.swTrueTileNames());
			nameAndIdContainer.swTrueTileIds = configTransformManager.getList(config.swTrueTileIds());

			nameAndIdContainer.hullNames = configTransformManager.getList(config.hullNames());
			nameAndIdContainer.hullIds = configTransformManager.getList(config.hullIds());

			nameAndIdContainer.areaNames = configTransformManager.getList(config.areaNames());
			nameAndIdContainer.areaIds = configTransformManager.getList(config.areaIds());

			nameAndIdContainer.outlineNames = configTransformManager.getList(config.outlineNames());
			nameAndIdContainer.outlineIds = configTransformManager.getList(config.outlineIds());

			nameAndIdContainer.clickboxNames = configTransformManager.getList(config.clickboxNames());
			nameAndIdContainer.clickboxIds = configTransformManager.getList(config.clickboxIds());

			nameAndIdContainer.namesToDisplay = configTransformManager.getList(config.displayName());
			nameAndIdContainer.ignoreDeadExclusionList = configTransformManager.getList(config.ignoreDeadExclusion());
			nameAndIdContainer.ignoreDeadExclusionIDList = configTransformManager.getList(config.ignoreDeadExclusionID());
			nameAndIdContainer.hiddenNames = configTransformManager.getList(config.entityHiderNames());
			nameAndIdContainer.hiddenIds = configTransformManager.getList(config.entityHiderIds());
			nameAndIdContainer.beneathNPCs = configTransformManager.getList(config.drawBeneathList());

			hooks.registerRenderableDrawListener(drawListener);
			chatCommandManager.registerKeyListener();
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
		chatCommandManager.unregisterKeyListener();
	}

	private void reset() {
		nameAndIdContainer.npcList.clear();
		nameAndIdContainer.currentTask = "";
		nameAndIdContainer.clearAll();
		nameAndIdContainer.confirmedWarning = false;
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
			nameAndIdContainer.npcSpawns.clear();
			nameAndIdContainer.npcList.clear();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		menuManager.craftMenu(event);
	}

	@Subscribe(priority = -1)
	public void onNpcSpawned(NpcSpawned event) {
		NPC npc = event.getNpc();

		for (NpcSpawn n : nameAndIdContainer.npcSpawns)
		{
			if (npc.getIndex() == n.index && npc.getId() == n.id)
			{
				if (n.spawnPoint == null && n.diedOnTick != -1)
				{
					WorldPoint wp = client.isInInstancedRegion() ? WorldPoint.fromLocalInstance(client, npc.getLocalLocation())
							: WorldPoint.fromLocal(client, npc.getLocalLocation());
					if (n.spawnLocations.contains(wp))
					{
						n.spawnPoint = wp;
						n.respawnTime = client.getTickCount() - n.diedOnTick + 1;
					}
					else
					{
						n.spawnLocations.add(wp);
					}
				}
				n.dead = false;
				break;
			}
		}

		NPCInfo npcInfo = checkValidNPC(npc);
		if (npcInfo != null)
		{
			nameAndIdContainer.npcList.add(npcInfo);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		NPC npc = event.getNpc();

		if (npc.isDead())
		{
			if (nameAndIdContainer.npcList.stream().anyMatch(n -> n.getNpc() == npc)
					&& nameAndIdContainer.npcSpawns.stream().noneMatch(n -> n.index == npc.getIndex()))
			{
				nameAndIdContainer.npcSpawns.add(new NpcSpawn(npc));
			}
			else
			{
				for (NpcSpawn n : nameAndIdContainer.npcSpawns)
				{
					if (npc.getIndex() == n.index && npc.getId() == n.id)
					{
						n.diedOnTick = client.getTickCount();
						n.dead = true;
						break;
					}
				}
			}
		}

		nameAndIdContainer.npcList.removeIf(n -> n.getNpc().getIndex() == npc.getIndex());
	}

	@Subscribe(priority = -1)
	public void onNpcChanged(NpcChanged event) {
		NPC npc = event.getNpc();

		nameAndIdContainer.npcList.removeIf(n -> n.getNpc().getIndex() == npc.getIndex());

		NPCInfo npcInfo = checkValidNPC(npc);
		if (npcInfo != null)
		{
			nameAndIdContainer.npcList.add(npcInfo);
		}
	}

	@Subscribe(priority = -1)
	public void onGameTick(GameTick event) {
		if (slayerPluginIntegration.checkSlayerPluginEnabled() && !nameAndIdContainer.currentTask.equals(slayerPluginService.getTask()))
		{
			configTransformManager.recreateNPCInfoList();
		}

		lastTickUpdate = Instant.now();
	}

	public NPCInfo checkValidNPC(NPC npc) {
		NPCInfo n = new NPCInfo(npc, this, slayerPluginService, config, slayerPluginIntegration, nameAndIdContainer, configTransformManager);
		if (n.getTile().isHighlight() || n.getTrueTile().isHighlight() || n.getSwTile().isHighlight() || n.getSwTrueTile().isHighlight()
				|| n.getHull().isHighlight() || n.getArea().isHighlight() || n.getOutline().isHighlight() || n.getClickbox().isHighlight()
				|| n.isTask())
		{
			return n;
		}
		return null;
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;

			if (config.entityHiderToggle())
			{
				return !nameAndIdContainer.hiddenIds.contains(String.valueOf(npc.getId()))
						&& (npc.getName() != null && !nameAndIdContainer.hiddenNames.contains(npc.getName().toLowerCase()));
			}
		}
		return true;
	}

	private void migrateOldConfig(ConfigManager configManager) {
		// migrate old tag style mode config into the new set version of config if
		// possible
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
