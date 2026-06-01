package com.main.game.utilityblock.furnace;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.blocks.AbstractBlock;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;
import com.main.game.items.DroppedItemManager;
import com.main.game.items.HarvestEntry;
import com.main.game.utils.TextureManager;
import com.main.game.world.World;

import java.util.HashMap;
import java.util.Map;

public class FurnaceManager {

    private static final String FURNACE_ID = "furnace";
    private static final float COOK_SECONDS = 10f;

    private final Map<Integer, FurnaceState> furnaces = new HashMap<>();
    private final TextureRegion litTexture;

    public FurnaceManager() {
        litTexture = TextureManager.getInstance().getTexture("furnace_lit");
    }

    public FurnaceState getOrCreate(World world, int tileX, int tileY) {
        int key = toKey(world, tileX, tileY);
        return furnaces.computeIfAbsent(key, ignored -> new FurnaceState());
    }

    public void update(float delta) {
        if (delta <= 0f) {
            return;
        }
        for (FurnaceState furnace : furnaces.values()) {
            updateFurnace(furnace, delta);
        }
    }

    public void render(SpriteBatch batch, World world, OrthographicCamera camera) {
        if (batch == null || world == null || litTexture == null) {
            return;
        }
        for (Map.Entry<Integer, FurnaceState> entry : furnaces.entrySet()) {
            FurnaceState furnace = entry.getValue();
            if (furnace == null || !furnace.isBurning()) {
                continue;
            }
            int key = entry.getKey();
            int tileX = (key - 1) % world.width;
            int tileY = (key - 1) / world.width;
            AbstractBlock block = world.getBlock(tileX, tileY);
            if (block != null && FURNACE_ID.equals(block.getBlockId())) {
                batch.draw(litTexture, tileX, tileY, 1f, 1f);
            }
        }
    }

    public void dropContents(AbstractBlock block, World world, DroppedItemManager droppedItemManager) {
        if (block == null || world == null || droppedItemManager == null) {
            return;
        }
        FurnaceState furnace = furnaces.remove(toKey(world, block.getTileX(), block.getTileY()));
        if (furnace == null) {
            return;
        }
        spawnStack(furnace.getInput(), block, world, droppedItemManager);
        spawnStack(furnace.getFuel(), block, world, droppedItemManager);
        spawnStack(furnace.getOutput(), block, world, droppedItemManager);
    }

    public void clear() {
        furnaces.clear();
    }

    private void updateFurnace(FurnaceState furnace, float delta) {
        boolean canSmelt = canSmelt(furnace);
        if (!canSmelt) {
            furnace.setCookProgress(0f);
            furnace.consumeBurn(delta);
            return;
        }

        if (!furnace.isBurning() && !consumeFuel(furnace)) {
            furnace.setCookProgress(0f);
            return;
        }

        float burnDelta = Math.min(delta, furnace.getBurnRemaining());
        furnace.consumeBurn(delta);
        furnace.setCookProgress(furnace.getCookProgress() + burnDelta);
        if (furnace.getCookProgress() >= COOK_SECONDS) {
            finishSmelt(furnace);
            furnace.setCookProgress(canSmelt(furnace) ? furnace.getCookProgress() - COOK_SECONDS : 0f);
        }
    }

    private boolean canSmelt(FurnaceState furnace) {
        ItemStack input = furnace.getInput();
        if (input == null || input.getCount() <= 0) {
            return false;
        }
        String outputItemId = SmeltingRecipeRegistry.getOutput(input.getItemId());
        if (outputItemId == null) {
            return false;
        }
        ItemStack output = furnace.getOutput();
        if (output == null || output.getCount() <= 0) {
            return true;
        }
        return outputItemId.equals(output.getItemId())
            && output.getCount() < ItemRegistry.getMaxStack(outputItemId);
    }

    private boolean consumeFuel(FurnaceState furnace) {
        ItemStack fuel = furnace.getFuel();
        if (fuel == null || fuel.getCount() <= 0) {
            return false;
        }
        float burnSeconds = FuelRegistry.getBurnSeconds(fuel.getItemId());
        if (burnSeconds <= 0f) {
            return false;
        }
        fuel.subtract(1);
        if (fuel.getCount() <= 0) {
            furnace.setFuel(null);
        }
        furnace.setBurn(burnSeconds);
        return true;
    }

    private void finishSmelt(FurnaceState furnace) {
        ItemStack input = furnace.getInput();
        if (input == null || input.getCount() <= 0) {
            return;
        }
        String outputItemId = SmeltingRecipeRegistry.getOutput(input.getItemId());
        if (outputItemId == null) {
            return;
        }

        input.subtract(1);
        if (input.getCount() <= 0) {
            furnace.setInput(null);
        }

        ItemStack output = furnace.getOutput();
        if (output == null || output.getCount() <= 0) {
            furnace.setOutput(new ItemStack(outputItemId, 1));
        } else {
            output.add(1);
        }
    }

    private void spawnStack(ItemStack stack, AbstractBlock block, World world, DroppedItemManager droppedItemManager) {
        if (stack == null || stack.getCount() <= 0) {
            return;
        }
        TextureRegion texture = ItemRegistry.getTexture(stack.getItemId());
        HarvestEntry entry = new HarvestEntry(
            HarvestEntry.toTileIdx(block.getTileX(), block.getTileY(), world),
            stack.getItemId(),
            texture,
            stack.getCount(),
            MathUtils.random(-0.1f, 0.1f),
            HarvestEntry.RANDOM_VERTICAL_SPEED
        );
        droppedItemManager.spawn(entry, world);
    }

    private int toKey(World world, int tileX, int tileY) {
        return HarvestEntry.toTileIdx(tileX, tileY, world);
    }
}
