package com.main.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.entities.Entity;
import com.main.game.entities.EntityState;
import com.main.game.physics.PhysicsEngine;
import com.main.game.world.World;

/**
 * Class Player - điều khiển bởi người chơi.
 *
 * State machine: IDLE -> RUN -> JUMP -> FALL -> HURT -> DEAD
 * Input: A/D hoặc ←/→ để di chuyển, SPACE/W/↑ để nhảy.
 *
 * Assets dùng:
 *  - IDLE  : mvp/player/idle.png
 *  - RUN   : mvp/player/walk_1.png .. walk_2.png
 *  - JUMP  : mvp/player/jump_0.png
 *  - FALL  : mvp/player/jump_1.png
 *  - HURT  : mvp/player/body2.png   (tạm dùng làm hurt flash)
 *
 * Kết nối PhysicsEngine (Lâm Hùng):
 *  - PhysicsEngine.applyGravity()  được gọi mỗi frame.
 *  - PhysicsEngine.resolveCollision() kiểm tra va chạm block.
 *  - onGround được set bởi PhysicsEngine sau resolve.
 */
public class Player extends Entity {

    // ─── Hằng số di chuyển ─────────────────────────────────────
    public static final float MOVE_SPEED   = 5f;   // tile/s
    public static final float JUMP_IMPULSE = 12f;  // tile/s
    public static final float PLAYER_W    = 0.8f;  // tile
    public static final float PLAYER_H    = 1.8f;  // tile

    private static final float HURT_DURATION   = 0.5f;

    private float stateTime = 0f;

    // ─── State ─────────────────────────────────────────────────
    private EntityState state     = EntityState.IDLE;
    private float       hurtTimer = 0f;
    private float       miningTime = 0f;
    private boolean     mining = false;
    private int         health    = 20;
    private int         maxHealth = 20;
    private boolean     isBanned  = false;

    // ─── Dependency ────────────────────────────────────────────
    private final PhysicsEngine physics;
    private final World world;
    private final PlayerRenderer renderer;

    // ───────────────────────────────────────────────────────────

    public Player(float x, float y, PhysicsEngine physics, World world) {
        super(x, y, PLAYER_W, PLAYER_H);
        this.physics = physics;
        this.world   = world;
        this.renderer = new PlayerRenderer();
    }

    // ─── Vòng đời ──────────────────────────────────────────────
    private float lastVy = 0f;
    private boolean wasOnGround = true;

    @Override
    public void update(float delta) {
        if (!isAlive || isBanned) return;

        stateTime += delta;
        miningTime = mining ? miningTime + delta : 0f;
        handleInput(delta);

        lastVy = velocity.y;
        wasOnGround = onGround;

        physics.update(this, world, delta);

        if (!wasOnGround && onGround && lastVy < -14f) {
            // Rơi quá nhanh -> mất máu
            int fallDamage = (int) (-lastVy - 13f);
            if (fallDamage > 0) takeDamage(fallDamage);
        }

        updateState(delta);
        updateBounds();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive) return;
        renderer.render(batch, this, state, stateTime, mining, miningTime, isHurt());
    }

    public void ban() {
        isBanned = true;
    }

    public boolean isBanned() {
        return isBanned;
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    // ─── Input ─────────────────────────────────────────────────

    private void handleInput(float delta) {
        float moveX = 0;
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX       = -MOVE_SPEED;
            facingRight = false;
        }
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX       = MOVE_SPEED;
            facingRight = true;
        }
        velocity.x = moveX;

        if (onGround &&
            (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Keys.W)
                || Gdx.input.isKeyJustPressed(Keys.UP))) {
            velocity.y = JUMP_IMPULSE;
            onGround   = false;
            stateTime  = 0f; // reset để jump animation bắt đầu từ đầu
        }
    }

    // ─── State machine ─────────────────────────────────────────

    private void updateState(float delta) {
        EntityState prev = state;

        if (hurtTimer > 0) {
            hurtTimer -= delta;
            state = EntityState.HURT;
            if (hurtTimer <= 0) {
                state     = EntityState.IDLE;
                stateTime = 0f;
            }
            return;
        }

        if (!isAlive) {
            state = EntityState.DEAD;
            return;
        }

        if (!onGround) {
            state = velocity.y > 0 ? EntityState.JUMP : EntityState.FALL;
        } else if (Math.abs(velocity.x) > 0.01f) {
            state = EntityState.RUN;
        } else {
            state = EntityState.IDLE;
        }

        // Reset stateTime khi đổi state để animation không bị lệch
        if (state != prev) stateTime = 0f;
    }

    // ─── Damage / Health ───────────────────────────────────────

    public void takeDamage(int amount) {
        if (hurtTimer > 0 || !isAlive) return;
        health -= amount;
        if (health <= 0) {
            health  = 0;
            isAlive = false;
            state   = EntityState.DEAD;
        } else {
            hurtTimer = HURT_DURATION;
            state     = EntityState.HURT;
            stateTime = 0f;
        }
    }

    public void kill() {
        health = 0;
        isAlive = false;
        state = EntityState.DEAD;
        hurtTimer = 0f;
    }

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()     { return state;      }
    public int         getHealth()    { return health;     }
    public int         getMaxHealth() { return maxHealth;  }
    public boolean     isHurt()       { return hurtTimer > 0; }

    public void setMining(boolean mining, float targetX) {
        this.mining = mining;
        if (mining) {
            this.facingRight = targetX >= position.x + width / 2f;
        }
    }

    public void respawn(float x, float y) {
        this.position.set(x, y);
        this.velocity.set(0, 0);
        this.health = maxHealth;
        this.isAlive = true;
        this.state = EntityState.IDLE;
        this.hurtTimer = 0;
        this.stateTime = 0;
        this.mining = false;
        this.miningTime = 0;
    }
}
