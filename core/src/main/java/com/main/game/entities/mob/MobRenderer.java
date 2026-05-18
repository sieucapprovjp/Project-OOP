package com.main.game.entities.mob;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

final class MobRenderer {

    void render(SpriteBatch batch, Mob mob, Animation<TextureRegion> idleAnim,
                Animation<TextureRegion> walkAnim, Animation<TextureRegion> hurtAnim) {
        if (batch == null || mob == null || !mob.isAlive()) {
            return;
        }

        TextureRegion frame = getCurrentFrame(mob, idleAnim, walkAnim, hurtAnim);
        if (frame == null) {
            return;
        }

        boolean needFlip = (!mob.isFacingRight() && !frame.isFlipX())
            || (mob.isFacingRight() && frame.isFlipX());
        if (needFlip) {
            frame.flip(true, false);
        }

        batch.draw(frame, mob.getX(), mob.getY(), mob.getWidth(), mob.getHeight());
    }

    private TextureRegion getCurrentFrame(Mob mob, Animation<TextureRegion> idleAnim,
                                          Animation<TextureRegion> walkAnim, Animation<TextureRegion> hurtAnim) {
        switch (mob.getState()) {
            case RUN:
                return walkAnim != null ? walkAnim.getKeyFrame(mob.getStateTime()) : null;
            case HURT:
                return hurtAnim != null ? hurtAnim.getKeyFrame(mob.getStateTime()) : null;
            case IDLE:
            default:
                return idleAnim != null ? idleAnim.getKeyFrame(mob.getStateTime()) : null;
        }
    }
}
