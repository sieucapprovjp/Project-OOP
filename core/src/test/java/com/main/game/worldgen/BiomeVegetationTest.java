package com.main.game.worldgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.inventory.ToolRegistry;
import org.junit.Test;

public class BiomeVegetationTest {

    @Test
    public void generatedTreeLogsArePassThroughAndDropNormalLogs() {
        assertNaturalLog("natural_wood", "wood");
        assertNaturalLog("natural_cherry_log", "cherry_log");
        assertNaturalLog("natural_spruce_log", "spruce_log");
    }

    @Test
    public void biomeSurfaceAndDecorationMetadataIsRegistered() {
        assertTrue(BlockRegistry.isSolid("grassin_desert"));
        assertTrue(BlockRegistry.isSolid("grassin_snow"));
        assertTrue(BlockRegistry.isSolid("grass_snow"));
        assertFalse(BlockRegistry.isSolid("cactus_flower"));
        assertFalse(BlockRegistry.isSolid("poppy"));
        assertFalse(BlockRegistry.isSolid("dandelion"));
        assertFalse(BlockRegistry.isSolid("blue_orchid"));
        assertFalse(BlockRegistry.isSolid("azure_bluet"));
        assertFalse(BlockRegistry.isSolid("cornflower"));
        assertFalse(BlockRegistry.isSolid("lily_of_the_valley"));
        assertFalse(BlockRegistry.isSolid("oxeye_daisy"));
        assertFalse(BlockRegistry.isSolid("dead_bush"));
        assertFalse(BlockRegistry.isSolid("dry_grass"));
        assertFalse(BlockRegistry.isSolid("short_dry_grass"));
        assertFalse(BlockRegistry.isSolid("apple_in_tree"));
        assertFalse(BlockRegistry.isPlaceable("apple_in_tree"));
        assertEquals("apple", BlockRegistry.getDropItemId("apple_in_tree"));
        assertFalse(BlockRegistry.isSolid("fern"));
        assertFalse(BlockRegistry.isSolid("firefly_bush"));
        assertFalse(BlockRegistry.isSolid("desert_oak_leaves"));
        assertFalse(BlockRegistry.isSolid("desert_oak_leaves_2"));
        assertFalse(BlockRegistry.isSolid("spruce_leaves"));
    }

    @Test
    public void savannaOakPatternsAreCenteredAndRegistered() {
        SavannaOakTreePlacer.TreePattern[] patterns = SavannaOakTreePlacer.patterns();

        assertEquals(2, patterns.length);
        assertPattern(patterns[0], "A", 7, 5, 3);
        assertPattern(patterns[1], "B", 9, 5, 4);
        assertEquals(5, SavannaOakTreePlacer.maxPatternHeight());
        assertPatternSymbolsRegistered(patterns);
        assertLogsStayOnCenterColumn(patterns);
    }

    @Test
    public void sprucePatternsAreTallCenteredAndRegistered() {
        SpruceTreePlacer.TreePattern[] patterns = SpruceTreePlacer.patterns();

        assertEquals(2, patterns.length);
        assertPattern(patterns[0], "A", 7, 9, 3);
        assertPattern(patterns[1], "B", 7, 10, 3);
        assertEquals(10, SpruceTreePlacer.maxPatternHeight());
        assertPatternSymbolsRegistered(patterns);
        assertLogsStayOnCenterColumn(patterns);
    }

    @Test
    public void desertGroundDecorationUsesExpectedBuckets() {
        assertEquals("dead_bush", WorldGenerator.desertGroundDecorationForNoise(0f));
        assertEquals("dead_bush", WorldGenerator.desertGroundDecorationForNoise(0.079f));
        assertEquals("dry_grass", WorldGenerator.desertGroundDecorationForNoise(0.08f));
        assertEquals("dry_grass", WorldGenerator.desertGroundDecorationForNoise(0.299f));
        assertEquals("short_dry_grass", WorldGenerator.desertGroundDecorationForNoise(0.30f));
        assertEquals("short_dry_grass", WorldGenerator.desertGroundDecorationForNoise(0.479f));
        assertNull(WorldGenerator.desertGroundDecorationForNoise(0.48f));
    }

    @Test
    public void snowyGroundDecorationUsesExpectedBuckets() {
        assertEquals("firefly_bush", WorldGenerator.snowGroundDecorationForNoise(0f));
        assertEquals("firefly_bush", WorldGenerator.snowGroundDecorationForNoise(0.049f));
        assertEquals("fern", WorldGenerator.snowGroundDecorationForNoise(0.05f));
        assertEquals("fern", WorldGenerator.snowGroundDecorationForNoise(0.169f));
        assertNull(WorldGenerator.snowGroundDecorationForNoise(0.17f));
    }

    @Test
    public void treeSpawnSurfacesAreConstrainedByBiome() {
        assertFalse(WorldGenerator.shouldUseDesertGrass(-0.15f));
        assertTrue(WorldGenerator.shouldUseDesertGrass(-0.149f));
        assertFalse(WorldGenerator.shouldUseSnowGrass(-0.75f));
        assertTrue(WorldGenerator.shouldUseSnowGrass(-0.749f));
        assertTrue(WorldGenerator.canPlaceSavannaOakOnSurface("grassin_desert"));
        assertFalse(WorldGenerator.canPlaceSavannaOakOnSurface("sand"));
        assertTrue(WorldGenerator.canPlaceSpruceOnSurface("grassin_snow"));
        assertTrue(WorldGenerator.canPlaceSpruceOnSurface("grass_snow"));
        assertFalse(WorldGenerator.canPlaceSpruceOnSurface("snow"));
        assertEquals("grassin_snow", WorldGenerator.snowGrassSurfaceForNoise(0.749f));
        assertEquals("grass_snow", WorldGenerator.snowGrassSurfaceForNoise(0.75f));
    }

    @Test
    public void cactusFlowerUsesHalfChanceThreshold() {
        assertTrue(WorldGenerator.shouldPlaceCactusFlower(0f));
        assertTrue(WorldGenerator.shouldPlaceCactusFlower(0.499f));
        assertFalse(WorldGenerator.shouldPlaceCactusFlower(0.5f));
    }

    @Test
    public void plainsFlowerDecorationUsesExpectedBuckets() {
        assertEquals("dandelion", WorldGenerator.plainsGroundDecorationForNoise(0f));
        assertEquals("poppy", WorldGenerator.plainsGroundDecorationForNoise(0.05f));
        assertEquals("blue_orchid", WorldGenerator.plainsGroundDecorationForNoise(0.10f));
        assertEquals("azure_bluet", WorldGenerator.plainsGroundDecorationForNoise(0.15f));
        assertEquals("cornflower", WorldGenerator.plainsGroundDecorationForNoise(0.20f));
        assertEquals("lily_of_the_valley", WorldGenerator.plainsGroundDecorationForNoise(0.25f));
        assertEquals("oxeye_daisy", WorldGenerator.plainsGroundDecorationForNoise(0.30f));
        assertNull(WorldGenerator.plainsGroundDecorationForNoise(0.34f));
    }

    @Test
    public void oakTreesCanGenerateAppleLeaves() {
        assertEquals("apple_in_tree", WorldGenerator.oakLeafBlockForRoll(0f));
        assertEquals("apple_in_tree", WorldGenerator.oakLeafBlockForRoll(0.099f));
        assertEquals("leaves", WorldGenerator.oakLeafBlockForRoll(0.10f));
    }

    @Test
    public void plainsProfileIsMostlyFlat() {
        BiomeProfile plains = BiomeProfile.forType(BiomeType.PLAINS);
        BiomeProfile forest = BiomeProfile.forType(BiomeType.FOREST);

        assertEquals("grass", plains.surfaceBlock);
        assertTrue(plains.terrainAmplitude < forest.terrainAmplitude);
        assertTrue(plains.detailStrength < forest.detailStrength);
        assertTrue(plains.treeChance < forest.treeChance);
    }

    private void assertNaturalLog(String naturalId, String dropId) {
        assertNotNull(BlockRegistry.get(naturalId));
        assertFalse(BlockRegistry.isSolid(naturalId));
        assertFalse(BlockRegistry.isPlaceable(naturalId));
        assertEquals(dropId, BlockRegistry.getDropItemId(naturalId));
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", naturalId) > 1f);
    }

    private void assertPattern(SavannaOakTreePlacer.TreePattern pattern, String name,
                               int width, int height, int centerX) {
        assertEquals(name, pattern.name);
        assertEquals(width, pattern.width);
        assertEquals(height, pattern.height);
        assertEquals(centerX, pattern.centerX);
        for (String row : pattern.rows) {
            assertEquals(width, row.length());
        }
    }

    private void assertPattern(SpruceTreePlacer.TreePattern pattern, String name,
                               int width, int height, int centerX) {
        assertEquals(name, pattern.name);
        assertEquals(width, pattern.width);
        assertEquals(height, pattern.height);
        assertEquals(centerX, pattern.centerX);
        for (String row : pattern.rows) {
            assertEquals(width, row.length());
        }
    }

    private void assertPatternSymbolsRegistered(SavannaOakTreePlacer.TreePattern[] patterns) {
        for (SavannaOakTreePlacer.TreePattern pattern : patterns) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    String blockId = SavannaOakTreePlacer.blockIdForSymbol(row.charAt(i));
                    if (blockId != null) {
                        assertNotNull(BlockRegistry.get(blockId));
                    }
                }
            }
        }
    }

    private void assertPatternSymbolsRegistered(SpruceTreePlacer.TreePattern[] patterns) {
        for (SpruceTreePlacer.TreePattern pattern : patterns) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    String blockId = SpruceTreePlacer.blockIdForSymbol(row.charAt(i));
                    if (blockId != null) {
                        assertNotNull(BlockRegistry.get(blockId));
                    }
                }
            }
        }
    }

    private void assertLogsStayOnCenterColumn(SavannaOakTreePlacer.TreePattern[] patterns) {
        for (SavannaOakTreePlacer.TreePattern pattern : patterns) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    if (SavannaOakTreePlacer.NATURAL_OAK_LOG.equals(SavannaOakTreePlacer.blockIdForSymbol(row.charAt(i)))) {
                        assertEquals(pattern.centerX, i);
                    }
                }
            }
        }
    }

    private void assertLogsStayOnCenterColumn(SpruceTreePlacer.TreePattern[] patterns) {
        for (SpruceTreePlacer.TreePattern pattern : patterns) {
            for (String row : pattern.rows) {
                for (int i = 0; i < row.length(); i++) {
                    if (SpruceTreePlacer.NATURAL_SPRUCE_LOG.equals(SpruceTreePlacer.blockIdForSymbol(row.charAt(i)))) {
                        assertEquals(pattern.centerX, i);
                    }
                }
            }
        }
    }
}
