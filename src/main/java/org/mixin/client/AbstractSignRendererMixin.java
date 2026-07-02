package org.mixin.client;

import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.SignText;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(AbstractSignRenderer.class)
public class AbstractSignRendererMixin {

    @Redirect(
            method = "submitSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/SignText;getRenderMessages(ZLjava/util/function/Function;)[Lnet/minecraft/util/FormattedCharSequence;"
            )
    )
    private FormattedCharSequence[] redirectGetRenderMessages(SignText signText, boolean shouldFilter, Function<Component, FormattedCharSequence> prepare) {
        Function<Component, FormattedCharSequence> colorAwareSplitter = (inputComponent) -> {
            if (inputComponent != null && inputComponent.getString().contains("&")) {
                Component colored = clientsidecolorcodesClient.convertColorCodes(inputComponent);
                return prepare.apply(colored);
            }
            return prepare.apply(inputComponent);
        };
        return signText.getRenderMessages(shouldFilter, colorAwareSplitter);
    }
}