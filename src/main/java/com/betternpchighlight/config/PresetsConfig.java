package com.betternpchighlight.config;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

public interface PresetsConfig extends Config {
	String presetsSectionName = "presets";

  //------------------------------------------------------------//
	// Presets Section
	//------------------------------------------------------------//
	@ConfigItem(position = 1, keyName = "presetColorAmount", name = "Preset Colors Amount", description = "The amount of preset colors you want in the Tag sub-menu", section = presetsSectionName)
	default presetColorAmount presetColorAmount() {
		return presetColorAmount.ZERO;
	}

	@Alpha
	@ConfigItem(position = 2, keyName = "presetColor1", name = "Preset Color 1", description = "Sets color for the first preset color", section = presetsSectionName)
	default Color presetColor1() {
		return new Color(224, 60, 49, 255);
	}

	@Alpha
	@ConfigItem(position = 3, keyName = "presetFillColor1", name = "Preset Fill Color 1", description = "Sets the fill color for the first preset color", section = presetsSectionName)
	default Color presetFillColor1() {
		return new Color(224, 60, 49, 20);
	}

	@Alpha
	@ConfigItem(position = 4, keyName = "presetColor2", name = "Preset Color 2", description = "Sets color for the second preset color", section = presetsSectionName)
	default Color presetColor2() {
		return new Color(37, 197, 79, 255);
	}

	@Alpha
	@ConfigItem(position = 5, keyName = "presetFillColor2", name = "Preset Fill Color 2", description = "Sets the fill color for the second preset color", section = presetsSectionName)
	default Color presetFillColor2() {
		return new Color(37, 197, 79, 20);
	}

	@Alpha
	@ConfigItem(position = 6, keyName = "presetColor3", name = "Preset Color 3", description = "Sets color for the third preset color", section = presetsSectionName)
	default Color presetColor3() {
		return new Color(207, 138, 253, 255);
	}

	@Alpha
	@ConfigItem(position = 7, keyName = "presetFillColor3", name = "Preset Fill Color 3", description = "Sets the fill color for the third preset color", section = presetsSectionName)
	default Color presetFillColor3() {
		return new Color(207, 138, 253, 20);
	}

	@Alpha
	@ConfigItem(position = 8, keyName = "presetColor4", name = "Preset Color 4", description = "Sets color for the fourth preset color", section = presetsSectionName)
	default Color presetColor4() {
		return new Color(38, 255, 169, 255);
	}

	@Alpha
	@ConfigItem(position = 9, keyName = "presetFillColor4", name = "Preset Fill Color 4", description = "Sets the fill color for the fourth preset color", section = presetsSectionName)
	default Color presetFillColor4() {
		return new Color(38, 255, 169, 20);
	}

	@Alpha
	@ConfigItem(position = 10, keyName = "presetColor5", name = "Preset Color 5", description = "Sets color for the fifth preset color", section = presetsSectionName)
	default Color presetColor5() {
		return new Color(0, 150, 200, 255);
	}

	@Alpha
	@ConfigItem(position = 11, keyName = "presetFillColor5", name = "Preset Fill Color 5", description = "Sets the fill color for the fifth preset color", section = presetsSectionName)
	default Color presetFillColor5() {
		return new Color(0, 150, 200, 20);
	}

  @Getter
	@RequiredArgsConstructor
	enum presetColorAmount {
		ZERO("None"), ONE("One"), TWO("Two"), THREE("Three"), FOUR("Four"), FIVE("Five"),;

		@Getter
		private final String group;

		@Override
		public String toString() {
			return group;
		}
	}
}
