package com.main.game.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DayNightCycleTest {

    @Test
    public void defaultStartIsMorning() {
        DayNightCycle cycle = new DayNightCycle();

        assertEquals(6f, cycle.getGameHour(), 0.001f);
        assertFalse(cycle.isNight());
        assertEquals(0, cycle.getGlobalLight());
    }

    @Test
    public void gameHourWrapsEveryTenMinutes() {
        DayNightCycle cycle = new DayNightCycle(0f);

        assertEquals(0f, cycle.getGameHour(), 0.001f);
        cycle.update(150f);
        assertEquals(6f, cycle.getGameHour(), 0.001f);
        cycle.update(150f);
        assertEquals(12f, cycle.getGameHour(), 0.001f);
        cycle.update(150f);
        assertEquals(18f, cycle.getGameHour(), 0.001f);
        cycle.update(150f);
        assertEquals(0f, cycle.getGameHour(), 0.001f);
    }

    @Test
    public void nightIsOutsideDayHours() {
        assertTrue(cycleAtHour(0f).isNight());
        assertTrue(cycleAtHour(5.99f).isNight());
        assertFalse(cycleAtHour(6f).isNight());
        assertFalse(cycleAtHour(12f).isNight());
        assertFalse(cycleAtHour(17.99f).isNight());
        assertTrue(cycleAtHour(18f).isNight());
        assertTrue(cycleAtHour(23.99f).isNight());
    }

    @Test
    public void globalLightFollowsPaperMinecraftShape() {
        assertEquals(12, cycleAtHour(0f).getGlobalLight());
        assertEquals(12, cycleAtHour(4.5f).getGlobalLight());
        assertEquals(6, cycleAtHour(5.25f).getGlobalLight());
        assertEquals(0, cycleAtHour(6f).getGlobalLight());
        assertEquals(0, cycleAtHour(12f).getGlobalLight());
        assertEquals(0, cycleAtHour(18f).getGlobalLight());
        assertEquals(6, cycleAtHour(18.75f).getGlobalLight());
        assertEquals(12, cycleAtHour(19.5f).getGlobalLight());
    }

    private DayNightCycle cycleAtHour(float hour) {
        return new DayNightCycle(hour);
    }
}
