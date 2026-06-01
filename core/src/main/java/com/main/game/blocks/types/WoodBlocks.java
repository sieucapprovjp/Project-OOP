package com.main.game.blocks.types; // Kiểm tra kỹ dòng này

import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WoodBlocks {
    public static class OakLogBlock extends AbstractBlock {
        public OakLogBlock(int x, int y) { super(x, y, "wood", true, true, 0.9f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("wood"); }
    }

    public static class OakPlanksBlock extends AbstractBlock {
        public OakPlanksBlock(int x, int y) { super(x, y, "planks", true, true, 0.9f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("planks"); }
    }

    public static class OakLeavesBlock extends AbstractBlock {
        public OakLeavesBlock(int x, int y) { super(x, y, "leaves", false, true, 0.2f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("leaves"); }
    }
}
