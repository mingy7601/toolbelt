package com.toolbelt.inventory;

import com.toolbelt.Toolbelt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Encapsulates all read/write operations for the 9-slot hotbar storage inside the toolbelt's NBT compound tag.
 * Serializes/deserializes up to 9 {@code ItemStack} entries into/from a single {@code NBTTagCompound}.
 */
public class ToolbeltInventory {

    private static final String TAG_KEY = "toolbelt_inventory";
    private static final String SLOTS_TAG = "slots";
    public static final int SLOT_COUNT = 9;

    /**
     * Stores the given hotbar array into this inventory's NBT data.
     */
    public void storeHotbar(ItemStack[] hotbar, NBTTagCompound nbt) {
        NBTTagList slots = new NBTTagList();
        for (int i = 0; i < SLOT_COUNT && i < hotbar.length; i++) {
            if (hotbar[i] != null && !hotbar[i].isEmpty()) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setInteger("Slot", i);
                hotbar[i].writeToNBT(slotTag);
                slots.appendTag(slotTag);
            }
        }

        NBTTagCompound inventoryTag = new NBTTagCompound();
        inventoryTag.setTag(SLOTS_TAG, slots);
        nbt.setTag(TAG_KEY, inventoryTag);


    }

    /**
     * Restores stored items from the given NBT data into a new array.
     * Returns an array of up to {@code SLOT_COUNT} ItemStacks (null for empty slots).
     */
    public ItemStack[] restoreHotbar(NBTTagCompound nbt) {
        ItemStack[] result = new ItemStack[SLOT_COUNT];

        if (!nbt.hasKey(TAG_KEY)) {
            return result;
        }

        NBTTagCompound inventoryTag = nbt.getCompoundTag(TAG_KEY);
        if (!inventoryTag.hasKey(SLOTS_TAG)) {
            return result;
        }

        NBTTagList slots = inventoryTag.getTagList(SLOTS_TAG, 10); // TAG_COMPOUND = 6
        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slotTag = slots.getCompoundTagAt(i);
            int slotIndex = slotTag.getInteger("Slot");
            if (slotIndex >= 0 && slotIndex < SLOT_COUNT) {
                result[slotIndex] = new ItemStack(slotTag);
            }
        }
        return result;
    }

    /**
     * Checks whether this inventory has any stored items.
     */
    public boolean isEmpty(NBTTagCompound nbt) {
        if (!nbt.hasKey(TAG_KEY)) {
            return true;
        }
        NBTTagCompound inventoryTag = nbt.getCompoundTag(TAG_KEY);
        if (!inventoryTag.hasKey(SLOTS_TAG)) {
            return true;
        }
        NBTTagList slots = inventoryTag.getTagList(SLOTS_TAG, 10);
        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slotTag = slots.getCompoundTagAt(i);
            int slotIndex = slotTag.getInteger("Slot");
            if (slotIndex >= 0 && slotIndex < SLOT_COUNT) {
                ItemStack stack = new ItemStack(slotTag);
                if (!stack.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reads stored hotbar from an item stack's NBT. Returns the restored array.
     */
    public ItemStack[] readFromStack(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return new ItemStack[SLOT_COUNT];
        }
        return restoreHotbar(stack.getTagCompound());
    }

    /**
     * Writes the current hotbar into an item stack's NBT.
     */
    public void writeToStack(ItemStack stack, ItemStack[] hotbar) {
        if (stack == null) {
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        storeHotbar(hotbar, stack.getTagCompound());
    }

    /**
     * Reads stored hotbar from an item stack's NBT.
     */
    public ItemStack[] readFromStackSafe(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return new ItemStack[SLOT_COUNT];
        }
        try {
            return restoreHotbar(stack.getTagCompound());
        } catch (Exception e) {
            // NBT corruption recovery — return empty array
            return new ItemStack[SLOT_COUNT];
        }
    }

    /**
     * Writes the current hotbar into an item stack's NBT.
     */
    public void writeToStackSafe(ItemStack stack, ItemStack[] hotbar) {
        if (stack == null) {
            return;
        }
        try {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            storeHotbar(hotbar, stack.getTagCompound());
        } catch (Exception e) {
            // NBT corruption recovery — reset tag
            stack.setTagCompound(new NBTTagCompound());
        }
    }
}
