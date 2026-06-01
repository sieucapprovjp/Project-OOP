package com.main.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.main.game.MainGame;
import com.main.game.blocks.AbstractBlock;
import com.main.game.combat.PlayerAttackController;
import com.main.game.crafting.CraftingController;
import com.main.game.entities.EntityManager;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.chest.ChestInteractionController;
import com.main.game.utilityblock.chest.ChestInteractionHandler;
import com.main.game.utilityblock.chest.ChestManager;
import com.main.game.utilityblock.chest.ChestRenderer;
import com.main.game.utilityblock.chest.ChestState;
import com.main.game.utilityblock.furnace.FurnaceInteractionController;
import com.main.game.utilityblock.furnace.FurnaceInteractionHandler;
import com.main.game.utilityblock.furnace.FurnaceManager;
import com.main.game.utilityblock.furnace.FurnaceRenderer;
import com.main.game.utilityblock.furnace.FurnaceState;
import com.main.game.interaction.BlockBreakOverlay;
import com.main.game.interaction.BlockBreaker;
import com.main.game.interaction.BlockPlacementController;
import com.main.game.utilityblock.craftingtable.CraftingTableInteractionController;
import com.main.game.inventory.Inventory;
import com.main.game.inventory.InventoryController;
import com.main.game.inventory.InventoryInteractionHandler;
import com.main.game.inventory.InventoryRenderer;
import com.main.game.inventory.ItemStack;
import com.main.game.inventory.StarterInventoryKit;
import com.main.game.inventory.ToolRegistry;
import com.main.game.items.BlockDropFactory;
import com.main.game.items.DroppedItemManager;
import com.main.game.items.HarvestEntry;
import com.main.game.items.MobDropFactory;
import com.main.game.navigation.ScreenId;
import com.main.game.physics.PhysicsEngine;
import com.main.game.time.DayNightCycle;
import com.main.game.ui.GameCameraController;
import com.main.game.ui.GameHudRenderer;
import com.main.game.ui.GameOverlayRenderer;
import com.main.game.world.BlockPalette;
import com.main.game.world.DemoBlockViewer;
import com.main.game.world.SpawnSafetyController;
import com.main.game.world.World;
import com.main.game.entities.mob.Mob;
import com.main.game.worldgen.BiomeMobSpawner;
import java.util.Random;

public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM = 0.5f;

    private World world;
    private PhysicsEngine physics;
    private Player player;
    private EntityManager entityManager;
    private BlockBreaker blockBreaker;
    private BlockPlacementController blockPlacementController;
    private CraftingTableInteractionController craftingTableInteractionController;
    private ChestInteractionController chestInteractionController;
    private FurnaceInteractionController furnaceInteractionController;
    private BlockBreakOverlay blockBreakOverlay;
    private PlayerAttackController playerAttackController;
    private DroppedItemManager droppedItemManager;
    private Inventory inventory;
    private InventoryController inventoryController;
    private InventoryRenderer inventoryRenderer;
    private InventoryInteractionHandler inventoryInteractionHandler;
    private ChestRenderer chestRenderer;
    private ChestInteractionHandler chestInteractionHandler;
    private ChestManager chestManager;
    private ChestState openChestState;
    private FurnaceRenderer furnaceRenderer;
    private FurnaceInteractionHandler furnaceInteractionHandler;
    private FurnaceManager furnaceManager;
    private FurnaceState openFurnaceState;
    private CraftingController craftingController;
    private GameCameraController cameraController;
    private GameHudRenderer hudRenderer;
    private GameOverlayRenderer overlayRenderer;
    private SpawnSafetyController spawnSafetyController;
    private BiomeMobSpawner mobSpawner;
    private DayNightCycle dayNightCycle;
    private Random mobDropRandom;
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
        craftingTableInteractionController = new CraftingTableInteractionController();
        chestInteractionController = new ChestInteractionController();
        furnaceInteractionController = new FurnaceInteractionController();
        blockBreakOverlay = new BlockBreakOverlay();
        playerAttackController = new PlayerAttackController();
        playerAttackController.setMobDeathListener(this::handleMobKilled);
        droppedItemManager = new DroppedItemManager();
        mobDropRandom = new Random(currentSeed + 7717L);
        inventory = new Inventory();
        StarterInventoryKit.grant(inventory);
        player.setArmorLoadout(inventory.getArmorLoadout());
        inventoryController = new InventoryController();
        inventoryRenderer = new InventoryRenderer();
        inventoryInteractionHandler = new InventoryInteractionHandler();
        chestRenderer = new ChestRenderer();
        chestInteractionHandler = new ChestInteractionHandler();
        chestManager = new ChestManager();
        furnaceRenderer = new FurnaceRenderer();
        furnaceInteractionHandler = new FurnaceInteractionHandler();
        furnaceManager = new FurnaceManager();
        craftingController = new CraftingController();
        cameraController = new GameCameraController();
        syncHeldItem();
        blockBreaker.setBlockBreakListener(this::handleBlockBroken);

        dayNightCycle = new DayNightCycle();
        mobSpawner = new BiomeMobSpawner(currentSeed);
        mobSpawner.spawnInitial(world, player, physics, entityManager, dayNightCycle.isNight());

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

        boolean inventoryKeyPressed = inventoryController.update();
        if (inventoryKeyPressed) handleInventoryKey();
        if (inventoryController.wasJustClosed()) handleInventoryClosed();
        syncHeldItem();

        if (paused) {
            player.setMining(false, player.getX() + player.getWidth() / 2f);
            return;
        }

        if (!dead) {
            if (dayNightCycle != null) {
                dayNightCycle.update(delta);
            }
            furnaceManager.update(delta);
            entityManager.update(delta);
            if (mobSpawner != null) {
                mobSpawner.update(delta, world, player, physics, entityManager,
                    dayNightCycle == null || dayNightCycle.isNight());
            }
            spawnSafetyController.update(delta, world, player);
            droppedItemManager.update(delta, world, player, inventory);
            if (inventoryController.isInventoryOpen()) {
                if (openChestState != null) {
                    chestInteractionHandler.update(inventory, openChestState, chestRenderer);
                } else if (openFurnaceState != null) {
                    furnaceInteractionHandler.update(inventory, openFurnaceState, furnaceRenderer);
                } else {
                    inventoryInteractionHandler.update(inventory, inventoryRenderer, craftingController);
                }
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
        boolean consumedFood = tryConsumeHeldFood(heldItemId);
        boolean placedBlock = false;
        if (!consumedFood && blockPlacementController.update(player, world, camera, viewport, heldItemId,
            inventoryController.isInventoryOpen())) {
            player.playPlaceAnimation(blockPlacementController.getHoveredPlaceX() + 0.5f, heldItemId);
            reduceHeldStack();
            blockBreaker.cancel();
            placedBlock = true;
        }
        boolean attacked = playerAttackController.update(delta, player, entityManager,
            camera, viewport, inventoryController.isInventoryOpen(), heldItemId);
        if (shouldPlaySwordSlash(heldItemId)) {
            player.playAttackAnimation(mouseWorldX(), heldItemId);
        }
        boolean brokeBlock = false;
        if (consumedFood || placedBlock || attacked || inventoryController.isInventoryOpen()) {
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
        int globalLight = dayNightCycle == null ? 0 : dayNightCycle.getGlobalLight();
        float nightFactor = dayNightCycle == null ? 0f : dayNightCycle.getNightFactor();
        r = lerp(r, 0.015f, nightFactor * 0.9f);
        g = lerp(g, 0.025f, nightFactor * 0.9f);
        b = lerp(b, 0.08f, nightFactor * 0.9f);

        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.render(batch, camera);
        furnaceManager.render(batch, world, camera);
        droppedItemManager.render(batch);
        entityManager.render(batch);
        blockBreakOverlay.render(batch, blockBreaker, blockPlacementController);
        batch.end();

        overlayRenderer.renderWorldDarkness(batch, globalLight);

        hudRenderer.render(batch, viewport, inventory, inventoryController, inventoryRenderer,
            inventoryInteractionHandler, craftingController, furnaceRenderer, furnaceInteractionHandler,
            openFurnaceState, chestRenderer, chestInteractionHandler, openChestState, player);

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

    private void handleInventoryKey() {
        if (inventoryController.isInventoryOpen()) {
            inventoryController.close();
            return;
        }
        if (paused || dead) {
            return;
        }
        if (chestInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.closeCrafting(inventory);
            openFurnaceState = null;
            openChestState = chestManager.getOrCreate(world,
                chestInteractionController.getHoveredTileX(),
                chestInteractionController.getHoveredTileY());
            inventoryController.open();
            return;
        }
        openChestState = null;
        if (furnaceInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.closeCrafting(inventory);
            openChestState = null;
            openFurnaceState = furnaceManager.getOrCreate(world,
                furnaceInteractionController.getHoveredTileX(),
                furnaceInteractionController.getHoveredTileY());
            inventoryController.open();
            return;
        }
        openFurnaceState = null;
        if (craftingTableInteractionController.canOpen(player, world, camera, viewport)) {
            craftingController.openTableCrafting(inventory);
        } else {
            craftingController.openPlayerCrafting(inventory);
        }
        inventoryController.open();
    }

    private void handleInventoryClosed() {
        if (openFurnaceState != null) {
            furnaceInteractionHandler.onCloseInventory(inventory);
            openFurnaceState = null;
            return;
        }
        if (openChestState != null) {
            chestInteractionHandler.onCloseInventory(inventory);
            openChestState = null;
            return;
        }
        inventoryInteractionHandler.onCloseInventory(inventory, craftingController);
    }

    private void handleBlockBroken(AbstractBlock block, World worldRef) {
        if (block != null && "furnace".equals(block.getBlockId())) {
            furnaceManager.dropContents(block, worldRef, droppedItemManager);
        }
        if (block != null && "chest".equals(block.getBlockId())) {
            chestManager.dropContents(block, worldRef, droppedItemManager);
        }
        droppedItemManager.spawn(BlockDropFactory.createDrop(block, worldRef, getHeldItemId()), worldRef);
    }

    private void handleMobKilled(Mob mob) {
        if (mob == null || world == null || droppedItemManager == null || mobDropRandom == null) {
            return;
        }
        for (HarvestEntry entry : MobDropFactory.createDrops(mob, world, mobDropRandom)) {
            droppedItemManager.spawn(entry, world);
        }
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

    private boolean shouldPlaySwordSlash(String heldItemId) {
        return inventoryController != null
            && !inventoryController.isInventoryOpen()
            && ToolRegistry.isSword(heldItemId)
            && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    private float mouseWorldX() {
        Vector2 mouseWorld = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);
        return mouseWorld.x;
    }

    private boolean tryConsumeHeldFood(String heldItemId) {
        if (player == null || inventory == null || inventoryController == null
            || inventoryController.isInventoryOpen()
            || !Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)
            || !player.eat(heldItemId)) {
            return false;
        }
        reduceHeldStack();
        return true;
    }

    private void reduceHeldStack() {
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

    private float lerp(float from, float to, float progress) {
        float t = Math.max(0f, Math.min(1f, progress));
        return from + (to - from) * t;
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
        if (chestRenderer != null) chestRenderer.dispose();
        if (chestManager != null) chestManager.clear();
        if (furnaceRenderer != null) furnaceRenderer.dispose();
        if (furnaceManager != null) furnaceManager.clear();
        entityManager.dispose();
    }

    @Override
    public ScreenId getScreenId() { return ScreenId.GAME; }
}
