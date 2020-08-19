package net.fast.travel.mixin;

import net.fast.travel.EntityExt;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntityExt {

    private int teleporterCooldown = 0;

    public boolean canUseTeleporter(){
        return teleporterCooldown == 0;
    }

    public void setTeleporterCooldown(){
        teleporterCooldown = 40;
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void TpCountdown(CallbackInfo info){
        if (teleporterCooldown > 0) --teleporterCooldown;
        else if (teleporterCooldown < 0) teleporterCooldown = 0;
    }


}
