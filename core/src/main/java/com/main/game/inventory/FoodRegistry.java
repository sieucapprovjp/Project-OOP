package com.main.game.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FoodRegistry {

    public static final class FoodDefinition {
        private final String itemId;
        private final int nutrition;

        private FoodDefinition(String itemId, int nutrition) {
            this.itemId = itemId;
            this.nutrition = nutrition;
        }

        public String getItemId() {
            return itemId;
        }

        public int getNutrition() {
            return nutrition;
        }
    }

    private static final Map<String, FoodDefinition> FOODS = new LinkedHashMap<>();

    static {
        register("apple", 4);
        register("forest_apple", 4);
        register("golden_apple", 10);
        register("bread", 5);
        register("carrot", 3);
        register("cookie", 2);
        register("la_baguette", 7);
        register("berry_bush3", 2);
        register("raw_beef", 3);
        register("cooked_beef", 8);
        register("raw_pork", 3);
        register("cooked_pork", 8);
        register("raw_mutton", 2);
        register("cooked_mutton", 6);
        register("raw_chicken", 2);
        register("cooked_chicken", 6);
        register("raw_salmon", 2);
        register("cooked_salmon", 6);
        register("rotten_flesh", 4);
    }

    private FoodRegistry() {
    }

    public static boolean isFood(String itemId) {
        return itemId != null && FOODS.containsKey(itemId);
    }

    public static FoodDefinition get(String itemId) {
        return itemId == null ? null : FOODS.get(itemId);
    }

    public static int getNutrition(String itemId) {
        FoodDefinition food = get(itemId);
        return food == null ? 0 : food.getNutrition();
    }

    public static List<String> getFoodItemIds() {
        return Collections.unmodifiableList(new ArrayList<>(FOODS.keySet()));
    }

    private static void register(String itemId, int nutrition) {
        FOODS.put(itemId, new FoodDefinition(itemId, nutrition));
    }
}
