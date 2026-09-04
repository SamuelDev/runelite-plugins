package com.betternpchighlight.managers;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.betternpchighlight.BetterNpcHighlightConfig;
import com.betternpchighlight.config.GlobalConfig.tagStyleMode;
import com.betternpchighlight.config.PresetsConfig.presetColorAmount;
import com.betternpchighlight.data.NPCInfo;
import com.betternpchighlight.data.NameAndIdContainer;
import com.google.common.collect.ImmutableSet;

import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

public class MenuManager {
	private static final String TAG_OPTION = "Tag-NPC";
	private static final String UNTAG_OPTION = "Untag-NPC";
	private static final String RESET_COLOR_OPTION = "Reset color";
	private static final String PRESET_COLOR_OPTION_PREFIX = "Preset color ";

	private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet
			.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION, MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION,
					MenuAction.NPC_FIFTH_OPTION, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.ITEM_USE_ON_NPC);

	/**
	 * Splits a menu target into its color tags, NPC name and optional
	 * "(level-N)" suffix so the name and level can be recolored independently.
	 */
	private static final Pattern MENU_TARGET_PATTERN = Pattern
			.compile("^(?<leadingTags>(?:<[^>]+>)*)" + "(?<name>[^<(]+?)" + "(?<trailingTags>(?:<[^>]+>)*)"
					+ "(?:\\s*(?<level>\\(level-\\d+\\)))?$");

	@Inject
	private Client client;
	@Inject
	private BetterNpcHighlightConfig config;
	@Inject
	private NameAndIdContainer nameAndIdContainer;
	@Inject
	private NpcUtil npcUtil;
	@Inject
	private ColorManager colorManager;
	@Inject
	private ConfigTransformManager configTransformManager;

	/**
	 * Entry point, invoked for every menu entry that is added. Dispatches to the
	 * handler for the entry's action type.
	 */
	public void onMenuEntryAdded(MenuEntryAdded event) {
		MenuAction action = MenuAction.of(normalizeType(event.getType()));
		NPC npc = event.getMenuEntry().getNpc();

		if (NPC_MENU_ACTIONS.contains(action))
		{
			colorizeNpcMenuEntry(event, npc);
		}
		else if (action == MenuAction.EXAMINE_NPC && npc != null)
		{
			handleExamineMenuEntry(event, npc);
		}
	}

	// ----------------------------------------------------------------//
	// NPC interaction entries (e.g. "Attack", "Use item ->", ...)
	// ----------------------------------------------------------------//

	/**
	 * Recolors the NPC name on regular interaction menu entries.
	 */
	private void colorizeNpcMenuEntry(MenuEntryAdded event, NPC npc) {
		Color color = resolveNpcActionColor(npc);
		if (color != null)
		{
			setLastMenuEntryTarget(getMenuEntryString(event.getTarget(), color, config.highlightMenuNamesLevel()));
		}
	}

	/**
	 * Resolves the color for a regular NPC interaction entry, or null to leave it
	 * untouched.
	 */
	private Color resolveNpcActionColor(NPC npc) {
		if (npcUtil.isDying(npc))
		{
			return config.deadNpcMenuColor();
		}

		if (config.highlightMenuNames() && npc.getName() != null && configTransformManager.isInAnyList(npc))
		{
			return getNpcDisplayColor(npc);
		}

		return null;
	}

	// ----------------------------------------------------------------//
	// Examine entry (Tag-NPC / Untag-NPC and preset colors)
	// ----------------------------------------------------------------//

	private void handleExamineMenuEntry(MenuEntryAdded event, NPC npc) {
		if (npc.getName() == null || !isTaggingEnabled())
		{
			return;
		}

		String option = configTransformManager.isInAnyList(npc) ? UNTAG_OPTION : TAG_OPTION;

		// Recolor the existing "Examine" entry when it shows an untag action.
		if (option.equals(UNTAG_OPTION) && shouldColorize(npc))
		{
			setLastMenuEntryTarget(colorizeTarget(npc, event.getTarget()));
		}

		// Holding shift adds a dedicated tag/untag entry with a preset submenu.
		if (client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			addTagMenuEntry(event, npc, option);
		}
	}

	/**
	 * Whether the Tag/Untag menu options should be shown, based on the configured
	 * tag styles.
	 */
	private boolean isTaggingEnabled() {
		Set<tagStyleMode> styles = config.tagStyleModeSet();
		if (styles.contains(tagStyleMode.NONE))
		{
			return styles.size() > 1;
		}
		return !styles.isEmpty();
	}

	/**
	 * Adds the shift-held "Tag-NPC"/"Untag-NPC" entry and its preset submenu.
	 */
	private void addTagMenuEntry(MenuEntryAdded event, NPC npc, String option) {
		MenuEntry parent = client
				.createMenuEntry(-1)
				.setOption(option)
				.setTarget(colorizeTarget(npc, event.getTarget()))
				.setIdentifier(event.getIdentifier())
				.setWorldViewId(event.getMenuEntry().getWorldViewId())
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setType(MenuAction.RUNELITE)
				.onClick(this::handleTagOptionClicked);

		if (parent != null)
		{
			addPresetColorSubmenu(npc, parent);
		}
	}

	/**
	 * Builds the preset color submenu under the tag/untag entry.
	 */
	private void addPresetColorSubmenu(NPC npc, MenuEntry parent) {
		if (config.presetColorAmount() == presetColorAmount.ZERO)
		{
			return;
		}

		List<Color> colors = loadPresetColors();
		Menu submenu = parent.createSubMenu();

		if (!colors.isEmpty())
		{
			int preset = 1;
			for (Color color : colors)
			{
				if (color != null)
				{
					final int presetNumber = preset;
					submenu
							.createMenuEntry(0)
							.setOption(ColorUtil.prependColorTag(PRESET_COLOR_OPTION_PREFIX + preset, color))
							.setType(MenuAction.RUNELITE)
							.onClick(e -> tagNpcWithPreset(npc, presetNumber));
					preset++;
				}
			}
		}

		// Only offer "Reset color" for NPCs that are already tagged.
		if (nameAndIdContainer.getNpcInfoByNpc(npc) != null)
		{
			submenu
					.createMenuEntry(0)
					.setOption(RESET_COLOR_OPTION)
					.setType(MenuAction.RUNELITE)
					.onClick(e -> tagNpcWithPreset(npc, 0));
		}
	}

	/**
	 * Handles clicks on the shift-held "Tag-NPC"/"Untag-NPC" entry.
	 */
	private void handleTagOptionClicked(MenuEntry entry) {
		if (entry.getType() != MenuAction.RUNELITE)
		{
			return;
		}

		if (entry.getOption().equals(TAG_OPTION) || entry.getOption().equals(UNTAG_OPTION))
		{
			NPC npc = client.getWorldView(entry.getWorldViewId()).npcs().byIndex(entry.getIdentifier());
			if (npc.getName() != null)
			{
				configTransformManager.updateListConfig(entry.getOption().equals(TAG_OPTION), npc.getName().toLowerCase(), 0);
			}
		}
	}

	/**
	 * Tags an NPC with the given preset color (0 resets to the default).
	 */
	private void tagNpcWithPreset(NPC npc, int preset) {
		if (npc.getName() != null)
		{
			configTransformManager.updateListConfig(true, npc.getName().toLowerCase(), preset);
		}
	}

	// ----------------------------------------------------------------//
	// Color resolution
	// ----------------------------------------------------------------//

	/**
	 * Color used for tag/untag related entries, falling back to the tag color when
	 * the NPC has no specific highlight.
	 */
	private Color getNpcDisplayColor(NPC npc) {
		Color color = getSpecificNpcColor(npc);
		return color != null ? color : colorManager.getTagColor();
	}

	/**
	 * Color of the NPC's current highlight, or null when it is not highlighted.
	 */
	private Color getSpecificNpcColor(NPC npc) {
		NPCInfo npcInfo = nameAndIdContainer.getNpcInfoByNpc(npc);
		return npcInfo != null ? colorManager.getSpecificColor(npcInfo) : null;
	}

	/**
	 * Whether an examine/tag target should be recolored.
	 */
	private boolean shouldColorize(NPC npc) {
		return config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null);
	}

	/**
	 * Recolors a target string for examine/tag entries, leaving it unchanged when
	 * colorization is disabled.
	 */
	private String colorizeTarget(NPC npc, String target) {
		if (!shouldColorize(npc))
		{
			return target;
		}

		Color color = (npc.isDead() && config.deadNpcMenuColor() != null) ? config.deadNpcMenuColor() : getNpcDisplayColor(npc);
		return getMenuEntryString(target, color, config.highlightMenuNamesLevel());
	}

	// ----------------------------------------------------------------//
	// Preset colors
	// ----------------------------------------------------------------//

	/**
	 * Loads the enabled preset colors in order.
	 */
	private List<Color> loadPresetColors() {
		List<Color> colors = new ArrayList<>();
		presetColorAmount amount = config.presetColorAmount();

		if (amount == presetColorAmount.ONE)
		{
			colors.add(config.presetColor1());
		}
		else if (amount == presetColorAmount.TWO)
		{
			colors.add(config.presetColor1());
			colors.add(config.presetColor2());
		}
		else if (amount == presetColorAmount.THREE)
		{
			colors.add(config.presetColor1());
			colors.add(config.presetColor2());
			colors.add(config.presetColor3());
		}
		else if (amount == presetColorAmount.FOUR)
		{
			colors.add(config.presetColor1());
			colors.add(config.presetColor2());
			colors.add(config.presetColor3());
			colors.add(config.presetColor4());
		}
		else if (amount == presetColorAmount.FIVE)
		{
			colors.add(config.presetColor1());
			colors.add(config.presetColor2());
			colors.add(config.presetColor3());
			colors.add(config.presetColor4());
			colors.add(config.presetColor5());
		}

		return colors;
	}

	// ----------------------------------------------------------------//
	// Menu target string building
	// ----------------------------------------------------------------//

	/**
	 * Rebuilds a menu target string, coloring the name and (optionally) its level.
	 */
	private String getMenuEntryString(String target, Color color, boolean includeLevel) {
		Matcher matcher = MENU_TARGET_PATTERN.matcher(target);
		if (!matcher.matches())
		{
			return ColorUtil.prependColorTag(Text.removeTags(target), color);
		}

		String name = matcher.group("name");
		String trailingTags = matcher.group("trailingTags");
		String levelPart = matcher.group("level");

		StringBuilder rebuilt = new StringBuilder(ColorUtil.prependColorTag(name, color));
		if (includeLevel)
		{
			if (levelPart != null)
			{
				rebuilt.append("  ").append(ColorUtil.prependColorTag(levelPart, color));
			}
		}
		else
		{
			if (trailingTags != null && !trailingTags.isEmpty())
			{
				rebuilt.append(trailingTags);
			}
			if (levelPart != null)
			{
				rebuilt.append("  ").append(levelPart);
			}
		}

		return rebuilt.toString();
	}

	/**
	 * Overwrites the target of the most recently added menu entry.
	 */
	private void setLastMenuEntryTarget(String target) {
		MenuEntry[] entries = client.getMenuEntries();
		entries[entries.length - 1].setTarget(target);
		client.setMenuEntries(entries);
	}

	private int normalizeType(int type) {
		return type >= MENU_ACTION_DEPRIORITIZE_OFFSET ? type - MENU_ACTION_DEPRIORITIZE_OFFSET : type;
	}
}