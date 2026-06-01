package com.main.game.utilityblock.chest;

import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;

public final class ChestLayout {

    public static final int CHEST_SLOT_BASE = Inventory.TOTAL_SIZE;

    private static final float CHEST_SLOT_ORIGIN_X = 34f;
    private static final float CHEST_TOP_Y = 590f;
    private static final float INVENTORY_SLOT_ORIGIN_X = 34f;
    private static final float INVENTORY_MAIN_TOP_Y = 309f;
    private static final float INVENTORY_HOTBAR_Y = 48f;
    private static final float SLOT_STEP_PX = 80f;
    private static final float SLOT_SIZE_PX = 72f;

    private ChestLayout() {
    }

    public static int findSlot(float screenX, float screenY, InventoryLayout.PanelRect panel) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slot = CHEST_SLOT_BASE + row * Inventory.HOTBAR_SIZE + col;
                if (insideSlot(screenX, screenY,
                    chestSlotX(panel, col),
                    chestSlotY(panel, row),
                    slotSize(panel))) {
                    return slot;
                }
            }
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

    public static boolean isChestSlot(int slot) {
        return slot >= CHEST_SLOT_BASE && slot < CHEST_SLOT_BASE + ChestState.SLOT_COUNT;
    }

    public static int toChestIndex(int slot) {
        return isChestSlot(slot) ? slot - CHEST_SLOT_BASE : -1;
    }

    public static float chestSlotX(InventoryLayout.PanelRect panel, int col) {
        return panel.x + (CHEST_SLOT_ORIGIN_X + col * SLOT_STEP_PX) * panel.scale;
    }

    public static float chestSlotY(InventoryLayout.PanelRect panel, int row) {
        return panel.y + (CHEST_TOP_Y - row * SLOT_STEP_PX) * panel.scale;
    }

    public static float inventorySlotX(InventoryLayout.PanelRect panel, int col) {
        return panel.x + (INVENTORY_SLOT_ORIGIN_X + col * SLOT_STEP_PX) * panel.scale;
    }

    public static float inventoryMainSlotY(InventoryLayout.PanelRect panel, int row) {
        return panel.y + (INVENTORY_MAIN_TOP_Y - row * SLOT_STEP_PX) * panel.scale;
    }

    public static float hotbarSlotY(InventoryLayout.PanelRect panel) {
        return panel.y + INVENTORY_HOTBAR_Y * panel.scale;
    }

    public static float slotSize(InventoryLayout.PanelRect panel) {
        return SLOT_SIZE_PX * panel.scale;
    }

    private static boolean insideSlot(float px, float py, float slotX, float slotY, float slotSize) {
        return px >= slotX && px <= slotX + slotSize && py >= slotY && py <= slotY + slotSize;
    }
}
