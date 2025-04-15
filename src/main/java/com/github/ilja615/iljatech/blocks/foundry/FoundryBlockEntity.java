package com.github.ilja615.iljatech.blocks.foundry;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.firebox.FireboxBlock;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.github.ilja615.iljatech.util.CountedIngredient;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.klikli_dev.modonomicon.api.ModonomiconAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FoundryBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {
    private int ticks = 0;
    private int maxTicks = 0;
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".foundry");

    private final SimpleInventory inventory = new SimpleInventory(5) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory,null);

    public FoundryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FOUNDRY, pos, state);
    }

    @Override
    public void tick() {
        Direction facing = world.getBlockState(pos).get(FoundryBlock.FACING);
        FireboxBlock.Lit lit = validateHeatMultiblock();
        if (validateFoundryMultiblock()) {
            if (lit != FireboxBlock.Lit.OFF) {
                List<RecipeEntry<FoundryRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.FOUNDRY_TYPE);
                for (RecipeEntry<FoundryRecipe> rr : recipes)
                {
                    FoundryRecipe r = rr.value();
                    // Gets the first 2 items and see if it matches with the recipe (second index is excl.)
                    if (r.matches(new FoundryRecipe.InputContainer(inventory.getHeldStacks().subList(0, 2), inventory.getHeldStacks().get(2)), world)) {
                        // Display particles
                        if (!world.isClient) {
                            double x = pos.getX() + 0.5d + (facing.getAxis() == Direction.Axis.X ? 0.52 * facing.getOffsetX() : world.random.nextDouble() * 0.6 - 0.3);
                            double y = pos.getY() + 0.3125d + world.random.nextDouble() * 6.0d / 16.0d;
                            double z = pos.getZ() + 0.5d + (facing.getAxis() == Direction.Axis.Z ? 0.52 * facing.getOffsetZ() : world.random.nextDouble() * 0.6 - 0.3);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.0f, 0.3f, 0.0f, 0.0);
                        }
                        // Update the processing time
                        this.maxTicks = r.processingTime();

                        ItemStack output = r.output().copy();
                        if (!output.isEmpty()) {
                            if (ticks++ > r.processingTime()) {
                                // The recipe is finished. The output is handled.
                                if (inventory.getStack(3).isEmpty()) { // 3 is output slot
                                    // In this case a new result ItemStack is added with 1 of the result.
                                    for (CountedIngredient ci : r.ingredients()) {
                                        inventory.getHeldStacks().subList(0, 2).forEach(itemStack -> {
                                            if (itemStack.isOf(ci.getMatchingStacks().get(0).getItem())) {
                                                itemStack.decrement(ci.count());
                                            }
                                        });
                                    }
                                    inventory.setStack(3, output);
                                } else if (inventory.getStack(3).getItem() == output.getItem() &&
                                    inventory.getStack(3).getCount() + output.getCount() <= inventory.getStack(3).getMaxCount()) {
                                    // In this case the result ItemStack is added to what was already there
                                    for (CountedIngredient ci : r.ingredients()) {
                                        inventory.getHeldStacks().subList(0, 2).forEach(itemStack -> {
                                            if (itemStack.isOf(ci.getMatchingStacks().get(0).getItem())) {
                                                itemStack.decrement(ci.count());
                                            }
                                        });
                                    }
                                    inventory.getStack(3).increment(output.getCount());
                                }

                                // Subtracting the flux
                                boolean providedFlux = r.flux().getMatchingStacks().stream().map(ItemStack::getItem).toList().contains(inventory.getStack(2).getItem());
                                inventory.getStack(2).decrement(r.flux().count()); // 2 is flux slot

                                float slagChance = providedFlux ? r.slagChanceUsingFlux() : r.slagChanceWithoutFlux();
                                if (world.random.nextFloat() <= slagChance) {
                                    ItemStack slag = r.slag().copy();
                                    if (inventory.getStack(4).isEmpty()) { // 4 is slag slot
                                        inventory.setStack(4, slag);
                                    } else if (inventory.getStack(4).getItem() == slag.getItem() &&
                                            inventory.getStack(4).getCount() + slag.getCount() <= inventory.getStack(4).getMaxCount()) {
                                        inventory.getStack(4).increment(slag.getCount());
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
        Direction facing = world.getBlockState(pos).get(FoundryBlock.FACING);
        BlockPos startPos = pos.offset(facing.getOpposite()).down();
        return (ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "foundry")).validate(world, startPos) != null);
    }

    public FireboxBlock.Lit validateHeatMultiblock() {
        Direction facing = world.getBlockState(pos).get(FoundryBlock.FACING);
        if (world.getBlockState(pos.down(2)).getBlock() instanceof FireboxBlock) {
            if (world.getBlockState(pos.down(2)).get(FireboxBlock.FACING) == facing) {
                BlockPos startPos = pos.offset(facing.getOpposite()).down(2);
                if (ModonomiconAPI.get().getMultiblock(Identifier.of(IljaTech.MOD_ID, "large_firebox")).validate(world, startPos) != null) {
                    return world.getBlockState(pos.down(2)).get(FireboxBlock.LIT);
                }
            }
        }
        return FireboxBlock.Lit.OFF;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.ticks = nbt.getInt("Ticks");
        this.maxTicks = nbt.getInt("MaxTicks");
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Ticks", this.ticks);
        nbt.putInt("MaxTicks", this.maxTicks);
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

    public int getMaxTicks() {
        return maxTicks;
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
        return new FoundryScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}
