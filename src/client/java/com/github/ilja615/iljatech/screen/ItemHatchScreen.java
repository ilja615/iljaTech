package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.hatch.ItemHatchScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemHatchScreen extends HandledScreen<ItemHatchScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(IljaTech.MOD_ID, "textures/gui/item_hatch.png");

    public ItemHatchScreen(ItemHatchScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 133;
        this.playerInventoryTitleY = 39;
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