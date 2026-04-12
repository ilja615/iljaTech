package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.ExtraCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.BiFunction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class WindParticleEffect extends ParticleType<WindParticleEffect> implements ParticleOptions {

    public static final MapCodec<WindParticleEffect> CODEC;

    public static final StreamCodec<RegistryFriendlyByteBuf, WindParticleEffect> PACKET_CODEC;

    private final Vector2f direction;

    public Vector2f getDirection() {
        return this.direction;
    }

    public WindParticleEffect(Vector2f vector2f) {
        super(true);
        direction = vector2f;
    }

    public MapCodec<WindParticleEffect> codec() {
        return this.CODEC;
    }

    public StreamCodec<RegistryFriendlyByteBuf, WindParticleEffect> streamCodec() {
        return this.PACKET_CODEC;
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ExtraCodecs.VECTOR_2F.fieldOf("color").forGetter((effect) -> {
                return effect.direction;
            })).apply(instance, WindParticleEffect::new);
        });
        PACKET_CODEC = StreamCodec.composite(ExtraCodecs.VECTOR_2F_PACKET, (effect) -> {
            return ((WindParticleEffect)effect).direction;
        }, WindParticleEffect::new);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.WIND;
    }
}

