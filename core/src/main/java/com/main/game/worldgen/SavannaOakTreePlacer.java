package com.main.game.worldgen;

import com.main.game.blocks.AbstractBlock;
import com.main.game.world.World;
import java.util.Random;

public final class SavannaOakTreePlacer {

    static final String NATURAL_OAK_LOG = "natural_wood";
    static final String DESERT_OAK_LEAVES = "desert_oak_leaves";
    static final String DESERT_OAK_LEAVES_2 = "desert_oak_leaves_2";

    private static final TreePattern[] PATTERNS = {
        new TreePattern("A", "0002000", "0225220", "2557552", "0257520", "0007000"),
        new TreePattern("B", "000050000", "002555200", "055575550", "225575522", "000070000")
    };

    private SavannaOakTreePlacer() {
    }

    public static boolean place(World world, int x, int baseY, Random random) {
        if (world == null || random == null) {
            return false;
        }
        TreePattern pattern = PATTERNS[random.nextInt(PATTERNS.length)];
        if (!canPlace(world, x, baseY, pattern)) {
            return false;
        }
        placeBlock(world, x, baseY, NATURAL_OAK_LOG);
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
                return NATURAL_OAK_LOG;
            case '1':
            case '2':
                return DESERT_OAK_LEAVES;
            case '5':
                return DESERT_OAK_LEAVES_2;
            default:
                throw new IllegalArgumentException("Unknown savanna oak tree pattern symbol: " + symbol);
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
        return NATURAL_OAK_LOG.equals(blockId)
            || CherryTreePlacer.NATURAL_CHERRY_LOG.equals(blockId)
            || SpruceTreePlacer.NATURAL_SPRUCE_LOG.equals(blockId);
    }

    private static boolean isLeafBlock(String blockId) {
        return "leaves".equals(blockId)
            || DESERT_OAK_LEAVES.equals(blockId)
            || DESERT_OAK_LEAVES_2.equals(blockId);
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
                throw new IllegalArgumentException("Savanna oak tree pattern must have rows");
            }
            this.name = name;
            this.rows = rows;
            this.width = rows[0].length();
            this.height = rows.length;
            this.centerX = width / 2;
            for (String row : rows) {
                if (row.length() != width) {
                    throw new IllegalArgumentException("Savanna oak tree pattern rows must have equal width");
                }
                for (int i = 0; i < row.length(); i++) {
                    blockIdForSymbol(row.charAt(i));
                }
            }
        }
    }
}
