package dev.blasty.sbm.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "sbm")
public class SbmConfig implements ConfigData {
    // Farming Macro Config
    public int farmingRows = 16;
    public int farmingRuns = 3;
    public boolean farmingMoveLeftFirst = true;
}
