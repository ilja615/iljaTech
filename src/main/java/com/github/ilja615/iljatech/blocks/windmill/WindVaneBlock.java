package com.github.ilja615.iljatech.blocks.windmill;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

public class WindVaneBlock extends Block {
    public static final EnumProperty<WindDirection> WIND_DIRECTION = EnumProperty.of("wind_direction", WindDirection.class);

    public WindVaneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WIND_DIRECTION, WindDirection.N));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        if (world instanceof ServerWorld) {
            Vec2f vector = Wind.getWindVectorAt(world, pos.getX(), pos.getZ());
            Pair<WindDirection, Double> wind = Wind.getWindFromVector(vector);
            return this.getDefaultState().with(WIND_DIRECTION, wind.getLeft());
        }
        return super.getPlacementState(ctx);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIND_DIRECTION);
    }
}
