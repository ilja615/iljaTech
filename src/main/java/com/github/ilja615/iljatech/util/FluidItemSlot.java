package com.github.ilja615.iljatech.util;

import com.github.ilja615.iljatech.init.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FluidItemSlot extends Slot {
    private final ContainerItemContext fluidItemContext;
    private final SingleFluidStorage fluidStorage;
    private final Runnable onChange;

    public FluidItemSlot(Inventory inventory, SingleFluidStorage fluidStorage, Runnable onChange, int index, int x, int y) {
        super(inventory, index, x, y);

        this.onChange = onChange;
        this.fluidStorage = fluidStorage;
        this.fluidItemContext = ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(index));
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return isValid(stack, 6);
    }

    public boolean isValid(ItemStack stack, int slot) {
        if(stack.isEmpty()) return true;

        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null;
    }

    public void onSlotUpdate(ItemStack stack) {
        System.out.println("insert stack");
        Storage<FluidVariant> itemFluidStorage  = this.fluidItemContext.find(FluidStorage.ITEM);
//        if (itemFluidStorage != null && fluidStorage.amount >= FluidConstants.BUCKET) {
//            long acceptedAmount = 0;
//            long transferredAmount = 0;
//            try(Transaction transaction = Transaction.openOuter()) {
//                acceptedAmount = itemFluidStorage.insert(FluidVariant.of(ModFluids.STILL_SEED_OIL), FluidConstants.BUCKET, transaction);
//                System.out.println(acceptedAmount);
//                if (acceptedAmount == FluidConstants.BUCKET) {
//                    transferredAmount = fluidStorage.extract(FluidVariant.of(ModFluids.STILL_SEED_OIL), FluidConstants.BUCKET, transaction);
//                    System.out.println(transferredAmount);
//                    if (transferredAmount == FluidConstants.BUCKET) {
//                        transaction.commit();
//                        onChange.run();
//                    }
//                }
//            }
//        }
        if (itemFluidStorage != null && itemFluidStorage.iterator().hasNext()) {
            itemFluidStorage.iterator().forEachRemaining(fluidVariantStorageView -> {
                long acceptedAmount = 0;
                long transferredAmount = 0;
                try(Transaction transaction = Transaction.openOuter()) {
                    acceptedAmount = this.fluidStorage.insert(fluidVariantStorageView.getResource(), Math.max(fluidVariantStorageView.getAmount(), FluidConstants.BUCKET), transaction);
                    System.out.println(acceptedAmount);
                    transferredAmount = itemFluidStorage.extract(fluidVariantStorageView.getResource(), acceptedAmount, transaction);
                    System.out.println(transferredAmount);
                    if (transferredAmount == acceptedAmount) {
                        transaction.commit();
                        onChange.run();
                    }
                }

            });

        }
    }
}
