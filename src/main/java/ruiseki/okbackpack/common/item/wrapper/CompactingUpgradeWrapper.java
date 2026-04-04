package ruiseki.okbackpack.common.item.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;

import ruiseki.okbackpack.api.IStorageWrapper;
import ruiseki.okbackpack.api.wrapper.ICompactingUpgrade;
import ruiseki.okbackpack.client.gui.handler.BackpackItemStackHandler;
import ruiseki.okbackpack.common.block.BackpackWrapper;
import ruiseki.okbackpack.common.recipe.CompactingRecipeCache;
import ruiseki.okbackpack.common.recipe.CompactingRecipeCache.CompactingResult;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemNBTHelpers;

public class CompactingUpgradeWrapper extends BasicUpgradeWrapper implements ICompactingUpgrade {

    public CompactingUpgradeWrapper(ItemStack upgrade, IStorageWrapper storage) {
        super(upgrade, storage);
    }

    @Override
    public String getSettingLangKey() {
        return "gui.backpack.compacting_settings";
    }

    @Override
    public boolean allowsGrid3x3() {
        return false;
    }

    @Override
    public boolean isOnlyReversible() {
        return ItemNBTHelpers.getBoolean(upgrade, ONLY_REVERSIBLE_TAG, true);
    }

    @Override
    public void setOnlyReversible(boolean onlyReversible) {
        ItemNBTHelpers.setBoolean(upgrade, ONLY_REVERSIBLE_TAG, onlyReversible);
        markDirty();
    }

    @Override
    public boolean checkFilter(ItemStack stack) {
        return isEnabled() && super.checkFilter(stack);
    }

    @Override
    public void compactInventory() {
        doCompact();
    }

    private void doCompact() {
        if (!isEnabled()) return;
        if (!(storage instanceof BackpackWrapper bw)) return;

        BackpackItemStackHandler invHandler = bw.backpackHandler;
        CompactingRecipeCache cache = CompactingRecipeCache.getInstance();
        boolean onlyReversible = isOnlyReversible();
        boolean allow3x3 = allowsGrid3x3();

        for (int slot = 0; slot < invHandler.getSlots(); slot++) {
            ItemStack stack = invHandler.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0) continue;
            if (!checkFilter(stack)) continue;

            compactSlot(slot, stack, invHandler, cache, allow3x3, onlyReversible);
        }
    }

    protected void compactSlot(int slot, ItemStack stack, BackpackItemStackHandler invHandler,
        CompactingRecipeCache cache, boolean allow3x3, boolean onlyReversible) {

        CompactingResult result = cache.findCompactingRecipe(stack, allow3x3, onlyReversible);
        if (result == null) return;

        int inputCount = result.inputCount();

        if (stack.stackSize < inputCount) return;

        int compactableUnits = stack.stackSize / inputCount;
        if (compactableUnits <= 0) return;

        for (int units = compactableUnits; units > 0; units--) {
            ItemStack outputCopy = result.output()
                .copy();
            outputCopy.stackSize = units;

            ItemStack remaining = tryInsertOutput(outputCopy, invHandler);

            if (remaining != null && remaining.stackSize == outputCopy.stackSize) {
                continue;
            }

            int insertedUnits = units - (remaining == null ? 0 : remaining.stackSize);
            if (insertedUnits <= 0) continue;

            int consumed = insertedUnits * inputCount;
            ItemStack current = invHandler.getStackInSlot(slot);
            if (current != null) {
                current.stackSize -= consumed;
                if (current.stackSize <= 0) {
                    invHandler.setStackInSlot(slot, null);
                } else {
                    invHandler.setStackInSlot(slot, current);
                }
            }
            break;
        }
    }

    private ItemStack tryInsertOutput(ItemStack output, BackpackItemStackHandler invHandler) {
        if (output == null) return null;

        ItemStack remaining = ItemHandlerHelper.copyStackWithSize(output, output.stackSize);

        for (int i = 0; i < invHandler.getSlots() && remaining != null; i++) {
            ItemStack existing = invHandler.getStackInSlot(i);
            if (existing == null) continue;
            if (!ItemHandlerHelper.canItemStacksStack(existing, remaining)) continue;

            int limit = invHandler.getSlotLimit(i);
            int space = limit - existing.stackSize;
            if (space <= 0) continue;

            int toInsert = Math.min(space, remaining.stackSize);
            existing.stackSize += toInsert;
            invHandler.setStackInSlot(i, existing);

            remaining.stackSize -= toInsert;
            if (remaining.stackSize <= 0) return null;
        }

        for (int i = 0; i < invHandler.getSlots() && remaining != null; i++) {
            ItemStack existing = invHandler.getStackInSlot(i);
            if (existing != null) continue;

            int limit = invHandler.getSlotLimit(i);
            int toInsert = Math.min(limit, remaining.stackSize);

            ItemStack placed = remaining.copy();
            placed.stackSize = toInsert;
            invHandler.setStackInSlot(i, placed);

            remaining.stackSize -= toInsert;
            if (remaining.stackSize <= 0) return null;
        }

        return remaining;
    }

    @Override
    public boolean tick(EntityPlayer player) {
        if (player.worldObj.isRemote) return false;
        compactInventory();
        return false;
    }

    @Override
    public boolean tick(World world, BlockPos pos) {
        if (world.isRemote) return false;
        compactInventory();
        return false;
    }
}
