package com.HidePrayerDescriptions;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.FontManager;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Hide Prayer Descriptions"
)
public class HidePrayerDescriptionsPlugin extends Plugin
{
	// Values courtesy of https://github.com/Enriath/external-plugins/tree/better-skill-tooltips
	private static final int BAR_PADDING_X = 6;
	private static final int BAR_PADDING_Y = 5;
	private static final int BAR_HEIGHT = 15;
	private static final int LINE_HEIGHT = 12;
	private static final int WIDTH_PADDING = 4;
	private static final int HEIGHT_PADDING = 7;
	private static final FontMetrics FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(FontManager.getRunescapeFont());


	@Inject
	private Client client;

	@Inject
	private HidePrayerDescriptionsConfig config;

	@Provides
	HidePrayerDescriptionsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HidePrayerDescriptionsConfig.class);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired e)
	{
		if (e.getScriptId() == 2701)
		{
			Widget tooltip = client.getWidget(InterfaceID.Prayerbook.TOOLTIP);
			if (tooltip == null || tooltip.isHidden())
			{
				return;
			}
			try
			{
				ModifyTooltip(tooltip);
			} catch (Exception ex) { }
		}
	}

	private void ModifyTooltip(Widget tt)
	{
		int lines = getLinesInTooltip(tt.getHeight());
		int linesForDesc = lines - 3; // Description can span multiple lines without <br> tags, so we need to account for that
		Widget background = tt.getChild(0);
		Widget border = tt.getChild(1);
		Widget text = tt.getChild(2);
		String ttText = text.getText();

		if (ttText == null || ttText.isEmpty())
		{
			return;
		}

		String[] parts = ttText.split("<br>");
		int i = 0;
		String level = parts[i++];
		String name = parts[i++];
		List<String> desc = new ArrayList<>();
		String drain = "";

		// Description can span multiple lines, so we need to collect all parts until we hit the drain rate which is always the last line
		while (i < parts.length)
		{
			String part = parts[i++];
			if (!part.startsWith("(Drain"))
			{
				desc.add(part);
			}
			else
			{
				drain = part;
			}
		}

		String newText = "";
		int newLines = 0;

		if (!config.hidePrayerLevel())
		{
			newText += level;
			newLines++;
		}
		if (!config.hidePrayerName())
		{
			if (!newText.isEmpty())
				newText += "<br>";

			newText += name;
			newLines++;
		}
		if (!config.hidePrayerDescription())
		{
			if (!newText.isEmpty())
				newText += "<br>";

			for (int j = 0; j < desc.size(); j++)
			{
				newText += desc.get(j);

				if (j != desc.size() - 1)
				{
					newText += "<br>";
				}
			}

			newLines += linesForDesc;
		}
		if (!config.hidePrayerDrain())
		{
			if (!newText.isEmpty())
				newText += "<br>";

			newText += drain;
			newLines++;
		}

		// Set the text in the tooltip to the new string
		text.setText(newText);
		text.revalidate();

		//int lines = newText.isEmpty() ? 0 : newText.split("<br>").length;
		int width = newText.isEmpty() ? 0 : calculateTooltipWidth(newText, tt.getOriginalWidth());
		int height = newText.isEmpty() ? 0 : calculateTooltipTextHeight(newLines);

		// Resize Tooltip background
		tt.setOriginalWidth(width);
		tt.setOriginalHeight(height);
		tt.revalidate();
		background.revalidate();
		border.revalidate();

		// Adjust positioning of tooltip
		if (height > 0 && width > 0)
		{
			Widget prayerTile = client.getWidget(prayerMap.get(name));
			Widget prayerContainer = tt.getParent();

			// Fix positioning
			int x = prayerTile.getOriginalX();
			int y = prayerTile.getOriginalY();
			int h = prayerTile.getOriginalHeight();

			x += 5;
			int y2 = y + h + 5;

			x = Math.min(x, prayerContainer.getOriginalWidth() - width);
			if (y2 > prayerContainer.getHeight() - height)
			{
				y2 = y - height - 5;
			}
			tt.setOriginalX(x);
			tt.setOriginalY(y2);
			tt.revalidate();
		}
	}

	// Tooltip size functions courtesy of https://github.com/Enriath/external-plugins/tree/better-skill-tooltips
	// Width function adjusted for prayer tooltips
	private int calculateTooltipWidth(String text, int originalWidth)
	{
		final String[] lines = text.split("<br>");
		int maxWidth = 0;
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			int width = FONT_METRICS.stringWidth(line) + WIDTH_PADDING;
			maxWidth = Math.max(maxWidth, width);
		}
		return Math.min(maxWidth, originalWidth) + WIDTH_PADDING;
	}

	private int calculateTooltipTextHeight(int lines)
	{
		return lines * LINE_HEIGHT + HEIGHT_PADDING;
	}

	private int getLinesInTooltip(int height)
	{
		return (height - HEIGHT_PADDING) / LINE_HEIGHT;
	}

	private final Map<String, Integer> prayerMap = Map.ofEntries(
			Map.entry("Thick Skin", InterfaceID.Prayerbook.PRAYER1),
			Map.entry("Burst of Strength", InterfaceID.Prayerbook.PRAYER2),
			Map.entry("Clarity of Thought", InterfaceID.Prayerbook.PRAYER3),
			Map.entry("Rock Skin", InterfaceID.Prayerbook.PRAYER4),
			Map.entry("Superhuman Strength", InterfaceID.Prayerbook.PRAYER5),
			Map.entry("Improved Reflexes", InterfaceID.Prayerbook.PRAYER6),
			Map.entry("Rapid Restore", InterfaceID.Prayerbook.PRAYER7),
			Map.entry("Rapid Heal", InterfaceID.Prayerbook.PRAYER8),
			Map.entry("Protect Item", InterfaceID.Prayerbook.PRAYER9),
			Map.entry("Steel Skin", InterfaceID.Prayerbook.PRAYER10),
			Map.entry("Ultimate Strength", InterfaceID.Prayerbook.PRAYER11),
			Map.entry("Incredible Reflexes", InterfaceID.Prayerbook.PRAYER12),
			Map.entry("Protect from Magic", InterfaceID.Prayerbook.PRAYER13),
			Map.entry("Protect from Missiles", InterfaceID.Prayerbook.PRAYER14),
			Map.entry("Protect from Melee", InterfaceID.Prayerbook.PRAYER15),
			Map.entry("Retribution", InterfaceID.Prayerbook.PRAYER16),
			Map.entry("Redemption", InterfaceID.Prayerbook.PRAYER17),
			Map.entry("Smite", InterfaceID.Prayerbook.PRAYER18),
			Map.entry("Sharp Eye", InterfaceID.Prayerbook.PRAYER19),
			Map.entry("Hawk Eye", InterfaceID.Prayerbook.PRAYER20),
			Map.entry("Eagle Eye", InterfaceID.Prayerbook.PRAYER21),
			Map.entry("Deadeye", InterfaceID.Prayerbook.PRAYER21), // If player has read scroll from royal titans the ID is the same but the name changes
			Map.entry("Mystic Will", InterfaceID.Prayerbook.PRAYER22),
			Map.entry("Mystic Lore", InterfaceID.Prayerbook.PRAYER23),
			Map.entry("Mystic Might", InterfaceID.Prayerbook.PRAYER24),
			Map.entry("Mystic Vigour", InterfaceID.Prayerbook.PRAYER24), // If player has read scroll from royal titans the ID is the same but the name changes
			Map.entry("Rigour", InterfaceID.Prayerbook.PRAYER25),
			Map.entry("Chivalry", InterfaceID.Prayerbook.PRAYER26),
			Map.entry("Piety", InterfaceID.Prayerbook.PRAYER27),
			Map.entry("Augury", InterfaceID.Prayerbook.PRAYER28),
			Map.entry("Preserve", InterfaceID.Prayerbook.PRAYER29)
		);
}