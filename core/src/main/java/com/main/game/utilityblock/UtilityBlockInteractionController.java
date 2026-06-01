package com.main.game.utilityblock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.blocks.AbstractBlock;
import com.main.game.entities.player.Player;
import com.main.game.world.World;

public class UtilityBlockInteractionController {

    private static final float OPEN_REACH = 4.5f;

    private final Vector2 mouseWorld = new Vector2();
    private int hoveredTileX = -1;
    private int hoveredTileY = -1;

    public boolean canOpen(String blockId, Player player, World world, OrthographicCamera camera, Viewport viewport) {
        hoveredTileX = -1;
        hoveredTileY = -1;
        if (blockId == null
            || player == null
            || world == null
            || camera == null
            || viewport == null
            || !player.isAlive()) {
            return false;
        }

        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        int tileX = (int) Math.floor(mouseWorld.x);
        int tileY = (int) Math.floor(mouseWorld.y);
        AbstractBlock block = world.getBlock(tileX, tileY);
        if (block == null
            || !blockId.equals(block.getBlockId())
            || !isWithinReach(player, tileX, tileY)) {
            return false;
        }

        hoveredTileX = tileX;
        hoveredTileY = tileY;
        return true;
    }

    public int getHoveredTileX() {
        return hoveredTileX;
    }

    public int getHoveredTileY() {
        return hoveredTileY;
    }

    private boolean isWithinReach(Player player, int tileX, int tileY) {
        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;
        float tileCenterX = tileX + 0.5f;
        float tileCenterY = tileY + 0.5f;
        float dx = tileCenterX - playerCenterX;
        float dy = tileCenterY - playerCenterY;
        return dx * dx + dy * dy <= OPEN_REACH * OPEN_REACH;
    }
}
