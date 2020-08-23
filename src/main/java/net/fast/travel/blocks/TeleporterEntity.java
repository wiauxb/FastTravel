package net.fast.travel.blocks;

import net.fast.travel.FastTravel;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TeleporterEntity extends BlockEntity {

    private RegistryKey<World> targetWorldKey = World.OVERWORLD;
    private BlockPos targetPos = BlockPos.ORIGIN;

    public TeleporterEntity() {
        super(FastTravel.TELEPORTER_ENTITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("targetX", targetPos.getX());
        tag.putInt("targetY", targetPos.getY());
        tag.putInt("targetZ", targetPos.getZ());

        tag.putString("targetWorld", targetWorldKey.getValue().toString());

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        int x = tag.getInt("targetX");
        int y = tag.getInt("targetY");
        int z = tag.getInt("targetZ");

        String strWorldKey = tag.getString("targetWorld");

        targetWorldKey = RegistryKey.of(Registry.DIMENSION, new Identifier(strWorldKey));
        targetPos = new BlockPos(x, y, z);

    }

    public TeleporterEntity getTarget(World world) throws IllegalAccessException {
        if (targetWorldKey == World.OVERWORLD && targetPos == BlockPos.ORIGIN) return null;
        if(world instanceof ServerWorld){
            World targetWorld = ((ServerWorld) world).getServer().getWorld(targetWorldKey);
            return FastTravel.TELEPORTER_ENTITY.get(targetWorld, targetPos);
        }
        else {
            throw new java.lang.IllegalAccessException("BlockEntity should be used by logical server");
        }
    }

    public void setTarget(TeleporterEntity target) {
        if (target == null) {
            throw new NullPointerException();
        }
        targetWorldKey = target.getWorld().getRegistryKey();
        targetPos = target.getPos();
        markDirty();
    }

}
