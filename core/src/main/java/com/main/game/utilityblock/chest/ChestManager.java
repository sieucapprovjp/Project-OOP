package com.main.game.utilityblock.chest;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.main.game.blocks.AbstractBlock;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;
import com.main.game.items.DroppedItemManager;
import com.main.game.items.HarvestEntry;
import com.main.game.world.World;

import java.util.HashMap;
import java.util.Map;

public class ChestManager {

    private final Map<Integer, ChestState> chests = new HashMap<>();

    public ChestState getOrCreate(World world, int tileX, int tileY) {
        int key = toKey(world, tileX, tileY);
        return chests.computeIfAbsent(key, ignored -> new ChestState());
    }

    public void dropContents(AbstractBlock block, World world, DroppedItemManager droppedItemManager) {
        if (block == null || world == null || droppedItemManager == null) {
            return;
        }
        ChestState chest = chests.remove(toKey(world, block.getTileX(), block.getTileY()));
        if (chest == null) {
            return;
        }
        for (int i = 0; i < ChestState.SLOT_COUNT; i++) {
            spawnStack(chest.getSlot(i), block, world, droppedItemManager);
        }
    }

    public void clear() {
        chests.clear();
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
