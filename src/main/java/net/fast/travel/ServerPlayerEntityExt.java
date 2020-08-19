package net.fast.travel;

public interface ServerPlayerEntityExt extends EntityExt{

    TeleporterEntity getSelection();
    void setSelection(TeleporterEntity teleporterEntity);
    void resetSelection();
    boolean hasSelection();

}
