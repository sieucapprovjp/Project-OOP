# Hệ Thống Mob Biome - Được Implementation

## Tóm tắt

Được đã xây dựng hệ thống spawn mob theo biome cho Paper Minecraft game. Hệ thống sử dụng **weighted random selection** để chọn mob type phù hợp với từng biome (FOREST, DESERT, SNOW).

## Files được tạo / sửa

### 1. BiomeSpawnTable.java (MỚI)
**Đường dẫn**: `core/src/main/java/com/main/game/worldgen/BiomeSpawnTable.java`

**Mục đích**: Quản lý weighted spawn distribution cho từng biome.

**Tính năng**:
- Enum ánh xạ mob type -> spawn weight per biome
- Method `selectMobForBiome(BiomeType, Random)` → lựa chọn mob type theo weighted distribution
- Deterministic selection (cùng seed = cùng spawn sequence)
- Fallback: nếu biome không định nghĩa, dùng FOREST

**Spawn weights**:
```
FOREST:
  - COW: 20 (cao nhất)
  - CHICKEN: 18
  - PIG: 15
  - SHEEP: 12
  - ZOMBIE: 5 (thấp nhất)

DESERT:
  - HUSK: 15
  - SKELETON: 10

SNOW:
  - SHEEP: 15
  - STRAY: 12
```

### 2. Mob.java (SỬA)
**Đường dẫn**: `core/src/main/java/com/main/game/entities/Mob.java`

**Thay đổi**:
- Mở rộng enum `MobType` từ `{ZOMBIE, SKELETON}` → `{ZOMBIE, SKELETON, COW, PIG, SHEEP, CHICKEN, HUSK, STRAY}`
- Thêm cases xử lý trong constructor cho các mob type mới
- Mỗi mob type có stats riêng:
  - Passive (COW, PIG, SHEEP, CHICKEN): aggroRadius = 0, attackDamage = 0
  - Hostile (HUSK, STRAY): aggroRadius = 8, attackDamage = 3
  - Varied health: COW(18), PIG(14), CHICKEN(8), HUSK(22), STRAY(22)

### 3. BiomeMobSpawner.java (REFACTOR)
**Đường dẫn**: `core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java`

**Thay đổi**:
- Refactor `chooseMobForBiome()` từ switch statement → dùng `BiomeSpawnTable.selectMobForBiome()`
- Thêm comments và docstring chi tiết
- Giữ nguyên API public: `spawnInitialMobs(world, player, physics, entityManager, seed)`

## Cơ chế hoạt động

```
SpawnInitialMobs() entry point
  ↓
Iterate xung quanh player (left/right alternating)
  ↓
Lấy BiomeType tại vị trí target
  ↓
BiomeSpawnTable.selectMobForBiome(biome, random)
  → Dùng weighted distribution để pick mob type
  ↓
SpawnSafety.findSurfaceSpawn()
  → Kiểm tra vị trí an toàn (ground, không block, entity bounds clear)
  ↓
EntityManager.addMob()
  → Thêm Mob vào thế giới
```

## Weighted Selection Algorithm

```java
// Ví dụ FOREST weights: COW(20) + PIG(15) + CHICKEN(18) + SHEEP(12) + ZOMBIE(5) = 70 total
totalWeight = 70
randomPick = random.nextInt(70)  // 0-69

Weighted selection:
- 0-19 (20%)   → COW
- 20-34 (21%)  → PIG  
- 35-52 (25%)  → CHICKEN
- 53-64 (17%)  → SHEEP
- 65-69 (7%)   → ZOMBIE
```

**Tính chất Deterministic**: Cùng seed → cùng sequence mobs.

## Integration Points

### Có sẵn (không cần sửa):
- `BiomeType` enum (FOREST, DESERT, SNOW)
- `SpawnSafety` class (kiểm tra vị trí an toàn)
- `EntityManager` (quản lý entities)
- `PhysicsEngine` (collision/gravity)

### Được sử dụng bởi:
- `GameScreen.show()` → gọi `BiomeMobSpawner.spawnInitialMobs()`
- `World.getBiome(x)` → cung cấp biome type cho spawner

## Testing

### Unit Test: BiomeSpawnTableTest.java
**Đường dẫn**: `core/src/test/java/com/main/game/worldgen/BiomeSpawnTableTest.java`

**Test cases**:
1. `testWeightedSelection()`: Spawn 20 mobs ở FOREST, verify distribution match weights
2. `testBiomeDistribution()`: Verify DESERT chỉ spawn HUSK/SKELETON; SNOW chỉ spawn STRAY/SHEEP

**Chạy test**:
```bash
./gradlew test
```

### Manual Smoke Test (Runtime)
```bash
./gradlew.bat lwjgl3:run
# Game starts → walk xung quanh
# FOREST: see COW, PIG, CHICKEN, SHEEP (ít ZOMBIE)
# DESERT: see HUSK, SKELETON (no passive)
# SNOW: see STRAY, SHEEP (no other passive/hostile)
```

## Acceptance Criteria (DONE)
- ✅ FOREST spawn chủ yếu Passive (COW, PIG, CHICKEN) + ít Zombie
- ✅ DESERT spawn chỉ Hostile (HUSK, SKELETON)
- ✅ SNOW spawn Hostile (STRAY) + Passive (SHEEP)
- ✅ Weighted selection deterministic (same seed = same order)
- ✅ Mob types có stats riêng per type
- ✅ Integration với BiomeMobSpawner đã refactor
- ✅ Comments + docstrings rõ ràng

## Known Limitations / TODO

1. **Asset loading**: Hiện tất cả mobs dùng cow texture (hardcoded). Cần thêm mob-specific assets sau.
   - TODO(duoc-asset): Load assets per MobType từ `mvp/mob/{type}/` folder

2. **Aggressive mobs**: Chưa test fully khi player aggro hostile mobs. Passive mobs không attack (✓ ok).

3. **Dynamic spawning**: Hiện chỉ spawn initial mobs lúc game start. Cần chunk-based spawner sau.
   - TODO(duoc-spawn): Implement runtime spawn manager per chunk

4. **Mob behavior**: Có thể tune patrol range / aggro distance per biome sau.
   - TODO(duoc-ai): Add biome-specific behavior modifiers

## Code Quality Notes

- **Design Pattern**: Factory + Strategy (BiomeSpawnTable = strategy for selection)
- **Maintainability**: Weights defined in one place (initializeBiomes), easy to adjust
- **Determinism**: Uses provided Random instance → consistent with seed
- **Fallback**: If biome undefined, defaults to FOREST spawn table

## Files Modified Summary

| File | Type | Lines Changed | Description |
|------|------|---|---|
| BiomeSpawnTable.java | NEW | 70 | Weighted spawn table per biome |
| Mob.java | EDIT | +80 | Added 6 new MobTypes + stats |
| BiomeMobSpawner.java | REFACTOR | -20 (switched logic) | Uses BiomeSpawnTable instead of switch |
| BiomeSpawnTableTest.java | NEW | 50 | Unit test for weighted selection |

---

**Implemented by**: Được (Mob Biome System)  
**Date**: 2026-05-31  
**Status**: Ready for review & manual testing
