package com.betternpchighlight.managers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.betternpchighlight.data.MemorizedNpc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.gameval.SpotanimID;

/**
 * Tracks highlighted NPCs across their death/respawn cycles and exposes the
 * NPCs that currently have an active respawn timer to draw.
 *
 * <p>The logic mirrors the RuneLite NPC Indicators plugin: an NPC is memorized
 * when it spawns, its death is confirmed on the following game tick (by
 * checking it despawned while still in view range), and its respawn time and
 * spawn tile are learned from the next time it spawns.</p>
 */
@Singleton
public class RespawnManager {
	private static final int MAX_ACTOR_VIEW_RANGE = 15;
	private static final int MAX_RESPAWN_TIME_TICKS = 500;

	@Inject
	private Client client;

	/**
	 * NPCs currently being tracked, keyed by NPC index.
	 */
	@Getter
	private final Map<Integer, MemorizedNpc> memorizedNpcs = new HashMap<>();

	/**
	 * Dead NPCs that currently have an active respawn timer to display.
	 */
	@Getter
	private final Map<Integer, MemorizedNpc> deadNpcsToDisplay = new HashMap<>();

	/**
	 * The time the last game tick was processed, used to smooth the seconds
	 * countdown between ticks.
	 */
	@Getter
	private Instant lastTickUpdate = Instant.now();

	private final List<NPC> spawnedNpcsThisTick = new ArrayList<>();
	private final List<DespawnedNpc> despawnedNpcsThisTick = new ArrayList<>();
	private final Set<WorldPoint> teleportGraphicsObjectSpawnedThisTick = new HashSet<>();
	private WorldPoint lastPlayerLocation;
	private boolean skipNextSpawnCheck = false;

	@AllArgsConstructor
	private static class DespawnedNpc {
		private final WorldPoint coord;
		private final int index;
	}

	/**
	 * Fully resets all state. Called on plugin startup and shutdown.
	 */
	public void reset() {
		memorizedNpcs.clear();
		deadNpcsToDisplay.clear();
		spawnedNpcsThisTick.clear();
		despawnedNpcsThisTick.clear();
		teleportGraphicsObjectSpawnedThisTick.clear();
		lastPlayerLocation = null;
		skipNextSpawnCheck = false;
	}

	/**
	 * Called when the game state changes to the login screen or a world hop.
	 */
	public void onGameStateChanged() {
		deadNpcsToDisplay.clear();
		memorizedNpcs.values().forEach(npc -> npc.setDiedOnTick(-1));
		lastPlayerLocation = null;
		skipNextSpawnCheck = true;
	}

	/**
	 * Registers an NPC for respawn tracking without treating it as a fresh
	 * spawn (used when rebuilding the list of highlighted NPCs).
	 */
	public void memorizeNpc(NPC npc) {
		memorizedNpcs.putIfAbsent(npc.getIndex(), new MemorizedNpc(npc));
	}

	/**
	 * Removes an NPC from respawn tracking because it is no longer highlighted.
	 */
	public void forgetNpc(int index) {
		memorizedNpcs.remove(index);
		deadNpcsToDisplay.remove(index);
	}

	/**
	 * Called when a highlighted NPC spawns.
	 */
	public void onNpcSpawned(NPC npc) {
		memorizeNpc(npc);
		spawnedNpcsThisTick.add(npc);
	}

	/**
	 * Called when an NPC despawns.
	 */
	public void onNpcDespawned(NPC npc) {
		if (memorizedNpcs.containsKey(npc.getIndex()))
		{
			despawnedNpcsThisTick.add(new DespawnedNpc(npc.getWorldLocation(), npc.getIndex()));
		}
	}

	/**
	 * Called when a graphics object is created, to detect NPCs that teleported.
	 */
	public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
		GraphicsObject go = event.getGraphicsObject();
		if (go.getId() == SpotanimID.SMOKEPUFF)
		{
			teleportGraphicsObjectSpawnedThisTick.add(WorldPoint.fromLocal(client, go.getLocation()));
		}
	}

	/**
	 * Called on every game tick to validate spawns/despawns and update timers.
	 */
	public void onGameTick() {
		removeOldHighlightedRespawns();
		validateSpawnedNpcs();
		lastTickUpdate = Instant.now();

		Player local = client.getLocalPlayer();
		if (local != null)
		{
			lastPlayerLocation = local.getWorldLocation();
		}
	}

	private void removeOldHighlightedRespawns() {
		deadNpcsToDisplay.values().removeIf(x -> x.getDiedOnTick() + x.getRespawnTime() <= client.getTickCount() + 1);
	}

	private void validateSpawnedNpcs() {
		if (skipNextSpawnCheck)
		{
			skipNextSpawnCheck = false;
		}
		else
		{
			for (DespawnedNpc npc : despawnedNpcsThisTick)
			{
				if (!teleportGraphicsObjectSpawnedThisTick.isEmpty() && teleportGraphicsObjectSpawnedThisTick.contains(npc.coord))
				{
					// NPC teleported away, so this is not a death.
					continue;
				}

				if (isInViewRange(client.getLocalPlayer().getWorldLocation(), npc.coord))
				{
					MemorizedNpc mn = memorizedNpcs.get(npc.index);

					if (mn != null)
					{
						// This runs before the tick counter updates, so add 1.
						mn.setDiedOnTick(client.getTickCount() + 1);

						if (!mn.getPossibleRespawnLocations().isEmpty())
						{
							deadNpcsToDisplay.put(mn.getNpcIndex(), mn);
						}
					}
				}
			}

			for (NPC npc : spawnedNpcsThisTick)
			{
				if (!teleportGraphicsObjectSpawnedThisTick.isEmpty()
						&& (teleportGraphicsObjectSpawnedThisTick.contains(npc.getWorldLocation())
								|| teleportGraphicsObjectSpawnedThisTick.contains(getWorldLocationBehind(npc))))
				{
					// NPC teleported here, so this is not a respawn.
					continue;
				}

				if (lastPlayerLocation != null && isInViewRange(lastPlayerLocation, npc.getWorldLocation()))
				{
					MemorizedNpc mn = memorizedNpcs.get(npc.getIndex());

					if (mn.getDiedOnTick() != -1)
					{
						final int respawnTime = client.getTickCount() + 1 - mn.getDiedOnTick();

						// Prefer the shorter observed respawn time, and ignore
						// observations that are clearly incorrect.
						if ((mn.getRespawnTime() == -1 || respawnTime < mn.getRespawnTime()) && respawnTime <= MAX_RESPAWN_TIME_TICKS)
						{
							mn.setRespawnTime(respawnTime);
						}

						mn.setDiedOnTick(-1);
					}

					final WorldPoint npcLocation = npc.getWorldLocation();
					// An NPC can move on the same tick it spawns, so also
					// consider the tile behind it as a possible spawn location.
					final WorldPoint possibleOtherNpcLocation = getWorldLocationBehind(npc);

					mn.getPossibleRespawnLocations().removeIf(x -> !x.equals(npcLocation) && !x.equals(possibleOtherNpcLocation));

					if (mn.getPossibleRespawnLocations().isEmpty())
					{
						mn.getPossibleRespawnLocations().add(npcLocation);
						mn.getPossibleRespawnLocations().add(possibleOtherNpcLocation);
					}
				}
			}
		}

		spawnedNpcsThisTick.clear();
		despawnedNpcsThisTick.clear();
		teleportGraphicsObjectSpawnedThisTick.clear();
	}

	private static boolean isInViewRange(WorldPoint wp1, WorldPoint wp2) {
		return wp1.distanceTo(wp2) < MAX_ACTOR_VIEW_RANGE;
	}

	private static WorldPoint getWorldLocationBehind(NPC npc) {
		final int orientation = npc.getOrientation() / 256;
		int dx = 0;
		int dy = 0;

		switch (orientation)
		{
			case 0: // South
				dy = -1;
				break;
			case 1: // Southwest
				dx = -1;
				dy = -1;
				break;
			case 2: // West
				dx = -1;
				break;
			case 3: // Northwest
				dx = -1;
				dy = 1;
				break;
			case 4: // North
				dy = 1;
				break;
			case 5: // Northeast
				dx = 1;
				dy = 1;
				break;
			case 6: // East
				dx = 1;
				break;
			case 7: // Southeast
				dx = 1;
				dy = -1;
				break;
		}

		final WorldPoint currWP = npc.getWorldLocation();
		return new WorldPoint(currWP.getX() - dx, currWP.getY() - dy, currWP.getPlane());
	}
}