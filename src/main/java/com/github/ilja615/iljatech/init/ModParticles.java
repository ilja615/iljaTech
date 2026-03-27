package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleType;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType STAR = register("star", FabricParticleTypes.simple());
    public static final SimpleParticleType STEAM = register("steam", FabricParticleTypes.simple());
    public static final ParticleType WIND = register("wind", FabricParticleTypes.complex(ParticleType::getCodec, ParticleType::getPacketCodec));
    public static final ParticleType WIND_LEADING = register("wind_leading", FabricParticleTypes.complex(ParticleType::getCodec, ParticleType::getPacketCodec));

    public static <T extends ParticleType<?>> T register(String name, T particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(IljaTech.MOD_ID, name), particleType);
    }
}
