package com.betternpchighlight.data;

import lombok.Getter;
import net.runelite.api.NPC;

@Getter
public class NPCInfo {
	private final NPC npc;
	private final HighlightColor tile;
	private final HighlightColor trueTile;
	private final HighlightColor swTile;
	private final HighlightColor swTrueTile;
	private final HighlightColor hull;
	private final HighlightColor area;
	private final HighlightColor outline;
	private final HighlightColor clickbox;
	private final boolean isTask;
	private final boolean ignoreDead;

	public NPCInfo(NPC npc, HighlightColor tile, HighlightColor trueTile, HighlightColor swTile, HighlightColor swTrueTile,
			HighlightColor hull, HighlightColor area, HighlightColor outline, HighlightColor clickbox, boolean isTask, boolean ignoreDead) {
		this.npc = npc;
		this.tile = tile;
		this.trueTile = trueTile;
		this.swTile = swTile;
		this.swTrueTile = swTrueTile;
		this.hull = hull;
		this.area = area;
		this.outline = outline;
		this.clickbox = clickbox;
		this.isTask = isTask;
		this.ignoreDead = ignoreDead;
	}
}