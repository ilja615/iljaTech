package com.github.ilja615.iljatech.blocks.windmill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class WindParticleType extends ParticleType<WindParticleType> implements ParticleEffect {

    public final MapCodec<WindParticleType> CODEC = RecordCodecBuilder.mapCodec((instance)
            -> instance.group(Codec.STRING.fieldOf("direction").forGetter((particleEffect)
            -> particleEffect.direction.asString())).apply(instance, (s -> { return new WindParticleType(s);})));

    public static final PacketCodec<RegistryByteBuf, WindParticleType> PACKET_CODEC;

    public WindDirection getDirection() {
        return direction;
    }

    private final WindDirection direction;

    protected WindParticleType(String s) {
        super(true);
        WindDirection d = WindDirection.N;
        for (WindDirection i : WindDirection.values()) {
            if (i.asString().equals(s))
                d = i;
        }
        direction = d;
    }

    public WindParticleType getType() {
        return this;
    }

    public MapCodec<WindParticleType> getCodec() {
        return this.CODEC;
    }

    public PacketCodec<RegistryByteBuf, WindParticleType> getPacketCodec() {
        return this.PACKET_CODEC;
    }

    static {
        PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, (effect) -> {
            return effect.direction.asString();
        }, WindParticleType::new);
    }
}

