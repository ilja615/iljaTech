package com.github.ilja615.iljatech.energy;

import com.github.ilja615.iljatech.blocks.turbine.TurbineBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.phys.AABB;

public class Heat {
    public static void emitHeat(Level world, BlockPos blockPos) {
        if (world.getBlockState(blockPos).is(Blocks.WATER_CAULDRON) && world.getBlockState(blockPos).getBlock() instanceof LayeredCauldronBlock) {
            boolean flag = false;
            // Detect for the first turbine in 2-6 blocks up
            for (int y = 2; y <= 6; y++) {
                // Check if there is turbine
                if (world.getBlockState(blockPos.above(y)).is(ModBlocks.TURBINE) && world.getBlockEntity(blockPos.above(y)) instanceof TurbineBlockEntity turbineBlockEntity) {

                    // Check if there is no steam going to the turbine yet
                    if (turbineBlockEntity.getSteamY() == -1.0f) {
                        // If the turbine is found, set it so that there will go a steam cloud to that
                        turbineBlockEntity.setSteamY(blockPos.getY());
                        LayeredCauldronBlock.lowerFillLevel(world.getBlockState(blockPos), world, blockPos);
                        flag = true;
                    }
                    break;
                }

                // The steam can only go through air
                if (!world.getBlockState(blockPos.above(y)).isAir()) {
                    break;
                }
            }

            // Check boiling recipes and puff some steam particles in case there was no turbine
            if (!flag) {
                LayeredCauldronBlock.lowerFillLevel(world.getBlockState(blockPos), world, blockPos);
                if (!world.isClientSide) {
                    ((ServerLevel) world).sendParticles(ModParticles.STEAM, blockPos.getX() + world.random.nextFloat() * 0.5f + 0.25f, blockPos.getY() + world.random.nextFloat() * 0.5f + 1.0f, blockPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f, 5, 0.0f, 0.3f, 0.0f, 0.0);
                }

                List<ItemEntity> itemEntityList= world.getEntitiesOfClass(ItemEntity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS);
                for (ItemEntity itemEntity : itemEntityList)
                {
                    if (itemEntity.getItem().isEmpty())
                        break;

                    List<RecipeHolder<BoilingRecipe>> recipes = world.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BOILING_TYPE);
                    for (RecipeHolder<BoilingRecipe> rr : recipes)
                    {
                        BoilingRecipe r = rr.value();
                        ItemStack resultingStack = r.output().copy();
                        if (r.stack().getItems()[0].isEmpty() || itemEntity.getItem().isEmpty())
                            continue;

                        if (r.stack().getItems()[0].getItem() == itemEntity.getItem().getItem())
                        {
                            itemEntity.getItem().shrink(1);
                            world.addFreshEntity(new ItemEntity(world, blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d, resultingStack));
                            break;
                        }
                    }
                }
            }
        }
    }
}
