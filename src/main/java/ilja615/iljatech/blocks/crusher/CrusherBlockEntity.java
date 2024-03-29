package ilja615.iljatech.blocks.crusher;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModRecipeTypes;
import ilja615.iljatech.energy.MechanicalPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CrusherBlockEntity extends BlockEntity
{
    public LazyOptional<IItemHandlerModifiable> crusherItemStackHandler = LazyOptional.of(() -> new CrusherBlockEntity.CrusherItemStackHandler(this));

    private int processingTime;

    public CrusherBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.CRUSHER.get(), p_155229_, p_155230_);
    }

    public int getProcessingTime()
    {
        return processingTime;
    }

    @Nullable
    public List<CrushingRecipe> getRecipes()
    {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CRUSHING.get());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrusherBlockEntity blockEntity)
    {
        blockEntity.crusherItemStackHandler.ifPresent(itemHandler ->
        {
            boolean foundRecipe = false;
            List<CrushingRecipe> recipes = blockEntity.getRecipes();
            for (CrushingRecipe r : recipes)
            {
                ItemStack resultingStack = r.result.copy();
                if (r.ingredient.getItems()[0].isEmpty() || itemHandler.getStackInSlot(0).isEmpty())
                    break;

                if (r.ingredient.getItems()[0].getItem() == itemHandler.getStackInSlot(0).getItem())
                {
                    // A matching recipe was found. Now then:
                    foundRecipe = true;
                    if (state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.OFF)
                    {
                        blockEntity.processingTime++;
                        if (level.isClientSide)
                            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemHandler.getStackInSlot(0)), pos.getX() + 0.25f + 0.5 * level.random.nextFloat(), pos.getY()  + 0.25f + 0.5 * level.random.nextFloat(), pos.getZ()  + 0.25f + 0.5 * level.random.nextFloat(),  - 0.1f + 0.2 * level.random.nextFloat(), 0.1f + 0.2 * level.random.nextFloat(), - 0.1f + 0.2 * level.random.nextFloat());
                    }

                    if (blockEntity.processingTime >= 60)
                    {
                        if (itemHandler.getStackInSlot(1).isEmpty())
                        {
                            // In this case a new result itemstack is added with 1 of the result.
                            itemHandler.getStackInSlot(0).shrink(1);
                            itemHandler.setStackInSlot(1, resultingStack);
                        } else if (itemHandler.getStackInSlot(1).getItem() == resultingStack.getItem() && itemHandler.getStackInSlot(1).getCount() + resultingStack.getCount() <= itemHandler.getStackInSlot(1).getMaxStackSize()) {
                            // In this case the result itemstack is added to what was already there
                            itemHandler.getStackInSlot(0).shrink(1);
                            itemHandler.getStackInSlot(1).grow(resultingStack.getCount());
                        }
                        blockEntity.processingTime = 0;
                        blockEntity.setChanged();
                    }
                    return;
                }
            }
            // In this case no recipe match was found. (Because otherwise it would have returned.)
            blockEntity.processingTime = 0; // Resset the processingtime, in case the recipe got for example interrupted halfway through or so.
        });
    }

    private boolean isProcessing() {
        return this.processingTime > 0;
    }


    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        crusherItemStackHandler.ifPresent(iItemHandlerModifiable -> ((ItemStackHandler)iItemHandlerModifiable).serializeNBT());
        compound.putInt("ProcessingTime", this.processingTime);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        crusherItemStackHandler.ifPresent(iItemHandlerModifiable -> ((ItemStackHandler)iItemHandlerModifiable).deserializeNBT(compound));
        this.processingTime = compound.getInt("ProcessingTime");
    }

    @Override
    public void invalidateCaps()
    {
        this.crusherItemStackHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return crusherItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (crusherItemStackHandler != null) {
            crusherItemStackHandler.invalidate();
        }
    }

    private class CrusherItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final CrusherBlockEntity tile;

        public CrusherItemStackHandler(CrusherBlockEntity te)
        {
            super(2);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            tile.setChanged();
        }
    }
}
