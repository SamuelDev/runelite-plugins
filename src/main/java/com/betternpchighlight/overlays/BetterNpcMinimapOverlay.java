package com.betternpchighlight.overlays;

import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;
import javax.inject.Inject;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;
import com.betternpchighlight.managers.ColorManager;

import java.awt.*;
import net.runelite.client.util.Text;

public class BetterNpcMinimapOverlay extends Overlay
{
	@Inject
	private BetterNpcHighlightConfig config;

	@Inject
	private NameAndIdContainer nameAndIdContainer;

	@Inject
	private ColorManager colorManager;

	@Inject
	private BetterNpcMinimapOverlay() {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		for (NPCInfo npcInfo : nameAndIdContainer.npcList)
		{
			NPC npc = npcInfo.getNpc();
			if (npc.getName() != null && config.npcMinimapMode() != BetterNpcHighlightConfig.npcMinimapMode.OFF)
			{
				Color color = colorManager.getSpecificColor(npcInfo);

				NPCComposition npcComposition = npc.getTransformedComposition();
				if (color != null && npcComposition != null && npcComposition.isInteractible())
				{
					Point minimapLocation = npc.getMinimapLocation();
					if (minimapLocation != null)
					{
						if (config.npcMinimapMode() == BetterNpcHighlightConfig.npcMinimapMode.DOT || config.npcMinimapMode() == BetterNpcHighlightConfig.npcMinimapMode.BOTH)
						{
							OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
						}

						if (config.npcMinimapMode() == BetterNpcHighlightConfig.npcMinimapMode.NAME || config.npcMinimapMode() == BetterNpcHighlightConfig.npcMinimapMode.BOTH)
						{
							OverlayUtil.renderTextLocation(graphics, minimapLocation, Text.removeTags(npc.getName()), color);
						}
					}
				}
			}
		}
		return null;
	}
}
