package com.main.game.items;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BlockDropFactoryTest {

    @Test
    public void appleInTreeDropsApple() {
        assertEquals("apple", BlockDropFactory.dropItemIdForBlock("apple_in_tree", 1f));
    }

    @Test
    public void oakLeavesCanDropAppleByChance() {
        assertEquals("apple", BlockDropFactory.dropItemIdForBlock("leaves", 0.049f));
        assertEquals("leaves", BlockDropFactory.dropItemIdForBlock("leaves", 0.05f));
        assertEquals("apple", BlockDropFactory.dropItemIdForBlock("desert_oak_leaves", 0.01f));
        assertEquals("desert_oak_leaves", BlockDropFactory.dropItemIdForBlock("desert_oak_leaves", 0.05f));
        assertEquals("apple", BlockDropFactory.dropItemIdForBlock("desert_oak_leaves_2", 0.01f));
    }
}
