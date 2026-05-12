package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private static final float WALK_FRAME_DUR  = 0.15f; // giây/frame
    private static final float HURT_FLASH_DUR  = 0.08f; // giây/frame blink

    // ─── Rig Textures ──────────────────────────────────────────
    private Texture tBodyL, tBodyR;
    private Texture tArmL, tArmR;
    private Texture tLegL, tLegR;
    private Texture tHeadR, tHeadL;
    private Texture tBootL, tBootR;

    private TextureRegion regBodyL, regBodyR;
    private TextureRegion regArmL, regArmR;
    private TextureRegion regLegL, regLegR;
    private TextureRegion regHeadR, regHeadL;
    private TextureRegion regBootL, regBootR;

    private float stateTime = 0f;

    // ─── State ─────────────────────────────────────────────────
    private EntityState state     = EntityState.IDLE;
    private float       hurtTimer = 0f;
    private int         health    = 20;
    private int         maxHealth = 20;
    private boolean     isBanned  = false;

    // ─── Dependency ────────────────────────────────────────────
    private final PhysicsEngine physics;
    private final World world;

    // ───────────────────────────────────────────────────────────

    public Player(float x, float y, PhysicsEngine physics, World world) {
        super(x, y, PLAYER_W, PLAYER_H);
        this.physics = physics;
        this.world   = world;
        loadAssets();
    }

    // ─── Asset loading ─────────────────────────────────────────

    private void loadAssets() {
        tBodyL = new Texture(Gdx.files.internal("mvp/player/body4.png"));
        tBodyR = new Texture(Gdx.files.internal("mvp/player/body4_1.png"));
        tArmL  = new Texture(Gdx.files.internal("mvp/player/arm4.png"));
        tArmR  = new Texture(Gdx.files.internal("mvp/player/arm4_1.png"));
        tLegL  = new Texture(Gdx.files.internal("mvp/player/leg.png"));
        tLegR  = new Texture(Gdx.files.internal("mvp/player/leg_1.png"));
        tHeadR = new Texture(Gdx.files.internal("mvp/player/right.png"));
        tHeadL = new Texture(Gdx.files.internal("mvp/player/right_1.png"));
        tBootL = new Texture(Gdx.files.internal("mvp/player/boot.png"));
        tBootR = new Texture(Gdx.files.internal("mvp/player/boot1.png"));

        regBodyL = new TextureRegion(tBodyL);
        regBodyR = new TextureRegion(tBodyR);
        regArmL  = new TextureRegion(tArmL);
        regArmR  = new TextureRegion(tArmR);
        regLegL  = new TextureRegion(tLegL);
        regLegR  = new TextureRegion(tLegR);
        regHeadR = new TextureRegion(tHeadR);
        regHeadL = new TextureRegion(tHeadL);
        regBootL = new TextureRegion(tBootL);
        regBootR = new TextureRegion(tBootR);
    }

    // ─── Vòng đời ──────────────────────────────────────────────
    private float lastVy = 0f;
    private boolean wasOnGround = true;

    @Override
    public void update(float delta) {
        if (!isAlive || isBanned) return;

        stateTime += delta;
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

        float armFrontAngle = 0f;
        float armBackAngle = 0f;
        float legFrontAngle = 0f;
        float legBackAngle = 0f;
        float headTilt = 0f;

        if (state == EntityState.RUN) {
            // Áp dụng logic đếm frame chính xác từ game gốc (Scratch)
            // SteveNextWalkFrame = Math.abs(((Math.floor("walkFrame") % 12) - 5))
            int walkFrame = (int) (stateTime * 15f);
            int scratchWalkFrame = Math.abs((walkFrame % 12) - 5);
            
            // scratchWalkFrame chạy trong khoảng [0, 6] theo dạng sóng tam giác
            // Ta map giá trị này sang góc xoay (bước nhảy 15 độ giống _WalkFrame * 15)
            float mappedAngle = (scratchWalkFrame - 3) * 15f;
            
            armFrontAngle = mappedAngle;
            armBackAngle = -mappedAngle;
            legFrontAngle = -mappedAngle;
            legBackAngle = mappedAngle;
            
            // Đầu ngẩng lên một chút khi chạy
            headTilt = 5f * (facingRight ? 1f : -1f);
        } else if (state == EntityState.JUMP) {
            armFrontAngle = 160f;
            armBackAngle = -20f;
            legFrontAngle = -20f;
            legBackAngle = 20f;
        } else if (state == EntityState.FALL) {
            armFrontAngle = 160f;
            armBackAngle = 20f;
            legFrontAngle = 10f;
            legBackAngle = -10f;
        } else if (state == EntityState.IDLE) {
            // Đứng yên hoàn toàn 0 độ, tay chân trùng nhau (giống ảnh 1)
            armFrontAngle = 0f;
            armBackAngle = 0f;
            legFrontAngle = 0f;
            legBackAngle = 0f;
            headTilt = 0f;
        }

        if (isHurt()) {
            batch.setColor(1f, 0.5f, 0.5f, 1f);
        }

        TextureRegion head = facingRight ? regHeadR : regHeadL;
        TextureRegion body = facingRight ? regBodyR : regBodyL;
        TextureRegion armFront = facingRight ? regArmR : regArmL;
        TextureRegion armBack = facingRight ? regArmL : regArmR;
        TextureRegion legFront = facingRight ? regLegR : regLegL;
        TextureRegion legBack = facingRight ? regLegL : regLegR;
        TextureRegion bootFront = facingRight ? regBootR : regBootL;
        TextureRegion bootBack = facingRight ? regBootL : regBootR;

        float px = position.x;
        float py = position.y;
        float cx = px + width / 2f;

        float headW = 0.5f, headH = 0.5f;
        float bodyW = 0.4f, bodyH = 0.6f;
        float armW  = 0.2f, armH  = 0.6f; // Sửa armH = 0.6 (ngang với thân) để đúng chuẩn Minecraft
        float legW  = 0.2f, legH  = 0.5f;
        float bootW = 0.22f, bootH = 0.2f;

        // Tính độ nhún (bobbing) khi chân xoạc ra
        float maxLegAngle = Math.max(Math.abs(legFrontAngle), Math.abs(legBackAngle));
        float totalLegH = legH + bootH;
        // hông (hip) hạ xuống theo hàm cos để bàn chân bám sát mặt đất
        float hipY = py + totalLegH * (float) Math.cos(Math.toRadians(maxLegAngle));

        float legY = hipY - totalLegH + bootH; 
        float bodyY = hipY;
        float headY = bodyY + bodyH;
        float armY = bodyY + bodyH - 0.1f;

        // Xếp theo order từ trong ra ngoài (back -> front)
        // Draw Back Arm
        batch.draw(armBack, cx - armW/2f, armY - armH, armW/2f, armH, armW, armH, 1f, 1f, armBackAngle);
        // Draw Back Boot
        batch.draw(bootBack, cx - bootW/2f, legY - bootH, bootW/2f, legH + bootH, bootW, bootH, 1f, 1f, legBackAngle);
        // Draw Back Leg
        batch.draw(legBack, cx - legW/2f, legY, legW/2f, legH, legW, legH, 1f, 1f, legBackAngle);
        // Draw Body
        batch.draw(body, cx - bodyW/2f, bodyY, bodyW, bodyH);
        // Draw Head (thêm headTilt)
        batch.draw(head, cx - headW/2f, headY, headW/2f, 0f, headW, headH, 1f, 1f, headTilt);
        // Draw Front Boot
        batch.draw(bootFront, cx - bootW/2f, legY - bootH, bootW/2f, legH + bootH, bootW, bootH, 1f, 1f, legFrontAngle);
        // Draw Front Leg
        batch.draw(legFront, cx - legW/2f, legY, legW/2f, legH, legW, legH, 1f, 1f, legFrontAngle);
        // Draw Front Arm
        batch.draw(armFront, cx - armW/2f, armY - armH, armW/2f, armH, armW, armH, 1f, 1f, armFrontAngle);

        batch.setColor(1f, 1f, 1f, 1f); // reset
    }

    public void ban() {
        isBanned = true;
    }

    public boolean isBanned() {
        return isBanned;
    }

    @Override
    public void dispose() {
        tBodyL.dispose(); tBodyR.dispose();
        tArmL.dispose(); tArmR.dispose();
        tLegL.dispose(); tLegR.dispose();
        tHeadR.dispose(); tHeadL.dispose();
        tBootL.dispose(); tBootR.dispose();
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

    // ─── Getters ───────────────────────────────────────────────

    public EntityState getState()     { return state;      }
    public int         getHealth()    { return health;     }
    public int         getMaxHealth() { return maxHealth;  }
    public boolean     isHurt()       { return hurtTimer > 0; }

    public void respawn(float x, float y) {
        this.position.set(x, y);
        this.velocity.set(0, 0);
        this.health = maxHealth;
        this.isAlive = true;
        this.state = EntityState.IDLE;
        this.hurtTimer = 0;
        this.stateTime = 0;
    }
}
