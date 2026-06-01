package com.main.game.inventory;

public class Inventory {

    public static final int HOTBAR_SIZE = 9;
    public static final int MAIN_SIZE = 27;
    public static final int PICKUP_SLOT_COUNT = HOTBAR_SIZE + MAIN_SIZE;
    public static final int TOTAL_SIZE = PICKUP_SLOT_COUNT;

    private final ItemStack[] slots = new ItemStack[TOTAL_SIZE];
    private final ArmorLoadout armorLoadout = new ArmorLoadout();

    public int add(String itemId, int count) {
        int remaining = count;
        int maxStack = ItemRegistry.getMaxStack(itemId);

        for (int i = 0; i < PICKUP_SLOT_COUNT && remaining > 0; i++) {
            ItemStack stack = slots[i];
            if (stack != null && stack.getItemId().equals(itemId) && stack.getCount() < maxStack) {
                int added = Math.min(remaining, maxStack - stack.getCount());
                stack.add(added);
                remaining -= added;
            }
        }

        for (int i = 0; i < PICKUP_SLOT_COUNT && remaining > 0; i++) {
            if (slots[i] == null || slots[i].getCount() <= 0) {
                int added = Math.min(remaining, maxStack);
                slots[i] = new ItemStack(itemId, added);
                remaining -= added;
            }
        }

        return remaining;
    }

    public ItemStack addStack(ItemStack incoming) {
        if (incoming == null || incoming.getCount() <= 0) {
            return null;
        }

        if (!incoming.hasDurability()) {
            int remaining = add(incoming.getItemId(), incoming.getCount());
            if (remaining <= 0) {
                return null;
            }
            incoming.setCount(remaining);
            return incoming;
        }

        for (int i = 0; i < PICKUP_SLOT_COUNT; i++) {
            if (slots[i] == null || slots[i].getCount() <= 0) {
                slots[i] = incoming;
                return null;
            }
        }
        return incoming;
    }

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

    public int getTotalSize() {
        return slots.length;
    }

    public ArmorLoadout getArmorLoadout() {
        return armorLoadout;
    }

    public ItemStack getArmorSlot(ArmorSlot slot) {
        return armorLoadout.getSlot(slot);
    }

    public void setArmorSlot(ArmorSlot slot, ItemStack stack) {
        armorLoadout.setSlot(slot, stack);
    }
}
