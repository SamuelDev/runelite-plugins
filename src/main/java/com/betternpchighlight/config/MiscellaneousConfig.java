package com.betternpchighlight.config;

import java.awt.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

public interface MiscellaneousConfig {
  String miscellaneousSectionName = "miscellaneous";

  //------------------------------------------------------------//
  // Miscellaneous Section
  //------------------------------------------------------------//
  @ConfigItem(position = -1, keyName = "tagCommands", name = "Tag Commands", description = "Enables the use of commands to add/remove NPCs to the Names/IDs list <br>Read the guide in Instructions section", section = miscellaneousSectionName)
  default boolean tagCommands() {
    return true;
  }

  @ConfigItem(position = 0, keyName = "highlightMenuNames", name = "Highlight Menu Names", description = "Highlights names in right click menu entry", section = miscellaneousSectionName)
  default boolean highlightMenuNames() {
    return false;
  }

  @ConfigItem(position = 1, keyName = "highlightMenuNamesLevel", name = "Highlight Menu Level", description = "Include the level when highlighting menu names", section = miscellaneousSectionName)
  default boolean highlightMenuNamesLevel() {
    return true;
  }

  @ConfigItem(position = 2, keyName = "ignoreDeadNpcs", name = "Ignore Dead NPCs", description = "Doesn't highlight dead NPCs", section = miscellaneousSectionName)
  default boolean ignoreDeadNpcs() {
    return false;
  }

  @ConfigItem(position = 3, keyName = "ignoreDeadExclusion", name = "Ignore Dead Exclusion Name List", description = "List of NPC names to not remove highlight when dead", section = miscellaneousSectionName)
  default String ignoreDeadExclusion() {
    return "";
  }

  @ConfigItem(position = 4, keyName = "ignoreDeadExclusionID", name = "Ignore Dead Exclusion ID List", description = "List of NPC IDs to not remove highlight when dead", section = miscellaneousSectionName)
  default String ignoreDeadExclusionID() {
    return "";
  }

  @ConfigItem(position = 5, keyName = "drawBeneath", name = "Draw Overlays Beneath NPCs", description = "Overlays will appear behind/below NPCs. GPU plugin must be turned on", section = miscellaneousSectionName)
  default boolean drawBeneath() {
    return false;
  }

  @Range(max = 20)
  @ConfigItem(position = 6, keyName = "drawBeneathLimit", name = "Draw Beneath Limit", description = "Sets the amount of NPCs to have the overlay draw beneath. The higher the number, the more it affects FPS", section = miscellaneousSectionName)
  default int drawBeneathLimit() {
    return 10;
  }

  @ConfigItem(position = 7, keyName = "drawBeneathList", name = "Draw Beneath List", description = "Sets specific NPCs to have the overlay draw beneath. Empty list will use Draw Beneath Limit", section = miscellaneousSectionName)
  default String drawBeneathList() {
    return "";
  }

  @ConfigItem(position = 8, keyName = "renderDistance", name = "Render Distance", description = "Limits overlays to be drawn to within the chosen distance from the local player. <br>Short = 7 tiles, Medium = 11 tiles", section = miscellaneousSectionName)
  default renderDistance renderDistance() {
    return renderDistance.NONE;
  }

  @ConfigItem(position = 9, keyName = "highlightPets", name = "Highlight pets", description = "Highlights followers/pets that are in any of your lists", section = miscellaneousSectionName)
  default boolean highlightPets() {
    return false;
  }

  @ConfigItem(position = 10, keyName = "deadNpcMenuColor", name = "Dead NPC Menu Color", description = "Highlights names in right click menu entry when an NPC is dead", section = miscellaneousSectionName)
  Color deadNpcMenuColor();

  @ConfigItem(position = 11, keyName = "respawnTimer", name = "Respawn Timer", description = "Marks tile and shows timer for when a marker NPC will respawn", section = miscellaneousSectionName)
  default respawnTimerMode respawnTimer() {
    return respawnTimerMode.OFF;
  }

  @Alpha
  @ConfigItem(position = 12, keyName = "respawnTimerColor", name = "Respawn Time Color", description = "Sets the color of the text for Respawn Timer", section = miscellaneousSectionName)
  default Color respawnTimerColor() {
    return Color.WHITE;
  }

  @Alpha
  @ConfigItem(position = 13, keyName = "respawnOutlineColor", name = "Respawn Outline Color", description = "Sets the color of the tile for Respawn Timer", section = miscellaneousSectionName)
  default Color respawnOutlineColor() {
    return Color.CYAN;
  }

  @Alpha
  @ConfigItem(position = 14, keyName = "respawnFillColor", name = "Respawn Fill Color", description = "Sets the fill color of the tile for Respawn Timer", section = miscellaneousSectionName)
  default Color respawnFillColor() {
    return new Color(0, 255, 255, 20);
  }

  @Range(min = 1, max = 10)
  @ConfigItem(position = 15, keyName = "respawnTileWidth", name = "Respawn Tile Width", description = "Sets the width of the tile for Respawn Timer", section = miscellaneousSectionName)
  default int respawnTileWidth() {
    return 2;
  }

  @ConfigItem(position = 16, keyName = "displayName", name = "Display Name", description = "Shows name of NPCs in the list above them", section = miscellaneousSectionName)
  default String displayName() {
    return "";
  }

  @ConfigItem(position = 17, keyName = "fontBackground", name = "Font Background", description = "Puts an outline, shadow, or nothing behind font overlays", section = miscellaneousSectionName)
  default background fontBackground() {
    return background.SHADOW;
  }

  @ConfigItem(position = 18, keyName = "npcMinimapMode", name = "Highlight Minimap", description = "Highlights NPC on minimap and/or displays name", section = miscellaneousSectionName)
  default npcMinimapMode npcMinimapMode() {
    return npcMinimapMode.OFF;
  }

  @ConfigItem(position = 19, keyName = "debugNPC", name = "Debug NPC Info", description = "Highlights all NPCs with their Name and ID", section = miscellaneousSectionName)
  default boolean debugNPC() {
    return false;
  }

  @Getter
  @RequiredArgsConstructor
  enum background {
    OFF("None"), SHADOW("Shadow"), OUTLINE("Outline"),;

    @Getter
    private final String group;

    @Override
    public String toString() {
      return group;
    }
  }

  @Getter
  @AllArgsConstructor
  enum renderDistance {
    SHORT("Short", 7), MED("Medium", 11), NONE("No Limit", 0); //14 = max distance

    private final String group;
    private final int distance;

    @Override
    public String toString() {
      return group;
    }
  }

  enum respawnTimerMode {
    OFF, TICKS, SECONDS
  }

  enum npcMinimapMode {
    OFF, DOT, NAME, BOTH
  }
}
