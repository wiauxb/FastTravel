package net.fast.travel;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;

public interface EntityExt {

    boolean canUseTeleporter();
    void setTeleporterCooldown();

    CompoundTag toTag(CompoundTag compoundTag);

    void copyFromMixin(EntityExt entity);
    Entity moveToTeleporter(TeleporterEntity teleporter);
}
