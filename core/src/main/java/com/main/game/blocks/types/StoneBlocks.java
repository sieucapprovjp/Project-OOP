package com.main.game.blocks.types;
import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StoneBlocks {
    public static class StoneBlock extends AbstractBlock {
        public StoneBlock(int x, int y) { super(x, y, "stone", true, true, 1.5f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("stone"); }
    }
    public static class BedrockBlock extends AbstractBlock {
        public BedrockBlock(int x, int y) { super(x, y, "bedrock", true, false, -1f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("bedrock"); }
    }
    public static class SandstoneBlock extends AbstractBlock {
        public SandstoneBlock(int x, int y) { super(x, y, "sandstone", true, true, 1.2f); }
        @Override public TextureRegion getTexture() { return com.main.game.world.BlockPalette.getSandstone(); }
    }
}
