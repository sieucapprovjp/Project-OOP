package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.main.game.MainGame;
import com.main.game.combat.PlayerAttackController;
import com.main.game.entities.EntityManager;
import com.main.game.entities.player.Player;
import com.main.game.interaction.BlockBreakOverlay;
import com.main.game.interaction.BlockBreaker;
import com.main.game.interaction.BlockPlacementController;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryController;
import com.main.game.inventory.InventoryInteractionHandler;
import com.main.game.inventory.InventoryRenderer;
import com.main.game.inventory.ItemStack;
import com.main.game.inventory.StarterInventoryFactory;
import com.main.game.items.BlockDropFactory;
import com.main.game.items.DroppedItemManager;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.ui.GameCameraController;
import com.main.game.ui.GameHudRenderer;
import com.main.game.ui.GameOverlayRenderer;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
import com.main.game.world.SpawnSafetyController;
import com.main.game.world.World;
import com.main.game.worldgen.BiomeMobSpawner;

public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM = 0.5f;

    private World world;
    private PhysicsEngine physics;
    private Player player;
    private EntityManager entityManager;
    private BlockBreaker blockBreaker;
    private BlockPlacementController blockPlacementController;
    private BlockBreakOverlay blockBreakOverlay;
    private PlayerAttackController playerAttackController;
    private DroppedItemManager droppedItemManager;
    private Inventory inventory;
    private InventoryController inventoryController;
    private InventoryRenderer inventoryRenderer;
    private InventoryInteractionHandler inventoryInteractionHandler;
    private GameCameraController cameraController;
    private GameHudRenderer hudRenderer;
    private GameOverlayRenderer overlayRenderer;
    private SpawnSafetyController spawnSafetyController;
    private boolean paused;
    private boolean dead;

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

        // Sinh toàn bộ finite world trước khi tìm spawn để cave/ore không bị lỗi seam.
        world.generate();
        camera.position.set(world.width / 2f, world.height / 2f, 0f);
        camera.update();

        Vector2 spawn = world.getInitialSpawnPoint();
        float spawnX = spawn.x;
        float spawnY = spawn.y;

        player = new Player(spawnX, spawnY, physics, world);
        spawnSafetyController = new SpawnSafetyController();
        spawnSafetyController.beginInitialSpawn(world, player);

        camera.position.set(player.getX(), player.getY(), 0f);
        camera.update();

        // ── Khởi tạo EntityManager & Tools ─────────────
        entityManager = new EntityManager();
        entityManager.setPlayer(player);
        blockBreaker = new BlockBreaker();
        blockPlacementController = new BlockPlacementController();
        blockBreakOverlay = new BlockBreakOverlay();
        playerAttackController = new PlayerAttackController();
        droppedItemManager = new DroppedItemManager();
        inventory = new Inventory();
        StarterInventoryFactory.populateStarterTools(inventory);
        inventoryController = new InventoryController();
        inventoryRenderer = new InventoryRenderer();
        inventoryInteractionHandler = new InventoryInteractionHandler();
        cameraController = new GameCameraController();
        syncHeldItem();
        blockBreaker.setBlockBreakListener((block, worldRef) ->
            droppedItemManager.spawn(BlockDropFactory.createDrop(block, worldRef), worldRef));

        // Spawner của team sử dụng seed hiện tại
        BiomeMobSpawner.spawnInitialMobs(world, player, physics, entityManager, currentSeed);

        paused = false;
        dead = false;
        camera.zoom = CAMERA_ZOOM;
        hudRenderer = new GameHudRenderer();
        overlayRenderer = new GameOverlayRenderer();
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
        syncHeldItem();

        if (paused) {
            player.setMining(false, player.getX() + player.getWidth() / 2f);
            return;
        }

        if (!dead) {
            entityManager.update(delta);
            spawnSafetyController.update(delta, world, player);
            droppedItemManager.update(delta, world, player, inventory);
            if (inventoryController.isInventoryOpen()) {
                inventoryInteractionHandler.update(inventory, inventoryRenderer);
                syncHeldItem();
            }
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

        // KIEN: Cập nhật chunk trong phạm vi map khi di chuyển
        world.update(camera);
        cameraController.update(camera, world, player, delta);

        String heldItemId = getHeldItemId();
        player.setHeldItemId(heldItemId);
        boolean placedBlock = false;
        if (blockPlacementController.update(player, world, camera, viewport, heldItemId,
            inventoryController.isInventoryOpen())) {
            player.playPlaceAnimation(blockPlacementController.getHoveredPlaceX() + 0.5f, heldItemId);
            reduceHeldBlockStack();
            blockBreaker.cancel();
            placedBlock = true;
        }
        boolean attacked = playerAttackController.update(delta, player, entityManager,
            camera, viewport, inventoryController.isInventoryOpen(), heldItemId);
        boolean brokeBlock = false;
        if (placedBlock || attacked || inventoryController.isInventoryOpen()) {
            blockBreaker.cancel();
        } else {
            brokeBlock = blockBreaker.update(delta, player, world, camera, viewport, heldItemId);
        }
        if (attacked || brokeBlock) {
            damageHeldTool();
        }
        float miningTargetX = blockBreaker.hasHoveredBlock()
            ? blockBreaker.getHoveredBlockX() + 0.5f
            : player.getX() + player.getWidth() / 2f;
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
        blockBreakOverlay.render(batch, blockBreaker, blockPlacementController);
        batch.end();

        hudRenderer.render(batch, viewport, inventory, inventoryController, inventoryRenderer,
            inventoryInteractionHandler, player);

        if (paused) overlayRenderer.renderPause(batch);
        else if (dead) overlayRenderer.renderDeath(batch);
        overlayRenderer.renderBrightness(batch, game.getGameState());
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
        spawnSafetyController.respawn(world, player);
        dead = false;
    }

    private String getHeldItemId() {
        if (inventory == null || inventoryController == null) {
            return null;
        }
        ItemStack stack = inventory.getSlot(inventoryController.getSelectedHotbarSlot());
        return stack == null || stack.getCount() <= 0 ? null : stack.getItemId();
    }

    private void syncHeldItem() {
        if (player != null) {
            player.setHeldItemId(getHeldItemId());
        }
    }

    private void damageHeldTool() {
        if (inventory == null || inventoryController == null) {
            return;
        }
        int slot = inventoryController.getSelectedHotbarSlot();
        ItemStack stack = inventory.getSlot(slot);
        if (stack == null || !stack.hasDurability()) {
            return;
        }
        if (stack.damage(1)) {
            inventory.setSlot(slot, null);
        }
        syncHeldItem();
    }

    private void reduceHeldBlockStack() {
        if (inventory == null || inventoryController == null) {
            return;
        }
        int slot = inventoryController.getSelectedHotbarSlot();
        ItemStack stack = inventory.getSlot(slot);
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        stack.subtract(1);
        if (stack.getCount() <= 0) {
            inventory.setSlot(slot, null);
        }
        syncHeldItem();
    }

    private void updateDeathButtonLayout() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        deathBtnW = sw * 0.45f;
        deathBtnH = sh * 0.12f;
        deathBtnX = (sw - deathBtnW) / 2f;
        deathBtnY = sh * 0.38f;
    }

    @Override
    public void dispose() {
        super.dispose();
        BlockPalette.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
        if (overlayRenderer != null) overlayRenderer.dispose();
        if (blockBreakOverlay != null) blockBreakOverlay.dispose();
        if (droppedItemManager != null) droppedItemManager.clear();
        if (inventoryRenderer != null) inventoryRenderer.dispose();
        entityManager.dispose();
    }

    @Override
    public ScreenId getScreenId() { return ScreenId.GAME; }
}
