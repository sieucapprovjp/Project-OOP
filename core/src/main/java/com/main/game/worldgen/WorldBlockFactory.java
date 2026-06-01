package com.main.game.worldgen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;

public final class WorldBlockFactory {

    private WorldBlockFactory() {
    }

    public static AbstractBlock create(int x, int y, String id) {
        if (id == null || "air".equals(id)) {
            return null;
        }
        TextureRegion texture = BlockRegistry.getTexture(id);
        return new SimpleBlock(x, y, id,
            BlockRegistry.isSolid(id),
            BlockRegistry.isBreakable(id),
            BlockRegistry.getHardness(id),
            texture);
    }
}
