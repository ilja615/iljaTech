package com.github.ilja615.iljatech.blocks.conveyorbelt;

import com.github.ilja615.iljatech.init.ModBlockEntityTypes;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.util.TickableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ConveyorBeltBlockEntity extends BlockEntity implements TickableBlockEntity {
//    Box ITEM_AREA_SHAPE = (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0);
    protected static final Map<Direction.Axis, Box> ITEM_AREA_SHAPES;

    static {
        Map<Direction.Axis, Box> tempMap = new EnumMap<>(Direction.Axis.class);

        tempMap.put(Direction.Axis.X, (Box) Block.createCuboidShape(-4.0, 0.0, 4.0, 20.0, 10.0, 12.0).getBoundingBoxes().get(0));
        tempMap.put(Direction.Axis.Y, (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0));
        tempMap.put(Direction.Axis.Z, (Box) Block.createCuboidShape(4.0, 0.0, -4.0, 12.0, 10.0, 20.0).getBoundingBoxes().get(0));

        ITEM_AREA_SHAPES = Collections.unmodifiableMap(tempMap);
    }

    float VELOCITY = 0.03f;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CONVEYOR_BELT, pos, state);
    }

    @Override
    public void tick() {
        // Push items
        Direction dir = world.getBlockState(pos).get(ConveyorBeltBlock.FACING);
        BlockState state1 = world.getBlockState(pos.offset(dir));
        boolean up = state1.isOf(ModBlocks.CONVEYOR_BELT) && (state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.TOP_SLAB || state1.get(ConveyorBeltBlock.CONVEYOR_BELT_STATE) == ConveyorBeltBlock.ConveyorBeltState.DIAGONAL) && state1.get(ConveyorBeltBlock.FACING) == dir;
        Box box = ITEM_AREA_SHAPES.get(dir.getAxis()).offset(pos.getX(), pos.getY() + 0.5d, pos.getZ());
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
            itemEntity.setVelocity(Vec3d.of(dir.getVector()).multiply(VELOCITY).add(itemEntity.getVelocity()));
            boolean atEnd = false;
            if (dir.getVector().getComponentAlongAxis(dir.getAxis()) == 1) {
                atEnd = itemEntity.getPos().getComponentAlongAxis(dir.getAxis()) >= pos.getComponentAlongAxis(dir.getAxis()) + 0.8d;
            }
            if (dir.getVector().getComponentAlongAxis(dir.getAxis()) == -1) {
                atEnd = itemEntity.getPos().getComponentAlongAxis(dir.getAxis()) <= pos.getComponentAlongAxis(dir.getAxis()) + 0.2d;
            }
            if (up && atEnd) {
                itemEntity.move(MovementType.PISTON, Vec3d.of(Direction.UP.getVector()).multiply(0.5d));
                itemEntity.setVelocity(Vec3d.of(dir.getVector()).multiply(VELOCITY).add(itemEntity.getVelocity()));
            }
        }
    }
}
