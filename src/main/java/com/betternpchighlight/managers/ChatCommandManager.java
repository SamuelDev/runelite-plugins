package com.betternpchighlight.managers;

import java.awt.event.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.data.NameAndIdContainer;
import com.betternpchighlight.service.ConfigReaderService;

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

	@Inject
	private ConfigReaderService configReaderService;

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
			if (configReaderService.isNumeric(npcToHide))
			{
				config.setEntityHiderIds(configTransformManager.configListToString(hide, npcToHide, nameAndIdContainer.getHiddenIds(), 0));
			}
			else
			{
				config.setEntityHiderNames(configTransformManager.configListToString(hide, npcToHide, nameAndIdContainer.getHiddenNames(), 0));
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
				if (configReaderService.isNumeric(strArr[1]))
					preset = Integer.parseInt(strArr[1]);
			}
			boolean tag = text.startsWith(TAG_COMMAND);

			if (!npcToTag.isEmpty())
			{
				if (validateCommand(text, "t ") || validateCommand(text, "tile "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getTileIds(), preset));
					}
					else
					{
						config.setTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getTileNames(), preset));
					}
				}
				else if (validateCommand(text, "tt ") || validateCommand(text, "truetile "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setTrueTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getTrueTileIds(), preset));
					}
					else
					{
						config.setTrueTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getTrueTileNames(), preset));
					}
				}
				else if (validateCommand(text, "sw ") || validateCommand(text, "swt ") || validateCommand(text, "southwesttile ")
						|| validateCommand(text, "southwest ") || validateCommand(text, "swtile ") || validateCommand(text, "southwestt "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setSwTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getSwTileIds(), preset));
					}
					else
					{
						config.setSwTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getSwTileNames(), preset));
					}
				}
				else if (validateCommand(text, "swtt ") || validateCommand(text, "swtruetile ") || validateCommand(text, "southwesttruetile ")
						|| validateCommand(text, "southwesttt "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setSwTrueTileIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getSwTrueTileIds(), preset));
					}
					else
					{
						config.setSwTrueTileNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getSwTrueTileNames(), preset));
					}
				}
				else if (validateCommand(text, "h ") || validateCommand(text, "hull "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setHullIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getHullIds(), preset));
					}
					else
					{
						config.setHullNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getHullNames(), preset));
					}
				}
				else if (validateCommand(text, "a ") || validateCommand(text, "area "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setAreaIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getAreaIds(), preset));
					}
					else
					{
						config.setAreaNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getAreaNames(), preset));
					}
				}
				else if (validateCommand(text, "o ") || validateCommand(text, "outline "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setOutlineIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getOutlineIds(), preset));
					}
					else
					{
						config.setOutlineNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getOutlineNames(), preset));
					}
				}
				else if (validateCommand(text, "c ") || validateCommand(text, "clickbox ") || validateCommand(text, "box "))
				{
					if (configReaderService.isNumeric(npcToTag))
					{
						config.setClickboxIds(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getClickboxIds(), preset));
					}
					else
					{
						config.setClickboxNames(configTransformManager.configListToString(tag, npcToTag, nameAndIdContainer.getClickboxNames(), preset));
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