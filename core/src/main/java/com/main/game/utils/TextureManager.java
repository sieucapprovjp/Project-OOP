package com.main.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.List;

public class TextureManager {
    private static TextureManager instance;
    private TextureAtlas atlas;
    private final List<Texture> ownedTextures = new ArrayList<>();

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
        nameMap.put("oak_leaves", "leaves");
        nameMap.put("oak_planks", "planks");
        nameMap.put("dirt", "dirt");
        nameMap.put("snow", "snow");
        nameMap.put("ice", "ice");
        nameMap.put("sandstone", "sandstone");
        nameMap.put("cactus", "cactus");

        String base = nameMap.getOrDefault(name, name);

        String[] searchDirs = new String[] {"", "atlas/", "mvp/tiles/", "mvp/player/", "mvp/ui/"};
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

        return null;
    }

    public void dispose() {
        if (atlas != null) atlas.dispose();
        for (Texture t : ownedTextures) t.dispose();
        ownedTextures.clear();
    }
}
