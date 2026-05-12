package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.MainGame;
import com.main.game.navigation.ScreenId;

/**
 * Màn hình chọn chế độ chơi — sử dụng ảnh gốc từ Paper Minecraft.
 *
 * Layout:
 *  - Nền biome panorama ngẫu nhiên
 *  - Logo nhỏ "MINECRAFT 2D EDITION" (splash-worldoptions.png)
 *  - Bảng chọn (world-options2.png) + nhãn text (world-options-text.png)
 *  - Nút Done + Back với hiệu ứng hover
 */
public class ModeSelectScreen extends BaseScreen {

    private Texture bgTexture;
    private Texture logoTexture;
    private Texture panelTexture;
    private Texture labelsTexture;
    private Texture doneTexture;
    private Texture backTexture;

    private float doneScale = 1f;
    private float backScale = 1f;

    // Cached button rects (computed once per frame in draw)
    private float doneX, doneY, doneW, doneH;
    private float backX, backY, backW, backH;

    public ModeSelectScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.MODE_SELECT;
    }

    @Override
    public void show() {
        int bgIndex = MathUtils.random(2, 28);
        bgTexture = new Texture(Gdx.files.internal("images/stage_sprite/empty" + bgIndex + ".png"));
        logoTexture = new Texture(Gdx.files.internal("images/stage_sprite/splash-worldoptions.png"));
        panelTexture = new Texture(Gdx.files.internal("images/menu/world-options2.png"));
        labelsTexture = new Texture(Gdx.files.internal("images/menu2/world-options-text.png"));
        doneTexture = new Texture(Gdx.files.internal("images/menu/done.png"));
        backTexture = new Texture(Gdx.files.internal("images/menu/back.png"));
    }

    @Override
    public void update(float delta) {
        float sh = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();
        boolean clicked = Gdx.input.justTouched();

        // Done button hover
        boolean doneHover = mx >= doneX && mx <= doneX + doneW && my >= doneY && my <= doneY + doneH;
        doneScale += ((doneHover ? 1.05f : 1.0f) - doneScale) * 0.2f;
        if (doneHover && clicked) {
            game.getScreenRouter().request(ScreenId.GAME);
        }

        // Back button hover
        boolean backHover = mx >= backX && mx <= backX + backW && my >= backY && my <= backY + backH;
        backScale += ((backHover ? 1.05f : 1.0f) - backScale) * 0.2f;
        if (backHover && clicked) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float uiScale = Math.min(sw / 482f, sh / 344f);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, sw, sh);
        batch.begin();

        // Background
        batch.draw(bgTexture, 0, 0, sw, sh);
        Color prev = new Color(batch.getColor());
        batch.setColor(0, 0, 0, 0.4f);
        batch.draw(bgTexture, 0, 0, sw, sh);
        batch.setColor(prev);

        // Logo (small title at top)
        float logoW = logoTexture.getWidth() * uiScale;
        float logoH = logoTexture.getHeight() * uiScale;
        float logoX = (sw - logoW) / 2f;
        float logoY = sh - logoH - 10f * uiScale;
        batch.draw(logoTexture, logoX, logoY, logoW, logoH);

        // Panel (world-options2) — centered below logo
        float panelW = panelTexture.getWidth() * uiScale;
        float panelH = panelTexture.getHeight() * uiScale;
        float panelX = (sw - panelW) / 2f;
        float panelY = logoY - panelH - 10f * uiScale;
        batch.draw(panelTexture, panelX, panelY, panelW, panelH);

        // Labels overlay (world-options-text) — on top of panel
        float lblW = labelsTexture.getWidth() * uiScale * 2f; // labels are small, scale up
        float lblH = labelsTexture.getHeight() * uiScale * 2f;
        float lblX = panelX + (panelW - lblW) / 2f;
        float lblY = panelY + (panelH - lblH) / 2f;
        batch.draw(labelsTexture, lblX, lblY, lblW, lblH);

        // Done button
        float baseBtnW = doneTexture.getWidth() * uiScale;
        float baseBtnH = doneTexture.getHeight() * uiScale;

        // Cache for hit detection
        doneW = baseBtnW;
        doneH = baseBtnH;
        doneX = sw / 2f - baseBtnW - 10f * uiScale;
        doneY = panelY - baseBtnH - 15f * uiScale;
        drawScaledButton(doneTexture, doneX, doneY, doneW, doneH, doneScale);

        // Back button
        backW = backTexture.getWidth() * uiScale;
        backH = backTexture.getHeight() * uiScale;
        backX = sw / 2f + 10f * uiScale;
        backY = doneY;
        drawScaledButton(backTexture, backX, backY, backW, backH, backScale);

        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawScaledButton(Texture tex, float x, float y, float w, float h, float scale) {
        float scaledW = w * scale;
        float scaledH = h * scale;
        float sx = x + (w - scaledW) / 2f;
        float sy = y + (h - scaledH) / 2f;
        float bright = (scale - 1f) * 10f;
        batch.setColor(1f + bright, 1f + bright, 1f + bright, 1f);
        batch.draw(tex, sx, sy, scaledW, scaledH);
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        super.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (logoTexture != null) logoTexture.dispose();
        if (panelTexture != null) panelTexture.dispose();
        if (labelsTexture != null) labelsTexture.dispose();
        if (doneTexture != null) doneTexture.dispose();
        if (backTexture != null) backTexture.dispose();
    }
}
