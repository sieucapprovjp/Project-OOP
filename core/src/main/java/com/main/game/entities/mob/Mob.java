package com.main.game.entities.mob;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.entities.Entity;
import com.main.game.entities.EntityState;
import com.main.game.entities.player.Player;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;

/**
 * Mob với AI đơn giản: PATROL <-> CHASE.
 *
 * Assets dùng (cow):
 *  - IDLE/LOOK : mvp/mob/cow/cow_look.png
 *  - RUN/PATROL: mvp/mob/cow/cow_walk_1.png .. cow_walk_6.png
 *  - HURT      : mvp/mob/cow/cow_hurt.png
 *
 * Hành vi:
 *  - PATROL : đi qua lại trong phạm vi patrolRange tile.
 *  - CHASE  : phát hiện Player trong aggroRadius, lao về phía Player.
 *  - ATTACK : trong attackRange, gây damage mỗi attackCooldown giây.
 *
 * TODO(DUOC-ENTITY):
 *  - Thêm IDLE timer ngắn khi đảo chiều patrol.
 *  - Mob nhảy qua chướng ngại vật khi chase (pathfinding đơn giản).
 *  - Thêm MobType riêng khi có asset skeleton.
 */
public class Mob extends Entity {

    // ─── Kiểu mob ─────────────────────────────────────────────
    public enum MobType { ZOMBIE, HUSK, SKELETON, STRAY, COW, PIG, SHEEP, CHICKEN }

    private static final float MOB_W = 0.8f;
    private static final float MOB_H = 1.8f;
    private static final float JUMP_IMPULSE = 10f;

    // ─── AI state ─────────────────────────────────────────────
    public enum AIState { PATROL, CHASE, ATTACK }
    private AIState aiState = AIState.PATROL;

    private float patrolOriginX;
    private float attackTimer = 0f;
    private float hurtTimer   = 0f;
    private int   health;
    private EntityState state = EntityState.IDLE;
    private float stateTime   = 0f;

    // ─── Animations ───────────────────────────────────────────
    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animWalk;
    private Animation<TextureRegion> animHurt;
    private final MobAssetPack assets = new MobAssetPack();
    private final MobBrain brain = new MobBrain();
    private final MobRenderer renderer = new MobRenderer();
    private final MobProfile profile;

    // ─── Refs ─────────────────────────────────────────────────
    private Player          target;
    private final PhysicsEngine physics;
    private final World         world;

    // ───────────────────────────────────────────────────────────

    public Mob(float x, float y, MobType type, Player target, PhysicsEngine physics, World world) {
        super(x, y, MOB_W, MOB_H);
        this.patrolOriginX = x;
        this.target        = target;
        this.physics       = physics;
        this.world         = world;
        this.profile = MobProfile.forType(type);
        this.health = profile.maxHealth;

        loadAssets(type);
    }

    // ─── Asset loading ─────────────────────────────────────────

    private void loadAssets(MobType type) {
        assets.load(type);
        animIdle = assets.idle();
        animWalk = assets.walk();
        animHurt = assets.hurt();
    }

    // ─── Vòng đời ──────────────────────────────────────────────

    @Override
    public void update(float delta) {
        if (!isAlive) return;

        stateTime += delta;
        tickTimers(delta);
        brain.update(this);
        physics.update(this, world, delta);
        updateEntityState();
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch, this, animIdle, animWalk, animHurt);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    void doPatrol() {
        boolean hitWall    = (Math.abs(velocity.x) < 0.01f && state == EntityState.RUN);
        boolean outOfRange = (position.x < patrolOriginX - profile.patrolRange)
            || (position.x > patrolOriginX + profile.patrolRange);
        if (hitWall || outOfRange) facingRight = !facingRight;
        velocity.x = facingRight ? profile.patrolSpeed : -profile.patrolSpeed;
        if (MobMovementHelper.shouldJumpOverObstacle(this, world, facingRight)) {
            velocity.y = JUMP_IMPULSE;
            onGround = false;
        }
    }

    void doChase() {
        boolean playerRight = target.getX() > position.x;
        facingRight = playerRight;
        velocity.x  = playerRight ? profile.chaseSpeed : -profile.chaseSpeed;
        if (MobMovementHelper.shouldJumpOverObstacle(this, world, facingRight)) {
            velocity.y = JUMP_IMPULSE;
            onGround = false;
        }
    }

    void doAttack() {
        if (!MobSightHelper.hasLineOfSight(world, this, target)) {
            aiState = AIState.CHASE;
            return;
        }
        if (attackTimer <= 0f) {
            target.takeDamage(profile.attackDamage);
            attackTimer = profile.attackCooldown;
        }
    }

    // ─── Helpers ───────────────────────────────────────────────

    private void tickTimers(float delta) {
        if (attackTimer > 0) attackTimer -= delta;
        if (hurtTimer  > 0) hurtTimer  -= delta;
    }

    float distanceTo(Entity other) {
        float dx = (other.getX() + other.getWidth() / 2f) - (position.x + width / 2f);
        float dy = (other.getY() + other.getHeight() / 2f) - (position.y + height / 2f);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void updateEntityState() {
        EntityState prev = state;
        if (!isAlive)      { state = EntityState.DEAD; }
        else if (hurtTimer > 0) { state = EntityState.HURT; }
        else if (!onGround){ state = velocity.y > 0 ? EntityState.JUMP : EntityState.FALL; }
        else               { state = Math.abs(velocity.x) > 0.01f ? EntityState.RUN : EntityState.IDLE; }
        if (state != prev) stateTime = 0f;
    }

    // ─── Nhận damage ──────────────────────────────────────────

    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return;
        health -= amount;
        hurtTimer = 0.3f;
        stateTime = 0f;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
        }
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()   { return state;   }
    public AIState     getAIState() { return aiState;  }
    public int         getHealth()  { return health;   }
    public boolean     isHostile()  { return profile.hostile;  }
    float getStateTime() { return stateTime; }

    public void setTarget(Player p)   { this.target  = p;   }
    Player getTarget() { return target; }
    void setAiState(AIState aiState) { this.aiState = aiState; }
    float getAggroRadius() { return profile.aggroRadius; }
    float getDeAggroRadius() { return profile.deAggroRadius; }
    float getAttackRange() { return profile.attackRange; }
}
