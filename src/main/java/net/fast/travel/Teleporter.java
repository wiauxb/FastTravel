package net.fast.travel;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.ToIntFunction;

public class Teleporter extends SlabBlock implements BlockEntityProvider {

    public static final BooleanProperty LINKED = BooleanProperty.of("linked");
    private static TeleporterEntity selection = null;

    public Teleporter() {
        super(FabricBlockSettings.copyOf(Blocks.QUARTZ_SLAB).lightLevel((blockState) -> blockState.get(LINKED) ? 10 : 0));
        this.setDefaultState(this.getStateManager().getDefaultState().with(LINKED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(LINKED);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        EntityExt entity_ext = (EntityExt) entity;
        if(!world.isClient && world.getBlockState(pos).get(LINKED)) {
            TeleporterEntity tp_entity = FastTravel.TELEPORTER_ENTITY.get(world, pos);
            TeleporterEntity target;
            try {
                target = tp_entity.getTarget();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }

            if(target == null || target.getWorld().getBlockState(target.getPos()).getBlock() != FastTravel.TELEPORTER) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                world.setBlockState(pos, world.getBlockState(pos).with(LINKED, false));
                return;
            }
            if(entity.canUsePortals() && entity_ext.canUseTeleporter()) {

                BlockPos dest = target.getPos();
                World targetWorld = target.getWorld();

                world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
                if (targetWorld instanceof ServerWorld) {
                    if(world.getRegistryKey() != targetWorld.getRegistryKey()) {
                        entity.moveToWorld((ServerWorld) targetWorld);
                    }
                    /*
                    this.world.getProfiler().push("portal");
                    this.netherPortalTime = i;
                    this.method_30229();
                    this.moveToWorld(serverWorld2);
                    this.world.getProfiler().pop();*/
                    float y_offset = ((targetWorld.getBlockState(dest).get(TYPE) == SlabType.BOTTOM) ? .5f : 1);
                    entity.teleport(dest.getX()+.5f, dest.getY()+y_offset, dest.getZ()+.5f);
                    world.playSound(null, dest, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
                }
                entity_ext.setTeleporterCooldown();
            }
        }
        super.onSteppedOn(world, pos, entity);
    }

    private void link(TeleporterEntity first, TeleporterEntity second){
        first.setTarget(second);
        second.setTarget(first);
        first.getWorld().setBlockState(first.getPos(), first.getCachedState().with(LINKED, true));
        second.getWorld().setBlockState(second.getPos(), second.getCachedState().with(LINKED, true));
    }

    private void unlink(TeleporterEntity first, TeleporterEntity second){
        first.getWorld().setBlockState(first.getPos(), first.getCachedState().with(LINKED, false));
        second.getWorld().setBlockState(second.getPos(), second.getCachedState().with(LINKED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if(stack.getItem() == Items.DIAMOND && !world.isClient){
            TeleporterEntity entity1 = FastTravel.TELEPORTER_ENTITY.get(world, pos);
            if( selection != null && !selection.equals(entity1)){

                link(entity1, selection);
                selection = null;

                if(!player.isCreative()){
                    stack.decrement(1);
                }
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1f, 1f);
            }
            else {
                if (selection == entity1){
                    selection = null;
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                } else {
                    selection = entity1;
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TeleporterEntity();
    }

}
