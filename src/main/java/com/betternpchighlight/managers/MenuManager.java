package com.betternpchighlight.managers;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;
import com.google.common.collect.ImmutableSet;

import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

public class MenuManager {
  @Inject
  private Client client;

  @Inject
  private BetterNpcHighlightConfig config;

  @Inject
  private NameAndIdContainer nameAndIdContainer;

  @Inject
  private NpcUtil npcUtil;

  @Inject
  private ColorManager colorManager;

  @Inject
  private ConfigTransformManager configTransformManager;

  private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION,
      MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_NPC,
      MenuAction.ITEM_USE_ON_NPC);

  public void craftMenu(MenuEntryAdded event) {
    // add craft menu entry
    int type = event.getType();
    if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET)
    {
      type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
    }

    final MenuAction menuAction = MenuAction.of(type);

    // Colorize all name in all menu entries except examine and tag/untag
    if (NPC_MENU_ACTIONS.contains(menuAction))
    {
      NPC npc = client.getTopLevelWorldView().npcs().byIndex(event.getIdentifier());

      // Get name for menu item coloring
      Color color = null;
      if (npcUtil.isDying(npc))
      {
        color = config.deadNpcMenuColor();
      }
      else if (config.highlightMenuNames() && npc.getName() != null && configTransformManager.isInAnyList(npc))
      {
        for (NPCInfo npcInfo : nameAndIdContainer.npcList)
        {
          if (npcInfo.getNpc() == npc)
          {
            color = colorManager.getSpecificColor(npcInfo);
            break;
          }
        }

        if (color == null)
        {
          color = colorManager.getTagColor();
        }
      }

      if (color != null)
      {
        MenuEntry[] menuEntries = client.getMenuEntries();
        final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
        final String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
        menuEntry.setTarget(target);
        client.setMenuEntries(menuEntries);
      }
    }
    // Colorize examine and tag/untag menu options
    else if (menuAction == MenuAction.EXAMINE_NPC)
    {
      final NPC npc = client.getTopLevelWorldView().npcs().byIndex(event.getIdentifier());

      if (npc != null)
      {
        String option;
        // if there is an NPC name AND
        // ((tag style none is selected, and so is another option) OR
        // (tag style none is not selected, and another is))
        if (npc.getName() != null
            && ((config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.NONE) && config.tagStyleModeSet().size() > 1)
                || (!config.tagStyleModeSet().contains(BetterNpcHighlightConfig.tagStyleMode.NONE) && !config.tagStyleModeSet().isEmpty())))
        {
          // If the NPC is tagged by name in any way, show "Untag-NPC", else show "Tag-NPC"
          if (configTransformManager.isInAnyList(npc))
          {
            option = "Untag-NPC";
          }
          else
          {
            option = "Tag-NPC";
          }

          // If the menu option is untag, and either highlight menu names is on or dead NPC color is set, colorize the existing examine entry
          if (option.equals("Untag-NPC") && (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null)))
          {
            MenuEntry[] menuEntries = client.getMenuEntries();
            final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];

            Color displayColor = getNpcDisplayColor(npc);
            if (npc.isDead() && config.deadNpcMenuColor() != null)
            {
              displayColor = config.deadNpcMenuColor();
            }

            String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), displayColor);
            menuEntry.setTarget(target);
            client.setMenuEntries(menuEntries);
          }

          // If shift is held, handle the tag/untag menu option
          if (client.isKeyPressed(KeyCode.KC_SHIFT))
          {
            String tagAllEntry;
            // If highlight menu names is enabled or the npc is dead and there is a dead color set
            if (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null))
            {
              Color displayColor = getNpcDisplayColor(npc);
              if (npc.isDead() && config.deadNpcMenuColor() != null)
              {
                displayColor = config.deadNpcMenuColor();
              }
              String colorCode = Integer.toHexString(displayColor.getRGB());
              tagAllEntry = "<col=" + colorCode.substring(2) + ">" + Text.removeTags(event.getTarget());
            }
            else
            {
              tagAllEntry = event.getTarget();
            }

            int idx = -1;
            MenuEntry parent = client.createMenuEntry(idx).setOption(option).setTarget(tagAllEntry).setIdentifier(event.getIdentifier())
                .setParam0(event.getActionParam0()).setParam1(event.getActionParam1()).setType(MenuAction.RUNELITE).onClick(this::tagNPC);

            if (parent != null)
            {
              this.customColorTag(idx, npc, parent);
            }
          }
        }
      }
    }
  }

  public void customColorTag(int idx, NPC npc, MenuEntry parent) {

    // add X amount of preset colors based off of config
    if (config.presetColorAmount() != BetterNpcHighlightConfig.presetColorAmount.ZERO)
    {
      List<Color> colors = loadPresetColors();
      Menu submenu = parent.createSubMenu();

      if (colors.size() > 0)
      {
        int index = 1;
        for (final Color c : colors)
        {
          if (c != null)
          {
            int preset = index;
            submenu.createMenuEntry(0).setOption(ColorUtil.prependColorTag("Preset color " + index, c)).setType(MenuAction.RUNELITE)
                .onClick(e -> {
                  if (npc.getName() != null)
                  {
                    configTransformManager.updateListConfig(true, npc.getName().toLowerCase(), preset);
                  }
                });
            index++;
          }
        }
      }

      for (NPCInfo n : nameAndIdContainer.npcList)
      {
        if (n.getNpc() == npc)
        {
          submenu.createMenuEntry(0).setOption("Reset color").setType(MenuAction.RUNELITE).onClick(e -> {
            if (npc.getName() != null)
            {
              configTransformManager.updateListConfig(true, npc.getName().toLowerCase(), 0);
            }
          });
          break;
        }
      }
    }
  }

  private void tagNPC(MenuEntry event) {
    if (event.getType() == MenuAction.RUNELITE)
    {
      if (event.getOption().equals("Tag-NPC") || event.getOption().equals("Untag-NPC"))
      {
        final int id = event.getIdentifier();
        final NPC npc = client.getTopLevelWorldView().npcs().byIndex(id);
        boolean tag = event.getOption().equals("Tag-NPC");
        if (npc.getName() != null)
        {
          configTransformManager.updateListConfig(tag, npc.getName().toLowerCase(), 0);
        }
      }
    }
  }

  /**
   * Gets the color to display for an NPC, considering turbo mode, specific highlight color, or fallback to tag color.
   */
  private Color getNpcDisplayColor(NPC npc) {
    NPCInfo npcInfo = nameAndIdContainer.getNpcInfoByNpc(npc);
    if (npcInfo != null)
    {
      Color highlightColor = colorManager.getSpecificColor(npcInfo);
      if (highlightColor != null)
      {
        return highlightColor;
      }
    }

    return colorManager.getTagColor();
  }

  /**
   * Loads preset colors based on config amount setting.
   */
  private List<Color> loadPresetColors() {
    List<Color> colors = new ArrayList<>();
    BetterNpcHighlightConfig.presetColorAmount amount = config.presetColorAmount();

    if (amount == BetterNpcHighlightConfig.presetColorAmount.ONE)
    {
      colors.add(config.presetColor1());
    }
    else if (amount == BetterNpcHighlightConfig.presetColorAmount.TWO)
    {
      colors.add(config.presetColor1());
      colors.add(config.presetColor2());
    }
    else if (amount == BetterNpcHighlightConfig.presetColorAmount.THREE)
    {
      colors.add(config.presetColor1());
      colors.add(config.presetColor2());
      colors.add(config.presetColor3());
    }
    else if (amount == BetterNpcHighlightConfig.presetColorAmount.FOUR)
    {
      colors.add(config.presetColor1());
      colors.add(config.presetColor2());
      colors.add(config.presetColor3());
      colors.add(config.presetColor4());
    }
    else if (amount == BetterNpcHighlightConfig.presetColorAmount.FIVE)
    {
      colors.add(config.presetColor1());
      colors.add(config.presetColor2());
      colors.add(config.presetColor3());
      colors.add(config.presetColor4());
      colors.add(config.presetColor5());
    }

    return colors;
  }
}
