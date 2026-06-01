package com.main.game.worldgen;

final class BiomeProfile {

    final String surfaceBlock;
    final String fillerBlock;
    final String deepBlock;
    final float treeChance;
    final float cactusChance;
    final float icePatchChance;
    final int heightOffset;
    final float terrainAmplitude;
    final float detailStrength;

    private BiomeProfile(String surfaceBlock, String fillerBlock, String deepBlock,
                         float treeChance, float cactusChance, float icePatchChance,
                         int heightOffset, float terrainAmplitude, float detailStrength) {
        this.surfaceBlock = surfaceBlock;
        this.fillerBlock = fillerBlock;
        this.deepBlock = deepBlock;
        this.treeChance = treeChance;
        this.cactusChance = cactusChance;
        this.icePatchChance = icePatchChance;
        this.heightOffset = heightOffset;
        this.terrainAmplitude = terrainAmplitude;
        this.detailStrength = detailStrength;
    }

    static BiomeProfile forType(BiomeType type) {
        switch (type) {
            case DESERT:
                return new BiomeProfile("sand", "sandstone", "stone", 0f, 0.18f, 0f, -2, 12f, 2.4f);
            case SNOW:
                return new BiomeProfile("snow", "dirt", "stone", 0.45f, 0f, 0.12f, 3, 12f, 2.4f);
            case CHERRY:
                return new BiomeProfile("grass", "dirt", "stone", 1f, 0f, 0f, 2, 12f, 2.4f);
            case PLAINS:
                return new BiomeProfile("grass", "dirt", "stone", 0.35f, 0f, 0f, 0, 3.5f, 0.6f);
            case FOREST:
            default:
                return new BiomeProfile("grass", "dirt", "stone", 0.85f, 0f, 0f, 0, 12f, 2.4f);
        }
    }
}
