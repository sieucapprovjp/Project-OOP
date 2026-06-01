package com.main.game.utilityblock.furnace;

import com.main.game.inventory.ItemStack;

public class FurnaceState {

    private static final float COOK_SECONDS = 10f;

    private ItemStack input;
    private ItemStack fuel;
    private ItemStack output;
    private float burnRemaining;
    private float burnDuration;
    private float cookProgress;

    public ItemStack getInput() {
        return input;
    }

    public void setInput(ItemStack input) {
        this.input = input;
    }

    public ItemStack getFuel() {
        return fuel;
    }

    public void setFuel(ItemStack fuel) {
        this.fuel = fuel;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public boolean isBurning() {
        return burnRemaining > 0f;
    }

    public float getBurnRemaining() {
        return burnRemaining;
    }

    public float getBurnRatio() {
        if (burnDuration <= 0f) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, burnRemaining / burnDuration));
    }

    public void setBurn(float burnSeconds) {
        burnDuration = burnSeconds;
        burnRemaining = burnSeconds;
    }

    public void consumeBurn(float delta) {
        burnRemaining = Math.max(0f, burnRemaining - Math.max(0f, delta));
    }

    public float getCookProgress() {
        return cookProgress;
    }

    public void setCookProgress(float cookProgress) {
        this.cookProgress = Math.max(0f, cookProgress);
    }

    public float getCookRatio() {
        return Math.max(0f, Math.min(1f, cookProgress / COOK_SECONDS));
    }
}
