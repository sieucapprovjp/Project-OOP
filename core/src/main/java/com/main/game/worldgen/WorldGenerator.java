package com.main.game.worldgen;

import com.main.game.world.World;
import java.util.Random;

public final class WorldGenerator {

    private static final float TERRAIN_FREQUENCY = 0.04f;
    private static final float BIOME_FREQUENCY = 0.018f;
    private static final float DESERT_BIOME_MAX_NOISE = -0.32f;
    private static final float PLAINS_BIOME_MAX_NOISE = -0.12f;
    private static final float FOREST_BIOME_MAX_NOISE = 0.08f;
    private static final float SNOW_BIOME_MIN_NOISE = 0.32f;
    private static final float CHERRY_FLOWER_CHANCE = 0.18f;
    private static final float CHERRY_GRASS_CHANCE = 0.20f;
    private static final int CHERRY_TREE_SPACING = 7;
    private static final float PLAINS_FLOWER_CHANCE = 0.34f;
    private static final int PLAINS_TREE_SPACING = 31;
    private static final int FOREST_TREE_SPACING = 19;
    private static final float OAK_APPLE_IN_TREE_CHANCE = 0.10f;
    private static final float DESERT_GRASS_PATCH_MIN_NOISE = -0.15f;
    private static final float CACTUS_FLOWER_CHANCE = 0.5f;
    private static final float DESERT_DEAD_BUSH_CHANCE = 0.08f;
    private static final float DESERT_DRY_GRASS_CHANCE = 0.22f;
    private static final float DESERT_SHORT_DRY_GRASS_CHANCE = 0.18f;
    private static final int SAVANNA_OAK_TREE_SPACING = 17;
    private static final float SAVANNA_OAK_TREE_CHANCE = 0.82f;
    private static final float SNOW_GRASS_PATCH_MIN_NOISE = -0.75f;
    private static final float SNOW_FIREFLY_BUSH_CHANCE = 0.05f;
    private static final float SNOW_FERN_CHANCE = 0.12f;
    private static final int SPRUCE_TREE_SPACING = 13;
    private static final float SPRUCE_TREE_CHANCE = 0.72f;
    private static final int FILLER_LAYER_DEPTH = 2;

    private WorldGenerator() {
    }

    public static void generate(World world, long seed) {
        if (world == null) return;
        Random random = new Random(seed);
        int baseGround = world.height / 2;

        for (int x = 0; x < world.width; x++) {
            BiomeType biome = chooseBiome(x, seed);
            BiomeProfile profile = BiomeProfile.forType(biome);
            world.setBiome(x, biome);

            float terrainNoise = WorldNoise.smoothNoise1D(x * TERRAIN_FREQUENCY, seed);
            float detailNoise = WorldNoise.smoothNoise1D(x * TERRAIN_FREQUENCY * 3f, seed + 1);
            int surface = baseGround + profile.heightOffset
                + (int) (terrainNoise * profile.terrainAmplitude + detailNoise * profile.detailStrength);
            surface = Math.max(8, Math.min(world.height - 8, surface));
            world.setSurfaceY(x, surface);

            fillColumn(world, x, surface, profile);
            decorateSurface(world, x, surface, biome, profile, random, seed);
        }

        StructurePlacer.placeVillageHouse(world, seed);
    }

    private static BiomeType chooseBiome(int x, long seed) {
        float noise = WorldNoise.smoothNoise1D(x * BIOME_FREQUENCY, seed + 9001);
        return chooseBiomeForNoise(noise);
    }

    static BiomeType chooseBiomeForNoise(float noise) {
        if (noise < DESERT_BIOME_MAX_NOISE) return BiomeType.DESERT;
        if (noise < PLAINS_BIOME_MAX_NOISE) return BiomeType.PLAINS;
        if (noise < FOREST_BIOME_MAX_NOISE) return BiomeType.FOREST;
        if (noise > SNOW_BIOME_MIN_NOISE) return BiomeType.SNOW;
        return BiomeType.CHERRY;
    }

    private static void fillColumn(World world, int x, int surface, BiomeProfile profile) {
        for (int y = 0; y <= surface; y++) {
            String blockId;
            if (y <= World.BEDROCK_TOP_Y) {
                blockId = "bedrock";
            } else if (y <= World.DEEPSLATE_TOP_Y) {
                blockId = "deepslate";
            } else if (y == surface) {
                blockId = profile.surfaceBlock;
            } else if (y >= surface - FILLER_LAYER_DEPTH && y < surface) {
                blockId = profile.fillerBlock;
            } else if (y < surface) {
                blockId = profile.deepBlock;
            } else {
                continue;
            }
            world.setBlock(x, y, WorldBlockFactory.create(x, y, blockId));
        }
    }

    private static void decorateSurface(World world, int x, int surface, BiomeType biome,
                                        BiomeProfile profile, Random random, long seed) {
        if (x <= 2 || x >= world.width - 3 || surface + 1 + maxTreePatternHeight() >= world.height) return;

        float local = Math.abs(WorldNoise.seededRandom(x, seed + 55));
        if (biome == BiomeType.DESERT) {
            decorateDesertSurface(world, x, surface, profile, random, seed, local);
            return;
        }
        if (biome == BiomeType.SNOW) {
            decorateSnowSurface(world, x, surface, profile, random, seed, local);
            return;
        }
        if (biome == BiomeType.CHERRY) {
            boolean treePlaced = false;
            if (profile.treeChance > 0f && x % CHERRY_TREE_SPACING == 0 && random.nextFloat() < profile.treeChance) {
                treePlaced = CherryTreePlacer.place(world, x, surface + 1, random);
            }
            if (!treePlaced) {
                placeCherryGroundDecoration(world, x, surface + 1, seed);
            }
            return;
        }
        if (biome == BiomeType.PLAINS) {
            decoratePlainsSurface(world, x, surface, profile, random, seed);
            return;
        }
        if (profile.treeChance > 0f && x % FOREST_TREE_SPACING == 0 && random.nextFloat() < profile.treeChance) {
            placeTree(world, x, surface + 1, 3 + random.nextInt(2), random);
        }
    }

    private static boolean placeTree(World world, int x, int baseY, int height, Random random) {
        int leafY = baseY + height;

        for (int ty = 0; ty < height; ty++) {
            int trunkY = baseY + ty;
            if (!world.isInBounds(x, trunkY) || world.getBlock(x, trunkY) != null) return false;
        }

        for (int lx = x - 1; lx <= x + 1; lx++) {
            for (int ly = leafY - 1; ly <= leafY; ly++) {
                if (!world.isInBounds(lx, ly)) return false;
                if (world.getBlock(lx, ly) != null) return false;
            }
        }

        for (int ty = 0; ty < height; ty++) {
            world.setBlock(x, baseY + ty, WorldBlockFactory.create(x, baseY + ty, "natural_wood"));
        }

        for (int lx = x - 1; lx <= x + 1; lx++) {
            for (int ly = leafY - 1; ly <= leafY; ly++) {
                if (world.isInBounds(lx, ly) && world.getBlock(lx, ly) == null) {
                    world.setBlock(lx, ly, WorldBlockFactory.create(lx, ly, oakLeafBlockForRoll(random.nextFloat())));
                }
            }
        }
        return true;
    }

    static String oakLeafBlockForRoll(float roll) {
        return roll < OAK_APPLE_IN_TREE_CHANCE ? "apple_in_tree" : "leaves";
    }

    private static void decorateDesertSurface(World world, int x, int surface, BiomeProfile profile,
                                              Random random, long seed, float local) {
        if (shouldUseDesertGrass(WorldNoise.smoothNoise1D(x * 0.08f, seed + 1203))) {
            world.setBlock(x, surface, WorldBlockFactory.create(x, surface, "grassin_desert"));
        }

        String surfaceBlockId = blockIdAt(world, x, surface);
        if (canPlaceSavannaOakOnSurface(surfaceBlockId)
            && x % SAVANNA_OAK_TREE_SPACING == 0
            && random.nextFloat() < SAVANNA_OAK_TREE_CHANCE
            && SavannaOakTreePlacer.place(world, x, surface + 1, random)) {
            return;
        }

        if ("sand".equals(surfaceBlockId) && local < profile.cactusChance) {
            placeCactus(world, x, surface + 1, 2 + random.nextInt(2), random);
            return;
        }

        placeDesertGroundDecoration(world, x, surface + 1, seed);
    }

    private static void decorateSnowSurface(World world, int x, int surface, BiomeProfile profile,
                                            Random random, long seed, float local) {
        if (local < profile.icePatchChance) {
            world.setBlock(x, surface, WorldBlockFactory.create(x, surface, "ice"));
            return;
        }

        if (shouldUseSnowGrass(WorldNoise.smoothNoise1D(x * 0.1f, seed + 2601))) {
            float surfaceNoise = Math.abs(WorldNoise.seededRandom(x, seed + 2602));
            world.setBlock(x, surface, WorldBlockFactory.create(x, surface, snowGrassSurfaceForNoise(surfaceNoise)));
        }

        String surfaceBlockId = blockIdAt(world, x, surface);
        if (canPlaceSpruceOnSurface(surfaceBlockId)
            && x % SPRUCE_TREE_SPACING == 0
            && random.nextFloat() < SPRUCE_TREE_CHANCE
            && SpruceTreePlacer.place(world, x, surface + 1, random)) {
            return;
        }

        if (canPlaceSpruceOnSurface(surfaceBlockId)) {
            placeSnowGroundDecoration(world, x, surface + 1, seed);
        }
    }

    private static void decoratePlainsSurface(World world, int x, int surface, BiomeProfile profile,
                                              Random random, long seed) {
        if (profile.treeChance > 0f
            && x % PLAINS_TREE_SPACING == 0
            && random.nextFloat() < profile.treeChance
            && placeTree(world, x, surface + 1, 3 + random.nextInt(2), random)) {
            return;
        }
        placePlainsGroundDecoration(world, x, surface + 1, seed);
    }

    private static void placeCherryGroundDecoration(World world, int x, int y, long seed) {
        if (!world.isInBounds(x, y) || world.getBlock(x, y) != null || !world.isSolid(x, y - 1)) {
            return;
        }
        float local = Math.abs(WorldNoise.seededRandom(x, seed + 1703));
        String decoration = cherryGroundDecorationForNoise(local);
        if (decoration != null) {
            world.setBlock(x, y, WorldBlockFactory.create(x, y, decoration));
        }
    }

    static String cherryGroundDecorationForNoise(float local) {
        if (local < CHERRY_FLOWER_CHANCE) {
            return "cherry_flower";
        }
        if (local < CHERRY_FLOWER_CHANCE + CHERRY_GRASS_CHANCE) {
            return "cherry_grass";
        }
        return null;
    }

    private static void placePlainsGroundDecoration(World world, int x, int y, long seed) {
        if (!world.isInBounds(x, y) || world.getBlock(x, y) != null || !world.isSolid(x, y - 1)) {
            return;
        }
        float local = Math.abs(WorldNoise.seededRandom(x, seed + 3109));
        String decoration = plainsGroundDecorationForNoise(local);
        if (decoration != null) {
            world.setBlock(x, y, WorldBlockFactory.create(x, y, decoration));
        }
    }

    static String plainsGroundDecorationForNoise(float local) {
        if (local >= PLAINS_FLOWER_CHANCE) {
            return null;
        }
        String[] flowers = {
            "dandelion", "poppy", "blue_orchid", "azure_bluet",
            "cornflower", "lily_of_the_valley", "oxeye_daisy"
        };
        int index = Math.min(flowers.length - 1, (int) (local / PLAINS_FLOWER_CHANCE * flowers.length));
        return flowers[index];
    }

    private static void placeDesertGroundDecoration(World world, int x, int y, long seed) {
        if (!world.isInBounds(x, y) || world.getBlock(x, y) != null || !world.isSolid(x, y - 1)) {
            return;
        }
        float local = Math.abs(WorldNoise.seededRandom(x, seed + 1907));
        String decoration = desertGroundDecorationForNoise(local);
        if (decoration != null) {
            world.setBlock(x, y, WorldBlockFactory.create(x, y, decoration));
        }
    }

    static String desertGroundDecorationForNoise(float local) {
        if (local < DESERT_DEAD_BUSH_CHANCE) {
            return "dead_bush";
        }
        float dryGrassMax = DESERT_DEAD_BUSH_CHANCE + DESERT_DRY_GRASS_CHANCE;
        if (local < dryGrassMax) {
            return "dry_grass";
        }
        if (local < 0.48f) {
            return "short_dry_grass";
        }
        return null;
    }

    private static void placeSnowGroundDecoration(World world, int x, int y, long seed) {
        if (!world.isInBounds(x, y) || world.getBlock(x, y) != null || !world.isSolid(x, y - 1)) {
            return;
        }
        float local = Math.abs(WorldNoise.seededRandom(x, seed + 2801));
        String decoration = snowGroundDecorationForNoise(local);
        if (decoration != null) {
            world.setBlock(x, y, WorldBlockFactory.create(x, y, decoration));
        }
    }

    static String snowGroundDecorationForNoise(float local) {
        if (local < SNOW_FIREFLY_BUSH_CHANCE) {
            return "firefly_bush";
        }
        if (local < 0.17f) {
            return "fern";
        }
        return null;
    }

    static boolean shouldUseDesertGrass(float patchNoise) {
        return patchNoise > DESERT_GRASS_PATCH_MIN_NOISE;
    }

    static boolean shouldUseSnowGrass(float patchNoise) {
        return patchNoise > SNOW_GRASS_PATCH_MIN_NOISE;
    }

    static String snowGrassSurfaceForNoise(float local) {
        return local < 0.75f ? "grassin_snow" : "grass_snow";
    }

    static boolean shouldPlaceCactusFlower(float local) {
        return local < CACTUS_FLOWER_CHANCE;
    }

    static boolean canPlaceSavannaOakOnSurface(String blockId) {
        return "grassin_desert".equals(blockId);
    }

    static boolean canPlaceSpruceOnSurface(String blockId) {
        return "grassin_snow".equals(blockId) || "grass_snow".equals(blockId);
    }

    private static int maxTreePatternHeight() {
        return Math.max(CherryTreePlacer.maxPatternHeight(),
            Math.max(SavannaOakTreePlacer.maxPatternHeight(), SpruceTreePlacer.maxPatternHeight()));
    }

    private static String blockIdAt(World world, int x, int y) {
        return world.getBlock(x, y) == null ? null : world.getBlock(x, y).getBlockId();
    }

    private static void placeCactus(World world, int x, int baseY, int height, Random random) {
        int placedHeight = 0;
        for (int i = 0; i < height && baseY + i < world.height; i++) {
            if (world.getBlock(x, baseY + i) != null) return;
            world.setBlock(x, baseY + i, WorldBlockFactory.create(x, baseY + i, "cactus"));
            placedHeight++;
        }
        int flowerY = baseY + placedHeight;
        if (placedHeight > 0
            && flowerY < world.height
            && world.getBlock(x, flowerY) == null
            && shouldPlaceCactusFlower(random.nextFloat())) {
            world.setBlock(x, flowerY, WorldBlockFactory.create(x, flowerY, "cactus_flower"));
        }
    }
}
