package com.main.game.crafting;

import com.main.game.inventory.Inventory;
import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

public class CraftingController {

    private CraftingMode mode;
    private CraftingGrid grid;

    public CraftingController() {
        this(CraftingMode.PLAYER_2X2);
    }

    public CraftingController(CraftingMode mode) {
        this.mode = mode;
        this.grid = new CraftingGrid(mode);
    }

    public CraftingMode getMode() {
        return mode;
    }

    public boolean isTableCrafting() {
        return mode == CraftingMode.TABLE_3X3;
    }

    public CraftingGrid getGrid() {
        return grid;
    }

    public void openPlayerCrafting(Inventory inventory) {
        setMode(CraftingMode.PLAYER_2X2, inventory);
    }

    public void openTableCrafting(Inventory inventory) {
        setMode(CraftingMode.TABLE_3X3, inventory);
    }

    public void closeCrafting(Inventory inventory) {
        returnInputsToInventory(inventory);
        if (isGridEmpty()) {
            setMode(CraftingMode.PLAYER_2X2, null);
        }
    }

    public ItemStack getResult() {
        CraftingMatch match = RecipeRegistry.findMatch(grid);
        if (match == null) {
            return null;
        }
        return new ItemStack(match.getRecipe().getOutputItemId(), match.getTotalOutputCount());
    }

    public ItemStack takeResult(ItemStack carriedStack) {
        CraftingMatch match = RecipeRegistry.findMatch(grid);
        if (match == null) {
            return carriedStack;
        }

        CraftingRecipe recipe = match.getRecipe();
        int craftsToTake = match.getCraftCount();
        if (carriedStack != null) {
            if (!recipe.getOutputItemId().equals(carriedStack.getItemId())) {
                return carriedStack;
            }
            int room = ItemRegistry.getMaxStack(carriedStack.getItemId()) - carriedStack.getCount();
            craftsToTake = Math.min(craftsToTake, room / recipe.getOutputCount());
            if (craftsToTake <= 0) {
                return carriedStack;
            }
            carriedStack.add(craftsToTake * recipe.getOutputCount());
            consume(match, craftsToTake);
            return carriedStack;
        }

        consume(match, craftsToTake);
        return new ItemStack(recipe.getOutputItemId(), craftsToTake * recipe.getOutputCount());
    }

    public void returnInputsToInventory(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        for (int i = 0; i < grid.getSize(); i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack == null || stack.getCount() <= 0) {
                grid.setSlot(i, null);
                continue;
            }
            ItemStack remaining = inventory.addStack(stack);
            grid.setSlot(i, remaining);
        }
    }

    private void setMode(CraftingMode nextMode, Inventory inventory) {
        if (nextMode == null || nextMode == mode) {
            return;
        }
        returnInputsToInventory(inventory);
        if (!isGridEmpty()) {
            return;
        }
        mode = nextMode;
        grid = new CraftingGrid(mode);
    }

    private boolean isGridEmpty() {
        for (int i = 0; i < grid.getSize(); i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack != null && stack.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

    private void consume(CraftingMatch match, int craftCount) {
        for (int slot : match.getIngredientSlots()) {
            ItemStack stack = grid.getSlot(slot);
            if (stack == null) {
                continue;
            }
            stack.subtract(craftCount);
            if (stack.getCount() <= 0) {
                grid.setSlot(slot, null);
            }
        }
    }
}
