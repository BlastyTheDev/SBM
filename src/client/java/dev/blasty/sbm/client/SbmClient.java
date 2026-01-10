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
    private final KeyBinding toggleFarmingKey = new KeyBinding("Start/Stop Farming", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F24, keybindCategory);
    private final KeyBinding resumeFarmingKey = new KeyBinding("Resume Farming", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F23, keybindCategory);

    private Macro currentMacro;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleFarmingKey);
        KeyBindingHelper.registerKeyBinding(resumeFarmingKey);

        ClientTickEvents.START_CLIENT_TICK.register((mc) -> Macro.tickDelayQueue.offer(Boolean.TRUE));
        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            if (toggleFarmingKey.wasPressed()) {
                if (currentMacro instanceof FarmingMacro && currentMacro.isAlive() && !currentMacro.isPaused()) {
                    currentMacro.pause();
                } else {
                    currentMacro = new FarmingMacro();
                    currentMacro.start();
                }
            }
            if (resumeFarmingKey.wasPressed()) {
                if (currentMacro instanceof FarmingMacro && currentMacro.isAlive() && currentMacro.isPaused()) {
                    currentMacro.unpause();
                }
            }
        });
    }
}
