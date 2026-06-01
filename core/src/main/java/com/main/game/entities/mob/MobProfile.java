package com.main.game.entities.mob;

final class MobProfile {

    private static final float DEFAULT_PATROL_SPEED = 2f;
    private static final float DEFAULT_CHASE_SPEED = 2.6f;
    private static final float HOSTILE_AGGRO_RADIUS = 8f;
    private static final float DEFAULT_DEAGGRO = 14f;
    private static final float DEFAULT_ATTACK_RANGE = 1.2f;
    private static final float DEFAULT_ATTACK_COOL = 1.5f;
    private static final float DEFAULT_PATROL_RANGE = 6f;
    private static final int LIGHT_HOSTILE_DAMAGE = 1;
    private static final float MELEE_HOSTILE_ATTACK_COOL = 2.2f;
    private static final float RANGED_HOSTILE_ATTACK_COOL = 3.0f;

    final float patrolSpeed;
    final float chaseSpeed;
    final float aggroRadius;
    final float deAggroRadius;
    final float attackRange;
    final float attackCooldown;
    final float patrolRange;
    final float width;
    final float height;
    final int attackDamage;
    final int maxHealth;
    final int allegiance;

    private MobProfile(float patrolSpeed, float chaseSpeed, float aggroRadius, float deAggroRadius,
                       float attackRange, float attackCooldown, float patrolRange, int attackDamage,
                       int maxHealth, int allegiance, float width, float height) {
        this.patrolSpeed = patrolSpeed;
        this.chaseSpeed = chaseSpeed;
        this.aggroRadius = aggroRadius;
        this.deAggroRadius = deAggroRadius;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.patrolRange = patrolRange;
        this.width = width;
        this.height = height;
        this.attackDamage = attackDamage;
        this.maxHealth = maxHealth;
        this.allegiance = allegiance;
    }

    static MobProfile forType(Mob.MobType type) {
        switch (type) {
            case DOG:
                return tamed(2.2f, 6f, 20, 0.9f, 1.0f);
            case TAMED_HORSE:
                return tamed(2.8f, 7f, 26, 1.6f, 1.6f);
            case HORSE:
                return passive(2.4f, 7f, 26, 1.6f, 1.6f);
            case WOLF:
                return passive(2.2f, 6f, 18, 0.9f, 1.0f);
            case CAT:
                return passive(2.0f, 5f, 10, 0.7f, 0.8f);
            case VILLAGER:
                return passive(1.4f, 4f, 20, 0.8f, 1.8f);
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
                return passive(1.2f, 3f, 6, 0.7f, 0.5f);
            case DOLPHIN:
                return passive(2.6f, 8f, 20, 1.6f, 0.8f);
            case HUSK:
                return new MobProfile(DEFAULT_PATROL_SPEED, DEFAULT_CHASE_SPEED, HOSTILE_AGGRO_RADIUS, 14f,
                    DEFAULT_ATTACK_RANGE, MELEE_HOSTILE_ATTACK_COOL, DEFAULT_PATROL_RANGE, LIGHT_HOSTILE_DAMAGE, 20, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case SKELETON:
                return new MobProfile(1.8f, 2.4f, HOSTILE_AGGRO_RADIUS, 18f,
                    5f, RANGED_HOSTILE_ATTACK_COOL, 5f, LIGHT_HOSTILE_DAMAGE, 20, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case STRAY:
                return new MobProfile(1.7f, 2.3f, HOSTILE_AGGRO_RADIUS, 18f,
                    5f, RANGED_HOSTILE_ATTACK_COOL, 5f, LIGHT_HOSTILE_DAMAGE, 22, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case PILLAGER:
                return new MobProfile(1.9f, 2.4f, HOSTILE_AGGRO_RADIUS, 18f,
                    6f, 2.0f, 5f, 3, 24, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case VINDICATOR:
                return new MobProfile(2.1f, 2.7f, HOSTILE_AGGRO_RADIUS, DEFAULT_DEAGGRO,
                    DEFAULT_ATTACK_RANGE, 1.2f, DEFAULT_PATROL_RANGE, 5, 24, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case EVOKER:
                return new MobProfile(1.6f, 2.1f, HOSTILE_AGGRO_RADIUS, 18f,
                    6f, 2.5f, 5f, 4, 24, MobAllegiance.HOSTILE, 0.8f, 1.8f);
            case RAVAGER:
                return new MobProfile(1.8f, 2.5f, HOSTILE_AGGRO_RADIUS, DEFAULT_DEAGGRO,
                    1.5f, 1.4f, 6f, 7, 40, MobAllegiance.HOSTILE, 2.0f, 2.2f);
            case COW:
                return passive(1.6f, 5f, 18, 1.4f, 1.4f);
            case PIG:
                return passive(1.7f, 5f, 14, 1.5f, 1.0f);
            case SHEEP:
                return passive(1.7f, 5f, 14, 1.3f, 1.3f);
            case CHICKEN:
                return passive(1.9f, 4f, 8, 0.6f, 0.8f);
            case ZOMBIE:
            default:
                return new MobProfile(DEFAULT_PATROL_SPEED, DEFAULT_CHASE_SPEED, HOSTILE_AGGRO_RADIUS, DEFAULT_DEAGGRO,
                    DEFAULT_ATTACK_RANGE, MELEE_HOSTILE_ATTACK_COOL, DEFAULT_PATROL_RANGE, LIGHT_HOSTILE_DAMAGE, 20, MobAllegiance.HOSTILE, 0.8f, 1.8f);
        }
    }

    private static MobProfile tamed(float speed, float patrolRange, int maxHealth, float width, float height) {
        return new MobProfile(speed, speed, 0f, 0f, 0f, 0f, patrolRange, 0, maxHealth, MobAllegiance.TAMED, width, height);
    }

    private static MobProfile passive(float speed, float patrolRange, int maxHealth, float width, float height) {
        return new MobProfile(speed, speed, 0f, 0f, 0f, 0f, patrolRange, 0, maxHealth, MobAllegiance.PASSIVE, width, height);
    }
}
