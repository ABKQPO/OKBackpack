package ruiseki.okbackpack.client.gui.widget.updateGroup;

import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.okbackpack.api.widget.IUpgradeSlotGroupFactory;
import ruiseki.okbackpack.client.gui.slot.ModularFilterSlot;
import ruiseki.okbackpack.client.gui.syncHandler.DelegatedStackHandlerSH;
import ruiseki.okbackpack.client.gui.syncHandler.FilterSlotSH;

public class SmeltingSlotGroupFactory implements IUpgradeSlotGroupFactory {

    @Override
    public void build(UpgradeSlotUpdateGroup group) {
        // Fuel filter slots (4 phantom slots for filtering allowed fuels)
        DelegatedStackHandlerSH fuelFilterHandler = new DelegatedStackHandlerSH(group.wrapper, group.slotIndex, 4);
        group.syncManager.syncValue("fuel_filter_delegation_" + group.slotIndex, fuelFilterHandler);
        group.put("fuel_filter_handler", fuelFilterHandler);

        ModularFilterSlot[] fuelFilterSlots = new ModularFilterSlot[4];
        for (int i = 0; i < 4; i++) {
            ModularFilterSlot slot = new ModularFilterSlot(fuelFilterHandler.delegatedStackHandler, i);
            slot.slotGroup("fuel_filters_" + group.slotIndex);
            group.syncManager.syncValue("fuel_filter_" + group.slotIndex, i, new FilterSlotSH(slot));
            fuelFilterSlots[i] = slot;
        }
        group.put("fuel_filter_slots", fuelFilterSlots);
        group.syncManager.registerSlotGroup(new SlotGroup("fuel_filters_" + group.slotIndex, 4, false));

        // Smelting inventory handler (3 slots: input, fuel, output)
        DelegatedStackHandlerSH smeltingInvHandler = new DelegatedStackHandlerSH(group.wrapper, group.slotIndex, 3);
        group.syncManager.syncValue("smelting_inv_delegation_" + group.slotIndex, smeltingInvHandler);
        group.put("smelting_inv_handler", smeltingInvHandler);
    }
}
