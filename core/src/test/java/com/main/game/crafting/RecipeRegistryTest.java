package com.main.game.crafting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.inventory.ItemStack;
import com.main.game.inventory.ToolRegistry;
import com.main.game.utilityblock.furnace.FuelRegistry;
import org.junit.Test;

public class RecipeRegistryTest {

    @Test
    public void cherryLogCraftsIntoCherryPlanks() {
        CraftingGrid grid = new CraftingGrid();
        grid.setSlot(0, new ItemStack("cherry_log", 1));

        CraftingMatch match = RecipeRegistry.findMatch(grid);

        assertNotNull(match);
        assertEquals("cherry_planks", match.getRecipe().getOutputItemId());
        assertEquals(4, match.getTotalOutputCount());
        assertTrue(BlockRegistry.isPlaceable("cherry_planks"));
        assertTrue(BlockRegistry.isSolid("cherry_planks"));
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "cherry_log") > 1f);
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "cherry_planks") > 1f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("cherry_log"), 0.001f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("cherry_planks"), 0.001f);
    }

    @Test
    public void spruceLogCraftsIntoSprucePlanks() {
        CraftingGrid grid = new CraftingGrid();
        grid.setSlot(0, new ItemStack("spruce_log", 1));

        CraftingMatch match = RecipeRegistry.findMatch(grid);

        assertNotNull(match);
        assertEquals("spruce_planks", match.getRecipe().getOutputItemId());
        assertEquals(4, match.getTotalOutputCount());
        assertTrue(BlockRegistry.isPlaceable("spruce_planks"));
        assertTrue(BlockRegistry.isSolid("spruce_planks"));
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "spruce_log") > 1f);
        assertTrue(ToolRegistry.getMiningMultiplier("wood_axe", "spruce_planks") > 1f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("spruce_log"), 0.001f);
        assertEquals(15f, FuelRegistry.getBurnSeconds("spruce_planks"), 0.001f);
    }
}
