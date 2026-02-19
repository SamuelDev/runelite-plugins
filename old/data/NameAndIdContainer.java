// package com.betternpchighlight.data;

// import java.util.ArrayList;
// import java.util.Arrays;

// import javax.inject.Inject;
// import javax.inject.Singleton;

// import com.betternpchighlight.BetterNpcHighlightConfig;

// import net.runelite.api.NPC;

// @Singleton
// public class NameAndIdContainer {
//   public String currentTask = "";
//   public boolean confirmedWarning = false;
//   public ArrayList<NPCInfo> npcList = new ArrayList<>();
//   public NPCInfo getNpcInfoByNpc(NPC npc) {
//     return npcList.stream().filter(npcInfo -> npcInfo.getNpc() == npc).findFirst().orElse(null);
//   }

//   public ArrayList<String> tileNames = new ArrayList<>();
//   public ArrayList<String> tileIds = new ArrayList<>();
//   public ArrayList<String> trueTileNames = new ArrayList<>();
//   public ArrayList<String> trueTileIds = new ArrayList<>();
//   public ArrayList<String> swTileNames = new ArrayList<>();
//   public ArrayList<String> swTileIds = new ArrayList<>();
//   public ArrayList<String> swTrueTileNames = new ArrayList<>();
//   public ArrayList<String> swTrueTileIds = new ArrayList<>();
//   public ArrayList<String> hullNames = new ArrayList<>();
//   public ArrayList<String> hullIds = new ArrayList<>();
//   public ArrayList<String> areaNames = new ArrayList<>();
//   public ArrayList<String> areaIds = new ArrayList<>();
//   public ArrayList<String> outlineNames = new ArrayList<>();
//   public ArrayList<String> outlineIds = new ArrayList<>();
//   public ArrayList<String> clickboxNames = new ArrayList<>();
//   public ArrayList<String> clickboxIds = new ArrayList<>();
//   public ArrayList<NpcSpawn> npcSpawns = new ArrayList<>();
//   public ArrayList<String> namesToDisplay = new ArrayList<>();
//   public ArrayList<String> ignoreDeadExclusionList = new ArrayList<>();
//   public ArrayList<String> ignoreDeadExclusionIDList = new ArrayList<>();
//   public ArrayList<String> hiddenNames = new ArrayList<>();
//   public ArrayList<String> hiddenIds = new ArrayList<>();
//   public ArrayList<String> beneathNPCs = new ArrayList<>();

//   public void clearAll() {
//     tileNames.clear();
//     tileIds.clear();
//     trueTileNames.clear();
//     trueTileIds.clear();
//     swTileNames.clear();
//     swTileIds.clear();
//     swTrueTileNames.clear();
//     swTrueTileIds.clear();
//     hullNames.clear();
//     hullIds.clear();
//     areaNames.clear();
//     areaIds.clear();
//     outlineNames.clear();
//     outlineIds.clear();
//     clickboxNames.clear();
//     clickboxIds.clear();
//     hiddenNames.clear();
//     hiddenIds.clear();
//     beneathNPCs.clear();
//     ignoreDeadExclusionList.clear();
//     ignoreDeadExclusionIDList.clear();
//     namesToDisplay.clear();
//     npcSpawns.clear();
//   }
// }
