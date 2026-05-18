package com.main.game.entities.mob;

import com.main.game.entities.player.Player;

final class MobBrain {

    void update(Mob mob) {
        if (mob == null) return;

        if (!mob.isHostile()) {
            mob.doPatrol();
            return;
        }

        Player target = mob.getTarget();
        if (target == null) {
            mob.setAiState(Mob.AIState.PATROL);
            mob.getVelocity().x = 0f;
            return;
        }

        float dist = mob.distanceTo(target);

        switch (mob.getAIState()) {
            case PATROL:
                if (dist <= mob.getAggroRadius() && target.isAlive()) {
                    mob.setAiState(Mob.AIState.CHASE);
                } else {
                    mob.doPatrol();
                }
                break;
            case CHASE:
                if (dist > mob.getDeAggroRadius() || !target.isAlive()) {
                    mob.setAiState(Mob.AIState.PATROL);
                    mob.getVelocity().x = 0f;
                } else if (dist <= mob.getAttackRange()) {
                    mob.setAiState(Mob.AIState.ATTACK);
                    mob.getVelocity().x = 0f;
                } else {
                    mob.doChase();
                }
                break;
            case ATTACK:
                mob.getVelocity().x = 0f;
                if (dist > mob.getAttackRange()) {
                    mob.setAiState(Mob.AIState.CHASE);
                } else {
                    mob.doAttack();
                }
                break;
        }
    }
}
