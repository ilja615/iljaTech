package com.github.ilja615.iljatech.blocks.windmill;

import com.github.ilja615.iljatech.init.ModParticles;
import com.github.ilja615.iljatech.util.ExtraCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.BiFunction;

public class WindParticleEffect extends ParticleType<WindParticleEffect> implements ParticleEffect {

    public static final MapCodec<WindParticleEffect> CODEC;

    public static final PacketCodec<RegistryByteBuf, WindParticleEffect> PACKET_CODEC;

    private final Vector2f direction;

    public Vector2f getDirection() {
        return this.direction;
    }

    public WindParticleEffect(Vector2f vector2f) {
        super(true);
        direction = vector2f;
    }

    public MapCodec<WindParticleEffect> getCodec() {
        return this.CODEC;
    }

    public PacketCodec<RegistryByteBuf, WindParticleEffect> getPacketCodec() {
        return this.PACKET_CODEC;
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(ExtraCodecs.VECTOR_2F.fieldOf("color").forGetter((effect) -> {
                return effect.direction;
            })).apply(instance, WindParticleEffect::new);
        });
        PACKET_CODEC = PacketCodec.tuple(ExtraCodecs.VECTOR_2F_PACKET, (effect) -> {
            return ((WindParticleEffect)effect).direction;
        }, WindParticleEffect::new);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.WIND;
    }
}

