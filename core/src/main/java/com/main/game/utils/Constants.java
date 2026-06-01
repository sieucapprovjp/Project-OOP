package com.main.game.utils;

public final class Constants {

    // TODO(HUY-LEAD):
    //  - Tách nhóm constant theo module nếu file này quá tải (WorldConstants, PhysicsConstants...).
    //  - Đặt quy tắc khi nào được sửa giá trị gameplay để tránh conflict giữa các team.

    private Constants() {} // Không cho khởi tạo

    // ─── Màn hình ────────────────────────────────────────────────
    public static final int    SCREEN_WIDTH         = 1280;
    public static final int    SCREEN_HEIGHT        = 720;
    public static final String GAME_TITLE           = "Minecraft";

    // ─── Tile / World ────────────────────────────────────────────
    public static final int   TILE_SIZE             = 32;       // pixel
    public static final int   WORLD_WIDTH           = 500;      // số tile ngang (team chốt)
    public static final int   WORLD_HEIGHT          = 128;      // số tile dọc

    // ─── Camera / Viewport ───────────────────────────────────────
    // Dùng đơn vị tile cho camera (1 unit = 1 tile) để tránh làm việc với pixel
    public static final float VIEWPORT_WIDTH_TILES  = (float) SCREEN_WIDTH  / TILE_SIZE; // 40f
    public static final float VIEWPORT_HEIGHT_TILES = (float) SCREEN_HEIGHT / TILE_SIZE; // 22.5f

    // ─── Physics ─────────────────────────────────────────────────
    public static final float GRAVITY               = -25f;     // tile/s²
    public static final float TERMINAL_VELOCITY     = -20f;     // tile/s

    // ─── Player ──────────────────────────────────────────────────
    public static final float PLAYER_SPEED          = 5f;       // tile/s (synced with Player.MOVE_SPEED)
    public static final float PLAYER_JUMP_FORCE     = 12f;      // tile/s
    public static final float PLAYER_WIDTH          = 0.8f;     // tile (synced with Player.PLAYER_W)
    public static final float PLAYER_HEIGHT         = 1.8f;     // tile (synced with Player.PLAYER_H)

    // ─── Assets paths ────────────────────────────────────────────
    public static final String TEXTURE_ATLAS_PATH   = "atlas/tiles.atlas";

    // ─── Render layer ────────────────────────────────────────────
    public static final int LAYER_BACKGROUND        = 0;
    public static final int LAYER_WORLD             = 1;
    public static final int LAYER_ENTITY            = 2;
    public static final int LAYER_UI                = 3;

    public static final int CHUNK_SIZE = 16;
}
