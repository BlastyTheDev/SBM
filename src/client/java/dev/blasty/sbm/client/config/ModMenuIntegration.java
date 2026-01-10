package dev.blasty.sbm.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import static dev.blasty.sbm.client.SbmClient.CONFIG;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
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

            return builder.build();
        };
    }
}
