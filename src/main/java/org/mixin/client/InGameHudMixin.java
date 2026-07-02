package org.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class InGameHudMixin {

    @Inject(
            method = "extractSelectedItemName",
            at = @At("HEAD")
    )
    private void convertSelectedItemNameColorCodes(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            ItemStack heldItem = minecraft.player.getInventory().getSelectedItem();

            if (!heldItem.isEmpty()) {
                Component originalName = heldItem.getHoverName();

                if (originalName.getString().contains("&")) {
                    Component coloredName = clientsidecolorcodesClient.convertColorCodes(originalName);
                    heldItem.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, coloredName);
                }
            }
        }
    }
}