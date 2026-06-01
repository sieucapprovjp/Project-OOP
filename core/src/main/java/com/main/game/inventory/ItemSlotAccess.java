package com.main.game.inventory;

public interface ItemSlotAccess {

    boolean isWritableSlot(int slotIndex);

    ItemStack getSlot(int slotIndex);

    void setSlot(int slotIndex, ItemStack stack);

    default boolean canPlaceSlot(int slotIndex, ItemStack stack) {
        return true;
    }

    default boolean isSpecialTakeSlot(int slotIndex) {
        return false;
    }

    default ItemStack takeSpecialSlot(int slotIndex, ItemStack carriedStack, boolean singleItem) {
        return carriedStack;
    }
}
