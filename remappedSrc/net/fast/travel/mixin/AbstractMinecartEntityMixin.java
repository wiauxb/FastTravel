package net.fast.travel.mixin;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends EntityMixin{

    @Inject(method = "tick", at = @At("HEAD"))
    public void TpCountdown(CallbackInfo info){
        if (teleporterCooldown > 0) --teleporterCooldown;
        else if (teleporterCooldown < 0) teleporterCooldown = 0;
    }
}
