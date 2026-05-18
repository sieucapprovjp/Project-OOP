package com.main.game.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.inventory.Inventory;
import com.main.game.world.World;

public class DroppedItem {

    private static final float ITEM_SIZE = 0.4f;
    private static final float GRAVITY_PER_TICK = -0.03f;
    private static final float MAX_FALL_SPEED = -0.6f;
    private static final float GROUND_FRICTION = 0.5f;
    private static final float SUCK_RANGE = 4f;
    private static final float SUCK_STRENGTH = 0.012f;

    private final String itemId;
    private final TextureRegion texture;
    private int count;
    private final Vector2 position;
    private final Vector2 velocity;
    private final float waitUntil;

    public DroppedItem(HarvestEntry entry, World world, float currentTime) {
        this.itemId = entry.getItemId();
        this.texture = entry.getTexture();
        this.count = entry.getCount();
        this.position = new Vector2(entry.getWorldX(world) - ITEM_SIZE / 2f, entry.getWorldY(world) - ITEM_SIZE / 2f);
        float sy = entry.getSy() == HarvestEntry.RANDOM_VERTICAL_SPEED
            ? MathUtils.random(0.15f, 0.35f)
            : entry.getSy();
        this.velocity = new Vector2(entry.getSx(), sy);
        this.waitUntil = currentTime + (Math.abs(entry.getSx()) > 0.08f ? 1.0f : 0.5f);
    }

    public boolean update(float delta, World world, Player player, Inventory inventory, float currentTime) {
        float tickStep = delta * 60f;

        position.x += velocity.x * tickStep;
        if (collides(world)) {
            position.x -= velocity.x * tickStep;
            velocity.x = -velocity.x;
        }

        velocity.y = Math.max(MAX_FALL_SPEED, velocity.y + GRAVITY_PER_TICK * tickStep);
        position.y += velocity.y * tickStep;

        if (isOnSolidGround(world)) {
            float centerY = (float) Math.floor(getCenterY() - 0.4f) + 1.41f;
            position.y = centerY - ITEM_SIZE / 2f;
            velocity.y = 0f;
            velocity.x *= GROUND_FRICTION;
            applyConveyor(world);
        }

        if (player != null && player.isAlive() && inventory != null) {
            return doSuck(player, inventory, currentTime, tickStep);
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, ITEM_SIZE, ITEM_SIZE);
        }
    }

    public boolean canBePickedUp(float currentTime) {
        return currentTime >= waitUntil;
    }

    public String getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    private boolean collides(World world) {
        int minX = (int) Math.floor(position.x);
        int maxX = (int) Math.floor(position.x + ITEM_SIZE);
        int minY = (int) Math.floor(position.y);
        int maxY = (int) Math.floor(position.y + ITEM_SIZE);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (world.isSolid(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOnSolidGround(World world) {
        int tileX = (int) Math.floor(getCenterX());
        int tileY = (int) Math.floor(getCenterY() - 0.4f);
        return velocity.y <= 0f && world.isSolid(tileX, tileY);
    }

    private void applyConveyor(World world) {
        AbstractBlock blockBelow = world.getBlock((int) Math.floor(getCenterX()), (int) Math.floor(getCenterY() - 0.4f));
        if (blockBelow == null) {
            return;
        }
        if ("L".equals(blockBelow.getBlockId())) {
            velocity.x = -0.1f;
        } else if ("R".equals(blockBelow.getBlockId())) {
            velocity.x = 0.1f;
        }
    }

    private boolean doSuck(Player player, Inventory inventory, float currentTime, float tickStep) {
        if (currentTime < waitUntil) {
            return false;
        }

        float dx = (player.getX() + player.getWidth() / 2f) - getCenterX();
        float dy = (player.getY() + player.getHeight() / 2f) - getCenterY();
        float dist = Math.abs(dx) + Math.abs(dy);
        if (dist <= 0.001f || dist >= 2f) {
            return false;
        }

        if (Math.abs(dx) < 0.25f && Math.abs(dy) < 0.9f) {
            int remaining = inventory.add(itemId, count);
            count = remaining;
            return remaining <= 0;
        }

        velocity.x = (dx / dist) * 0.2f;
        velocity.y = (dy / dist) * 0.2f + 0.04f;
        velocity.x += Math.signum(dx) * SUCK_STRENGTH * tickStep;
        velocity.y += Math.signum(dy) * SUCK_STRENGTH * tickStep;
        return false;
    }

    private float getCenterX() {
        return position.x + ITEM_SIZE / 2f;
    }

    private float getCenterY() {
        return position.y + ITEM_SIZE / 2f;
    }
}
