package com.github.ilja615.iljatech.entities;

import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.github.ilja615.iljatech.init.ModEntities;

public class SeatEntity extends Entity
{
    public SeatEntity(World world) {
        super(ModEntities.SEAT, world);
        this.noClip = true;
    }

    public SeatEntity(World world, BlockPos source, Direction direction) {
        this(world);
        this.setPos(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5);
        this.setRotation(direction.getOpposite().asRotation(), 0F);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.getWorld().isClient()) {
            if(!this.hasPassengers() || this.getWorld().isAir(this.getBlockPos())) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        BlockPos pos = this.getBlockPos().offset(Direction.fromRotation(getYaw()));
        double d = this.getWorld().getDismountHeight(pos);
        if (Dismounting.canDismountInBlock(d)) {
            Vec3d vec3d = Vec3d.ofCenter(pos, d);
            if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, passenger.getBoundingBox().offset(vec3d))) {
                return vec3d;
            }
        }
        pos = this.getBlockPos();
        d = this.getWorld().getDismountHeight(pos);
        if (Dismounting.canDismountInBlock(d)) {
            Vec3d vec3d = Vec3d.ofCenter(pos, d);
            if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, passenger.getBoundingBox().offset(vec3d))) {
                return vec3d;
            }
        }
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}