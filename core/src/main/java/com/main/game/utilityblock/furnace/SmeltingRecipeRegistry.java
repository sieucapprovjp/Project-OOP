package com.main.game.utilityblock.furnace;

public final class SmeltingRecipeRegistry {

    private SmeltingRecipeRegistry() {
    }

    public static String getOutput(String inputItemId) {
        if ("raw_iron".equals(inputItemId)) return "iron_ingot";
        if ("raw_gold".equals(inputItemId)) return "gold_ingot";
        if ("raw_copper".equals(inputItemId)) return "copper_ingot";
        if ("raw_beef".equals(inputItemId)) return "cooked_beef";
        if ("raw_mutton".equals(inputItemId)) return "cooked_mutton";
        if ("raw_chicken".equals(inputItemId)) return "cooked_chicken";
        if ("raw_pork".equals(inputItemId)) return "cooked_pork";
        return null;
    }
}
