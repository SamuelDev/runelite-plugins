package com.betternpchighlight;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import java.awt.Color;

import javax.inject.Inject;

@Getter
@Setter
public class NPCInfo
{
	@Inject
	private SlayerPluginService slayerPluginService;

	@Inject
	private BetterNpcHighlightConfig config;

	@Inject
	private SlayerPluginIntegration slayerPluginIntegration;

	NPC npc;
	HighlightColor tile;
	HighlightColor trueTile;
	HighlightColor swTile;
	HighlightColor swTrueTile;
	HighlightColor hull;
	HighlightColor area;
	HighlightColor outline;
	HighlightColor clickbox;
	HighlightColor turbo;
	boolean isTask;
	boolean ignoreDead;

	public NPCInfo(NPC npc, BetterNpcHighlightPlugin plugin, SlayerPluginService slayerPluginService, BetterNpcHighlightConfig config, SlayerPluginIntegration slayerPluginIntegration, NameAndIdContainer nameAndIdContainer)
	{	
		Color globalTileColor = config.useGlobalTileColor() ? config.globalTileColor() : null;
		Color globalFillColor = config.useGlobalTileColor() ? config.globalFillColor() : null;

		this.npc = npc;
		this.tile = plugin.checkSpecificList(nameAndIdContainer.tileNames, nameAndIdContainer.tileIds, npc,
				coalesceColor(globalTileColor, config.tileColor()), coalesceColor(globalFillColor, config.tileFillColor()));
		this.trueTile = plugin.checkSpecificList(nameAndIdContainer.trueTileNames, nameAndIdContainer.trueTileIds, npc,
				coalesceColor(globalTileColor, config.trueTileColor()),
				coalesceColor(globalFillColor, config.trueTileFillColor()));
		this.swTile = plugin.checkSpecificList(nameAndIdContainer.swTileNames, nameAndIdContainer.swTileIds, npc,
				coalesceColor(globalTileColor, config.swTileColor()), coalesceColor(globalFillColor, config.swTileFillColor()));
		this.swTrueTile = plugin.checkSpecificList(nameAndIdContainer.swTrueTileNames, nameAndIdContainer.swTrueTileIds,
				npc, coalesceColor(globalTileColor, config.swTrueTileColor()),
				coalesceColor(globalFillColor, config.swTrueTileFillColor()));

		this.hull = plugin.checkSpecificList(nameAndIdContainer.hullNames, nameAndIdContainer.hullIds, npc,
				config.hullColor(), config.hullFillColor());
		this.area = plugin.checkSpecificList(nameAndIdContainer.areaNames, nameAndIdContainer.areaIds, npc,
				config.areaColor(), null);
		this.outline = plugin.checkSpecificList(nameAndIdContainer.outlineNames, nameAndIdContainer.outlineIds, npc,
				config.outlineColor(), null);
		this.clickbox = plugin.checkSpecificList(nameAndIdContainer.clickboxNames, nameAndIdContainer.clickboxIds, npc,
				config.clickboxColor(), config.clickboxFillColor());
		this.turbo = plugin.checkSpecificList(nameAndIdContainer.turboNames, nameAndIdContainer.turboIds, npc, null, null);
		this.isTask = slayerPluginIntegration.checkSlayerPluginEnabled() && slayerPluginService != null
				&& slayerPluginService.getTargets().contains(npc);
		this.ignoreDead = plugin.checkSpecificNameList(nameAndIdContainer.ignoreDeadExclusionList, npc)
				|| plugin.checkSpecificIdList(nameAndIdContainer.ignoreDeadExclusionIDList, npc);
	}

	private Color coalesceColor(Color color, Color defaultColor)
	{
		return color != null ? color : defaultColor;
	}
}
