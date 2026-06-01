package com.main.game.worldgen;

import com.badlogic.gdx.math.Vector2;
import com.main.game.world.World;

public final class SpawnSafety {

    private SpawnSafety() {
    }

    public static boolean isAreaClear(World world, int x, int y, int width, int height) {
        if (world == null) return false;
        for (int tx = x; tx < x + width; tx++) {
            for (int ty = y; ty < y + height; ty++) {
                if (!world.isInBounds(tx, ty) || world.isSolid(tx, ty)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasSolidGround(World world, int x, int y, int width) {
        if (world == null || y <= 0) return false;
        for (int tx = x; tx < x + width; tx++) {
            if (!world.isInBounds(tx, y - 1) || !world.isSolid(tx, y - 1)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSafeEntitySpawn(World world, int x, int y, int width, int height) {
        return hasSolidGround(world, x, y, width) && isAreaClear(world, x, y, width, height);
    }

    public static Vector2 findSurfaceSpawn(World world, int startX, int searchRadius, int width, int height) {
        if (world == null) return null;
        int clampedStart = Math.max(1, Math.min(world.width - width - 1, startX));
        for (int radius = 0; radius <= searchRadius; radius++) {
            int[] candidates = radius == 0
                ? new int[] {clampedStart}
                : new int[] {clampedStart - radius, clampedStart + radius};
            for (int x : candidates) {
                if (x < 1 || x + width >= world.width - 1) continue;
                int y = findSurfaceY(world, x);
                if (y > 0 && isSafeEntitySpawn(world, x, y, width, height)) {
                    return new Vector2(x + 0.1f, y);
                }
            }
        }
        return null;
    }

    public static int findSurfaceY(World world, int x) {
        if (world == null || x < 0 || x >= world.width) return -1;
        for (int y = world.height - 2; y >= 1; y--) {
            if (world.isSolid(x, y) && !world.isSolid(x, y + 1)) {
                return y + 1;
            }
        }
        return -1;
    }

}
