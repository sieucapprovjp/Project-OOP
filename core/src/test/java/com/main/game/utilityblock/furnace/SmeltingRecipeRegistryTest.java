package com.main.game.utilityblock.furnace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SmeltingRecipeRegistryTest {

    @Test
    public void rawMeatSmeltsIntoCookedFood() {
        assertEquals("cooked_beef", SmeltingRecipeRegistry.getOutput("raw_beef"));
        assertEquals("cooked_mutton", SmeltingRecipeRegistry.getOutput("raw_mutton"));
        assertEquals("cooked_chicken", SmeltingRecipeRegistry.getOutput("raw_chicken"));
        assertEquals("cooked_pork", SmeltingRecipeRegistry.getOutput("raw_pork"));
    }

    @Test
    public void unsupportedFoodDoesNotSmelt() {
        assertNull(SmeltingRecipeRegistry.getOutput("rotten_flesh"));
        assertNull(SmeltingRecipeRegistry.getOutput("apple"));
    }
}
