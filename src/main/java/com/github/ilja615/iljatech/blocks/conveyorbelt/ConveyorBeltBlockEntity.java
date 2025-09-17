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
    public final List<Integer> toRemove = new ArrayList<>();
    private int ticks = 0;

    private final static Codec<Pair<ItemStack, Vec3d>> CODEC = Codec.mapPair(ItemStack.CODEC.fieldOf("item"), Vec3d.CODEC.fieldOf("offset")).codec();

    float VELOCITY = 0.07f;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CONVEYOR_BELT, pos, state);
    }

    @Override
    public void tick() {
        boolean flag = false;
        Direction nextDir = getCachedState().get(ConveyorBeltBlock.FACING);
        if (getCachedState().get(ConveyorBeltBlock.POWERED))
            nextDir = nextDir.getOpposite();
        BlockState state1 = world.getBlockState(pos.offset(nextDir));
        boolean onSlab = getCachedState().get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.BOTTOM_SLAB;

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
                if (!itemEntity.isOnGround() || getCachedState().get(ConveyorBeltBlock.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF)
                    break;

                ticks = 20;
                int count = itemEntity.getStack().getCount();
                Vec3d relPos = new Vec3d(getXforProgress(nextDir, 0.0f), 0.75d, getZforProgress(nextDir, 0.0f));
                if (count > 16) {
                    itemEntity.getStack().decrement(16);
                    STACKS.add(new Pair<>(itemEntity.getStack().copyWithCount(16), relPos));
                } else {
                    itemEntity.kill();
                    STACKS.add(new Pair<>(itemEntity.getStack().copyWithCount(count), relPos));
                }
                flag = true;
            }
        } else if (ticks > 0) {
            ticks--;
        }

        List<Pair<Integer, Pair<ItemStack, Vec3d>>> updates = new ArrayList<>();

        if (getCachedState().get(ConveyorBeltBlock.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
            for (int i = 0; i < STACKS.size(); ++i) {
                Pair<ItemStack, Vec3d> pair = STACKS.get(i);
                ItemStack itemStack = pair.getFirst();
                Vec3d offset = pair.getSecond();
                float progress = getProgressForOffset(nextDir, offset);
                if (progress < 1.0 - VELOCITY) {
                    progress += VELOCITY;

                    boolean up = state1.isOf(ModBlocks.CONVEYOR_BELT) && state1.get(ConveyorBeltBlock.FACING) == nextDir
                            && (state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB
                            || state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.DIAGONAL);

                    Vec3d newPos = new Vec3d(
                            getXforProgress(nextDir, progress),
                            1.25d - (onSlab ? 0.5d : 0) + (up ? 0.5d * progress : 0),
                            getZforProgress(nextDir, progress));

                    if (world.getBlockEntity(pos.up()) instanceof RollerMillBlockEntity rmbe) {
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
                    if (state1.isOf(ModBlocks.CONVEYOR_BELT)) {
                        BlockPos nextPos = pos.offset(nextDir);
                        if (state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB) {
                            nextPos = nextPos.up();
                        }
                        if (world.getBlockEntity(nextPos) instanceof ConveyorBeltBlockEntity cbbe) {
                            Vec3d relPos = new Vec3d(getXforProgress(nextDir, 0.0f), 0.75d, getZforProgress(nextDir, 0.0f));
                            // Transfer it to the next conveyor belt block
                            cbbe.STACKS.add(new Pair<>(itemStack, relPos));
                            updates.add(new Pair<>(i, new Pair<>(itemStack, new Vec3d(0.5d, (onSlab ? 0.0d : 0.5d), 0.5d)))); // hide it inside the block for when removal doesnt sync
                            toRemove.add(i); // schedule removal
                        }
                    } else {
                        // The end of the belt
                        world.spawnEntity(new ItemEntity(world, pos.offset(nextDir).getX() + 0.5d, pos.getY() + 1.25d, pos.offset(nextDir).getZ() + 0.5d, itemStack, nextDir.getOffsetX() * 0.1f, 0, nextDir.getOffsetZ() * 0.1f));
                        updates.add(new Pair<>(i, new Pair<>(itemStack, new Vec3d(0.5d, (onSlab ? 0.0d : 0.5d), 0.5d)))); // hide it inside the block for when removal doesnt sync
                        toRemove.add(i); // schedule removal
                    }
                }
            }
        }
        // apply changes after the foreach otherwise there is maybe ConcurrentModificationException
        updates.forEach(indexedPair -> STACKS.set(indexedPair.getFirst(), indexedPair.getSecond()));

        if (!toRemove.isEmpty() || !updates.isEmpty() || flag) {
            update();
        }
    }

    public double getXforProgress(Direction direction, float progress) {
        if (direction.getAxis() == Direction.Axis.X) {
            if (direction.getOffsetX() == 1) {
                return progress;
            } else
                return 1.0f - progress;
        } else
            return 0.5f;
    }

    public double getZforProgress(Direction direction, float progress) {
        if (direction.getAxis() == Direction.Axis.Z) {
            if (direction.getOffsetZ() == 1) {
                return progress;
            } else
                return 1.0f - progress;
        } else
            return 0.5f;
    }

    public float getProgressForOffset(Direction direction, Vec3d offset) {
        if (direction.getAxis() == Direction.Axis.X) {
            if (direction.getOffsetX() == 1) {
                return (float) offset.getX();
            } else
                return 1.0f - (float) offset.getX();
        } else if (direction.getAxis() == Direction.Axis.Z) {
            if (direction.getOffsetZ() == 1) {
                return (float) offset.getZ();
            } else
                return 1.0f - (float) offset.getZ();
        }
        return 0.5f;
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

    public void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public List<Pair<ItemStack, Vec3d>> getStacks() {
        return STACKS;
    }
}
