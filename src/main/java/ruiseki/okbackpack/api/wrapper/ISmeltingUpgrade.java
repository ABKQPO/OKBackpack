package ruiseki.okbackpack.api.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.okbackpack.client.gui.handler.BaseItemStackHandler;

public interface ISmeltingUpgrade extends ITickable, IToggleable {

    String SMELTING_INPUT_TAG = "SmeltInput";
    String SMELTING_OUTPUT_TAG = "SmeltOutput";
    String SMELTING_FUEL_TAG = "SmeltFuel";
    String SMELTING_PROGRESS_TAG = "SmeltProgress";
    String SMELTING_FUEL_PROGRESS_TAG = "FuelProgress";
    String SMELTING_FUEL_TOTAL_TAG = "FuelTotal";
    String FUEL_FILTER_TAG = "FuelFilter";

    BaseItemStackHandler getSmeltingInventory();

    ItemStack getSmeltingResult(ItemStack input);

    int getSmeltTime();

    int getSmeltProgress();

    void setSmeltProgress(int progress);

    int getFuelProgress();

    void setFuelProgress(int progress);

    int getFuelTotal();

    void setFuelTotal(int total);

    boolean isBurning();

    boolean checkFilter(ItemStack stack);

    boolean checkFuelFilter(ItemStack fuel);

    BaseItemStackHandler getFuelFilterItems();
}
