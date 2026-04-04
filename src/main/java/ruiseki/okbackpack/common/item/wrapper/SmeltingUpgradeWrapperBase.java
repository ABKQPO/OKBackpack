package ruiseki.okbackpack.common.item.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;

import ruiseki.okbackpack.api.IStorageWrapper;
import ruiseki.okbackpack.api.wrapper.ISlotModifiable;
import ruiseki.okbackpack.api.wrapper.ISmeltingUpgrade;
import ruiseki.okbackpack.client.gui.handler.BaseItemStackHandler;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemNBTHelpers;

public abstract class SmeltingUpgradeWrapperBase extends UpgradeWrapperBase
    implements ISmeltingUpgrade, ISlotModifiable {

    protected final IStorageWrapper storage;
    protected BaseItemStackHandler smeltingInventory;
    private boolean enabled;

    public SmeltingUpgradeWrapperBase(ItemStack upgrade, IStorageWrapper storage) {
        super(upgrade, storage);
        this.storage = storage;

        this.smeltingInventory = new BaseItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
                tag.setTag("SmeltingInv", this.serializeNBT());
                markDirty();
            }
        };
        NBTTagCompound invTag = ItemNBTHelpers.getCompound(upgrade, "SmeltingInv", false);
        if (invTag != null) smeltingInventory.deserializeNBT(invTag);

        this.enabled = ItemNBTHelpers.getBoolean(upgrade, ENABLED_TAG, true);
    }

    @Override
    public BaseItemStackHandler getSmeltingInventory() {
        return smeltingInventory;
    }

    @Override
    public int getSmeltTime() {
        return 200;
    }

    @Override
    public int getSmeltProgress() {
        return ItemNBTHelpers.getInt(upgrade, SMELTING_PROGRESS_TAG, 0);
    }

    @Override
    public void setSmeltProgress(int progress) {
        ItemNBTHelpers.setInt(upgrade, SMELTING_PROGRESS_TAG, progress);
        markDirty();
    }

    @Override
    public int getFuelProgress() {
        return ItemNBTHelpers.getInt(upgrade, SMELTING_FUEL_PROGRESS_TAG, 0);
    }

    @Override
    public void setFuelProgress(int progress) {
        ItemNBTHelpers.setInt(upgrade, SMELTING_FUEL_PROGRESS_TAG, progress);
        markDirty();
    }

    @Override
    public int getFuelTotal() {
        return ItemNBTHelpers.getInt(upgrade, SMELTING_FUEL_TOTAL_TAG, 0);
    }

    @Override
    public void setFuelTotal(int total) {
        ItemNBTHelpers.setInt(upgrade, SMELTING_FUEL_TOTAL_TAG, total);
        markDirty();
    }

    @Override
    public boolean isBurning() {
        return getFuelProgress() > 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        ItemNBTHelpers.setBoolean(upgrade, ENABLED_TAG, enabled);
        markDirty();
    }

    @Override
    public void toggle() {
        setEnabled(!isEnabled());
    }

    @Override
    public boolean canAddUpgrade(int slot, ItemStack stack) {
        if (stack == null) return true;
        UpgradeWrapperBase candidate = UpgradeWrapperFactory.createWrapper(stack, storage);
        return !(candidate instanceof ISmeltingUpgrade);
    }

    @Override
    public String getSettingLangKey() {
        return "gui.backpack.smelting_settings";
    }

    protected ItemStack getInput() {
        return smeltingInventory.getStackInSlot(0);
    }

    protected void setInput(ItemStack stack) {
        smeltingInventory.setStackInSlot(0, stack);
    }

    protected ItemStack getFuel() {
        return smeltingInventory.getStackInSlot(1);
    }

    protected void setFuel(ItemStack stack) {
        smeltingInventory.setStackInSlot(1, stack);
    }

    protected ItemStack getOutput() {
        return smeltingInventory.getStackInSlot(2);
    }

    protected void setOutput(ItemStack stack) {
        smeltingInventory.setStackInSlot(2, stack);
    }

    protected void doSmeltTick() {
        if (!isEnabled()) return;

        boolean dirty = false;
        int fuelProgress = getFuelProgress();
        int smeltProgress = getSmeltProgress();
        int fuelTotal = getFuelTotal();

        if (fuelProgress > 0) {
            fuelProgress--;
            setFuelProgress(fuelProgress);
            dirty = true;
        }

        ItemStack input = getInput();
        if (input != null) {
            ItemStack result = getSmeltingResult(input);
            if (result != null) {
                ItemStack output = getOutput();
                boolean canOutput = output == null || (ItemHandlerHelper.canItemStacksStack(output, result)
                    && output.stackSize + result.stackSize <= output.getMaxStackSize());

                if (canOutput) {
                    if (fuelProgress <= 0) {
                        ItemStack fuel = getFuel();
                        if (fuel != null) {
                            int burnTime = TileEntityFurnace.getItemBurnTime(fuel);
                            if (burnTime > 0) {
                                fuelProgress = burnTime;
                                fuelTotal = burnTime;
                                setFuelProgress(fuelProgress);
                                setFuelTotal(fuelTotal);

                                fuel.stackSize--;
                                if (fuel.stackSize <= 0) {
                                    ItemStack containerItem = fuel.getItem()
                                        .getContainerItem(fuel);
                                    setFuel(containerItem);
                                } else {
                                    setFuel(fuel);
                                }
                                dirty = true;
                            }
                        }
                    }

                    if (fuelProgress > 0) {
                        smeltProgress++;
                        if (smeltProgress >= getSmeltTime()) {
                            smeltProgress = 0;
                            if (output == null) {
                                setOutput(result.copy());
                            } else {
                                output.stackSize += result.stackSize;
                                setOutput(output);
                            }
                            input.stackSize--;
                            if (input.stackSize <= 0) {
                                setInput(null);
                            } else {
                                setInput(input);
                            }
                        }
                        setSmeltProgress(smeltProgress);
                        dirty = true;
                    } else if (smeltProgress > 0) {
                        smeltProgress = Math.max(smeltProgress - 2, 0);
                        setSmeltProgress(smeltProgress);
                        dirty = true;
                    }
                }
            } else if (smeltProgress > 0) {
                smeltProgress = Math.max(smeltProgress - 2, 0);
                setSmeltProgress(smeltProgress);
                dirty = true;
            }
        } else if (smeltProgress > 0) {
            smeltProgress = Math.max(smeltProgress - 2, 0);
            setSmeltProgress(smeltProgress);
            dirty = true;
        }

        if (dirty) {
            markDirty();
        }
    }

    @Override
    public boolean tick(EntityPlayer player) {
        if (player.worldObj.isRemote) return false;
        doSmeltTick();
        return false;
    }

    @Override
    public boolean tick(World world, BlockPos pos) {
        if (world.isRemote) return false;
        doSmeltTick();
        return false;
    }
}
