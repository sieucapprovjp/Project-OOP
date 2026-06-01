package com.main.game.inventory;

public enum ArmorSlot {
    HELMET(0),
    CHESTPLATE(1),
    LEGGINGS(2),
    BOOTS(3);

    public static final int COUNT = values().length;

    private final int index;

    ArmorSlot(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static ArmorSlot fromIndex(int index) {
        for (ArmorSlot slot : values()) {
            if (slot.index == index) {
                return slot;
            }
        }
        return null;
    }
}
