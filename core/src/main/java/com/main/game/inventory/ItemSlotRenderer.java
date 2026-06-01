package com.main.game.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class ItemSlotRenderer {

    private static final float ICON_DRAW_SCALE = 0.85f;
    private static final float SLOT_ITEM_SIZE_RATIO = 26f / InventoryLayout.INV_SLOT_SIZE_PX;
    private static final float SLOT_ITEM_Y_OFFSET_PX = -2f;
    private static final float CARRIED_ITEM_SIZE_PX = 42f;

    private final BitmapFont font;
    private final Texture durabilityTexture;

    public ItemSlotRenderer() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        durabilityTexture = new Texture(pixmap);
        pixmap.dispose();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/2c90030680a2fafd21f53fd39a0862e7.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 18;
        params.color = Color.WHITE;
        params.borderWidth = 2f;
        params.borderColor = new Color(0f, 0f, 0f, 0.85f);
        font = generator.generateFont(params);
        generator.dispose();
    }

    public void drawInSlot(SpriteBatch batch, ItemStack stack, float slotX, float slotY, float slotSize) {
        float itemSize = slotSize * SLOT_ITEM_SIZE_RATIO;
        float itemX = slotX + (slotSize - itemSize) / 2f;
        float itemY = slotY + (slotSize - itemSize) / 2f
            + SLOT_ITEM_Y_OFFSET_PX * (slotSize / InventoryLayout.INV_SLOT_SIZE_PX);
        draw(batch, stack, itemX, itemY, itemSize);
    }

    public void draw(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        TextureRegion texture = ItemRegistry.getTexture(stack.getItemId());
        if (texture == null) {
            return;
        }
        float renderSize = size * ICON_DRAW_SCALE;
        float renderY = y + (size - renderSize) / 2f;
        float renderX = x + (size - renderSize) / 2f
            + ItemRenderOffset.xOffset(stack.getItemId(), renderSize);
        batch.draw(texture, renderX, renderY, renderSize, renderSize);
        drawDurabilityBar(batch, stack, renderX, renderY - renderSize * 0.08f, renderSize);
        if (stack.getCount() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(stack.getCount()), renderX + renderSize * 0.48f, renderY + renderSize * 0.36f);
        }
    }

    public void renderCarriedStack(SpriteBatch batch, ItemStack stack) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        draw(batch, stack,
            mouseX - CARRIED_ITEM_SIZE_PX * 0.5f,
            mouseY - CARRIED_ITEM_SIZE_PX * 0.5f,
            CARRIED_ITEM_SIZE_PX);
    }

    public void dispose() {
        font.dispose();
        durabilityTexture.dispose();
    }

    private void drawDurabilityBar(SpriteBatch batch, ItemStack stack, float x, float y, float size) {
        if (!stack.hasDurability()) {
            return;
        }
        float ratio = stack.getDurabilityRatio();
        float barWidth = size * 0.82f;
        float barHeight = Math.max(2f, size * 0.07f);
        float barX = x + (size - barWidth) / 2f;
        float barY = y;

        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(durabilityTexture, barX - 1f, barY - 1f, barWidth + 2f, barHeight + 2f);

        if (ratio > 0.55f) {
            batch.setColor(0.15f, 0.9f, 0.25f, 1f);
        } else if (ratio > 0.25f) {
            batch.setColor(1f, 0.82f, 0.12f, 1f);
        } else {
            batch.setColor(0.95f, 0.16f, 0.12f, 1f);
        }
        batch.draw(durabilityTexture, barX, barY, barWidth * ratio, barHeight);
        batch.setColor(Color.WHITE);
    }
}
