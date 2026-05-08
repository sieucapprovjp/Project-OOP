package com.main.game.blocks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.main.game.utils.Constants;

/**
 * Abstract base cho mọi loại block: Dirt, Stone, Wood, Air...
 *
 * Việt Hưng extend class này để tạo từng loại block cụ thể.
 * Mỗi block chiếm đúng 1 ô tile (TILE_SIZE x TILE_SIZE).
 *
 * TODO(VHUNG-BLOCKS):
 *  - Tạo block concrete classes + metadata chuẩn (solid/breakable/hardness).
 *  - Kết nối TextureAtlas thật (thay texture tạm).
 *  - Thống nhất blockId để WorldGen và Inventory dùng chung.
 */
public abstract class AbstractBlock {

    // ─── Vị trí trong world (đơn vị tile) ────────────────────────
    protected int  tileX;
    protected int  tileY;

    // ─── Thuộc tính block ─────────────────────────────────────────
    protected boolean  isSolid;      // có collision không (Lâm Hùng dùng)
    protected boolean  isBreakable;  // có thể đào không
    protected float   hardness;     // độ cứng — thời gian đào (giây)
    protected String  blockId;      // ID định danh, vd: "dirt", "stone"

    // ─── Collision box (luôn = 1 tile, Lâm Hùng dùng) ────────────
    protected Rectangle bounds;

    public AbstractBlock(int tileX, int tileY, String blockId,
                         boolean isSolid, boolean isBreakable, float hardness) {
        this.tileX       = tileX;
        this.tileY       = tileY;
        this.blockId     = blockId;
        this.isSolid     = isSolid;
        this.isBreakable = isBreakable;
        this.hardness    = hardness;
        this.bounds      = new Rectangle(tileX, tileY, 1f, 1f); // 1 tile
    }

    // ─── Abstract ─────────────────────────────────────────────────

    /** Trả về texture region để vẽ block (lấy từ TextureAtlas) */
    public abstract TextureRegion getTexture();

    // ─── Render ───────────────────────────────────────────────────

    /** Vẽ block — Kiên gọi cái này khi render world */
    public void render(SpriteBatch batch) {
        TextureRegion tex = getTexture();
        if (tex != null) {
            batch.draw(tex,
                tileX,   tileY,     // position (đơn vị tile, camera lo việc scale)
                1f,      1f         // width = height = 1 tile
            );
        }
    }

    // ─── Getters ──────────────────────────────────────────────────
    public int       getTileX()       { return tileX;       }
    public int       getTileY()       { return tileY;       }
    public boolean   isSolid()        { return isSolid;     }
    public boolean   isBreakable()    { return isBreakable; }
    public float     getHardness()    { return hardness;    }
    public String    getBlockId()     { return blockId;     }
    public Rectangle getBounds()      { return bounds;      }
    public boolean   isAir()          { return !isSolid && blockId.equals("air"); }
}
