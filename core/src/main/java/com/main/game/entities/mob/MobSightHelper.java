package com.main.game.entities.mob;

import com.main.game.entities.Entity;
import com.main.game.world.World;

final class MobSightHelper {

    private MobSightHelper() {
    }

    static boolean hasLineOfSight(World world, Entity from, Entity to) {
        if (world == null || from == null || to == null) {
            return false;
        }

        float x1 = from.getX() + from.getWidth() * 0.5f;
        float y1 = from.getY() + from.getHeight() * 0.65f;
        float x2 = to.getX() + to.getWidth() * 0.5f;
        float y2 = to.getY() + to.getHeight() * 0.65f;

        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len <= 0.001f) {
            return true;
        }

        float step = 0.2f;
        int steps = Math.max(1, (int) Math.ceil(len / step));
        for (int i = 1; i < steps; i++) {
            float t = i / (float) steps;
            float px = x1 + dx * t;
            float py = y1 + dy * t;
            int tx = (int) Math.floor(px);
            int ty = (int) Math.floor(py);
            if (world.isSolid(tx, ty)) {
                return false;
            }
        }
        return true;
    }
}
