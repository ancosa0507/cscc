package org.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.client.clientsidecolorcodesClient;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(
            method = "updateRenderState",
            at = @At("TAIL")
    )
    private void overrideSneakState(T entity, S state, float tickProgress, CallbackInfo ci) {
        if (state.displayName != null && state.displayName.getString().contains("&")) {
            state.displayName = clientsidecolorcodesClient.convertColorCodes(state.displayName);
        }
    }
}
