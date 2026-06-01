package com.main.game.inventory;

public class ItemSlotInteractionController {

    private ItemStack carriedStack;

    public void onLeftClick(ItemSlotAccess slotAccess, int slotIndex) {
        if (slotAccess == null) {
            return;
        }
        if (slotAccess.isSpecialTakeSlot(slotIndex)) {
            carriedStack = slotAccess.takeSpecialSlot(slotIndex, carriedStack, false);
            return;
        }
        if (!slotAccess.isWritableSlot(slotIndex)) {
            return;
        }

        ItemStack slotStack = slotAccess.getSlot(slotIndex);
        if (carriedStack == null) {
            if (slotStack != null && slotStack.getCount() > 0) {
                carriedStack = slotStack;
                slotAccess.setSlot(slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            if (!slotAccess.canPlaceSlot(slotIndex, carriedStack)) {
                return;
            }
            slotAccess.setSlot(slotIndex, carriedStack);
            carriedStack = null;
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            if (!slotAccess.canPlaceSlot(slotIndex, carriedStack)) {
                return;
            }
            slotAccess.setSlot(slotIndex, carriedStack);
            carriedStack = slotStack;
            return;
        }

        if (!slotAccess.canPlaceSlot(slotIndex, carriedStack)) {
            return;
        }
        int maxStack = ItemRegistry.getMaxStack(carriedStack.getItemId());
        int room = Math.max(0, maxStack - slotStack.getCount());
        if (room <= 0) {
            return;
        }
        int moved = Math.min(room, carriedStack.getCount());
        slotStack.add(moved);
        carriedStack.subtract(moved);
        if (carriedStack.getCount() <= 0) {
            carriedStack = null;
        }
    }

    public void onRightClick(ItemSlotAccess slotAccess, int slotIndex) {
        if (slotAccess == null) {
            return;
        }
        if (slotAccess.isSpecialTakeSlot(slotIndex)) {
            carriedStack = slotAccess.takeSpecialSlot(slotIndex, carriedStack, true);
            return;
        }
        if (!slotAccess.isWritableSlot(slotIndex)) {
            return;
        }

        ItemStack slotStack = slotAccess.getSlot(slotIndex);
        if (carriedStack == null) {
            if (slotStack == null || slotStack.getCount() <= 0) {
                return;
            }
            int take = (slotStack.getCount() + 1) / 2;
            carriedStack = slotStack.copy();
            carriedStack.setCount(take);
            slotStack.subtract(take);
            if (slotStack.getCount() <= 0) {
                slotAccess.setSlot(slotIndex, null);
            }
            return;
        }

        if (slotStack == null || slotStack.getCount() <= 0) {
            ItemStack placed = carriedStack.copy();
            placed.setCount(1);
            if (!slotAccess.canPlaceSlot(slotIndex, placed)) {
                return;
            }
            slotAccess.setSlot(slotIndex, placed);
            carriedStack.subtract(1);
            if (carriedStack.getCount() <= 0) {
                carriedStack = null;
            }
            return;
        }

        if (!slotStack.getItemId().equals(carriedStack.getItemId())) {
            return;
        }
        if (!slotAccess.canPlaceSlot(slotIndex, carriedStack)) {
            return;
        }
        int maxStack = ItemRegistry.getMaxStack(carriedStack.getItemId());
        if (slotStack.getCount() >= maxStack) {
            return;
        }
        slotStack.add(1);
        carriedStack.subtract(1);
        if (carriedStack.getCount() <= 0) {
            carriedStack = null;
        }
    }

    public void returnCarriedStackToInventory(Inventory inventory) {
        if (inventory == null) {
            carriedStack = null;
            return;
        }
        if (carriedStack == null || carriedStack.getCount() <= 0) {
            carriedStack = null;
            return;
        }
        carriedStack = inventory.addStack(carriedStack);
    }

    public ItemStack getCarriedStack() {
        return carriedStack;
    }
}
