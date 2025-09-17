package com.github.ilja615.iljatech.blocks.stampinghammer;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlock;
import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.init.ModSounds;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HammerBlock extends Block {
    Box ITEM_AREA_SHAPE = (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0).expand(1.0d);
    protected static final VoxelShape HITBOX_SHAPE = Block.createCuboidShape(0.0, 15.5, 0.0, 16.0, 16.0, 16.0);

    public HammerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityShapeContext)
            if (entityShapeContext.getEntity() instanceof ItemEntity)
                return HITBOX_SHAPE;
        return VoxelShapes.fullCube();
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify && !world.isClient()) {
            Box box = ITEM_AREA_SHAPE.offset(pos.getX(), pos.getY() + 0.5d, pos.getZ());
            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
                if (itemEntity.getStack().isEmpty())
                    break;

                List<RecipeEntry<StampingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.STAMPING_TYPE);
                for (RecipeEntry<StampingRecipe> rr : recipes)
                {
                    StampingRecipe r = rr.value();
                    ItemStack resultingStack = r.output().copy();
                    if (r.stack().getMatchingStacks()[0].isEmpty() || itemEntity.getStack().isEmpty())
                        continue;

                    if (r.stack().getMatchingStacks()[0].getItem() == itemEntity.getStack().getItem())
                    {
                        itemEntity.getStack().decrement(1);
                        world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.1d, pos.getZ() + 0.5d, resultingStack));
                        world.playSound(null, pos, ModSounds.STAMPING_HAMMER_BLOCK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        return;
                    }
                }
            }
            if (world.getBlockState(pos.down()).isOf(ModBlocks.CONVEYOR_BELT) && world.getBlockEntity(pos.down()) instanceof ConveyorBeltBlockEntity blockEntity) {
                for (int i = 0; i < blockEntity.getStacks().size(); ++i) {
                    Pair<ItemStack, Vec3d> pair = blockEntity.getStacks().get(i);
                    ItemStack itemStack = pair.getFirst();
                    Vec3d offset = pair.getSecond();
                    List<RecipeEntry<StampingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.STAMPING_TYPE);
                    for (RecipeEntry<StampingRecipe> rr : recipes)
                    {
                        StampingRecipe r = rr.value();
                        ItemStack resultingStack = r.output().copy();
                        if (r.stack().getMatchingStacks()[0].isEmpty() || itemStack.isEmpty())
                            continue;

                        if (r.stack().getMatchingStacks()[0].getItem() == itemStack.getItem())
                        {
                            if (itemStack.getCount() == 1) {
                                blockEntity.getStacks().remove(i);
                            } else {
                                itemStack.decrement(1);
                            }
                            Direction facing = blockEntity.getCachedState().get(ConveyorBeltBlock.FACING);
                            blockEntity.getStacks().add(new Pair<>(resultingStack, new Vec3d(blockEntity.getXforProgress(facing, 0.2f), 1.25d, blockEntity.getZforProgress(facing, 0.2f))));
                            blockEntity.update();
                            world.playSound(null, pos, ModSounds.STAMPING_HAMMER_BLOCK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            return;
                        }
                    }
                }
            }
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }
}
