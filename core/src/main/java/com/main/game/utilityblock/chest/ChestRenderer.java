package com.main.game.utilityblock.chest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;
import com.main.game.inventory.ItemSlotRenderer;
import com.main.game.inventory.ItemStack;

public class ChestRenderer {

    private final ItemSlotRenderer itemSlotRenderer;
    private final Texture chestTexture;

    public ChestRenderer() {
        itemSlotRenderer = new ItemSlotRenderer();
        chestTexture = new Texture(Gdx.files.internal("util_block/gui/chest.png"));
    }

    public void renderChest(SpriteBatch batch, Inventory inventory, ChestState chestState, float sw, float sh) {
        InventoryLayout.PanelRect panel = panel(sw, sh);

        batch.setColor(Color.WHITE);
        batch.draw(chestTexture, panel.x, panel.y, panel.width, panel.height);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = row * Inventory.HOTBAR_SIZE + col;
                itemSlotRenderer.drawInSlot(batch, chestState.getSlot(slotIndex),
                    ChestLayout.chestSlotX(panel, col),
                    ChestLayout.chestSlotY(panel, row),
                    ChestLayout.slotSize(panel));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                itemSlotRenderer.drawInSlot(batch, inventory.getSlot(slotIndex),
                    ChestLayout.inventorySlotX(panel, col),
                    ChestLayout.inventoryMainSlotY(panel, row),
                    ChestLayout.slotSize(panel));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            itemSlotRenderer.drawInSlot(batch, inventory.getSlot(col),
                ChestLayout.inventorySlotX(panel, col),
                ChestLayout.hotbarSlotY(panel),
                ChestLayout.slotSize(panel));
        }
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        itemSlotRenderer.renderCarriedStack(batch, stack);
    }

    public int findHoveredSlot(float screenX, float screenY) {
        return ChestLayout.findSlot(screenX, screenY, panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void dispose() {
        itemSlotRenderer.dispose();
        chestTexture.dispose();
    }

    private InventoryLayout.PanelRect panel(float sw, float sh) {
        return InventoryLayout.computePanel(sw, sh, chestTexture.getWidth(), chestTexture.getHeight());
    }
}
