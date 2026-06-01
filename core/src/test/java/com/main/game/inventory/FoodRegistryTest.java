package com.main.game.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FoodRegistryTest {

    @Test
    public void rawAndCookedFoodsHaveNutritionValues() {
        assertEquals(4, FoodRegistry.getNutrition("apple"));
        assertEquals(8, FoodRegistry.getNutrition("cooked_beef"));
        assertEquals(8, FoodRegistry.getNutrition("cooked_pork"));
        assertEquals(6, FoodRegistry.getNutrition("cooked_mutton"));
        assertEquals(6, FoodRegistry.getNutrition("cooked_chicken"));
        assertEquals(6, FoodRegistry.getNutrition("cooked_salmon"));
        assertEquals(4, FoodRegistry.getNutrition("rotten_flesh"));
    }

    @Test
    public void nonFoodItemsAreRejected() {
        assertTrue(FoodRegistry.isFood("raw_beef"));
        assertTrue(FoodRegistry.isFood("bread"));
        assertFalse(FoodRegistry.isFood("bone"));
        assertFalse(FoodRegistry.isFood("bonemeal"));
        assertFalse(FoodRegistry.isFood("apple_in_tree"));
        assertEquals(0, FoodRegistry.getNutrition("netherite_sword"));
    }
}
