package com.main.game.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {

    private static final Map<String, TextureRegion> TEXTURE_CACHE = new HashMap<>();

    private ItemRegistry() {
    }

    public static int getMaxStack(String itemId) {
        return 64;
    }

    public static TextureRegion getTexture(String itemId) {
        if (TEXTURE_CACHE.containsKey(itemId)) {
            return TEXTURE_CACHE.get(itemId);
        }

        TextureRegion texture = TextureManager.getInstance().getTexture(toTextureName(itemId));
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }
        texture = getBlockPaletteTexture(itemId);
        TEXTURE_CACHE.put(itemId, texture);
        return texture;
    }

    private static String toTextureName(String itemId) {
        if ("grass".equals(itemId)) return "grass";
        if ("wood".equals(itemId)) return "wood";
        if ("leaves".equals(itemId)) return "leaves";
        if ("planks".equals(itemId)) return "planks";
        if ("snow".equals(itemId)) return "snow";
        if ("ice".equals(itemId)) return "ice";
        if ("sandstone".equals(itemId)) return "sandstone";
        if ("cactus".equals(itemId)) return "cactus";
        return itemId;
    }

    private static TextureRegion getBlockPaletteTexture(String itemId) {
        if ("grass".equals(itemId)) return BlockPalette.getGrass();
        if ("dirt".equals(itemId)) return BlockPalette.getDirt();
        if ("stone".equals(itemId)) return BlockPalette.getStone();
        if ("sand".equals(itemId)) return BlockPalette.getSand();
        if ("wood".equals(itemId)) return BlockPalette.getWood();
        if ("leaves".equals(itemId)) return BlockPalette.getLeaves();
        if ("planks".equals(itemId)) return BlockPalette.getPlanks();
        if ("bedrock".equals(itemId)) return BlockPalette.getBedrock();
        if ("snow".equals(itemId)) return BlockPalette.getSnow();
        if ("ice".equals(itemId)) return BlockPalette.getIce();
        if ("sandstone".equals(itemId)) return BlockPalette.getSandstone();
        if ("cactus".equals(itemId)) return BlockPalette.getCactus();
        return null;
    }
}
