package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.google.common.collect.Lists;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.List;

public class BlueprintTableScreenHandler extends AbstractContainerMenu {
    private final ContainerLevelAccess context;
    private final Player player;
    private final DataSlot selected;
    private final DataSlot points;

    private List<RecipeHolder<BlueprintingRecipe>> availableRecipes;

    // Client Constructor
    public BlueprintTableScreenHandler(int syncId, Inventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, payload.pos());
    }

    // Main Constructor - (Directly called from server)
    public BlueprintTableScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
        super(ModScreenHandlerTypes.RESEARCH, syncId);

        this.selected = DataSlot.standalone();
        this.points = DataSlot.standalone();
        this.context = ContainerLevelAccess.create(playerInventory.player.level(), pos);

        this.player = playerInventory.player;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.availableRecipes = player.level().getRecipeManager().getAllRecipesFor(ModRecipeTypes.BLUEPRINTING_TYPE);

        this.addDataSlot(this.selected);
        this.addDataSlot(this.points);

        // TODO: gui wont open if the player has not yet any research pts (null value error)
        this.points.set(this.player.getAttached(ModDataAttachments.RESEARCH_PNTS));
        this.selected.set(999);
    }

    private void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    public boolean clickMenuButton(Player player, int i) {
        if (i < getAvailableRecipeCount()) {
            this.selected.set(i);
            return true;
        }
        if (i == 999) {
            // it means the "Ok"/Unlock button was pressed
            BlueprintingRecipe r = getAvailableRecipes().get(this.getSelected()).value();
            int newPts = this.points.get() - r.pointsCost();

            ServerPlayer serverPlayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
            boolean alreadyDone = isAlreadyUnlocked(serverPlayer, r.output());
            if (newPts >= 0 && !alreadyDone) {
                this.points.set(newPts);
                player.setAttached(ModDataAttachments.RESEARCH_PNTS, newPts);
                ModCriteria.BLUEPRINT_UNLOCK.trigger(serverPlayer, r.output());

                return true;
            }
        }
        return false;
    }

    public boolean isAlreadyUnlocked(ServerPlayer serverPlayer, ItemStack itemStack) {
        String str = itemStack.getDescriptionId();
        str = str.substring(str.lastIndexOf(".")+1);
        ResourceLocation id = ResourceLocation.parse("iljatech/blueprint_"+str);
        AdvancementHolder advancement = player.getServer().getAdvancements().get(id);
        if (advancement == null)
            return false;
        return serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, ModBlocks.BLUEPRINT_TABLE);
    }

    public int getPoints() {
        return points.get();
    }

    public int getSelected() {
        return this.selected.get();
    }

    public List<RecipeHolder<BlueprintingRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }
}
