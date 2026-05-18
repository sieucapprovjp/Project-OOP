package com.main.game.entities.mob;

final class MobProfile {

    private static final float DEFAULT_PATROL_SPEED = 2f;
    private static final float DEFAULT_CHASE_SPEED = 3.5f;
    private static final float HOSTILE_AGGRO_RADIUS = 8f;
    private static final float DEFAULT_DEAGGRO = 14f;
    private static final float DEFAULT_ATTACK_RANGE = 1.2f;
    private static final float DEFAULT_ATTACK_COOL = 1.5f;
    private static final float DEFAULT_PATROL_RANGE = 6f;

    final float patrolSpeed;
    final float chaseSpeed;
    final float aggroRadius;
    final float deAggroRadius;
    final float attackRange;
    final float attackCooldown;
    final float patrolRange;
    final int attackDamage;
    final int maxHealth;
    final boolean hostile;

    private MobProfile(float patrolSpeed, float chaseSpeed, float aggroRadius, float deAggroRadius,
                       float attackRange, float attackCooldown, float patrolRange, int attackDamage,
                       int maxHealth, boolean hostile) {
        this.patrolSpeed = patrolSpeed;
        this.chaseSpeed = chaseSpeed;
        this.aggroRadius = aggroRadius;
        this.deAggroRadius = deAggroRadius;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.patrolRange = patrolRange;
        this.attackDamage = attackDamage;
        this.maxHealth = maxHealth;
        this.hostile = hostile;
    }

    static MobProfile forType(Mob.MobType type) {
        switch (type) {
            case HUSK:
                return new MobProfile(DEFAULT_PATROL_SPEED, DEFAULT_CHASE_SPEED, HOSTILE_AGGRO_RADIUS, 14f,
                    DEFAULT_ATTACK_RANGE, DEFAULT_ATTACK_COOL, DEFAULT_PATROL_RANGE, 3, 20, true);
            case SKELETON:
                return new MobProfile(1.8f, 3.0f, HOSTILE_AGGRO_RADIUS, 18f,
                    6f, 2.0f, 5f, 3, 20, true);
            case STRAY:
                return new MobProfile(1.7f, 2.8f, HOSTILE_AGGRO_RADIUS, 18f,
                    6f, 2.2f, 5f, 3, 22, true);
            case COW:
                return passive(1.6f, 5f, 18);
            case PIG:
                return passive(1.7f, 5f, 14);
            case SHEEP:
                return passive(1.7f, 5f, 14);
            case CHICKEN:
                return passive(1.9f, 4f, 8);
            case ZOMBIE:
            default:
                return new MobProfile(DEFAULT_PATROL_SPEED, DEFAULT_CHASE_SPEED, HOSTILE_AGGRO_RADIUS, DEFAULT_DEAGGRO,
                    DEFAULT_ATTACK_RANGE, DEFAULT_ATTACK_COOL, DEFAULT_PATROL_RANGE, 2, 20, true);
        }
    }

    private static MobProfile passive(float speed, float patrolRange, int maxHealth) {
        return new MobProfile(speed, speed, 0f, 0f, 0f, 0f, patrolRange, 0, maxHealth, false);
    }
}
