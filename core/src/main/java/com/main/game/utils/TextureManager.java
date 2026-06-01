package com.main.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {
    private static TextureManager instance;
    private TextureAtlas atlas;
    private final List<Texture> ownedTextures = new ArrayList<>();
    private final Map<String, TextureRegion> generatedFallbacks = new HashMap<>();

    private TextureManager() {
        // Tự động load atlas từ đường dẫn trong Constants của bạn
        try {
            FileHandle atlasFile = Gdx.files.internal(Constants.TEXTURE_ATLAS_PATH);
            if (atlasFile.exists()) {
                atlas = new TextureAtlas(atlasFile);
            } else {
                atlas = null;
            }
        } catch (Exception e) {
            atlas = null;
        }
    }

    public static TextureManager getInstance() {
        if (instance == null) instance = new TextureManager();
        return instance;
    }

    public TextureRegion getTexture(String name) {
        // If libGDX file resolver is not initialized yet (no Application created), bail out.
        if (Gdx.files == null) {
            System.err.println("Gdx.files not initialized yet; cannot load texture: " + name);
            return null;
        }
        if (atlas != null) {
            TextureRegion r = atlas.findRegion(name);
            if (r != null) return r;
        }

        // Fallback: try to load individual image files from common asset locations.
        // Provide some common name mappings for convenience.
        java.util.Map<String, String> nameMap = new java.util.HashMap<>();
        nameMap.put("grass_block", "grass");
        nameMap.put("oak_log", "wood");
        nameMap.put("natural_wood", "wood");
        nameMap.put("oak_leaves", "leaves");
        nameMap.put("oak_planks", "planks");
        nameMap.put("apple_in_tree", "food/apple_in_tree");
        nameMap.put("apple", "food/apple");
        nameMap.put("forest_apple", "food/forest_apple");
        nameMap.put("golden_apple", "food/golden_apple");
        nameMap.put("bread", "food/bread");
        nameMap.put("carrot", "food/carrot");
        nameMap.put("cookie", "food/cookie");
        nameMap.put("la_baguette", "food/la_baguette");
        nameMap.put("berry_bush3", "food/berry_bush3");
        nameMap.put("raw_beef", "food/raw_beef");
        nameMap.put("raw_pork", "food/pork");
        nameMap.put("raw_chicken", "food/chicken");
        nameMap.put("raw_mutton", "food/mutton");
        nameMap.put("raw_salmon", "food/raw_salmon");
        nameMap.put("cooked_beef", "food/beef_cooked");
        nameMap.put("cooked_pork", "food/pork-cooked");
        nameMap.put("cooked_chicken", "food/chicken-cooked");
        nameMap.put("cooked_mutton", "food/cooked_mutton");
        nameMap.put("cooked_salmon", "food/cooked_salmon");
        nameMap.put("rotten_flesh", "food/rotten-flesh");
        nameMap.put("bone", "food/bone_1");
        nameMap.put("bonemeal", "food/bonemeal");
        nameMap.put("dirt", "dirt");
        nameMap.put("snow", "tiles/snowy/snow/snow");
        nameMap.put("ice", "tiles/snowy/ice/ice");
        nameMap.put("sand", "tiles/desert/sand/sand");
        nameMap.put("sandstone", "tiles/desert/sand/sandstone");
        nameMap.put("cactus", "tiles/desert/cactus/cactus");
        nameMap.put("cactus_flower", "tiles/desert/cactus/cactus_flower");
        nameMap.put("grassin_desert", "tiles/desert/vegetation/grassin_desert");
        nameMap.put("dead_bush", "tiles/desert/vegetation/dead_bush");
        nameMap.put("dry_grass", "tiles/desert/vegetation/dry_grass");
        nameMap.put("short_dry_grass", "tiles/desert/vegetation/short_dry_grass");
        nameMap.put("desert_oak_leaves", "tiles/desert/vegetation/oak_leaves_desert");
        nameMap.put("desert_oak_leaves_2", "tiles/desert/vegetation/oak_leaves_desert2");
        nameMap.put("grassin_snow", "tiles/snowy/snow/grassinsnow");
        nameMap.put("grass_snow", "tiles/snowy/snow/grass-snow");
        nameMap.put("spruce_log", "tiles/snowy/spruce/wood_spruce");
        nameMap.put("natural_spruce_log", "tiles/snowy/spruce/wood_spruce");
        nameMap.put("spruce_planks", "tiles/snowy/spruce/woodenplanks_spruce");
        nameMap.put("spruce_leaves", "tiles/snowy/spruce/leaves_spruce");
        nameMap.put("spruce_sapling", "tiles/snowy/spruce/sapling_spruce");
        nameMap.put("fern", "tiles/snowy/spruce/fern");
        nameMap.put("firefly_bush", "tiles/snowy/spruce/fireflybush2");
        nameMap.put("poppy", "tiles/plain/rose2");
        nameMap.put("dandelion", "tiles/plain/dandelion");
        nameMap.put("blue_orchid", "tiles/plain/blue_orchid");
        nameMap.put("azure_bluet", "tiles/plain/azure_bluet");
        nameMap.put("cornflower", "tiles/plain/cornflower");
        nameMap.put("lily_of_the_valley", "tiles/plain/lily_of_the_valley");
        nameMap.put("oxeye_daisy", "tiles/plain/oxeye_daisy");
        nameMap.put("cherry_grass", "tiles/cherry/grass_cherry");
        nameMap.put("cherry_log", "tiles/cherry/cherry_log");
        nameMap.put("natural_cherry_log", "tiles/cherry/cherry_log");
        nameMap.put("cherry_planks", "tiles/cherry/planks_cherry");
        nameMap.put("cherry_leaves", "tiles/cherry/cherry_leaves5");
        nameMap.put("cherry_leaves_5", "tiles/cherry/cherry_leaves5");
        nameMap.put("cherry_leaves_2", "tiles/cherry/cherry_leaves6");
        nameMap.put("cherry_leaves_6", "tiles/cherry/cherry_leaves6");
        nameMap.put("cherry_flower", "tiles/cherry/cherry_flower");
        nameMap.put("cherry_sapling", "tiles/cherry/cherry_sapling");
        nameMap.put("nether_quartz", "quartz_ore");
        nameMap.put("deepslate", "tiles/cave/natural/deepslate");
        nameMap.put("deepslate_co", "tiles/cave/ores_deepslate/deepslate_co");
        nameMap.put("deepslate_io", "tiles/cave/ores_deepslate/deepslate_io");
        nameMap.put("deepslate_go", "tiles/cave/ores_deepslate/deepslate_go");
        nameMap.put("deepslate_do", "tiles/cave/ores_deepslate/deepslate_do");
        nameMap.put("deepslate_copper", "tiles/cave/ores_deepslate/deepslate_copper");
        nameMap.put("ore_lapis_deepslate", "tiles/cave/ores_deepslate/ore_lapis_deepslate");
        nameMap.put("deepslate_ro", "tiles/cave/ores_deepslate/deepslate_ro");
        nameMap.put("deepslate_eo", "tiles/cave/ores_deepslate/deepslate_eo");

        String base = nameMap.getOrDefault(name, name);

        String[] searchDirs = new String[] {"", "atlas/", "items/", "mvp/tiles/", "mvp/player/", "mvp/ui/", "util_block/", "images/gui_invrow/", "Ores/", "tiles/cave/Ores/"};
        String[] exts = new String[] {".png", ".jpg", ".jpeg"};

        for (String dir : searchDirs) {
            for (String ext : exts) {
                String path = dir + base + ext;
                FileHandle fh = Gdx.files.internal(path);
                if (fh.exists()) {
                    try {
                        Texture t = new Texture(fh);
                        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                        ownedTextures.add(t);
                        return new TextureRegion(t);
                    } catch (Exception e) {
                        // continue searching
                    }
                }
            }
        }

        // Last resort: try variants (remove suffixes like _block)
        if (base.endsWith("_block")) {
            return getTexture(base.substring(0, base.length() - 6));
        }

        TextureRegion generatedArmor = generatedArmorFallback(name);
        if (generatedArmor != null) return generatedArmor;

        TextureRegion generatedOre = generatedOreFallback(name);
        if (generatedOre != null) return generatedOre;

        return null;
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
        generatedFallbacks.clear();
    }

    private TextureRegion generatedOreFallback(String name) {
        Color color = oreColor(name);
        if (color == null) return null;
        if (generatedFallbacks.containsKey(name)) return generatedFallbacks.get(name);

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.35f, 0.35f, 0.35f, 1f));
        pixmap.fill();
        pixmap.setColor(new Color(0.24f, 0.24f, 0.24f, 1f));
        for (int y = 0; y < 16; y += 4) {
            pixmap.drawLine(0, y, 15, y);
        }
        pixmap.setColor(color);
        pixmap.fillCircle(4, 5, 2);
        pixmap.fillCircle(10, 4, 2);
        pixmap.fillCircle(7, 11, 2);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        ownedTextures.add(texture);

        TextureRegion region = new TextureRegion(texture);
        generatedFallbacks.put(name, region);
        return region;
    }

    private TextureRegion generatedArmorFallback(String name) {
        if (name == null || !name.startsWith("armor/")) return null;
        if (generatedFallbacks.containsKey(name)) return generatedFallbacks.get(name);

        String itemId = name.substring("armor/".length());
        Color color = armorColor(itemId);
        if (color == null) return null;

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(color);
        drawArmorShape(pixmap, itemId);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        ownedTextures.add(texture);

        TextureRegion region = new TextureRegion(texture);
        generatedFallbacks.put(name, region);
        return region;
    }

    private void drawArmorShape(Pixmap pixmap, String itemId) {
        if (itemId.endsWith("_helmet")) {
            pixmap.fillRectangle(4, 3, 8, 3);
            pixmap.fillRectangle(3, 6, 10, 4);
            pixmap.fillRectangle(5, 9, 2, 2);
            pixmap.fillRectangle(9, 9, 2, 2);
        } else if (itemId.endsWith("_chestplate")) {
            pixmap.fillRectangle(4, 4, 8, 9);
            pixmap.fillRectangle(2, 5, 3, 5);
            pixmap.fillRectangle(11, 5, 3, 5);
            pixmap.fillRectangle(6, 3, 4, 2);
        } else if (itemId.endsWith("_leggings")) {
            pixmap.fillRectangle(4, 4, 8, 3);
            pixmap.fillRectangle(4, 7, 3, 7);
            pixmap.fillRectangle(9, 7, 3, 7);
        } else if (itemId.endsWith("_boots")) {
            pixmap.fillRectangle(3, 8, 4, 5);
            pixmap.fillRectangle(9, 8, 4, 5);
            pixmap.fillRectangle(2, 12, 5, 2);
            pixmap.fillRectangle(9, 12, 5, 2);
        }
    }

    private Color armorColor(String itemId) {
        if (itemId.startsWith("copper_")) return new Color(0.86f, 0.42f, 0.2f, 1f);
        if (itemId.startsWith("iron_")) return new Color(0.78f, 0.82f, 0.84f, 1f);
        if (itemId.startsWith("gold_")) return new Color(1f, 0.78f, 0.18f, 1f);
        if (itemId.startsWith("diamond_")) return new Color(0.16f, 0.86f, 0.95f, 1f);
        return null;
    }

    private Color oreColor(String name) {
        if ("coal_ore".equals(name)) return new Color(0.08f, 0.08f, 0.08f, 1f);
        if ("iron_ore".equals(name)) return new Color(0.72f, 0.52f, 0.36f, 1f);
        if ("gold_ore".equals(name)) return new Color(1f, 0.78f, 0.18f, 1f);
        if ("diamond_ore".equals(name)) return new Color(0.2f, 0.9f, 1f, 1f);
        if ("copper_ore".equals(name)) return new Color(0.9f, 0.45f, 0.2f, 1f);
        if ("lapis_ore".equals(name)) return new Color(0.12f, 0.22f, 0.9f, 1f);
        if ("redstone_ore".equals(name)) return new Color(0.9f, 0.05f, 0.04f, 1f);
        if ("emerald_ore".equals(name)) return new Color(0.1f, 0.85f, 0.32f, 1f);
        if ("quartz_ore".equals(name) || "nether_quartz".equals(name)) return new Color(0.92f, 0.86f, 0.76f, 1f);
        if ("deepslate_co".equals(name)) return new Color(0.08f, 0.08f, 0.08f, 1f);
        if ("deepslate_io".equals(name)) return new Color(0.72f, 0.52f, 0.36f, 1f);
        if ("deepslate_go".equals(name)) return new Color(1f, 0.78f, 0.18f, 1f);
        if ("deepslate_do".equals(name)) return new Color(0.2f, 0.9f, 1f, 1f);
        if ("deepslate_copper".equals(name)) return new Color(0.9f, 0.45f, 0.2f, 1f);
        if ("ore_lapis_deepslate".equals(name)) return new Color(0.12f, 0.22f, 0.9f, 1f);
        if ("deepslate_ro".equals(name)) return new Color(0.9f, 0.05f, 0.04f, 1f);
        if ("deepslate_eo".equals(name)) return new Color(0.1f, 0.85f, 0.32f, 1f);
        return null;
    }
}
