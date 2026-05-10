package com.main.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.navigation.ScreenId;
import com.main.game.navigation.ScreenRouter;
import com.main.game.screens.BaseScreen;
import com.main.game.screens.GameScreen;
import com.main.game.screens.StateScreen;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

/**
 * Entry point của game — thay thế file MainGame.java hiện tại.
 * SpriteBatch được tạo một lần ở đây và share cho tất cả Screen
 * để tránh tạo nhiều batch gây tốn bộ nhớ.
 *
 * TODO(HUY-LEAD):
 *  - Chuẩn hóa lifecycle tài nguyên dùng chung (SpriteBatch, AssetManager).
 *  - Bổ sung cơ chế chuyển screen an toàn khi có Menu/Pause/GameOver.
 */
public class MainGame extends Game {

    private SpriteBatch batch;
    private AssetManager assetManager;
    private ScreenRouter screenRouter;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        screenRouter = new ScreenRouter(this);
        screenRouter.request(ScreenId.GAME);
    }

    @Override
    public void render() {
        screenRouter.flush();
        super.render();
    }

    public Screen createScreen(ScreenId id) {
        switch (id) {
            case GAME:
                return new GameScreen(this);
            case MENU:
            case PAUSE:
            case GAME_OVER:
                return new StateScreen(this, id);
            default:
                throw new IllegalArgumentException("Unsupported screen id: " + id);
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public ScreenRouter getScreenRouter() {
        return screenRouter;
    }

    @Override
    public void dispose() {
        Screen current = getScreen();
        if (current instanceof BaseScreen) {
            ((BaseScreen) current).onExit();
        }
        if (current != null) {
            current.dispose();
        }
        // Dispose shared resources
        BlockPalette.dispose();
        TextureManager.getInstance().dispose();
        assetManager.dispose();
        batch.dispose();
    }
}
