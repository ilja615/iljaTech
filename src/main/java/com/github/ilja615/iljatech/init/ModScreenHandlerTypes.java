package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlockEntity;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenScreenHandler;
import com.github.ilja615.iljatech.blocks.foundry.FoundryScreenHandler;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchScreenHandler;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerTypes {
    public static final ScreenHandlerType<FoundryScreenHandler> FOUNDRY =
            register("foundry", FoundryScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final ScreenHandlerType<CokeOvenScreenHandler> COKE_OVEN =
            register("coke_oven", CokeOvenScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final ScreenHandlerType<ItemHatchScreenHandler> ITEM_HATCH =
            register("item_hatch", ItemHatchScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends ScreenHandler, D extends CustomPayload> ExtendedScreenHandlerType<T, D> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(IljaTech.MOD_ID, name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void load() {}
}
