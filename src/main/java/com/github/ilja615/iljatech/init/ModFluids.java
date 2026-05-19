package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.fluids.AbstractFluid;
import com.github.ilja615.iljatech.fluids.CreosoteOilFluid;
import com.github.ilja615.iljatech.fluids.SeedOilFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import java.util.function.Supplier;

public class ModFluids {
    public static final FlowingFluid STILL_CREOSOTE_OIL = Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "creosote_oil"), new CreosoteOilFluid.Still());
    public static final FlowingFluid  FLOWING_CREOSOTE_OIL  = Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "flowing_creosote_oil"), new CreosoteOilFluid.Flowing());
    public static final Item CREOSOTE_OIL_BUCKET = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "creosote_oil_bucket"),
            new BucketItem(STILL_CREOSOTE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Block CREOSOTE_OIL_BLOCK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "creosote_oil"),
            new LiquidBlock(STILL_CREOSOTE_OIL, FabricBlockSettings.ofFullCopy(Blocks.WATER)));

    public static final FlowingFluid STILL_SEED_OIL = Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "seed_oil"), new SeedOilFluid.Still());
    public static final FlowingFluid  FLOWING_SEED_OIL  = Registry.register(BuiltInRegistries.FLUID, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "flowing_seed_oil"), new SeedOilFluid.Flowing());
    public static final Item SEED_OIL_BUCKET = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "seed_oil_bucket"),
            new BucketItem(STILL_SEED_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final Block SEED_OIL_BLOCK = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "seed_oil"),
            new LiquidBlock(STILL_SEED_OIL, FabricBlockSettings.ofFullCopy(Blocks.WATER)));

    public static void load() {}
}
