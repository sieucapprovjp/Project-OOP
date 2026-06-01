package com.main.game.blocks.types;
import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NatureBlocks {
    public static class DirtBlock extends AbstractBlock {
        public DirtBlock(int x, int y) { super(x, y, "dirt", true, true, 0.6f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("dirt"); }
    }
    public static class GrassBlockBlock extends AbstractBlock {
        public GrassBlockBlock(int x, int y) { super(x, y, "grass", true, true, 0.6f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("grass"); }
    }
    public static class SandBlock extends AbstractBlock {
        public SandBlock(int x, int y) { super(x, y, "sand", true, true, 0.6f); }
        @Override public TextureRegion getTexture() { return TextureManager.getInstance().getTexture("sand"); }
    }
    public static class SnowBlock extends AbstractBlock {
        public SnowBlock(int x, int y) { super(x, y, "snow", true, true, 0.2f); }
        @Override public TextureRegion getTexture() { return com.main.game.world.BlockPalette.getSnow(); }
    }
    public static class IceBlock extends AbstractBlock {
        public IceBlock(int x, int y) { super(x, y, "ice", true, true, 0.4f); }
        @Override public TextureRegion getTexture() { return com.main.game.world.BlockPalette.getIce(); }
    }
    public static class CactusBlock extends AbstractBlock {
        public CactusBlock(int x, int y) { super(x, y, "cactus", true, true, 0.9f); }
        @Override public TextureRegion getTexture() { return com.main.game.world.BlockPalette.getCactus(); }
    }
}
