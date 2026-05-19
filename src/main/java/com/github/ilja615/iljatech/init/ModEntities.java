package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.entities.SeatEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;


public class ModEntities {
    public static final EntityType<SeatEntity> SEAT =
            register("seat_entity",
                    EntityType.Builder.<SeatEntity>of(((type, world) -> new SeatEntity(world)),
                                    MobCategory.MISC)
                            .sized(0f, 0f));

    public static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id.toString()));
    }

    public static void load() {}
}
