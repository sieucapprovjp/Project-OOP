package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.GameState;
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
    private Texture settingsTexture;

    private float doneScale = 1f;
    private float backScale = 1f;
    private float settingsScale = 1f;

    private final int[] menuChoices = {0, 0, 0, 0};

    private static final float SCRATCH_W = 480f;
    private static final float SCRATCH_H = 360f;
    private static final float DONE_CENTER_X = -90f;
    private static final float SETTINGS_CENTER_X = 90f;
    private static final float BUTTON_CENTER_Y = -135f;

    // Cached button rects (computed once per frame in draw)
    private float doneX, doneY, doneW, doneH;
    private float backX, backY, backW, backH;
    private float settingsX, settingsY, settingsW, settingsH;
    private float uiScale;
    private float logoX, logoY, logoW, logoH;
    private float panelX, panelY, panelW, panelH;
    private float lblX, lblY, lblW, lblH;

    public ModeSelectScreen(MainGame game) {
        super(game);
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.MODE_SELECT;
    }

    @Override
    public void show() {
        FileHandle[] stageFiles = Gdx.files.internal("stage").list();
        if (stageFiles.length > 0) {
            int bgIndex = MathUtils.random(stageFiles.length - 1);
            bgTexture = new Texture(stageFiles[bgIndex]);
        } else {
            bgTexture = new Texture(Gdx.files.internal("images/stage_sprite/empty2.png"));
        }
        logoTexture = new Texture(Gdx.files.internal("images/stage_sprite/splash-worldoptions.png"));
        panelTexture = new Texture(Gdx.files.internal("images/menu/world-options2.png"));
        labelsTexture = new Texture(Gdx.files.internal("images/menu2/world-options-text.png"));
        doneTexture = new Texture(Gdx.files.internal("images/menu/done.png"));
        backTexture = new Texture(Gdx.files.internal("images/menu/back.png"));
        settingsTexture = new Texture(Gdx.files.internal("images/stage_sprite/spl1b-game_settings.png"));

        GameState gameState = game.getGameState();
        menuChoices[0] = gameState.hardcore ? 3 : (gameState.creative ? 1 : 0);
        menuChoices[1] = gameState.bonusChest;
        menuChoices[2] = gameState.skin;
        menuChoices[3] = gameState.loot;
    }

    @Override
    public void update(float delta) {
        updateLayout();
        float sh = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = sh - Gdx.input.getY();
        boolean clicked = Gdx.input.justTouched();

        // Done button hover
        boolean doneHover = mx >= doneX && mx <= doneX + doneW && my >= doneY && my <= doneY + doneH;
        doneScale += ((doneHover ? 1.05f : 1.0f) - doneScale) * 0.2f;
        if (doneHover && clicked) {
            applyMenuChoices();
            game.getScreenRouter().request(ScreenId.GAME);
        }

        // Back button hover
        boolean backHover = mx >= backX && mx <= backX + backW && my >= backY && my <= backY + backH;
        backScale += ((backHover ? 1.05f : 1.0f) - backScale) * 0.2f;
        if (backHover && clicked) {
            game.getScreenRouter().request(ScreenId.MENU);
        }

        // Settings button hover
        boolean settingsHover = mx >= settingsX && mx <= settingsX + settingsW && my >= settingsY && my <= settingsY + settingsH;
        settingsScale += ((settingsHover ? 1.05f : 1.0f) - settingsScale) * 0.2f;
        if (settingsHover && clicked) {
            game.getScreenRouter().request(ScreenId.SETTINGS);
        }

        if (clicked && !(doneHover || backHover || settingsHover)) {
            handleOptionClick(mx, my);
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        updateLayout();

        batch.getProjectionMatrix().setToOrtho2D(0, 0, sw, sh);
        batch.begin();

        // Background
        batch.draw(bgTexture, 0, 0, sw, sh);
        Color prev = new Color(batch.getColor());
        batch.setColor(0, 0, 0, 0.4f);
        batch.draw(bgTexture, 0, 0, sw, sh);
        batch.setColor(prev);

        // Logo (small title at top)
        batch.draw(logoTexture, logoX, logoY, logoW, logoH);

        // Panel and labels use the original Scratch center coordinate system.
        batch.draw(panelTexture, panelX, panelY, panelW, panelH);
        batch.draw(labelsTexture, lblX, lblY, lblW, lblH);

        // Done button
        drawScaledButton(doneTexture, doneX, doneY, doneW, doneH, doneScale);

        // Settings button (placeholder)
        drawScaledButton(settingsTexture, settingsX, settingsY, settingsW, settingsH, settingsScale);

        // Back button
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

    private void handleOptionClick(float mx, float my) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float scratchX = (mx - sw / 2f) / uiScale;
        float scratchY = (my - sh / 2f) / uiScale;

        int optID = Math.round((15f - scratchY) / 34f) + 1;
        int choiceID = Math.round((scratchX + 58f) / 72f);

        if (optID < 1 || optID > 4) {
            return;
        }

        int maxChoice;
        switch (optID) {
            case 1:
                maxChoice = 3;
                break;
            case 2:
                maxChoice = 2;
                break;
            case 3:
                maxChoice = 3;
                break;
            case 4:
                maxChoice = 1;
                break;
            default:
                return;
        }

        if (choiceID < 0 || choiceID > maxChoice) {
            return;
        }

        menuChoices[optID - 1] = choiceID;
    }

    private void updateLayout() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        uiScale = Math.min(sw / SCRATCH_W, sh / SCRATCH_H);

        float logoScale = uiScale * 0.8f;
        logoW = logoTexture.getWidth() * logoScale;
        logoH = logoTexture.getHeight() * logoScale;
        logoX = (sw - logoW) / 2f;
        logoY = sh - logoH - 8f * uiScale;

        panelW = panelTexture.getWidth() * uiScale;
        panelH = panelTexture.getHeight() * uiScale;
        panelX = scratchXToScreen(-panelTexture.getWidth() / 2f);
        panelY = scratchYToScreen(-panelTexture.getHeight() / 2f);

        lblW = labelsTexture.getWidth() * uiScale;
        lblH = labelsTexture.getHeight() * uiScale;
        lblX = scratchXToScreen(-labelsTexture.getWidth() / 2f);
        lblY = scratchYToScreen(-labelsTexture.getHeight() / 2f);

        float baseBtnW = doneTexture.getWidth() * uiScale;
        float baseBtnH = doneTexture.getHeight() * uiScale;

        doneW = baseBtnW;
        doneH = baseBtnH;
        doneX = scratchXToScreen(DONE_CENTER_X - doneTexture.getWidth() / 2f);
        doneY = scratchYToScreen(BUTTON_CENTER_Y - doneTexture.getHeight() / 2f);

        settingsW = baseBtnW;
        settingsH = baseBtnH;
        settingsX = scratchXToScreen(SETTINGS_CENTER_X - doneTexture.getWidth() / 2f);
        settingsY = doneY;

        backW = backTexture.getWidth() * uiScale;
        backH = backTexture.getHeight() * uiScale;
        backX = (sw - backW) / 2f;
        backY = scratchYToScreen(-170f - backTexture.getHeight() / 2f);
    }

    private float scratchXToScreen(float scratchX) {
        return (Gdx.graphics.getWidth() / 2f) + scratchX * uiScale;
    }

    private float scratchYToScreen(float scratchY) {
        return (Gdx.graphics.getHeight() / 2f) + scratchY * uiScale;
    }

    private void applyMenuChoices() {
        GameState gameState = game.getGameState();
        int gameMode = menuChoices[0];

        switch (gameMode) {
            case 0:
                gameState.creative = false;
                gameState.survival = true;
                gameState.hardcore = false;
                break;
            case 1:
                gameState.creative = true;
                gameState.survival = false;
                gameState.hardcore = false;
                break;
            case 2:
                gameState.creative = false;
                gameState.survival = true;
                gameState.hardcore = false;
                break;
            case 3:
                gameState.creative = false;
                gameState.survival = true;
                gameState.hardcore = true;
                break;
            default:
                break;
        }

        gameState.bonusChest = menuChoices[1];
        gameState.skin = menuChoices[2];
        gameState.loot = menuChoices[3];
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
        if (settingsTexture != null) settingsTexture.dispose();
    }
}
