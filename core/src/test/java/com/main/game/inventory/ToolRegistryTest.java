package com.main.game.inventory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ToolRegistryTest {

    @Test
    public void identifiesSwordsSeparatelyFromOtherTools() {
        assertTrue(ToolRegistry.isSword("netherite_sword"));
        assertTrue(ToolRegistry.isSword("wood_sword"));
        assertFalse(ToolRegistry.isSword("netherite_axe"));
        assertFalse(ToolRegistry.isSword("apple"));
        assertFalse(ToolRegistry.isSword(null));
    }
}
