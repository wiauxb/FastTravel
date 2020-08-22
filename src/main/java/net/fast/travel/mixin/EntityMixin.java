package net.fast.travel.mixin;

import net.fast.travel.EntityExt;
import net.fast.travel.Teleporter;
import net.fast.travel.TeleporterEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExt {

    @Shadow public World world;
    @Shadow public boolean removed;

    @Shadow public abstract void detach();

    @Shadow public abstract EntityType<?> getType();

    @Shadow protected abstract void method_30076();

    @Shadow private boolean teleportRequested;

    @Shadow public abstract void fromTag(CompoundTag tag);

    @Shadow public abstract Vec3d getVelocity();

    @Shadow public float yaw;
    @Shadow public float pitch;
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

    public Entity moveToTeleporter(TeleporterEntity teleporter) {
        if(teleporter.getWorld() instanceof ServerWorld) {
            ServerWorld destination = (ServerWorld) teleporter.getWorld();
            BlockPos pos = teleporter.getPos();
            float y_offset = (teleporter.getWorld().getBlockState(teleporter.getPos()).get(Teleporter.TYPE) == SlabType.BOTTOM) ? .5f : 1;
            if (this.world instanceof ServerWorld && !this.removed) {
                this.world.getProfiler().push("changeDimension");
                this.detach();
                this.world.getProfiler().push("reposition");
                TeleportTarget teleportTarget = new TeleportTarget(new Vec3d((double) pos.getX() + 0.5D, (double) pos.getY()+y_offset, (double) pos.getZ() + 0.5D), this.getVelocity(), this.yaw, this.pitch);
                this.world.getProfiler().swap("reloading");
                Entity entity = this.getType().create(destination);
                if (entity != null) {
                    ((EntityExt) entity).copyFromMixin(this);
                    entity.refreshPositionAndAngles(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z, teleportTarget.yaw, entity.pitch);
                    entity.setVelocity(teleportTarget.velocity);
                    destination.onDimensionChanged(entity);
                }

                this.method_30076();
                this.world.getProfiler().pop();
                ((ServerWorld) this.world).resetIdleTimeout();
                destination.resetIdleTimeout();
                this.world.getProfiler().pop();
                return entity;
            } else {
                return null;
            }
        } else return null;
    }

    public void copyFromMixin(EntityExt original) {
        CompoundTag compoundTag = original.toTag(new CompoundTag());
        compoundTag.remove("Dimension");
        this.fromTag(compoundTag);
    }

}
