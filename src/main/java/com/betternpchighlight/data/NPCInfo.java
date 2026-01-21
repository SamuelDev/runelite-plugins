package com.betternpchighlight.data;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import java.awt.Color;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.BetterNpcHighlightPlugin;
import com.betternpchighlight.managers.ConfigTransformManager;
import com.betternpchighlight.managers.SlayerPluginManager;

@Getter
@Setter
public class NPCInfo {
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

	public NPCInfo(NPC npc, BetterNpcHighlightPlugin plugin, SlayerPluginService slayerPluginService, BetterNpcHighlightConfig config,
			SlayerPluginManager slayerPluginIntegration, NameAndIdContainer nameAndIdContainer, ConfigTransformManager configTransformManager) {
		Color globalTileColor = config.useGlobalTileColor() ? config.globalTileColor() : null;
		Color globalFillColor = config.useGlobalTileColor() ? config.globalFillColor() : null;

		this.npc = npc;
		this.tile = configTransformManager.isInNameOrIdList(nameAndIdContainer.tileNames, nameAndIdContainer.tileIds, npc,
				coalesceColor(globalTileColor, config.tileColor()), coalesceColor(globalFillColor, config.tileFillColor()));
		this.trueTile = configTransformManager.isInNameOrIdList(nameAndIdContainer.trueTileNames, nameAndIdContainer.trueTileIds, npc,
				coalesceColor(globalTileColor, config.trueTileColor()), coalesceColor(globalFillColor, config.trueTileFillColor()));
		this.swTile = configTransformManager.isInNameOrIdList(nameAndIdContainer.swTileNames, nameAndIdContainer.swTileIds, npc,
				coalesceColor(globalTileColor, config.swTileColor()), coalesceColor(globalFillColor, config.swTileFillColor()));
		this.swTrueTile = configTransformManager.isInNameOrIdList(nameAndIdContainer.swTrueTileNames, nameAndIdContainer.swTrueTileIds, npc,
				coalesceColor(globalTileColor, config.swTrueTileColor()), coalesceColor(globalFillColor, config.swTrueTileFillColor()));

		this.hull = configTransformManager.isInNameOrIdList(nameAndIdContainer.hullNames, nameAndIdContainer.hullIds, npc, config.hullColor(),
				config.hullFillColor());
		this.area = configTransformManager.isInNameOrIdList(nameAndIdContainer.areaNames, nameAndIdContainer.areaIds, npc, config.areaColor(),
				null);
		this.outline = configTransformManager.isInNameOrIdList(nameAndIdContainer.outlineNames, nameAndIdContainer.outlineIds, npc,
				config.outlineColor(), null);
		this.clickbox = configTransformManager.isInNameOrIdList(nameAndIdContainer.clickboxNames, nameAndIdContainer.clickboxIds, npc,
				config.clickboxColor(), config.clickboxFillColor());
		this.turbo = configTransformManager.isInNameOrIdList(nameAndIdContainer.turboNames, nameAndIdContainer.turboIds, npc, null, null);
		this.isTask = slayerPluginIntegration.checkSlayerPluginEnabled() && slayerPluginService != null
				&& slayerPluginService.getTargets().contains(npc);
		this.ignoreDead = configTransformManager.isInSpecificNameList(nameAndIdContainer.ignoreDeadExclusionList, npc)
				|| configTransformManager.isInSpecificIdList(nameAndIdContainer.ignoreDeadExclusionIDList, npc);
	}

	private Color coalesceColor(Color color, Color defaultColor) {
		return color != null ? color : defaultColor;
	}
}
