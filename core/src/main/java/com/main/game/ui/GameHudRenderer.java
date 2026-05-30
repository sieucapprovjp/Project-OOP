package com.main.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryController;
import com.main.game.inventory.InventoryInteractionHandler;
import com.main.game.inventory.InventoryRenderer;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;

public class GameHudRenderer {

    private final Texture[] healthTextures = new Texture[21];
    private final Texture[] hungerTextures = new Texture[21];
    private final Texture hotbarTex;
    private final Texture selectorTex;
    private final Texture xpBgTex;
    private final Texture xpFgTex;
    private final BitmapFont font;
    private final Matrix4 uiProjection = new Matrix4();

    public GameHudRenderer() {
        for (int i = 0; i <= 20; i++) {
            healthTextures[i] = loadTextureWithFallback(
                "mvp/ui/health/health" + i + ".png",
                "mvp/ui/health/health0.png");
            hungerTextures[i] = loadTextureWithFallback(
                "mvp/ui/hunger/hunger_" + i + ".png",
                "mvp/ui/hunger/hunger_0.png");
        }
        hotbarTex = new Texture(Gdx.files.internal("mvp/ui/hotbar.png"));
        selectorTex = new Texture(Gdx.files.internal("mvp/ui/selector.png"));
        xpBgTex = new Texture(Gdx.files.internal("mvp/ui/xp/xp_bg.png"));
        xpFgTex = new Texture(Gdx.files.internal("mvp/ui/xp/xp_fg.png"));
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void render(SpriteBatch batch, Viewport viewport, Inventory inventory,
                       InventoryController inventoryController, InventoryRenderer inventoryRenderer,
                       InventoryInteractionHandler inventoryInteractionHandler, Player player) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        drawDebugPalette(batch);

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float scale = 0.65f;
        float hbW = hotbarTex.getWidth() * scale;
        float hbH = hotbarTex.getHeight() * scale;
        float hbX = (sw - hbW) / 2f;
        float hbY = 10f;

        inventoryRenderer.renderHotbar(batch, inventory, inventoryController, hotbarTex, selectorTex, sw, scale);
        if (inventoryController.isInventoryOpen()) {
            inventoryRenderer.renderInventory(batch, inventory, sw, sh, scale);
            inventoryRenderer.renderCarriedStack(batch, inventoryInteractionHandler.getCarriedStack());
        }

        drawExperienceBar(batch, hbX, hbY, hbW, hbH, scale);
        drawHealthAndHunger(batch, player, hbX, hbY, hbW, hbH, scale);
        drawDebugText(batch, player, sh);

        batch.end();
    }

    public void dispose() {
        for (Texture texture : healthTextures) {
            if (texture != null) texture.dispose();
        }
        for (Texture texture : hungerTextures) {
            if (texture != null) texture.dispose();
        }
        hotbarTex.dispose();
        selectorTex.dispose();
        xpBgTex.dispose();
        xpFgTex.dispose();
        font.dispose();
    }

    private void drawDebugPalette(SpriteBatch batch) {
        if (BlockPalette.getGrass() != null) {
            batch.draw(BlockPalette.getGrass(), 0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getStone() != null) {
            batch.draw(BlockPalette.getStone(), 1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getBedrock() != null) {
            batch.draw(BlockPalette.getBedrock(), 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
    }

    private void drawExperienceBar(SpriteBatch batch, float hbX, float hbY, float hbW, float hbH, float scale) {
        float xpScaleX = hbW / xpBgTex.getWidth();
        float xpBgW = xpBgTex.getWidth() * xpScaleX;
        float xpBgH = xpBgTex.getHeight() * xpScaleX;
        float xpX = hbX + (hbW - xpBgW) / 2f;
        float xpY = hbY + hbH + (5f * scale);
        batch.draw(xpBgTex, xpX, xpY, xpBgW, xpBgH);
        batch.draw(xpFgTex, xpX, xpY, xpBgW * 0.5f, xpBgH,
            0, 0, (int) (xpFgTex.getWidth() * 0.5f), xpFgTex.getHeight(), false, false);
    }

    private void drawHealthAndHunger(SpriteBatch batch, Player player, float hbX, float hbY,
                                     float hbW, float hbH, float scale) {
        float xpScaleX = hbW / xpBgTex.getWidth();
        float xpBgH = xpBgTex.getHeight() * xpScaleX;
        float xpY = hbY + hbH + (5f * scale);

        int hp = Math.max(0, Math.min(20, player.getHealth()));
        Texture hpTex = healthTextures[hp];
        float hpW = hpTex.getWidth() * scale;
        float hpH = hpTex.getHeight() * scale;
        float hpY = xpY + xpBgH + (5f * scale);
        batch.draw(hpTex, hbX, hpY, hpW, hpH);

        int hunger = 20;
        Texture hungerTex = hungerTextures[hunger];
        float hgW = hungerTex.getWidth() * (scale * 2f);
        float hgH = hungerTex.getHeight() * (scale * 2f);
        batch.draw(hungerTex, hbX + hbW - hgW, hpY, hgW, hgH);
    }

    private void drawDebugText(SpriteBatch batch, Player player, float screenHeight) {
        font.setColor(Color.WHITE);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, screenHeight - 40);
        font.draw(batch, "X: " + (int) player.getX() + "  Y: " + (int) player.getY(), 20, screenHeight - 60);
    }

    private Texture loadTextureWithFallback(String path, String fallbackPath) {
        return Gdx.files.internal(path).exists()
            ? new Texture(Gdx.files.internal(path))
            : new Texture(Gdx.files.internal(fallbackPath));
    }
}
