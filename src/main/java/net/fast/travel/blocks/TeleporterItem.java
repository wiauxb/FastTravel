package net.fast.travel.blocks;

import net.fast.travel.FastTravel;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;

public class TeleporterItem extends BlockItem {

    public TeleporterItem() {
        super(FastTravel.TELEPORTER, new Item.Settings()
                .group(ItemGroup.MISC)
                .maxCount(8));
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).isOf(FastTravel.TELEPORTER)) {
            return ActionResult.FAIL;
        }
        return super.useOnBlock(context);
    }
}
