package com.main.game.utilityblock.furnace;

import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;

public final class FurnaceLayout {

    public static final int INPUT_SLOT = Inventory.TOTAL_SIZE;
    public static final int FUEL_SLOT = Inventory.TOTAL_SIZE + 1;
    public static final int OUTPUT_SLOT = Inventory.TOTAL_SIZE + 2;

    private static final float INVENTORY_SLOT_ORIGIN_X = 34f;
    private static final float INVENTORY_MAIN_TOP_Y = 309f;
    private static final float INVENTORY_HOTBAR_Y = 48f;
    private static final float INVENTORY_SLOT_STEP_PX = 80f;
    private static final float SLOT_SIZE_PX = 72f;
    private static final float INPUT_X = 274f;
    private static final float INPUT_Y = 572f;
    private static final float FUEL_X = 274f;
    private static final float FUEL_Y = 412f;
    private static final float OUTPUT_X = 434f;
    private static final float OUTPUT_Y = 492f;

    private FurnaceLayout() {
    }

    public static int findSlot(float screenX, float screenY, InventoryLayout.PanelRect panel) {
        if (insideSlot(screenX, screenY, inputSlotX(panel), inputSlotY(panel), slotSize(panel))) {
            return INPUT_SLOT;
        }
        if (insideSlot(screenX, screenY, fuelSlotX(panel), fuelSlotY(panel), slotSize(panel))) {
            return FUEL_SLOT;
        }
        if (insideSlot(screenX, screenY, outputSlotX(panel), outputSlotY(panel), slotSize(panel))) {
            return OUTPUT_SLOT;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slot = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                if (insideSlot(screenX, screenY,
                    inventorySlotX(panel, col),
                    inventoryMainSlotY(panel, row),
                    slotSize(panel))) {
                    return slot;
                }
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            if (insideSlot(screenX, screenY,
                inventorySlotX(panel, col),
                hotbarSlotY(panel),
                slotSize(panel))) {
                return col;
            }
        }
        return -1;
    }

    public static boolean isInputSlot(int slot) {
        return slot == INPUT_SLOT;
    }

    public static boolean isFuelSlot(int slot) {
        return slot == FUEL_SLOT;
    }

    public static boolean isOutputSlot(int slot) {
        return slot == OUTPUT_SLOT;
    }

    public static float inventorySlotX(InventoryLayout.PanelRect panel, int col) {
        return panel.x + (INVENTORY_SLOT_ORIGIN_X + col * INVENTORY_SLOT_STEP_PX) * panel.scale;
    }

    public static float inventoryMainSlotY(InventoryLayout.PanelRect panel, int row) {
        return panel.y + (INVENTORY_MAIN_TOP_Y - row * INVENTORY_SLOT_STEP_PX) * panel.scale;
    }

    public static float hotbarSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + INVENTORY_HOTBAR_Y * panel.scale;
    }

    public static float inputSlotX(InventoryLayout.PanelRect panel) {
        return panel.x + INPUT_X * panel.scale;
    }

    public static float inputSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + INPUT_Y * panel.scale;
    }

    public static float fuelSlotX(InventoryLayout.PanelRect panel) {
        return panel.x + FUEL_X * panel.scale;
    }

    public static float fuelSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + FUEL_Y * panel.scale;
    }

    public static float outputSlotX(InventoryLayout.PanelRect panel) {
        return panel.x + OUTPUT_X * panel.scale;
    }

    public static float outputSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + OUTPUT_Y * panel.scale;
    }

    public static float slotSize(InventoryLayout.PanelRect panel) {
        return SLOT_SIZE_PX * panel.scale;
    }

    private static boolean insideSlot(float px, float py, float slotX, float slotY, float slotSize) {
        return px >= slotX && px <= slotX + slotSize && py >= slotY && py <= slotY + slotSize;
    }
}
