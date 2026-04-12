package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.SawDustBlock;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CarpentryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".carpentry");
    private String craftingStatus = "No recipe match.";

    private final SimpleContainer inventory = new SimpleContainer(7) {
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

    public CarpentryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CARPENTRY, pos, state);
    }

    protected void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void hammer() {
        // Planks to nailed boards conversion
        for (int i = 0; i <= 3; i++) {
            if (inventory.getItem(i).is(ItemTags.PLANKS)) {
                if (!inventory.getItem(4).isEmpty() && inventory.getItem(4).is(ModItems.IRON_NAILS)) {
                    String key = inventory.getItem(i).getItem().getDescriptionId();
                    String name = key.substring(key.lastIndexOf(".") + 1);
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "nailed_" + name);

                    level.playSound(null, worldPosition, ModSounds.HAMMER, SoundSource.PLAYERS, 1f, 1f);

                    inventory.setItem(i, new ItemStack(BuiltInRegistries.BLOCK.get(id).asItem(), 1));
                    inventory.getItem(4).shrink(1);
                    checkRecipes();
                    break;
                }
            }
        }
    }

    public void saw() {
        // Planks to frames conversion
        for (int i = 0; i <= 3; i++) {
            if (inventory.getItem(i).is(ItemTags.PLANKS)) {
                String key = inventory.getItem(i).getItem().getDescriptionId();
                String name = key.substring(key.lastIndexOf(".") + 1, key.length() - 7); // Removre also the last 7 characters: "_planks"
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "frame_" + name);

                level.playSound(null, worldPosition, ModSounds.SAW, SoundSource.PLAYERS, 1f, 1f);

                createSawdust(level, worldPosition);
                inventory.setItem(i, new ItemStack(BuiltInRegistries.BLOCK.get(id).asItem(), 1));
                checkRecipes();
                break;
            }
        }
    }

    public CarpentryRecipe checkRecipes() {
        if (this.level == null || this.level.isClientSide)
            return null;
        CarpentryRecipe checkedRecipe = null;
        // Recipe detection and handling
        CraftingInput input = CraftingInput.of(2, 2, Arrays.asList(
                inventory.getItem(0), inventory.getItem(1), inventory.getItem(2), inventory.getItem(3)
        ));
        List<RecipeHolder<CarpentryRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CARPENTRY_TYPE);
        for (RecipeHolder<CarpentryRecipe> rr : recipes)
        {
            CarpentryRecipe r = rr.value();

            ItemStack output = r.result().copy();

            if (!output.isEmpty() && r.matches(input, level)) {
                if (fluidStorage.amount >= r.fluidAmount() && (fluidStorage.variant.getFluid().isSame(ModFluids.STILL_CREOSOTE_OIL) || fluidStorage.variant.getFluid().isSame(ModFluids.STILL_SEED_OIL))) {
                    checkedRecipe = r;
                    this.craftingStatus = "";
                } else {
                    this.craftingStatus = "Needs "+(int) (r.fluidAmount() * 1000.0f / FluidConstants.BUCKET)+" mB oil.";
                    update();
                    return null;
                }

                break;
            }
        }
        if (checkedRecipe == null) {
            this.craftingStatus = "No recipe match.";
        }
        update();
        return checkedRecipe;
    }

    public void finish() {
        CarpentryRecipe r = checkRecipes();
        if (r != null) {
            ItemStack output = r.result().copy();
            // The recipe is finished. The output is handled.
            if (inventory.getItem(5).isEmpty()) { // 3 is output slot
                // In this case a new result ItemStack is added with 1 of the result.
                inventory.setItem(5, output);
                inventory.getItems().subList(0, 4).forEach(itemStack -> {
                    itemStack.shrink(1);
                });
                fluidStorage.amount -= r.fluidAmount();
                this.craftingStatus = "No recipe match.";
                update();
            } else if (inventory.getItem(5).getItem() == output.getItem() &&
                    inventory.getItem(5).getCount() + output.getCount() <= inventory.getItem(5).getMaxStackSize()) {
                // In this case the result ItemStack is added to what was already there
                inventory.getItem(5).grow(output.getCount());
                inventory.getItems().subList(0, 4).forEach(itemStack -> {
                    itemStack.shrink(1);
                });
                fluidStorage.amount -= r.fluidAmount();
                this.craftingStatus = "No recipe match.";
                update();
            }
        }
    }

    public static boolean createSawdust (Level world, BlockPos pos) {
        return createSawdust(world, pos, Direction.Plane.HORIZONTAL.getRandomDirection(world.random), 0.35f);
    }

    public static boolean createSawdust (Level world, BlockPos pos, Direction direction, float chance) {
        if (world.random.nextFloat() > chance)
            return false;
        BlockState state = world.getBlockState(pos.relative(direction));
        if (state.isAir() || state.canBeReplaced()) {
            world.setBlockAndUpdate(pos.relative(direction), ModBlocks.SAWDUST.defaultBlockState());
            return true;
        }
        if (state.is(ModBlocks.SAWDUST)) {
            int level = state.getValue(SawDustBlock.LEVEL);
            if (level < 3) {
                world.setBlockAndUpdate(pos.relative(direction), ModBlocks.SAWDUST.defaultBlockState().setValue(SawDustBlock.LEVEL, Math.min(3, level+1)));
                return true;
            }
        }
        return false;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        this.craftingStatus = nbt.getString("CraftingStatus");

        if (nbt.contains("Inventory", Tag.TAG_COMPOUND))
            ContainerHelper.loadAllItems(nbt.getCompound("Inventory"), this.inventory.getItems(), registryLookup);

        if (nbt.contains("FluidTank", Tag.TAG_COMPOUND))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        nbt.putString("CraftingStatus", this.craftingStatus);

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
        return new CarpentryScreenHandler(syncId, playerInventory, this, this.inventory);
    }

    public String getCraftingStatus() {
        return craftingStatus;
    }
}