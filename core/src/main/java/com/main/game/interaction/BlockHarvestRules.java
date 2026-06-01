package com.main.game.interaction;

import com.main.game.blocks.metadata.BlockRegistry;

public final class BlockHarvestRules {

    private BlockHarvestRules() {
    }

    public static boolean canDrop(String blockId, String heldItemId) {
        return BlockRegistry.canDrop(blockId, heldItemId);
    }
}
