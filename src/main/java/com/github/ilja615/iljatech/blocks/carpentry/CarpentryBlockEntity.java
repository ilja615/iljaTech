package com.github.ilja615.iljatech.blocks.carpentry;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.SawDustBlock;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModSounds;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CarpentryBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    public static final Text TITLE = Text.translatable("container." + IljaTech.MOD_ID + ".carpentry");
    private int layout = 0;

    private final SimpleInventory inventory = new SimpleInventory(7) {
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

    public CarpentryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CARPENTRY, pos, state);
    }

    protected void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public void hammer() {
        this.setLayout(0);
        for (int i = 0; i <= 3; i++) {
            if (inventory.getStack(i).isIn(ItemTags.PLANKS)) {
                if (!inventory.getStack(4).isEmpty() && inventory.getStack(4).isOf(ModItems.IRON_NAILS)) {
                    String key = inventory.getStack(i).getItem().getTranslationKey();
                    String name = key.substring(key.lastIndexOf(".") + 1);
                    Identifier id = Identifier.of(IljaTech.MOD_ID, "nailed_" + name);

                    world.playSound(null, pos, ModSounds.HAMMER, SoundCategory.PLAYERS, 1f, 1f);

                    inventory.setStack(i, new ItemStack(Registries.BLOCK.get(id).asItem(), 1));
                    inventory.getStack(4).decrement(1);
                    break;
                }
            }
        }
    }

    public void saw() {
        this.setLayout(1);
        for (int i = 0; i <= 3; i++) {
            if (inventory.getStack(i).isIn(ItemTags.PLANKS)) {
                String key = inventory.getStack(i).getItem().getTranslationKey();
                String name = key.substring(key.lastIndexOf(".") + 1, key.length() - 7); // Removre also the last 7 characters: "_planks"
                Identifier id = Identifier.of(IljaTech.MOD_ID, "frame_" + name);

                world.playSound(null, pos, ModSounds.SAW, SoundCategory.PLAYERS, 1f, 1f);

                createSawdust(world, pos);
                inventory.setStack(i, new ItemStack(Registries.BLOCK.get(id).asItem(), 1));
                break;
            }
        }
    }

    public static boolean createSawdust (World world, BlockPos pos) {
        return createSawdust(world, pos, Direction.Type.HORIZONTAL.random(world.random), 0.35f);
    }

    public static boolean createSawdust (World world, BlockPos pos, Direction direction, float chance) {
        BlockState state = world.getBlockState(pos.offset(direction));
        if (state.isAir() || state.isReplaceable()) {
            world.setBlockState(pos.offset(direction), ModBlocks.SAWDUST.getDefaultState());
            return true;
        }
        if (state.isOf(ModBlocks.SAWDUST)) {
            int level = state.get(SawDustBlock.LEVEL);
            if (level < 3) {
                world.setBlockState(pos.offset(direction), ModBlocks.SAWDUST.getDefaultState().with(SawDustBlock.LEVEL, Math.min(3, level+1)));
                return true;
            }
        }
        return false;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.layout = nbt.getInt("Layout");

        if (nbt.contains("Inventory", NbtElement.COMPOUND_TYPE))
            Inventories.readNbt(nbt.getCompound("Inventory"), this.inventory.getHeldStacks(), registryLookup);

        if (nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Layout", this.layout);

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

    public int getLayout() {
        return this.layout;
    }

    public void setLayout(int layout) {
        if (layout >= 0 && layout < 3) {
            this.layout = layout;
            this.update();
        }
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
        return new CarpentryScreenHandler(syncId, playerInventory, this, this.inventory);
    }
}