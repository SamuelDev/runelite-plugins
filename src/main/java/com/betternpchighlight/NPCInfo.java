package com.betternpchighlight;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import java.awt.Color;

@Getter
@Setter
public class NPCInfo
{
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

	public NPCInfo(NPC npc, BetterNpcHighlightPlugin plugin, SlayerPluginService slayerPluginService, BetterNpcHighlightConfig config, SlayerPluginIntegration slayerPluginIntegration, NameAndIdContainer nameAndIdContainer, ConfigTransformManager configTransformManager)
	{	
		Color globalTileColor = config.useGlobalTileColor() ? config.globalTileColor() : null;
		Color globalFillColor = config.useGlobalTileColor() ? config.globalFillColor() : null;

		this.npc = npc;
		this.tile = configTransformManager.checkSpecificList(nameAndIdContainer.tileNames, nameAndIdContainer.tileIds, npc,
				coalesceColor(globalTileColor, config.tileColor()), coalesceColor(globalFillColor, config.tileFillColor()));
		this.trueTile = configTransformManager.checkSpecificList(nameAndIdContainer.trueTileNames, nameAndIdContainer.trueTileIds, npc,
				coalesceColor(globalTileColor, config.trueTileColor()),
				coalesceColor(globalFillColor, config.trueTileFillColor()));
		this.swTile = configTransformManager.checkSpecificList(nameAndIdContainer.swTileNames, nameAndIdContainer.swTileIds, npc,
				coalesceColor(globalTileColor, config.swTileColor()), coalesceColor(globalFillColor, config.swTileFillColor()));
		this.swTrueTile = configTransformManager.checkSpecificList(nameAndIdContainer.swTrueTileNames, nameAndIdContainer.swTrueTileIds,
				npc, coalesceColor(globalTileColor, config.swTrueTileColor()),
				coalesceColor(globalFillColor, config.swTrueTileFillColor()));

		this.hull = configTransformManager.checkSpecificList(nameAndIdContainer.hullNames, nameAndIdContainer.hullIds, npc,
				config.hullColor(), config.hullFillColor());
		this.area = configTransformManager.checkSpecificList(nameAndIdContainer.areaNames, nameAndIdContainer.areaIds, npc,
				config.areaColor(), null);
		this.outline = configTransformManager.checkSpecificList(nameAndIdContainer.outlineNames, nameAndIdContainer.outlineIds, npc,
				config.outlineColor(), null);
		this.clickbox = configTransformManager.checkSpecificList(nameAndIdContainer.clickboxNames, nameAndIdContainer.clickboxIds, npc,
				config.clickboxColor(), config.clickboxFillColor());
		this.turbo = configTransformManager.checkSpecificList(nameAndIdContainer.turboNames, nameAndIdContainer.turboIds, npc, null, null);
		this.isTask = slayerPluginIntegration.checkSlayerPluginEnabled() && slayerPluginService != null
				&& slayerPluginService.getTargets().contains(npc);
		this.ignoreDead = configTransformManager.checkSpecificNameList(nameAndIdContainer.ignoreDeadExclusionList, npc)
				|| configTransformManager.checkSpecificIdList(nameAndIdContainer.ignoreDeadExclusionIDList, npc);
	}

	private Color coalesceColor(Color color, Color defaultColor)
	{
		return color != null ? color : defaultColor;
	}
}
