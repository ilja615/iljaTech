package com.github.ilja615.iljatech.blocks.stampinghammer;

import com.github.ilja615.iljatech.init.ModRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class HammerBlock extends Block {
    Box ITEM_AREA_SHAPE = (Box) Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0).getBoundingBoxes().get(0).expand(1.0d);

    public HammerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (notify) {
            Box box = ITEM_AREA_SHAPE.offset(pos.getX(), pos.getY() + 0.5d, pos.getZ());
            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, EntityPredicates.VALID_ENTITY)) {
                if (itemEntity.getStack().isEmpty())
                    break;

                System.out.println("hi item");
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
                        break;
                    }
                }
            }
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }
}
