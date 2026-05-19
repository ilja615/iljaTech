package com.github.ilja615.iljatech.blocks.pulverizermill;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.init.ModSounds;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PulverizerMillBlockEntity  extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    // TODO: Might change it to SidedInventory later
    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public PulverizerMillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.PULVERIZER_MILL , pos, state);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide())
            return;

        ItemStack stack0 = this.inventory.getItem(0);
        if (stack0.isEmpty()) {
            this.ticks = 0;
        }
        List<RecipeHolder<PulverizingRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.PULVERIZING_TYPE);
        if (ticks > 0 && ticks % 10 == 0) {
            for (RecipeHolder<PulverizingRecipe> rr : recipes)
            {
                PulverizingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();
                if (r.stack().getItems()[0].isEmpty())
                    continue;

                if (r.stack().getItems()[0].getItem() == stack0.getItem())
                {
                    level.playSound(null, worldPosition, ModSounds.ORE_CRUSHING, SoundSource.BLOCKS, 1.0f, 1.0f);
                    break;
                }
            }
        }
        if (ticks++ > 100) {
            this.ticks = 0;

            Direction direction = this.getBlockState().getValue(PulverizerMillBlock.FACING);
            for (RecipeHolder<PulverizingRecipe> rr : recipes)
            {
                PulverizingRecipe r = rr.value();
                ItemStack resultingStack = r.output().copy();
                if (r.stack().getItems()[0].isEmpty())
                    continue;

                if (r.stack().getItems()[0].getItem() == stack0.getItem())
                {
                    this.inventory.getItem(0).shrink(1);
                    Vec3 outputPos = worldPosition.relative(direction, 2).getCenter();
                    level.addFreshEntity(new ItemEntity(level, outputPos.x(), outputPos.y(), outputPos.z(), resultingStack, direction.getStepX() * 0.1f, 0, direction.getStepZ() * 0.1f));
                    ((ServerLevel) this.level).getChunkSource().blockChanged(this. getBlockPos());
                    this.update();
                    break;
                }
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        ContainerHelper.loadAllItems(nbt, this.inventory.getItems(), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        ContainerHelper.saveAllItems(nbt, this.inventory.getItems(), registryLookup);
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
}
