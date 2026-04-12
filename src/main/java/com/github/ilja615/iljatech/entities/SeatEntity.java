package com.github.ilja615.iljatech.entities;

import com.github.ilja615.iljatech.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SeatEntity extends Entity
{
    public SeatEntity(Level world) {
        super(ModEntities.SEAT, world);
        this.noPhysics = true;
    }

    public SeatEntity(Level world, BlockPos source, Direction direction) {
        this(world);
        this.setPosRaw(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5);
        this.setRot(direction.toYRot(), 0F);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide()) {
            if(!this.isVehicle() || this.level().isEmptyBlock(this.blockPosition())) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        BlockPos pos = this.blockPosition().relative(Direction.fromYRot(getYRot()));
        double d = this.level().getBlockFloorHeight(pos);
        if (DismountHelper.isBlockFloorValid(d)) {
            Vec3 vec3d = Vec3.upFromBottomCenterOf(pos, d);
            if (DismountHelper.canDismountTo(this.level(), passenger, passenger.getBoundingBox().move(vec3d))) {
                return vec3d;
            }
        }
        // Fallback option
        pos = this.blockPosition();
        Vec3 vec3d = Vec3.upFromBottomCenterOf(pos, 1.0d);
        return vec3d;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {}
}