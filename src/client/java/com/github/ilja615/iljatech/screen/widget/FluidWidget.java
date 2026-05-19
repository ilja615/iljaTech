package com.github.ilja615.iljatech.screen.widget;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidWidget implements Renderable, LayoutElement {
    private final SingleFluidStorage fluidTank;
    private final Supplier<BlockPos> posSupplier;
    private int x;
    private int y;
    private Font textRenderer;

    public FluidWidget(SingleFluidStorage fluidTank, int x, int y, Supplier<BlockPos> posSupplier, Font textRenderer) {
        this.fluidTank = fluidTank;
        this.x = x;
        this.y = y;
        this.posSupplier = posSupplier;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Fluid fluid = this.fluidTank.variant.getFluid();
        long fluidAmount = this.fluidTank.getAmount();
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (fluidRenderHandler == null || fluidAmount <= 0)
            return;

        BlockPos pos = this.posSupplier.get();
        FluidState fluidState = fluid.defaultFluidState();
        Level world = Minecraft.getInstance().level;

        TextureAtlasSprite stillTexture = fluidRenderHandler.getFluidSprites(world, pos, fluidState)[0];
        int tintColor = fluidRenderHandler.getFluidColor(world, pos, fluidState);

        float red = (tintColor >> 16 & 0xFF) / 255.0F;
        float green = (tintColor >> 8 & 0xFF) / 255.0F;
        float blue = (tintColor & 0xFF) / 255.0F;
        context.blit(this.x, this.y, 0, 16, 16, stillTexture, red, green, blue, 1.0f);

        String string = String.valueOf(Mth.floor((float) fluidAmount / FluidConstants.BUCKET * 10f)/10.0f);
        context.drawString(textRenderer, string, x + 19 - 2 - textRenderer.width(string), y + 6 + 3, 16777215, true);

        if (isPointWithinBounds(this.x, this.y, mouseX, mouseY)) {
            drawTooltip(context, mouseX, mouseY);
//            context.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.y, this.x + 16, this.y + 16, -2130706433, -2130706433, 0);
        }
    }

    private static boolean isPointWithinBounds(int x, int y, int pointX, int pointY) {
        return pointX >= x && pointX <= x + 16 && pointY >= y && pointY <= y + 16;
    }

    protected void drawTooltip(GuiGraphics context, int mouseX, int mouseY) {
        Fluid fluid = this.fluidTank.variant.getFluid();

        long fluidAmount = this.fluidTank.getAmount();
        long fluidCapacity = this.fluidTank.getCapacity();

        Font textRenderer = Minecraft.getInstance().font;
        if (fluid != null && fluidAmount > 0) {
            List<Component> texts = List.of(
                    Component.translatable(fluid.defaultFluidState().createLegacyBlock().getBlock().getDescriptionId()),
                    Component.literal(((int) (((float) fluidAmount / FluidConstants.BUCKET) * 1000)) + " / " + ((int) (((float) fluidCapacity / FluidConstants.BUCKET) * 1000)) + " mB"));
            context.renderComponentTooltip(textRenderer, texts, mouseX, mouseY);
        }
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    public SingleFluidStorage getFluidTank() {
        return fluidTank;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {

    }
}
