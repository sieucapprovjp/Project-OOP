package com.main.game.world;

import static org.junit.Assert.assertEquals;

import com.main.game.utils.Constants;
import org.junit.Test;

public class WorldConstantsTest {

    @Test
    public void worldWidthIsFiveHundredTiles() {
        assertEquals(500, Constants.WORLD_WIDTH);
    }
}
