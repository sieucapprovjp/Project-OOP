package com.main.game.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StarterInventoryKitTest {

    @Test
    public void grantsNetheriteSwordWithNineAttackDamage() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory);

        ItemStack sword = findStack(inventory, "netherite_sword");
        assertNotNull(sword);
        assertEquals(1, sword.getCount());
        assertTrue(ToolRegistry.isTool("netherite_sword"));
        assertEquals(9, ToolRegistry.getAttackDamage("netherite_sword", 2));
    }

    @Test
    public void grantsAllFoodsAndNoArmor() {
        Inventory inventory = new Inventory();

        StarterInventoryKit.grant(inventory);

        for (String itemId : FoodRegistry.getFoodItemIds()) {
            ItemStack stack = findStack(inventory, itemId);
            assertNotNull(itemId, stack);
            assertEquals(8, stack.getCount());
        }
        for (int i = 0; i < inventory.getTotalSize(); i++) {
            ItemStack stack = inventory.getSlot(i);
            if (stack != null) {
                assertFalse(stack.getItemId(), ArmorRegistry.isArmor(stack.getItemId()));
            }
        }
    }

    private ItemStack findStack(Inventory inventory, String itemId) {
        for (int i = 0; i < inventory.getTotalSize(); i++) {
            ItemStack stack = inventory.getSlot(i);
            if (stack != null && itemId.equals(stack.getItemId())) {
                return stack;
            }
        }
        return null;
    }
}
