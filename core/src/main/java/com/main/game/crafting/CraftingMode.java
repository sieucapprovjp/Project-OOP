package com.main.game.crafting;

public enum CraftingMode {
    PLAYER_2X2(2, 2),
    TABLE_3X3(3, 3);

    private final int width;
    private final int height;

    CraftingMode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
