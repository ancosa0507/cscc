package org.mixin.client;

import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState;
import net.minecraft.text.Text;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSignBlockEntityRenderer.class)
public class AbstractSignBlockEntityRendererMixin {
    @ModifyVariable(
            method = "renderText",
            at = @At("HEAD"),
            argsOnly = true
    )
    private SignBlockEntityRenderState convertColorCodesOnSignRender(SignBlockEntityRenderState value) {
        if (value == null) return null;
        if (value.frontText != null) {
            Text[] frontMessages = value.frontText.getMessages(false);
            for (int i = 0; i < frontMessages.length; i++) {
                Text original = frontMessages[i];
                Text converted = convertColorCodes(original);
                if (converted != original) {
                    value.frontText = value.frontText.withMessage(i, converted);
                }
            }
        }
        if (value.backText != null) {
            Text[] backMessages = value.backText.getMessages(false);
            for (int i = 0; i < backMessages.length; i++) {
                Text original = backMessages[i];
                Text converted = convertColorCodes(original);
                if (converted != original) {
                    value.backText = value.backText.withMessage(i, converted);
                }
            }
        }
        return value;
    }
    @Unique
    private Text convertColorCodes(Text original) {
        if (original == null) return null;
        return clientsidecolorcodesClient.convertColorCodes(original);
    }
}
