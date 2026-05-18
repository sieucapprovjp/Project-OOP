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

/**
 * Screen chính của game — nơi mọi module hội tụ.
 *
 * Mỗi thành viên chỉ cần quan tâm đến object của mình,
 * GameScreen lo việc gọi update/render theo đúng thứ tự.
 *
 * TODO(HUY-LEAD):
 * - Đây là điểm integration chính, chỉ merge khi module giao diện giữa các team
 * ổn định.
 */
public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM = 0.65f;
    private static final long DEFAULT_WORLD_SEED = 1337L;

    private World world; // TODO(KIEN-WORLD): quản lý world/chunk/camera follow
    private PhysicsEngine physics; // TODO(LHUNG-PHYSICS): collision + resolve
    private Player player; // DUOC-ENTITY: player input/state machine
    private EntityManager entityManager; // DUOC-ENTITY: quản lý update/render entity
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
        world = new World();
        // TODO(KIEN-WORLD): seed nên lấy từ save/game config thay vì hardcode.
        world.generate(DEFAULT_WORLD_SEED);

        physics = new PhysicsEngine();

        // ── Khởi tạo Player ─────────────────────────── DUOC-ENTITY ──
        Vector2 spawn = world.getSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;
        player = new Player(spawnX, spawnY, physics, world);

        // ── Khởi tạo EntityManager ───────────────────── DUOC-ENTITY ──
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

        BiomeMobSpawner.spawnInitialMobs(world, player, physics, entityManager, DEFAULT_WORLD_SEED);

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

        // Load Pause & Death screen textures
        pauseTexture = new Texture(Gdx.files.internal("images/stage_sprite/pause.png"));
        deathTexture = new Texture(Gdx.files.internal("images/stage_sprite/death_screen.png"));

        // Spawn camera gần mặt đất để test terrain dễ hơn.
        camera.position.set(spawnX, spawnY, 0f);
        camera.update();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            int sx = Math.max(2, (int) player.getX());
            int sy = Math.max(2, (int) player.getY());
            DemoBlockViewer.populateDemo(world, sx, sy);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            player.kill();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            player.ban();
        }

        inventoryController.update();
        if (inventoryController.wasJustClosed()) {
            inventoryInteractionHandler.onCloseInventory(inventory);
        }

        if (paused) {
            player.setMining(false, player.getX() + player.getWidth() / 2f);
            return;
        }
        // DUOC-ENTITY: update toàn bộ entity (Player input + Mob AI + sync physics)
        if (!dead) {
            entityManager.update(delta);
            droppedItemManager.update(delta, world, player, inventory);
            if (inventoryController.isInventoryOpen()) {
                inventoryInteractionHandler.update(inventory, inventoryRenderer);
            }
        }

        // Chết -> Game Over
        if (player.getHealth() <= 0) {
            dead = true;
        }

        // Handle clicks on Pause/Death screens
        if (paused || dead) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (paused) {
                if (Gdx.input.justTouched()) {
                    handlePauseClick(mx, my);
                }
            } else if (dead) {
                player.setMining(false, player.getX() + player.getWidth() / 2f);
                updateDeathButtonLayout();
                boolean hover = mx >= deathBtnX && mx <= deathBtnX + deathBtnW && my >= deathBtnY && my <= deathBtnY + deathBtnH;
                if (Gdx.input.justTouched() && hover) {
                    handleDeathClick();
                }
            }
            return;
        }

        float halfW = camera.viewportWidth * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;

        if (player != null && player.isAlive()) {
            // DUOC-ENTITY: camera follow player — clamp trong biên world
            // TODO(KIEN-WORLD): chuyển logic này sang CameraController khi có chunk system
            float targetX = player.getX() + Player.PLAYER_W / 2f;
            float targetY = player.getY() + Player.PLAYER_H / 2f;
            float followLerp = Math.min(1f, delta * 7f);
            camera.position.x += (targetX - camera.position.x) * followLerp;
            camera.position.y += (targetY - camera.position.y) * followLerp;
            camera.position.x = Math.max(halfW, Math.min(world.width - halfW, camera.position.x));
            camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));
        } else {
            // Fallback WASD khi player chết hoặc chưa có
            float cameraSpeed = 16f;
            if (Gdx.input.isKeyPressed(Input.Keys.A))
                camera.position.x -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                camera.position.x += cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.S))
                camera.position.y -= cameraSpeed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.W))
                camera.position.y += cameraSpeed * delta;
            camera.position.x = Math.max(halfW, Math.min(world.width - halfW, camera.position.x));
            camera.position.y = Math.max(halfH, Math.min(world.height - halfH, camera.position.y));
        }

        blockBreaker.update(delta, player, world, camera, viewport);
        float miningTargetX = blockBreaker.hasHoveredBlock()
            ? blockBreaker.getHoveredBlockX() + 0.5f
            : player.getX() + player.getWidth() / 2f;
        player.setMining(blockBreaker.isBreaking(), miningTargetX);
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        droppedItemManager.render(batch);
        entityManager.render(batch); // DUOC-ENTITY: mob trước, player sau (render order)
        blockBreakOverlay.render(batch, blockBreaker);
        batch.end();

        // ── HUD / debug block palette ────────────────────────────
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        if (BlockPalette.getGrass() != null) {
            batch.draw(BlockPalette.getGrass(), 0.25f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getStone() != null) {
            batch.draw(BlockPalette.getStone(), 1.35f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }
        if (BlockPalette.getBedrock() != null) {
            batch.draw(BlockPalette.getBedrock(), 2.45f, Constants.VIEWPORT_HEIGHT_TILES - 1.25f, 1f, 1f);
        }

        // ── Text HUD ─────────────────────────────────────────────────
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);

        // ── Vẽ Minecraft HUD ─────────────────────────────────────────
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Vì ảnh hotbar đã được upscaled lên 728x88, ta sẽ dùng scale = 0.5f hoặc 0.75f
        // tùy màn hình
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

        // Vẽ XP Bar (Ngay trên hotbar)
        // Lưu ý: Ảnh XP Bar chỉ có kích thước ~357 (gần 2x), ta sẽ scale nó để bằng
        // chiều rộng Hotbar
        float xpScaleX = hbW / xpBgTex.getWidth();
        float xpScaleY = xpScaleX;
        float xpBgW = xpBgTex.getWidth() * xpScaleX;
        float xpBgH = xpBgTex.getHeight() * xpScaleY;

        float xpX = hbX + (hbW - xpBgW) / 2f; // Sẽ bằng mép trái hotbar
        float xpY = hbY + hbH + (5f * scale);
        batch.draw(xpBgTex, xpX, xpY, xpBgW, xpBgH);

        // Giả lập XP đang được 50%
        float xpProgress = 0.5f;
        batch.draw(xpFgTex, xpX, xpY, xpBgW * xpProgress, xpBgH, 0, 0, (int) (xpFgTex.getWidth() * xpProgress),
                xpFgTex.getHeight(), false, false);

        // Vẽ Health Bar
        int hp = player.getHealth();
        hp = Math.max(0, Math.min(20, hp));
        Texture hpTex = healthTextures[hp];

        // Ảnh Health là 324 (4x) -> scale bình thường
        float hpScale = scale;
        float hpW = hpTex.getWidth() * hpScale;
        float hpH = hpTex.getHeight() * hpScale;
        float hpX = hbX; // Canh trái bằng mép Hotbar
        float hpY = xpY + xpBgH + (5f * scale);
        batch.draw(hpTex, hpX, hpY, hpW, hpH);

        // Vẽ Hunger Bar (Giả lập đầy 20)
        int hunger = 20;
        Texture hungerTex = hungerTextures[hunger];

        // Ảnh Hunger là 162 (2x) -> phải nhân 2 scale lên để to bằng Health (4x)
        float hgScale = scale * 2f;
        float hgW = hungerTex.getWidth() * hgScale;
        float hgH = hungerTex.getHeight() * hgScale;
        float hgX = hbX + hbW - hgW; // Canh lề phải bằng mép phải Hotbar
        float hgY = hpY; // Ngang hàng với Health
        batch.draw(hungerTex, hgX, hgY, hgW, hgH);
        font.setColor(Color.WHITE);
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "X: " + (int) player.getX() + "  Y: " + (int) player.getY(), 20,
                Gdx.graphics.getHeight() - 60);

        batch.end();

        if (paused) {
            drawPauseOverlay();
        } else if (dead) {
            drawDeathOverlay();
        }

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

        if (mx >= bx && mx <= bx + bw && my >= by1 && my <= by1 + bh) {
            paused = false;
        } else if (mx >= bx && mx <= bx + bw && my >= by2 && my <= by2 + bh) {
            game.getScreenRouter().request(ScreenId.MENU);
        }
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
        if (Gdx.files.internal(path).exists()) {
            return new Texture(Gdx.files.internal(path));
        }
        return new Texture(Gdx.files.internal(fallbackPath));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (pauseTexture != null)
            pauseTexture.dispose();
        if (deathTexture != null)
            deathTexture.dispose();
        font.dispose();
        BlockPalette.dispose();
        overlayTexture.dispose();
        overlayFont.dispose();
        if (healthTextures != null) {
            for (Texture t : healthTextures) {
                if (t != null)
                    t.dispose();
            }
        }
        if (hungerTextures != null) {
            for (Texture t : hungerTextures) {
                if (t != null)
                    t.dispose();
            }
        }
        if (hotbarTex != null)
            hotbarTex.dispose();
        if (selectorTex != null)
            selectorTex.dispose();
        if (xpBgTex != null)
            xpBgTex.dispose();
        if (xpFgTex != null)
            xpFgTex.dispose();
        if (blockBreakOverlay != null)
            blockBreakOverlay.dispose();
        if (droppedItemManager != null)
            droppedItemManager.clear();
        if (inventoryRenderer != null)
            inventoryRenderer.dispose();

        entityManager.dispose(); // DUOC-ENTITY: giải phóng tài nguyên player + mob
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.GAME;
    }
}
