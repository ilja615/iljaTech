package com.github.ilja615.iljatech.init;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.carpentry.CarpentryScreenHandler;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenScreenHandler;
import com.github.ilja615.iljatech.blocks.foundry.FoundryScreenHandler;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchScreenHandler;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintTableScreenHandler;
import com.github.ilja615.iljatech.blocks.squeezer.SqueezerScreenHandler;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlerTypes {
    public static final MenuType<FoundryScreenHandler> FOUNDRY =
            register("foundry", FoundryScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final MenuType<CokeOvenScreenHandler> COKE_OVEN =
            register("coke_oven", CokeOvenScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final MenuType<ItemHatchScreenHandler> ITEM_HATCH =
            register("item_hatch", ItemHatchScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final MenuType<SqueezerScreenHandler> SQUEEZER =
            register("squeezer", SqueezerScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final MenuType<CarpentryScreenHandler> CARPENTRY =
            register("carpentry", CarpentryScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final MenuType<BlueprintTableScreenHandler> RESEARCH =
            register("blueprints", BlueprintTableScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends AbstractContainerMenu, D extends CustomPacketPayload> ExtendedScreenHandlerType<T, D> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, name), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void load() {}
}
