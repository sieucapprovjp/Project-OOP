package com.main.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.AbstractBlock;
import com.main.game.worldgen.BiomeType;
import com.main.game.worldgen.WorldGenerator;
import com.main.game.utils.Constants;

/**
 * Quản lý toàn bộ map game: lưu trữ và truy xuất block.
 * KIÊN sẽ implement terrain generation và camera control.
 *
 * Hiện tại có sẵn:
 *  - Mảng 2D lưu block
 *  - getBlock() / setBlock() để truy xuất
 *  - isInBounds() kiểm tra tọa độ hợp lệ
 *
 * Kiên cần implement thêm:
 *  - generate() — sinh địa hình
 *  - render()   — vẽ các tile nhìn thấy trong camera
 *
 * TODO(KIEN-WORLD):
 *  - Terrain hiện tại là bản nền (noise-lite), cần nâng cấp perlin/simplex + cave.
 *  - Tích hợp chunk data để tối ưu streaming world lớn.
 *  - Thêm API lấy spawn point cho player.
 */
public class World {

    private final AbstractBlock[][] blocks;
    private final BiomeType[] biomes;
    public final int width;
    public final int height;

    public World() {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.blocks = new AbstractBlock[width][height];
        this.biomes = new BiomeType[width];
    }

    /** Lấy block tại tọa độ tile (x, y) */
    public AbstractBlock getBlock(int x, int y) {
        if (!isInBounds(x, y)) return null;
        return blocks[x][y];
    }

    /** Đặt block tại tọa độ tile (x, y) */
    public void setBlock(int x, int y, AbstractBlock block) {
        if (!isInBounds(x, y)) return;
        blocks[x][y] = block;
    }

    public void setBiome(int x, BiomeType biome) {
        if (x < 0 || x >= width || biome == null) return;
        biomes[x] = biome;
    }

    public BiomeType getBiome(int x) {
        if (x < 0 || x >= width) return BiomeType.FOREST;
        return biomes[x] != null ? biomes[x] : BiomeType.FOREST;
    }

    /** Kiểm tra tọa độ có nằm trong world không */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /** Block có solid không — Dùng cho vật lý/collision */
    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    /**
     * Sinh địa hình ngẫu nhiên bằng Fractal/Value Noise 1D
     */
    public void generate(long seed) {
        WorldGenerator.generate(this, seed);
    }

    /**
     * Chỉ vẽ các block nằm trong tầm nhìn của Camera (Culling)
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        int minX = Math.max(0, (int) Math.floor(camera.position.x - halfW) - 1);
        int maxX = Math.min(width - 1, (int) Math.ceil(camera.position.x + halfW) + 1);
        int minY = Math.max(0, (int) Math.floor(camera.position.y - halfH) - 1);
        int maxY = Math.min(height - 1, (int) Math.ceil(camera.position.y + halfH) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = blocks[x][y];
                if (block != null) {
                    block.render(batch);
                }
            }
        }
    }

    /**
     * TÌM VỊ TRÍ SPAWN CHO NHÂN VẬT
     * Thả người chơi xuống mặt đất ở ngay giữa bản đồ.
     */
    public Vector2 getSpawnPoint() {
        int spawnX = width / 2;

        // Quét từ trên trời xuống dưới đất tại cột giữa map để tìm block cứng đầu tiên
        for (int y = height - 1; y >= 0; y--) {
            if (isSolid(spawnX, y)) {
                // Trả về tọa độ ngay TRÊN block đó để nhân vật không bị kẹt vào đất
                return new Vector2(spawnX, y + 1);
            }
        }

        // Tọa độ dự phòng nếu lỗi map
        return new Vector2(spawnX, height / 2f);
    }

}
