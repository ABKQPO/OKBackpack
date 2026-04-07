package ruiseki.okbackpack.api.wrapper;

import ruiseki.okbackpack.client.gui.handler.BaseItemStackHandler;

/**
 * Interface for refill upgrades that auto-refill player inventory from backpack.
 */
public interface IRefillUpgrade extends ITickable, IToggleable {

    String TARGET_SLOTS_TAG = "TargetSlots";

    BaseItemStackHandler getFilterHandler();

    /**
     * Whether this upgrade supports precise target slot selection.
     */
    boolean allowsTargetSlotSelection();

    /**
     * Whether this upgrade supports picking a block from the backpack via middle mouse.
     */
    boolean supportsBlockPick();
}
