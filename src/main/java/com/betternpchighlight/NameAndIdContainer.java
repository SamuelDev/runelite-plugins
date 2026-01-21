package com.betternpchighlight;

import java.awt.Color;
import java.util.ArrayList;

import javax.inject.Singleton;

@Singleton
public class NameAndIdContainer {
  public String currentTask = "";
  public boolean confirmedWarning = false;
  public ArrayList<NPCInfo> npcList = new ArrayList<>();

  public ArrayList<String> tileNames = new ArrayList<>();
  public ArrayList<String> tileIds = new ArrayList<>();
  public ArrayList<String> trueTileNames = new ArrayList<>();
  public ArrayList<String> trueTileIds = new ArrayList<>();
  public ArrayList<String> swTileNames = new ArrayList<>();
  public ArrayList<String> swTileIds = new ArrayList<>();
  public ArrayList<String> swTrueTileNames = new ArrayList<>();
  public ArrayList<String> swTrueTileIds = new ArrayList<>();
  public ArrayList<String> hullNames = new ArrayList<>();
  public ArrayList<String> hullIds = new ArrayList<>();
  public ArrayList<String> areaNames = new ArrayList<>();
  public ArrayList<String> areaIds = new ArrayList<>();
  public ArrayList<String> outlineNames = new ArrayList<>();
  public ArrayList<String> outlineIds = new ArrayList<>();
  public ArrayList<String> clickboxNames = new ArrayList<>();
  public ArrayList<String> clickboxIds = new ArrayList<>();
  public ArrayList<String> turboNames = new ArrayList<>();
  public ArrayList<String> turboIds = new ArrayList<>();
  public ArrayList<Color> turboColors = new ArrayList<>();
  public ArrayList<NpcSpawn> npcSpawns = new ArrayList<>();
  public ArrayList<String> namesToDisplay = new ArrayList<>();
  public ArrayList<String> ignoreDeadExclusionList = new ArrayList<>();
  public ArrayList<String> ignoreDeadExclusionIDList = new ArrayList<>();
  public ArrayList<String> hiddenNames = new ArrayList<>();
  public ArrayList<String> hiddenIds = new ArrayList<>();
  public ArrayList<String> beneathNPCs = new ArrayList<>();

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
    turboNames.clear();
    turboIds.clear();
    hiddenNames.clear();
    hiddenIds.clear();
    beneathNPCs.clear();
    ignoreDeadExclusionList.clear();
    ignoreDeadExclusionIDList.clear();
    namesToDisplay.clear();
    npcSpawns.clear();
  }
}
