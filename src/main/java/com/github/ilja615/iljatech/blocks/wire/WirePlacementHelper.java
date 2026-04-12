package com.github.ilja615.iljatech.blocks.wire;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WirePlacementHelper {
    private final Level world;
    private final BlockPos pos;
    private final Block block;
    private BlockState state;
    private List<BlockPos> connections = new ArrayList<>();

    public WirePlacementHelper(Level world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.block = state.getBlock();
        if (this.block instanceof WireBlock)
        {
            WireShape wireshape = state.getValue(WireBlock.WIRE_SHAPE);
            this.updateConnections(wireshape);
        }
    }

    public List<BlockPos> getConnections() {
        return this.connections;
    }

    private void updateConnections(WireShape shape) {
        this.connections.clear();
        this.connections = getConnectionArray(this.pos, shape);
    }

    public static List<BlockPos> getConnectionArray(BlockPos startPos, WireShape shape) {
        List<BlockPos> list = new ArrayList<>();
        switch(shape) {
            case NORTH_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south());
                break;
            case EAST_WEST:
                list.add(startPos.west());
                list.add(startPos.east());
                break;
            case ASCENDING_EAST:
                list.add(startPos.west());
                list.add(startPos.east().above());
                break;
            case ASCENDING_WEST:
                list.add(startPos.west().above());
                list.add(startPos.east());
                break;
            case ASCENDING_NORTH:
                list.add(startPos.north().above());
                list.add(startPos.south());
                break;
            case ASCENDING_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south().above());
                break;
            case SOUTH_EAST:
                list.add(startPos.east());
                list.add(startPos.south());
                break;
            case SOUTH_WEST:
                list.add(startPos.west());
                list.add(startPos.south());
                break;
            case NORTH_WEST:
                list.add(startPos.west());
                list.add(startPos.north());
                break;
            case NORTH_EAST:
                list.add(startPos.east());
                list.add(startPos.north());
                break;
            case CROSSROAD:
                list.add(startPos.north());
                list.add(startPos.south());
                list.add(startPos.west());
                list.add(startPos.east());
                break;
        }
        return list;
    }

    private void removeSoftConnections() {
        for(int i = 0; i < this.connections.size(); ++i) {
            WirePlacementHelper wirestate = this.getWire(this.connections.get(i));
            if (wirestate != null && wirestate.connectsTo(this)) {
                this.connections.set(i, wirestate.pos);
            } else {
                this.connections.remove(i--);
            }
        }

    }

    private boolean hasWire(BlockPos pos) {
        return WireBlock.isWire(this.world, pos) || WireBlock.isWire(this.world, pos.above()) || WireBlock.isWire(this.world, pos.below());
    }

    @Nullable
    private WirePlacementHelper getWire(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        if (WireBlock.isWire(blockstate)) {
            return new WirePlacementHelper(this.world, pos, blockstate);
        } else {
            BlockPos $$1 = pos.above();
            blockstate = this.world.getBlockState($$1);
            if (WireBlock.isWire(blockstate)) {
                return new WirePlacementHelper(this.world, $$1, blockstate);
            } else {
                $$1 = pos.below();
                blockstate = this.world.getBlockState($$1);
                return WireBlock.isWire(blockstate) ? new WirePlacementHelper(this.world, $$1, blockstate) : null;
            }
        }
    }

    private boolean connectsTo(WirePlacementHelper state) {
        return this.hasConnection(state.pos);
    }

    private boolean hasConnection(BlockPos p_55444_) {
        for(int i = 0; i < this.connections.size(); ++i) {
            BlockPos blockpos = this.connections.get(i);
            if (blockpos.getX() == p_55444_.getX() && blockpos.getZ() == p_55444_.getZ()) {
                return true;
            }
        }

        return false;
    }

    protected int countPotentialConnections() {
        int i = 0;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.hasWire(this.pos.relative(direction))) {
                ++i;
            }
        }

        return i;
    }

    private boolean canConnectTo(WirePlacementHelper wirePlacementHelper) {
        return this.connectsTo(wirePlacementHelper) || this.connections.size() < 4;
    }

    private void connectTo(WirePlacementHelper wirePlacementHelper) {
        this.connections.add(wirePlacementHelper.pos);
        BlockPos blockpos = this.pos.north();
        BlockPos blockpos1 = this.pos.south();
        BlockPos blockpos2 = this.pos.west();
        BlockPos blockpos3 = this.pos.east();
        boolean flag = this.hasConnection(blockpos);
        boolean flag1 = this.hasConnection(blockpos1);
        boolean flag2 = this.hasConnection(blockpos2);
        boolean flag3 = this.hasConnection(blockpos3);
        WireShape wireshape = null;
        if (flag || flag1) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (flag2 || flag3) {
            wireshape = WireShape.EAST_WEST;
        }

        if (flag1 && flag3 && !flag && !flag2) {
            wireshape = WireShape.SOUTH_EAST;
        }

        if (flag1 && flag2 && !flag && !flag3) {
            wireshape = WireShape.SOUTH_WEST;
        }

        if (flag && flag2 && !flag1 && !flag3) {
            wireshape = WireShape.NORTH_WEST;
        }

        if (flag && flag3 && !flag1 && !flag2) {
            wireshape = WireShape.NORTH_EAST;
        }

        if (countPotentialConnections() > 2) {
            wireshape = WireShape.CROSSROAD;
        }

        if (wireshape == WireShape.NORTH_SOUTH) {
            if (WireBlock.isWire(this.world, blockpos.above())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (WireBlock.isWire(this.world, blockpos1.above())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (WireBlock.isWire(this.world, blockpos3.above())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (WireBlock.isWire(this.world, blockpos2.above())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        if (wireshape == null) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (this.block instanceof WireBlock)
        {
            this.state = this.state.setValue(WireBlock.WIRE_SHAPE, wireshape);
        }
        this.world.setBlock(this.pos, this.state, 3);
    }

    private boolean hasNeighborWire(BlockPos p_55447_) {
        WirePlacementHelper wirestate = this.getWire(p_55447_);
        if (wirestate == null) {
            return false;
        } else {
            wirestate.removeSoftConnections();
            return wirestate.canConnectTo(this);
        }
    }

    public WirePlacementHelper place(boolean forceUpdate, WireShape shape) {
        BlockPos northpos = this.pos.north();
        BlockPos southpos = this.pos.south();
        BlockPos westpos = this.pos.west();
        BlockPos eastpos = this.pos.east();
        boolean north = this.hasNeighborWire(northpos);
        boolean south = this.hasNeighborWire(southpos);
        boolean west = this.hasNeighborWire(westpos);
        boolean east = this.hasNeighborWire(eastpos);
        WireShape wireshape = null;
        boolean northsouth = north || south;
        boolean westeast = west || east;
        if (northsouth && !westeast) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (westeast && !northsouth) {
            wireshape = WireShape.EAST_WEST;
        }

        boolean southeast = south && east;
        boolean southwest = south && west;
        boolean northeast = north && east;
        boolean northwest = north && west;
        if (southeast && !north && !west) {
            wireshape = WireShape.SOUTH_EAST;
        }

        if (southwest && !north && !east) {
            wireshape = WireShape.SOUTH_WEST;
        }

        if (northwest && !south && !east) {
            wireshape = WireShape.NORTH_WEST;
        }

        if (northeast && !south && !west) {
            wireshape = WireShape.NORTH_EAST;
        }

        if (countPotentialConnections() > 2 || wireshape == null) {
            wireshape = WireShape.CROSSROAD;
        }

        if (wireshape == WireShape.NORTH_SOUTH) {
            if (WireBlock.isWire(this.world, northpos.above())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (WireBlock.isWire(this.world, southpos.above())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (WireBlock.isWire(this.world, eastpos.above())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (WireBlock.isWire(this.world, westpos.above())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        this.updateConnections(wireshape);
        if (this.block instanceof WireBlock)
        {
            this.state = this.state.setValue(WireBlock.WIRE_SHAPE, wireshape);
        }
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlock(this.pos, this.state, 3);

            for(int i = 0; i < this.connections.size(); ++i) {
                WirePlacementHelper wirestate = this.getWire(this.connections.get(i));
                if (wirestate != null) {
                    wirestate.removeSoftConnections();
                    if (wirestate.canConnectTo(this)) {
                        wirestate.connectTo(this);
                    }
                }
            }
        }

        return this;
    }

    public BlockState getState() {
        return this.state;
    }
}
