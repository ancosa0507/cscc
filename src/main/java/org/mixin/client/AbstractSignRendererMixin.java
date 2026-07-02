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
            method = "submitSignWithText",
            at = @At("HEAD"),
            argsOnly = true,
            name = "state")
    private SignRenderState convertColorCodesOnSignRender(SignRenderState state) {
        if (state == null) return null;
        if (state.frontText != null) {
            Component[] frontMessages = state.frontText.getMessages(false);
            for (int i = 0; i < frontMessages.length; i++) {
                Component original = frontMessages[i];
                Component converted = convertColorCodes(original);
                if (converted != original) {
                    state.frontText = state.frontText.setMessage(i, converted);
                }
            }
        }
        if (state.backText != null) {
            Component[] backMessages = state.backText.getMessages(false);
            for (int i = 0; i < backMessages.length; i++) {
                Component original = backMessages[i];
                Component converted = convertColorCodes(original);
                if (converted != original) {
                    state.backText = state.backText.setMessage(i, converted);
                }
            }
        }
        return state;
    }

    @Unique
    private Component convertColorCodes(Component original) {
        if (original == null) return null;
        return clientsidecolorcodesClient.convertColorCodes(original);
    }
}
