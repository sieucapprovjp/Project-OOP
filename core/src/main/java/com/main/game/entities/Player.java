package com.main.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private static final String PLAYER_ASSET_ROOT = "images";

    // Idle calibration constants (tile units)
    private static final float BODY_X = 0.23f;
    private static final float BODY_Y = 0.54f;
    private static final float BODY_W = 0.50f;
    private static final float BODY_H = 0.80f;

    private static final float HEAD_X = 0.24f;
    private static final float HEAD_Y = 1.18f;
    private static final float HEAD_W = 0.42f;
    private static final float HEAD_H = 0.52f;

    private static final float ARM_BACK_X = 0.01f;
    private static final float ARM_FRONT_X = 0.57f;
    private static final float ARM_Y = 1.03f;
    private static final float ARM_W = 0.50f;
    private static final float ARM_H = 0.22f;

    private static final float LEG_BACK_X = 0.18f;
    private static final float LEG_FRONT_X = 0.48f;
    private static final float LEG_Y = 0.18f;
    private static final float LEG_W = 0.24f;
    private static final float LEG_H = 0.90f;
    private static final float LEG_PIVOT_X = 0.12f;
    private static final float LEG_PIVOT_Y = 0.78f;

    private static final float BOOT_Y = 0.02f;
    private static final float BOOT_H = 0.26f;

    public enum PlayerState {
        IDLE,
        RUN,
        JUMP,
        FALL
    }

    private final TextureRegion bodyLeftTexture;
    private final TextureRegion bodyRightTexture;
    private final TextureRegion headRightTexture;
    private final TextureRegion headLeftTexture;
    private final TextureRegion armRightFacingRightTexture;
    private final TextureRegion armRightFacingLeftTexture;
    private final TextureRegion armLeftFacingRightTexture;
    private final TextureRegion armLeftFacingLeftTexture;
    private final TextureRegion legLeftTexture;
    private final TextureRegion legRightTexture;
    private final TextureRegion bootsTexture;
    private final List<Texture> ownedTextures;

    private PlayerState state;
    private float walkTimer;
    private float rigPhase;
    private float armFrontAngle;
    private float armBackAngle;
    private float legFrontAngle;
    private float legBackAngle;
    private float bodyAngle;
    private float headOffsetY;
    private float groundedGrace;

    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
        ownedTextures = new ArrayList<>();

        bodyLeftTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve/body4.png");
        bodyRightTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve/body4_1.png");

        headRightTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steves_head/right_1.png");
        headLeftTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steves_head/left_1.png");

        armRightFacingRightTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_arm/arm4_1.png");
        armRightFacingLeftTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_arm/arm2_1.png");
        armLeftFacingRightTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_arm/arm2_1.png");
        armLeftFacingLeftTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_arm/arm4_1.png");

        legLeftTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_legs/leg.png");
        legRightTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_legs/leg_1.png");

        bootsTexture = loadTrimmedRegion(PLAYER_ASSET_ROOT + "/steve_boots/leg_1.png");
        state = PlayerState.IDLE;
        walkTimer = 0f;
        rigPhase = 0f;
        bodyAngle = 0f;
        headOffsetY = 0f;
        groundedGrace = 0f;
    }

    @Override
    public void update(float delta) {
        float horizontal = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontal -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontal += 1f;
        }

        velocity.x = horizontal * Constants.PLAYER_SPEED;
        if (horizontal < 0f) {
            facingRight = false;
        } else if (horizontal > 0f) {
            facingRight = true;
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && onGround) {
            velocity.y = Constants.PLAYER_JUMP_FORCE;
            onGround = false;
        }

        if (onGround) {
            groundedGrace = 0.08f;
        } else {
            groundedGrace = Math.max(0f, groundedGrace - delta);
        }

        if (!onGround && groundedGrace <= 0f) {
            if (velocity.y > 0f) {
                state = PlayerState.JUMP;
            } else {
                state = PlayerState.FALL;
            }
        } else if (Math.abs(velocity.x) > 0.001f) {
            state = PlayerState.RUN;
        } else {
            state = PlayerState.IDLE;
        }

        if (state == PlayerState.RUN) {
            walkTimer += delta;
        } else {
            walkTimer = 0f;
        }

        updateRigPose(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion bodyFrame = selectBodyFrame();
        TextureRegion headFrame = facingRight ? headRightTexture : headLeftTexture;
        TextureRegion armFrontFrame = facingRight ? armRightFacingRightTexture : armLeftFacingLeftTexture;
        TextureRegion armBackFrame = facingRight ? armLeftFacingRightTexture : armRightFacingLeftTexture;
        TextureRegion legFrontFrame = facingRight ? legRightTexture : legLeftTexture;
        TextureRegion legBackFrame = facingRight ? legLeftTexture : legRightTexture;

        float baseX = position.x;
        float baseY = position.y;
        boolean drawBackLimbs = state == PlayerState.RUN;

        // Layer order: [optional back limbs] -> body -> head -> front leg+boot -> front arm
        if (drawBackLimbs) {
            drawLegWithBoot(batch, legBackFrame, baseX, baseY, LEG_BACK_X, legBackAngle);
            drawRigPart(batch, armBackFrame, baseX, baseY, ARM_BACK_X, ARM_Y, ARM_W, ARM_H, 0.10f, 0.11f, armBackAngle);
        }

        drawStaticPartRotated(batch, bodyFrame, baseX, baseY, BODY_X, BODY_Y, BODY_W, BODY_H, 0.25f, 0.60f, bodyAngle);
        drawStaticPart(batch, headFrame, baseX, baseY, HEAD_X, HEAD_Y + headOffsetY, HEAD_W, HEAD_H);

        drawLegWithBoot(batch, legFrontFrame, baseX, baseY, LEG_FRONT_X, legFrontAngle);

        drawRigPart(batch, armFrontFrame, baseX, baseY, ARM_FRONT_X, ARM_Y, ARM_W, ARM_H, 0.10f, 0.11f, armFrontAngle);
    }

    private void drawLegWithBoot(SpriteBatch batch, TextureRegion legFrame,
                                 float baseX, float baseY,
                                 float legLocalX, float legAngle) {
        drawRigPart(batch, legFrame, baseX, baseY, legLocalX, LEG_Y, LEG_W, LEG_H, LEG_PIVOT_X, LEG_PIVOT_Y, legAngle);

        float bootPivotY = (LEG_Y + LEG_PIVOT_Y) - BOOT_Y;
        drawRigPart(batch, bootsTexture, baseX, baseY, legLocalX, BOOT_Y, LEG_W, BOOT_H, LEG_PIVOT_X, bootPivotY, legAngle);
    }

    private void drawStaticPart(SpriteBatch batch, TextureRegion texture,
                                float baseX, float baseY,
                                float localX, float localY,
                                float partW, float partH) {
        float drawX = facingRight
            ? baseX + localX
            : baseX + (width - localX - partW);
        batch.draw(texture, snapPos(drawX), snapPos(baseY + localY), partW, partH);
    }

    private void drawRigPart(SpriteBatch batch, TextureRegion texture,
                             float baseX, float baseY,
                             float localX, float localY,
                             float partW, float partH,
                             float pivotX, float pivotY,
                             float angle) {
        float drawX = facingRight
            ? baseX + localX
            : baseX + (width - localX - partW);

        float originX = facingRight ? pivotX : (partW - pivotX);
        float rotation = facingRight ? angle : -angle;

        batch.draw(
            texture,
            snapPos(drawX),
            snapPos(baseY + localY),
            originX,
            pivotY,
            partW,
            partH,
            1f,
            1f,
            rotation
        );
    }

    private void drawStaticPartRotated(SpriteBatch batch, TextureRegion texture,
                                       float baseX, float baseY,
                                       float localX, float localY,
                                       float partW, float partH,
                                       float pivotX, float pivotY,
                                       float angle) {
        float drawX = facingRight
            ? baseX + localX
            : baseX + (width - localX - partW);
        float originX = facingRight ? pivotX : (partW - pivotX);
        float rotation = facingRight ? angle : -angle;

        batch.draw(
            texture,
            snapPos(drawX),
            snapPos(baseY + localY),
            originX,
            pivotY,
            partW,
            partH,
            1f,
            1f,
            rotation
        );
    }

    private void updateRigPose(float delta) {
        float speedNorm = Math.min(1f, Math.abs(velocity.x) / Constants.PLAYER_SPEED);

        if (state == PlayerState.RUN && onGround && speedNorm > 0.01f) {
            float frequency = 8f + speedNorm * 6f;
            rigPhase += delta * frequency;

            // Pixel-style run cycle: stepped poses thay vì sin mượt.
            final float[] armCycle = { -20f, -6f, 20f, 6f };
            final float[] legCycle = { 30f, 10f, -30f, -10f };
            int frame = ((int) rigPhase) & 3;

            armFrontAngle = armCycle[frame];
            armBackAngle = -armFrontAngle;
            legFrontAngle = legCycle[frame];
            legBackAngle = -legFrontAngle;
            bodyAngle = snapAngle(-7f * speedNorm, 1f);
            headOffsetY = (frame == 1 || frame == 3) ? -0.01f : 0f;
            return;
        }

        if (state == PlayerState.JUMP || state == PlayerState.FALL) {
            // One fixed jump pose theo reference ảnh: tay hạ, chân dạng chữ V.
            armFrontAngle = -66f;
            armBackAngle = -95f;
            legFrontAngle = 34f;
            legBackAngle = -34f;
            bodyAngle = -6f;
            headOffsetY = 0f;
            return;
        }

        // Idle đứng yên hoàn toàn.
        armFrontAngle = -88f;
        armBackAngle = -92f;
        legFrontAngle = 0f;
        legBackAngle = 0f;
        bodyAngle = 0f;
        headOffsetY = 0f;
    }

    private float snapAngle(float value, float step) {
        return Math.round(value / step) * step;
    }

    private float snapValue(float value, float step) {
        return Math.round(value / step) * step;
    }

    private TextureRegion selectBodyFrame() {
        return facingRight ? bodyRightTexture : bodyLeftTexture;
    }

    private TextureRegion loadTrimmedRegion(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        ownedTextures.add(texture);
        return new TextureRegion(texture);
    }

    private float snapPos(float value) {
        return Math.round(value * Constants.TILE_SIZE) / (float) Constants.TILE_SIZE;
    }

    public PlayerState getState() {
        return state;
    }

    @Override
    public void dispose() {
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
        ownedTextures.clear();
    }
}
