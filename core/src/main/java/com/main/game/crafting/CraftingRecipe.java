package com.main.game.crafting;

import com.main.game.inventory.ItemRegistry;
import com.main.game.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingRecipe {

    public enum Type {
        SHAPED,
        SHAPELESS
    }

    private final String name;
    private final Type type;
    private final int width;
    private final int height;
    private final String[] input;
    private final String outputItemId;
    private final int outputCount;

    private CraftingRecipe(String name, Type type, int width, int height,
                           String[] input, String outputItemId, int outputCount) {
        this.name = name;
        this.type = type;
        this.width = width;
        this.height = height;
        this.input = input;
        this.outputItemId = outputItemId;
        this.outputCount = outputCount;
    }

    public static CraftingRecipe shaped(String name, String[] pattern, String outputItemId, int outputCount) {
        return shaped(name, CraftingGrid.PLAYER_WIDTH, CraftingGrid.PLAYER_HEIGHT, pattern, outputItemId, outputCount);
    }

    public static CraftingRecipe shaped(String name, int width, int height,
                                        String[] pattern, String outputItemId, int outputCount) {
        if (width <= 0 || height <= 0 || width > CraftingGrid.TABLE_WIDTH || height > CraftingGrid.TABLE_HEIGHT) {
            throw new IllegalArgumentException("Shaped recipes must fit inside the 3x3 crafting table.");
        }
        if (pattern == null || pattern.length != width * height) {
            throw new IllegalArgumentException("Shaped recipe pattern must match width * height.");
        }
        return new CraftingRecipe(name, Type.SHAPED, width, height,
            Arrays.copyOf(pattern, pattern.length), outputItemId, outputCount);
    }

    public static CraftingRecipe shapeless(String name, String[] ingredients, String outputItemId, int outputCount) {
        if (ingredients == null || ingredients.length == 0 || ingredients.length > CraftingGrid.MAX_SIZE) {
            throw new IllegalArgumentException("Shapeless recipes require 1-9 ingredients.");
        }
        return new CraftingRecipe(name, Type.SHAPELESS, 0, 0,
            Arrays.copyOf(ingredients, ingredients.length), outputItemId, outputCount);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getOutputItemId() {
        return outputItemId;
    }

    public int getOutputCount() {
        return outputCount;
    }

    CraftingMatch match(CraftingGrid grid) {
        if (grid == null) {
            return null;
        }
        if (type == Type.SHAPELESS) {
            return matchShapeless(grid);
        }
        return matchShaped(grid);
    }

    private CraftingMatch matchShaped(CraftingGrid grid) {
        Bounds bounds = findBounds(grid);
        if (bounds == null || bounds.width() != width || bounds.height() != height) {
            return null;
        }

        int[] slots = matchedSlots(grid, bounds, false);
        if (slots == null) {
            slots = matchedSlots(grid, bounds, true);
        }
        return createMatch(grid, slots);
    }

    private CraftingMatch matchShapeless(CraftingGrid grid) {
        List<Integer> remainingSlots = new ArrayList<>();
        for (int i = 0; i < grid.getSize(); i++) {
            ItemStack stack = grid.getSlot(i);
            if (stack != null && stack.getCount() > 0) {
                remainingSlots.add(i);
            }
        }
        if (remainingSlots.size() != input.length) {
            return null;
        }

        int[] matchedSlots = new int[input.length];
        boolean[] used = new boolean[remainingSlots.size()];
        for (int i = 0; i < input.length; i++) {
            boolean matched = false;
            for (int slotIndex = 0; slotIndex < remainingSlots.size(); slotIndex++) {
                if (used[slotIndex]) {
                    continue;
                }
                int gridSlot = remainingSlots.get(slotIndex);
                ItemStack stack = grid.getSlot(gridSlot);
                if (stack != null && input[i].equals(stack.getItemId())) {
                    matchedSlots[i] = gridSlot;
                    used[slotIndex] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return null;
            }
        }
        return createMatch(grid, matchedSlots);
    }

    private CraftingMatch createMatch(CraftingGrid grid, int[] ingredientSlots) {
        if (ingredientSlots == null || outputCount <= 0 || outputItemId == null) {
            return null;
        }

        int ingredientCrafts = Integer.MAX_VALUE;
        for (int slot : ingredientSlots) {
            ItemStack stack = grid.getSlot(slot);
            if (stack == null || stack.getCount() <= 0) {
                return null;
            }
            ingredientCrafts = Math.min(ingredientCrafts, stack.getCount());
        }

        int maxOutputStack = ItemRegistry.getMaxStack(outputItemId);
        int outputCrafts = maxOutputStack / outputCount;
        int craftCount = Math.min(ingredientCrafts, outputCrafts);
        if (craftCount <= 0) {
            return null;
        }
        return new CraftingMatch(this, ingredientSlots, craftCount);
    }

    private int[] matchedSlots(CraftingGrid grid, Bounds bounds, boolean mirrored) {
        List<Integer> slots = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int recipeCol = mirrored ? width - 1 - col : col;
                String expected = input[row * width + recipeCol];
                int gridSlot = grid.toIndex(bounds.minCol + col, bounds.minRow + row);
                ItemStack stack = grid.getSlot(gridSlot);
                String actual = stack == null || stack.getCount() <= 0 ? null : stack.getItemId();
                if (expected == null) {
                    if (actual != null) {
                        return null;
                    }
                    continue;
                }
                if (!expected.equals(actual)) {
                    return null;
                }
                slots.add(gridSlot);
            }
        }
        int[] result = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            result[i] = slots.get(i);
        }
        return result;
    }

    private Bounds findBounds(CraftingGrid grid) {
        int minCol = grid.getWidth();
        int minRow = grid.getHeight();
        int maxCol = -1;
        int maxRow = -1;

        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {
                ItemStack stack = grid.getSlot(grid.toIndex(col, row));
                if (stack == null || stack.getCount() <= 0) {
                    continue;
                }
                minCol = Math.min(minCol, col);
                minRow = Math.min(minRow, row);
                maxCol = Math.max(maxCol, col);
                maxRow = Math.max(maxRow, row);
            }
        }

        if (maxCol < 0 || maxRow < 0) {
            return null;
        }
        return new Bounds(minCol, minRow, maxCol, maxRow);
    }

    private static final class Bounds {
        private final int minCol;
        private final int minRow;
        private final int maxCol;
        private final int maxRow;

        private Bounds(int minCol, int minRow, int maxCol, int maxRow) {
            this.minCol = minCol;
            this.minRow = minRow;
            this.maxCol = maxCol;
            this.maxRow = maxRow;
        }

        private int width() {
            return maxCol - minCol + 1;
        }

        private int height() {
            return maxRow - minRow + 1;
        }
    }
}
