package com.main.game.worldgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.main.game.blocks.metadata.BlockRegistry;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class CherryTreePlacerTest {

    @Test
    public void patternsKeepExpectedDimensionsAndCenters() {
        CherryTreePlacer.TreePattern[] patterns = CherryTreePlacer.patterns();

        assertEquals(4, patterns.length);
        assertPattern(patterns[0], "A", 7, 5, 3);
        assertPattern(patterns[1], "B", 9, 6, 4);
        assertPattern(patterns[2], "C", 7, 5, 3);
        assertPattern(patterns[3], "D", 7, 8, 3);
        assertEquals(8, CherryTreePlacer.maxPatternHeight());
    }

    @Test
    public void patternSymbolsMapToRegisteredBlockIds() {
        assertNull(CherryTreePlacer.blockIdForSymbol('0'));

        for (CherryTreePlacer.TreePattern pattern : CherryTreePlacer.patterns()) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    String blockId = CherryTreePlacer.blockIdForSymbol(row.charAt(i));
                    if (blockId != null) {
                        assertNotNull("Missing registry entry for " + blockId, BlockRegistry.get(blockId));
                    }
                }
            }
        }
    }

    @Test
    public void treePatternsUseOnlyCherryLogsAndLeafVariants() {
        Set<String> blockIds = new HashSet<>();
        for (CherryTreePlacer.TreePattern pattern : CherryTreePlacer.patterns()) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    String blockId = CherryTreePlacer.blockIdForSymbol(row.charAt(i));
                    if (blockId != null) {
                        blockIds.add(blockId);
                    }
                }
            }
        }

        assertTrue(blockIds.contains("natural_cherry_log"));
        assertTrue(blockIds.contains("cherry_leaves_5"));
        assertTrue(blockIds.contains("cherry_leaves_6"));
        assertEquals(3, blockIds.size());
    }

    @Test
    public void treePatternLogsStayOnCenterColumn() {
        for (CherryTreePlacer.TreePattern pattern : CherryTreePlacer.patterns()) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    if (CherryTreePlacer.NATURAL_CHERRY_LOG.equals(CherryTreePlacer.blockIdForSymbol(row.charAt(i)))) {
                        assertEquals("Cherry logs should not form horizontal branches", pattern.centerX, i);
                    }
                }
            }
        }
    }

    @Test
    public void legacyLogLeavesSymbolMapsToLeafVariant() {
        assertEquals("cherry_leaves_6", CherryTreePlacer.blockIdForSymbol('6'));
    }

    @Test
    public void treeLogSymbolUsesPassThroughNaturalLog() {
        assertEquals("natural_cherry_log", CherryTreePlacer.blockIdForSymbol('7'));
    }

    @Test
    public void cherryLeavesAndGroundDecorationsAreNotSolid() {
        assertTrue("Cherry log should remain solid", BlockRegistry.isSolid("cherry_log"));
        assertFalse("Generated cherry logs should be pass-through", BlockRegistry.isSolid("natural_cherry_log"));
        assertFalse(BlockRegistry.isPlaceable("natural_cherry_log"));
        assertEquals("cherry_log", BlockRegistry.getDropItemId("natural_cherry_log"));
        assertFalse(BlockRegistry.isSolid("cherry_leaves_5"));
        assertFalse(BlockRegistry.isSolid("cherry_leaves_6"));
        assertFalse(BlockRegistry.isSolid("cherry_grass"));
        assertFalse(BlockRegistry.isSolid("cherry_flower"));
        assertFalse(BlockRegistry.isSolid("cherry_sapling"));
    }

    @Test
    public void cherryGroundDecorationUsesExpectedDensityBuckets() {
        assertEquals("cherry_flower", WorldGenerator.cherryGroundDecorationForNoise(0.00f));
        assertEquals("cherry_flower", WorldGenerator.cherryGroundDecorationForNoise(0.179f));
        assertEquals("cherry_grass", WorldGenerator.cherryGroundDecorationForNoise(0.18f));
        assertEquals("cherry_grass", WorldGenerator.cherryGroundDecorationForNoise(0.379f));
        assertNull(WorldGenerator.cherryGroundDecorationForNoise(0.38f));
    }

    @Test
    public void cherryBiomeKeepsSolidGrassSurface() {
        assertEquals("grass", BiomeProfile.forType(BiomeType.CHERRY).surfaceBlock);
        assertTrue(BlockRegistry.isSolid(BiomeProfile.forType(BiomeType.CHERRY).surfaceBlock));
    }

    @Test
    public void cherryBiomeOccupiesExpandedNoiseRange() {
        assertEquals(BiomeType.PLAINS, WorldGenerator.chooseBiomeForNoise(-0.31f));
        assertEquals(BiomeType.FOREST, WorldGenerator.chooseBiomeForNoise(-0.119f));
        assertEquals(BiomeType.CHERRY, WorldGenerator.chooseBiomeForNoise(0.081f));
        assertEquals(BiomeType.CHERRY, WorldGenerator.chooseBiomeForNoise(0.31f));
        assertEquals(BiomeType.SNOW, WorldGenerator.chooseBiomeForNoise(0.321f));
    }

    @Test
    public void unknownPatternSymbolsAreRejected() {
        try {
            CherryTreePlacer.blockIdForSymbol('x');
            fail("Unknown cherry tree symbol should be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected.
        }
    }

    private void assertPattern(CherryTreePlacer.TreePattern pattern, String name, int width, int height, int centerX) {
        assertEquals(name, pattern.name);
        assertEquals(width, pattern.width);
        assertEquals(height, pattern.height);
        assertEquals(centerX, pattern.centerX);
        for (String row : pattern.rows) {
            assertEquals(width, row.length());
        }
    }
}
