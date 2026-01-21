package com.betternpchighlight;

import java.util.ArrayList;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.slayer.SlayerPluginService;

public class ConfigTransformManager {
	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private SlayerPluginIntegration slayerPluginIntegration;

	@Inject
	private NameAndIdContainer nameAndIdContainer;

	@Inject
	private BetterNpcHighlightConfig config;

	@Inject
	private BetterNpcHighlightPlugin plugin;

	public void splitList(String configStr, ArrayList<String> strList) {
		clientThread.invokeLater(() -> {
			if (!configStr.equals("")) {
				for (String str : configStr.split(",")) {
					if (!str.trim().equals("")) {
						strList.add(str.trim().toLowerCase());
					}
				}
			}
		});
	}

	public void updateConfig(ConfigChanged event) {
		switch (event.getKey()) {
			case "tileNames":
				nameAndIdContainer.tileNames.clear();
				splitList(config.tileNames(), nameAndIdContainer.tileNames);
				recreateList();
				break;
			case "tileIds":
				nameAndIdContainer.tileIds.clear();
				splitList(config.tileIds(), nameAndIdContainer.tileIds);
				recreateList();
				break;
			case "trueTileNames":
				nameAndIdContainer.trueTileNames.clear();
				splitList(config.trueTileNames(), nameAndIdContainer.trueTileNames);
				recreateList();
				break;
			case "trueTileIds":
				nameAndIdContainer.trueTileIds.clear();
				splitList(config.trueTileIds(), nameAndIdContainer.trueTileIds);
				recreateList();
				break;
			case "swTileNames":
				nameAndIdContainer.swTileNames.clear();
				splitList(config.swTileNames(), nameAndIdContainer.swTileNames);
				recreateList();
				break;
			case "swTileIds":
				nameAndIdContainer.swTileIds.clear();
				splitList(config.swTileIds(), nameAndIdContainer.swTileIds);
				recreateList();
				break;
			case "swTrueTileNames":
				nameAndIdContainer.swTrueTileNames.clear();
				splitList(config.swTrueTileNames(), nameAndIdContainer.swTrueTileNames);
				recreateList();
				break;
			case "swTrueTileIds":
				nameAndIdContainer.swTrueTileIds.clear();
				splitList(config.swTrueTileIds(), nameAndIdContainer.swTrueTileIds);
				recreateList();
				break;
			case "hullNames":
				nameAndIdContainer.hullNames.clear();
				splitList(config.hullNames(), nameAndIdContainer.hullNames);
				recreateList();
				break;
			case "hullIds":
				nameAndIdContainer.hullIds.clear();
				splitList(config.hullIds(), nameAndIdContainer.hullIds);
				recreateList();
				break;
			case "areaNames":
				nameAndIdContainer.areaNames.clear();
				splitList(config.areaNames(), nameAndIdContainer.areaNames);
				recreateList();
				break;
			case "areaIds":
				nameAndIdContainer.areaIds.clear();
				splitList(config.areaIds(), nameAndIdContainer.areaIds);
				recreateList();
				break;
			case "outlineNames":
				nameAndIdContainer.outlineNames.clear();
				splitList(config.outlineNames(), nameAndIdContainer.outlineNames);
				recreateList();
				break;
			case "outlineIds":
				nameAndIdContainer.outlineIds.clear();
				splitList(config.outlineIds(), nameAndIdContainer.outlineIds);
				recreateList();
				break;
			case "clickboxNames":
				nameAndIdContainer.clickboxNames.clear();
				splitList(config.clickboxNames(), nameAndIdContainer.clickboxNames);
				recreateList();
				break;
			case "clickboxIds":
				nameAndIdContainer.clickboxIds.clear();
				splitList(config.clickboxIds(), nameAndIdContainer.clickboxIds);
				recreateList();
				break;
			case "turboNames":
				nameAndIdContainer.turboNames.clear();
				splitList(config.turboNames(), nameAndIdContainer.turboNames);
				recreateList();
				break;
			case "turboIds":
				nameAndIdContainer.turboIds.clear();
				splitList(config.turboIds(), nameAndIdContainer.turboIds);
				recreateList();
				break;
			case "displayName":
				nameAndIdContainer.namesToDisplay.clear();
				splitList(config.displayName(), nameAndIdContainer.namesToDisplay);
				break;
			case "ignoreDeadExclusion":
				nameAndIdContainer.ignoreDeadExclusionList.clear();
				splitList(config.ignoreDeadExclusion(), nameAndIdContainer.ignoreDeadExclusionList);
				recreateList();
				break;
			case "ignoreDeadExclusionID":
				nameAndIdContainer.ignoreDeadExclusionIDList.clear();
				splitList(config.ignoreDeadExclusionID(), nameAndIdContainer.ignoreDeadExclusionIDList);
				recreateList();
				break;
			case "turboHighlight":
				if (event.getNewValue().equals("true")) {
					if (!nameAndIdContainer.confirmedWarning) {
						plugin.showEpilepsyWarning();
					} else {
						nameAndIdContainer.confirmedWarning = false;
					}
				}
				break;
			case "entityHiderNames":
				nameAndIdContainer.hiddenNames.clear();
				splitList(config.entityHiderNames(), nameAndIdContainer.hiddenNames);
				break;
			case "entityHiderIds":
				nameAndIdContainer.hiddenIds.clear();
				splitList(config.entityHiderIds(), nameAndIdContainer.hiddenIds);
				break;
			case "drawBeneathList":
				nameAndIdContainer.beneathNPCs.clear();
				splitList(config.drawBeneathList(), nameAndIdContainer.beneathNPCs);
				break;
			case "slayerHighlight":
				slayerPluginIntegration.enableSlayerPlugin();
				break;
			case "tileColor":
			case "tileFillColor":
			case "trueTileColor":
			case "trueTileFillColor":
			case "swTileColor":
			case "swTileFillColor":
			case "swTrueTileColor":
			case "swTrueTileFillColor":
			case "hullColor":
			case "hullFillColor":
			case "areaColor":
			case "outlineColor":
			case "clickboxColor":
			case "clickboxFillColor":
			case "taskColor":
			case "taskFillColor":
			case "presetColor1":
			case "presetFillColor1":
			case "presetColor2":
			case "presetFillColor2":
			case "presetColor3":
			case "presetFillColor3":
			case "presetColor4":
			case "presetFillColor4":
			case "presetColor5":
			case "presetFillColor5":
			case "useGlobalTileColor":
			case "globalTileColor":
			case "globalFillColor":
				recreateList();
				break;
		}
	}

	public void recreateList() {
		clientThread.invokeLater(() -> {
			if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null
					&& client.getLocalPlayer().getPlayerComposition() != null) {
				nameAndIdContainer.npcList.clear();

				for (NPC npc : client.getNpcs()) {
					NPCInfo npcInfo = plugin.checkValidNPC(npc);
					if (npcInfo != null) {
						nameAndIdContainer.npcList.add(npcInfo);
					}
				}
				nameAndIdContainer.currentTask = slayerPluginService.getTask();
			}
		});
	}
}
