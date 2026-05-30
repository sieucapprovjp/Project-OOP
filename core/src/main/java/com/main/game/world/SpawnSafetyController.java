package com.main.game.world;

import com.badlogic.gdx.math.Vector2;
import com.main.game.entities.player.Player;

public class SpawnSafetyController {

    private static final float SPAWN_GUARD_SECONDS = 5f;
    private static final float INITIAL_PLATFORM_SECONDS = 1.25f;

    private float spawnGuardTimer;
    private float initialSpawnPlatformTimer;

    public void beginInitialSpawn(World world, Player player) {
        spawnGuardTimer = SPAWN_GUARD_SECONDS;
        initialSpawnPlatformTimer = INITIAL_PLATFORM_SECONDS;
        ensurePlayerSpawnSafety(world, player);
    }

    public void update(float delta, World world, Player player) {
        updateInitialSpawnPlatform(delta, world);
        if (spawnGuardTimer > 0f) {
            ensurePlayerSpawnSafety(world, player);
            spawnGuardTimer -= delta;
        }
    }

    public void respawn(World world, Player player) {
        world.removeInitialSpawnPlatform();
        initialSpawnPlatformTimer = 0f;
        Vector2 spawn = world.getSpawnPoint();
        player.respawn(spawn.x, spawn.y);
        ensurePlayerSpawnSafety(world, player);
    }

    private void updateInitialSpawnPlatform(float delta, World world) {
        if (initialSpawnPlatformTimer <= 0f) return;
        initialSpawnPlatformTimer -= delta;
        if (initialSpawnPlatformTimer <= 0f) {
            world.removeInitialSpawnPlatform();
        }
    }

    private void ensurePlayerSpawnSafety(World world, Player player) {
        int tileY = Math.max(1, (int) Math.floor(player.getY()));
        if (tileY <= World.DEEPSLATE_TOP_Y) {
            world.removeInitialSpawnPlatform();
            initialSpawnPlatformTimer = 0f;
            Vector2 spawn = world.getSpawnPoint();
            player.respawn(spawn.x, spawn.y);
        }
    }
}
