package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModParticles {
    public static final SimpleParticleType STAR = register("star", FabricParticleTypes.simple());
    public static final SimpleParticleType STEAM = register("steam", FabricParticleTypes.simple());
    public static final ParticleType<WindParticleEffect> WIND = register("wind", FabricParticleTypes.complex(type -> WindParticleEffect.CODEC, type -> WindParticleEffect.PACKET_CODEC));
    public static final SimpleParticleType WIND_LEADING = register("wind_leading", FabricParticleTypes.simple());

    public static <T extends ParticleType<?>> T register(String name, T particleType) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), particleType);
    }
}
