package org.mixin.client;

import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.network.chat.Component;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSignRenderer.class)
public class AbstractSignRendererMixin {
    @ModifyVariable(
            method = "submitSignText",
            at = @At("HEAD"),
            argsOnly = true
    )
    private SignRenderState convertColorCodesOnSignRender(SignRenderState value) {
        if (value == null) return null;
        if (value.frontText != null) {
            Component[] frontMessages = value.frontText.getMessages(false);
            for (int i = 0; i < frontMessages.length; i++) {
                Component original = frontMessages[i];
                Component converted = convertColorCodes(original);
                if (converted != original) {
                    value.frontText = value.frontText.setMessage(i, converted);
                }
            }
        }
        if (value.backText != null) {
            Component[] backMessages = value.backText.getMessages(false);
            for (int i = 0; i < backMessages.length; i++) {
                Component original = backMessages[i];
                Component converted = convertColorCodes(original);
                if (converted != original) {
                    value.backText = value.backText.setMessage(i, converted);
                }
            }
        }
        return value;
    }

    @Unique
    private Component convertColorCodes(Component original) {
        if (original == null) return null;
        return clientsidecolorcodesClient.convertColorCodes(original);
    }
}
