package com.main.game.inventory;

public class ItemStack {

    private final String itemId;
    private int count;
    private int durability;
    private final int maxDurability;

    public ItemStack(String itemId, int count) {
        this.itemId = itemId;
        this.count = count;
        this.maxDurability = ItemRegistry.getMaxDurability(itemId);
        this.durability = maxDurability;
    }

    public String getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void add(int amount) {
        count += amount;
    }

    public void subtract(int amount) {
        count -= amount;
    }

    public boolean hasDurability() {
        return maxDurability > 0;
    }

    public int getDurability() {
        return durability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public float getDurabilityRatio() {
        if (!hasDurability()) {
            return 1f;
        }
        return Math.max(0f, Math.min(1f, durability / (float) maxDurability));
    }

    public boolean damage(int amount) {
        if (!hasDurability() || amount <= 0) {
            return false;
        }
        durability -= amount;
        if (durability <= 0) {
            durability = 0;
            count = 0;
            return true;
        }
        return false;
    }

    public ItemStack copy() {
        ItemStack copy = new ItemStack(itemId, count);
        copy.durability = durability;
        return copy;
    }
}
