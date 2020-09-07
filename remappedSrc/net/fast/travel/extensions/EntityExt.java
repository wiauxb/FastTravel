package net.fast.travel.extensions;

import net.fast.travel.blocks.TeleporterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;

public interface EntityExt {

    boolean canUseTeleporter();
    void setTeleporterCooldown();

    void copyFromMixin(EntityExt entity);
    Entity moveToTeleporter(TeleporterEntity teleporter);
}
