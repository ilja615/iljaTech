package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundryBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    private int maxTicks = 0;
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".foundry");

    private final SimpleContainer inventory = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public FoundryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FOUNDRY, pos, state);
    }

    @Override
    public void tick() {
        Direction facing = level.getBlockState(worldPosition).getValue(FoundryBlock.FACING);
        FireboxBlock.Lit lit = validateHeatMultiblock();
        if (validateFoundryMultiblock()) {
            if (lit != FireboxBlock.Lit.OFF) {
                List<RecipeHolder<FoundryRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FOUNDRY_TYPE);
                for (RecipeHolder<FoundryRecipe> rr : recipes)
                {
                    FoundryRecipe r = rr.value();
                    // Gets the first 2 items and see if it matches with the recipe (second index is excl.)
                    if (r.matches(new FoundryRecipe.InputContainer(inventory.getItems().subList(0, 2), inventory.getItems().get(2)), level)) {
                        // Display particles
                        if (!level.isClientSide) {
                            double x = worldPosition.getX() + 0.5d + (facing.getAxis() == Direction.Axis.X ? 0.52 * facing.getStepX() : level.random.nextDouble() * 0.6 - 0.3);
                            double y = worldPosition.getY() + 0.3125d + level.random.nextDouble() * 6.0d / 16.0d;
                            double z = worldPosition.getZ() + 0.5d + (facing.getAxis() == Direction.Axis.Z ? 0.52 * facing.getStepZ() : level.random.nextDouble() * 0.6 - 0.3);
                            ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0f, 0.3f, 0.0f, 0.0);
                        }
                        // Update the processing time
                        this.maxTicks = r.processingTime();

                        ItemStack output = r.output().copy();
                        if (!output.isEmpty()) {
                            if (ticks++ > r.processingTime()) {
                                // The recipe is finished. The output is handled.
                                if (inventory.getItem(3).isEmpty()) { // 3 is output slot
                                    // In this case a new result ItemStack is added with 1 of the result.
                                    for (CountedIngredient ci : r.ingredients()) {
                                        inventory.getItems().subList(0, 2).forEach(itemStack -> {
                                            if (itemStack.is(ci.getMatchingStacks().get(0).getItem())) {
                                                itemStack.shrink(ci.count());
                                            }
                                        });
                                    }
                                    inventory.setItem(3, output);
                                } else if (inventory.getItem(3).getItem() == output.getItem() &&
                                    inventory.getItem(3).getCount() + output.getCount() <= inventory.getItem(3).getMaxStackSize()) {
                                    // In this case the result ItemStack is added to what was already there
                                    for (CountedIngredient ci : r.ingredients()) {
                                        inventory.getItems().subList(0, 2).forEach(itemStack -> {
                                            if (itemStack.is(ci.getMatchingStacks().get(0).getItem())) {
                                                itemStack.shrink(ci.count());
                                            }
                                        });
                                    }
                                    inventory.getItem(3).grow(output.getCount());
                                }

                                // Subtracting the flux
                                boolean providedFlux = r.flux().getMatchingStacks().stream().map(ItemStack::getItem).toList().contains(inventory.getItem(2).getItem());
                                inventory.getItem(2).shrink(r.flux().count()); // 2 is flux slot

                                float slagChance = providedFlux ? r.slagChanceUsingFlux() : r.slagChanceWithoutFlux();
                                if (level.random.nextFloat() <= slagChance) {
                                    ItemStack slag = r.slag().copy();
                                    if (inventory.getItem(4).isEmpty()) { // 4 is slag slot
                                        inventory.setItem(4, slag);
                                    } else if (inventory.getItem(4).getItem() == slag.getItem() &&
                                            inventory.getItem(4).getCount() + slag.getCount() <= inventory.getItem(4).getMaxStackSize()) {
                                        inventory.getItem(4).grow(slag.getCount());
                                    }
                                }
                                this.ticks = 0;
                                this.maxTicks = 0;
                            }
                        }
                        break;
                    }
                }
            } else {
                if (ticks > 2) {
                    // Progress reverts if not heated
                    this.ticks -= 2;
                }
            }
        } else {
            // Progress resets if Multiblock becomes invalidated
            this.ticks = 0;
        }
        update();
    }

    public boolean validateFoundryMultiblock() {
        Direction facing = level.getBlockState(worldPosition).getValue(FoundryBlock.FACING);
        BlockPos startPos = worldPosition.relative(facing.getOpposite()).below();
        return (ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "foundry")).validate(level, startPos) != null);
    }

    public FireboxBlock.Lit validateHeatMultiblock() {
        Direction facing = level.getBlockState(worldPosition).getValue(FoundryBlock.FACING);
        if (level.getBlockState(worldPosition.below(2)).getBlock() instanceof FireboxBlock) {
            if (level.getBlockState(worldPosition.below(2)).getValue(FireboxBlock.FACING) == facing) {
                BlockPos startPos = worldPosition.relative(facing.getOpposite()).below(2);
                if (ModonomiconAPI.get().getMultiblock(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "large_firebox")).validate(level, startPos) != null) {
                    return level.getBlockState(worldPosition.below(2)).getValue(FireboxBlock.LIT);
                }
            }
        }
        return FireboxBlock.Lit.OFF;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        this.maxTicks = nbt.getInt("MaxTicks");
        ContainerHelper.loadAllItems(nbt, this.inventory.getItems(), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        nbt.putInt("MaxTicks", this.maxTicks);
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

    public int getMaxTicks() {
        return maxTicks;
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
        return new FoundryScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
