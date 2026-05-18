package com.main.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.main.game.blocks.AbstractBlock;
import com.main.game.utils.TextureManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

// IMPORT CỤ THỂ TỪNG CLASS CHA ĐỂ MÁY KHÔNG NHẦM LÀ PACKAGE
import com.main.game.blocks.types.UtilityBlocks;
import com.main.game.blocks.types.NatureBlocks;
import com.main.game.blocks.types.StoneBlocks;
import com.main.game.blocks.types.WoodBlocks;

/**
 * BlockPalette — Lazy-init texture palette cho các loại block.
 *
 * Dùng lazy init thay vì static final để đảm bảo texture chỉ được
 * load SAU KHI libGDX Application đã khởi tạo xong (Gdx.files sẵn sàng).
 */
public final class BlockPalette {

    // ─── Lazy-init texture fields ─────────────────────────────────
    private static TextureRegion grass;
    private static TextureRegion dirt;
    private static TextureRegion stone;
    private static TextureRegion bedrock;
    private static TextureRegion sand;
    private static TextureRegion wood;
    private static TextureRegion leaves;
    private static TextureRegion planks;
    private static TextureRegion snow;
    private static TextureRegion ice;
    private static TextureRegion sandstone;
    private static TextureRegion cactus;
    private static final List<Texture> generatedTextures = new ArrayList<>();
    private static boolean initialized = false;

    private BlockPalette() {}

    /** Đảm bảo texture đã được load. Gọi lần đầu sẽ load, các lần sau bỏ qua. */
    private static void ensureInitialized() {
        if (initialized) return;
        TextureManager tm = TextureManager.getInstance();
        grass   = tm.getTexture("grass_block");
        dirt    = tm.getTexture("dirt");
        stone   = tm.getTexture("stone");
        bedrock = tm.getTexture("bedrock");
        sand    = tm.getTexture("sand");
        wood    = tm.getTexture("oak_log");
        leaves  = tm.getTexture("oak_leaves");
        planks  = tm.getTexture("oak_planks");
        snow = textureOrGenerated(tm, "snow", Color.WHITE);
        ice = textureOrGenerated(tm, "ice", new Color(0.55f, 0.85f, 1f, 1f));
        sandstone = textureOrGenerated(tm, "sandstone", new Color(0.78f, 0.67f, 0.38f, 1f));
        cactus = textureOrGenerated(tm, "cactus", new Color(0.1f, 0.55f, 0.18f, 1f));
        initialized = true;
    }

    // ─── Public getters (lazy) ────────────────────────────────────
    public static TextureRegion getGrass()   { ensureInitialized(); return grass;   }
    public static TextureRegion getDirt()    { ensureInitialized(); return dirt;    }
    public static TextureRegion getStone()   { ensureInitialized(); return stone;   }
    public static TextureRegion getBedrock() { ensureInitialized(); return bedrock; }
    public static TextureRegion getSand()    { ensureInitialized(); return sand;    }
    public static TextureRegion getWood()    { ensureInitialized(); return wood;    }
    public static TextureRegion getLeaves()  { ensureInitialized(); return leaves;  }
    public static TextureRegion getPlanks()  { ensureInitialized(); return planks;  }
    public static TextureRegion getSnow()    { ensureInitialized(); return snow;    }
    public static TextureRegion getIce()     { ensureInitialized(); return ice;     }
    public static TextureRegion getSandstone() { ensureInitialized(); return sandstone; }
    public static TextureRegion getCactus()  { ensureInitialized(); return cactus;  }

    // ─── Compat: giữ tên field cũ dưới dạng getter ───────────────
    // Để code cũ dùng BlockPalette.GRASS vẫn compile, ta giữ public static fields
    // nhưng chúng sẽ được gán sau khi init. Các chỗ dùng nên migrate sang getter.
    //
    // MIGRATION: Thay BlockPalette.GRASS → BlockPalette.getGrass(), v.v.

    public static AbstractBlock getBlockByInt(int id, int x, int y) {
        switch (id) {
            // Dùng cấu trúc: TênClassCha.TênClassCon
            case 0:  return new UtilityBlocks.AirBlock(x, y);
            case 1:  return new NatureBlocks.GrassBlockBlock(x, y);
            case 2:  return new NatureBlocks.DirtBlock(x, y);
            case 3:  return new StoneBlocks.StoneBlock(x, y);
            case 4:  return new WoodBlocks.OakLogBlock(x, y);
            case 5:  return new WoodBlocks.OakPlanksBlock(x, y);
            case 6:  return new WoodBlocks.OakLeavesBlock(x, y);
            case 7:  return new StoneBlocks.BedrockBlock(x, y);
            case 9:  return new NatureBlocks.SandBlock(x, y);
            case 10: return new NatureBlocks.SnowBlock(x, y);
            case 11: return new NatureBlocks.IceBlock(x, y);
            case 12: return new StoneBlocks.SandstoneBlock(x, y);
            case 13: return new NatureBlocks.CactusBlock(x, y);

            default: return new UtilityBlocks.AirBlock(x, y);
        }
    }

    public static void dispose() {
        initialized = false;
        grass = dirt = stone = bedrock = sand = wood = leaves = planks = null;
        snow = ice = sandstone = cactus = null;
        for (Texture texture : generatedTextures) {
            texture.dispose();
        }
        generatedTextures.clear();
    }

    private static TextureRegion textureOrGenerated(TextureManager tm, String name, Color fallbackColor) {
        TextureRegion texture = tm.getTexture(name);
        if (texture != null) {
            return texture;
        }
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(fallbackColor);
        pixmap.fill();
        pixmap.setColor(fallbackColor.cpy().mul(0.82f));
        for (int i = 0; i < 16; i += 4) {
            pixmap.drawLine(0, i, 15, i);
        }
        Texture generated = new Texture(pixmap);
        generated.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        generatedTextures.add(generated);
        return new TextureRegion(generated);
    }
}
