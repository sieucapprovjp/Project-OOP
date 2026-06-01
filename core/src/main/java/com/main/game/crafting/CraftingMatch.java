package com.main.game.crafting;

final class CraftingMatch {

    private final CraftingRecipe recipe;
    private final int[] ingredientSlots;
    private final int craftCount;

    CraftingMatch(CraftingRecipe recipe, int[] ingredientSlots, int craftCount) {
        this.recipe = recipe;
        this.ingredientSlots = ingredientSlots;
        this.craftCount = craftCount;
    }

    CraftingRecipe getRecipe() {
        return recipe;
    }

    int[] getIngredientSlots() {
        return ingredientSlots;
    }

    int getCraftCount() {
        return craftCount;
    }

    int getTotalOutputCount() {
        return recipe.getOutputCount() * craftCount;
    }
}
