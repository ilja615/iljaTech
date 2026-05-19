package com.github.ilja615.iljatech.screen;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintingRecipe;
import com.github.ilja615.iljatech.blocks.researchtable.BlueprintTableScreenHandler;
import com.github.ilja615.iljatech.init.ModDataAttachments;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BlueprintSelectionScreen extends AbstractContainerScreen<BlueprintTableScreenHandler> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IljaTech.MOD_ID, "textures/gui/research_selection.png");
    private float scrollAmount;
    private int scrollOffset = 0;
    private boolean mouseClicked;
    private static final int SUBJECTS_AMOUNT = 4;

    public BlueprintSelectionScreen(BlueprintTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        ++this.inventoryLabelY;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;

        double mx = mouseX - (double)(this.leftPos + 118);
        double my = mouseY - (double)(this.topPos + 52);
        if (mx >= 0.0 && my >= 0.0 && mx < 40 && my < 20) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
            this.minecraft.gameMode.handleInventoryButtonClick(((BlueprintTableScreenHandler)this.menu).containerId, 999);
            return true;
        }


        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < menu.getAvailableRecipeCount(); i++) {
                mx = mouseX - (double)(this.leftPos + 9 + (i % 3)*26);
                my = mouseY - (double)(this.topPos + (i < this.scrollOffset/2 * 3 + 3 ? 16 : 43));
                if (mx >= 0.0 && my >= 0.0 && mx < 16.0 && my < 18.0 && ((BlueprintTableScreenHandler)this.menu).clickMenuButton(this.minecraft.player, i)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(((BlueprintTableScreenHandler)this.menu).containerId, i);
                    return true;
                }
            }
        } else {
            for (int i = this.scrollOffset * 3; i < this.scrollOffset * 3 + 3 && i < menu.getAvailableRecipeCount(); i++) {
                mx = mouseX - (double)(this.leftPos + 9 + (i % 3)*26);
                my = mouseY - (this.topPos + 30.0d);
                if (mx >= 0.0 && my >= 0.0 && mx < 16.0 && my < 18.0 && ((BlueprintTableScreenHandler)this.menu).clickMenuButton(this.minecraft.player, i)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(((BlueprintTableScreenHandler)this.menu).containerId, i);
                    return true;
                }
            }
        }

        mx = mouseX - (double)(this.leftPos + 91);
        my = mouseY - (double)(this.topPos + 9);
        if (mx >= 0.0 && my >= 0.0 && mx < 12.0 && my < 56.0) {
            this.mouseClicked = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked) {
            int i = this.topPos + 14;
            int j = i + 56;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        int i = this.getMaxScroll();
        float f = (float)verticalAmount / (float)i;
        this.scrollAmount = Mth.clamp(this.scrollAmount - f, 0.0F, 1.0F);
        this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5);
        return true;
    }

    protected int getMaxScroll() {
        return menu.getAvailableRecipeCount() / 3;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int s = (int)(41.0F * this.scrollAmount);
        context.blit(TEXTURE, this.leftPos + 91, this.topPos + 15 + s, 176, 52, 12, 15);
        this.renderCosts(context);
        this.renderButtons(context, mouseX, mouseY);
        this.renderUnlockButton(context, mouseX, mouseY);
        this.renderRecipeIcons(context);
    }

    private void renderButtons(GuiGraphics context, int mouseX, int mouseY) {
        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < menu.getAvailableRecipeCount(); i++) {
                int bx = this.leftPos + 9 + (i % 3)*26;
                int by = this.topPos + (i < this.scrollOffset/2 * 3 + 3 ? 16 : 43);
                boolean selected = menu.getSelected() == i;
                boolean mouseOver = (mouseX >= bx && mouseY >= by && mouseX < bx + 25 && mouseY < by + 26);
                context.blit(TEXTURE, bx, by, selected||mouseOver? 228:202, selected? 26:0, 26, 26);
            }
        } else {
            for (int i = this.scrollOffset * 3 - 3; i < this.scrollOffset * 3 + 6 && i < menu.getAvailableRecipeCount(); i++) {
                int bx = this.leftPos + 9 + (i % 3)*26;
                boolean selected = menu.getSelected() == i;
                if (i < this.scrollOffset * 3)
                    context.blit(TEXTURE, bx, this.topPos + 15, selected? 228:202, selected? 38:12, 26, 14);
                else if (i < this.scrollOffset * 3 + 3) {
                    boolean mouseOver = (mouseX >= bx && mouseY >= 30 && mouseX < bx + 25 && mouseY < 56);
                    context.blit(TEXTURE, bx, this.topPos + 30, selected||mouseOver? 228:202, selected? 26:0, 26, 26);
                }
                else
                    context.blit(TEXTURE, bx, this.topPos + 57, selected? 228:202, selected? 26:0, 26, 14);
            }
        }
    }

    private void renderUnlockButton(GuiGraphics context, int mouseX, int mouseY) {
        double mx = mouseX - (double)(this.leftPos + 118);
        double my = mouseY - (double)(this.topPos + 52);
        boolean mouseOver = (mx >= 0.0 && my >= 0.0 && mx < 40 && my < 20);
        context.blit(TEXTURE, this.leftPos + 118, this.topPos + 52, 176, mouseOver? 98:78, 40, 20);
        context.drawString(font, "Ok", this.leftPos +124, this.topPos + 58, 16777215 , true);
    }

    private void renderCosts(GuiGraphics context) {
        if (menu.getSelected() < menu.getAvailableRecipeCount()) {
            context.drawManaged(() -> {
                TooltipRenderUtil.renderTooltipBackground(context, this.leftPos + 108, this.topPos + 17, 62, 28, 400);
            });
            List<RecipeHolder<BlueprintingRecipe>> list = this.menu.getAvailableRecipes();
            int cost = list.get(this.menu.getSelected()).value().pointsCost();

            context.pose().pushPose();
            context.pose().translate(0.0F, 0.0F, 400.0F);

            String text = "Costs " + cost + "pts.";
            context.drawString(font, text, this.leftPos + 105, this.topPos + 23, 16777215 , true);
            text = "Your pts: " + menu.getPoints();
            context.drawString(font, text, this.leftPos + 105, this.topPos + 32, 16777215 , true);
            context.pose().popPose();
        }
    }

    private void renderRecipeIcons(GuiGraphics context) {
        List<RecipeHolder<BlueprintingRecipe>> list = this.menu.getAvailableRecipes();
        if (this.scrollOffset % 2 == 0) {
            for (int i = this.scrollOffset/2 * 3; i < this.scrollOffset/2 * 3 + 6 && i < menu.getAvailableRecipeCount(); i++) {
                int bx = this.leftPos + 14 + (i % 3)*26;
                int by = this.topPos + (i < this.scrollOffset/2 * 3 + 3 ? 21 : 48);
                BlueprintingRecipe r = list.get(i).value();
                context.renderItem(r.output().copy(), bx, by);
            }
        } else {
            for (int i = this.scrollOffset * 3 - 3; i < this.scrollOffset * 3 + 3 && i < menu.getAvailableRecipeCount(); i++) {
                int bx = this.leftPos + 14 + (i % 3) * 26;
                int by = this.topPos + 35;
                BlueprintingRecipe r = list.get(i).value();
                context.renderItem(r.output().copy(), bx, by);
            }
        }
    }
}
