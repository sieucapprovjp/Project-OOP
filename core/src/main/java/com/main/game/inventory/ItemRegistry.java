package com.main.game.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.blocks.metadata.BlockRegistry;
import com.main.game.utils.TextureManager;
import com.main.game.world.BlockPalette;

import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {

    private static final Map<String, TextureRegion> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, TextureRegion> HELD_TEXTURE_CACHE = new HashMap<>();

    private ItemRegistry() {
    }

    public static int getMaxStack(String itemId) {
        if (ToolRegistry.isTool(itemId) || ArmorRegistry.isArmor(itemId)) {
            return 1;
        }
        return 64;
    }

    public static int getMaxDurability(String itemId) {
        int toolDurability = ToolRegistry.getMaxDurability(itemId);
        if (toolDurability > 0) {
            return toolDurability;
        }
        return ArmorRegistry.getMaxDurability(itemId);
    }

    public static TextureRegion getTexture(String itemId) {
        if (TEXTURE_CACHE.containsKey(itemId)) {
            return TEXTURE_CACHE.get(itemId);
        }

        TextureRegion texture = getToolTexture(itemId);
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }

        texture = getArmorTexture(itemId);
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }

        texture = TextureManager.getInstance().getTexture(toTextureName(itemId));
        if (texture != null) {
            TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }
        texture = getBlockPaletteTexture(itemId);
        TEXTURE_CACHE.put(itemId, texture);
        return texture;
    }

    public static TextureRegion getHeldTexture(String itemId) {
        if (HELD_TEXTURE_CACHE.containsKey(itemId)) {
            return HELD_TEXTURE_CACHE.get(itemId);
        }

        TextureRegion texture = getToolHeldTexture(itemId);
        if (texture != null) {
            HELD_TEXTURE_CACHE.put(itemId, texture);
            return texture;
        }

        texture = getTexture(itemId);
        HELD_TEXTURE_CACHE.put(itemId, texture);
        return texture;
    }

    public static boolean isPlaceableBlock(String itemId) {
        return itemId != null && !ToolRegistry.isTool(itemId) && BlockRegistry.isPlaceable(itemId);
    }

    private static String toTextureName(String itemId) {
        if ("cobblestone".equals(itemId)) return "cobble_stone";
        if ("stick".equals(itemId)) return "stick";
        String blockTextureName = BlockRegistry.getTextureName(itemId);
        if (blockTextureName != null) return blockTextureName;
        return itemId;
    }

    private static TextureRegion getToolTexture(String itemId) {
        ToolRegistry.ToolDefinition tool = ToolRegistry.get(itemId);
        if (tool == null) {
            return null;
        }
        return TextureManager.getInstance().getTexture(tool.getTextureName());
    }

    private static TextureRegion getToolHeldTexture(String itemId) {
        ToolRegistry.ToolDefinition tool = ToolRegistry.get(itemId);
        if (tool == null) {
            return null;
        }
        return TextureManager.getInstance().getTexture(tool.getHeldTextureName());
    }

    private static TextureRegion getArmorTexture(String itemId) {
        String textureName = ArmorRegistry.getTextureName(itemId);
        if (textureName == null) {
            return null;
        }
        TextureRegion texture = TextureManager.getInstance().getTexture(textureName);
        return texture != null ? texture : TextureManager.getInstance().getTexture("armor/" + itemId);
    }

    private static TextureRegion getBlockPaletteTexture(String itemId) {
        if ("cobblestone".equals(itemId)) return BlockPalette.getStone();
        return BlockRegistry.getPaletteFallbackTexture(itemId);
    }
}
