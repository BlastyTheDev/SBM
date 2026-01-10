package dev.blasty.sbm.client;

import dev.blasty.sbm.client.config.SbmConfig;
import dev.blasty.sbm.client.macro.FarmingMacro;
import dev.blasty.sbm.client.macro.Macro;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class SbmClient implements ClientModInitializer {
    public static final ConfigHolder<SbmConfig> CONFIG = AutoConfig.register(SbmConfig.class, JanksonConfigSerializer::new);

    private final KeyBinding.Category keybindCategory = KeyBinding.Category.create(Identifier.of("sbm", "keybinds"));
    private final KeyBinding toggleFarmingKey = new KeyBinding("Toggle Farming", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F24, keybindCategory);

    private Thread currentMacro;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleFarmingKey);

        ClientTickEvents.START_CLIENT_TICK.register((client) -> Macro.tickDelayQueue.offer(Boolean.TRUE));
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (toggleFarmingKey.wasPressed()) {
                if (currentMacro instanceof FarmingMacro farmingMacro && currentMacro.isAlive()) {
                    if (farmingMacro.isPaused()) {
                        farmingMacro.unpause();
                    } else {
                        farmingMacro.pause();
                    }
                } else {
                    currentMacro = new FarmingMacro();
                    currentMacro.start();
                }
            }
        });
    }
}
