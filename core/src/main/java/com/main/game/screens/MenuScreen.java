package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

/**
 * Màn hình Menu chính — sử dụng ảnh gốc từ Paper Minecraft.
 *
 * Layout:
 *  - Nền: 1 ảnh bất kỳ từ thư mục stage
 *  - Layer 2: splash-worldoptions.png
 *  - 4 nút bấm texture: New Game, Load Worlds, Game Help, Settings
 *  - Hiệu ứng hover: phóng to 105% + sáng lên (giống Scratch gốc)
 */
public class MenuScreen extends BaseScreen {

    // Textures
    private Texture stageTexture;
    private Texture layer2Texture;
    private Texture[] btnTextures;  // 0=NewGame, 1=LoadWorlds, 2=Help, 3=Settings

    // Hover animation state per button (current scale, 1.0 = normal)
    private float[] btnScales;

    // Button layout constants (will be computed in draw based on screen size)
    private static final int BTN_COUNT = 4;

    public MenuScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.MENU;
    }

    @Override
    public void show() {
        // Background stage layer (pick any single file from stage folder)
        FileHandle[] stageFiles = Gdx.files.internal("stage").list();
        if (stageFiles.length > 0) {
            int index = MathUtils.random(stageFiles.length - 1);
            stageTexture = new Texture(stageFiles[index]);
        } else {
            stageTexture = new Texture(Gdx.files.internal("stage/sky.png"));
        }

        // Layer 2 sprite
        layer2Texture = new Texture(Gdx.files.internal("images/stage_sprite/splash-worldoptions.png"));

        // Buttons
        btnTextures = new Texture[BTN_COUNT];
        btnTextures[0] = new Texture(Gdx.files.internal("images/stage_sprite/spl1b-new_game.png"));
        btnTextures[1] = new Texture(Gdx.files.internal("images/stage_sprite/spl1b-load_game.png"));
        btnTextures[2] = new Texture(Gdx.files.internal("images/stage_sprite/spl1b-help.png"));
        btnTextures[3] = new Texture(Gdx.files.internal("images/stage_sprite/spl1b-game_settings.png"));

        btnScales = new float[BTN_COUNT];
        for (int i = 0; i < BTN_COUNT; i++) btnScales[i] = 1.0f;
    }

    @Override
    public void update(float delta) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY(); // flip Y for libGDX coords

        // Compute button positions (same logic as draw)
        float uiScale = Math.min(sw / 482f, sh / 344f);
        float btnW = 262f * uiScale;
        float btnH = 36f * uiScale;
        float btnX = (sw - btnW) / 2f;
        float btnStartY = sh * 0.45f;
        float btnGap = btnH + 10f * uiScale;

        boolean clicked = Gdx.input.justTouched();

        for (int i = 0; i < BTN_COUNT; i++) {
            float by = btnStartY - i * btnGap;

            // Check hover
            boolean hover = mx >= btnX && mx <= btnX + btnW && my >= by && my <= by + btnH;

            // Smooth scale animation (like Scratch: (target - current) * 0.2)
            float target = hover ? 1.05f : 1.0f;
            btnScales[i] += (target - btnScales[i]) * 0.2f;

            // Handle click
            if (hover && clicked) {
                switch (i) {
                    case 0: // New Game
                        game.getScreenRouter().request(ScreenId.MODE_SELECT);
                        break;
                    case 1: // Load Worlds
                        game.getScreenRouter().request(ScreenId.GAME);
                        break;
                    case 2: // Help (placeholder)
                        break;
                    case 3: // Settings (placeholder)
                        break;
                }
            }
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Use a separate ortho projection for UI
        batch.getProjectionMatrix().setToOrtho2D(0, 0, sw, sh);
        batch.begin();

        // ── Draw stage background (center-crop) ──
        float texW = stageTexture.getWidth();
        float texH = stageTexture.getHeight();
        float bgScale = Math.max(sw / texW, sh / texH);
        float drawW = texW * bgScale;
        float drawH = texH * bgScale;
        float drawX = (sw - drawW) / 2f;
        float drawY = (sh - drawH) / 2f;
        batch.draw(stageTexture, drawX, drawY, drawW, drawH);

        // ── Scale factor based on screen vs original splash size (482x344) ──
        float uiScale = Math.min(sw / 482f, sh / 344f);

        // ── Draw layer 2 sprite ──
        float layer2W = layer2Texture.getWidth() * uiScale;
        float layer2H = layer2Texture.getHeight() * uiScale;
        float layer2X = (sw - layer2W) / 2f;
        float layer2Y = sh - layer2H - 20f * uiScale;
        batch.draw(layer2Texture, layer2X, layer2Y, layer2W, layer2H);

        // ── Draw 4 buttons ──
        float btnBaseW = 262f * uiScale;
        float btnBaseH = 36f * uiScale;
        float btnX = (sw - btnBaseW) / 2f;
        float btnStartY = sh * 0.45f;
        float btnGap = btnBaseH + 10f * uiScale;

        for (int i = 0; i < BTN_COUNT; i++) {
            float by = btnStartY - i * btnGap;
            float scale = btnScales[i];

            float btnW = btnBaseW * scale;
            float btnH = btnBaseH * scale;
            // Center the scaled button on the original position
            float bx = btnX + (btnBaseW - btnW) / 2f;
            float scaledY = by + (btnBaseH - btnH) / 2f;

            // Brightness effect: when scale > 1, make slightly brighter
            float bright = (scale - 1f) * 10f; // 0 to ~0.5
            batch.setColor(1f + bright, 1f + bright, 1f + bright, 1f);
            batch.draw(btnTextures[i], bx, scaledY, btnW, btnH);
        }

        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // No viewport needed — we use raw screen coords
    }

    @Override
    public void hide() {
        disposeTextures();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposeTextures();
    }

    private void disposeTextures() {
        if (stageTexture != null) {
            stageTexture.dispose();
            stageTexture = null;
        }
        if (layer2Texture != null) {
            layer2Texture.dispose();
            layer2Texture = null;
        }
        if (btnTextures != null) {
            for (Texture t : btnTextures) {
                if (t != null) t.dispose();
            }
            btnTextures = null;
        }
    }
}
