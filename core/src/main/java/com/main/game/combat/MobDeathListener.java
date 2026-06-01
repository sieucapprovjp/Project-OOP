package com.main.game.combat;

import com.main.game.entities.mob.Mob;

public interface MobDeathListener {
    void onMobKilled(Mob mob);
}
