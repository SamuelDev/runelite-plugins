package com.betternpchighlight.managers;

import java.awt.Color;
import java.util.Random;

import javax.inject.Inject;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;

import net.runelite.api.Client;
import net.runelite.client.util.WildcardMatcher;

public class ColorManager {
  @Inject
  private Client client;

  @Inject
  private BetterNpcHighlightConfig config;

  @Inject
  private NameAndIdContainer nameAndIdContainer;

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
}
