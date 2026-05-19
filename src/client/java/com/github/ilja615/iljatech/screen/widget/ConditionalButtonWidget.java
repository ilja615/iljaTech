package com.github.ilja615.iljatech.screen.widget;

import com.github.ilja615.iljatech.screen.CarpentryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConditionalButtonWidget extends AbstractWidget {
    private ResourceLocation texture;
    private int x;
    private int y;
    private int u;
    private int v;
    private Runnable action;
    private Supplier<Tuple<Boolean, String>> condition;

    public ConditionalButtonWidget(int x, int y, int width, int height, ResourceLocation texture, int u, int v, Runnable action, Supplier<Tuple<Boolean, String>> condition) {
        super(x, y, width, height, Component.nullToEmpty(" "));
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.texture = texture;
        this.action = action;
        this.condition = condition;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(this.texture, this.getX(), this.getY(), this.u + (this.condition.get().getA() ? (this.isMouseOver(mouseX, mouseY) ? 18 : 0) : 36), this.v, this.getWidth(), this.getHeight());
        if (isPointWithinBounds(this.x, this.y, mouseX, mouseY)) {
            drawTooltip(context, mouseX, mouseY);
        }
    }

    private static boolean isPointWithinBounds(int x, int y, int pointX, int pointY) {
        return pointX >= x && pointX <= x + 16 && pointY >= y && pointY <= y + 16;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    protected void drawTooltip(GuiGraphics context, int mouseX, int mouseY) {
        Font textRenderer = Minecraft.getInstance().font;
        if (!this.condition.get().getA() && this.condition.get().getB() != null && !this.condition.get().getB().isEmpty()) {
            List<Component> texts = List.of(
                    Component.literal(this.condition.get().getB()));
            context.renderComponentTooltip(textRenderer, texts, mouseX, mouseY);
        }
    }
}
