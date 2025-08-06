package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public class ConveyorBeltBlockEntity extends BlockEntity implements TickableBlockEntity {
    Box ITEM_AREA_SHAPE = (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0);

    private final Map<ItemStack, Vec3d> STACKS = new HashMap<>();

    private final static Codec<Pair<ItemStack, Vec3d>> CODEC = Codec.pair(ItemStack.CODEC, Vec3d.CODEC);

    float VELOCITY = 0.07f;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CONVEYOR_BELT, pos, state);
    }

    @Override
    public void tick() {
        // Push items (which should happen only on server)
        if (world.isClient)
            return;

        Box box = ITEM_AREA_SHAPE.offset(pos.getX(), pos.getY() + 0.5d, pos.getZ());
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
            if (!itemEntity.isOnGround() || world.getBlockState(pos).get(ConveyorBeltBlock.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF)
                break;
            STACKS.put(itemEntity.getStack(), pos.toCenterPos().add(0, 0.6d, 0));
            itemEntity.kill();
        }
        Map<ItemStack, Vec3d> updates = new HashMap<>();
        List<ItemStack> toRemove = new ArrayList<>();

        STACKS.forEach((itemStack, itemPos) -> {
            BlockPos under = BlockPos.ofFloored(itemPos.add(0, -0.5, 0));
            if (world.getBlockState(under).getBlock() instanceof ConveyorBeltBlock && world.getBlockState(under).get(ConveyorBeltBlock.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
                Direction nextDir = world.getBlockState(under).get(ConveyorBeltBlock.FACING);
                Vec3d newPos = itemPos;

                if (nextDir.getAxis() == Direction.Axis.X)
                    newPos = new Vec3d(itemPos.getX() + nextDir.getOffsetX() * VELOCITY, itemPos.getY(), Math.floor(itemPos.getZ()) + 0.5d);
                if (nextDir.getAxis() == Direction.Axis.Z)
                    newPos = new Vec3d(Math.floor(itemPos.getX()) + 0.5d, itemPos.getY(), itemPos.getZ() + nextDir.getOffsetZ() * VELOCITY);

                double progress = 0.0d;
                if (nextDir.getVector().getComponentAlongAxis(nextDir.getAxis()) == 1) {
                    progress = itemPos.getComponentAlongAxis(nextDir.getAxis()) - under.getComponentAlongAxis(nextDir.getAxis());
                }
                if (nextDir.getVector().getComponentAlongAxis(nextDir.getAxis()) == -1) {
                    progress =  under.getComponentAlongAxis(nextDir.getAxis()) + 1.0d - itemPos.getComponentAlongAxis(nextDir.getAxis());
                }

                BlockState state1 = world.getBlockState(under.offset(nextDir));
                boolean up = state1.isOf(ModBlocks.CONVEYOR_BELT) && (state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB || state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.DIAGONAL) && state1.get(ConveyorBeltBlock.FACING) == nextDir;

                if (up && progress >= 1.0d - VELOCITY) {
                    up = false;
                    newPos = newPos.add(0, 0.5d, 0);
                    if (nextDir.getAxis() == Direction.Axis.X)
                        newPos = new Vec3d(newPos.getX() + nextDir.getOffsetX() * VELOCITY, newPos.getY(), Math.floor(newPos.getZ()) + 0.5d);
                    if (nextDir.getAxis() == Direction.Axis.Z)
                        newPos = new Vec3d(Math.floor(newPos.getX()) + 0.5d, newPos.getY(), newPos.getZ() + nextDir.getOffsetZ() * VELOCITY);
                }
                System.out.println(up + " " + progress);

                ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, newPos.getX(), newPos.getY() + (up ?  + progress * 0.5d : 0), newPos.getZ(), 1, 0.0f, 0.0f, 0.0f, 0.0);

                updates.put(itemStack, newPos); // schedule update
            } else {
                world.spawnEntity(new ItemEntity(world, itemPos.getX(), itemPos.getY(), itemPos.getZ(), itemStack));
                toRemove.add(itemStack); // schedule removal
            }
        });
        // apply changes after the foreach otherwise there is ConcurrentModificationException
        if (!toRemove.isEmpty() || !updates.isEmpty())
            update();
        toRemove.forEach(STACKS::remove);
        updates.forEach(STACKS::put);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        NbtList nbtList = nbt.getList("Stacks", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Optional<Pair<ItemStack, Vec3d>> pair = CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), nbtCompound).resultOrPartial((error) -> {});
            if (pair.isPresent()) {
                STACKS.put(pair.get().getFirst(), pair.get().getSecond());
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        NbtList nbtList = new NbtList();
        for(int i = 0; i < STACKS.size(); ++i) {
            ItemStack itemStack = STACKS.entrySet().stream().toList().get(i).getKey();
            Vec3d itemPos = STACKS.entrySet().stream().toList().get(i).getValue();
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtList.add(CODEC.encode(new Pair<>(itemStack, itemPos), registryLookup.getOps(NbtOps.INSTANCE), nbtCompound).getOrThrow());
            }
        }
        nbt.put("Stacks", nbtList);
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

    public Map<ItemStack, Vec3d> getStacks() {
        return STACKS;
    }
}
