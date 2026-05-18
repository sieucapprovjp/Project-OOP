package com.main.game.worldgen;

final class BiomeProfile {

    final String surfaceBlock;
    final String fillerBlock;
    final String deepBlock;
    final float treeChance;
    final float cactusChance;
    final float icePatchChance;

    private BiomeProfile(String surfaceBlock, String fillerBlock, String deepBlock,
                         float treeChance, float cactusChance, float icePatchChance) {
        this.surfaceBlock = surfaceBlock;
        this.fillerBlock = fillerBlock;
        this.deepBlock = deepBlock;
        this.treeChance = treeChance;
        this.cactusChance = cactusChance;
        this.icePatchChance = icePatchChance;
    }

    static BiomeProfile forType(BiomeType type) {
        switch (type) {
            case DESERT:
                return new BiomeProfile("sand", "sandstone", "stone", 0f, 0.18f, 0f);
            case SNOW:
                return new BiomeProfile("snow", "dirt", "stone", 0.45f, 0f, 0.12f);
            case FOREST:
            default:
                return new BiomeProfile("grass", "dirt", "stone", 0.65f, 0f, 0f);
        }
    }
}
