package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.crafting.CraftingController;
import com.main.game.crafting.CraftingGrid;

public class InventoryRenderer {

    private static final float HOTBAR_SLOT_STEP_PX = 80f;
    private static final float HOTBAR_ITEM_X_PX = 8f;
    private static final float HOTBAR_ITEM_Y_PX = 12f;
    private static final float HOTBAR_ITEM_SIZE_PX = 64f;
    private static final float CRAFT_ITEM_X_OFFSET_PX = 4f;
    private static final float CRAFT_ITEM_Y_OFFSET_PX = -4f;
    private static final float TABLE_CRAFT_ITEM_Y_OFFSET_PX = 2f;

    private final ItemSlotRenderer itemSlotRenderer;
    private final Texture inventoryTexture;
    private final Texture craftingTableTexture;

    public InventoryRenderer() {
        itemSlotRenderer = new ItemSlotRenderer();
        inventoryTexture = new Texture(Gdx.files.internal("images/gui_invrow/inventory.png"));
        craftingTableTexture = new Texture(Gdx.files.internal("images/gui_invrow/crafting_table.png"));
    }

    public void renderHotbar(SpriteBatch batch, Inventory inventory, InventoryController controller,
                             Texture hotbarTexture, Texture selectorTexture, float sw, float scale) {
        float hbW = hotbarTexture.getWidth() * scale;
        float hbH = hotbarTexture.getHeight() * scale;
        float hbX = (sw - hbW) / 2f;
        float hbY = 10f;
        batch.draw(hotbarTexture, hbX, hbY, hbW, hbH);

        float selW = selectorTexture.getWidth() * scale;
        float selH = selectorTexture.getHeight() * scale;
        float slotOffset = 80f * scale;
        float selX = hbX - (4f * scale) + (controller.getSelectedHotbarSlot() * slotOffset);
        float selY = hbY - (4f * scale);
        batch.draw(selectorTexture, selX, selY, selW, selH);

        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            itemSlotRenderer.draw(batch, inventory.getSlot(i),
                hbX + (HOTBAR_ITEM_X_PX + i * HOTBAR_SLOT_STEP_PX) * scale,
                hbY + HOTBAR_ITEM_Y_PX * scale,
                HOTBAR_ITEM_SIZE_PX * scale);
        }
    }

    public void renderInventory(SpriteBatch batch, Inventory inventory, CraftingController craftingController,
                                float sw, float sh, float scale) {
        CraftingGrid grid = craftingController == null ? null : craftingController.getGrid();
        Texture panelTexture = craftingController != null && craftingController.isTableCrafting()
            ? craftingTableTexture
            : inventoryTexture;
        float craftItemYOffset = craftingController != null && craftingController.isTableCrafting()
            ? TABLE_CRAFT_ITEM_Y_OFFSET_PX
            : CRAFT_ITEM_Y_OFFSET_PX;
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, panelTexture.getWidth(), panelTexture.getHeight());

        batch.setColor(Color.WHITE);
        batch.draw(panelTexture, panel.x, panel.y, panel.width, panel.height);

        if (craftingController != null && grid != null) {
            for (int i = 0; i < grid.getSize(); i++) {
                itemSlotRenderer.drawInSlot(batch, grid.getSlot(i),
                    InventoryLayout.craftInputSlotX(panel, grid, i) + CRAFT_ITEM_X_OFFSET_PX * panel.scale,
                    InventoryLayout.craftInputSlotY(panel, grid, i) + craftItemYOffset * panel.scale,
                    InventoryLayout.slotSize(panel, grid));
            }
            itemSlotRenderer.drawInSlot(batch, craftingController.getResult(),
                InventoryLayout.craftResultSlotX(panel, grid) + CRAFT_ITEM_X_OFFSET_PX * panel.scale,
                InventoryLayout.craftResultSlotY(panel, grid) + craftItemYOffset * panel.scale,
                InventoryLayout.slotSize(panel, grid));
        }

        if (InventoryLayout.shouldShowArmorSlots(grid)) {
            for (ArmorSlot slot : ArmorSlot.values()) {
                itemSlotRenderer.drawInSlot(batch, inventory.getArmorSlot(slot),
                    InventoryLayout.armorSlotX(panel, slot),
                    InventoryLayout.armorSlotY(panel, slot),
                    InventoryLayout.slotSize(panel, grid));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                itemSlotRenderer.drawInSlot(batch, inventory.getSlot(slotIndex),
                    InventoryLayout.inventorySlotX(panel, grid, col),
                    InventoryLayout.inventoryMainSlotY(panel, grid, row),
                    InventoryLayout.slotSize(panel, grid));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            itemSlotRenderer.drawInSlot(batch, inventory.getSlot(col),
                InventoryLayout.inventorySlotX(panel, grid, col),
                InventoryLayout.hotbarSlotY(panel, grid),
                InventoryLayout.slotSize(panel, grid));
        }
    }

    public void renderInventory(SpriteBatch batch, Inventory inventory, float sw, float sh, float scale) {
        renderInventory(batch, inventory, null, sw, sh, scale);
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        itemSlotRenderer.renderCarriedStack(batch, stack);
    }

    public int findHoveredSlot(float screenX, float screenY) {
        return findHoveredSlot(screenX, screenY, null);
    }

    public int findHoveredSlot(float screenX, float screenY, CraftingController craftingController) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        CraftingGrid grid = craftingController == null ? null : craftingController.getGrid();
        Texture panelTexture = craftingController != null && craftingController.isTableCrafting()
            ? craftingTableTexture
            : inventoryTexture;
        InventoryLayout.PanelRect panel = InventoryLayout.computePanel(sw, sh, panelTexture.getWidth(), panelTexture.getHeight());
        return InventoryLayout.findInventorySlot(screenX, screenY, panel, grid);
    }

    public void dispose() {
        itemSlotRenderer.dispose();
        inventoryTexture.dispose();
        craftingTableTexture.dispose();
    }
}
