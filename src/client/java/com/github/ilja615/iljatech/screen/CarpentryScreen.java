package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.carpentry.CarpentryScreenHandler;
import com.github.ilja615.iljatech.screen.widget.ConditionalButtonWidget;
import com.github.ilja615.iljatech.screen.widget.FluidWidget;
import com.github.ilja615.iljatech.util.FluidItemSlot;
import com.github.ilja615.iljatech.util.MaxStackSize1Slot;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class CarpentryScreen extends HandledScreen<CarpentryScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(IljaTech.MOD_ID, "textures/gui/carpentry.png");
    private final List<ClickableWidget> buttons = Lists.newArrayList();

    public CarpentryScreen(CarpentryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        handler.addListener(new ScreenHandlerListener() {
            public void onSlotUpdate(ScreenHandler handlerx, int slotId, ItemStack stack) {
            }
            public void onPropertyUpdate(ScreenHandler handlerx, int property, int value) {
            }
        });
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add(button);
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        addDrawable(new FluidWidget(this.handler.getBlockEntity().getFluidStorage(),
                this.x + 109, this.y + 20, () -> this.handler.getBlockEntity().getPos(), this.textRenderer));

        this.addButton(new ConditionalButtonWidget(this.x + 43, this.y + 16, 18, 18, TEXTURE, 176, 0, this::hammer));

        this.addButton(new ConditionalButtonWidget(this.x + 61, this.y + 16, 18, 18, TEXTURE, 176, 18, this::saw));

        this.addButton(new ConditionalButtonWidget(this.x + 7, this.y + 43, 18, 18, TEXTURE, 176, 36, this::grid));
    }

    public void hammer() {
        this.client.getNetworkHandler().sendPacket(new ButtonClickC2SPacket(handler.syncId, 0));
    }

    public void saw() {
        this.client.getNetworkHandler().sendPacket(new ButtonClickC2SPacket(handler.syncId, 1));
    }

    public void grid() {
        this.handler.changeLayoutSlots((this.handler.getLayout() + 1) % 2);
        this.client.getNetworkHandler().sendPacket(new ButtonClickC2SPacket(handler.syncId, 2));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(TEXTURE,this.x + 25, this.y + 34, 176, 54 + 36*((CarpentryScreenHandler) this.handler).getLayout(), 72, 36);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}