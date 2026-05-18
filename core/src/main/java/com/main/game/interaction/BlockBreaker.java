package com.main.game.interaction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.world.World;

public class BlockBreaker {

    private static final float BLOCK_REACH = 4.5f;
    private static final float BREAK_COMPLETE_STAGES = 9.99f;
    private static final float DIG_SOUND_INTERVAL = 8f / 60f;

    private int hoveredBlockX = -1;
    private int hoveredBlockY = -1;
    private int breakingBlockX = -1;
    private int breakingBlockY = -1;
    private float breakElapsed = 0f;
    private float digStepDuration = 0.1f;
    private float digSoundTimer = 0f;
    private boolean breaking = false;
    private boolean digSoundRequested = false;
    private BlockBreakListener blockBreakListener;

    public void setBlockBreakListener(BlockBreakListener blockBreakListener) {
        this.blockBreakListener = blockBreakListener;
    }

    public void update(float delta, Player player, World world, OrthographicCamera camera, Viewport viewport) {
        hoveredBlockX = -1;
        hoveredBlockY = -1;
        breaking = false;
        digSoundRequested = false;

        camera.update();
        Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        int tileX = (int) Math.floor(mouseWorld.x);
        int tileY = (int) Math.floor(mouseWorld.y);
        AbstractBlock block = world.getBlock(tileX, tileY);

        if (block == null || !isWithinBlockReach(player, tileX, tileY) || !hasLineOfSight(player, world, tileX, tileY)) {
            resetBreaking();
            return;
        }

        hoveredBlockX = tileX;
        hoveredBlockY = tileY;

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) || !BlockBreakRules.canBreak(block)) {
            resetBreaking();
            return;
        }

        breaking = true;
        if (breakingBlockX != tileX || breakingBlockY != tileY) {
            breakingBlockX = tileX;
            breakingBlockY = tileY;
            breakElapsed = 0f;
            digSoundTimer = 0f;
        }

        float hardness = Math.max(0.1f, block.getHardness());
        digStepDuration = hardness / BREAK_COMPLETE_STAGES;
        breakElapsed += delta;
        digSoundTimer += delta;

        if (digSoundTimer >= DIG_SOUND_INTERVAL) {
            digSoundTimer = 0f;
            digSoundRequested = true;
        }

        if (breakElapsed > digStepDuration * BREAK_COMPLETE_STAGES) {
            if (blockBreakListener != null) {
                blockBreakListener.onBlockBroken(block, world);
            }
            world.setBlock(tileX, tileY, null);
            resetBreaking();
        }
    }

    public boolean hasHoveredBlock() {
        return hoveredBlockX >= 0 && hoveredBlockY >= 0;
    }

    public int getHoveredBlockX() {
        return hoveredBlockX;
    }

    public int getHoveredBlockY() {
        return hoveredBlockY;
    }

    public float getBreakProgress() {
        return Math.min(1f, breakElapsed / (digStepDuration * BREAK_COMPLETE_STAGES));
    }

    public int getCrackStageIndex(int maxStages) {
        if (!breaking || maxStages <= 0) {
            return -1;
        }
        return Math.min(maxStages - 1, (int) Math.floor(breakElapsed / digStepDuration));
    }

    public boolean isBreaking() {
        return breaking;
    }

    public boolean consumeDigSoundRequest() {
        boolean requested = digSoundRequested;
        digSoundRequested = false;
        return requested;
    }

    private boolean isWithinBlockReach(Player player, int tileX, int tileY) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        float blockCenterX = tileX + 0.5f;
        float blockCenterY = tileY + 0.5f;
        float dx = blockCenterX - playerCenterX;
        float dy = blockCenterY - playerCenterY;
        return dx * dx + dy * dy <= BLOCK_REACH * BLOCK_REACH;
    }

    private boolean hasLineOfSight(Player player, World world, int targetX, int targetY) {
        float startX = player.getX() + player.getWidth() / 2f;
        float startY = player.getY() + player.getHeight() / 2f;
        float endX = targetX + 0.5f;
        float endY = targetY + 0.5f;
        float dx = endX - startX;
        float dy = endY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        int steps = Math.max(1, (int) Math.ceil(distance * 16f));

        for (int i = 1; i <= steps; i++) {
            float t = i / (float) steps;
            int checkX = (int) Math.floor(startX + dx * t);
            int checkY = (int) Math.floor(startY + dy * t);

            if (checkX == targetX && checkY == targetY) {
                return true;
            }
            if (world.isSolid(checkX, checkY)) {
                return false;
            }
        }
        return true;
    }

    private void resetBreaking() {
        breakingBlockX = -1;
        breakingBlockY = -1;
        breakElapsed = 0f;
        digSoundTimer = 0f;
        breaking = false;
    }
}
