// package com.betternpchighlight.managers;

// import java.awt.Color;
// import java.util.ArrayList;
// import javax.inject.Inject;
// import org.apache.commons.lang3.StringUtils;

// import com.betternpchighlight.BetterNpcHighlightConfig;
// import com.betternpchighlight.BetterNpcHighlightPlugin;
// import com.betternpchighlight.data.HighlightColor;
// import com.betternpchighlight.data.NPCInfo;
// import com.betternpchighlight.data.NameAndIdContainer;

// import lombok.extern.slf4j.Slf4j;
// import net.runelite.api.Client;
// import net.runelite.api.GameState;
// import net.runelite.api.NPC;
// import net.runelite.client.callback.ClientThread;
// import net.runelite.client.events.ConfigChanged;
// import net.runelite.client.plugins.slayer.SlayerPluginService;
// import net.runelite.client.util.Text;
// import net.runelite.client.util.WildcardMatcher;
// import net.runelite.api.WorldView;

// @Slf4j
// public class ConfigTransformManager {
// 	@Inject
// 	private ClientThread clientThread;

// 	@Inject
// 	private Client client;

// 	@Inject
// 	private SlayerPluginService slayerPluginService;

// 	@Inject
// 	private SlayerPluginManager slayerPluginIntegration;

// 	@Inject
// 	private NameAndIdContainer nameAndIdContainer;

// 	@Inject
// 	private BetterNpcHighlightConfig config;

// 	@Inject
// 	private BetterNpcHighlightPlugin plugin;

// 	@Inject
// 	private ColorManager colorManager;

// 	public void splitList(String configStr, ArrayList<String> strList) {
// 		clientThread.invokeLater(() -> {
// 			if (!configStr.equals(""))
// 			{
// 				for (String str : configStr.split(","))
// 				{
// 					if (!str.trim().equals(""))
// 					{
// 						strList.add(str.trim().toLowerCase());
// 					}
// 				}
// 			}
// 		});
// 	}

// 	public ArrayList<String> getList(String configStr) {
// 		ArrayList<String> strList = new ArrayList<>();
// 		if (!configStr.equals(""))
// 		{
// 			for (String str : configStr.split(","))
// 			{
// 				if (!str.trim().equals(""))
// 				{
// 					strList.add(str.trim().toLowerCase());
// 				}
// 			}
// 		}

// 		return strList;
// 	}

// 	public void updateConfig(ConfigChanged event) {
// 		switch (event.getKey()) {
// 		case "tileNames":
// 			nameAndIdContainer.tileNames.clear();
// 			splitList(config.tileNames(), nameAndIdContainer.tileNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "tileIds":
// 			nameAndIdContainer.tileIds.clear();
// 			splitList(config.tileIds(), nameAndIdContainer.tileIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "trueTileNames":
// 			nameAndIdContainer.trueTileNames.clear();
// 			splitList(config.trueTileNames(), nameAndIdContainer.trueTileNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "trueTileIds":
// 			nameAndIdContainer.trueTileIds.clear();
// 			splitList(config.trueTileIds(), nameAndIdContainer.trueTileIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "swTileNames":
// 			nameAndIdContainer.swTileNames.clear();
// 			splitList(config.swTileNames(), nameAndIdContainer.swTileNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "swTileIds":
// 			nameAndIdContainer.swTileIds.clear();
// 			splitList(config.swTileIds(), nameAndIdContainer.swTileIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "swTrueTileNames":
// 			nameAndIdContainer.swTrueTileNames.clear();
// 			splitList(config.swTrueTileNames(), nameAndIdContainer.swTrueTileNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "swTrueTileIds":
// 			nameAndIdContainer.swTrueTileIds.clear();
// 			splitList(config.swTrueTileIds(), nameAndIdContainer.swTrueTileIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "hullNames":
// 			nameAndIdContainer.hullNames.clear();
// 			splitList(config.hullNames(), nameAndIdContainer.hullNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "hullIds":
// 			nameAndIdContainer.hullIds.clear();
// 			splitList(config.hullIds(), nameAndIdContainer.hullIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "areaNames":
// 			nameAndIdContainer.areaNames.clear();
// 			splitList(config.areaNames(), nameAndIdContainer.areaNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "areaIds":
// 			nameAndIdContainer.areaIds.clear();
// 			splitList(config.areaIds(), nameAndIdContainer.areaIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "outlineNames":
// 			nameAndIdContainer.outlineNames.clear();
// 			splitList(config.outlineNames(), nameAndIdContainer.outlineNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "outlineIds":
// 			nameAndIdContainer.outlineIds.clear();
// 			splitList(config.outlineIds(), nameAndIdContainer.outlineIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "clickboxNames":
// 			nameAndIdContainer.clickboxNames.clear();
// 			splitList(config.clickboxNames(), nameAndIdContainer.clickboxNames);
// 			recreateNPCInfoList();
// 			break;
// 		case "clickboxIds":
// 			nameAndIdContainer.clickboxIds.clear();
// 			splitList(config.clickboxIds(), nameAndIdContainer.clickboxIds);
// 			recreateNPCInfoList();
// 			break;
// 		case "displayName":
// 			nameAndIdContainer.namesToDisplay.clear();
// 			splitList(config.displayName(), nameAndIdContainer.namesToDisplay);
// 			break;
// 		case "ignoreDeadExclusion":
// 			nameAndIdContainer.ignoreDeadExclusionList.clear();
// 			splitList(config.ignoreDeadExclusion(), nameAndIdContainer.ignoreDeadExclusionList);
// 			recreateNPCInfoList();
// 			break;
// 		case "ignoreDeadExclusionID":
// 			nameAndIdContainer.ignoreDeadExclusionIDList.clear();
// 			splitList(config.ignoreDeadExclusionID(), nameAndIdContainer.ignoreDeadExclusionIDList);
// 			recreateNPCInfoList();
// 			break;
// 		case "entityHiderNames":
// 			nameAndIdContainer.hiddenNames.clear();
// 			splitList(config.entityHiderNames(), nameAndIdContainer.hiddenNames);
// 			break;
// 		case "entityHiderIds":
// 			nameAndIdContainer.hiddenIds.clear();
// 			splitList(config.entityHiderIds(), nameAndIdContainer.hiddenIds);
// 			break;
// 		case "drawBeneathList":
// 			nameAndIdContainer.beneathNPCs.clear();
// 			splitList(config.drawBeneathList(), nameAndIdContainer.beneathNPCs);
// 			break;
// 		case "slayerHighlight":
// 			slayerPluginIntegration.enableSlayerPlugin();
// 			break;
// 		case "tileColor":
// 		case "tileFillColor":
// 		case "trueTileColor":
// 		case "trueTileFillColor":
// 		case "swTileColor":
// 		case "swTileFillColor":
// 		case "swTrueTileColor":
// 		case "swTrueTileFillColor":
// 		case "hullColor":
// 		case "hullFillColor":
// 		case "areaColor":
// 		case "outlineColor":
// 		case "clickboxColor":
// 		case "clickboxFillColor":
// 		case "taskColor":
// 		case "taskFillColor":
// 		case "presetColor1":
// 		case "presetFillColor1":
// 		case "presetColor2":
// 		case "presetFillColor2":
// 		case "presetColor3":
// 		case "presetFillColor3":
// 		case "presetColor4":
// 		case "presetFillColor4":
// 		case "presetColor5":
// 		case "presetFillColor5":
// 		case "useGlobalTileColor":
// 		case "globalTileColor":
// 		case "globalFillColor":
// 			recreateNPCInfoList();
// 			break;
// 		}
// 	}

// 	public void recreateNPCInfoList() {
// 		clientThread.invokeLater(() -> {
// 			if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null
// 					&& client.getLocalPlayer().getPlayerComposition() != null)
// 			{
// 				nameAndIdContainer.npcList.clear();

// 				recreateNPCInfoListForWorldView(client.getTopLevelWorldView());

// 				nameAndIdContainer.currentTask = slayerPluginService.getTask() == null ? "" : slayerPluginService.getTask();
// 			}
// 		});
// 	}

// 	private void recreateNPCInfoListForWorldView(WorldView wv) {
// 		for (NPC npc : wv.npcs())
// 		{
// 			NPCInfo npcInfo = plugin.checkValidNPC(npc);
// 			if (npcInfo != null)
// 			{
// 				nameAndIdContainer.npcList.add(npcInfo);
// 			}
// 		}

// 		for (WorldView subWv : wv.worldViews())
// 		{
// 			recreateNPCInfoListForWorldView(subWv);
// 		}
// 	}

// 	public String configListToString(boolean tagOrHide, String name, ArrayList<String> strList, int preset) {
// 		if (tagOrHide)
// 		{
// 			boolean foundName = false;
// 			String newName = preset > 0 ? name + ":" + preset : name;
// 			for (String str : strList)
// 			{
// 				if (str.startsWith(name + ":") || str.equalsIgnoreCase(name))
// 				{
// 					strList.set(strList.indexOf(str), newName);
// 					foundName = true;
// 				}
// 			}

// 			if (!foundName)
// 			{
// 				strList.add(newName);
// 			}
// 		}
// 		else
// 		{
// 			strList.removeIf(str -> str.toLowerCase().startsWith(name + ":") || str.equalsIgnoreCase(name));
// 		}
// 		return Text.toCSV(strList);
// 	}

// 	public void updateListConfig(boolean add, String name, int preset) {
// 		if (!add)
// 		{
// 			removeAllTagStyles(name);
// 		}
// 		else
// 		{
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TILE))
// 			{
// 				config.setTileNames(configListToString(add, name, nameAndIdContainer.tileNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TRUE_TILE))
// 			{
// 				config.setTrueTileNames(configListToString(add, name, nameAndIdContainer.trueTileNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TILE))
// 			{
// 				config.setSwTileNames(configListToString(add, name, nameAndIdContainer.swTileNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TRUE_TILE))
// 			{
// 				config.setSwTrueTileNames(configListToString(add, name, nameAndIdContainer.swTrueTileNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.HULL))
// 			{
// 				config.setHullNames(configListToString(add, name, nameAndIdContainer.hullNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.AREA))
// 			{
// 				config.setAreaNames(configListToString(add, name, nameAndIdContainer.areaNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.OUTLINE))
// 			{
// 				config.setOutlineNames(configListToString(add, name, nameAndIdContainer.outlineNames, preset));
// 			}
// 			if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.CLICKBOX))
// 			{
// 				config.setClickboxNames(configListToString(add, name, nameAndIdContainer.clickboxNames, preset));
// 			}
// 		}
// 	}

// 	public HighlightColor getHighlightColorFromNameOrIdList(ArrayList<String> strList, ArrayList<String> idList, NPC npc, Color configColor,
// 			Color configFillColor) {
// 		for (String entry : idList)
// 		{
// 			int id = -1;
// 			String preset = "";
// 			if (entry.contains(":"))
// 			{
// 				String[] strArr = entry.split(":");
// 				if (StringUtils.isNumeric(strArr[0]))
// 				{
// 					id = Integer.parseInt(strArr[0]);
// 				}
// 				preset = strArr[1];
// 			}
// 			else if (StringUtils.isNumeric(entry))
// 			{
// 				id = Integer.parseInt(entry);
// 			}

// 			if (id == npc.getId())
// 			{
// 				return new HighlightColor(true, colorManager.getHighlightColor(preset, configColor),
// 						colorManager.getHighlightFillColor(preset, configFillColor));
// 			}
// 		}

// 		if (npc.getName() != null)
// 		{
// 			String name = npc.getName().toLowerCase();
// 			for (String entry : strList)
// 			{
// 				String nameStr = entry;
// 				String preset = "";
// 				if (entry.contains(":"))
// 				{
// 					String[] strArr = entry.split(":");
// 					nameStr = strArr[0];
// 					preset = strArr[1];
// 				}

// 				if (WildcardMatcher.matches(nameStr, name))
// 				{
// 					return new HighlightColor(true, colorManager.getHighlightColor(preset, configColor),
// 							colorManager.getHighlightFillColor(preset, configFillColor));
// 				}
// 			}
// 		}
// 		return new HighlightColor(false, configColor, configFillColor);
// 	}

// 	public boolean isInSpecificNameList(ArrayList<String> strList, NPC npc) {
// 		if (npc.getName() != null)
// 		{
// 			String name = npc.getName().toLowerCase();
// 			for (String entry : strList)
// 			{
// 				String nameStr = entry;
// 				if (entry.contains(":"))
// 				{
// 					String[] strArr = entry.split(":");
// 					nameStr = strArr[0];
// 				}

// 				if (WildcardMatcher.matches(nameStr, name))
// 				{
// 					return true;
// 				}
// 			}
// 		}
// 		return false;
// 	}

// 	public boolean isInSpecificIdList(ArrayList<String> strList, NPC npc) {
// 		int id = npc.getId();
// 		for (String entry : strList)
// 		{
// 			String idStr = entry;
// 			if (entry.contains(":"))
// 			{
// 				String[] strArr = entry.split(":");
// 				idStr = strArr[0];
// 			}

// 			if (StringUtils.isNumeric(idStr) && Integer.parseInt(idStr) == id)
// 			{
// 				return true;
// 			}
// 		}
// 		return false;
// 	}

// 	public boolean isInAnyNameList(NPC npc) {
// 		return isInSpecificNameList(nameAndIdContainer.tileNames, npc) || isInSpecificNameList(nameAndIdContainer.trueTileNames, npc)
// 				|| isInSpecificNameList(nameAndIdContainer.swTileNames, npc) || isInSpecificNameList(nameAndIdContainer.swTrueTileNames, npc)
// 				|| isInSpecificNameList(nameAndIdContainer.hullNames, npc) || isInSpecificNameList(nameAndIdContainer.areaNames, npc)
// 				|| isInSpecificNameList(nameAndIdContainer.outlineNames, npc) || isInSpecificNameList(nameAndIdContainer.clickboxNames, npc);
// 	}

// 	public boolean isInAnyIdList(NPC npc) {
// 		return isInSpecificIdList(nameAndIdContainer.tileIds, npc) || isInSpecificIdList(nameAndIdContainer.trueTileIds, npc)
// 				|| isInSpecificIdList(nameAndIdContainer.swTileIds, npc) || isInSpecificIdList(nameAndIdContainer.swTrueTileIds, npc)
// 				|| isInSpecificIdList(nameAndIdContainer.hullIds, npc) || isInSpecificIdList(nameAndIdContainer.areaIds, npc)
// 				|| isInSpecificIdList(nameAndIdContainer.outlineIds, npc) || isInSpecificIdList(nameAndIdContainer.clickboxIds, npc);
// 	}

// 	public boolean isInAnyList(NPC npc) {
// 		return isInAnyNameList(npc) || isInAnyIdList(npc);
// 	}

// 	private void removeAllTagStyles(String name) {
// 		config.setTileNames(configListToString(false, name, nameAndIdContainer.tileNames, 0));
// 		config.setTrueTileNames(configListToString(false, name, nameAndIdContainer.trueTileNames, 0));
// 		config.setSwTileNames(configListToString(false, name, nameAndIdContainer.swTileNames, 0));
// 		config.setSwTrueTileNames(configListToString(false, name, nameAndIdContainer.swTrueTileNames, 0));
// 		config.setHullNames(configListToString(false, name, nameAndIdContainer.hullNames, 0));
// 		config.setAreaNames(configListToString(false, name, nameAndIdContainer.areaNames, 0));
// 		config.setOutlineNames(configListToString(false, name, nameAndIdContainer.outlineNames, 0));
// 		config.setClickboxNames(configListToString(false, name, nameAndIdContainer.clickboxNames, 0));
// 	}
// }
