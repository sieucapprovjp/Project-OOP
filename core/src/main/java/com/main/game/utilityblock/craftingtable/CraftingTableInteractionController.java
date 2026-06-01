package com.main.game.utilityblock.craftingtable;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.player.Player;
import com.main.game.utilityblock.UtilityBlockInteractionController;
import com.main.game.world.World;

public class CraftingTableInteractionController {

    private static final String CRAFTING_TABLE_ID = "crafting_table";

    private final UtilityBlockInteractionController interactionController = new UtilityBlockInteractionController();

    public boolean canOpen(Player player, World world, OrthographicCamera camera, Viewport viewport) {
        return interactionController.canOpen(CRAFTING_TABLE_ID, player, world, camera, viewport);
    }
}
