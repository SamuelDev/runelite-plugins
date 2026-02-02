package com.betternpchighlight.managers;

import java.awt.event.KeyEvent;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.data.NameAndIdContainer;
import com.google.inject.Singleton;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.VarClientStr;
import net.runelite.api.vars.InputType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;

@Singleton
public class ChatCommandManager implements KeyListener {
  @Inject
  private Client client;

  @Inject
  private BetterNpcHighlightConfig config;

  @Inject
  private KeyManager keyManager;

  @Inject
  private ChatMessageManager chatMessageManager;

  @Inject
  private ConfigTransformManager configTransformManager;

  @Inject
  private NameAndIdContainer nameAndIdContainer;

  private static final String HIDE_COMMAND = "!hide";
  private static final String UNHIDE_COMMAND = "!unhide";
  private static final String TAG_COMMAND = "!tag";
  private static final String UNTAG_COMMAND = "!untag";

  public void registerKeyListener() {
    keyManager.registerKeyListener(this);
  }

  public void unregisterKeyListener() {
    keyManager.unregisterKeyListener(this);
  }

  public void keyPressed(KeyEvent e) {
    // Enter is pressed
    if (e.getKeyCode() == 10)
    {
      int inputType = client.getVarcIntValue(VarClientInt.INPUT_TYPE);
      if (inputType == InputType.PRIVATE_MESSAGE.getType() || inputType == InputType.NONE.getType())
      {
        int var;
        if (inputType == InputType.PRIVATE_MESSAGE.getType())
        {
          var = VarClientStr.INPUT_TEXT;
        }
        else
        {
          var = VarClientStr.CHATBOX_TYPED_TEXT;
        }

        if (client.getVarcStrValue(var) != null && !client.getVarcStrValue(var).isEmpty())
        {
          String text = client.getVarcStrValue(var).toLowerCase();
          if (config.entityHiderCommands() && (text.startsWith(HIDE_COMMAND) || text.startsWith(UNHIDE_COMMAND)))
          {
            hideNPCCommand(text, var);
          }
          else if (config.tagCommands() && (text.startsWith(TAG_COMMAND) || text.startsWith(UNTAG_COMMAND)))
          {
            tagNPCCommand(text, var);
          }
        }
      }
    }
  }

  public void hideNPCCommand(String text, int var) {
    String npcToHide = text.replace(text.startsWith(HIDE_COMMAND) ? HIDE_COMMAND : UNHIDE_COMMAND, "").trim();
    boolean hide = text.startsWith(HIDE_COMMAND);

    if (!npcToHide.isEmpty())
    {
      if (StringUtils.isNumeric(npcToHide))
      {
        config.setEntityHiderIds(configTransformManager.configListToString(hide, npcToHide, nameAndIdContainer.hiddenIds, 0));
      }
      else
      {
        config.setEntityHiderNames(configTransformManager.configListToString(hide, npcToHide, nameAndIdContainer.hiddenNames, 0));
      }
    }
    else
    {
      printMessage("Please enter a valid NPC name or ID!");
    }

    // Set typed text to nothing
    client.setVarcStrValue(var, "");
  }

  public void tagNPCCommand(String text, int var) {
    if (text.trim().equals(TAG_COMMAND) || text.trim().equals(UNTAG_COMMAND))
    {
      printMessage("Please enter a tag abbreviation followed by a valid NPC name or ID!");
    }
    else if (text.contains(TAG_COMMAND + " ") || text.contains(UNTAG_COMMAND + " "))
    {
      printMessage("Please enter a valid tag abbreviation!");
    }
    else if (!text.trim().contains(" "))
    {
      printMessage("Please enter a valid NPC name or ID!");
    }
    else
    {
      String npcToTag = text.substring(text.indexOf(" ") + 1).toLowerCase().trim();
      int preset = 0;
      if (npcToTag.contains(":"))
      {
        String[] strArr = npcToTag.split(":");
        npcToTag = strArr[0];
        if (StringUtils.isNumeric(strArr[1]))
          preset = Integer.parseInt(strArr[1]);
      }
      boolean tag = text.startsWith(TAG_COMMAND);

      if (!npcToTag.isEmpty())
      {
        if (validateCommand(text, "t ") || validateCommand(text, "tile "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.tileIds, preset));
          }
          else
          {
            config.setTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.tileNames, preset));
          }
        }
        else if (validateCommand(text, "tt ") || validateCommand(text, "truetile "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setTrueTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.trueTileIds, preset));
          }
          else
          {
            config.setTrueTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.trueTileNames, preset));
          }
        }
        else if (validateCommand(text, "sw ") || validateCommand(text, "swt ") || validateCommand(text, "southwesttile ")
            || validateCommand(text, "southwest ") || validateCommand(text, "swtile ") || validateCommand(text, "southwestt "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setSwTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.swTileIds, preset));
          }
          else
          {
            config.setSwTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.swTileNames, preset));
          }
        }
        else if (validateCommand(text, "swtt ") || validateCommand(text, "swtruetile ") || validateCommand(text, "southwesttruetile ")
            || validateCommand(text, "southwesttt "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setSwTrueTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.swTrueTileIds, preset));
          }
          else
          {
            config.setSwTrueTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.swTrueTileNames, preset));
          }
        }
        else if (validateCommand(text, "h ") || validateCommand(text, "hull "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setHullIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.hullIds, preset));
          }
          else
          {
            config.setHullNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.hullNames, preset));
          }
        }
        else if (validateCommand(text, "a ") || validateCommand(text, "area "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setAreaIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.areaIds, preset));
          }
          else
          {
            config.setAreaNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.areaNames, preset));
          }
        }
        else if (validateCommand(text, "o ") || validateCommand(text, "outline "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setOutlineIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.outlineIds, preset));
          }
          else
          {
            config.setOutlineNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.outlineNames, preset));
          }
        }
        else if (validateCommand(text, "c ") || validateCommand(text, "clickbox ") || validateCommand(text, "box "))
        {
          if (StringUtils.isNumeric(npcToTag))
          {
            config.setClickboxIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.clickboxIds, preset));
          }
          else
          {
            config.setClickboxNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.clickboxNames, preset));
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
    final ChatMessageBuilder message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(msg);

    chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(message.build()).build());
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }
}
