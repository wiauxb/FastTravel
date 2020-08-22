package net.fast.travel.mixin;

import net.fast.travel.ServerPlayerEntityExt;
import net.fast.travel.TeleporterEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends EntityMixin implements ServerPlayerEntityExt {

    private TeleporterEntity selection;

    public TeleporterEntity getSelection() {
        return selection;
    }

    public boolean hasSelection(){
        return selection != null;
    }

    public void setSelection(TeleporterEntity selection) {
        this.selection = selection;
        this.selection.getWorld().getChunkManager().setChunkForced(new ChunkPos(selection.getPos()), true);
    }

    public void resetSelection() {
        if (selection != null) {
            selection.getWorld().getChunkManager().setChunkForced(new ChunkPos(selection.getPos()), false);
            selection = null;
        }
    }

    @Inject(at = @At("HEAD"), method = "onDisconnect")
    public void removeSelection(CallbackInfo callbackInfo){
        resetSelection();
    }

}
