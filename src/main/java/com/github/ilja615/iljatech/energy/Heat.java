package com.github.ilja615.iljatech.energy;

import com.github.ilja615.iljatech.blocks.turbine.TurbineBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.init.ModRecipeTypes;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class Heat {
    public static void emitHeat(World world, BlockPos blockPos) {
        if (world.getBlockState(blockPos).isOf(Blocks.WATER_CAULDRON) && world.getBlockState(blockPos).getBlock() instanceof LeveledCauldronBlock) {
            boolean flag = false;
            // Detect for the first turbine in 2-6 blocks up
            for (int y = 2; y <= 6; y++) {
                // Check if there is turbine
                if (world.getBlockState(blockPos.up(y)).isOf(ModBlocks.TURBINE) && world.getBlockEntity(blockPos.up(y)) instanceof TurbineBlockEntity turbineBlockEntity) {

                    // Check if there is no steam going to the turbine yet
                    if (turbineBlockEntity.getSteamY() == -1.0f) {
                        // If the turbine is found, set it so that there will go a steam cloud to that
                        turbineBlockEntity.setSteamY(blockPos.getY());
                        LeveledCauldronBlock.decrementFluidLevel(world.getBlockState(blockPos), world, blockPos);
                        flag = true;
                    }
                    break;
                }

                // The steam can only go through air
                if (!world.getBlockState(blockPos.up(y)).isAir()) {
                    break;
                }
            }

            // Check boiling recipes and puff some steam particles in case there was no turbine
            if (!flag) {
                LeveledCauldronBlock.decrementFluidLevel(world.getBlockState(blockPos), world, blockPos);
                if (!world.isClient) {
                    ((ServerWorld) world).spawnParticles(ModParticles.STEAM, blockPos.getX() + world.random.nextFloat() * 0.5f + 0.25f, blockPos.getY() + world.random.nextFloat() * 0.5f + 1.0f, blockPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f, 5, 0.0f, 0.3f, 0.0f, 0.0);
                }

                List<ItemEntity> itemEntityList= world.getEntitiesByClass(ItemEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR);
                for (ItemEntity itemEntity : itemEntityList)
                {
                    if (itemEntity.getStack().isEmpty())
                        break;

                    List<RecipeEntry<BoilingRecipe>> recipes = world.getRecipeManager().listAllOfType(ModRecipeTypes.BOILING_TYPE);
                    for (RecipeEntry<BoilingRecipe> rr : recipes)
                    {
                        BoilingRecipe r = rr.value();
                        ItemStack resultingStack = r.output().copy();
                        if (r.stack().getMatchingStacks()[0].isEmpty() || itemEntity.getStack().isEmpty())
                            continue;

                        if (r.stack().getMatchingStacks()[0].getItem() == itemEntity.getStack().getItem())
                        {
                            itemEntity.getStack().decrement(1);
                            world.spawnEntity(new ItemEntity(world, blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d, resultingStack));
                            break;
                        }
                    }
                }
            }
        }
    }
}
