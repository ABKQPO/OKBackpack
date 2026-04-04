package ruiseki.okbackpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okbackpack.Reference;
import ruiseki.okbackpack.api.IStoragePanel;
import ruiseki.okbackpack.api.IStorageWrapper;
import ruiseki.okbackpack.client.gui.syncHandler.DelegatedStackHandlerSH;
import ruiseki.okbackpack.client.gui.widget.updateGroup.UpgradeSlotUpdateGroup;
import ruiseki.okbackpack.client.gui.widget.upgrade.ExpandedTabWidget;
import ruiseki.okbackpack.client.gui.widget.upgrade.SmeltingUpgradeWidget;
import ruiseki.okbackpack.common.item.wrapper.BlastingUpgradeWrapper;
import ruiseki.okcore.helper.LangHelpers;

public class ItemBlastingUpgrade extends ItemUpgrade<BlastingUpgradeWrapper> {

    public ItemBlastingUpgrade() {
        super("blasting_upgrade");
        setMaxStackSize(1);
        setTextureName(Reference.PREFIX_MOD + "blasting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize("tooltip.backpack.blasting_upgrade"));
    }

    @Override
    public BlastingUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper storage) {
        return new BlastingUpgradeWrapper(stack, storage);
    }

    @Override
    public void updateWidgetDelegates(BlastingUpgradeWrapper wrapper, UpgradeSlotUpdateGroup group) {
        DelegatedStackHandlerSH handler = group.get("common_filter_handler");
        if (handler == null) return;
        handler.setDelegatedStackHandler(wrapper::getFilterItems);
        handler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);

        DelegatedStackHandlerSH fuelHandler = group.get("fuel_filter_handler");
        if (fuelHandler == null) return;
        fuelHandler.setDelegatedStackHandler(wrapper::getFuelFilterItems);
        fuelHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_FILTERABLE);

        DelegatedStackHandlerSH smeltingHandler = group.get("smelting_inv_handler");
        if (smeltingHandler == null) return;
        smeltingHandler.setDelegatedStackHandler(wrapper::getSmeltingInventory);
        smeltingHandler.syncToServer(DelegatedStackHandlerSH.UPDATE_STORAGE);
    }

    @Override
    public ExpandedTabWidget getExpandedTabWidget(int slotIndex, BlastingUpgradeWrapper wrapper, ItemStack stack,
        IStoragePanel<?> panel, String titleKey) {
        return new SmeltingUpgradeWidget(slotIndex, wrapper, stack, panel, titleKey);
    }
}
