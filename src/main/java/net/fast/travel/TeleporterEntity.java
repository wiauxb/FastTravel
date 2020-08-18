package net.fast.travel;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TeleporterEntity extends BlockEntity {

    private RegistryKey<World> targetWorldKey = World.OVERWORLD;
    private BlockPos targetPos = BlockPos.ORIGIN;

    private TeleporterEntity target;
    private boolean targetChanged = false;

    public TeleporterEntity() {
        super(FastTravel.TELEPORTER_ENTITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("targetX", targetPos.getX());
        tag.putInt("targetY", targetPos.getY());
        tag.putInt("targetZ", targetPos.getZ());

        if (World.OVERWORLD.equals(targetWorldKey)) {
            tag.putString("targetWorld", "OVERWORLD");
        } else if (World.NETHER.equals(targetWorldKey)) {
            tag.putString("targetWorld", "NETHER");
        } else if (World.END.equals(targetWorldKey)) {
            tag.putString("targetWorld", "END");
        } else {
            System.out.println("Error: Invalid Target World Registry Key");
        }

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) { //TODO real exception gestion
        super.fromTag(state, tag);

        int x = tag.getInt("targetX");
        int y = tag.getInt("targetY");
        int z = tag.getInt("targetZ");

        String strWorldKey = tag.getString("targetWorld");

        RegistryKey<World> targetWorldKey = World.OVERWORLD;

        switch (strWorldKey) {
            case "OVERWORLD":
                targetWorldKey = World.OVERWORLD;
                break;
            case "NETHER":
                targetWorldKey = World.NETHER;
                break;
            case "END":
                targetWorldKey = World.END;
                break;
            default:
                System.out.println("Error: Invalid Target World Tag");
                break;
        }

        this.targetWorldKey = targetWorldKey;
        targetPos = new BlockPos(x, y, z);
        targetChanged = true;
    }

    private void lazyTargetCalculation() throws IllegalAccessException {
        if(!targetChanged){}
        else if (targetWorldKey == World.OVERWORLD && targetPos == BlockPos.ORIGIN) {
            target = null;
            targetChanged = false;
        }
        else if(world instanceof ServerWorld){
            World  targetWorld = ((ServerWorld) world).getServer().getWorld(targetWorldKey);
            targetChanged = false;
            target = FastTravel.TELEPORTER_ENTITY.get(targetWorld, targetPos);
        }
        else {
            throw new java.lang.IllegalAccessException("BlockEntity should be used by logical server");
        }
    }

    public TeleporterEntity getTarget() throws IllegalAccessException {
        lazyTargetCalculation();
        return target;
    }

    public void setTarget(TeleporterEntity target) {
        if (target == null) {
            throw new NullPointerException();
        }
        targetWorldKey = target.getWorld().getRegistryKey();
        targetPos = target.getPos();
        targetChanged = true;
        System.out.println("[FT]: "+targetPos+" "+targetWorldKey);
        markDirty();
    }

}
