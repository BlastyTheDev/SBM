package dev.blasty.sbm.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class SbmClient implements ClientModInitializer {
    private final KeyBinding.Category keybindCategory = KeyBinding.Category.create(Identifier.of("sbm", "keybinds"));
    private final KeyBinding toggleFarmingKey = new KeyBinding("Toggle Farming", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_F24, keybindCategory);

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleFarmingKey);
    }
}
