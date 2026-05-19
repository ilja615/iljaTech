package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import com.github.ilja615.iljatech.energy.MechPwrAccepter;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConveyorBeltBlockEntity extends BlockEntity implements TickableBlockEntity {
    AABB ITEM_AREA_SHAPE = (AABB) Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).toAabbs().get(0);

    private final List<Pair<ItemStack, Vec3>> STACKS = new ArrayList<>();
    public final List<Integer> toRemove = new ArrayList<>();
    private int ticks = 0;

    private final static Codec<Pair<ItemStack, Vec3>> CODEC = Codec.mapPair(ItemStack.CODEC.fieldOf("item"), Vec3.CODEC.fieldOf("offset")).codec();

    float VELOCITY = 0.07f;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CONVEYOR_BELT, pos, state);
    }

    @Override
    public void tick() {
        boolean flag = false;
        Direction nextDir = getBlockState().getValue(ConveyorBeltBlock.FACING);
        if (getBlockState().getValue(ConveyorBeltBlock.POWERED))
            nextDir = nextDir.getOpposite();
        BlockState state1 = level.getBlockState(worldPosition.relative(nextDir));
        boolean onSlab = getBlockState().getValue(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.BOTTOM_SLAB;

        if (!toRemove.isEmpty()) {
            Collections.sort(toRemove);
            for (int j = toRemove.size() - 1; j >= 0; j--) {
                int i = toRemove.get(j);
                STACKS.remove(i);
            }
            toRemove.clear();
        }

        if (ticks == 0) {
            AABB box = ITEM_AREA_SHAPE.move(worldPosition.getX(), worldPosition.getY() + 0.5d, worldPosition.getZ());
            for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE)) {
                if (!itemEntity.onGround() || getBlockState().getValue(ConveyorBeltBlock.ON_OFF_PWR) == MechPwrAccepter.OnOffPwr.OFF)
                    break;

                ticks = 20;
                int count = itemEntity.getItem().getCount();
                Vec3 relPos = new Vec3(getXforProgress(nextDir, 0.0f), 0.75d, getZforProgress(nextDir, 0.0f));
                if (count > 16) {
                    itemEntity.getItem().shrink(16);
                    STACKS.add(new Pair<>(itemEntity.getItem().copyWithCount(16), relPos));
                } else {
                    itemEntity.kill();
                    STACKS.add(new Pair<>(itemEntity.getItem().copyWithCount(count), relPos));
                }
                flag = true;
            }
        } else if (ticks > 0) {
            ticks--;
        }

        List<Pair<Integer, Pair<ItemStack, Vec3>>> updates = new ArrayList<>();

        if (getBlockState().getValue(ConveyorBeltBlock.ON_OFF_PWR) != MechPwrAccepter.OnOffPwr.OFF) {
            for (int i = 0; i < STACKS.size(); ++i) {
                Pair<ItemStack, Vec3> pair = STACKS.get(i);
                ItemStack itemStack = pair.getFirst();
                Vec3 offset = pair.getSecond();
                float progress = getProgressForOffset(nextDir, offset);
                if (progress < 1.0 - VELOCITY) {
                    progress += VELOCITY;

                    boolean up = state1.is(ModBlocks.CONVEYOR_BELT) && state1.getValue(ConveyorBeltBlock.FACING) == nextDir
                            && (state1.getValue(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB
                            || state1.getValue(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.DIAGONAL);

                    Vec3 newPos = new Vec3(
                            getXforProgress(nextDir, progress),
                            1.25d - (onSlab ? 0.5d : 0) + (up ? 0.5d * progress : 0),
                            getZforProgress(nextDir, progress));

                    if (level.getBlockEntity(worldPosition.above()) instanceof RollerMillBlockEntity rmbe) {
                        ItemStack input = rmbe.getInventory().getItem(0);
                        if (input.isEmpty()) {
                            rmbe.getInventory().setItem(0, itemStack);
                            toRemove.add(i); // schedule removal from conveyor
                        } else if (input.is(itemStack.getItem()) && input.getCount() < input.getMaxStackSize()) {
                            rmbe.getInventory().setItem(0, input.copyWithCount(input.getCount() + 1));
                            toRemove.add(i); // schedule removal from conveyor
                        }
                    }
                    updates.add(new Pair<>(i, new Pair<>(itemStack, newPos))); // schedule update
                } else {
                    if (state1.is(ModBlocks.CONVEYOR_BELT)) {
                        BlockPos nextPos = worldPosition.relative(nextDir);
                        if (state1.getValue(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB) {
                            nextPos = nextPos.above();
                        }
                        if (level.getBlockEntity(nextPos) instanceof ConveyorBeltBlockEntity cbbe) {
                            Vec3 relPos = new Vec3(getXforProgress(nextDir, 0.0f), 0.75d, getZforProgress(nextDir, 0.0f));
                            // Transfer it to the next conveyor belt block
                            cbbe.STACKS.add(new Pair<>(itemStack, relPos));
                            updates.add(new Pair<>(i, new Pair<>(itemStack, new Vec3(0.5d, (onSlab ? 0.0d : 0.5d), 0.5d)))); // hide it inside the block for when removal doesnt sync
                            toRemove.add(i); // schedule removal
                        }
                    } else {
                        // The end of the belt
                        level.addFreshEntity(new ItemEntity(level, worldPosition.relative(nextDir).getX() + 0.5d, worldPosition.getY() + 1.25d, worldPosition.relative(nextDir).getZ() + 0.5d, itemStack, nextDir.getStepX() * 0.1f, 0, nextDir.getStepZ() * 0.1f));
                        updates.add(new Pair<>(i, new Pair<>(itemStack, new Vec3(0.5d, (onSlab ? 0.0d : 0.5d), 0.5d)))); // hide it inside the block for when removal doesnt sync
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
            if (direction.getStepX() == 1) {
                return progress;
            } else
                return 1.0f - progress;
        } else
            return 0.5f;
    }

    public double getZforProgress(Direction direction, float progress) {
        if (direction.getAxis() == Direction.Axis.Z) {
            if (direction.getStepZ() == 1) {
                return progress;
            } else
                return 1.0f - progress;
        } else
            return 0.5f;
    }

    public float getProgressForOffset(Direction direction, Vec3 offset) {
        if (direction.getAxis() == Direction.Axis.X) {
            if (direction.getStepX() == 1) {
                return (float) offset.x();
            } else
                return 1.0f - (float) offset.x();
        } else if (direction.getAxis() == Direction.Axis.Z) {
            if (direction.getStepZ() == 1) {
                return (float) offset.z();
            } else
                return 1.0f - (float) offset.z();
        }
        return 0.5f;
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);

        ListTag nbtList = nbt.getList("Stacks", Tag.TAG_COMPOUND);
        RegistryOps<Tag> ops = registryLookup.createSerializationContext(NbtOps.INSTANCE);

        int targetSize = nbtList.size();
        if (STACKS.size() > targetSize) {
            STACKS.subList(targetSize, STACKS.size()).clear();
        }

        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag entry = nbtList.getCompound(i);
            Optional<Pair<ItemStack, Vec3>> parsed =
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
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);

        RegistryOps<Tag> ops = registryLookup.createSerializationContext(NbtOps.INSTANCE);
        ListTag out = new ListTag();

        for (int i = 0; i < STACKS.size(); i++) {
            Pair<ItemStack, Vec3> pair = STACKS.get(i);
            ItemStack stack = pair.getFirst();
            if (stack.isEmpty()) continue;

            CompoundTag dst = new CompoundTag();
            CODEC.encode(pair, ops, dst).resultOrPartial(err -> {}).ifPresent(out::add);
        }

        nbt.put("Stacks", out);
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

    public void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public List<Pair<ItemStack, Vec3>> getStacks() {
        return STACKS;
    }
}
