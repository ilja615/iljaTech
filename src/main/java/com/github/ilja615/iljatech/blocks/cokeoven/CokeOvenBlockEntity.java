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
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CokeOvenBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".coke_oven");
    public static final int PROCESS_TIME = 120; // Ten minutes
    private final SimpleInventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(
            FluidConstants.BUCKET * 16,
            this::update);

    public CokeOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COKE_OVEN, pos, state);
    }

    @Override
    public void tick() {
        if (validateCokeOvenMultiblock()) {
            // Transfer items from the item hatch if there is one
            Direction facing = world.getBlockState(pos).get(CokeOvenBlock.FACING);
            if (world.getBlockEntity(pos.offset(facing.getOpposite()).up(2)) instanceof ItemHatchBlockEntity itemHatch) {
                for (ItemStack stack : itemHatch.getInventory().getHeldStacks()) {
                    if (!stack.isEmpty()) {
                        if (inventory.getStack(0).isEmpty()) {
                            inventory.setStack(0, new ItemStack(stack.getItem(), 1));
                            stack.decrement(1);
                            break;
                        } else if (inventory.getStack(0).isOf(stack.getItem())) {
                            inventory.getStack(0).increment(1);
                            stack.decrement(1);
                            break;
                        }
                    }
                }
            }

            List<RecipeEntry<CokingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.COKING_TYPE);
            boolean flag = false;
            for (RecipeEntry<CokingRecipe> rr : recipes)
            {
                CokingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();

                if (r.matches(new CokingRecipe.InputContainer(inventory.getStack(0)), world))
                {
                    world.setBlockState(pos, world.getBlockState(pos).with(CokeOvenBlock.LIT, true));
                    ItemStack output = r.output().copy();
                    if (!output.isEmpty()) {
                        if (ticks++ > PROCESS_TIME) {
                            // The recipe is finished. The output is handled.
                            long insertedAmount = 0;
                            try(Transaction transaction = Transaction.openOuter()) {
                                insertedAmount = fluidStorage.insert(FluidVariant.of(ModFluids.STILL_CREOSOTE_OIL), FluidConstants.BUCKET/10, transaction);
                            }
                            if (insertedAmount > 0) {
                                if (inventory.getStack(1).isEmpty()) { // 1 is output slot
                                    // In this case a new result ItemStack is added with 1 of the result.
                                    inventory.getStack(0).decrement(1);
                                    inventory.setStack(1, output);
                                } else if (inventory.getStack(1).getItem() == output.getItem() &&
                                        inventory.getStack(1).getCount() + output.getCount() <= inventory.getStack(1).getMaxCount()) {
                                    // In this case the result ItemStack is added to what was already there
                                    inventory.getStack(0).decrement(1);
                                    inventory.getStack(1).increment(output.getCount());
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
                world.setBlockState(pos, world.getBlockState(pos).with(CokeOvenBlock.LIT, false));
        } else {
            // Progress resets if Multiblock becomes invalidated
            this.ticks = 0;
        }
        update();
    }

    public boolean validateCokeOvenMultiblock() {
        BlockRotation rotation = ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven")).validate(world, pos);
        Direction facing = world.getBlockState(pos).get(CokeOvenBlock.FACING);
        if (rotation != null) {
            return (ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven_wall")).validate(world, pos.offset(facing.rotateYClockwise()), rotation)
                    || ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven")).validate(world, pos.offset(facing.rotateYClockwise()), rotation))
                    && (ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven_wall")).validate(world, pos.offset(facing.rotateYCounterclockwise()), rotation)
                    || ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven")).validate(world, pos.offset(facing.rotateYCounterclockwise()), rotation));
        }
        return false;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");

        if (nbt.contains("Inventory", NbtElement.COMPOUND_TYPE))
            Inventories.readNbt(nbt.getCompound("Inventory"), this.inventory.getHeldStacks(), registryLookup);

        if (nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);

        var inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.put("Inventory", inventoryNbt);

        var fluidNbt = new NbtCompound();
        this.fluidStorage.writeNbt(fluidNbt, registryLookup);
        nbt.put("FluidTank", fluidNbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    public int getTicks() {
        return ticks;
    }

    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return storage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CokeOvenScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
