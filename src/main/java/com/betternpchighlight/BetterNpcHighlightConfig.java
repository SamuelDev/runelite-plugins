/*
 * Copyright (c) 2022, Buchus <http://github.com/MoreBuchus>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.betternpchighlight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import com.betternpchighlight.config.AreaConfig;
import com.betternpchighlight.config.ClickboxConfig;
import com.betternpchighlight.config.EntityHiderConfig;
import com.betternpchighlight.config.GlobalConfig;
import com.betternpchighlight.config.HullConfig;
import com.betternpchighlight.config.MiscellaneousConfig;
import com.betternpchighlight.config.OutlineConfig;
import com.betternpchighlight.config.PresetsConfig;
import com.betternpchighlight.config.SlayerConfig;
import com.betternpchighlight.config.SouthwestTileConfig;
import com.betternpchighlight.config.SouthwestTrueTileConfig;
import com.betternpchighlight.config.TileConfig;
import com.betternpchighlight.config.TrueTileConfig;

@ConfigGroup(BetterNpcHighlightConfig.CONFIG_GROUP)
public interface BetterNpcHighlightConfig extends GlobalConfig, TileConfig, TrueTileConfig, SouthwestTileConfig, SouthwestTrueTileConfig,
		HullConfig, AreaConfig, OutlineConfig, ClickboxConfig, SlayerConfig, EntityHiderConfig, PresetsConfig, MiscellaneousConfig {
	String CONFIG_GROUP = "BetterNpcHighlight";

	@Deprecated
	@ConfigItem(position = -1, keyName = "tagStyleMode", name = "Tag Style", description = "Sets which highlight style list the NPC tagged is added too", hidden = true)
	default tagStyleMode tagStyleMode() {
		return null;
	}

	@ConfigSection(name = globalTagSectionGroupName, description = "Settings that apply across multiple highlight types", position = 0, closedByDefault = false)
	String globalTagSection = globalTagSectionGroupName;

	@ConfigSection(name = tileSectionName, description = "Tile Plugins", position = 1, closedByDefault = true)
	String tileSection = tileSectionName;

	@ConfigSection(name = trueTileSectionName, description = "True Tile Plugins", position = 2, closedByDefault = true)
	String trueTileSection = trueTileSectionName;

	@ConfigSection(name = swTileSectionName, description = "South West Tile Plugins", position = 3, closedByDefault = true)
	String swTileSection = swTileSectionName;

	@ConfigSection(name = swTrueTileSectionName, description = "South West True Tile Plugins", position = 4, closedByDefault = true)
	String swTrueTileSection = swTrueTileSectionName;

	@ConfigSection(name = hullSectionName, description = "Hull Plugins", position = 5, closedByDefault = true)
	String hullSection = hullSectionName;

	@ConfigSection(name = areaSectionName, description = "Area Plugins", position = 6, closedByDefault = true)
	String areaSection = areaSectionName;

	@ConfigSection(name = outlineSectionName, description = "Outline Plugins", position = 7, closedByDefault = true)
	String outlineSection = outlineSectionName;

	@ConfigSection(name = clickboxSectionName, description = "Clickbox Plugins", position = 8, closedByDefault = true)
	String clickboxSection = clickboxSectionName;

	@ConfigSection(name = slayerSectionName, description = "Slayer Plugins", position = 10, closedByDefault = true)
	String slayerSection = slayerSectionName;

	@ConfigSection(name = entityHiderSectionName, description = "Entity Hider Plugins", position = 11, closedByDefault = true)
	String entityHiderSection = entityHiderSectionName;

	@ConfigSection(name = presetsSectionName, description = "Presets Plugins", position = 12, closedByDefault = true)
	String presetsSection = presetsSectionName;

	@ConfigSection(name = miscellaneousSectionName, description = "Miscellaneous Settings", position = 13, closedByDefault = true)
	String miscellaneousSection = miscellaneousSectionName;

	//------------------------------------------------------------//
	// Enums
	//------------------------------------------------------------//
	@Getter
	@RequiredArgsConstructor
	enum lineType {
		REG("Regular"), DASH("Dashed"), CORNER("Corners"),;

		@Getter
		private final String group;

		@Override
		public String toString() {
			return group;
		}
	}

	@Getter
	@RequiredArgsConstructor
	enum highlightType {
		GLOBAL(globalTagSectionGroupName), TILE(tileSectionName), TRUE_TILE(trueTileSectionName), SOUTH_WEST_TILE(swTileSectionName),
		SOUTH_WEST_TRUE_TILE(swTrueTileSectionName), HULL(hullSectionName), AREA(areaSectionName), OUTLINE(outlineSectionName),
		CLICKBOX(clickboxSectionName),;
		// SLAYER(slayerSectionName), ENTITY_HIDER(entityHiderSectionName), PRESETS(presetsSectionName), MISCELLANEOUS(miscellaneousSectionName);

		@Getter
		private final String type;

		@Override
		public String toString() {
			return type;
		}
	}
}
