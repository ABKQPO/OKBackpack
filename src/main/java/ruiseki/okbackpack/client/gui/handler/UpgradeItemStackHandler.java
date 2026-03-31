package ruiseki.okbackpack.client.gui.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

public class UpgradeItemStackHandler extends ItemStackHandler {

    public UpgradeItemStackHandler(int size) {
        super(size);
    }

    public void resize(int newSize) {
        List<ItemStack> newStacks = new ArrayList<>(newSize);

        for (int i = 0; i < newSize; i++) {
            if (i < stacks.size()) {
                newStacks.add(stacks.get(i));
            } else {
                newStacks.add(null);
            }
        }

        this.stacks = newStacks;
    }

    public boolean isSizeInconsistent(int newSize) {
        return newSize != stacks.size();
    }

    public static <T> void syncListSize(List<T> list, int newSize, T defaultValue) {
        int currentSize = list.size();
        if (newSize < currentSize) {
            list.subList(newSize, currentSize)
                .clear();
        } else {
            for (int i = currentSize; i < newSize; i++) {
                list.add(defaultValue);
            }
        }
    }
}
