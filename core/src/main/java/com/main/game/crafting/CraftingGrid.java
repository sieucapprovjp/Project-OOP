package com.main.game.crafting;

import com.main.game.inventory.ItemStack;

public class CraftingGrid {

    public static final int PLAYER_WIDTH = 2;
    public static final int PLAYER_HEIGHT = 2;
    public static final int TABLE_WIDTH = 3;
    public static final int TABLE_HEIGHT = 3;
    public static final int MAX_SIZE = TABLE_WIDTH * TABLE_HEIGHT;

    private final int width;
    private final int height;
    private final ItemStack[] slots;

    public CraftingGrid() {
        this(PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    public CraftingGrid(CraftingMode mode) {
        this(mode.getWidth(), mode.getHeight());
    }

    public CraftingGrid(int width, int height) {
        if (width <= 0 || height <= 0 || width * height > MAX_SIZE) {
            throw new IllegalArgumentException("Crafting grid must fit inside the 3x3 table limit.");
        }
        this.width = width;
        this.height = height;
        this.slots = new ItemStack[width * height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return slots.length;
    }

    public int toIndex(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            return -1;
        }
        return row * width + col;
    }

    public ItemStack getSlot(int index) {
        if (index < 0 || index >= slots.length) {
            return null;
        }
        return slots[index];
    }

    public void setSlot(int index, ItemStack stack) {
        if (index < 0 || index >= slots.length) {
            return;
        }
        if (stack == null || stack.getCount() <= 0) {
            slots[index] = null;
            return;
        }
        slots[index] = stack;
    }
}
