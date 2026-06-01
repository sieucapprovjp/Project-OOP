package com.main.game.utilityblock.chest;

import com.main.game.inventory.ItemStack;

public class ChestState {

    public static final int SLOT_COUNT = 27;

    private final ItemStack[] slots = new ItemStack[SLOT_COUNT];

    public ItemStack getSlot(int index) {
        if (index < 0 || index >= slots.length) {
            return null;
        }
        return slots[index];
    }

    public void setSlot(int index, ItemStack stack) {
        if (index < 0 || index >= slots.length) {
            return;
        }
        if (stack == null || stack.getCount() <= 0) {
            slots[index] = null;
            return;
        }
        slots[index] = stack;
    }
}
