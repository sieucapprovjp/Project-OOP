package com.main.game.worldgen;

final class WorldNoise {

    private WorldNoise() {
    }

    static float smoothNoise1D(float x, long seed) {
        int intX = (int) Math.floor(x);
        float fracX = x - intX;

        float v1 = seededRandom(intX, seed);
        float v2 = seededRandom(intX + 1, seed);
        float f = (1f - (float) Math.cos(fracX * Math.PI)) * 0.5f;
        return v1 * (1f - f) + v2 * f;
    }

    static float seededRandom(int x, long seed) {
        long n = x * 374761393L + seed * 668265263L;
        n = (n ^ (n >> 13)) * 1274126177L;
        return (((n & 0x7FFFFFFF) / (float) 0x7FFFFFFF) * 2f) - 1f;
    }
}
