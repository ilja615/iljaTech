package com.github.ilja615.iljatech.blocks.funnel;

import com.github.ilja615.iljatech.blocks.pipe.PipeBlock;
import com.github.ilja615.iljatech.blocks.pipe.PipeShape;
import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
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
            if (!level.getFluidState(worldPosition.above()).is(Fluids.WATER) && !level.getBlockState(worldPosition.above()).is(Blocks.WATER_CAULDRON))
                return;

            BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();
            Direction thisDirection = Direction.DOWN;
            nextPos.set(worldPosition.relative(thisDirection));
            int niter = 0;

            // Pipe liquid transfer
            float hydrostaticPressure = 0.1f;
            while (niter < 100) {
                if (!(level.getBlockState(nextPos).getBlock() instanceof PipeBlock))
                    break;
                PipeShape pipeShape = level.getBlockState(nextPos).getValue(PipeBlock.PIPE_SHAPE);
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
            if (level.getBlockState(nextPos).isAir()) {
                if (!level.isClientSide) {
                    double px = thisDirection.getAxis() == Direction.Axis.X ? nextPos.getX() + 0.5d - 0.5d * thisDirection.getStepX() : nextPos.getX() + level.random.nextFloat() * 0.5f + 0.25f;
                    double py = thisDirection.getAxis() == Direction.Axis.Y ? nextPos.getY() + 0.5d - 0.5d * thisDirection.getStepY() : nextPos.getY() + level.random.nextFloat() * 0.25f + 0.25f;
                    double pz = thisDirection.getAxis() == Direction.Axis.Z ? nextPos.getZ() + 0.5d - 0.5d * thisDirection.getStepZ() : nextPos.getZ() + level.random.nextFloat() * 0.5f + 0.25f;
                    ((ServerLevel) level).sendParticles(ParticleTypes.DRIPPING_WATER, px, py, pz, level.random.nextInt(3), 0.1f, 0.1f, 0.1f, 0.0);
                }
            } else if (ticks >= 100 && hydrostaticPressure >= 0) {
                ticks = 0;
                if (level.getBlockState(worldPosition.above()).is(Blocks.WATER_CAULDRON)) {
                    LayeredCauldronBlock.lowerFillLevel(level.getBlockState(worldPosition.above()), level, worldPosition.above());
                }
                if (level.getBlockState(nextPos).getBlock() instanceof CauldronBlock) {
                    level.setBlockAndUpdate(nextPos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MIN_FILL_LEVEL));
                } else if (level.getBlockState(nextPos).getBlock() instanceof LayeredCauldronBlock && level.getBlockState(nextPos).is(Blocks.WATER_CAULDRON)) {
                    int level = level.getBlockState(nextPos).getValue(LayeredCauldronBlock.LEVEL);
                    level.setBlockAndUpdate(nextPos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, Math.min(LayeredCauldronBlock.MAX_FILL_LEVEL, level + 1)));
                }
                update();
            }
        }
    }


    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        if (nbt.contains("FluidTank", Tag.TAG_COMPOUND))
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
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

    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
    }

    public SingleFluidStorage getFluidStorage() {
        return this.fluidStorage;
    }


    private void update() {
        setChanged();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }
}
