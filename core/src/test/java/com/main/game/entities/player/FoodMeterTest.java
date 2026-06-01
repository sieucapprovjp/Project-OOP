package com.main.game.entities.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FoodMeterTest {

    @Test
    public void eatingFoodRestoresHungerAndClampsToMax() {
        FoodMeter meter = new FoodMeter();
        meter.setFoodLevel(10);

        assertTrue(meter.eat("cooked_beef"));
        assertEquals(18, meter.getFoodLevel());
        assertTrue(meter.eat("golden_apple"));
        assertEquals(20, meter.getFoodLevel());
        assertFalse(meter.eat("apple"));
        assertFalse(meter.eat("bone"));
    }

    @Test
    public void exhaustionConsumesFoodLevel() {
        FoodMeter meter = new FoodMeter();

        meter.addExhaustion(3.9f);
        assertEquals(20, meter.getFoodLevel());
        meter.addExhaustion(0.1f);

        assertEquals(19, meter.getFoodLevel());
    }

    @Test
    public void foodLevelControlsStarvationAndRegenerationTicks() {
        FoodMeter meter = new FoodMeter();
        meter.setFoodLevel(0);

        FoodMeter.TickResult starvation = meter.update(4f, true);
        assertEquals(1, starvation.getDamage());
        assertEquals(0, starvation.getHealing());

        meter.setFoodLevel(18);
        FoodMeter.TickResult regeneration = meter.update(4f, true);
        assertEquals(0, regeneration.getDamage());
        assertEquals(1, regeneration.getHealing());
    }
}
