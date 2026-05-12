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
import com.main.game.MainGame;
import com.main.game.entities.EntityManager;
import com.main.game.entities.Mob;
import com.main.game.entities.Player;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.utils.Constants;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
import com.main.game.world.World;

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

    private World world; // TODO(KIEN-WORLD): quản lý world/chunk/camera follow
    private PhysicsEngine physics; // TODO(LHUNG-PHYSICS): collision + resolve
    private Player player; // DUOC-ENTITY: player input/state machine
    private EntityManager entityManager; // DUOC-ENTITY: quản lý update/render entity
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
    private int selectedSlot = 0;

    public GameScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        world = new World();
        // TODO(KIEN-WORLD): seed nên lấy từ save/game config thay vì hardcode.
        world.generate(1337L);

        physics = new PhysicsEngine();

        // ── Khởi tạo Player ─────────────────────────── DUOC-ENTITY ──
        Vector2 spawn = world.getSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;
        player = new Player(spawnX, spawnY, physics, world);

        // ── Khởi tạo EntityManager ───────────────────── DUOC-ENTITY ──
        entityManager = new EntityManager();
        entityManager.setPlayer(player);

        // ── Spawn mob mẫu để test ────────────────────── DUOC-ENTITY ──
        entityManager.addMob(new Mob(spawnX + 10f, spawnY + 5f, Mob.MobType.ZOMBIE, player, physics, world));
        entityManager.addMob(new Mob(spawnX + 20f, spawnY + 5f, Mob.MobType.SKELETON, player, physics, world));

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
            healthTextures[i] = new Texture(Gdx.files.internal("mvp/ui/health/health" + i + ".png"));
            hungerTextures[i] = new Texture(Gdx.files.internal("mvp/ui/hunger/hunger_" + i + ".png"));
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
            game.getScreenRouter().request(ScreenId.GAME_OVER);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            player.ban();
        }

        // Chọn ô hotbar (phím 1-9)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            selectedSlot = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            selectedSlot = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            selectedSlot = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4))
            selectedSlot = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5))
            selectedSlot = 4;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6))
            selectedSlot = 5;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7))
            selectedSlot = 6;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8))
            selectedSlot = 7;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9))
            selectedSlot = 8;

        if (paused) {
            return;
        }
        // DUOC-ENTITY: update toàn bộ entity (Player input + Mob AI + sync physics)
        if (!dead) {
            entityManager.update(delta);
        }

        // Chết -> Game Over
        if (player.getHealth() <= 0) {
            dead = true;
        }

        // Handle clicks on Pause/Death screens
        if (paused || dead) {
            if (Gdx.input.justTouched()) {
                float mx = Gdx.input.getX();
                float my = Gdx.graphics.getHeight() - Gdx.input.getY();
                if (paused)
                    handlePauseClick(mx, my);
                else if (dead)
                    handleDeathClick(mx, my);
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
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0.4f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        entityManager.render(batch); // DUOC-ENTITY: mob trước, player sau (render order)
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

        // Vẽ Hotbar (căn giữa cạnh dưới)
        float hbX = (sw - hbW) / 2f;
        float hbY = 10f;
        batch.draw(hotbarTex, hbX, hbY, hbW, hbH);

        // Vẽ Selector
        // Kích thước chuẩn: hotbar width 182, ô mỗi slot 20, viền slot lệch 1.
        // Hotbar đã upscaled 4x -> khoảng cách mỗi ô là 80, lệch 4
        float selW = selectorTex.getWidth() * scale;
        float selH = selectorTex.getHeight() * scale;
        float slotOffset = 80f * scale;
        float selX = hbX - (4f * scale) + (selectedSlot * slotOffset);
        float selY = hbY - (4f * scale);
        batch.draw(selectorTex, selX, selY, selW, selH);

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

    private void handleDeathClick(float mx, float my) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        float bw = 220f * (sw / 640f);
        float bh = 40f * (sh / 360f);
        float bx = (sw - bw) / 2f;
        float by = sh * 0.40f;

        if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
            player.respawn(world.getSpawnPoint().x, world.getSpawnPoint().y);
            dead = false;
        }
    }

    private void drawPauseOverlay() {
        uiProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(uiProjection);

        batch.begin();
        batch.draw(pauseTexture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
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

        entityManager.dispose(); // DUOC-ENTITY: giải phóng tài nguyên player + mob
    }

    @Override
    public ScreenId getScreenId() {
        return ScreenId.GAME;
    }
}
