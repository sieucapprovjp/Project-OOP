package com.main.game.time;

public final class DayNightCycle {

    public static final float FULL_CYCLE_SECONDS = 600f;
    public static final float GAME_HOUR_SECONDS = FULL_CYCLE_SECONDS / 24f;
    public static final float DEFAULT_START_GAME_HOUR = 6f;
    public static final int MAX_GLOBAL_LIGHT = 12;

    private float elapsedSeconds;
    private float timeReal;

    public DayNightCycle() {
        this(DEFAULT_START_GAME_HOUR);
    }

    public DayNightCycle(float startGameHour) {
        elapsedSeconds = normalizeGameHour(startGameHour) * GAME_HOUR_SECONDS;
        timeReal = elapsedSeconds;
    }

    public void update(float delta) {
        if (delta <= 0f) {
            return;
        }
        elapsedSeconds += delta;
        timeReal = elapsedSeconds;
    }

    public float getGameHour() {
        float hour = (elapsedSeconds / GAME_HOUR_SECONDS) % 24f;
        return hour < 0f ? hour + 24f : hour;
    }

    public int getGlobalLight() {
        float hour = getGameHour();
        if (hour >= 4.5f && hour < 6f) {
            return clampGlobalLight(MAX_GLOBAL_LIGHT - Math.round((hour - 4.5f) * 8f));
        }
        if (hour >= 6f && hour < 18f) {
            return 0;
        }
        if (hour >= 18f && hour < 19.5f) {
            return clampGlobalLight(MAX_GLOBAL_LIGHT - Math.round((19.5f - hour) * 8f));
        }
        return MAX_GLOBAL_LIGHT;
    }

    public float getNightFactor() {
        return getGlobalLight() / (float) MAX_GLOBAL_LIGHT;
    }

    public boolean isNight() {
        float hour = getGameHour();
        return hour < 6f || hour >= 18f;
    }

    public int getDayCount() {
        return (int) Math.floor(elapsedSeconds / FULL_CYCLE_SECONDS) + 1;
    }

    public float getTime() {
        return elapsedSeconds;
    }

    public float getTimeReal() {
        return timeReal;
    }

    private int clampGlobalLight(int value) {
        return Math.max(0, Math.min(MAX_GLOBAL_LIGHT, value));
    }

    private float normalizeGameHour(float gameHour) {
        float normalized = gameHour % 24f;
        return normalized < 0f ? normalized + 24f : normalized;
    }
}
