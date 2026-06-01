package com.main.game.utilityblock.chest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemSlotAccess;
import com.main.game.inventory.ItemSlotInteractionController;
import com.main.game.inventory.ItemStack;

public class ChestInteractionHandler {

    private final ItemSlotInteractionController slotInteraction = new ItemSlotInteractionController();

    public void update(Inventory inventory, ChestState chestState, ChestRenderer renderer) {
        if (inventory == null || chestState == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            slotInteraction.onLeftClick(new ChestSlotAccess(inventory, chestState), slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            slotInteraction.onRightClick(new ChestSlotAccess(inventory, chestState), slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        slotInteraction.returnCarriedStackToInventory(inventory);
    }

    public ItemStack getCarriedStack() {
        return slotInteraction.getCarriedStack();
    }

    private static class ChestSlotAccess implements ItemSlotAccess {
        private final Inventory inventory;
        private final ChestState chestState;

        ChestSlotAccess(Inventory inventory, ChestState chestState) {
            this.inventory = inventory;
            this.chestState = chestState;
        }

        @Override
        public boolean isWritableSlot(int slotIndex) {
            return slotIndex >= 0 && slotIndex < inventory.getTotalSize()
                || ChestLayout.isChestSlot(slotIndex);
        }

        @Override
        public ItemStack getSlot(int slotIndex) {
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                return inventory.getSlot(slotIndex);
            }
            if (ChestLayout.isChestSlot(slotIndex)) {
                return chestState.getSlot(ChestLayout.toChestIndex(slotIndex));
            }
            return null;
        }

        @Override
        public void setSlot(int slotIndex, ItemStack stack) {
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                inventory.setSlot(slotIndex, stack);
                return;
            }
            if (ChestLayout.isChestSlot(slotIndex)) {
                chestState.setSlot(ChestLayout.toChestIndex(slotIndex), stack);
            }
        }
    }
}
