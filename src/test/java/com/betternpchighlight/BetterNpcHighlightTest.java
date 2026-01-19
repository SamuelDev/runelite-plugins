package com.betternpchighlight;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BetterNpcHighlightTest {
  public static void main(String[] args) throws Exception {
    ExternalPluginManager.loadBuiltin(BetterNpcHighlightPlugin.class);
    RuneLite.main(args);
  }
}