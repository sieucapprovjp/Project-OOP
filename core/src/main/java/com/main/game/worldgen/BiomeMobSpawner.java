package com.main.game.worldgen;

import com.badlogic.gdx.math.Vector2;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;
import java.util.Random;

public final class BiomeMobSpawner {

    private BiomeMobSpawner() {
    }

    public static void spawnInitialMobs(World world, Player player, PhysicsEngine physics,
                                        EntityManager entityManager, long seed) {
        if (world == null || player == null || physics == null || entityManager == null) {
            return;
        }
        Random random = new Random(seed + 404);
        int playerX = Math.round(player.getX());
        int spawned = 0;

        for (int i = 0; i < 18 && spawned < 12; i++) {
            int side = i % 2 == 0 ? -1 : 1;
            int distance = 12 + i * 7 + random.nextInt(5);
            int targetX = playerX + side * distance;
            Vector2 spawn = SpawnSafety.findSurfaceSpawn(world, targetX, 10, 1, 2);
            if (spawn == null) continue;

            Mob.MobType type = chooseMobForBiome(world.getBiome((int) spawn.x), random);
            entityManager.addMob(new Mob(spawn.x, spawn.y, type, player, physics, world));
            spawned++;
        }
    }

    private static Mob.MobType chooseMobForBiome(BiomeType biome, Random random) {
        switch (biome) {
            case DESERT:
                return random.nextFloat() < 0.7f ? Mob.MobType.HUSK : Mob.MobType.SKELETON;
            case SNOW:
                return random.nextFloat() < 0.7f ? Mob.MobType.STRAY : Mob.MobType.SHEEP;
            case FOREST:
            default:
                Mob.MobType[] forest = {
                    Mob.MobType.COW,
                    Mob.MobType.PIG,
                    Mob.MobType.SHEEP,
                    Mob.MobType.CHICKEN,
                    Mob.MobType.ZOMBIE
                };
                return forest[random.nextInt(forest.length)];
        }
    }
}
