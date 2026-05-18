package com.main.game.entities.mob;

import com.main.game.entities.Entity;
import com.main.game.world.World;

final class MobMovementHelper {

    private MobMovementHelper() {
    }

    static boolean shouldJumpOverObstacle(Entity mob, World world, boolean facingRight) {
        if (mob == null || world == null || !mob.isOnGround()) {
            return false;
        }

        int dir = facingRight ? 1 : -1;
        float probeXf = facingRight ? mob.getX() + mob.getWidth() + 0.05f : mob.getX() - 0.05f;
        int probeX = (int) Math.floor(probeXf) + (dir < 0 ? -1 : 0);

        int footY = (int) Math.floor(mob.getY() + 0.05f);
        int chestY = (int) Math.floor(mob.getY() + 0.95f);
        int headY = (int) Math.floor(mob.getY() + 1.6f);

        boolean blockedAhead = world.isSolid(probeX, footY) || world.isSolid(probeX, chestY);
        boolean spaceToJump = !world.isSolid(probeX, headY) && !world.isSolid(probeX, headY + 1);
        return blockedAhead && spaceToJump;
    }
}
