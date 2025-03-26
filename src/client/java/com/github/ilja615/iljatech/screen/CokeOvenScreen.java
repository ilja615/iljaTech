package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.cokeoven.CokeOvenScreenHandler;
import com.github.ilja615.iljatech.blocks.foundry.FoundryScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CokeOvenScreen extends HandledScreen<CokeOvenScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(IljaTech.MOD_ID, "textures/gui/coke_oven.png");

    public CokeOvenScreen(CokeOvenScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int l = MathHelper.ceil(((CokeOvenScreenHandler)this.handler).getProgress() * 48);
        context.drawTexture(TEXTURE,this.x + 64, this.y + 24, 176, 0, l, 16);
        switch (((CokeOvenScreenHandler)this.handler).getLitState()) {
            case ON -> context.drawTexture(TEXTURE,this.x + 39, this.y + 54, 176, 16, 24, 14);
            case STOKED -> context.drawTexture(TEXTURE,this.x + 39, this.y + 54, 176, 30, 24, 14);
            case CHOKING -> context.drawTexture(TEXTURE,this.x + 39, this.y + 54, 176, 44, 24, 14);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        if (isPointWithinBounds(76, 62, 24, 14, mouseX, mouseY)) {
            String toolTip = switch (((CokeOvenScreenHandler)this.handler).getLitState()) {
                case ON -> "Firebox burning as normally";
                case OFF -> "Provide heat with large firebox";
                case STOKED -> "Firebox is being stoked";
                case CHOKING -> "Firebox choking. Cleaning needed.";
            };
            context.drawTooltip(this.textRenderer, Text.literal(toolTip), mouseX, mouseY);
        }
    }
}