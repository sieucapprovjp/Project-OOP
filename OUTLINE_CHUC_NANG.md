# OUTLINE CHỨC NĂNG GAME — PAPER MINECRAFT

## 1. Hệ thống màn hình
- Menu chính (MENU) — vào game / thoát
- Màn chơi (GAME) — gameplay chính
- Tạm dừng (PAUSE) — nhấn P/ESC để pause/resume
- Kết thúc (GAME OVER) — khi player chết
- Điều hướng giữa các screen qua `ScreenRouter`

## 2. Thế giới (World)
- Sinh terrain ngẫu nhiên 400×128 tiles từ seed
- Tạo thế giới qua module `worldgen`, `World.generate(seed)` chỉ ủy quyền cho generator
- 3 biome MVP: forest, desert, snow
- 5 lớp địa chất: bedrock → stone/sandstone → dirt/sand → grass/snow/sand
- Decoration tự động: cây, cactus, ice patch
- Structure MVP: village/house đơn giản đặt trên mặt đất hợp lệ
- Block cơ bản: grass, stone, bedrock, sand, wood, leaves, planks, dirt
- Block biome mới: snow, ice, sandstone, cactus
- Mỗi block có thuộc tính: solid, breakable, hardness
- Frustum culling — chỉ render block trong tầm nhìn camera

## 3. Nhân vật (Player)
- Di chuyển trái/phải (A/D, ←/→)
- Nhảy (SPACE/W/↑) khi đứng trên mặt đất
- 6 trạng thái: IDLE, RUN, JUMP, FALL, HURT, DEAD
- Animation riêng cho mỗi trạng thái
- Flip sprite theo hướng di chuyển
- Hệ thống máu 20 HP, nhận damage, hurt blink

## 4. Mob (AI)
- Hostile: Zombie, Husk, Skeleton, Stray
- Passive: Cow, Pig, Sheep, Chicken
- AI 3 trạng thái:
  - PATROL — đi tuần trong phạm vi
  - CHASE — phát hiện player → đuổi theo
  - ATTACK — trong tầm đánh → gây damage
- Aggro/de-aggro theo khoảng cách
- Animation walk, idle, hurt
- Initial spawn theo biome: forest ưu tiên passive, desert ưu tiên husk/skeleton, snow ưu tiên stray/sheep

## 5. Vật lý (Physics)
- Trọng lực kéo entity xuống
- Ground detection — chạm block solid → dừng rơi
- Entity Manager quản lý update/render tất cả entity

## 6. Camera
- Theo dõi player với lerp smoothing
- Giới hạn trong biên world
- Zoom có thể điều chỉnh
- Fallback WASD khi player chết

## 7. Quản lý tài nguyên
- SpriteBatch + AssetManager dùng chung
- BlockPalette load texture tập trung, fallback khi lỗi
- Dispose pattern giải phóng bộ nhớ đúng cách
