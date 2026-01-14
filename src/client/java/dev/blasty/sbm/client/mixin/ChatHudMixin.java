package dev.blasty.sbm.client.mixin;

import dev.blasty.sbm.client.SbmClient;
import dev.blasty.sbm.client.macro.FarmingMacro;
import dev.blasty.sbm.client.macro.Macro;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V")
    private void addMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Macro currentMacro = SbmClient.getInstance().getCurrentMacro();
        if (message.getString().endsWith(" has arrived on your Garden!") && currentMacro instanceof FarmingMacro farmingMacro) {
            farmingMacro.queueVisitorCheck();
        }
    }
}
