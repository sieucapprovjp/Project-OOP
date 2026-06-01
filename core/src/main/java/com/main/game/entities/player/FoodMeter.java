package com.main.game.entities.player;

import com.main.game.inventory.FoodRegistry;

public final class FoodMeter {

    public static final int DEFAULT_MAX_FOOD = 20;

    private static final float EXHAUSTION_TO_FOOD = 4f;
    private static final float MOVE_EXHAUSTION_PER_SECOND = 0.10f;
    private static final float JUMP_EXHAUSTION = 0.20f;
    private static final float STARVATION_SECONDS = 4f;
    private static final float REGEN_SECONDS = 4f;

    private final int maxFoodLevel;
    private int foodLevel;
    private float exhaustion;
    private float starvationTimer;
    private float regenerationTimer;

    public FoodMeter() {
        this(DEFAULT_MAX_FOOD);
    }

    public FoodMeter(int maxFoodLevel) {
        this.maxFoodLevel = Math.max(1, maxFoodLevel);
        this.foodLevel = this.maxFoodLevel;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public int getMaxFoodLevel() {
        return maxFoodLevel;
    }

    public boolean canEat(String itemId) {
        return FoodRegistry.isFood(itemId) && foodLevel < maxFoodLevel;
    }

    public boolean eat(String itemId) {
        int nutrition = FoodRegistry.getNutrition(itemId);
        if (nutrition <= 0 || foodLevel >= maxFoodLevel) {
            return false;
        }
        foodLevel = Math.min(maxFoodLevel, foodLevel + nutrition);
        starvationTimer = 0f;
        return true;
    }

    public void addMovementExhaustion(float delta) {
        addExhaustion(MOVE_EXHAUSTION_PER_SECOND * Math.max(0f, delta));
    }

    public void addJumpExhaustion() {
        addExhaustion(JUMP_EXHAUSTION);
    }

    public void addExhaustion(float amount) {
        if (amount <= 0f) {
            return;
        }
        if (foodLevel <= 0) {
            exhaustion = 0f;
            return;
        }
        exhaustion += amount;
        while (foodLevel > 0 && exhaustion >= EXHAUSTION_TO_FOOD) {
            exhaustion -= EXHAUSTION_TO_FOOD;
            foodLevel--;
        }
        if (foodLevel <= 0) {
            exhaustion = 0f;
        }
    }

    public TickResult update(float delta, boolean healthBelowMax) {
        if (delta <= 0f) {
            return TickResult.NONE;
        }

        int damage = 0;
        int healing = 0;
        if (foodLevel <= 0) {
            starvationTimer += delta;
            regenerationTimer = 0f;
            if (starvationTimer >= STARVATION_SECONDS) {
                starvationTimer -= STARVATION_SECONDS;
                damage = 1;
            }
        } else {
            starvationTimer = 0f;
            if (foodLevel >= 18 && healthBelowMax) {
                regenerationTimer += delta;
                if (regenerationTimer >= REGEN_SECONDS) {
                    regenerationTimer -= REGEN_SECONDS;
                    healing = 1;
                    addExhaustion(1f);
                }
            } else {
                regenerationTimer = 0f;
            }
        }
        return damage == 0 && healing == 0 ? TickResult.NONE : new TickResult(healing, damage);
    }

    public void reset() {
        foodLevel = maxFoodLevel;
        exhaustion = 0f;
        starvationTimer = 0f;
        regenerationTimer = 0f;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = Math.max(0, Math.min(maxFoodLevel, foodLevel));
        if (this.foodLevel > 0) {
            starvationTimer = 0f;
        }
    }

    public static final class TickResult {
        private static final TickResult NONE = new TickResult(0, 0);

        private final int healing;
        private final int damage;

        private TickResult(int healing, int damage) {
            this.healing = healing;
            this.damage = damage;
        }

        public int getHealing() {
            return healing;
        }

        public int getDamage() {
            return damage;
        }
    }
}
