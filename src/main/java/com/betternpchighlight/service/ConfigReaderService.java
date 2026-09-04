package com.betternpchighlight.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

@Singleton
public class ConfigReaderService {

	/**
	 * Splits a comma separated config value into a normalized list of entries.
	 * A comma escaped with a backslash is part of the entry, not a delimiter, so
	 * names containing commas survive the round trip. Entries are trimmed, lower
	 * cased and empty entries are dropped.
	 */
	public ArrayList<String> parseList(String configValue) {
		ArrayList<String> list = new ArrayList<>();
		if (configValue == null || configValue.isEmpty())
		{
			return list;
		}

		for (String entry : splitCsv(configValue))
		{
			String trimmed = entry.trim();
			if (!trimmed.isEmpty())
			{
				list.add(trimmed.toLowerCase());
			}
		}

		return list;
	}

	/**
	 * Joins a list of entries back into a single comma separated config value,
	 * escaping commas and backslashes so the value can be parsed back losslessly.
	 */
	public String listToCsv(List<String> list) {
		return list.stream().map(ConfigReaderService::escape).collect(Collectors.joining(","));
	}

	/**
	 * Returns true when the given string contains only digits.
	 */
	public boolean isNumeric(String str) {
		if (str == null || str.isEmpty())
		{
			return false;
		}
		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

	private static String escape(String value) {
		return value.replace("\\", "\\\\").replace(",", "\\,");
	}

	/**
	 * Splits on commas only when they are not escaped. An escaped comma (and any
	 * other escaped character) is kept as part of the entry, with the escape
	 * backslash consumed in the process.
	 */
	private static List<String> splitCsv(String value) {
		List<String> parts = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < value.length(); i++)
		{
			char c = value.charAt(i);
			if (escaped)
			{
				current.append(c);
				escaped = false;
			}
			else if (c == '\\')
			{
				escaped = true;
			}
			else if (c == ',')
			{
				parts.add(current.toString());
				current.setLength(0);
			}
			else
			{
				current.append(c);
			}
		}
		if (escaped)
		{
			current.append('\\');
		}
		parts.add(current.toString());
		return parts;
	}
}