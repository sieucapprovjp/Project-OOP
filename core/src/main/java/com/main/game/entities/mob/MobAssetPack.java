package com.main.game.entities.mob;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

final class MobAssetPack {

    private final List<Texture> loadedTextures = new ArrayList<>();
    private Animation<TextureRegion> idleAnim;
    private Animation<TextureRegion> walkAnim;
    private Animation<TextureRegion> hurtAnim;

    void load(Mob.MobType type) {
        switch (type) {
            case HUSK:
                idleAnim = single("mobs/husk/mobs/husk_face.png");
                walkAnim = sequenceWithFallback(0.11f,
                    "mobs/husk/mobs/husk_walk_2.png",
                    "mobs/husk/mobs/husk_walk_3.png",
                    "mobs/husk/mobs/husk_walk_4.png",
                    "mobs/husk/mobs/husk_walk_5.png",
                    "mobs/husk/mobs/husk_walk_6.png",
                    "mobs/husk/mobs/husk_walk_7.png",
                    "mobs/husk/mobs/husk_walk_8.png");
                hurtAnim = single("mobs/husk/mobs/husk_face.png");
                break;
            case SKELETON:
                idleAnim = single("mobs/skeleton/mobs/skeletonface.png");
                walkAnim = sequenceWithFallback(0.11f,
                    "mobs/skeleton/mobs/skeleton1.png",
                    "mobs/skeleton/mobs/skeleton2.png",
                    "mobs/skeleton/mobs/skeleton3.png",
                    "mobs/skeleton/mobs/skeleton4.png");
                hurtAnim = single("mobs/skeleton/mobs/skeletonfacehurt.png");
                break;
            case STRAY:
                idleAnim = single("mobs/skeleton/mobs/skeletonface.png");
                walkAnim = sequenceWithFallback(0.12f,
                    "mobs/skeleton/mobs/skeleton1.png",
                    "mobs/skeleton/mobs/skeleton2.png",
                    "mobs/skeleton/mobs/skeleton3.png",
                    "mobs/skeleton/mobs/skeleton4.png");
                hurtAnim = single("mobs/skeleton/mobs/skeletonfacehurt.png");
                break;
            case PIG:
                idleAnim = single("mobs/pig/mobs/piglook3.png");
                walkAnim = sequenceWithFallback(0.11f,
                    "mobs/pig/mobs/pig8.png",
                    "mobs/pig/mobs/pig9.png",
                    "mobs/pig/mobs/pig13.png",
                    "mobs/pig/mobs/pig14.png");
                hurtAnim = single("mobs/pig/mobs/pigdamage.png");
                break;
            case SHEEP:
                idleAnim = single("mobs/sheep/mobs/sheep_face.png");
                walkAnim = sequenceWithFallback(0.11f,
                    "mobs/sheep/mobs/sheep_2.png",
                    "mobs/sheep/mobs/sheep_3.png",
                    "mobs/sheep/mobs/sheep_4.png");
                hurtAnim = single("mobs/sheep/mobs/sheep_hurt.png");
                break;
            case CHICKEN:
                idleAnim = single("mobs/chicken/mobs/chickenface.png");
                walkAnim = sequenceWithFallback(0.10f,
                    "mobs/chicken/mobs/chicken1.png",
                    "mobs/chicken/mobs/chicken2.png",
                    "mobs/chicken/mobs/chicken3.png",
                    "mobs/chicken/mobs/chicken4.png",
                    "mobs/chicken/mobs/chicken5.png");
                hurtAnim = single("mobs/chicken/mobs/chickenface.png");
                break;
            case ZOMBIE:
            default:
                idleAnim = single("mobs/zombie/mobs/zombielook.png");
                walkAnim = sequence("mobs/zombie/mobs/zombie%d.png", 4, 7, 0.11f);
                hurtAnim = single("mobs/zombie/mobs/zombievillager-hurt.png");
                break;
        }

        if (idleAnim == null) idleAnim = single("mvp/mob/cow/cow_look.png");
        if (walkAnim == null) walkAnim = sequence("mvp/mob/cow/cow_walk_%d.png", 1, 6, 0.12f);
        if (hurtAnim == null) hurtAnim = single("mvp/mob/cow/cow_hurt.png");
    }

    Animation<TextureRegion> idle() {
        return idleAnim;
    }

    Animation<TextureRegion> walk() {
        return walkAnim;
    }

    Animation<TextureRegion> hurt() {
        return hurtAnim;
    }

    void dispose() {
        for (Texture t : loadedTextures) {
            if (t != null) t.dispose();
        }
        loadedTextures.clear();
    }

    private Animation<TextureRegion> single(String path) {
        Texture texture = loadTexture(path);
        if (texture == null) return null;
        Animation<TextureRegion> anim = new Animation<>(0.6f, new TextureRegion(texture));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private Animation<TextureRegion> sequence(String pathPattern, int start, int endInclusive, float frameDur) {
        TextureRegion[] frames = new TextureRegion[endInclusive - start + 1];
        int count = 0;
        for (int i = start; i <= endInclusive; i++) {
            String path = String.format(pathPattern, i);
            Texture t = loadTexture(path);
            if (t != null) {
                frames[count++] = new TextureRegion(t);
            }
        }
        if (count == 0) return null;
        TextureRegion[] trimmed = new TextureRegion[count];
        System.arraycopy(frames, 0, trimmed, 0, count);
        Animation<TextureRegion> anim = new Animation<>(frameDur, trimmed);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private Animation<TextureRegion> sequenceWithFallback(float frameDur, String... paths) {
        TextureRegion[] frames = new TextureRegion[paths.length];
        int count = 0;
        for (String path : paths) {
            Texture t = loadTexture(path);
            if (t != null) {
                frames[count++] = new TextureRegion(t);
            }
        }
        if (count == 0) return null;
        TextureRegion[] trimmed = new TextureRegion[count];
        System.arraycopy(frames, 0, trimmed, 0, count);
        Animation<TextureRegion> anim = new Animation<>(frameDur, trimmed);
        anim.setPlayMode(Animation.PlayMode.LOOP);
        return anim;
    }

    private Texture loadTexture(String path) {
        if (!Gdx.files.internal(path).exists()) {
            return null;
        }
        Texture t = new Texture(Gdx.files.internal(path));
        loadedTextures.add(t);
        return t;
    }
}
