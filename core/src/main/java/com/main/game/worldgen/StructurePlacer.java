package com.main.game.worldgen;

import com.main.game.world.World;

final class StructurePlacer {

    private StructurePlacer() {
    }

    static void placeVillageHouse(World world, long seed) {
        int center = world.width / 2 + 42;
        int baseX = findFlatGround(world, center, 90, 10);
        if (baseX < 0) return;

        int groundY = SpawnSafety.findSurfaceY(world, baseX);
        if (groundY <= 1) return;
        int floorY = groundY;

        clearArea(world, baseX - 1, floorY, 12, 8);
        for (int x = baseX; x < baseX + 10; x++) {
            world.setBlock(x, floorY, WorldBlockFactory.create(x, floorY, "planks"));
        }

        for (int y = floorY + 1; y <= floorY + 4; y++) {
            world.setBlock(baseX, y, WorldBlockFactory.create(baseX, y, "wood"));
            world.setBlock(baseX + 9, y, WorldBlockFactory.create(baseX + 9, y, "wood"));
        }
        for (int x = baseX + 1; x < baseX + 9; x++) {
            world.setBlock(x, floorY + 4, WorldBlockFactory.create(x, floorY + 4, "planks"));
        }
        for (int x = baseX - 1; x <= baseX + 10; x++) {
            int roofY = floorY + 5 - Math.abs((baseX + 4) - Math.max(baseX, Math.min(baseX + 8, x))) / 4;
            world.setBlock(x, roofY, WorldBlockFactory.create(x, roofY, "wood"));
        }

        for (int y = floorY + 1; y <= floorY + 3; y++) {
            world.setBlock(baseX + 3, y, WorldBlockFactory.create(baseX + 3, y, "planks"));
            world.setBlock(baseX + 6, y, WorldBlockFactory.create(baseX + 6, y, "planks"));
        }

        world.setBlock(baseX + 4, floorY + 1, null);
        world.setBlock(baseX + 4, floorY + 2, null);
        world.setBlock(baseX + 5, floorY + 2, null);
    }

    private static int findFlatGround(World world, int center, int radius, int width) {
        for (int offset = 0; offset <= radius; offset++) {
            int[] candidates = offset == 0 ? new int[] {center} : new int[] {center - offset, center + offset};
            for (int baseX : candidates) {
                if (baseX < 2 || baseX + width >= world.width - 2) continue;
                int y = SpawnSafety.findSurfaceY(world, baseX);
                if (y <= 0) continue;
                boolean flat = true;
                for (int x = baseX + 1; x < baseX + width; x++) {
                    int sy = SpawnSafety.findSurfaceY(world, x);
                    if (Math.abs(sy - y) > 1) {
                        flat = false;
                        break;
                    }
                }
                if (flat) return baseX;
            }
        }
        return -1;
    }

    private static void clearArea(World world, int x, int y, int width, int height) {
        for (int tx = x; tx < x + width; tx++) {
            for (int ty = y; ty < y + height; ty++) {
                if (world.isInBounds(tx, ty)) {
                    world.setBlock(tx, ty, null);
                }
            }
        }
    }
}
