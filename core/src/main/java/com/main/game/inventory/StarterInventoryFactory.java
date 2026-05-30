package com.main.game.inventory;

public final class StarterInventoryFactory {

    private static final String[] STARTER_TOOLS = {
        "wood_pickaxe",
        "wood_axe",
        "wood_shovel",
        "wood_hoe",
        "stone_pickaxe",
        "stone_axe",
        "stone_shovel",
        "stone_sword",
        "stone_hoe",
        "copper_pickaxe",
        "copper_axe",
        "copper_shovel",
        "copper_sword",
        "copper_hoe",
        "iron_pickaxe",
        "iron_axe",
        "iron_shovel",
        "iron_sword",
        "iron_hoe",
        "gold_pickaxe",
        "gold_axe",
        "gold_shovel",
        "gold_sword",
        "gold_hoe",
        "diamond_pickaxe",
        "diamond_axe",
        "diamond_shovel",
        "diamond_sword",
        "diamond_hoe",
        "netherite_pickaxe",
        "netherite_axe",
        "netherite_shovel",
        "netherite_sword",
        "netherite_hoe"
    };

    private StarterInventoryFactory() {
    }

    public static void populateStarterTools(Inventory inventory) {
        for (int i = 0; i < STARTER_TOOLS.length && i < inventory.getTotalSize(); i++) {
            inventory.setSlot(i, new ItemStack(STARTER_TOOLS[i], 1));
        }
    }
}
