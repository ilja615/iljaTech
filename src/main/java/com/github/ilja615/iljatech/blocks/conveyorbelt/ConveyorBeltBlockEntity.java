package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConveyorBeltBlockEntity extends BlockEntity implements TickableBlockEntity {
    Box ITEM_AREA_SHAPE = (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0);

    private final List<Pair<ItemStack, Vec3d>> STACKS = new ArrayList<>();
    private final List<Integer> toRemove = new ArrayList<>();
    private int ticks = 0;

    private final static Codec<Pair<ItemStack, Vec3d>> CODEC = Codec.mapPair(ItemStack.CODEC.fieldOf("item"), Vec3d.CODEC.fieldOf("pos")).codec();

    float VELOCITY = 0.07f;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CONVEYOR_BELT, pos, state);
    }

    @Override
    public void tick() {
        boolean flag = false;

        if (!toRemove.isEmpty()) {
            Collections.sort(toRemove);
            for (int j = toRemove.size() - 1; j >= 0; j--) {
                int i = toRemove.get(j);
                STACKS.remove(i);
            }
            toRemove.clear();
        }

        if (ticks == 0) {
            Box box = ITEM_AREA_SHAPE.offset(pos.getX(), pos.getY() + 0.5d, pos.getZ());
            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
                if (!itemEntity.isOnGround() || world.getBlockState(pos).get(ConveyorBeltBlock.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF)
                    break;

                ticks = 20;
                int count = itemEntity.getStack().getCount();
                if (count > 16) {
                    itemEntity.getStack().decrement(16);
                    STACKS.add(new Pair<>(itemEntity.getStack().copyWithCount(16), pos.toCenterPos().add(0, 0.75d, 0)));
                } else {
                    itemEntity.kill();
                    STACKS.add(new Pair<>(itemEntity.getStack().copyWithCount(count), pos.toCenterPos().add(0, 0.75d, 0)));
                }
                flag = true;
            }
        } else if (ticks >0) {
            ticks--;
        }

        List<Pair<Integer, Pair<ItemStack, Vec3d>>> updates = new ArrayList<>();

        for(int i = 0; i < STACKS.size(); ++i) {
            Pair<ItemStack, Vec3d> pair = STACKS.get(i);
            ItemStack itemStack = pair.getFirst();
            Vec3d itemPos = pair.getSecond();
            BlockPos under = BlockPos.ofFloored(itemPos.add(0, -1.0, 0));
            if (world.getBlockState(under).getBlock() instanceof ConveyorBeltBlock && world.getBlockState(under).get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB)
                under = under.up();
            if (world.getBlockState(under).getBlock() instanceof ConveyorBeltBlock && world.getBlockState(under).get(ConveyorBeltBlock.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
                Direction nextDir = world.getBlockState(under).get(ConveyorBeltBlock.FACING);
                double progress = 0.0d;
                if (nextDir.getVector().getComponentAlongAxis(nextDir.getAxis()) == 1) {
                    progress = itemPos.getComponentAlongAxis(nextDir.getAxis()) - under.getComponentAlongAxis(nextDir.getAxis());
                }
                if (nextDir.getVector().getComponentAlongAxis(nextDir.getAxis()) == -1) {
                    progress =  under.getComponentAlongAxis(nextDir.getAxis()) + 1.0d - itemPos.getComponentAlongAxis(nextDir.getAxis());
                }

                BlockState state1 = world.getBlockState(under.offset(nextDir));
                boolean up = state1.isOf(ModBlocks.CONVEYOR_BELT) && (state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB || state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.DIAGONAL) && state1.get(ConveyorBeltBlock.FACING) == nextDir;
                boolean onSlab = world.getBlockState(under).get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.BOTTOM_SLAB;

                Vec3d newPos = new Vec3d(
                        nextDir.getAxis() == Direction.Axis.X ? itemPos.getX() + nextDir.getOffsetX() * VELOCITY : Math.floor(itemPos.getX()) + 0.5d,
                        under.getY() + 1.25d - (onSlab ? 0.5d : 0) + (up ? 0.5d * progress : 0),
                        nextDir.getAxis() == Direction.Axis.Z ? itemPos.getZ() + nextDir.getOffsetZ() * VELOCITY : Math.floor(itemPos.getZ()) + 0.5d);

                if (world.getBlockEntity(under.up()) instanceof RollerMillBlockEntity rmbe) {
                    ItemStack input = rmbe.getInventory().getStack(0);
                    if (input.isEmpty()) {
                        rmbe.getInventory().setStack(0, itemStack);
                        toRemove.add(i); // schedule removal from conveyor
                    } else if (input.isOf(itemStack.getItem()) && input.getCount() < input.getMaxCount()) {
                        rmbe.getInventory().setStack(0, input.copyWithCount(input.getCount() + 1));
                        toRemove.add(i); // schedule removal from conveyor
                    }
                }
                updates.add(new Pair<>(i, new Pair<>(itemStack, newPos))); // schedule update
            } else {
                world.spawnEntity(new ItemEntity(world, itemPos.getX(), itemPos.getY(), itemPos.getZ(), itemStack));
                boolean slab = world.getBlockState(pos).isOf(ModBlocks.CONVEYOR_BELT) && world.getBlockState(pos).get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.BOTTOM_SLAB;
                updates.add(new Pair<>(i, new Pair<>(itemStack, pos.toCenterPos().add(0.0d, (slab ? -0.5d : 0.0d), 0.0d)))); // hide it for when removal doesnt sync
                toRemove.add(i); // schedule removal
            }
        }
        // apply changes after the foreach otherwise there is maybe ConcurrentModificationException
        updates.forEach(indexedPair -> STACKS.set(indexedPair.getFirst(), indexedPair.getSecond()));

        if (!toRemove.isEmpty() || !updates.isEmpty() || flag) {
            update();
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        NbtList nbtList = nbt.getList("Stacks", NbtElement.COMPOUND_TYPE);
        RegistryOps<NbtElement> ops = registryLookup.getOps(NbtOps.INSTANCE);

        int targetSize = nbtList.size();
        if (STACKS.size() > targetSize) {
            STACKS.subList(targetSize, STACKS.size()).clear();
        }

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound entry = nbtList.getCompound(i);
            Optional<Pair<ItemStack, Vec3d>> parsed =
                    CODEC.parse(ops, entry).resultOrPartial(err -> {});

            if (parsed.isEmpty()) continue;

            if (i < STACKS.size()) {
                STACKS.set(i, parsed.get());
            } else {
                STACKS.add(parsed.get());
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        RegistryOps<NbtElement> ops = registryLookup.getOps(NbtOps.INSTANCE);
        NbtList out = new NbtList();

        for (int i = 0; i < STACKS.size(); i++) {
            Pair<ItemStack, Vec3d> pair = STACKS.get(i);
            ItemStack stack = pair.getFirst();
            if (stack.isEmpty()) continue;

            NbtCompound dst = new NbtCompound();
            CODEC.encode(pair, ops, dst).resultOrPartial(err -> {}).ifPresent(out::add);
        }

        nbt.put("Stacks", out);
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

    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public List<Pair<ItemStack, Vec3d>> getStacks() {
        return STACKS;
    }
}
