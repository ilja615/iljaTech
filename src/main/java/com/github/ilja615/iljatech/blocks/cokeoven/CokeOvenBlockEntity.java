package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlock;
import com.github.ilja615.iljatech.blocks.foundry.FoundryRecipe;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchBlockEntity;
import com.github.ilja615.iljatech.energy.BoilingRecipe;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModFluids;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class CokeOvenBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".coke_oven");
    public static final int PROCESS_TIME = 12000; // 12000 ticks is ten minutes
    private final SimpleContainer inventory = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(
            FluidConstants.BUCKET * 16,
            this::update);
    private final ContainerItemContext fluidItemContext = ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(2));

    public CokeOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COKE_OVEN, pos, state);
    }

    @Override
    public void tick() {
        if (validateCokeOvenMultiblock()) {
            // Transfer items from the item hatch if there is one
            Direction facing = level.getBlockState(worldPosition).getValue(CokeOvenBlock.FACING);
            if (level.getBlockEntity(worldPosition.relative(facing.getOpposite()).above(2)) instanceof ItemHatchBlockEntity itemHatch) {
                for (ItemStack stack : itemHatch.getInventory().getItems()) {
                    if (!stack.isEmpty()) {
                        if (inventory.getItem(0).isEmpty()) {
                            inventory.setItem(0, new ItemStack(stack.getItem(), 1));
                            stack.shrink(1);
                            break;
                        } else if (inventory.getItem(0).is(stack.getItem())) {
                            inventory.getItem(0).grow(1);
                            stack.shrink(1);
                            break;
                        }
                    }
                }
            }

            // Transfer fluids to the bucket or item if there is one
            Storage<FluidVariant> itemFluidStorage  = this.fluidItemContext.find(FluidStorage.ITEM);
            if (itemFluidStorage != null && fluidStorage.amount >= FluidConstants.BUCKET) {
                long acceptedAmount = 0;
                long transferredAmount = 0;
                try(Transaction transaction = Transaction.openOuter()) {
                    acceptedAmount = itemFluidStorage.insert(FluidVariant.of(ModFluids.STILL_CREOSOTE_OIL), FluidConstants.BUCKET, transaction);
                    System.out.println(acceptedAmount);
                    if (acceptedAmount == FluidConstants.BUCKET) {
                        transferredAmount = fluidStorage.extract(FluidVariant.of(ModFluids.STILL_CREOSOTE_OIL), FluidConstants.BUCKET, transaction);
                        System.out.println(transferredAmount);
                        if (transferredAmount == FluidConstants.BUCKET) {
                            transaction.commit();
                            update();
                        }
                    }
                }
            }

            List<RecipeHolder<CokingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COKING_TYPE);
            boolean flag = false;
            for (RecipeHolder<CokingRecipe> rr : recipes)
            {
                CokingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();

                if (r.matches(new CokingRecipe.InputContainer(inventory.getItem(0)), level))
                {
                    level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(CokeOvenBlock.LIT, true));
                    ItemStack output = r.output().copy();
                    if (!output.isEmpty()) {
                        if (ticks++ > PROCESS_TIME) {
                            // The recipe is finished. The output is handled.
                            long insertedAmount = 0;
                            try(Transaction transaction = Transaction.openOuter()) {
                                insertedAmount = fluidStorage.insert(FluidVariant.of(ModFluids.STILL_CREOSOTE_OIL), r.fluidAmount(), transaction);
                                if (insertedAmount > 0) {
                                    transaction.commit();
                                    update();
                                }
                            }
                            if (insertedAmount > 0) {
                                if (inventory.getItem(1).isEmpty()) { // 1 is output slot
                                    // In this case a new result ItemStack is added with 1 of the result.
                                    inventory.getItem(0).shrink(r.countedIngredient().count());
                                    inventory.setItem(1, output);
                                } else if (inventory.getItem(1).getItem() == output.getItem() &&
                                        inventory.getItem(1).getCount() + output.getCount() <= inventory.getItem(1).getMaxStackSize()) {
                                    // In this case the result ItemStack is added to what was already there
                                    inventory.getItem(0).shrink(r.countedIngredient().count());
                                    inventory.getItem(1).grow(output.getCount());
                                }
                                this.ticks = 0;
                            }
                        }
                        flag = true;
                    }
                    break;
                }
            }
            if (!flag)
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(CokeOvenBlock.LIT, false));
        } else {
            // Progress resets if Multiblock becomes invalidated
            this.ticks = 0;
        }
        update();
    }

    public boolean validateCokeOvenMultiblock() {
        Rotation rotation = ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "coke_oven")).validate(level, worldPosition);
        Direction facing = level.getBlockState(worldPosition).getValue(CokeOvenBlock.FACING);
        if (rotation != null) {
            return (ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "coke_oven_wall")).validate(level, worldPosition.relative(facing.getClockWise()), rotation)
                    || ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "coke_oven")).validate(level, worldPosition.relative(facing.getClockWise()), rotation))
                    && (ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "coke_oven_wall")).validate(level, worldPosition.relative(facing.getCounterClockWise()), rotation)
                    || ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "coke_oven")).validate(level, worldPosition.relative(facing.getCounterClockWise()), rotation));
        }
        return false;
    }

    public boolean isValid(ItemStack stack, int slot) {
        if(stack.isEmpty()) return true;

        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        if (nbt.contains("Inventory", Tag.TAG_COMPOUND))
            ContainerHelper.loadAllItems(nbt.getCompound("Inventory"), this.inventory.getItems(), registryLookup);

        if (nbt.contains("FluidTank", Tag.TAG_COMPOUND))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);

        var inventoryNbt = new CompoundTag();
        ContainerHelper.saveAllItems(inventoryNbt, this.inventory.getItems(), registryLookup);
        nbt.put("Inventory", inventoryNbt);

        var fluidNbt = new CompoundTag();
        this.fluidStorage.writeNbt(fluidNbt, registryLookup);
        nbt.put("FluidTank", fluidNbt);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        var nbt = super.getUpdateTag(registryLookup);
        saveAdditional(nbt, registryLookup);
        return nbt;
    }

    public int getTicks() {
        return ticks;
    }

    private void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayer player) {
        return new BlockPosPayload(this.worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new CokeOvenScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
