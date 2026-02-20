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

import com.betternpchighlight.config.migrators.ConfigMigrator;
// import com.betternpchighlight.data.NPCInfo;
// import com.betternpchighlight.data.NameAndIdContainer;
// import com.betternpchighlight.data.NpcSpawn;
// import com.betternpchighlight.managers.ChatCommandManager;
// import com.betternpchighlight.managers.ConfigTransformManager;
// import com.betternpchighlight.managers.MenuManager;
// import com.betternpchighlight.managers.SlayerPluginManager;
// import com.betternpchighlight.overlays.BetterNpcHighlightOverlay;
// import com.betternpchighlight.overlays.BetterNpcMinimapOverlay;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.slayer.SlayerPlugin;
import net.runelite.client.plugins.slayer.SlayerPluginService;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;

@Slf4j
@PluginDescriptor(name = "Better NPC Highlight", description = "A more customizable NPC highlight", tags = { "npc", "highlight",
		"indicators", "respawn", "hide", "entity", "custom", "id", "name" })
@PluginDependency(SlayerPlugin.class)
public class BetterNpcHighlightPlugin extends Plugin {
	// @Inject
	// private Client client;

	// @Inject
	// private OverlayManager overlayManager;

	// @Inject
	// private BetterNpcHighlightOverlay overlay;

	// @Inject
	// private BetterNpcHighlightConfig config;

	// @Inject
	// private BetterNpcMinimapOverlay mapOverlay;

	// @Inject
	// private ConfigManager configManager;

	// @Inject
	// private Hooks hooks;

	// @Inject
	// private SlayerPluginService slayerPluginService;

	// @Inject
	// private ClientThread clientThread;

	// @Inject
	// private SlayerPluginManager slayerPluginIntegration;

	// @Inject
	// private MenuManager menuManager;

	// @Inject
	// private ConfigTransformManager configTransformManager;

	// @Inject
	// private NameAndIdContainer nameAndIdContainer;

	// @Inject
	// private ChatCommandManager chatCommandManager;

	// public Instant lastTickUpdate;

	// private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Provides
	BetterNpcHighlightConfig providesConfig(ConfigManager configManager) {
		ConfigMigrator.migrateOldConfigs(configManager);
		return configManager.getConfig(BetterNpcHighlightConfig.class);
	}

	// Every time the client receives an event to load an npc, check if the name or id should be highlighted, and for which types
	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();
		final int npcId = npc.getId();

		if (npcName == null)
		{
			return;
		}

		// if (npcTags.contains(npc.getIndex()))
		// {
		// 	memorizeNpc(npc);
		// 	highlightedNpcs.put(npc, highlightedNpc(npc));
		// 	spawnedNpcsThisTick.add(npc);
		// 	return;
		// }

		// if (highlightMatchesNPCName(npcName))
		// {
		// 	highlightedNpcs.put(npc, highlightedNpc(npc));
		// 	if (!client.isInInstancedRegion())
		// 	{
		// 		memorizeNpc(npc);
		// 		spawnedNpcsThisTick.add(npc);
		// 	}
		// }
	}
}
