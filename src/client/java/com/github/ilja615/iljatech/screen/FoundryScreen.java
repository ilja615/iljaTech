package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.foundry.FoundryScreenHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class FoundryScreen extends AbstractContainerScreen<FoundryScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/foundry.png");

    public FoundryScreen(FoundryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int l = Mth.ceil(((FoundryScreenHandler)this.menu).getProgress() * 27);
        context.blit(TEXTURE,this.leftPos + 85, this.topPos + 36, 176, 0, l, 16);
        switch (((FoundryScreenHandler)this.menu).getLitState()) {
            case ON -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 16, 24, 14);
            case STOKED -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 30, 24, 14);
            case CHOKING -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 44, 24, 14);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);

        if (isHovering(39, 54, 24, 14, mouseX, mouseY)) {
            String toolTip = switch (((FoundryScreenHandler)this.menu).getLitState()) {
                case ON -> "Firebox burning as normally";
                case OFF -> "Provide heat with large firebox";
                case STOKED -> "Firebox is being stoked";
                case CHOKING -> "Firebox choking. Cleaning needed.";
            };
            context.renderTooltip(this.font, Component.literal(toolTip), mouseX, mouseY);
        }
    }
}