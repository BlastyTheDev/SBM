package dev.blasty.sbm.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static dev.blasty.sbm.client.SbmClient.CONFIG;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::createConfigScreen;
    }

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("SkyBlock Macro Config"))
                .setSavingRunnable(CONFIG::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory farming = builder.getOrCreateCategory(Text.literal("Farming"));
        farming.addEntry(entryBuilder.startIntField(Text.literal("Number of Rows"), CONFIG.get().farmingRows)
                .setSaveConsumer(value -> CONFIG.get().farmingRows = value)
                .setDefaultValue(16)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Number of Runs"), CONFIG.get().farmingRuns)
                .setSaveConsumer(value -> CONFIG.get().farmingRuns = value)
                .setDefaultValue(3)
                .build());
        farming.addEntry(entryBuilder.startBooleanToggle(Text.literal("Move Left First"), CONFIG.get().farmingMoveLeftFirst)
                .setSaveConsumer(value -> CONFIG.get().farmingMoveLeftFirst = value)
                .setDefaultValue(true)
                .build());
        farming.addEntry(entryBuilder.startBooleanToggle(Text.literal("Warp to Garden on Start"), CONFIG.get().farmingWarpOnStart)
                .setSaveConsumer(value -> CONFIG.get().farmingWarpOnStart = value)
                .setDefaultValue(false)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Warp Execution Delay (Ticks)"), CONFIG.get().farmingWarpExecutionDelay)
                .setSaveConsumer(value -> CONFIG.get().farmingWarpExecutionDelay = value)
                .setTooltip(Text.literal("Ticks to wait before warping to garden."),
                        Text.literal("Does not apply when starting the macro."))
                .setDefaultValue(40)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Warp Continuation Delay (Ticks)"), CONFIG.get().farmingWarpContinueDelay)
                .setSaveConsumer(value -> CONFIG.get().farmingWarpContinueDelay = value)
                .setTooltip(Text.literal("Ticks to wait before continuing to macro after warping to garden."))
                .setDefaultValue(20)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Tool Hotbar Slot"), CONFIG.get().farmingToolSlot)
                .setSaveConsumer(value -> CONFIG.get().farmingToolSlot = value)
                .setDefaultValue(1)
                .setMin(1)
                .setMax(8)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Vacuum Hotbar Slot"), CONFIG.get().farmingVacuumSlot)
                .setSaveConsumer(value -> CONFIG.get().farmingVacuumSlot = value)
                .setDefaultValue(8)
                .setMin(1)
                .setMax(8)
                .build());
        farming.addEntry(entryBuilder.startBooleanToggle(Text.literal("Serve Visitors"), CONFIG.get().farmingServeVisitors)
                .setSaveConsumer(value -> CONFIG.get().farmingServeVisitors = value)
                .setDefaultValue(true)
                .build());
        farming.addEntry(entryBuilder.startIntField(Text.literal("Minimum Copper Reward"), CONFIG.get().farmingMinCopperReward)
                .setSaveConsumer(value -> CONFIG.get().farmingMinCopperReward = value)
                .setTooltip(Text.literal("Minimum amount of Copper a visitor needs to offer to be accepted"))
                .setDefaultValue(20)
                .setMin(0)
                .build());

        return builder.build();
    }
}
