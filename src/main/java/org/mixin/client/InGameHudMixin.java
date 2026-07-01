package org.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @ModifyExpressionValue(
            method = "renderHeldItemTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"
            )
    )
    private Text convertColorCodes(Text original) {
        return clientsidecolorcodesClient.convertColorCodes(original);
    }
}
