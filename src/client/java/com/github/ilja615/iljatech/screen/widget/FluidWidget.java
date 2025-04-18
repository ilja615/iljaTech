package com.github.ilja615.iljatech.screen.widget;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidWidget implements Drawable, Widget {
    private final SingleFluidStorage fluidTank;
    private final Supplier<BlockPos> posSupplier;
    private int x;
    private int y;
    private TextRenderer textRenderer;

    public FluidWidget(SingleFluidStorage fluidTank, int x, int y, Supplier<BlockPos> posSupplier, TextRenderer textRenderer) {
        this.fluidTank = fluidTank;
        this.x = x;
        this.y = y;
        this.posSupplier = posSupplier;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Fluid fluid = this.fluidTank.variant.getFluid();
        long fluidAmount = this.fluidTank.getAmount();
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
        if (fluidRenderHandler == null || fluidAmount <= 0)
            return;

        BlockPos pos = this.posSupplier.get();
        FluidState fluidState = fluid.getDefaultState();
        World world = MinecraftClient.getInstance().world;

        Sprite stillTexture = fluidRenderHandler.getFluidSprites(world, pos, fluidState)[0];
        int tintColor = fluidRenderHandler.getFluidColor(world, pos, fluidState);

        float red = (tintColor >> 16 & 0xFF) / 255.0F;
        float green = (tintColor >> 8 & 0xFF) / 255.0F;
        float blue = (tintColor & 0xFF) / 255.0F;
        context.drawSprite(this.x, this.y, 0, 16, 16, stillTexture, red, green, blue, 1.0f);

        String string = String.valueOf(MathHelper.floor((float) fluidAmount / FluidConstants.BUCKET * 10f)/10.0f);
        context.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, 16777215, true);

        if (isPointWithinBounds(this.x, this.y, mouseX, mouseY)) {
            drawTooltip(context, mouseX, mouseY);
//            context.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.y, this.x + 16, this.y + 16, -2130706433, -2130706433, 0);
        }
    }

    private static boolean isPointWithinBounds(int x, int y, int pointX, int pointY) {
        return pointX >= x && pointX <= x + 16 && pointY >= y && pointY <= y + 16;
    }

    protected void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        Fluid fluid = this.fluidTank.variant.getFluid();

        long fluidAmount = this.fluidTank.getAmount();
        long fluidCapacity = this.fluidTank.getCapacity();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (fluid != null && fluidAmount > 0) {
            List<Text> texts = List.of(
                    Text.translatable(fluid.getDefaultState().getBlockState().getBlock().getTranslationKey()),
                    Text.literal(((int) (((float) fluidAmount / FluidConstants.BUCKET) * 1000)) + " / " + ((int) (((float) fluidCapacity / FluidConstants.BUCKET) * 1000)) + " mB"));
            context.drawTooltip(textRenderer, texts, mouseX, mouseY);
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
    public void forEachChild(Consumer<ClickableWidget> consumer) {

    }
}
