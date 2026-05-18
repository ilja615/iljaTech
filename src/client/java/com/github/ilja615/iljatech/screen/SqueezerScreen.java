package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.squeezer.SqueezerScreenHandler;
import com.github.ilja615.iljatech.screen.widget.FluidWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SqueezerScreen extends HandledScreen<SqueezerScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(IljaTech.MOD_ID, "textures/gui/squeezer.png");

    public SqueezerScreen(SqueezerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        addDrawable(new FluidWidget(this.handler.getBlockEntity().getFluidStorage(),
                this.x + 116, this.y + 24, () -> this.handler.getBlockEntity().getPos(), this.textRenderer));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}