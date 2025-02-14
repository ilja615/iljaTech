package com.github.ilja615.iljatech.energy;

import com.github.ilja615.iljatech.blocks.turbine.TurbineBlockEntity;
import com.github.ilja615.iljatech.init.ModBlocks;
import com.github.ilja615.iljatech.init.ModItems;
import com.github.ilja615.iljatech.init.ModParticles;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
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
                        turbineBlockEntity.setSteamY(blockPos.getY());
                        flag = true;
                    }
                    break;
                }

                // The steam can only go through air
                if (!world.getBlockState(blockPos.up(y)).isAir()) {
                    break;
                }
            }

            // Show some particles of steam if there was no turbine found
            if (!flag) {
                //LeveledCauldronBlock.decrementFluidLevel(world.getBlockState(blockPos), world, blockPos);
                if (!world.isClient) {
                    ((ServerWorld) world).spawnParticles(ModParticles.STEAM, blockPos.getX() + world.random.nextFloat() * 0.5f + 0.25f, blockPos.getY() + world.random.nextFloat() * 0.5f + 1.0f, blockPos.getZ() + world.random.nextFloat() * 0.5f + 0.25f, 5, 0.0f, 0.3f, 0.0f, 0.0);
                }

                List<ItemEntity> itemEntityList= world.getEntitiesByClass(ItemEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR);
                for (ItemEntity itemEntity : itemEntityList)
                {
                    if (itemEntity.getStack().isEmpty())
                        break;

                    if (itemEntity.getStack().isOf(Items.EGG)) {
                        itemEntity.getStack().decrement(1);
                        world.spawnEntity(new ItemEntity(world, blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d, new ItemStack(ModItems.BOILED_EGG, 1)));
                    }
                }
            }
        }
    }
}
