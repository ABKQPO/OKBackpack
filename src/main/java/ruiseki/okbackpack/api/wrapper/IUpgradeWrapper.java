package ruiseki.okbackpack.api.wrapper;

import net.minecraft.item.ItemStack;

public interface IUpgradeWrapper {

    String TAB_STATE_TAG = "TabState";

    void setTabOpened(boolean opened);

    boolean isTabOpened();

    String getSettingLangKey();

    ItemStack getUpgradeStack();

    default boolean canBeDisabled() {
        return true;
    }

    default void onBeforeRemoved() {
        // noop
    }

    default void onAdded() {
        // noop
    }
}
