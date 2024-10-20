package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.rollermill.RollerMillBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntityTypes {
    public static final BlockEntityType<RollerMillBlockEntity> ROLLER_MILL = register("roller_mill",
            BlockEntityType.Builder.create(RollerMillBlockEntity::new, ModBlocks.ROLLER_MILL).build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type)
    {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(IljaTech.MOD_ID, name), type);
    }

    public static void load() {}
}
