package ruiseki.okbackpack.client.gui.widget.upgrade;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.okbackpack.api.IStoragePanel;
import ruiseki.okbackpack.client.gui.OKBGuiTextures;
import ruiseki.okbackpack.client.gui.slot.BigItemSlot;
import ruiseki.okbackpack.common.item.wrapper.AdvancedSmeltingUpgradeWrapperBase;

public class AdvancedSmeltingUpgradeWidget<T extends AdvancedSmeltingUpgradeWrapperBase>
    extends AdvancedExpandedTabWidget<T> {

    public AdvancedSmeltingUpgradeWidget(int slotIndex, T wrapper, ItemStack stack, IStoragePanel<?> panel,
        String titleKey) {
        super(slotIndex, wrapper, stack, titleKey, "adv_common_filter", 6, 100);

        // Furnace layout using SlotGroupWidget with absolute positioning
        SlotGroupWidget furnaceGroup = (SlotGroupWidget) new SlotGroupWidget().coverChildren();

        // Input slot (index 0)
        ItemSlot inputSlot = (ItemSlot) new ItemSlot()
            .syncHandler("smelting_slot_" + slotIndex, 0)
            .pos(0, 0)
            .name("smelting_input_" + slotIndex);
        furnaceGroup.child(inputSlot);

        // Fuel slot (index 1)
        ItemSlot fuelSlot = (ItemSlot) new ItemSlot()
            .syncHandler("smelting_slot_" + slotIndex, 1)
            .pos(0, 36)
            .name("smelting_fuel_" + slotIndex);
        furnaceGroup.child(fuelSlot);

        // Flame progress (fuel burn time remaining)
        ProgressWidget flameProgress = (ProgressWidget) new ProgressWidget()
            .size(14, 14)
            .texture(OKBGuiTextures.FURNACE_FLAME_BACKGROUND, OKBGuiTextures.FURNACE_FLAME_FOREGROUND, 14)
            .direction(ProgressWidget.Direction.UP)
            .progress(() -> {
                int total = wrapper.getFuelTotal();
                int current = wrapper.getFuelProgress();
                return total > 0 ? (float) current / total : 0f;
            })
            .pos(2, 20);
        furnaceGroup.child(flameProgress);

        // Arrow progress (smelting progress)
        ProgressWidget arrowProgress = (ProgressWidget) new ProgressWidget()
            .size(24, 17)
            .texture(OKBGuiTextures.FURNACE_ARROW_BACKGROUND, OKBGuiTextures.FURNACE_ARROW_FOREGROUND, 24)
            .direction(ProgressWidget.Direction.RIGHT)
            .progress(() -> {
                int total = wrapper.getSmeltTime();
                int current = wrapper.getSmeltProgress();
                return total > 0 ? (float) current / total : 0f;
            })
            .pos(24, 18);
        furnaceGroup.child(arrowProgress);

        // Output slot (index 2)
        BigItemSlot outputSlot = (BigItemSlot) new BigItemSlot()
            .syncHandler("smelting_slot_" + slotIndex, 2)
            .pos(56, 14)
            .name("smelting_output_" + slotIndex);
        furnaceGroup.child(outputSlot);

        this.startingRow.coverChildrenWidth()
            .height(56)
            .child(furnaceGroup);
    }
}
