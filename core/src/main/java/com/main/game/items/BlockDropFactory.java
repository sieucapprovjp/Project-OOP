package com.main.game.items;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.inventory.ItemRegistry;
import com.main.game.world.World;

public final class BlockDropFactory {

    private static final float OAK_LEAF_APPLE_DROP_CHANCE = 0.05f;

    private BlockDropFactory() {
    }

    public static HarvestEntry createDrop(AbstractBlock block, World world) {
        return createDrop(block, world, null);
    }

    public static HarvestEntry createDrop(AbstractBlock block, World world, String heldItemId) {
        if (block == null) {
            return null;
        }

        String blockId = block.getBlockId();
        if (!BlockRegistry.canDrop(blockId, heldItemId)) {
            return null;
        }

        int tileIdx = HarvestEntry.toTileIdx(block.getTileX(), block.getTileY(), world);
        String itemId = dropItemIdForBlock(blockId, MathUtils.random());
        if (itemId == null) {
            return null;
        }
        TextureRegion texture = ItemRegistry.getTexture(itemId);
        if (texture == null && !BlockRegistry.isOre(blockId)) {
            texture = block.getTexture();
        }
        return new HarvestEntry(
            tileIdx,
            itemId,
            texture,
            1,
            MathUtils.random(-0.1f, 0.1f),
            HarvestEntry.RANDOM_VERTICAL_SPEED
        );
    }

    static String dropItemIdForBlock(String blockId, float appleRoll) {
        if ("apple_in_tree".equals(blockId)) {
            return "apple";
        }
        if (isOakLeafBlock(blockId) && appleRoll < OAK_LEAF_APPLE_DROP_CHANCE) {
            return "apple";
        }
        return BlockRegistry.getDropItemId(blockId);
    }

    private static boolean isOakLeafBlock(String blockId) {
        return "leaves".equals(blockId)
            || "desert_oak_leaves".equals(blockId)
            || "desert_oak_leaves_2".equals(blockId);
    }
}
