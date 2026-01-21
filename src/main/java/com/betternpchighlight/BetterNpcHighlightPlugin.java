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
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.vars.InputType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Random;
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
	public int turboModeStyle = 0;
	public int turboTileWidth = 0;
	public int turboOutlineWidth = 0;
	public int turboOutlineFeather = 0;

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
			configTransformManager.splitList(config.tileNames(), nameAndIdContainer.tileNames);
			configTransformManager.splitList(config.tileIds(), nameAndIdContainer.tileIds);
			configTransformManager.splitList(config.trueTileNames(), nameAndIdContainer.trueTileNames);
			configTransformManager.splitList(config.trueTileIds(), nameAndIdContainer.trueTileIds);
			configTransformManager.splitList(config.swTileNames(), nameAndIdContainer.swTileNames);
			configTransformManager.splitList(config.swTileIds(), nameAndIdContainer.swTileIds);
			configTransformManager.splitList(config.swTrueTileNames(), nameAndIdContainer.swTrueTileNames);
			configTransformManager.splitList(config.swTrueTileIds(), nameAndIdContainer.swTrueTileIds);
			configTransformManager.splitList(config.hullNames(), nameAndIdContainer.hullNames);
			configTransformManager.splitList(config.hullIds(), nameAndIdContainer.hullIds);
			configTransformManager.splitList(config.areaNames(), nameAndIdContainer.areaNames);
			configTransformManager.splitList(config.areaIds(), nameAndIdContainer.areaIds);
			configTransformManager.splitList(config.outlineNames(), nameAndIdContainer.outlineNames);
			configTransformManager.splitList(config.outlineIds(), nameAndIdContainer.outlineIds);
			configTransformManager.splitList(config.clickboxNames(), nameAndIdContainer.clickboxNames);
			configTransformManager.splitList(config.clickboxIds(), nameAndIdContainer.clickboxIds);
			configTransformManager.splitList(config.turboNames(), nameAndIdContainer.turboNames);
			configTransformManager.splitList(config.turboIds(), nameAndIdContainer.turboIds);
			configTransformManager.splitList(config.displayName(), nameAndIdContainer.namesToDisplay);
			configTransformManager.splitList(config.ignoreDeadExclusion(), nameAndIdContainer.ignoreDeadExclusionList);
			configTransformManager.splitList(config.ignoreDeadExclusionID(), nameAndIdContainer.ignoreDeadExclusionIDList);
			configTransformManager.splitList(config.entityHiderNames(), nameAndIdContainer.hiddenNames);
			configTransformManager.splitList(config.entityHiderIds(), nameAndIdContainer.hiddenIds);
			configTransformManager.splitList(config.drawBeneathList(), nameAndIdContainer.beneathNPCs);

			hooks.registerRenderableDrawListener(drawListener);
			chatCommandManager.registerKeyListener();
			slayerPluginIntegration.enableSlayerPlugin();

			if (client.getGameState() == GameState.LOGGED_IN)
			{
				configTransformManager.recreateList();
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

	private void reset() {
		nameAndIdContainer.npcList.clear();
		nameAndIdContainer.currentTask = "";
		nameAndIdContainer.clearAll();
		turboModeStyle = 0;
		turboTileWidth = 0;
		turboOutlineWidth = 0;
		turboOutlineFeather = 0;
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
			configTransformManager.recreateList();
		}

		lastTickUpdate = Instant.now();
		nameAndIdContainer.turboColors.clear();
		for (int i = 0; i < nameAndIdContainer.npcList.size(); i++)
		{
			nameAndIdContainer.turboColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
		}
		turboModeStyle = new Random().nextInt(6);
		turboTileWidth = new Random().nextInt(10) + 1;
		turboOutlineWidth = new Random().nextInt(50) + 1;
		turboOutlineFeather = new Random().nextInt(4);
	}

	public NPCInfo checkValidNPC(NPC npc) {
		NPCInfo n = new NPCInfo(npc, this, slayerPluginService, config, slayerPluginIntegration, nameAndIdContainer, configTransformManager);
		if (n.getTile().isHighlight() || n.getTrueTile().isHighlight() || n.getSwTile().isHighlight() || n.getSwTrueTile().isHighlight()
				|| n.getHull().isHighlight() || n.getArea().isHighlight() || n.getOutline().isHighlight() || n.getClickbox().isHighlight()
				|| n.getTurbo().isHighlight() || n.isTask())
		{
			return n;
		}
		return null;
	}

	public void showEpilepsyWarning() {
		configManager.setConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "turboHighlight", false);
		Font font = (Font) UIManager.get("OptionPane.buttonFont");
		Object[] options = { "Okay, I accept the risk", "No, this is an affront to my eyes" };
		JLabel label = new JLabel(
				"<html><p>Turning this on will cause any NPCs highlighted with this style to change colors and styles rapidly.</p></html>");

		if (JOptionPane.showOptionDialog(new JFrame(), label, "EPILEPSY WARNING - Occular Abhorrence", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[1]) == 0)
		{
			nameAndIdContainer.confirmedWarning = true;
			configManager.setConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "turboHighlight", true);
		}
		UIManager.put("OptionPane.buttonFont", font);
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
}
