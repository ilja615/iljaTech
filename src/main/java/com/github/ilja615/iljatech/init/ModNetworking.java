package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.network.WindRandomizerSeedS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {
    public static void load() {
        PayloadTypeRegistry.playS2C().register(WindRandomizerSeedS2CPayload.ID, WindRandomizerSeedS2CPayload.CODEC);
    }
}
