package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenBlockEntity;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenScreenHandler;
import com.github.ilja615.iljatech.blocks.foundry.FoundryScreenHandler;
import com.github.ilja615.iljatech.screen.widget.FluidWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class CokeOvenScreen extends AbstractContainerScreen<CokeOvenScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/coke_oven.png");

    public CokeOvenScreen(CokeOvenScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new FluidWidget(this.menu.getBlockEntity().getFluidStorage(),
                this.leftPos + 116, this.topPos + 42, () -> this.menu.getBlockEntity().getBlockPos(), this.font));
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int l = Mth.ceil((float) ((CokeOvenScreenHandler) this.menu).getTicks() / CokeOvenBlockEntity.PROCESS_TIME * 48);
        context.blit(TEXTURE,this.leftPos + 64, this.topPos + 24, 176, 0, l, 16);
        switch (((CokeOvenScreenHandler)this.menu).getLitState()) {
            case ON -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 16, 24, 14);
            case STOKED -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 30, 24, 14);
            case CHOKING -> context.blit(TEXTURE,this.leftPos + 39, this.topPos + 54, 176, 44, 24, 14);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);

        if (isHovering(76, 62, 24, 14, mouseX, mouseY)) {
            String toolTip = switch (((CokeOvenScreenHandler)this.menu).getLitState()) {
                case ON -> "Firebox burning as normally";
                case OFF -> "Provide heat with large firebox";
                case STOKED -> "Firebox is being stoked";
                case CHOKING -> "Firebox choking. Cleaning needed.";
            };
            context.renderTooltip(this.font, Component.literal(toolTip), mouseX, mouseY);
        }
        if (isHovering(64, 24, 48, 16, mouseX, mouseY) && ((CokeOvenScreenHandler) this.menu).getTicks() > 0) {
            int ticksRemaining = CokeOvenBlockEntity.PROCESS_TIME - ((CokeOvenScreenHandler) this.menu).getTicks();
            int minutes = Mth.floor(ticksRemaining / 1200f);
            int seconds = Mth.floor((ticksRemaining % 1200f) / 20f);
            String toolTip = "Time left: " + (minutes < 10 ? "0"+minutes : minutes) + ":" + (seconds < 10 ? "0"+seconds : seconds);
            context.renderTooltip(this.font, Component.literal(toolTip), mouseX, mouseY);
        }
    }

}