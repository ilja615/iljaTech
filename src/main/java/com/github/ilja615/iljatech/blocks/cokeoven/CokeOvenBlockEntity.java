package com.github.ilja615.iljatech.blocks.cokeoven;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.foundry.FoundryBlock;
import com.github.ilja615.iljatech.blocks.foundry.FoundryRecipe;
import com.github.ilja615.iljatech.energy.BoilingRecipe;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CokeOvenBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".coke_oven");
    private final SimpleInventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public CokeOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COKE_OVEN, pos, state);
    }

    @Override
    public void tick() {
        if (validateCokeOvenMultiblock()) {
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
                        if (ticks++ > 100) {
                            // The recipe is finished. The output is handled.
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
        return (ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "coke_oven")).validate(world, pos) != null);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
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
        return new CokeOvenScreenHandler(syncId, playerInventory, this);
    }
}
