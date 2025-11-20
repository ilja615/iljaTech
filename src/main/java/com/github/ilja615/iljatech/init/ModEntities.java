package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.entities.SeatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;


public class ModEntities {
    public static final EntityType<SeatEntity> SEAT =
            register("seat_entity",
                    EntityType.Builder.<SeatEntity>create(((type, world) -> new SeatEntity(world)),
                                    SpawnGroup.MISC)
                            .dimensions(0f, 0f));

    public static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        Identifier id = Identifier.of(IljaTech.MOD_ID, name);
        return Registry.register(Registries.ENTITY_TYPE, id, builder.build(id.toString()));
    }

    public static void load() {}
}
