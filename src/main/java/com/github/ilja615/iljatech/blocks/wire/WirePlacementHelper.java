package com.github.ilja615.iljatech.blocks.wire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WirePlacementHelper {
    private final World world;
    private final BlockPos pos;
    private final Block block;
    private BlockState state;
    private List<BlockPos> connections = new ArrayList<>();

    public WirePlacementHelper(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.block = state.getBlock();
        if (this.block instanceof WireBlock)
        {
            WireShape wireshape = state.get(WireBlock.WIRE_SHAPE);
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
                list.add(startPos.east().up());
                break;
            case ASCENDING_WEST:
                list.add(startPos.west().up());
                list.add(startPos.east());
                break;
            case ASCENDING_NORTH:
                list.add(startPos.north().up());
                list.add(startPos.south());
                break;
            case ASCENDING_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south().up());
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
        return WireBlock.isWire(this.world, pos) || WireBlock.isWire(this.world, pos.up()) || WireBlock.isWire(this.world, pos.down());
    }

    @Nullable
    private WirePlacementHelper getWire(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        if (WireBlock.isWire(blockstate)) {
            return new WirePlacementHelper(this.world, pos, blockstate);
        } else {
            BlockPos $$1 = pos.up();
            blockstate = this.world.getBlockState($$1);
            if (WireBlock.isWire(blockstate)) {
                return new WirePlacementHelper(this.world, $$1, blockstate);
            } else {
                $$1 = pos.down();
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

        for(Direction direction : Direction.Type.HORIZONTAL) {
            if (this.hasWire(this.pos.offset(direction))) {
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
            if (WireBlock.isWire(this.world, blockpos.up())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (WireBlock.isWire(this.world, blockpos1.up())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (WireBlock.isWire(this.world, blockpos3.up())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (WireBlock.isWire(this.world, blockpos2.up())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        if (wireshape == null) {
            wireshape = WireShape.NORTH_SOUTH;
        }

        if (this.block instanceof WireBlock)
        {
            this.state = this.state.with(WireBlock.WIRE_SHAPE, wireshape);
        }
        this.world.setBlockState(this.pos, this.state, 3);
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
            if (WireBlock.isWire(this.world, northpos.up())) {
                wireshape = WireShape.ASCENDING_NORTH;
            }

            if (WireBlock.isWire(this.world, southpos.up())) {
                wireshape = WireShape.ASCENDING_SOUTH;
            }
        }

        if (wireshape == WireShape.EAST_WEST) {
            if (WireBlock.isWire(this.world, eastpos.up())) {
                wireshape = WireShape.ASCENDING_EAST;
            }

            if (WireBlock.isWire(this.world, westpos.up())) {
                wireshape = WireShape.ASCENDING_WEST;
            }
        }

        this.updateConnections(wireshape);
        if (this.block instanceof WireBlock)
        {
            this.state = this.state.with(WireBlock.WIRE_SHAPE, wireshape);
        }
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

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
