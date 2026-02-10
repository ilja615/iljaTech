package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingRecipe;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintTableScreenHandler;
import com.github.ilja615.iljatech.init.ModDataAttachments;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class BlueprintSelectionScreen extends HandledScreen<BlueprintTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(IljaTech.MOD_ID, "textures/gui/research_selection.png");
    private float scrollAmount;
    private int scrollOffset = 0;
    private boolean mouseClicked;
    private static final int SUBJECTS_AMOUNT = 4;

    public BlueprintSelectionScreen(BlueprintTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        ++this.playerInventoryTitleY;
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.handler.updateUnlocks();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;

        double mx = mouseX - (double)(this.x + 118);
        double my = mouseY - (double)(this.y + 52);
        if (mx >= 0.0 && my >= 0.0 && mx < 40 && my < 20) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
            this.client.interactionManager.clickButton(((BlueprintTableScreenHandler)this.handler).syncId, 999);
            return true;
        }


        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < handler.getAvailableRecipeCount(); i++) {
                mx = mouseX - (double)(this.x + 9 + (i % 3)*26);
                my = mouseY - (double)(this.y + i < this.scrollOffset/2 * 3 + 3 ? 16 : 43);
                if (mx >= 0.0 && my >= 0.0 && mx < 16.0 && my < 18.0 && ((BlueprintTableScreenHandler)this.handler).onButtonClick(this.client.player, i)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.client.interactionManager.clickButton(((BlueprintTableScreenHandler)this.handler).syncId, i);
                    return true;
                }
            }
        } else {
            for (int i = this.scrollOffset * 3; i < this.scrollOffset * 3 + 3 && i < handler.getAvailableRecipeCount(); i++) {
                mx = mouseX - (double)(this.x + 9 + (i % 3)*26);
                my = mouseY - (this.y + 30.0d);
                if (mx >= 0.0 && my >= 0.0 && mx < 16.0 && my < 18.0 && ((BlueprintTableScreenHandler)this.handler).onButtonClick(this.client.player, i)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.client.interactionManager.clickButton(((BlueprintTableScreenHandler)this.handler).syncId, i);
                    return true;
                }
            }
        }

        mx = mouseX - (double)(this.x + 91);
        my = mouseY - (double)(this.y + 9);
        if (mx >= 0.0 && my >= 0.0 && mx < 12.0 && my < 56.0) {
            this.mouseClicked = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked) {
            int i = this.y + 14;
            int j = i + 56;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5);
            this.handler.setScrollOffset(this.scrollOffset);
            this.client.interactionManager.clickButton(((BlueprintTableScreenHandler)this.handler).syncId, 1000);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        int i = this.getMaxScroll();
        float f = (float)verticalAmount / (float)i;
        this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0F, 1.0F);
        this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5);
        this.handler.setScrollOffset(this.scrollOffset);
        this.client.interactionManager.clickButton(((BlueprintTableScreenHandler)this.handler).syncId, 1000);
        return true;
    }

    protected int getMaxScroll() {
        return handler.getAvailableRecipeCount() / 3;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int s = (int)(41.0F * this.scrollAmount);
        context.drawTexture(TEXTURE, this.x + 91, this.y + 15 + s, 176, 52, 12, 15);
        this.renderCosts(context);
        this.renderButtons(context, mouseX, mouseY);
        this.renderUnlockButton(context, mouseX, mouseY);
        this.renderRecipeIcons(context);
    }

    private void renderButtons(DrawContext context, int mouseX, int mouseY) {
        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < handler.getAvailableRecipeCount(); i++) {
                int bx = this.x + 9 + (i % 3)*26;
                int by = this.y + (i < this.scrollOffset/2 * 3 + 3 ? 16 : 43);
                boolean selected = handler.getSelected() == i;
                boolean mouseOver = (mouseX >= bx && mouseY >= by && mouseX < bx + 25 && mouseY < by + 26);
                context.drawTexture(TEXTURE, bx, by, selected||mouseOver? 228:202, selected? 26:0, 26, 26);
            }
        } else {
            for (int i = this.scrollOffset * 3 - 3; i < this.scrollOffset * 3 + 6 && i < handler.getAvailableRecipeCount(); i++) {
                int bx = this.x + 9 + (i % 3)*26;
                boolean selected = handler.getSelected() == i;
                if (i < this.scrollOffset * 3)
                    context.drawTexture(TEXTURE, bx, this.y + 15, selected? 228:202, selected? 38:12, 26, 14);
                else if (i < this.scrollOffset * 3 + 3) {
                    boolean mouseOver = (mouseX >= bx && mouseY >= 30 && mouseX < bx + 25 && mouseY < 56);
                    context.drawTexture(TEXTURE, bx, this.y + 30, selected||mouseOver? 228:202, selected? 26:0, 26, 26);
                }
                else
                    context.drawTexture(TEXTURE, bx, this.y + 57, selected? 228:202, selected? 26:0, 26, 14);
            }
        }
    }

    private void renderUnlockButton(DrawContext context, int mouseX, int mouseY) {
        double mx = mouseX - (double)(this.x + 118);
        double my = mouseY - (double)(this.y + 52);
        boolean mouseOver = (mx >= 0.0 && my >= 0.0 && mx < 40 && my < 20);
        context.drawTexture(TEXTURE, this.x + 118, this.y + 52, 176, mouseOver? 98:78, 40, 20);
        context.drawText(textRenderer, "Ok", this.x +124, this.y + 58, 16777215 , true);
    }

    private void renderCosts(DrawContext context) {
        if (handler.getSelected() < handler.getAvailableRecipeCount()) {
            context.draw(() -> {
                TooltipBackgroundRenderer.render(context, this.x + 108, this.y + 17, 62, 28, 400);
            });
            List<RecipeEntry<BlueprintingRecipe>> list = this.handler.getAvailableRecipes();
            int cost = list.get(this.handler.getSelected()).value().pointsCost();

            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 400.0F);
            int n = handler.getSelected() - this.scrollOffset/2 * 3;
            int bits = this.handler.getUnlocks();
            String text =  ((bits & (1 << n)) != 0) ? "Already unlocked" : ("Costs " + cost + "pts.");
            context.drawText(textRenderer, text, this.x + 105, this.y + 23, 16777215 , true);
            text = "Your pts: " + handler.getPoints();
            context.drawText(textRenderer, text, this.x + 105, this.y + 32, 16777215 , true);
            context.getMatrices().pop();
        }
    }

    private void renderRecipeIcons(DrawContext context) {
        List<RecipeEntry<BlueprintingRecipe>> list = this.handler.getAvailableRecipes();
        int bits = this.handler.getUnlocks();
        int n = 0;
        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < handler.getAvailableRecipeCount(); i++) {
                int bx = this.x + 14 + (i % 3)*26;
                int by = this.y + i < this.scrollOffset/2 * 3 + 3 ? 21 : 48;
                BlueprintingRecipe r = list.get(i).value();
                context.drawItem(r.output().copy(), bx, by);

                // draw the checkmark if this is unlocked
                if ((bits & (1 << n)) != 0)
                    context.drawTexture(TEXTURE, bx+11, by+14, 176, 67, 10, 10);
                n++;
            }
        } else {
            for (int i = this.scrollOffset * 3 - 3; i < this.scrollOffset * 3 + 3 && i < handler.getAvailableRecipeCount(); i++) {
                int bx = this.x + 14 + (i % 3) * 26;
                int cy = this.y + (n < 3 ? 22 : 49);
                if (n >= 3) {
                    BlueprintingRecipe r = list.get(i).value();
                    context.drawItem(r.output().copy(), bx, 35);
                }

                // draw the checkmark if this is unlocked
                if ((bits & (1 << n)) != 0)
                    context.drawTexture(TEXTURE, bx+11, cy, 176, 67, 10, 10);
                n++;
            }
        }
    }
}
