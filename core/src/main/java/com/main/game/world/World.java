

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
package com.main.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.main.game.blocks.AbstractBlock;
import com.main.game.blocks.SimpleBlock;
import com.main.game.worldgen.BiomeType;
import com.main.game.utils.Constants;
import com.main.game.utils.NoiseUtils;
import com.badlogic.gdx.math.GridPoint2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Quản lý toàn bộ map game (Đã nâng cấp lên hệ thống Chunk vô tận)
 */
public class World {

    private final long seed;
    private final Map<GridPoint2, Chunk> chunks;
    private final Map<Integer, BiomeType> biomes; // Dùng Map để hỗ trợ tọa độ X vô tận
    public final int width;
    public final int height;

    public World(long seed) {
        this.seed = seed;
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.chunks = new HashMap<>();
        this.biomes = new HashMap<>();
    }

    private GridPoint2 getChunkCoord(int worldX, int worldY) {
        int cx = Math.floorDiv(worldX, Constants.CHUNK_SIZE);
        int cy = Math.floorDiv(worldY, Constants.CHUNK_SIZE);
        return new GridPoint2(cx, cy);
    }

    public AbstractBlock getBlock(int x, int y) {
        if (y < 0 || y >= height) return null; // Chỉ giới hạn trần và đáy, X vô tận
        GridPoint2 chunkPos = getChunkCoord(x, y);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) return null;
        return chunk.getBlock(Math.floorMod(x, Constants.CHUNK_SIZE), Math.floorMod(y, Constants.CHUNK_SIZE));
    }

    public void setBlock(int x, int y, AbstractBlock block) {
        if (y < 0 || y >= height) return;
        GridPoint2 chunkPos = getChunkCoord(x, y);
        Chunk chunk = chunks.computeIfAbsent(chunkPos, k -> new Chunk(chunkPos.x, chunkPos.y));
        chunk.setBlock(Math.floorMod(x, Constants.CHUNK_SIZE), Math.floorMod(y, Constants.CHUNK_SIZE), block);
    }

    public void setBiome(int x, BiomeType biome) {
        if (biome == null) return;
        biomes.put(x, biome);
    }

    public BiomeType getBiome(int x) {
        return biomes.getOrDefault(x, BiomeType.FOREST);
    }

    public boolean isInBounds(int x, int y) {
        return y >= 0 && y < height; // X vô tận, không kiểm tra width nữa
    }

    public boolean isSolid(int x, int y) {
        AbstractBlock block = getBlock(x, y);
        return block != null && block.isSolid();
    }

    /** Streaming Map: Load Chunk xung quanh Camera */
    public void update(OrthographicCamera camera) {
        int camChunkX = Math.floorDiv((int) camera.position.x, Constants.CHUNK_SIZE);
        int camChunkY = Math.floorDiv((int) camera.position.y, Constants.CHUNK_SIZE);
        int loadRadius = 4;

        for (int cx = camChunkX - loadRadius; cx <= camChunkX + loadRadius; cx++) {
            for (int cy = camChunkY - loadRadius; cy <= camChunkY + loadRadius; cy++) {
                GridPoint2 pos = new GridPoint2(cx, cy);
                if (!chunks.containsKey(pos)) {
                    chunks.put(pos, new Chunk(cx, cy));
                    generateChunk(cx, cy);
                }
            }
        }
    }

    /** Logic sinh Map + Hang động (Đã fix lỗi Overflow) */
    public void generateChunk(int chunkX, int chunkY) {
        int startX = chunkX * Constants.CHUNK_SIZE;
        int endX = startX + Constants.CHUNK_SIZE;
        int startY = chunkY * Constants.CHUNK_SIZE;
        int endY = startY + Constants.CHUNK_SIZE;

        Random random = new Random(this.seed + chunkX);
        int baseGround = height / 2;
        float amplitude = 12f;
        float frequency = 0.04f;

        NoiseUtils noiseUtils = new NoiseUtils(this.seed);

        for (int x = startX; x < endX; x++) {
            // Set Biome mặc định cho tọa độ này
            setBiome(x, BiomeType.FOREST);

            float noiseVal = getSmoothNoise1D(x * frequency, this.seed);
            float detailNoise = getSmoothNoise1D(x * frequency * 3f, this.seed + 1) * 0.2f;

            int surface = baseGround + (int) ((noiseVal + detailNoise) * amplitude);
            surface = Math.max(8, Math.min(height - 4, surface));

            for (int y = startY; y < endY; y++) {
                AbstractBlock block = null;
                if (y == 0) {
                    block = new SimpleBlock(x, y, "bedrock", true, false, 999f, BlockPalette.getBedrock());
                } else if (y < surface - 3 && y > 0) {
                    // Logic hang động chuẩn: KHÔNG cộng seed vào tọa độ
                    double caveNoise = noiseUtils.noise2D(x * 0.05f, y * 0.05f);
                    if (caveNoise >= -0.25) {
                        block = new SimpleBlock(x, y, "stone", true, true, 1.2f, BlockPalette.getStone());
                    }
                } else if (y >= surface - 3 && y < surface) {
                    block = new SimpleBlock(x, y, "dirt", true, true, 0.7f, BlockPalette.getDirt());
                } else if (y == surface) {
                    block = new SimpleBlock(x, y, "grass", true, true, 0.6f, BlockPalette.getGrass());
                }

                if (block != null) setBlock(x, y, block);
            }

            // Trồng cây
            if (startY <= surface + 1 && endY >= surface + 1) {
                if (x % 29 == 0 && random.nextFloat() < 0.65f) {
                    int trunkBaseY = surface + 1;
                    int trunkHeight = 3 + random.nextInt(2);
                    for (int ty = 0; ty < trunkHeight; ty++) {
                        setBlock(x, trunkBaseY + ty, new SimpleBlock(x, trunkBaseY + ty, "wood", true, true, 0.9f, BlockPalette.getWood()));
                    }
                    int leafY = trunkBaseY + trunkHeight;
                    for (int lx = x - 1; lx <= x + 1; lx++) {
                        for (int ly = leafY - 1; ly <= leafY; ly++) {
                            if (getBlock(lx, ly) == null) {
                                setBlock(lx, ly, new SimpleBlock(lx, ly, "leaves", false, true, 0.2f, BlockPalette.getLeaves()));
                            }
                        }
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        int minX = (int) Math.floor(camera.position.x - halfW) - 1;
        int maxX = (int) Math.ceil(camera.position.x + halfW) + 1;
        int minY = Math.max(0, (int) Math.floor(camera.position.y - halfH) - 1);
        int maxY = Math.min(height - 1, (int) Math.ceil(camera.position.y + halfH) + 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                AbstractBlock block = getBlock(x, y);
                if (block != null) block.render(batch);
            }
        }
    }

    public Vector2 getSpawnPoint() {
        int spawnX = width / 2;
        for (int y = height - 1; y >= 0; y--) {
            if (isSolid(spawnX, y)) return new Vector2(spawnX, y + 3);
        }
        return new Vector2(spawnX, height / 2f);
    }

    private float getSmoothNoise1D(float x, long seed) {
        int intX = (int) Math.floor(x);
        float fracX = x - intX;
        float v1 = getSeededRandom(intX, seed);
        float v2 = getSeededRandom(intX + 1, seed);
        float f = (1f - (float)Math.cos(fracX * Math.PI)) * 0.5f;
        return v1 * (1f - f) + v2 * f;
    }

    private float getSeededRandom(int x, long seed) {
        long n = x * 374761393L + seed * 668265263L;
        n = (n ^ (n >> 13)) * 1274126177L;
        return (((n & 0x7FFFFFFF) / (float) 0x7FFFFFFF) * 2f) - 1f;
    }
}
