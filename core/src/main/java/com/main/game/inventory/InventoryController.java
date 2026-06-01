package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InventoryController {

    private int selectedHotbarSlot = 0;
    private boolean inventoryOpen = false;
    private boolean justClosed = false;

    public boolean update() {
        justClosed = false;
        boolean inventoryKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.E);
        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                selectedHotbarSlot = i;
            }
        }
        return inventoryKeyPressed;
    }

    public void open() {
        inventoryOpen = true;
        justClosed = false;
    }

    public void close() {
        boolean wasOpen = inventoryOpen;
        inventoryOpen = false;
        justClosed = wasOpen;
    }

    public int getSelectedHotbarSlot() {
        return selectedHotbarSlot;
    }

    public boolean isInventoryOpen() {
        return inventoryOpen;
    }

    public boolean wasJustClosed() {
        return justClosed;
    }
}
