package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.crafting.CraftingController;

public class InventoryInteractionHandler {

    private final ItemSlotInteractionController slotInteraction = new ItemSlotInteractionController();

    public void update(Inventory inventory, InventoryRenderer renderer) {
        update(inventory, renderer, null);
    }

    public void update(Inventory inventory, InventoryRenderer renderer, CraftingController craftingController) {
        if (inventory == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY, craftingController);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            slotInteraction.onLeftClick(new InventorySlotAccess(inventory, craftingController), slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            slotInteraction.onRightClick(new InventorySlotAccess(inventory, craftingController), slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        onCloseInventory(inventory, null);
    }

    public void onCloseInventory(Inventory inventory, CraftingController craftingController) {
        if (inventory == null) {
            slotInteraction.returnCarriedStackToInventory(null);
            return;
        }
        slotInteraction.returnCarriedStackToInventory(inventory);
        if (craftingController != null) {
            craftingController.closeCrafting(inventory);
        }
    }

    public ItemStack getCarriedStack() {
        return slotInteraction.getCarriedStack();
    }

    private static class InventorySlotAccess implements ItemSlotAccess {
        private final Inventory inventory;
        private final CraftingController craftingController;

        InventorySlotAccess(Inventory inventory, CraftingController craftingController) {
            this.inventory = inventory;
            this.craftingController = craftingController;
        }

        @Override
        public boolean isSpecialTakeSlot(int slotIndex) {
            return InventoryLayout.isCraftResultSlot(slotIndex);
        }

        @Override
        public ItemStack takeSpecialSlot(int slotIndex, ItemStack carriedStack, boolean singleItem) {
            if (craftingController == null) {
                return carriedStack;
            }
            return craftingController.takeResult(carriedStack);
        }

        @Override
        public boolean isWritableSlot(int slotIndex) {
            if (InventoryLayout.isArmorSlot(slotIndex)) {
                return canUseArmorSlots();
            }
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                return true;
            }
            return craftingController != null
                && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid());
        }

        @Override
        public ItemStack getSlot(int slotIndex) {
            if (InventoryLayout.isArmorSlot(slotIndex) && canUseArmorSlots()) {
                return inventory.getArmorSlot(InventoryLayout.toArmorSlot(slotIndex));
            }
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                return inventory.getSlot(slotIndex);
            }
            if (craftingController != null && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid())) {
                return craftingController.getGrid().getSlot(InventoryLayout.toCraftInputIndex(slotIndex));
            }
            return null;
        }

        @Override
        public void setSlot(int slotIndex, ItemStack stack) {
            if (InventoryLayout.isArmorSlot(slotIndex) && canUseArmorSlots()) {
                inventory.setArmorSlot(InventoryLayout.toArmorSlot(slotIndex), stack);
                return;
            }
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                inventory.setSlot(slotIndex, stack);
                return;
            }
            if (craftingController != null && InventoryLayout.isCraftInputSlot(slotIndex, craftingController.getGrid())) {
                craftingController.getGrid().setSlot(InventoryLayout.toCraftInputIndex(slotIndex), stack);
            }
        }

        @Override
        public boolean canPlaceSlot(int slotIndex, ItemStack stack) {
            if (!InventoryLayout.isArmorSlot(slotIndex)) {
                return true;
            }
            if (!canUseArmorSlots()) {
                return false;
            }
            return inventory.getArmorLoadout().canPlace(InventoryLayout.toArmorSlot(slotIndex), stack);
        }

        private boolean canUseArmorSlots() {
            return InventoryLayout.shouldShowArmorSlots(craftingController == null ? null : craftingController.getGrid());
        }
    }
}
