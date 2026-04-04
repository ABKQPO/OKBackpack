package ruiseki.okbackpack.client.gui.widget.updateGroup;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.okbackpack.api.widget.IUpgradeSlotGroupFactory;
import ruiseki.okbackpack.client.gui.syncHandler.DelegatedStackHandlerSH;

public class SmeltingSlotGroupFactory implements IUpgradeSlotGroupFactory {

    @Override
    public void build(UpgradeSlotUpdateGroup group) {
        // Smelting inventory handler (3 slots: input, fuel, output)
        DelegatedStackHandlerSH smeltingInvHandler = new DelegatedStackHandlerSH(group.wrapper, group.slotIndex, 3);
        group.syncManager.syncValue("smelting_inv_delegation_" + group.slotIndex, smeltingInvHandler);
        group.put("smelting_inv_handler", smeltingInvHandler);

        // Input slot (index 0) - accepts any item
        ModularSlot inputSlot = new ModularSlot(smeltingInvHandler.delegatedStackHandler, 0);
        inputSlot.slotGroup("smelting_slots_" + group.slotIndex);
        group.syncManager.syncValue("smelting_slot_" + group.slotIndex, 0, new ItemSlotSH(inputSlot));

        // Fuel slot (index 1) - accepts any item (burn time checked during smelting)
        ModularSlot fuelSlot = new ModularSlot(smeltingInvHandler.delegatedStackHandler, 1);
        fuelSlot.slotGroup("smelting_slots_" + group.slotIndex);
        group.syncManager.syncValue("smelting_slot_" + group.slotIndex, 1, new ItemSlotSH(fuelSlot));

        // Output slot (index 2) - extraction only
        ModularSlot outputSlot = new ModularSlot(smeltingInvHandler.delegatedStackHandler, 2);
        outputSlot.slotGroup("smelting_slots_" + group.slotIndex);
        outputSlot.canPut(false);
        group.syncManager.syncValue("smelting_slot_" + group.slotIndex, 2, new ItemSlotSH(outputSlot));

        group.syncManager.registerSlotGroup(new SlotGroup("smelting_slots_" + group.slotIndex, 3, false));
    }
}
