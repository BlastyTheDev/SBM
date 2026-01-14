package dev.blasty.sbm.client;

import dev.blasty.sbm.client.config.ModMenuIntegration;
import dev.blasty.sbm.client.config.SbmConfig;
import dev.blasty.sbm.client.macro.FarmingMacro;
import dev.blasty.sbm.client.macro.Macro;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SbmClient implements ClientModInitializer {
    public static final ConfigHolder<SbmConfig> CONFIG = AutoConfig.register(SbmConfig.class, JanksonConfigSerializer::new);

    private final KeyBinding.Category keybindCategory = KeyBinding.Category.create(Identifier.of("sbm", "keybinds"));
    private final KeyBinding toggleFarmingKey = new KeyBinding("Start/Stop Farming", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F24, keybindCategory);
    private final KeyBinding resumeKey = new KeyBinding("Resume Macro", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F23, keybindCategory);

    private Macro currentMacro;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleFarmingKey);
        KeyBindingHelper.registerKeyBinding(resumeKey);

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
            if (resumeKey.wasPressed()) {
                if (currentMacro.isAlive() && currentMacro.isPaused()) {
                    currentMacro.unpause();
                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("sbm")
                    .executes(context -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        mc.execute(() -> mc.setScreen(ModMenuIntegration.createConfigScreen(mc.currentScreen)));
                        return 1;
                    })
                    .then(literal("start")
                            .then(literal("farming")
                                    .executes(context -> {
                                        currentMacro = new FarmingMacro();
                                        currentMacro.start();
                                        context.getSource().sendFeedback(Text.literal("Farming macro started"));
                                        return 1;
                                    })))
                    .then(literal("pause")
                            .executes(context -> {
                                if (currentMacro.isAlive() && !currentMacro.isPaused()) {
                                    currentMacro.pause();
                                    context.getSource().sendFeedback(Text.literal("Macro paused"));
                                    return 1;
                                }
                                context.getSource().sendError(Text.literal("Macro is not running"));
                                return 0;
                            }))
                    .then(literal("resume")
                            .executes(context -> {
                                if (currentMacro.isAlive() && currentMacro.isPaused()) {
                                    currentMacro.unpause();
                                    context.getSource().sendFeedback(Text.literal("Macro resumed"));
                                    return 1;
                                }
                                context.getSource().sendError(Text.literal("No Farming macro to resume"));
                                return 0;
                            })));
        });
    }
}
