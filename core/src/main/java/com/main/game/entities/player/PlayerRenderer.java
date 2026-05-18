package com.main.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.entities.EntityState;

class PlayerRenderer {

    private static final float MINING_ARM_SPEED = 11f;

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

    PlayerRenderer() {
        loadAssets();
    }

    void render(SpriteBatch batch, Player player, EntityState state, float stateTime, boolean mining, float miningTime, boolean hurt) {
        float armFrontAngle = 0f;
        float armBackAngle = 0f;
        float legFrontAngle = 0f;
        float legBackAngle = 0f;
        float headTilt = 0f;

        if (state == EntityState.RUN) {
            int walkFrame = (int) (stateTime * 15f);
            int scratchWalkFrame = Math.abs((walkFrame % 12) - 5);
            float mappedAngle = (scratchWalkFrame - 3) * 15f;

            armFrontAngle = mappedAngle;
            armBackAngle = -mappedAngle;
            legFrontAngle = -mappedAngle;
            legBackAngle = mappedAngle;
            headTilt = 5f;
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
            armFrontAngle = 0f;
            armBackAngle = 0f;
            legFrontAngle = 0f;
            legBackAngle = 0f;
            headTilt = 0f;
        }

        if (mining && state != EntityState.HURT && state != EntityState.DEAD) {
            float swing = Math.abs(((miningTime * MINING_ARM_SPEED) % 2f) - 1f);
            armFrontAngle = 115f + swing * 65f;
            armBackAngle = 5f;
        }

        if (hurt) {
            batch.setColor(1f, 0.5f, 0.5f, 1f);
        }

        TextureRegion head = regHeadR;
        TextureRegion body = regBodyR;
        TextureRegion armFront = regArmR;
        TextureRegion armBack = regArmL;
        TextureRegion legFront = regLegR;
        TextureRegion legBack = regLegL;
        TextureRegion bootFront = regBootR;
        TextureRegion bootBack = regBootL;

        float px = player.getX();
        float py = player.getY();
        float cx = px + player.getWidth() / 2f;

        float headW = 0.5f;
        float headH = 0.5f;
        float bodyW = 0.4f;
        float bodyH = 0.6f;
        float armW = 0.2f;
        float armH = 0.6f;
        float legW = 0.2f;
        float legH = 0.5f;
        float bootW = 0.22f;
        float bootH = 0.2f;

        float maxLegAngle = Math.max(Math.abs(legFrontAngle), Math.abs(legBackAngle));
        float totalLegH = legH + bootH;
        float hipY = py + totalLegH * (float) Math.cos(Math.toRadians(maxLegAngle));

        float legY = hipY - totalLegH + bootH;
        float bodyY = hipY;
        float headY = bodyY + bodyH;
        float armY = bodyY + bodyH - 0.1f;

        float scaleX = player.isFacingRight() ? 1f : -1f;
        float angleSign = player.isFacingRight() ? 1f : -1f;
        float armFrontRot = armFrontAngle * angleSign;
        float armBackRot = armBackAngle * angleSign;
        float legFrontRot = legFrontAngle * angleSign;
        float legBackRot = legBackAngle * angleSign;
        float headRot = headTilt * angleSign;

        batch.draw(armBack, cx - armW / 2f, armY - armH, armW / 2f, armH, armW, armH, scaleX, 1f, armBackRot);
        batch.draw(bootBack, cx - bootW / 2f, legY - bootH, bootW / 2f, legH + bootH, bootW, bootH, scaleX, 1f, legBackRot);
        batch.draw(legBack, cx - legW / 2f, legY, legW / 2f, legH, legW, legH, scaleX, 1f, legBackRot);
        batch.draw(body, cx - bodyW / 2f, bodyY, bodyW / 2f, 0f, bodyW, bodyH, scaleX, 1f, 0f);
        batch.draw(head, cx - headW / 2f, headY, headW / 2f, 0f, headW, headH, scaleX, 1f, headRot);
        batch.draw(bootFront, cx - bootW / 2f, legY - bootH, bootW / 2f, legH + bootH, bootW, bootH, scaleX, 1f, legFrontRot);
        batch.draw(legFront, cx - legW / 2f, legY, legW / 2f, legH, legW, legH, scaleX, 1f, legFrontRot);
        batch.draw(armFront, cx - armW / 2f, armY - armH, armW / 2f, armH, armW, armH, scaleX, 1f, armFrontRot);

        batch.setColor(Color.WHITE);
    }

    void dispose() {
        tBodyL.dispose();
        tBodyR.dispose();
        tArmL.dispose();
        tArmR.dispose();
        tLegL.dispose();
        tLegR.dispose();
        tHeadR.dispose();
        tHeadL.dispose();
        tBootL.dispose();
        tBootR.dispose();
    }

    private void loadAssets() {
        tBodyL = new Texture(Gdx.files.internal("mvp/player/body4.png"));
        tBodyR = new Texture(Gdx.files.internal("mvp/player/body4.png"));
        tArmL = new Texture(Gdx.files.internal("mvp/player/arm4.png"));
        tArmR = new Texture(Gdx.files.internal("mvp/player/arm4.png"));
        tLegL = new Texture(Gdx.files.internal("mvp/player/leg.png"));
        tLegR = new Texture(Gdx.files.internal("mvp/player/leg.png"));
        tHeadR = new Texture(Gdx.files.internal("mvp/player/right.png"));
        tHeadL = new Texture(Gdx.files.internal("mvp/player/left.png"));
        tBootL = new Texture(Gdx.files.internal("mvp/player/boot.png"));
        tBootR = new Texture(Gdx.files.internal("mvp/player/boot1.png"));

        regBodyL = new TextureRegion(tBodyL);
        regBodyR = new TextureRegion(tBodyR);
        regArmL = new TextureRegion(tArmL);
        regArmR = new TextureRegion(tArmR);
        regLegL = new TextureRegion(tLegL);
        regLegR = new TextureRegion(tLegR);
        regHeadR = new TextureRegion(tHeadR);
        regHeadL = new TextureRegion(tHeadL);
        regBootL = new TextureRegion(tBootL);
        regBootR = new TextureRegion(tBootR);
    }
}
