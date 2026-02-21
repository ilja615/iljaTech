package com.github.ilja615.iljatech.blocks.researchtable;

import com.github.ilja615.iljatech.IljaTech;
import com.github.ilja615.iljatech.init.*;
import com.github.ilja615.iljatech.network.BlockPosPayload;
import com.google.common.collect.Lists;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlueprintTableScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final PlayerEntity player;
    private final Property selected;
    private final Property points;

    private List<RecipeEntry<BlueprintingRecipe>> availableRecipes;

    // Client Constructor
    public BlueprintTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, payload.pos());
    }

    // Main Constructor - (Directly called from server)
    public BlueprintTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(ModScreenHandlerTypes.RESEARCH, syncId);

        this.selected = Property.create();
        this.points = Property.create();
        this.context = ScreenHandlerContext.create(playerInventory.player.getWorld(), pos);

        this.player = playerInventory.player;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.availableRecipes = player.getWorld().getRecipeManager().listAllOfType(ModRecipeTypes.BLUEPRINTING_TYPE);

        this.addProperty(this.selected);
        this.addProperty(this.points);

        // TODO: gui wont open if the player has not yet any research pts (null value error)
        this.points.set(this.player.getAttached(ModDataAttachments.RESEARCH_PNTS));
        this.selected.set(999);
    }

    private void addPlayerInventory(PlayerInventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 84 + (row * 18)));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 142));
        }
    }

    public boolean onButtonClick(PlayerEntity player, int i) {
        if (i < getAvailableRecipeCount()) {
            this.selected.set(i);
            return true;
        }
        if (i == 999) {
            // it means the "Ok"/Unlock button was pressed
            BlueprintingRecipe r = getAvailableRecipes().get(this.getSelected()).value();
            int newPts = this.points.get() - r.pointsCost();

            ServerPlayerEntity serverPlayer = player.getServer().getPlayerManager().getPlayer(player.getUuid());
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

    public boolean isAlreadyUnlocked(ServerPlayerEntity serverPlayer, ItemStack itemStack) {
        String str = itemStack.getTranslationKey();
        str = str.substring(str.lastIndexOf(".")+1);
        Identifier id = Identifier.of("iljatech/blueprint_"+str);
        AdvancementEntry advancement = player.getServer().getAdvancementLoader().get(id);
        if (advancement == null)
            return false;
        return serverPlayer.getAdvancementTracker().getProgress(advancement).isDone();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.BLUEPRINT_TABLE);
    }

    public int getPoints() {
        return points.get();
    }

    public int getSelected() {
        return this.selected.get();
    }

    public List<RecipeEntry<BlueprintingRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }
}
