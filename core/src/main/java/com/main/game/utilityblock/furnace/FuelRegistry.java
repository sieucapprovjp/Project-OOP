package com.main.game.utilityblock.furnace;

public final class FuelRegistry {

    private FuelRegistry() {
    }

    public static float getBurnSeconds(String itemId) {
        if ("coal".equals(itemId)) return 80f;
        if ("wood".equals(itemId)) return 15f;
        if ("cherry_log".equals(itemId)) return 15f;
        if ("spruce_log".equals(itemId)) return 15f;
        if ("planks".equals(itemId)) return 15f;
        if ("cherry_planks".equals(itemId)) return 15f;
        if ("spruce_planks".equals(itemId)) return 15f;
        if ("stick".equals(itemId)) return 5f;
        return 0f;
    }
}
