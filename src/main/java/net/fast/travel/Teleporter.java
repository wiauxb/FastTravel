package net.fast.travel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class Teleporter extends SlabBlock implements BlockEntityProvider {

    public static final BooleanProperty LINKED = BooleanProperty.of("linked");

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
                target = tp_entity.getTarget(world);
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
                    float y_offset = getYOffset(targetWorld.getBlockState(dest));
                    entity.teleport(dest.getX()+.5f, dest.getY()+y_offset, dest.getZ()+.5f);
                    world.playSound(null, dest, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
                }
                entity_ext.setTeleporterCooldown();
            }
        }
        super.onSteppedOn(world, pos, entity);
    }

    private float getYOffset(BlockState blockState){
        return (blockState.get(TYPE) == SlabType.BOTTOM) ? .5f : 1;
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
        if(!world.isClient) {
            ServerPlayerEntityExt player_ext = (ServerPlayerEntityExt) player;
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() == Items.DIAMOND) {
                TeleporterEntity entity1 = FastTravel.TELEPORTER_ENTITY.get(world, pos);
                if (player_ext.hasSelection() && !player_ext.getSelection().equals(entity1)) {

                    link(entity1, player_ext.getSelection());
                    player_ext.resetSelection();

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1f, 1f);
                } else {
                    if (player_ext.getSelection() == entity1) {
                        player_ext.resetSelection();
                        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                    } else {
                        player_ext.setSelection(entity1);
                        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                    }
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TeleporterEntity();
    }


    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(world.getBlockState(pos).get(LINKED)){
            for(int i = 0; i < 2; ++i) {
                double x = (double)pos.getX() + random.nextDouble();
                double y = (double)pos.getY() + getYOffset(state) + 0.25D;
                double z = (double)pos.getZ() + random.nextDouble();
                double Vx = ((double)random.nextFloat() - 0.5D) * 0.5D;
                double Vy = (random.nextFloat() * 2.0F);
                double Vz = ((double)random.nextFloat() - 0.5D) * 0.5D;

                world.addParticle(ParticleTypes.PORTAL, x, y, z, Vx, Vy, Vz);
            }
        }

    }

}
