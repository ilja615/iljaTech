package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.mechanical_arm.MechanicalArmScreenHandler;
import com.github.ilja615.iljatech.screen.widget.ConditionalButtonWidget;
import com.github.ilja615.iljatech.screen.widget.FluidWidget;
import com.github.ilja615.iljatech.screen.widget.TwoCaseButtonWidget;
import com.github.ilja615.iljatech.util.Triple;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class MechanicalArmScreen extends AbstractContainerScreen<MechanicalArmScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/mechanical_arm.png");
    private final List<AbstractWidget> buttons = Lists.newArrayList();

    public MechanicalArmScreen(MechanicalArmScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 133;
        this.inventoryLabelY = 39;
    }

    private <T extends AbstractWidget> void addButton(T button) {
        this.addRenderableWidget(button);
        this.buttons.add(button);
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.addButton(new TwoCaseButtonWidget(this.leftPos + 97, this.topPos + 19, 18, 18, TEXTURE, 176, 0, this::mouseSide, this.menu::getSetting));
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);
    }

    public void mouseSide() {
        this.minecraft.getConnection().send(new ServerboundContainerButtonClickPacket(menu.containerId, 0));
    }
}