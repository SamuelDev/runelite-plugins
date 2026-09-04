package com.betternpchighlight.data;

import java.util.ArrayList;

import javax.inject.Singleton;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;

@Singleton
@Getter
@Setter
public class NameAndIdContainer {
	private String currentTask = "";
	private boolean confirmedWarning = false;
	private ArrayList<NPCInfo> npcList = new ArrayList<>();

	private ArrayList<String> tileNames = new ArrayList<>();
	private ArrayList<String> tileIds = new ArrayList<>();
	private ArrayList<String> trueTileNames = new ArrayList<>();
	private ArrayList<String> trueTileIds = new ArrayList<>();
	private ArrayList<String> swTileNames = new ArrayList<>();
	private ArrayList<String> swTileIds = new ArrayList<>();
	private ArrayList<String> swTrueTileNames = new ArrayList<>();
	private ArrayList<String> swTrueTileIds = new ArrayList<>();
	private ArrayList<String> hullNames = new ArrayList<>();
	private ArrayList<String> hullIds = new ArrayList<>();
	private ArrayList<String> areaNames = new ArrayList<>();
	private ArrayList<String> areaIds = new ArrayList<>();
	private ArrayList<String> outlineNames = new ArrayList<>();
	private ArrayList<String> outlineIds = new ArrayList<>();
	private ArrayList<String> clickboxNames = new ArrayList<>();
	private ArrayList<String> clickboxIds = new ArrayList<>();

	private ArrayList<String> namesToDisplay = new ArrayList<>();
	private ArrayList<String> ignoreDeadExclusionList = new ArrayList<>();
	private ArrayList<String> ignoreDeadExclusionIDList = new ArrayList<>();
	private ArrayList<String> hiddenNames = new ArrayList<>();
	private ArrayList<String> hiddenIds = new ArrayList<>();
	private ArrayList<String> beneathNPCs = new ArrayList<>();

	public NPCInfo getNpcInfoByNpc(NPC npc) {
		return npcList.stream().filter(npcInfo -> npcInfo.getNpc() == npc).findFirst().orElse(null);
	}

	public void clearAll() {
		tileNames.clear();
		tileIds.clear();
		trueTileNames.clear();
		trueTileIds.clear();
		swTileNames.clear();
		swTileIds.clear();
		swTrueTileNames.clear();
		swTrueTileIds.clear();
		hullNames.clear();
		hullIds.clear();
		areaNames.clear();
		areaIds.clear();
		outlineNames.clear();
		outlineIds.clear();
		clickboxNames.clear();
		clickboxIds.clear();
		hiddenNames.clear();
		hiddenIds.clear();
		beneathNPCs.clear();
		ignoreDeadExclusionList.clear();
		ignoreDeadExclusionIDList.clear();
		namesToDisplay.clear();
		npcList.clear();
	}
}