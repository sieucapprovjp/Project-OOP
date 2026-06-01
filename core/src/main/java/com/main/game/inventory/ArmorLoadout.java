package com.main.game.inventory;

public class ArmorLoadout {

    private final ItemStack[] slots = new ItemStack[ArmorSlot.COUNT];

    public ItemStack getSlot(ArmorSlot slot) {
        if (slot == null) {
            return null;
        }
        return slots[slot.getIndex()];
    }

    public void setSlot(ArmorSlot slot, ItemStack stack) {
        if (slot == null) {
            return;
        }
        if (stack == null || stack.getCount() <= 0) {
            slots[slot.getIndex()] = null;
            return;
        }
        slots[slot.getIndex()] = stack;
    }

    public boolean canPlace(ArmorSlot slot, ItemStack stack) {
        if (slot == null || stack == null || stack.getCount() <= 0) {
            return true;
        }
        return slot == ArmorRegistry.getSlot(stack.getItemId());
    }

    public int getTotalDefensePoints() {
        int defensePoints = 0;
        for (ItemStack stack : slots) {
            if (stack != null && stack.getCount() > 0) {
                defensePoints += ArmorRegistry.getDefensePoints(stack.getItemId());
            }
        }
        return defensePoints;
    }

    public int applyDamageAndGetDefense(int rawDamage) {
        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack stack = getSlot(slot);
            if (stack == null || stack.getCount() <= 0) {
                setSlot(slot, null);
                continue;
            }
            if (rawDamage > 0 && stack.damage(rawDamage)) {
                setSlot(slot, null);
            }
        }
        return getTotalDefensePoints();
    }
}
