package com.github.ilja615.iljatech.blocks.pipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PipePlacementHelper {
    private final World world;
    private final BlockPos pos;
    private final Block block;
    private BlockState state;
    private List<BlockPos> connections = new ArrayList<>();

    public PipePlacementHelper(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.block = state.getBlock();
        if (this.block instanceof PipeBlock)
        {
            PipeShape pipeshape = state.get(PipeBlock.PIPE_SHAPE);
            this.updateConnections(pipeshape);
        }
    }

    public List<BlockPos> getConnections() {
        return this.connections;
    }

    private void updateConnections(PipeShape shape) {
        this.connections.clear();
        this.connections = getConnectionArray(this.pos, shape);
    }

    public static List<BlockPos> getConnectionArray(BlockPos startPos, PipeShape shape) {
        List<BlockPos> list = new ArrayList<>();
        switch(shape) {
            case NORTH_SOUTH:
                list.add(startPos.north());
                list.add(startPos.south());
                break;
            case UP_DOWN:
                list.add(startPos.up());
                list.add(startPos.down());
                break;
            case EAST_WEST:
                list.add(startPos.west());
                list.add(startPos.east());
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
            case EAST_UP:
                list.add(startPos.east());
                list.add(startPos.up());
                break;
            case NORTH_UP:
                list.add(startPos.north());
                list.add(startPos.up());
                break;
            case WEST_UP:
                list.add(startPos.west());
                list.add(startPos.up());
                break;
            case SOUTH_UP:
                list.add(startPos.south());
                list.add(startPos.up());
                break;
            case EAST_DOWN:
                list.add(startPos.east());
                list.add(startPos.down());
                break;
            case NORTH_DOWN:
                list.add(startPos.north());
                list.add(startPos.down());
                break;
            case WEST_DOWN:
                list.add(startPos.west());
                list.add(startPos.down());
                break;
            case SOUTH_DOWN:
                list.add(startPos.south());
                list.add(startPos.down());
                break;
        }
        return list;
    }

    private void removeSoftConnections() {
        for(int i = 0; i < this.connections.size(); ++i) {
            com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper pipestate = this.getPipe(this.connections.get(i));
            if (pipestate != null && pipestate.connectsTo(this)) {
                this.connections.set(i, pipestate.pos);
            } else {
                this.connections.remove(i--);
            }
        }

    }

    private boolean hasPipe(BlockPos pos) {
        return PipeBlock.isPipe(this.world, pos) || PipeBlock.isPipe(this.world, pos.up()) || PipeBlock.isPipe(this.world, pos.down());
    }

    @Nullable
    private com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper getPipe(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        if (PipeBlock.isPipe(blockstate)) {
            return new com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper(this.world, pos, blockstate);
        } else {
            BlockPos $$1 = pos.up();
            blockstate = this.world.getBlockState($$1);
            if (PipeBlock.isPipe(blockstate)) {
                return new com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper(this.world, $$1, blockstate);
            } else {
                $$1 = pos.down();
                blockstate = this.world.getBlockState($$1);
                return PipeBlock.isPipe(blockstate) ? new com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper(this.world, $$1, blockstate) : null;
            }
        }
    }

    private boolean connectsTo(com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper state) {
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
            if (this.hasPipe(this.pos.offset(direction))) {
                ++i;
            }
        }

        return i;
    }

    private boolean canConnectTo(com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper pipePlacementHelper) {
        return this.connectsTo(pipePlacementHelper) || this.connections.size() < 2;
    }

    private void connectTo(com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper pipePlacementHelper) {
        this.connections.add(pipePlacementHelper.pos);
        BlockPos blockpos_n = this.pos.north();
        BlockPos blockpos_s = this.pos.south();
        BlockPos blockpos_w = this.pos.west();
        BlockPos blockpos_e = this.pos.east();
        BlockPos blockpos_u = this.pos.up();
        BlockPos blockpos_d = this.pos.down();
        boolean flag_n = this.hasConnection(blockpos_n);
        boolean flag_s = this.hasConnection(blockpos_s);
        boolean flag_w = this.hasConnection(blockpos_w);
        boolean flag_e = this.hasConnection(blockpos_e);
        boolean flag_u = this.hasConnection(blockpos_u);
        boolean flag_d = this.hasConnection(blockpos_d);
        PipeShape pipeshape = null;
        if (flag_n || flag_s) {
            pipeshape = PipeShape.NORTH_SOUTH;
        }

        if (flag_w || flag_e) {
            pipeshape = PipeShape.EAST_WEST;
        }

        if (flag_u || flag_d) {
            pipeshape = PipeShape.UP_DOWN;
        }

        if (flag_s && flag_e && !flag_n && !flag_w && !flag_u && !flag_d) {
            pipeshape = PipeShape.SOUTH_EAST;
        }

        if (flag_s && flag_w && !flag_n && !flag_e && !flag_u && !flag_d) {
            pipeshape = PipeShape.SOUTH_WEST;
        }

        if (flag_n && flag_w && !flag_s && !flag_e && !flag_u && !flag_d) {
            pipeshape = PipeShape.NORTH_WEST;
        }

        if (flag_n && flag_e && !flag_s && !flag_w && !flag_u && !flag_d) {
            pipeshape = PipeShape.NORTH_EAST;
        }

        if (flag_s && !flag_e && !flag_n && !flag_w && flag_u && !flag_d) {
            pipeshape = PipeShape.SOUTH_UP;
        }

        if (!flag_s && flag_w && !flag_n && !flag_e && flag_u && !flag_d) {
            pipeshape = PipeShape.WEST_UP;
        }

        if (flag_n && !flag_w && !flag_s && !flag_e && flag_u && !flag_d) {
            pipeshape = PipeShape.NORTH_UP;
        }

        if (!flag_n && flag_e && !flag_s && !flag_w && flag_u && !flag_d) {
            pipeshape = PipeShape.EAST_UP;
        }

        if (flag_s && !flag_e && !flag_n && !flag_w && !flag_u && flag_d) {
            pipeshape = PipeShape.SOUTH_DOWN;
        }

        if (!flag_s && flag_w && !flag_n && !flag_e && !flag_u && flag_d) {
            pipeshape = PipeShape.WEST_DOWN;
        }

        if (flag_n && !flag_w && !flag_s && !flag_e && !flag_u && flag_d) {
            pipeshape = PipeShape.NORTH_DOWN;
        }

        if (!flag_n && flag_e && !flag_s && !flag_w && !flag_u && flag_d) {
            pipeshape = PipeShape.EAST_DOWN;
        }

        if (pipeshape == null) {
            pipeshape = PipeShape.NORTH_SOUTH;
        }

        if (this.block instanceof PipeBlock)
        {
            this.state = this.state.with(PipeBlock.PIPE_SHAPE, pipeshape);
        }
        this.world.setBlockState(this.pos, this.state, 3);
    }

    private boolean hasNeighborPipe(BlockPos p_55447_) {
        com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper pipestate = this.getPipe(p_55447_);
        if (pipestate == null) {
            return false;
        } else {
            pipestate.removeSoftConnections();
            return pipestate.canConnectTo(this);
        }
    }

    public com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper place(boolean forceUpdate, PipeShape shape) {
        BlockPos blockpos_n = this.pos.north();
        BlockPos blockpos_s = this.pos.south();
        BlockPos blockpos_w = this.pos.west();
        BlockPos blockpos_e = this.pos.east();
        BlockPos blockpos_u = this.pos.up();
        BlockPos blockpos_d = this.pos.down();
        boolean flag_n = this.hasConnection(blockpos_n);
        boolean flag_s = this.hasConnection(blockpos_s);
        boolean flag_w = this.hasConnection(blockpos_w);
        boolean flag_e = this.hasConnection(blockpos_e);
        boolean flag_u = this.hasConnection(blockpos_u);
        boolean flag_d = this.hasConnection(blockpos_d);
        PipeShape pipeshape = null;
        boolean northsouth = flag_n || flag_s;
        boolean westeast = flag_w || flag_e;
        boolean updown = flag_u || flag_d;

        if (northsouth && !westeast && !updown) {
            pipeshape = PipeShape.NORTH_SOUTH;
        }

        if (westeast && !northsouth && !updown) {
            pipeshape = PipeShape.EAST_WEST;
        }

        if (!westeast && !northsouth && updown) {
            pipeshape = PipeShape.UP_DOWN;
        }

        boolean southeast = flag_s && flag_e;
        boolean southwest = flag_s && flag_w;
        boolean northeast = flag_n && flag_e;
        boolean northwest = flag_n && flag_w;
        boolean southup = flag_s && flag_u;
        boolean westup = flag_w && flag_u;
        boolean eastup = flag_e && flag_u;
        boolean northup = flag_n && flag_u;
        boolean southdown = flag_s && flag_d;
        boolean westdown = flag_w && flag_d;
        boolean eastdown = flag_e && flag_d;
        boolean northdown = flag_n && flag_d;
        if (southeast && !flag_n && !flag_w && !flag_u && !flag_d) {
            pipeshape = PipeShape.SOUTH_EAST;
        }

        if (southwest && !flag_n && !flag_e && !flag_u && !flag_d) {
            pipeshape = PipeShape.SOUTH_WEST;
        }

        if (northwest && !flag_s && !flag_e && !flag_u && !flag_d) {
            pipeshape = PipeShape.NORTH_WEST;
        }

        if (northeast && !flag_s && !flag_w && !flag_u && !flag_d) {
            pipeshape = PipeShape.NORTH_EAST;
        }

        if (southup && !flag_n && !flag_w && !flag_e && !flag_d) {
            pipeshape = PipeShape.SOUTH_UP;
        }

        if (westup && !flag_n && !flag_e && !flag_s && !flag_d) {
            pipeshape = PipeShape.WEST_UP;
        }

        if (eastup && !flag_s && !flag_w && !flag_n && !flag_d) {
            pipeshape = PipeShape.EAST_UP;
        }

        if (northup && !flag_s && !flag_w && !flag_e && !flag_d) {
            pipeshape = PipeShape.NORTH_UP;
        }

        if (southdown && !flag_n && !flag_w && !flag_e && !flag_u) {
            pipeshape = PipeShape.SOUTH_DOWN;
        }

        if (westdown && !flag_n && !flag_e && !flag_s && !flag_u) {
            pipeshape = PipeShape.WEST_DOWN;
        }

        if (eastdown && !flag_s && !flag_w && !flag_n && !flag_u) {
            pipeshape = PipeShape.EAST_DOWN;
        }

        if (northdown && !flag_s && !flag_w && !flag_e && !flag_u) {
            pipeshape = PipeShape.NORTH_DOWN;
        }

        if (pipeshape == null) {
            pipeshape = PipeShape.UP_DOWN;
        }

        this.updateConnections(pipeshape);
        if (this.block instanceof PipeBlock)
        {
            this.state = this.state.with(PipeBlock.PIPE_SHAPE, pipeshape);
        }
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for(int i = 0; i < this.connections.size(); ++i) {
                com.github.ilja615.iljatech.blocks.pipe.PipePlacementHelper pipestate = this.getPipe(this.connections.get(i));
                if (pipestate != null) {
                    pipestate.removeSoftConnections();
                    if (pipestate.canConnectTo(this)) {
                        pipestate.connectTo(this);
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
