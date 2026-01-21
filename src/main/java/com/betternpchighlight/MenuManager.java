package com.betternpchighlight;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

public class MenuManager {
  @Inject
  private BetterNpcHighlightConfig config;

  private ArrayList<String> tileNames;
  private ArrayList<String> trueTileNames;
  private ArrayList<String> swTileNames;
  private ArrayList<String> swTrueTileNames;
  private ArrayList<String> hullNames;
  private ArrayList<String> areaNames;
  private ArrayList<String> outlineNames;
  private ArrayList<String> clickboxNames;
  private ArrayList<String> turboNames;
  private ArrayList<NPCInfo> npcList;

  /**
   * Injects the highlight lists into this menu manager.
   * This must be called before using customColorTag or updateListConfig.
   */
  public void setHighlightLists(
      ArrayList<String> tileNames,
      ArrayList<String> trueTileNames,
      ArrayList<String> swTileNames,
      ArrayList<String> swTrueTileNames,
      ArrayList<String> hullNames,
      ArrayList<String> areaNames,
      ArrayList<String> outlineNames,
      ArrayList<String> clickboxNames,
      ArrayList<String> turboNames,
      ArrayList<NPCInfo> npcList) {
    this.tileNames = tileNames;
    this.trueTileNames = trueTileNames;
    this.swTileNames = swTileNames;
    this.swTrueTileNames = swTrueTileNames;
    this.hullNames = hullNames;
    this.areaNames = areaNames;
    this.outlineNames = outlineNames;
    this.clickboxNames = clickboxNames;
    this.turboNames = turboNames;
    this.npcList = npcList;
  }

  public void customColorTag(int idx, NPC npc, MenuEntry parent) {
    // add X amount of preset colors based off of config
    if (config.presetColorAmount() != BetterNpcHighlightConfig.presetColorAmount.ZERO) {
      List<Color> colors = new ArrayList<>();
      Menu submenu = parent.createSubMenu();

      if (config.presetColorAmount() == BetterNpcHighlightConfig.presetColorAmount.ONE) {
        colors.add(config.presetColor1());
      } else if (config.presetColorAmount() == BetterNpcHighlightConfig.presetColorAmount.TWO) {
        colors.add(config.presetColor1());
        colors.add(config.presetColor2());
      } else if (config.presetColorAmount() == BetterNpcHighlightConfig.presetColorAmount.THREE) {
        colors.add(config.presetColor1());
        colors.add(config.presetColor2());
        colors.add(config.presetColor3());
      } else if (config.presetColorAmount() == BetterNpcHighlightConfig.presetColorAmount.FOUR) {
        colors.add(config.presetColor1());
        colors.add(config.presetColor2());
        colors.add(config.presetColor3());
        colors.add(config.presetColor4());
      } else if (config.presetColorAmount() == BetterNpcHighlightConfig.presetColorAmount.FIVE) {
        colors.add(config.presetColor1());
        colors.add(config.presetColor2());
        colors.add(config.presetColor3());
        colors.add(config.presetColor4());
        colors.add(config.presetColor5());
      }

      if (colors.size() > 0) {
        int index = 1;
        for (final Color c : colors) {
          if (c != null) {
            int preset = index;
            submenu.createMenuEntry(0)
                .setOption(ColorUtil.prependColorTag("Preset color " + index, c))
                .setType(MenuAction.RUNELITE)
                .onClick(e -> {
                  if (npc.getName() != null) {
                    updateListConfig(true, npc.getName().toLowerCase(), preset);
                  }
                });
            index++;
          }
        }
      }

      for (NPCInfo n : npcList) {
        if (n.getNpc() == npc) {
          submenu.createMenuEntry(0)
              .setOption("Reset color")
              .setType(MenuAction.RUNELITE)
              .onClick(e -> {
                if (npc.getName() != null) {
                  updateListConfig(true, npc.getName().toLowerCase(), 0);
                }
              });
          break;
        }
      }
    }
  }

  public void updateListConfig(boolean add, String name, int preset) {
    if (!add) {
      removeAllTagStyles(name);
    } else {
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TILE)) {
        config.setTileNames(configListToString(add, name, tileNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TRUE_TILE)) {
        config.setTrueTileNames(configListToString(add, name, trueTileNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TILE)) {
        config.setSwTileNames(configListToString(add, name, swTileNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.SW_TRUE_TILE)) {
        config.setSwTrueTileNames(configListToString(add, name, swTrueTileNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.HULL)) {
        config.setHullNames(configListToString(add, name, hullNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.AREA)) {
        config.setAreaNames(configListToString(add, name, areaNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.OUTLINE)) {
        config.setOutlineNames(configListToString(add, name, outlineNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.CLICKBOX)) {
        config.setClickboxNames(configListToString(add, name, clickboxNames, preset));
      }
      if (config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.TURBO)) {
        config.setTurboNames(configListToString(add, name, turboNames, 0));
      }
    }
  }

  public String configListToString(boolean tagOrHide, String name, ArrayList<String> strList, int preset)
	{
		if (tagOrHide)
		{
			boolean foundName = false;
			String newName = preset > 0 ? name + ":" + preset : name;
			for (String str : strList)
			{
				if (str.startsWith(name + ":") || str.equalsIgnoreCase(name))
				{
					strList.set(strList.indexOf(str), newName);
					foundName = true;
				}
			}

			if (!foundName)
			{
				strList.add(newName);
			}
		}
		else
		{
			strList.removeIf(str -> str.toLowerCase().startsWith(name + ":") || str.equalsIgnoreCase(name));
		}
		return Text.toCSV(strList);
	}

  private void removeAllTagStyles(String name) {
    config.setTileNames(configListToString(false, name, tileNames, 0));
    config.setTrueTileNames(configListToString(false, name, trueTileNames, 0));
    config.setSwTileNames(configListToString(false, name, swTileNames, 0));
    config.setSwTrueTileNames(configListToString(false, name, swTrueTileNames, 0));
    config.setHullNames(configListToString(false, name, hullNames, 0));
    config.setAreaNames(configListToString(false, name, areaNames, 0));
    config.setOutlineNames(configListToString(false, name, outlineNames, 0));
    config.setClickboxNames(configListToString(false, name, clickboxNames, 0));
    config.setTurboNames(configListToString(false, name, turboNames, 0));
  }
}
