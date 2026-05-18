package com.github.ilja615.iljatech.blocks.funnel;

import com.github.ilja615.iljatech.blocks.pipe.PipeBlock;
import com.github.ilja615.iljatech.blocks.pipe.PipeShape;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class FunnelBlockEntity  extends BlockEntity implements TickableBlockEntity {
    private int ticks = 0;

    public FunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FUNNEL, pos, state);
    }

    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(
            FluidConstants.BUCKET * 16,
            this::update);

    @Override
    public void tick() {
        if (ticks++ % 20 == 0) {
            // Check if there is water above
            if (!world.getFluidState(pos.up()).isOf(Fluids.WATER) && !world.getBlockState(pos.up()).isOf(Blocks.WATER_CAULDRON))
                return;

            BlockPos.Mutable nextPos = new BlockPos.Mutable();
            Direction thisDirection = Direction.DOWN;
            nextPos.set(pos.offset(thisDirection));
            int niter = 0;

            // Pipe liquid transfer
            float hydrostaticPressure = 0.1f;
            while (niter < 100) {
                if (!(world.getBlockState(nextPos).getBlock() instanceof PipeBlock))
                    break;
                PipeShape pipeShape = world.getBlockState(nextPos).get(PipeBlock.PIPE_SHAPE);
                Direction pipeDir1 = pipeShape.getDirection1();
                Direction pipeDir2 = pipeShape.getDirection2();
                if (pipeDir1.getOpposite() == thisDirection) {
                    nextPos.move(pipeDir2);
                    thisDirection = pipeDir2;
                } else {
                    nextPos.move(pipeDir1);
                    thisDirection = pipeDir1;
                }
                if (thisDirection == Direction.DOWN)
                    hydrostaticPressure += 0.1f;
                else if (thisDirection == Direction.UP)
                    hydrostaticPressure -= 0.2f;
                else
                    hydrostaticPressure -= 0.01f;

                niter++;
            }
            if (world.getBlockState(nextPos).isAir()) {
                if (!world.isClient) {
                    double px = thisDirection.getAxis() == Direction.Axis.X ? nextPos.getX() + 0.5d - 0.5d * thisDirection.getOffsetX() : nextPos.getX() + world.random.nextFloat() * 0.5f + 0.25f;
                    double py = thisDirection.getAxis() == Direction.Axis.Y ? nextPos.getY() + 0.5d - 0.5d * thisDirection.getOffsetY() : nextPos.getY() + world.random.nextFloat() * 0.25f + 0.25f;
                    double pz = thisDirection.getAxis() == Direction.Axis.Z ? nextPos.getZ() + 0.5d - 0.5d * thisDirection.getOffsetZ() : nextPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f;
                    ((ServerWorld) world).spawnParticles(ParticleTypes.DRIPPING_WATER, px, py, pz, world.random.nextInt(3), 0.1f, 0.1f, 0.1f, 0.0);
                }
            } else if (ticks >= 100 && hydrostaticPressure >= 0) {
                ticks = 0;
                if (world.getBlockState(pos.up()).isOf(Blocks.WATER_CAULDRON)) {
                    LeveledCauldronBlock.decrementFluidLevel(world.getBlockState(pos.up()), world, pos.up());
                }
                if (world.getBlockState(nextPos).getBlock() instanceof CauldronBlock) {
                    world.setBlockState(nextPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, LeveledCauldronBlock.MIN_LEVEL));
                } else if (world.getBlockState(nextPos).getBlock() instanceof LeveledCauldronBlock && world.getBlockState(nextPos).isOf(Blocks.WATER_CAULDRON)) {
                    int level = world.getBlockState(nextPos).get(LeveledCauldronBlock.LEVEL);
                    world.setBlockState(nextPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, Math.min(LeveledCauldronBlock.MAX_LEVEL, level + 1)));
                }
                update();
            }
        }
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
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

    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }


    private void update() {
        markDirty();
        if (world != null)
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }
}
