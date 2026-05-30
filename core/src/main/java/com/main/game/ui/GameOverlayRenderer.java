package com.main.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.main.game.GameState;

public class GameOverlayRenderer {

    private final Texture overlayTexture;
    private final Texture pauseTexture;
    private final Texture deathTexture;
    private final Matrix4 uiProjection = new Matrix4();

    public GameOverlayRenderer() {
        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(Color.WHITE);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        pauseTexture = new Texture(Gdx.files.internal("images/stage_sprite/pause.png"));
        deathTexture = new Texture(Gdx.files.internal("images/stage_sprite/death_screen.png"));
    }

    public void renderPause(SpriteBatch batch) {
        renderFullScreenTexture(batch, pauseTexture);
    }

    public void renderDeath(SpriteBatch batch) {
        renderFullScreenTexture(batch, deathTexture);
    }

    public void renderBrightness(SpriteBatch batch, GameState gameState) {
        int brightness = gameState.brightness;
        float alpha;
        Color overlayColor;
        if (brightness < 50) {
            alpha = (50 - brightness) / 50f * 0.8f;
            overlayColor = new Color(0f, 0f, 0f, alpha);
        } else if (brightness > 50) {
            alpha = (brightness - 50) / 50f * 0.4f;
            overlayColor = new Color(1f, 1f, 1f, alpha);
        } else {
            return;
        }

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.setColor(overlayColor);
        batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
    }

    public void dispose() {
        overlayTexture.dispose();
        pauseTexture.dispose();
        deathTexture.dispose();
    }

    private void renderFullScreenTexture(SpriteBatch batch, Texture texture) {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.draw(texture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }
}
