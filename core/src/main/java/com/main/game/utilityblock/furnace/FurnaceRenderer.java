package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryLayout;
import com.main.game.inventory.ItemSlotRenderer;
import com.main.game.inventory.ItemStack;

public class FurnaceRenderer {

    private static final float ARROW_X = 362f;
    private static final float ARROW_Y = 514f;
    private static final float FLAME_X = 295f;
    private static final float FLAME_Y = 504f;
    private static final float FLAME_SCALE = 1.15f;
    private static final int MIN_BURNING_FLAME_FRAME = 1;

    private final ItemSlotRenderer itemSlotRenderer;
    private final Texture furnaceTexture;
    private final Texture[] arrowFrames = new Texture[12];
    private final Texture[] flameFrames = new Texture[14];

    public FurnaceRenderer() {
        itemSlotRenderer = new ItemSlotRenderer();
        furnaceTexture = new Texture(Gdx.files.internal("util_block/gui/furnace.png"));
        for (int i = 0; i < arrowFrames.length; i++) {
            arrowFrames[i] = new Texture(Gdx.files.internal("util_block/process/arrow" + i + ".png"));
        }
        for (int i = 0; i < flameFrames.length; i++) {
            flameFrames[i] = new Texture(Gdx.files.internal("util_block/process/flame" + i + ".png"));
        }
    }

    public void renderFurnace(SpriteBatch batch, Inventory inventory, FurnaceState furnaceState, float sw, float sh) {
        InventoryLayout.PanelRect panel = panel(sw, sh);

        batch.setColor(Color.WHITE);
        batch.draw(furnaceTexture, panel.x, panel.y, panel.width, panel.height);
        drawProgress(batch, panel, furnaceState);

        itemSlotRenderer.drawInSlot(batch, furnaceState.getInput(),
            FurnaceLayout.inputSlotX(panel),
            FurnaceLayout.inputSlotY(panel),
            FurnaceLayout.slotSize(panel));
        itemSlotRenderer.drawInSlot(batch, furnaceState.getFuel(),
            FurnaceLayout.fuelSlotX(panel),
            FurnaceLayout.fuelSlotY(panel),
            FurnaceLayout.slotSize(panel));
        itemSlotRenderer.drawInSlot(batch, furnaceState.getOutput(),
            FurnaceLayout.outputSlotX(panel),
            FurnaceLayout.outputSlotY(panel),
            FurnaceLayout.slotSize(panel));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
                int slotIndex = Inventory.HOTBAR_SIZE + row * Inventory.HOTBAR_SIZE + col;
                itemSlotRenderer.drawInSlot(batch, inventory.getSlot(slotIndex),
                    FurnaceLayout.inventorySlotX(panel, col),
                    FurnaceLayout.inventoryMainSlotY(panel, row),
                    FurnaceLayout.slotSize(panel));
            }
        }
        for (int col = 0; col < Inventory.HOTBAR_SIZE; col++) {
            itemSlotRenderer.drawInSlot(batch, inventory.getSlot(col),
                FurnaceLayout.inventorySlotX(panel, col),
                FurnaceLayout.hotbarSlotY(panel),
                FurnaceLayout.slotSize(panel));
        }
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        itemSlotRenderer.renderCarriedStack(batch, stack);
    }

    public int findHoveredSlot(float screenX, float screenY) {
        return FurnaceLayout.findSlot(screenX, screenY, panel(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public void dispose() {
        itemSlotRenderer.dispose();
        furnaceTexture.dispose();
        for (Texture texture : arrowFrames) {
            texture.dispose();
        }
        for (Texture texture : flameFrames) {
            texture.dispose();
        }
    }

    private InventoryLayout.PanelRect panel(float sw, float sh) {
        return InventoryLayout.computePanel(sw, sh, furnaceTexture.getWidth(), furnaceTexture.getHeight());
    }

    private void drawProgress(SpriteBatch batch, InventoryLayout.PanelRect panel, FurnaceState furnaceState) {
        if (furnaceState == null) {
            return;
        }
        if (furnaceState.isBurning()) {
            int flameIndex = Math.max(MIN_BURNING_FLAME_FRAME,
                frameIndex(furnaceState.getBurnRatio(), flameFrames.length));
            drawProcessTexture(batch, flameFrames[flameIndex], panel, FLAME_X, FLAME_Y, FLAME_SCALE);
        }
        if (furnaceState.getCookRatio() > 0f) {
            int arrowIndex = frameIndex(furnaceState.getCookRatio(), arrowFrames.length);
            drawProcessTexture(batch, arrowFrames[arrowIndex], panel, ARROW_X, ARROW_Y, 1f);
        }
    }

    private int frameIndex(float ratio, int frameCount) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        return Math.min(frameCount - 1, (int) (clamped * (frameCount - 1)));
    }

    private void drawProcessTexture(SpriteBatch batch, Texture texture, InventoryLayout.PanelRect panel,
                                    float x, float y, float processScale) {
        float width = texture.getWidth() * panel.scale * processScale;
        float height = texture.getHeight() * panel.scale * processScale;
        float centerAdjustX = texture.getWidth() * panel.scale * (processScale - 1f) * 0.5f;
        batch.draw(texture,
            panel.x + x * panel.scale - centerAdjustX,
            panel.y + y * panel.scale,
            width,
            height);
    }
}
