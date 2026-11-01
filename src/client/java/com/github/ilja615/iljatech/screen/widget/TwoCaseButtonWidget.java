package com.github.ilja615.iljatech.screen.widget;

import com.github.ilja615.iljatech.util.Triple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class TwoCaseButtonWidget extends AbstractWidget {
    private ResourceLocation texture;
    private int x;
    private int y;
    private int u;
    private int v;
    private Runnable action;
    private Supplier<Tuple<Boolean, String>> setting;

    public TwoCaseButtonWidget(int x, int y, int width, int height, ResourceLocation texture, int u, int v, Runnable action, Supplier<Tuple<Boolean, String>> setting) {
        super(x, y, width, height, Component.nullToEmpty(" "));
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.texture = texture;
        this.action = action;
        this.setting = setting;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(this.texture, this.getX(), this.getY(), this.u + (this.isMouseOver(mouseX, mouseY) ? this.getWidth() : 0), this.v + (this.setting.get().getA() ? this.getHeight() : 0), this.getWidth(), this.getHeight());
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
        if (this.setting.get().getB() != null && !this.setting.get().getB().isEmpty()) {
            List<Component> texts = List.of(
                    Component.literal(this.setting.get().getB()));
            context.renderComponentTooltip(textRenderer, texts, mouseX, mouseY);
        }
    }
}
