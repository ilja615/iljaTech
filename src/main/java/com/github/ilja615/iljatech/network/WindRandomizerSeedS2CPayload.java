package com.github.ilja615.iljatech.network;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record WindRandomizerSeedS2CPayload(long seed) implements CustomPayload {
    public static final CustomPayload.Id<WindRandomizerSeedS2CPayload> ID = new CustomPayload.Id<>(Identifier.of(IljaTech.MOD_ID, "wind_randomizer_seed"));
    public static final PacketCodec<RegistryByteBuf, WindRandomizerSeedS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, WindRandomizerSeedS2CPayload::seed, WindRandomizerSeedS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
