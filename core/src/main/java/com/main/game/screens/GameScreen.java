package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.main.game.GameState;
import com.main.game.MainGame;
import com.main.game.entities.EntityManager;
import com.main.game.entities.player.Player;
import com.main.game.interaction.BlockBreakOverlay;
import com.main.game.interaction.BlockBreaker;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryController;
import com.main.game.inventory.InventoryInteractionHandler;
import com.main.game.inventory.InventoryRenderer;
import com.main.game.items.BlockDropFactory;
import com.main.game.items.DroppedItemManager;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
import com.main.game.world.World;
import com.main.game.worldgen.BiomeMobSpawner;

public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM = 0.65f;

    private World world;
    private PhysicsEngine physics;
    private Player player;
    private EntityManager entityManager;
    private BlockBreaker blockBreaker;
    private BlockBreakOverlay blockBreakOverlay;
    private DroppedItemManager droppedItemManager;
    private Inventory inventory;
    private InventoryController inventoryController;
    private InventoryRenderer inventoryRenderer;
    private InventoryInteractionHandler inventoryInteractionHandler;
    private boolean paused;
    private boolean dead;
    private Texture overlayTexture;
    private BitmapFont overlayFont;
    private GlyphLayout overlayLayout;
    private Matrix4 uiProjection;
    private BitmapFont font;

    // Pause & Death textures
    private Texture pauseTexture;
    private Texture deathTexture;

    // HUD Textures
    private Texture[] healthTextures;
    private Texture[] hungerTextures;
    private Texture hotbarTex;
    private Texture selectorTex;
    private Texture xpBgTex;
    private Texture xpFgTex;

    private float deathBtnX, deathBtnY, deathBtnW, deathBtnH;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        // Tích hợp Seed Random
        long currentSeed = System.currentTimeMillis();
        world = new World(currentSeed);
        physics = new PhysicsEngine();

        // ─── MỒI CHUNK TRƯỚC KHI TÌM SPAWN ─────────────
        // 1. Đặt tạm Camera ra giữa bản đồ
        camera.position.set(world.width / 2f, world.height / 2f, 0f);
        camera.update();

        // 2. Ép World sinh ra Chunk đất đá tại vị trí Camera
        world.update(camera);

        // 3. Tìm Spawn Point (Chắc chắn sẽ chạm đất)
        Vector2 spawn = world.getSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;

        // 4. Khởi tạo Player tại tọa độ an toàn
        player = new Player(spawnX, spawnY, physics, world);

        // 5. Cập nhật lại camera bám theo sát Player
        camera.position.set(spawnX, spawnY, 0f);
        camera.update();
        // ───────────────────────────────────────────────

        // ── Khởi tạo EntityManager & Tools ─────────────
        entityManager = new EntityManager();
        entityManager.setPlayer(player);
        blockBreaker = new BlockBreaker();
        blockBreakOverlay = new BlockBreakOverlay();
        droppedItemManager = new DroppedItemManager();
        inventory = new Inventory();
        inventoryController = new InventoryController();
        inventoryRenderer = new InventoryRenderer();
        inventoryInteractionHandler = new InventoryInteractionHandler();
        blockBreaker.setBlockBreakListener((block, worldRef) ->
            droppedItemManager.spawn(BlockDropFactory.createDrop(block, worldRef), worldRef));

        // Spawner của team sử dụng seed hiện tại
        BiomeMobSpawner.spawnInitialMobs(world, player, physics, entityManager, currentSeed);

        paused = false;
        dead = false;
        camera.zoom = CAMERA_ZOOM;

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(Color.WHITE);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        overlayFont = new BitmapFont();
        overlayFont.setColor(Color.WHITE);
        overlayLayout = new GlyphLayout();
        uiProjection = new Matrix4();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Load HUD textures
        healthTextures = new Texture[21];
        hungerTextures = new Texture[21];
        for (int i = 0; i <= 20; i++) {
            healthTextures[i] = loadTextureWithFallback("mvp/ui/health/health" + i + ".png", "mvp/ui/health/health0.png");
            hungerTextures[i] = loadTextureWithFallback("mvp/ui/hunger/hunger_" + i + ".png", "mvp/ui/hunger/hunger_0.png");
        }
        hotbarTex = new Texture(Gdx.files.internal("mvp/ui/hotbar.png"));
        selectorTex = new Texture(Gdx.files.internal("mvp/ui/selector.png"));
        xpBgTex = new Texture(Gdx.files.internal("mvp/ui/xp/xp_bg.png"));
        xpFgTex = new Texture(Gdx.files.internal("mvp/ui/xp/xp_fg.png"));

        pauseTexture = new Texture(Gdx.files.internal("images/stage_sprite/pause.png"));
        deathTexture = new Texture(Gdx.files.internal("images/stage_sprite/death_screen.png"));
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) paused = !paused;
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) game.getScreenRouter().request(ScreenId.MENU);
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) DemoBlockViewer.populateDemo(world, Math.max(2, (int) player.getX()), Math.max(2, (int) player.getY()));
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) player.kill();
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) player.ban();

        inventoryController.update();
        if (inventoryController.wasJustClosed()) inventoryInteractionHandler.onCloseInventory(inventory);

        if (paused) {
            player.setMining(false, player.getX() + player.getWidth() / 2f);
            return;
        }

        if (!dead) {
            entityManager.update(delta);
            droppedItemManager.update(delta, world, player, inventory);
            if (inventoryController.isInventoryOpen()) inventoryInteractionHandler.update(inventory, inventoryRenderer);
        }

        if (player.getHealth() <= 0) dead = true;

        if (paused || dead) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (paused && Gdx.input.justTouched()) handlePauseClick(mx, my);
            else if (dead) {
                player.setMining(false, player.getX() + player.getWidth() / 2f);
                updateDeathButtonLayout();
                if (Gdx.input.justTouched() && mx >= deathBtnX && mx <= deathBtnX + deathBtnW && my >= deathBtnY && my <= deathBtnY + deathBtnH) {
                    handleDeathClick();
                }
            }
            return;
        }

        float halfW = camera.viewportWidth * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;

        // KIEN: Cập nhật World Chunk Streaming liên tục khi di chuyển
        world.update(camera);

        if (player != null && player.isAlive()) {
            float targetX = player.getX() + Player.PLAYER_W / 2f;
            float targetY = player.getY() + Player.PLAYER_H / 2f;
            float followLerp = Math.min(1f, delta * 7f);
            camera.position.x += (targetX - camera.position.x) * followLerp;
            camera.position.y += (targetY - camera.position.y) * followLerp;

            // XÓA clamp trục X để map chạy vô tận, chỉ giữ clamp đáy
            camera.position.y = Math.max(halfH, camera.position.y);
        } else {
            float cameraSpeed = 16f;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.x += cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += cameraSpeed * delta;
            camera.position.y = Math.max(halfH, camera.position.y);
        }

        blockBreaker.update(delta, player, world, camera, viewport);
        float miningTargetX = blockBreaker.hasHoveredBlock() ? blockBreaker.getHoveredBlockX() + 0.5f : player.getX() + player.getWidth() / 2f;
        player.setMining(blockBreaker.isBreaking(), miningTargetX);
    }

    @Override
    public void draw() {
        // KIEN: Tối màu hang động
        float surfaceY = world.height / 2f;
        float deepCaveY = 20f;
        float lightRatio = Math.max(0f, Math.min(1f, (camera.position.y - deepCaveY) / (surfaceY - deepCaveY)));
        float r = (0.4f * lightRatio) + (0.02f * (1 - lightRatio));
        float g = (0.7f * lightRatio) + (0.02f * (1 - lightRatio));
        float b = (1.0f * lightRatio) + (0.05f * (1 - lightRatio));

        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        droppedItemManager.render(batch);
        entityManager.render(batch);
        blockBreakOverlay.render(batch, blockBreaker);
        batch.end();

        // ── HUD (Giữ nguyên của Team) ─────────────────────────
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        if (BlockPalette.getGrass() != null) batch.draw(BlockPalette.getGrass(), 0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        if (BlockPalette.getStone() != null) batch.draw(BlockPalette.getStone(), 1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        if (BlockPalette.getBedrock() != null) batch.draw(BlockPalette.getBedrock(), 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);

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

        float xpScaleX = hbW / xpBgTex.getWidth();
        float xpBgW = xpBgTex.getWidth() * xpScaleX;
        float xpBgH = xpBgTex.getHeight() * xpScaleX;
        float xpX = hbX + (hbW - xpBgW) / 2f;
        float xpY = hbY + hbH + (5f * scale);
        batch.draw(xpBgTex, xpX, xpY, xpBgW, xpBgH);
        batch.draw(xpFgTex, xpX, xpY, xpBgW * 0.5f, xpBgH, 0, 0, (int) (xpFgTex.getWidth() * 0.5f), xpFgTex.getHeight(), false, false);

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

        font.setColor(Color.WHITE);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, sh - 40);
        font.draw(batch, "X: " + (int) player.getX() + "  Y: " + (int) player.getY(), 20, sh - 60);

        batch.end();

        if (paused) drawPauseOverlay();
        else if (dead) drawDeathOverlay();
        drawBrightnessOverlay();
    }

    private void drawBrightnessOverlay() {
        GameState gameState = game.getGameState();
        int brightness = gameState.brightness;
        float alpha;
        Color overlayColor;
        if (brightness < 50) {
            alpha = (50 - brightness) / 50f * 0.8f;
            overlayColor = new Color(0f, 0f, 0f, alpha);
        } else if (brightness > 50) {
            alpha = (brightness - 50) / 50f * 0.4f;
            overlayColor = new Color(1f, 1f, 1f, alpha);
        } else return;

        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.setColor(overlayColor);
        batch.draw(overlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawDeathOverlay() {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.draw(deathTexture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    private void handlePauseClick(float mx, float my) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        float bw = 300f * (sw / 640f);
        float bh = 50f * (sh / 360f);
        float bx = (sw - bw) / 2f;
        float by1 = sh * 0.45f;
        float by2 = sh * 0.30f;
        if (mx >= bx && mx <= bx + bw && my >= by1 && my <= by1 + bh) paused = false;
        else if (mx >= bx && mx <= bx + bw && my >= by2 && my <= by2 + bh) game.getScreenRouter().request(ScreenId.MENU);
    }

    private void handleDeathClick() {
        player.respawn(world.getSpawnPoint().x, world.getSpawnPoint().y);
        dead = false;
    }

    private void drawPauseOverlay() {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);
        batch.begin();
        batch.draw(pauseTexture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    private void updateDeathButtonLayout() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        deathBtnW = sw * 0.45f;
        deathBtnH = sh * 0.12f;
        deathBtnX = (sw - deathBtnW) / 2f;
        deathBtnY = sh * 0.38f;
    }

    private Texture loadTextureWithFallback(String path, String fallbackPath) {
        return Gdx.files.internal(path).exists() ? new Texture(Gdx.files.internal(path)) : new Texture(Gdx.files.internal(fallbackPath));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (pauseTexture != null) pauseTexture.dispose();
        if (deathTexture != null) deathTexture.dispose();
        font.dispose();
        BlockPalette.dispose();
        overlayTexture.dispose();
        overlayFont.dispose();
        if (healthTextures != null) for (Texture t : healthTextures) if (t != null) t.dispose();
        if (hungerTextures != null) for (Texture t : hungerTextures) if (t != null) t.dispose();
        if (hotbarTex != null) hotbarTex.dispose();
        if (selectorTex != null) selectorTex.dispose();
        if (xpBgTex != null) xpBgTex.dispose();
        if (xpFgTex != null) xpFgTex.dispose();
        if (blockBreakOverlay != null) blockBreakOverlay.dispose();
        if (droppedItemManager != null) droppedItemManager.clear();
        if (inventoryRenderer != null) inventoryRenderer.dispose();
        entityManager.dispose();
    }

    @Override
    public ScreenId getScreenId() { return ScreenId.GAME; }
}
