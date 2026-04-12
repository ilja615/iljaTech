package com.github.ilja615.iljatech.network;

import com.github.ilja615.iljatech.IljaTech;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WindRandomizerSeedS2CPayload(long seed) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WindRandomizerSeedS2CPayload> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "wind_randomizer_seed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WindRandomizerSeedS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, WindRandomizerSeedS2CPayload::seed, WindRandomizerSeedS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
