package com.github.ilja615.iljatech.blocks.stampinghammer;

import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlock;
import com.github.ilja615.iljatech.blocks.conveyorbelt.ConveyorBeltBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import com.github.ilja615.iljatech.init.ModSounds;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HammerBlock extends Block {
    AABB ITEM_AREA_SHAPE = (AABB) Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).toAabbs().get(0).inflate(1.0d);
    protected static final VoxelShape HITBOX_SHAPE = Block.box(0.0, 15.5, 0.0, 16.0, 16.0, 16.0);

    public HammerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityShapeContext)
            if (entityShapeContext.getEntity() instanceof ItemEntity)
                return HITBOX_SHAPE;
        return Shapes.block();
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify && !world.isClientSide()) {
            AABB box = ITEM_AREA_SHAPE.move(pos.getX(), pos.getY() + 0.5d, pos.getZ());
            for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE)) {
                if (itemEntity.getItem().isEmpty())
                    break;

                List<RecipeHolder<StampingRecipe>> recipes = world.getRecipeManager().getAllRecipesFor(ModRecipeTypes.STAMPING_TYPE);
                for (RecipeHolder<StampingRecipe> rr : recipes)
                {
                    StampingRecipe r = rr.value();
                    ItemStack resultingStack = r.output().copy();
                    if (r.stack().getItems()[0].isEmpty() || itemEntity.getItem().isEmpty())
                        continue;

                    if (r.stack().getItems()[0].getItem() == itemEntity.getItem().getItem())
                    {
                        itemEntity.getItem().shrink(1);
                        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.1d, pos.getZ() + 0.5d, resultingStack));
                        world.playSound(null, pos, ModSounds.STAMPING_HAMMER_BLOCK, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return;
                    }
                }
            }
            if (world.getBlockState(pos.below()).is(ModBlocks.CONVEYOR_BELT) && world.getBlockEntity(pos.below()) instanceof ConveyorBeltBlockEntity blockEntity) {
                for (int i = 0; i < blockEntity.getStacks().size(); ++i) {
                    Pair<ItemStack, Vec3> pair = blockEntity.getStacks().get(i);
                    ItemStack itemStack = pair.getFirst();
                    Vec3 offset = pair.getSecond();
                    List<RecipeHolder<StampingRecipe>> recipes = world.getRecipeManager().getAllRecipesFor(ModRecipeTypes.STAMPING_TYPE);
                    for (RecipeHolder<StampingRecipe> rr : recipes)
                    {
                        StampingRecipe r = rr.value();
                        ItemStack resultingStack = r.output().copy();
                        if (r.stack().getItems()[0].isEmpty() || itemStack.isEmpty())
                            continue;

                        if (r.stack().getItems()[0].getItem() == itemStack.getItem())
                        {
                            if (itemStack.getCount() == 1) {
                                blockEntity.getStacks().remove(i);
                            } else {
                                itemStack.shrink(1);
                            }
                            Direction facing = blockEntity.getBlockState().getValue(ConveyorBeltBlock.FACING);
                            blockEntity.getStacks().add(new Pair<>(resultingStack, new Vec3(blockEntity.getXforProgress(facing, 0.2f), 1.25d, blockEntity.getZforProgress(facing, 0.2f))));
                            blockEntity.update();
                            world.playSound(null, pos, ModSounds.STAMPING_HAMMER_BLOCK, SoundSource.BLOCKS, 1.0f, 1.0f);
                            return;
                        }
                    }
                }
            }
        }
        super.onPlace(state, world, pos, oldState, notify);
    }
}
