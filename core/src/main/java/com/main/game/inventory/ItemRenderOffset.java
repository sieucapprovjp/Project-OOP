package com.main.game.inventory;

import java.util.Set;

public final class ItemRenderOffset {

    private static final float ORE_ITEM_X_OFFSET_RATIO = 0.08f;
    private static final Set<String> ORE_ITEM_IDS = Set.of(
        "coal",
        "diamond",
        "lapis",
        "redstone",
        "emerald",
        "raw_iron",
        "raw_gold",
        "raw_copper",
        "iron_ingot",
        "gold_ingot",
        "copper_ingot",
        "coal_ore",
        "iron_ore",
        "gold_ore",
        "diamond_ore",
        "copper_ore",
        "lapis_ore",
        "redstone_ore",
        "emerald_ore",
        "deepslate_co",
        "deepslate_io",
        "deepslate_go",
        "deepslate_do",
        "deepslate_copper",
        "ore_lapis_deepslate",
        "deepslate_ro",
        "deepslate_eo"
    );

    private ItemRenderOffset() {
    }

    public static float xOffset(String itemId, float itemSize) {
        return ORE_ITEM_IDS.contains(itemId) ? itemSize * ORE_ITEM_X_OFFSET_RATIO : 0f;
    }
}
