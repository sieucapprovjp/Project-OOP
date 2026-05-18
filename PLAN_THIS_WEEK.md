# Kế Hoạch 1 Tuần: Biome, Structure, Block/Mob Mới

## Tóm Tắt
- Tuần này tập trung vào nền tảng tạo thế giới mới: biome, village/house structure, block mới và mob mới theo biome.
- Phần final boss/raid/boss event sẽ để bạn tự làm riêng, không đưa vào phân công cho 4 thành viên.
- Thành viên thực hiện: Kiên + Lâm Hùng, Việt Hùng, Được. Huy/Codex giữ vai trò điều phối, review và cập nhật tài liệu.

## Thay Đổi Chính
- Tách logic tạo thế giới khỏi `World.generate(seed)` sang module worldgen mới.
- Thêm 3 biome đầu tiên: `FOREST`, `DESERT`, `SNOW`.
- Thêm structure đầu tiên: village/house đơn giản, đặt trên mặt đất hợp lệ.
- Thêm block biome tối thiểu: `snow`, `ice`, `sandstone`, `cactus`.
- Thêm mob variants theo biome bằng hệ thống `MobType`, `MobProfile`, `MobAssetPack`.
- Không implement final boss, raid hoặc boss arena trong kế hoạch này.

## Phân Việc
- Kiên + Lâm Hùng: worldgen, biome, structure, spawn safety
  - Tạo module worldgen mới và để `World.generate(seed)` chỉ gọi vào module này.
  - Implement biome noise cho forest/desert/snow.
  - Implement surface rules và decoration cơ bản: cây, cactus, snow/ice.
  - Implement village/house placement trên ground phẳng.
  - Thêm helper kiểm tra spawn an toàn cho player/mob/structure.
  - Done khi world có 3 biome rõ ràng, house spawn hợp lệ và entity không kẹt block.

- Việt Hùng: block mới, asset, registry
  - Thêm `snow`, `ice`, `sandstone`, `cactus`.
  - Cập nhật `BlockPalette`, block type, `ItemRegistry`, texture mapping, hardness, solid/breakable.
  - Đảm bảo block mới render, phá/drop/pickup đúng.
  - Done khi không còn crash missing texture và block mới dùng được trong world/inventory.

- Được: mob biome
  - Thêm mob variants theo biome.
  - Dùng lại AI hostile/passive hiện có nếu đủ.
  - Mở rộng `MobType`, `MobProfile`, `MobAssetPack` cho mob mới.
  - Kết nối spawn mob theo biome với helper spawn an toàn của Kiên + Lâm Hùng.
  - Done khi mob biome spawn đúng khu vực, passive không tấn công và hostile aggro đúng.

## Test Plan
- Build: `./gradlew.bat classes`.
- Runtime smoke: `./gradlew.bat lwjgl3:run`.
- Test thủ công:
  - World cùng seed tạo kết quả ổn định.
  - Có forest/desert/snow nhìn khác nhau.
  - Village/house không nằm trong đất, không bị cắt đôi.
  - Block mới render đúng, phá được nếu breakable, drop/pickup được.
  - Mob biome spawn đúng khu vực.
  - Passive không tấn công; hostile aggro đúng.
  - Player, mob và dropped item không kẹt/xuyên block quanh structure mới.

## Giả Định
- Boss/final event/raid không nằm trong scope của team tuần này.
- Village/house là structure đầu tiên.
- Không làm chunk streaming, save/load hoặc cave generation đầy đủ trong tuần này.
- Huy/Codex sẽ cập nhật `codex.md`, `TODO_TEAM.md`, `OUTLINE_CHUC_NANG.md` khi chuyển sang implementation mode.
