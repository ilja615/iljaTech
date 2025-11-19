package com.github.ilja615.iljatech.screen.widget;

import com.github.ilja615.iljatech.screen.CarpentryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConditionalButtonWidget extends ClickableWidget {
    private Identifier texture;
    private int x;
    private int y;
    private int u;
    private int v;
    private Runnable action;
    private Supplier<Pair<Boolean, String>> condition;

    public ConditionalButtonWidget(int x, int y, int width, int height, Identifier texture, int u, int v, Runnable action, Supplier<Pair<Boolean, String>> condition) {
        super(x, y, width, height, Text.of(" "));
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
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(this.texture, this.getX(), this.getY(), this.u + (this.condition.get().getLeft() ? (this.isMouseOver(mouseX, mouseY) ? 18 : 0) : 36), this.v, this.getWidth(), this.getHeight());
        if (isPointWithinBounds(this.x, this.y, mouseX, mouseY)) {
            drawTooltip(context, mouseX, mouseY);
        }
    }

    private static boolean isPointWithinBounds(int x, int y, int pointX, int pointY) {
        return pointX >= x && pointX <= x + 16 && pointY >= y && pointY <= y + 16;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    protected void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (!this.condition.get().getLeft() && this.condition.get().getRight() != null && !this.condition.get().getRight().isEmpty()) {
            List<Text> texts = List.of(
                    Text.literal(this.condition.get().getRight()));
            context.drawTooltip(textRenderer, texts, mouseX, mouseY);
        }
    }
}
