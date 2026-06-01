# Hệ Thống Mob Biome — Tài liệu (Được)

Mục đích: mô tả ngắn gọn, rõ ràng các file đã triển khai cho Mob Biome System và hướng dẫn kiểm thử.

---

## 1. Tổng quan
Hệ thống spawn mob theo biome (FOREST, DESERT, SNOW) sử dụng bảng trọng số (weighted spawn table) để chọn MobType phù hợp tại vị trí spawn. Spawn được kiểm tra an toàn qua SpawnSafety trước khi khởi tạo entity.

Key points:
- Deterministic: sử dụng Random được truyền vào → cùng seed cho cùng sequence
- Phân biệt Passive vs Hostile bằng MobProfile (aggroRadius, attackDamage)
- Dependency injection: Mob nhận Player, PhysicsEngine, World để tách rời hệ thống

---

## 2. Vị trí file chính (absolute)
- core/src/main/java/com/main/game/entities/mob/Mob.java
- core/src/main/java/com/main/game/entities/mob/MobProfile.java
- core/src/main/java/com/main/game/entities/mob/MobBrain.java
- core/src/main/java/com/main/game/entities/mob/MobAssetPack.java
- core/src/main/java/com/main/game/entities/mob/MobRenderer.java
- core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java
- core/src/main/java/com/main/game/worldgen/BiomeSpawnTable.java
- core/src/main/java/com/main/game/screens/GameScreen.java (đã cập nhật import)

---

## 3. Nội dung chính đã triển khai
### MobType & MobProfile
- MobType mở rộng (22 types trong codebase; MVP focus 8 types): COW, PIG, SHEEP, CHICKEN, ZOMBIE, HUSK, SKELETON, STRAY.
- MobProfile.forType(type) trả về cấu hình: health, speed, width/height, patrolRange, aggroRadius, attackDamage.
  - Passive: aggroRadius=0, attackDamage=0
  - Hostile: aggroRadius≈8f, attackDamage>0

### BiomeSpawnTable
- Cấu trúc: Map<BiomeType, List<MobEntry(MobType, weight)>>
- Hàm selectMobForBiome(biome, random) trả về MobType theo cumulative weights.
- If biome missing → fallback FOREST table.

### BiomeMobSpawner
- Entry: spawnInitialMobs(World world, Player player, PhysicsEngine physics, EntityManager em, long seed)
- Vòng lặp spawn: maxAttempts (18), goalSpawned (12).
- Steps: compute position → world.getBiome(x) → select MobType → SpawnSafety.findSurfaceSpawn(world, startX, searchRadius, mobWidth, mobHeight) → new Mob(...) → entityManager.addMob(mob).
- Gọi SpawnSafety trước khi tạo mob (dependency point).

### Mob & AI
- Mob constructor nhận dependencies và MobProfile.
- Mob.update() gọi MobBrain để quyết định trạng thái AI (PATROL, CHASE, ATTACK).
- Passive mobs: chỉ PATROL; Hostile: PATROL→CHASE→ATTACK dựa trên aggroRadius và khoảng cách tới player.

---

## 4. Cấu hình trọng số (hiện tại)
FOREST: COW(20), CHICKEN(18), PIG(15), SHEEP(12), ZOMBIE(5)
DESERT: HUSK(15), SKELETON(10)
SNOW: SHEEP(15), STRAY(12)

(Các số có thể điều chỉnh trong BiomeSpawnTable.initializeBiomes())

---

## 5. Integration points (chỗ cần team inject code)
- World.getBiome(int x): trả về BiomeType tại vị trí (theo cột x).
- SpawnSafety.findSurfaceSpawn(World, startX, searchRadius, mobWidth, mobHeight): trả về Vector2 safe position hoặc null.
- EntityManager.addMob(Mob): đăng ký entity vào hệ thống update/render.

Chú ý: Trong code đã có chỗ comment rõ "// TODO: inject SpawnSafety/ISpawnHelper here" — team cần cung cấp implementation.

---

## 6. Cách test (tóm tắt) — chi tiết trong doc/MOB_BIOME_TESTING.md
- Build: `./gradlew :core:classes`
- Run game: `./gradlew lwjgl3:run` → GameScreen gọi spawnInitialMobs
- Manual: đi vào từng biome và quan sát mobs spawn
- Unit tests: BiomeSpawnTableTest (core/src/test/...)

---

## 7. Known limitations & TODO
1. Asset per-mob chưa đầy đủ (hiện fallback textures).
2. Chỉ spawn initial tại game start; cần runtime chunk-based spawn.
3. Tuning AI/behavior per-biome có thể cần điều chỉnh.

---

## 8. Liên hệ
Implemented by: Được
Date: 2026-05-31

---
