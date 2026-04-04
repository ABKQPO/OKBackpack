package ruiseki.okbackpack.client.gui.widget.upgrade;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.okbackpack.api.IStoragePanel;
import ruiseki.okbackpack.client.gui.slot.FilterSlot;
import ruiseki.okbackpack.common.item.wrapper.SmeltingUpgradeWrapperBase;

public class SmeltingUpgradeWidget<T extends SmeltingUpgradeWrapperBase> extends BasicExpandedTabWidget<T> {

    protected final ProgressWidget smeltProgressBar;
    protected final ProgressWidget fuelProgressBar;
    protected final Row fuelFilterRow;

    public SmeltingUpgradeWidget(int slotIndex, T wrapper, ItemStack stack, IStoragePanel<?> panel, String titleKey) {
        super(slotIndex, wrapper, stack, titleKey, "common_filter", 5, 110);

        // Smelting progress bar (arrow style)
        this.smeltProgressBar = new ProgressWidget().size(22, 16)
            .direction(ProgressWidget.Direction.RIGHT)
            .progress(() -> {
                int total = wrapper.getSmeltTime();
                int current = wrapper.getSmeltProgress();
                return total > 0 ? (float) current / total : 0f;
            });

        // Fuel burn progress bar (flame style)
        this.fuelProgressBar = new ProgressWidget().size(14, 14)
            .direction(ProgressWidget.Direction.UP)
            .progress(() -> {
                int total = wrapper.getFuelTotal();
                int current = wrapper.getFuelProgress();
                return total > 0 ? (float) current / total : 0f;
            });

        // Smelting slots: input[0], fuel[1], output[2] displayed via synced inventory
        Row smeltingRow = (Row) new Row().leftRel(0.5f)
            .height(40)
            .coverChildrenWidth()
            .childPadding(2);

        Column inputFuelCol = (Column) new Column().coverChildren()
            .childPadding(2);

        // Input slot placeholder
        inputFuelCol.child(
            IKey.lang("gui.backpack.smelting_input")
                .asWidget()
                .height(10));
        inputFuelCol.child(fuelProgressBar);

        smeltingRow.child(inputFuelCol);
        smeltingRow.child(smeltProgressBar);

        // Fuel filter row (4 filter slots)
        this.fuelFilterRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        fuelFilterRow.child(
            IKey.lang("gui.backpack.fuel_filter")
                .asWidget()
                .height(10));

        SlotGroupWidget fuelFilterSlotGroup = new SlotGroupWidget().coverChildren();
        for (int i = 0; i < 4; i++) {
            FilterSlot slot = new FilterSlot();
            slot.name("fuel_filter_" + slotIndex)
                .syncHandler("fuel_filter_" + slotIndex, i)
                .pos(i * 18, 0);
            fuelFilterSlotGroup.child(slot);
        }
        fuelFilterRow.child(fuelFilterSlotGroup);

        this.startingRow.leftRel(0.5f)
            .height(60)
            .childPadding(2)
            .child(smeltingRow)
            .child(fuelFilterRow);
    }
}
