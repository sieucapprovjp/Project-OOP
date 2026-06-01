package com.main.game.world;

import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.blocks.types.NatureBlocks;
import com.main.game.blocks.types.StoneBlocks;
import com.main.game.blocks.types.UtilityBlocks;
import com.main.game.blocks.types.WoodBlocks;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

/*
 * Utility to populate a small demo grid in the world so you can visually inspect block types.
 *
 * Usage: call DemoBlockViewer.populateDemo(world, startX, startY) from GameScreen (for example on a key press).
 */
public final class DemoBlockViewer {

    private DemoBlockViewer() {}

    public interface BlockFactory {
        AbstractBlock create(int x, int y);
    }

    private static List<BlockFactory> makeFactories() {
        List<BlockFactory> f = new ArrayList<>();
        // Nature
        f.add((x,y) -> new NatureBlocks.DirtBlock(x,y));
        f.add((x,y) -> new NatureBlocks.GrassBlockBlock(x,y));
        f.add((x,y) -> new NatureBlocks.SandBlock(x,y));
        f.add((x,y) -> new NatureBlocks.SnowBlock(x,y));
        f.add((x,y) -> new NatureBlocks.IceBlock(x,y));
        f.add((x,y) -> new NatureBlocks.CactusBlock(x,y));

        // Stone / base
        f.add((x,y) -> new StoneBlocks.StoneBlock(x,y));
        f.add((x,y) -> new StoneBlocks.BedrockBlock(x,y));
        f.add((x,y) -> new StoneBlocks.SandstoneBlock(x,y));

        // Wood
        f.add((x,y) -> new WoodBlocks.OakLogBlock(x,y));
        f.add((x,y) -> new WoodBlocks.OakPlanksBlock(x,y));
        f.add((x,y) -> new WoodBlocks.OakLeavesBlock(x,y));

        // Cave natural
        addTextured(f, "deepslate", "tiles/cave/natural/deepslate", true, true, 1.8f);
        addTextured(f, "cobbled_deepslate", "tiles/cave/natural/cobbled_deepslate", true, true, 1.8f);
        addTextured(f, "chiseled_deepslate", "tiles/cave/natural/chiseled_deepslate", true, true, 1.8f);
        addTextured(f, "cracked_deepslate", "tiles/cave/natural/cracked_deepslate", true, true, 1.8f);
        addTextured(f, "cracked_deepslate2", "tiles/cave/natural/cracked_deepslate2", true, true, 1.8f);
        addTextured(f, "deepslate_polished", "tiles/cave/natural/deepslate_polished", true, true, 1.8f);
        addTextured(f, "reinforced_deepslate", "tiles/cave/natural/reinforced_deepslate", true, false, 999f);

        // Cave bricks, tiles, slabs
        addTextured(f, "deepslate_bricks", "tiles/cave/bricks/deepslate_bricks", true, true, 1.8f);
        addTextured(f, "deepslate_bricks2", "tiles/cave/bricks/deepslate_bricks2", true, true, 1.8f);
        addTextured(f, "deepslate_bricks3", "tiles/cave/bricks/deepslate_bricks3", true, true, 1.8f);
        addTextured(f, "deepslate_bricks4", "tiles/cave/bricks/deepslate_bricks4", true, true, 1.8f);
        addTextured(f, "deepslate_tiles", "tiles/cave/tiles/deepslate_tiles", true, true, 1.8f);
        addTextured(f, "deepslate_slab", "tiles/cave/slabs/deepslate_slab", true, true, 1.8f);
        addTextured(f, "polished_deepslate_slab", "tiles/cave/slabs/polished_deepslate_slab", true, true, 1.8f);
        addTextured(f, "deepslate_tiles_slab", "tiles/cave/slabs/deepslate_tiles_slab", true, true, 1.8f);
        addTextured(f, "deepslate_brick_slab", "tiles/cave/slabs/deepslate_brick_slab", true, true, 1.8f);

        // Cave ores
        addTextured(f, "coal_ore", "tiles/cave/Ores/coal_ore", true, true, 3f);
        addTextured(f, "iron_ore", "tiles/cave/Ores/iron_ore", true, true, 3f);
        addTextured(f, "gold_ore", "tiles/cave/Ores/gold_ore", true, true, 3f);
        addTextured(f, "ngold_ore", "tiles/cave/Ores/ngold_ore", true, true, 3f);
        addTextured(f, "diamond_ore", "tiles/cave/Ores/diamond_ore", true, true, 5f);
        addTextured(f, "copper_ore", "tiles/cave/Ores/copper_ore", true, true, 3f);
        addTextured(f, "lapis_ore", "tiles/cave/Ores/lapis_ore", true, true, 3f);
        addTextured(f, "redstone_ore", "tiles/cave/Ores/redstone_ore", true, true, 3f);
        addTextured(f, "emerald_ore", "tiles/cave/Ores/emerald_ore", true, true, 3f);
        addTextured(f, "quartz_ore", "tiles/cave/Ores/quartz_ore", true, true, 2f);

        // Deepslate ores
        addTextured(f, "deepslate_co", "tiles/cave/ores_deepslate/deepslate_co", true, true, 3f);
        addTextured(f, "deepslate_io", "tiles/cave/ores_deepslate/deepslate_io", true, true, 3f);
        addTextured(f, "deepslate_go", "tiles/cave/ores_deepslate/deepslate_go", true, true, 3f);
        addTextured(f, "deepslate_do", "tiles/cave/ores_deepslate/deepslate_do", true, true, 5f);
        addTextured(f, "deepslate_copper", "tiles/cave/ores_deepslate/deepslate_copper", true, true, 3f);
        addTextured(f, "ore_lapis_deepslate", "tiles/cave/ores_deepslate/ore_lapis_deepslate", true, true, 3f);
        addTextured(f, "deepslate_ro", "tiles/cave/ores_deepslate/deepslate_ro", true, true, 3f);
        addTextured(f, "deepslate_eo", "tiles/cave/ores_deepslate/deepslate_eo", true, true, 3f);

        // Utility blocks with no visible texture are kept last.
        f.add((x,y) -> new UtilityBlocks.AirBlock(x,y));
        f.add((x,y) -> new UtilityBlocks.WaterBlock(x,y));

        return f;
    }

    private static void addTextured(List<BlockFactory> factories, String id, String textureName,
                                    boolean solid, boolean breakable, float hardness) {
        factories.add((x, y) -> {
            TextureRegion texture = TextureManager.getInstance().getTexture(textureName);
            if (texture == null) {
                texture = BlockPalette.getStone();
            }
            return new SimpleBlock(x, y, id, solid, breakable, hardness, texture);
        });
    }

    /** Populate a compact grid of blocks starting at (startX, startY). */
    public static void populateDemo(World world, int startX, int startY) {
        List<BlockFactory> factories = makeFactories();
        int cols = 10;
        for (int i = 0; i < factories.size(); i++) {
            int cx = i % cols;
            int cy = i / cols;
            int tx = startX + cx;
            int ty = startY + cy;
            if (!world.isInBounds(tx, ty)) continue;
            AbstractBlock b = factories.get(i).create(tx, ty);
            world.setBlock(tx, ty, b);
        }
    }
}
