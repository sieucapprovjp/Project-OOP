package com.main.game.worldgen;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;
import java.util.Random;

public final class CherryTreePlacer {

    static final String CHERRY_LOG = "cherry_log";
    static final String NATURAL_CHERRY_LOG = "natural_cherry_log";
    static final String CHERRY_LEAVES_5 = "cherry_leaves_5";
    static final String CHERRY_LEAVES_6 = "cherry_leaves_6";

    private static final TreePattern[] PATTERNS = {
        new TreePattern("A", "0001000", "0115110", "1557551", "0157510", "0007000"),
        new TreePattern("B", "000151000", "001555100", "015575510", "155575551", "015575510", "000070000"),
        new TreePattern("C", "0005000", "0015510", "0157550", "1557551", "0007000"),
        new TreePattern("D", "0015100", "0155510", "1557551", "0155510", "0007000", "0007000", "0007000", "0007000")
    };

    private CherryTreePlacer() {
    }

    public static boolean place(World world, int x, int baseY, Random random) {
        if (world == null || random == null) {
            return false;
        }
        TreePattern pattern = PATTERNS[random.nextInt(PATTERNS.length)];
        if (!canPlace(world, x, baseY, pattern)) {
            return false;
        }
        placeBlock(world, x, baseY, NATURAL_CHERRY_LOG);
        for (int rowIndex = 0; rowIndex < pattern.height; rowIndex++) {
            String row = pattern.rows[rowIndex];
            int y = baseY + pattern.height - rowIndex;
            for (int col = 0; col < pattern.width; col++) {
                String blockId = blockIdForSymbol(row.charAt(col));
                if (blockId == null) {
                    continue;
                }
                int tx = x + col - pattern.centerX;
                placeBlock(world, tx, y, blockId);
            }
        }
        return true;
    }

    static TreePattern[] patterns() {
        return PATTERNS.clone();
    }

    static int maxPatternHeight() {
        int max = 0;
        for (TreePattern pattern : PATTERNS) {
            max = Math.max(max, pattern.height);
        }
        return max;
    }

    static String blockIdForSymbol(char symbol) {
        switch (symbol) {
            case '0':
                return null;
            case '7':
                return NATURAL_CHERRY_LOG;
            case '1':
            case '2':
            case '3':
                return CHERRY_LEAVES_5;
            case '4':
            case '5':
            case '6':
                return CHERRY_LEAVES_6;
            default:
                throw new IllegalArgumentException("Unknown cherry tree pattern symbol: " + symbol);
        }
    }

    private static boolean canPlace(World world, int x, int baseY, TreePattern pattern) {
        if (!canReplace(world, x, baseY)) {
            return false;
        }
        for (int rowIndex = 0; rowIndex < pattern.height; rowIndex++) {
            String row = pattern.rows[rowIndex];
            int y = baseY + pattern.height - rowIndex;
            for (int col = 0; col < pattern.width; col++) {
                if (blockIdForSymbol(row.charAt(col)) == null) {
                    continue;
                }
                int tx = x + col - pattern.centerX;
                if (!canReplace(world, tx, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean canReplace(World world, int x, int y) {
        if (!world.isInBounds(x, y)) {
            return false;
        }
        AbstractBlock block = world.getBlock(x, y);
        if (block == null) {
            return true;
        }
        if (isGeneratedLog(block.getBlockId())) {
            return false;
        }
        if (!block.isSolid()) {
            return true;
        }
        return isLeafBlock(block.getBlockId());
    }

    private static boolean isGeneratedLog(String blockId) {
        return NATURAL_CHERRY_LOG.equals(blockId)
            || SavannaOakTreePlacer.NATURAL_OAK_LOG.equals(blockId)
            || SpruceTreePlacer.NATURAL_SPRUCE_LOG.equals(blockId);
    }

    private static boolean isLeafBlock(String blockId) {
        return "leaves".equals(blockId)
            || SavannaOakTreePlacer.DESERT_OAK_LEAVES.equals(blockId)
            || SavannaOakTreePlacer.DESERT_OAK_LEAVES_2.equals(blockId)
            || SpruceTreePlacer.SPRUCE_LEAVES.equals(blockId)
            || "cherry_leaves".equals(blockId)
            || "cherry_leaves_2".equals(blockId)
            || CHERRY_LEAVES_5.equals(blockId)
            || CHERRY_LEAVES_6.equals(blockId);
    }

    private static void placeBlock(World world, int x, int y, String blockId) {
        world.setBlock(x, y, WorldBlockFactory.create(x, y, blockId));
    }

    static final class TreePattern {
        final String name;
        final String[] rows;
        final int width;
        final int height;
        final int centerX;

        TreePattern(String name, String... rows) {
            if (rows == null || rows.length == 0) {
                throw new IllegalArgumentException("Cherry tree pattern must have rows");
            }
            this.name = name;
            this.rows = rows;
            this.width = rows[0].length();
            this.height = rows.length;
            this.centerX = width / 2;
            for (String row : rows) {
                if (row.length() != width) {
                    throw new IllegalArgumentException("Cherry tree pattern rows must have equal width");
                }
                for (int i = 0; i < row.length(); i++) {
                    blockIdForSymbol(row.charAt(i));
                }
            }
        }
    }
}
