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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Menu;
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
import net.runelite.client.game.NpcUtil;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import org.apache.commons.lang3.StringUtils;
import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

@Slf4j
@PluginDescriptor(name = "Better NPC Highlight", description = "A more customizable NPC highlight", tags = { "npc",
		"highlight", "indicators", "respawn", "hide", "entity", "custom", "id", "name" })
@PluginDependency(SlayerPlugin.class)
public class BetterNpcHighlightPlugin extends Plugin implements KeyListener {
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
	private NpcUtil npcUtil;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Hooks hooks;

	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SlayerPluginIntegration slayerPluginIntegration;

	@Inject
	private MenuManager menuManager;

	@Inject
	private ConfigTransformManager configTransformManager;

	@Inject
	private NameAndIdContainer nameAndIdContainer;

	private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION,
			MenuAction.NPC_SECOND_OPTION,
			MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION,
			MenuAction.WIDGET_TARGET_ON_NPC,
			MenuAction.ITEM_USE_ON_NPC);

	public Instant lastTickUpdate;
	public int turboModeStyle = 0;
	public int turboTileWidth = 0;
	public int turboOutlineWidth = 0;
	public int turboOutlineFeather = 0;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	private static final String HIDE_COMMAND = "!hide";
	private static final String UNHIDE_COMMAND = "!unhide";
	private static final String TAG_COMMAND = "!tag";
	private static final String UNTAG_COMMAND = "!untag";

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
			keyManager.registerKeyListener(this);
			slayerPluginIntegration.enableSlayerPlugin();

			if (client.getGameState() == GameState.LOGGED_IN) {
				configTransformManager.recreateList();
			}
		});
	}

	protected void shutDown() {
		reset();
		overlayManager.remove(overlay);
		overlayManager.remove(mapOverlay);
		hooks.unregisterRenderableDrawListener(drawListener);
		keyManager.unregisterKeyListener(this);
	}

	private void migrateOldConfig(ConfigManager configManager) {
		// migrate old tag style mode config into the new set version of config if
		// possible
		try {
			String oldValue = configManager.getConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleMode");
			String newValue = configManager.getConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleModeSet");
			if (oldValue != null && newValue == null) {
				log.debug("BNPC: Migrating old tag style mode config to new set version");

				// Parse the old enum string value
				BetterNpcHighlightConfig.tagStyleMode oldMode = BetterNpcHighlightConfig.tagStyleMode
						.valueOf(oldValue.toUpperCase());

				// Convert to Set format
				Set<BetterNpcHighlightConfig.tagStyleMode> newModeSet = Set.of(oldMode);

				// Save the properly serialized Set to the new config
				configManager.setConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "tagStyleModeSet", newModeSet);
			}
		} catch (Exception e) {
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
		if (event.getGroup().equals(BetterNpcHighlightConfig.CONFIG_GROUP)) {
			configTransformManager.updateConfig(event);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
			nameAndIdContainer.npcSpawns.clear();
			nameAndIdContainer.npcList.clear();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		int type = event.getType();
		if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET) {
			type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
		}

		final MenuAction menuAction = MenuAction.of(type);
		if (NPC_MENU_ACTIONS.contains(menuAction)) {
			NPC npc = client.getTopLevelWorldView().npcs().byIndex(event.getIdentifier());

			Color color = null;
			if (npcUtil.isDying(npc)) {
				color = config.deadNpcMenuColor();
			} else if (config.highlightMenuNames() && npc.getName() != null) {
				for (NPCInfo npcInfo : nameAndIdContainer.npcList) {
					if (npcInfo.getNpc() == npc) {
						color = getSpecificColor(npcInfo);
						break;
					}
				}
			}

			if (color != null) {
				MenuEntry[] menuEntries = client.getMenuEntries();
				final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
				final String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
				menuEntry.setTarget(target);
				client.setMenuEntries(menuEntries);
			}
		} else if (menuAction == MenuAction.EXAMINE_NPC) {
			final int id = event.getIdentifier();
			final NPC npc = client.getTopLevelWorldView().npcs().byIndex(id);

			if (npc != null) {
				String option;
				// if there is an NPC name AND
				// ((tag style none is selected, and so is another option) OR
				// (tag style none is not selected, and another is))
				if (npc.getName() != null &&
						((config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.NONE)
								&& config.tagStyleModeSet().size() > 1) ||
								(!config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.NONE)
										&& !config.tagStyleModeSet().isEmpty()))) {
					if (checkSpecificNameList(nameAndIdContainer.tileNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.trueTileNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.swTileNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.swTrueTileNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.hullNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.areaNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.outlineNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.clickboxNames, npc) ||
							checkSpecificNameList(nameAndIdContainer.turboNames, npc)) {
						option = "Untag-NPC";
					} else {
						option = "Tag-NPC";
					}

					if (option.equals("Untag-NPC")
							&& (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null))) {
						MenuEntry[] menuEntries = client.getMenuEntries();
						final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
						String target;
						if (checkSpecificNameList(nameAndIdContainer.turboNames, npc)) {
							target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()),
									Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
						} else {
							Color color = npc.isDead() ? config.deadNpcMenuColor() : getTagColor();
							target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
						}
						menuEntry.setTarget(target);
						client.setMenuEntries(menuEntries);
					}

					if (client.isKeyPressed(KeyCode.KC_SHIFT)) {
						String tagAllEntry;
						if (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null)) {
							String colorCode;
							if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TURBO)) {
								if (nameAndIdContainer.turboColors.size() == 0
										&& nameAndIdContainer.turboNames.contains(npc.getName().toLowerCase())) {
									colorCode = Integer
											.toHexString(nameAndIdContainer.turboColors
													.get(nameAndIdContainer.turboNames.indexOf(npc.getName().toLowerCase())).getRGB());
								} else {
									colorCode = Integer.toHexString(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F).getRGB());
								}
							} else {
								colorCode = npc.isDead() ? Integer.toHexString(config.deadNpcMenuColor().getRGB())
										: Integer.toHexString(getTagColor().getRGB());
							}
							tagAllEntry = "<col=" + colorCode.substring(2) + ">" + Text.removeTags(event.getTarget());
						} else {
							tagAllEntry = event.getTarget();
						}

						int idx = -1;
						MenuEntry parent = client.createMenuEntry(idx)
								.setOption(option)
								.setTarget(tagAllEntry)
								.setIdentifier(event.getIdentifier())
								.setParam0(event.getActionParam0())
								.setParam1(event.getActionParam1())
								.setType(MenuAction.RUNELITE)
								.onClick(this::tagNPC);

						if (parent != null) {
							menuManager.customColorTag(idx, npc, parent);
						}
					}
				}
			}
		}
	}

	public void tagNPC(MenuEntry event) {
		if (event.getType() == MenuAction.RUNELITE) {
			if (event.getOption().equals("Tag-NPC") || event.getOption().equals("Untag-NPC")) {
				final int id = event.getIdentifier();
				final NPC npc = client.getTopLevelWorldView().npcs().byIndex(id);
				boolean tag = event.getOption().equals("Tag-NPC");
				if (npc.getName() != null) {
					menuManager.updateListConfig(tag, npc.getName().toLowerCase(), 0);
				}
			}
		}
	}

	@Subscribe(priority = -1)
	public void onNpcSpawned(NpcSpawned event) {
		NPC npc = event.getNpc();

		for (NpcSpawn n : nameAndIdContainer.npcSpawns) {
			if (npc.getIndex() == n.index && npc.getId() == n.id) {
				if (n.spawnPoint == null && n.diedOnTick != -1) {
					WorldPoint wp = client.isInInstancedRegion() ? WorldPoint.fromLocalInstance(client, npc.getLocalLocation())
							: WorldPoint.fromLocal(client, npc.getLocalLocation());
					if (n.spawnLocations.contains(wp)) {
						n.spawnPoint = wp;
						n.respawnTime = client.getTickCount() - n.diedOnTick + 1;
					} else {
						n.spawnLocations.add(wp);
					}
				}
				n.dead = false;
				break;
			}
		}

		NPCInfo npcInfo = checkValidNPC(npc);
		if (npcInfo != null) {
			nameAndIdContainer.npcList.add(npcInfo);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event) {
		NPC npc = event.getNpc();

		if (npc.isDead()) {
			if (nameAndIdContainer.npcList.stream().anyMatch(n -> n.getNpc() == npc)
					&& nameAndIdContainer.npcSpawns.stream().noneMatch(n -> n.index == npc.getIndex())) {
				nameAndIdContainer.npcSpawns.add(new NpcSpawn(npc));
			} else {
				for (NpcSpawn n : nameAndIdContainer.npcSpawns) {
					if (npc.getIndex() == n.index && npc.getId() == n.id) {
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
		if (npcInfo != null) {
			nameAndIdContainer.npcList.add(npcInfo);
		}
	}

	@Subscribe(priority = -1)
	public void onGameTick(GameTick event) {
		if (slayerPluginIntegration.checkSlayerPluginEnabled() && !nameAndIdContainer.currentTask.equals(slayerPluginService.getTask())) {
			configTransformManager.recreateList();
		}

		lastTickUpdate = Instant.now();
		nameAndIdContainer.turboColors.clear();
		for (int i = 0; i < nameAndIdContainer.npcList.size(); i++) {
			nameAndIdContainer.turboColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
		}
		turboModeStyle = new Random().nextInt(6);
		turboTileWidth = new Random().nextInt(10) + 1;
		turboOutlineWidth = new Random().nextInt(50) + 1;
		turboOutlineFeather = new Random().nextInt(4);
	}

	public NPCInfo checkValidNPC(NPC npc) {
		NPCInfo n = new NPCInfo(npc, this, slayerPluginService, config, slayerPluginIntegration, nameAndIdContainer);
		if (n.getTile().isHighlight() || n.getTrueTile().isHighlight() || n.getSwTile().isHighlight()
				|| n.getSwTrueTile().isHighlight() || n.getHull().isHighlight()
				|| n.getArea().isHighlight() || n.getOutline().isHighlight() || n.getClickbox().isHighlight()
				|| n.getTurbo().isHighlight() || n.isTask()) {
			return n;
		}
		return null;
	}

	public HighlightColor checkSpecificList(ArrayList<String> strList, ArrayList<String> idList, NPC npc,
			Color configColor, Color configFillColor) {
		for (String entry : idList) {
			int id = -1;
			String preset = "";
			if (entry.contains(":")) {
				String[] strArr = entry.split(":");
				if (StringUtils.isNumeric(strArr[0])) {
					id = Integer.parseInt(strArr[0]);
				}
				preset = strArr[1];
			} else if (StringUtils.isNumeric(entry)) {
				id = Integer.parseInt(entry);
			}

			if (id == npc.getId()) {
				return new HighlightColor(true, getHighlightColor(preset, configColor),
						getHighlightFillColor(preset, configFillColor));
			}
		}

		if (npc.getName() != null) {
			String name = npc.getName().toLowerCase();
			for (String entry : strList) {
				String nameStr = entry;
				String preset = "";
				if (entry.contains(":")) {
					String[] strArr = entry.split(":");
					nameStr = strArr[0];
					preset = strArr[1];
				}

				if (WildcardMatcher.matches(nameStr, name)) {
					return new HighlightColor(true, getHighlightColor(preset, configColor),
							getHighlightFillColor(preset, configFillColor));
				}
			}
		}
		return new HighlightColor(false, configColor, configFillColor);
	}

	public boolean checkSpecificNameList(ArrayList<String> strList, NPC npc) {
		if (npc.getName() != null) {
			String name = npc.getName().toLowerCase();
			for (String entry : strList) {
				String nameStr = entry;
				if (entry.contains(":")) {
					String[] strArr = entry.split(":");
					nameStr = strArr[0];
				}

				if (WildcardMatcher.matches(nameStr, name)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkSpecificIdList(ArrayList<String> strList, NPC npc) {
		int id = npc.getId();
		for (String entry : strList) {
			if (StringUtils.isNumeric(entry) && Integer.parseInt(entry) == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Color of the tag menu (ex. "Tag-Hull")
	 *
	 * @return Color
	 */
	public Color getTagColor() {
		if (config.useGlobalTileColor()) {
			return config.globalTileColor();
		}
		if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TILE)) {
			return config.tileColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TRUE_TILE)) {
			return config.trueTileColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TILE)) {
			return config.swTileColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TRUE_TILE)) {
			return config.swTrueTileColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.HULL)) {
			return config.hullColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.AREA)) {
			return config.areaColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.OUTLINE)) {
			return config.outlineColor();
		} else if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.CLICKBOX)) {
			return config.clickboxColor();
		} else {
			return Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F);
		}
	}

	/**
	 * Color of the NPC in the list
	 * Used for Minimap dot and displayed names
	 *
	 * @return Color
	 */
	public Color getSpecificColor(NPCInfo n) {
		if (n.isTask() && config.slayerHighlight()) {
			return config.slayerRave() ? getRaveColor(config.slayerRaveSpeed()) : config.taskColor();
		} else if (n.getTile().isHighlight() && config.tileHighlight()) {
			return config.tileRave() ? getRaveColor(config.tileRaveSpeed()) : n.getTile().getColor();
		} else if (n.getTrueTile().isHighlight() && config.trueTileHighlight()) {
			return config.trueTileRave() ? getRaveColor(config.trueTileRaveSpeed()) : n.getTrueTile().getColor();
		} else if (n.getSwTile().isHighlight() && config.swTileHighlight()) {
			return config.swTileRave() ? getRaveColor(config.swTileRaveSpeed()) : n.getSwTile().getColor();
		} else if (n.getSwTrueTile().isHighlight() && config.swTrueTileHighlight()) {
			return config.swTrueTileRave() ? getRaveColor(config.swTrueTileRaveSpeed()) : n.getSwTrueTile().getColor();
		} else if (n.getHull().isHighlight() && config.hullHighlight()) {
			return config.hullRave() ? getRaveColor(config.hullRaveSpeed()) : n.getHull().getColor();
		} else if (n.getArea().isHighlight() && config.areaHighlight()) {
			return config.areaRave() ? getRaveColor(config.areaRaveSpeed()) : n.getArea().getColor();
		} else if (n.getOutline().isHighlight() && config.outlineHighlight()) {
			return config.outlineRave() ? getRaveColor(config.outlineRaveSpeed()) : n.getOutline().getColor();
		} else if (n.getClickbox().isHighlight() && config.clickboxHighlight()) {
			return config.clickboxRave() ? getRaveColor(config.clickboxRaveSpeed()) : n.getClickbox().getColor();
		} else if (n.getTurbo().isHighlight() && config.turboHighlight()) {
			return getTurboIndex(n.getNpc().getId(), n.getNpc().getName()) != -1
					? nameAndIdContainer.turboColors.get(getTurboIndex(n.getNpc().getId(), n.getNpc().getName()))
					: Color.WHITE;
		} else {
			return null;
		}
	}

	/**
	 * Returns color of either the config or a preset if selected
	 *
	 * @return Color
	 */
	public Color getHighlightColor(String preset, Color color) {
		switch (preset) {
			case "1":
				return config.presetColor1();
			case "2":
				return config.presetColor2();
			case "3":
				return config.presetColor3();
			case "4":
				return config.presetColor4();
			case "5":
				return config.presetColor5();
		}

		return color;
	}

	/**
	 * Returns fill color of either the config or a preset if selected
	 *
	 * @return Color
	 */
	public Color getHighlightFillColor(String preset, Color color) {
		switch (preset) {
			case "1":
				return config.presetFillColor1();
			case "2":
				return config.presetFillColor2();
			case "3":
				return config.presetFillColor3();
			case "4":
				return config.presetFillColor4();
			case "5":
				return config.presetFillColor5();
		}

		return color;
	}

	public Color getRaveColor(int speed) {
		int ticks = speed / 20;
		return Color.getHSBColor((client.getGameCycle() % ticks) / ((float) ticks), 1.0f, 1.0f);
	}

	public int getTurboIndex(int id, String name) {
		if (nameAndIdContainer.turboIds.contains(String.valueOf(id))) {
			return nameAndIdContainer.turboIds.indexOf(String.valueOf(id));
		} else if (name != null) {
			int index = nameAndIdContainer.turboIds.size() - 1;
			for (String str : nameAndIdContainer.turboNames) {
				if (WildcardMatcher.matches(str, name)) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	public void showEpilepsyWarning() {
		configManager.setConfiguration(config.CONFIG_GROUP, "turboHighlight", false);
		Font font = (Font) UIManager.get("OptionPane.buttonFont");
		Object[] options = { "Okay, I accept the risk", "No, this is an affront to my eyes" };
		JLabel label = new JLabel(
				"<html><p>Turning this on will cause any NPCs highlighted with this style to change colors and styles rapidly.</p></html>");

		if (JOptionPane.showOptionDialog(new JFrame(),
				label,
				"EPILEPSY WARNING - Occular Abhorrence",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				options,
				options[1]) == 0) {
			nameAndIdContainer.confirmedWarning = true;
			configManager.setConfiguration(BetterNpcHighlightConfig.CONFIG_GROUP, "turboHighlight", true);
		}
		UIManager.put("OptionPane.buttonFont", font);
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (renderable instanceof NPC) {
			NPC npc = (NPC) renderable;

			if (config.entityHiderToggle()) {
				return !nameAndIdContainer.hiddenIds.contains(String.valueOf(npc.getId()))
						&& (npc.getName() != null && !nameAndIdContainer.hiddenNames.contains(npc.getName().toLowerCase()));
			}
		}
		return true;
	}

	public void keyPressed(KeyEvent e) {
		// Enter is pressed
		if (e.getKeyCode() == 10) {
			int inputType = client.getVarcIntValue(VarClientInt.INPUT_TYPE);
			if (inputType == InputType.PRIVATE_MESSAGE.getType() || inputType == InputType.NONE.getType()) {
				int var;
				if (inputType == InputType.PRIVATE_MESSAGE.getType()) {
					var = VarClientStr.INPUT_TEXT;
				} else {
					var = VarClientStr.CHATBOX_TYPED_TEXT;
				}

				if (client.getVarcStrValue(var) != null && !client.getVarcStrValue(var).isEmpty()) {
					String text = client.getVarcStrValue(var).toLowerCase();
					if (config.entityHiderCommands() && (text.startsWith(HIDE_COMMAND) || text.startsWith(UNHIDE_COMMAND))) {
						hideNPCCommand(text, var);
					} else if (config.tagCommands() && (text.startsWith(TAG_COMMAND) || text.startsWith(UNTAG_COMMAND))) {
						tagNPCCommand(text, var);
					}
				}
			}
		}
	}

	private void hideNPCCommand(String text, int var) {
		String npcToHide = text.replace(text.startsWith(HIDE_COMMAND) ? HIDE_COMMAND : UNHIDE_COMMAND, "").trim();
		boolean hide = text.startsWith(HIDE_COMMAND);

		if (!npcToHide.isEmpty()) {
			if (StringUtils.isNumeric(npcToHide)) {
				config.setEntityHiderIds(menuManager.configListToString(hide, npcToHide, nameAndIdContainer.hiddenIds, 0));
			} else {
				config.setEntityHiderNames(menuManager.configListToString(hide, npcToHide, nameAndIdContainer.hiddenNames, 0));
			}
		} else {
			printMessage("Please enter a valid NPC name or ID!");
		}

		// Set typed text to nothing
		client.setVarcStrValue(var, "");
	}

	private void tagNPCCommand(String text, int var) {
		if (text.trim().equals(TAG_COMMAND) || text.trim().equals(UNTAG_COMMAND)) {
			printMessage("Please enter a tag abbreviation followed by a valid NPC name or ID!");
		} else if (text.contains(TAG_COMMAND + " ") || text.contains(UNTAG_COMMAND + " ")) {
			printMessage("Please enter a valid tag abbreviation!");
		} else if (!text.trim().contains(" ")) {
			printMessage("Please enter a valid NPC name or ID!");
		} else {
			String npcToTag = text.substring(text.indexOf(" ") + 1).toLowerCase().trim();
			int preset = 0;
			if (npcToTag.contains(":")) {
				String[] strArr = npcToTag.split(":");
				npcToTag = strArr[0];
				if (StringUtils.isNumeric(strArr[1]))
					preset = Integer.parseInt(strArr[1]);
			}
			boolean tag = text.startsWith(TAG_COMMAND);

			if (!npcToTag.isEmpty()) {
				if (validateCommand(text, "t ") || validateCommand(text, "tile ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setTileIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.tileIds, preset));
					} else {
						config.setTileNames(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.tileNames, preset));
					}
				} else if (validateCommand(text, "tt ") || validateCommand(text, "truetile ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config
								.setTrueTileIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.trueTileIds, preset));
					} else {
						config.setTrueTileNames(
								menuManager.configListToString(tag, npcToTag, nameAndIdContainer.trueTileNames, preset));
					}
				} else if (validateCommand(text, "sw ") || validateCommand(text, "swt ")
						|| validateCommand(text, "southwesttile ") || validateCommand(text, "southwest ")
						|| validateCommand(text, "swtile ") || validateCommand(text, "southwestt ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setSwTileIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.swTileIds, preset));
					} else {
						config
								.setSwTileNames(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.swTileNames, preset));
					}
				} else if (validateCommand(text, "swtt ") || validateCommand(text, "swtruetile ")
						|| validateCommand(text, "southwesttruetile ") || validateCommand(text, "southwesttt ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setSwTrueTileIds(
								menuManager.configListToString(tag, npcToTag, nameAndIdContainer.swTrueTileIds, preset));
					} else {
						config.setSwTrueTileNames(
								menuManager.configListToString(tag, npcToTag, nameAndIdContainer.swTrueTileNames, preset));
					}
				} else if (validateCommand(text, "h ") || validateCommand(text, "hull ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setHullIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.hullIds, preset));
					} else {
						config.setHullNames(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.hullNames, preset));
					}
				} else if (validateCommand(text, "a ") || validateCommand(text, "area ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setAreaIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.areaIds, preset));
					} else {
						config.setAreaNames(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.areaNames, preset));
					}
				} else if (validateCommand(text, "o ") || validateCommand(text, "outline ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setOutlineIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.outlineIds, preset));
					} else {
						config.setOutlineNames(
								menuManager.configListToString(tag, npcToTag, nameAndIdContainer.outlineNames, preset));
					}
				} else if (validateCommand(text, "c ") || validateCommand(text, "clickbox ") || validateCommand(text, "box ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config
								.setClickboxIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.clickboxIds, preset));
					} else {
						config.setClickboxNames(
								menuManager.configListToString(tag, npcToTag, nameAndIdContainer.clickboxNames, preset));
					}
				} else if (validateCommand(text, "tu ") || validateCommand(text, "turbo ")) {
					if (StringUtils.isNumeric(npcToTag)) {
						config.setTurboIds(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.turboIds, preset));
					} else {
						config.setTurboNames(menuManager.configListToString(tag, npcToTag, nameAndIdContainer.turboNames, preset));
					}
				}
			}
		}
		// Set typed text to nothing
		client.setVarcStrValue(var, "");
	}

	public boolean validateCommand(String command, String type) {
		return command.startsWith(TAG_COMMAND + type) || command.startsWith(UNTAG_COMMAND + type);
	}

	public void printMessage(String msg) {
		final ChatMessageBuilder message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(msg);

		chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(message.build())
				.build());
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}
