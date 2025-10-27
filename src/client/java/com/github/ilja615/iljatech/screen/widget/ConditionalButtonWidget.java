package com.github.ilja615.iljatech.screen.widget;

import com.github.ilja615.iljatech.screen.CarpentryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ConditionalButtonWidget extends ClickableWidget {
    private Identifier texture;
    private int u;
    private int v;
    private Runnable action;

    public ConditionalButtonWidget(int x, int y, int width, int height, Identifier texture, int u, int v, Runnable action) {
        super(x, y, width, height, Text.of(" "));

        this.u = u;
        this.v = v;
        this.texture = texture;
        this.action = action;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(this.texture, this.getX(), this.getY(), this.u + (this.isMouseOver(mouseX, mouseY) ? 18 : 0), this.v, this.getWidth(), this.getHeight());
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
