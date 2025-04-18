package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.fluids.AbstractFluid;
import com.github.ilja615.iljatech.fluids.CreosoteOilFluid;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModFluids {
    public static final FlowableFluid STILL_CREOSOTE_OIL = Registry.register(Registries.FLUID, Identifier.of(IljaTech.MOD_ID, "creosote_oil"), new CreosoteOilFluid.Still());
    public static final FlowableFluid  FLOWING_CREOSOTE_OIL  = Registry.register(Registries.FLUID, Identifier.of(IljaTech.MOD_ID, "flowing_creosote_oil"), new CreosoteOilFluid.Flowing());
    public static final Item CREOSOTE_OIL_BUCKET = Registry.register(Registries.ITEM, Identifier.of(IljaTech.MOD_ID, "creosote_oil_bucket"),
            new BucketItem(STILL_CREOSOTE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
    public static final Block CREOSOTE_OIL_BLOCK = Registry.register(Registries.BLOCK, Identifier.of(IljaTech.MOD_ID, "creosote_oil"),
            new FluidBlock(STILL_CREOSOTE_OIL, FabricBlockSettings.copy(Blocks.WATER)));

    public static void load() {}
}
