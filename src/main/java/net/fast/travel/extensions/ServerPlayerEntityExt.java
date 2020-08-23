package net.fast.travel.extensions;

import net.fast.travel.blocks.TeleporterEntity;

public interface ServerPlayerEntityExt extends EntityExt{

    TeleporterEntity getSelection();
    void setSelection(TeleporterEntity teleporterEntity);
    void resetSelection();
    boolean hasSelection();

}
