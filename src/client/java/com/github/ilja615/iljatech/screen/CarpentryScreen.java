package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.carpentry.CarpentryScreenHandler;
import com.github.ilja615.iljatech.screen.widget.ConditionalButtonWidget;
import com.github.ilja615.iljatech.screen.widget.FluidWidget;
import com.github.ilja615.iljatech.util.FluidItemSlot;
import com.github.ilja615.iljatech.util.MaxStackSize1Slot;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import java.util.List;

public class CarpentryScreen extends AbstractContainerScreen<CarpentryScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/carpentry.png");
    private final List<AbstractWidget> buttons = Lists.newArrayList();

    public CarpentryScreen(CarpentryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private <T extends AbstractWidget> void addButton(T button) {
        this.addRenderableWidget(button);
        this.buttons.add(button);
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        addRenderableOnly(new FluidWidget(this.menu.getBlockEntity().getFluidStorage(),
                this.leftPos + 103, this.topPos + 17, () -> this.menu.getBlockEntity().getBlockPos(), this.font));

        this.addButton(new ConditionalButtonWidget(this.leftPos + 7, this.topPos + 34, 18, 18, TEXTURE, 176, 0, this::hammer, this.menu::canHammer));

        this.addButton(new ConditionalButtonWidget(this.leftPos + 7, this.topPos + 52, 18, 18, TEXTURE, 176, 18, this::saw, () -> new Tuple<>(true, "")));

        this.addButton(new ConditionalButtonWidget(this.leftPos + 102, this.topPos + 34, 18, 18, TEXTURE, 176, 36, this::finish, this.menu::canFinish));
    }

    public void hammer() {
        this.minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
    }

    public void saw() {
        this.minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, 1));
    }

    public void finish() {
        this.minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, 2));
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        super.slotClicked(slot, slotId, button, actionType);
        if (slot != null) {
            this.minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, 3));
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }
}