package dev.blasty.sbm.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "sbm")
public class SbmConfig implements ConfigData {
    // Farming Macro Config
    public int farmingRows = 16;
    public int farmingRuns = 3;
    public boolean farmingMoveLeftFirst = true;
    public boolean farmingWarpOnStart = false;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int farmingWarpExecutionDelay = 40;
    @ConfigEntry.Gui.Tooltip
    public int farmingWarpContinueDelay = 20;
}
