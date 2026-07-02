package org.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.client.clientsidecolorcodesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void overrideSneakState(T entity, S state, float f, CallbackInfo ci) {
        if (state.nameTag != null && state.nameTag.getString().contains("&")) {
            state.nameTag = clientsidecolorcodesClient.convertColorCodes(state.nameTag);
        }
    }
}