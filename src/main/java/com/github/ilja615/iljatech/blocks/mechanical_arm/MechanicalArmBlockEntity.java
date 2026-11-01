package com.github.ilja615.iljatech.blocks.mechanical_arm;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlock;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.github.ilja615.iljatech.util.TickableBlockEntity;


public class MechanicalArmBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload> {
    public static final Component TITLE = Component.translatable("container." + IljaTech.MOD_ID + ".mechanical_arm");
    private boolean mouseRightClickToggle = true;
    private int ticks = 0;

    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            update();
        }
    };
    private final InventoryStorage storage = InventoryStorage.of(inventory, null);

    public MechanicalArmBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MECHANICAL_ARM, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.mouseRightClickToggle = nbt.getBoolean("MouseSide");
        ContainerHelper.loadAllItems(nbt, this.inventory.getItems(), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putBoolean("MouseSide", this.mouseRightClickToggle);

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
        return new MechanicalArmScreenHandler(syncId, playerInventory, this, this.inventory);
    }


//    @Override
//    public void tick() {
//        if (this.level == null || this.level.isClientSide())
//            return;
//
//        ItemStack stack0 = this.inventory.getItem(0);
//        if (stack0.isEmpty()) {
//            this.ticks = 0;
//        }else if (ticks++ >= 20) {
//            this.ticks = 0;
//
//            Direction direction = this.getBlockState().getValue(RollerMillBlock.FACING);
//        }
//    }

    public void toggleMouseSide() {
        this.mouseRightClickToggle = !this.mouseRightClickToggle;
        update();
    }

    public boolean getMouseSide() {
        return mouseRightClickToggle;
    }

    private FakePlayer getFakePlayer() {
        if (!(this.level instanceof ServerLevel serverWorld)) {
            return null;
        }

        return FakePlayer.get(serverWorld);
    }
}