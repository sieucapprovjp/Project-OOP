package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemSlotAccess;
import com.main.game.inventory.ItemSlotInteractionController;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

public class FurnaceInteractionHandler {

    private final ItemSlotInteractionController slotInteraction = new ItemSlotInteractionController();

    public void update(Inventory inventory, FurnaceState furnaceState, FurnaceRenderer renderer) {
        if (inventory == null || furnaceState == null || renderer == null) {
            return;
        }

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        int slot = renderer.findHoveredSlot(mouseX, mouseY);
        if (slot < 0) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            slotInteraction.onLeftClick(new FurnaceSlotAccess(inventory, furnaceState), slot);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            slotInteraction.onRightClick(new FurnaceSlotAccess(inventory, furnaceState), slot);
        }
    }

    public void onCloseInventory(Inventory inventory) {
        slotInteraction.returnCarriedStackToInventory(inventory);
    }

    public ItemStack getCarriedStack() {
        return slotInteraction.getCarriedStack();
    }

    private static class FurnaceSlotAccess implements ItemSlotAccess {
        private final Inventory inventory;
        private final FurnaceState furnaceState;

        FurnaceSlotAccess(Inventory inventory, FurnaceState furnaceState) {
            this.inventory = inventory;
            this.furnaceState = furnaceState;
        }

        @Override
        public boolean isSpecialTakeSlot(int slotIndex) {
            return FurnaceLayout.isOutputSlot(slotIndex);
        }

        @Override
        public ItemStack takeSpecialSlot(int slotIndex, ItemStack carriedStack, boolean singleItem) {
            return takeOutput(furnaceState, carriedStack, singleItem);
        }

        @Override
        public boolean isWritableSlot(int slotIndex) {
            return slotIndex >= 0 && slotIndex < inventory.getTotalSize()
                || FurnaceLayout.isInputSlot(slotIndex)
                || FurnaceLayout.isFuelSlot(slotIndex);
        }

        @Override
        public ItemStack getSlot(int slotIndex) {
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                return inventory.getSlot(slotIndex);
            }
            if (FurnaceLayout.isInputSlot(slotIndex)) {
                return furnaceState.getInput();
            }
            if (FurnaceLayout.isFuelSlot(slotIndex)) {
                return furnaceState.getFuel();
            }
            return null;
        }

        @Override
        public void setSlot(int slotIndex, ItemStack stack) {
            if (slotIndex >= 0 && slotIndex < inventory.getTotalSize()) {
                inventory.setSlot(slotIndex, stack);
                return;
            }
            if (FurnaceLayout.isInputSlot(slotIndex)) {
                furnaceState.setInput(stack);
            } else if (FurnaceLayout.isFuelSlot(slotIndex)) {
                furnaceState.setFuel(stack);
            }
        }
    }

    private static ItemStack takeOutput(FurnaceState furnaceState, ItemStack carriedStack, boolean singleItem) {
        ItemStack output = furnaceState.getOutput();
        if (output == null || output.getCount() <= 0) {
            furnaceState.setOutput(null);
            return carriedStack;
        }

        int moved = singleItem ? 1 : output.getCount();
        if (carriedStack == null) {
            ItemStack nextCarriedStack = output.copy();
            nextCarriedStack.setCount(moved);
            output.subtract(moved);
            if (output.getCount() <= 0) {
                furnaceState.setOutput(null);
            }
            return nextCarriedStack;
        }

        if (!carriedStack.getItemId().equals(output.getItemId())) {
            return carriedStack;
        }
        int room = ItemRegistry.getMaxStack(carriedStack.getItemId()) - carriedStack.getCount();
        if (room <= 0) {
            return carriedStack;
        }
        moved = Math.min(room, moved);
        carriedStack.add(moved);
        output.subtract(moved);
        if (output.getCount() <= 0) {
            furnaceState.setOutput(null);
        }
        return carriedStack;
    }
}
