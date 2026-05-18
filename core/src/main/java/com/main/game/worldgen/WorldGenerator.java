package com.main.game.worldgen;

import com.main.game.world.World;
import java.util.Random;

public final class WorldGenerator {

    private static final float TERRAIN_FREQUENCY = 0.04f;
    private static final float BIOME_FREQUENCY = 0.018f;

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
            float detailNoise = WorldNoise.smoothNoise1D(x * TERRAIN_FREQUENCY * 3f, seed + 1) * 0.2f;
            int biomeOffset = biome == BiomeType.SNOW ? 3 : biome == BiomeType.DESERT ? -2 : 0;
            int surface = baseGround + biomeOffset + (int) ((terrainNoise + detailNoise) * 12f);
            surface = Math.max(8, Math.min(world.height - 8, surface));

            fillColumn(world, x, surface, profile);
            decorateSurface(world, x, surface, biome, profile, random, seed);
        }

        StructurePlacer.placeVillageHouse(world, seed);
    }

    private static BiomeType chooseBiome(int x, long seed) {
        float noise = WorldNoise.smoothNoise1D(x * BIOME_FREQUENCY, seed + 9001);
        if (noise < -0.28f) return BiomeType.DESERT;
        if (noise > 0.32f) return BiomeType.SNOW;
        return BiomeType.FOREST;
    }

    private static void fillColumn(World world, int x, int surface, BiomeProfile profile) {
        for (int y = 0; y <= surface; y++) {
            String blockId;
            if (y == 0) {
                blockId = "bedrock";
            } else if (y == surface) {
                blockId = profile.surfaceBlock;
            } else if (y >= surface - 3) {
                blockId = profile.fillerBlock;
            } else {
                blockId = profile.deepBlock;
            }
            world.setBlock(x, y, WorldBlockFactory.create(x, y, blockId));
        }
    }

    private static void decorateSurface(World world, int x, int surface, BiomeType biome,
                                        BiomeProfile profile, Random random, long seed) {
        if (x <= 2 || x >= world.width - 3 || surface + 6 >= world.height) return;

        float local = Math.abs(WorldNoise.seededRandom(x, seed + 55));
        if (biome == BiomeType.DESERT && local < profile.cactusChance) {
            placeCactus(world, x, surface + 1, 2 + random.nextInt(2));
            return;
        }
        if (biome == BiomeType.SNOW && local < profile.icePatchChance) {
            world.setBlock(x, surface, WorldBlockFactory.create(x, surface, "ice"));
            return;
        }
        if (profile.treeChance > 0f && x % 29 == 0 && random.nextFloat() < profile.treeChance) {
            placeTree(world, x, surface + 1, 3 + random.nextInt(2));
        }
    }

    private static void placeTree(World world, int x, int baseY, int height) {
        for (int ty = 0; ty < height && baseY + ty < world.height; ty++) {
            world.setBlock(x, baseY + ty, WorldBlockFactory.create(x, baseY + ty, "wood"));
        }

        int leafY = baseY + height;
        for (int lx = x - 1; lx <= x + 1; lx++) {
            for (int ly = leafY - 1; ly <= leafY; ly++) {
                if (world.isInBounds(lx, ly) && world.getBlock(lx, ly) == null) {
                    world.setBlock(lx, ly, WorldBlockFactory.create(lx, ly, "leaves"));
                }
            }
        }
    }

    private static void placeCactus(World world, int x, int baseY, int height) {
        for (int i = 0; i < height && baseY + i < world.height; i++) {
            if (world.getBlock(x, baseY + i) != null) return;
            world.setBlock(x, baseY + i, WorldBlockFactory.create(x, baseY + i, "cactus"));
        }
    }
}
