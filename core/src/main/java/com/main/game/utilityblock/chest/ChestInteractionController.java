package com.main.game.utilityblock.chest;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.UtilityBlockInteractionController;
import com.main.game.world.World;

public class ChestInteractionController {

    private static final String CHEST_ID = "chest";

    private final UtilityBlockInteractionController interactionController = new UtilityBlockInteractionController();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        return interactionController.canOpen(CHEST_ID, player, world, camera, viewport);
    }

    public int getHoveredTileX() {
        return interactionController.getHoveredTileX();
    }

    public int getHoveredTileY() {
        return interactionController.getHoveredTileY();
    }
}
