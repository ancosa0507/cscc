package org.mixin.client;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
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
    private SignText convertColorCodesOnSignRender(SignText text) {
        if (text == null) return null;
        Text[] frontMessages = text.getMessages(false);
        for (int i = 0; i < frontMessages.length; i++) {
            Text original = frontMessages[i];
            Text converted = convertColorCodes(original);
            if (converted != original) {
                text = text.withMessage(i, converted);
            }
        }
        return text;
    }

    @Unique
    private Text convertColorCodes(Text original) {
        if (original == null) return null;
        return clientsidecolorcodesClient.convertColorCodes(original);
    }
}
