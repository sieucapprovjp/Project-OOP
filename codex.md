# codex.md

## Muc tieu du an
- Du an game 2D phong cach Minecraft, xay dung bang Java + LibGDX.
- Vertical slice uu tien: `world + player + collision + 1 mob`.

## Stack va cau truc
- Engine: LibGDX.
- Build: Gradle (`gradlew.bat`).
- Modules:
  - `core`: logic game, screens, world, entities, physics, navigation.
  - `lwjgl3`: launcher desktop.
  - `assets`: texture/sprite/font.

## Core file map (chi tiet)
- `core/src/main/java/com/main/game/MainGame.java`
  - Entry point cua game (`Game`).
  - Tao resource dung chung: `SpriteBatch`, `AssetManager`, `ScreenRouter`, `GameState`.
  - `render()` goi `screenRouter.flush()` truoc `super.render()`.
  - Chiu trach nhiem dispose resource shared khi thoat game.
- `core/src/main/java/com/main/game/GameState.java`
  - Luu state toan cuc (vd: `brightness`) dung cho nhieu screen.

- `core/src/main/java/com/main/game/navigation/ScreenId.java`
  - Enum dinh danh cac screen (`LOADING`, `MENU`, `GAME`, `PAUSE`, `GAME_OVER`, ...).
- `core/src/main/java/com/main/game/navigation/ScreenRouter.java`
  - Co che chuyen screen an toan qua `request()` + `flush()`.
  - Dam bao thu tu lifecycle: `onExit -> dispose -> create screen moi -> onEnter`.
  - Tranh switch trung screen hien tai.

- `core/src/main/java/com/main/game/screens/BaseScreen.java`
  - Base class cho cac screen, giu reference `MainGame`, `batch`, `camera`, `viewport`.
  - Dinh nghia hook `onEnter()` / `onExit()` va `update()` / `draw()`.
- `core/src/main/java/com/main/game/screens/GameScreen.java`
  - Noi integration gameplay chinh: `World`, `PhysicsEngine`, `Player`, `EntityManager`.
  - Xu ly input test transition (`ESC/P`, `M`, `K`, ...), camera follow, HUD, overlay.
  - Render order: world -> entities -> HUD -> overlay.
- `core/src/main/java/com/main/game/screens/StateScreen.java`
  - Screen trang thai (`PAUSE`, `GAME_OVER`) va dieu huong quay lai game/menu.
- `core/src/main/java/com/main/game/screens/MenuScreen.java`
  - Menu chinh.
- `core/src/main/java/com/main/game/screens/LoadingScreen.java`
  - Man hinh loading va chuyen tiep.
- `core/src/main/java/com/main/game/screens/ModeSelectScreen.java`
  - Chon mode game.
- `core/src/main/java/com/main/game/screens/SettingsScreen.java`
  - Cai dat (anh huong `GameState`).

- `core/src/main/java/com/main/game/world/World.java`
  - Quan ly grid world, generation terrain, spawn point, render theo camera.
  - Noi can uu tien toi uu khi doi sang chunk streaming.
- `core/src/main/java/com/main/game/world/BlockPalette.java`
  - Load/giu texture block dung chung.
  - Co fallback texture va dispose tap trung.
- `core/src/main/java/com/main/game/world/DemoBlockViewer.java`
  - Tool debug de spawn/xem nhanh block trong world.
- `core/src/main/java/com/main/game/worldgen/*.java`
  - Module tao world moi: biome, surface rules, decoration, house structure, spawn safety, mob spawn theo biome.

- `core/src/main/java/com/main/game/blocks/AbstractBlock.java`
  - Contract block co thuoc tinh vat ly: `solid`, `breakable`, `hardness`, `bounds`.
- `core/src/main/java/com/main/game/blocks/SimpleBlock.java`
  - Implement co ban cho block.
- `core/src/main/java/com/main/game/blocks/types/*.java`
  - Nhom block theo domain (`Nature`, `Ore`, `Stone`, `Wood`, `Utility`).

- `core/src/main/java/com/main/game/entities/Entity.java`
  - Base entity: position, velocity, bounds, trang thai song/chet.
- `core/src/main/java/com/main/game/entities/player/Player.java`
  - Input + state machine player + health/damage/respawn.
- `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - Render rig player theo body part, swing arm khi mining.
- `core/src/main/java/com/main/game/entities/mob/Mob.java`
  - Orchestration mob: state, health, physics, goi AI/render.
- `core/src/main/java/com/main/game/entities/mob/MobBrain.java`
  - AI mob (`PATROL`, `CHASE`, `ATTACK`).
- `core/src/main/java/com/main/game/entities/mob/MobProfile.java`
  - Thong so theo loai mob: hostile/passive, aggro, speed, damage, HP.
- `core/src/main/java/com/main/game/entities/mob/MobRenderer.java`
  - Chon animation frame va render mob.
- `core/src/main/java/com/main/game/entities/mob/MobAssetPack.java`
  - Load asset mob theo loai, co fallback neu thieu frame.
- `core/src/main/java/com/main/game/entities/mob/MobMovementHelper.java`
  - Helper nhay qua vat can don gian.
- `core/src/main/java/com/main/game/entities/mob/MobSightHelper.java`
  - Check line-of-sight de tranh tan cong xuyen block.
- `core/src/main/java/com/main/game/entities/EntityManager.java`
  - Update/render/dispose tap trung cho player + danh sach mob.
- `core/src/main/java/com/main/game/entities/EntityState.java`
  - Enum state cho entity (`IDLE`, `RUN`, `JUMP`, `FALL`, ...).

- `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - Xu ly hover/break block, check block bi che, danh sach block khong the pha.
- `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - Render cursor va crack texture khi dang pha block.

- `core/src/main/java/com/main/game/items/DroppedItemManager.java`
  - Quan ly item entity roi tren map.
- `core/src/main/java/com/main/game/items/DroppedItem.java`
  - Vat ly item roi, pickup delay, hut ve player va ghi vao inventory.
- `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - Tao drop tu block bi pha.

- `core/src/main/java/com/main/game/inventory/Inventory.java`
  - Model inventory 36 slot pickup (hotbar + main inventory).
- `core/src/main/java/com/main/game/inventory/InventoryController.java`
  - Toggle inventory, selected hotbar slot.
- `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`
  - Click trai/phai de cam, dat, swap, stack, tach stack.
- `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - Render hotbar/inventory/item dang cam va stack number.
- `core/src/main/java/com/main/game/inventory/InventoryLayout.java`
  - Toa do slot dung chung cho render va hit-test.
- `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - Lookup texture va stack limit item/block.

- `core/src/main/java/com/main/game/physics/PhysicsEngine.java`
  - Gravity + collision/ground detection.
  - Diem nong can nang cap: AABB day du 4 phia, resolve theo truc X/Y.

- `core/src/main/java/com/main/game/utils/Constants.java`
  - Hang so game (viewport, tile size, gravity, toc do, ...).
- `core/src/main/java/com/main/game/utils/TextureManager.java`
  - Quan ly texture dung chung theo key/cache.
- `core/src/main/java/com/main/game/ui/UISkin.java`
  - Dinh nghia style UI (font, mau, skin binding neu dung Scene2D UI).

## Cach chay nhanh
- Chay game desktop: `./gradlew.bat lwjgl3:run`
- Build toan bo: `./gradlew.bat build`
- Chay test (neu co): `./gradlew.bat test`

## Co che da lam (hien tai)
- Screen lifecycle:
  - Tao/dispose `SpriteBatch`, `AssetManager` chi trong `MainGame`.
  - Chuyen screen chi qua `ScreenRouter.request(ScreenId)`.
  - Khi doi screen: `onExit() -> dispose() -> create screen moi -> onEnter()`.
- World:
  - Terrain ngau nhien (khoang 400x128), nhieu lop dia chat.
  - Co culling de giam block render ngoai khung nhin.
- Player:
  - Input co ban: di trai/phai, jump.
  - State co ban: `IDLE`, `RUN`, `JUMP`, `FALL`, `HURT`, `DEAD`.
  - Render da tach sang `entities/player/PlayerRenderer.java`.
  - Co mining arm animation khi dang pha block.
- Physics:
  - Da co gravity + ground detection co ban.
  - Da resolve collision theo truc X/Y cho entity va block solid.
- Blocks/Assets:
  - Co `BlockPalette`, `TextureManager`, bo block type classes.
  - Da cap nhat asset path sau khi xoa cac file `*_1.png`.
  - HUD texture co fallback khi thieu frame rieng le.
- Block breaking:
  - Co cursor hover block va crack animation tu `assets/cursor`.
  - Chi cho pha block visible, khong pha block bi block khac che.
  - Co danh sach block khong the pha (`bedrock`).
  - Khi pha xong, block spawn dropped item.
- Dropped item:
  - Item roi co physics co ban: gravity, bounce ngang, snap dat, friction.
  - Item hut ve player sau pickup delay va co the ghi vao inventory.
- Inventory/hotbar:
  - Hotbar render item dung texture.
  - Mo inventory bang `E`.
  - Click trai/phai de cam, dat, swap, stack va tach stack.
  - Stack number dung font asset trong `assets/fonts`.
- Mob:
  - Da co mob hostile: `ZOMBIE`, `HUSK`, `SKELETON`.
  - Da co mob passive: `COW`, `PIG`, `SHEEP`, `CHICKEN`.
  - Hostile aggro player trong ban kinh 8 block; passive khong tan cong.
  - Co patrol/chase/attack, line-of-sight check, nhay qua vat can co ban.
  - Da refactor mob thanh `Mob`, `MobBrain`, `MobProfile`, `MobRenderer`, `MobAssetPack`, helper movement/sight.
- Worldgen MVP:
  - `World.generate(seed)` da tach sang `WorldGenerator.generate(world, seed)`.
  - Da co 3 biome: `FOREST`, `DESERT`, `SNOW`.
  - Da co village/house structure don gian tren mat dat phang.
  - Da co spawn safety helper va initial mob spawn theo biome.
  - Da them block biome: `snow`, `ice`, `sandstone`, `cactus` voi generated texture fallback neu chua co asset.
  - Da them mob biome `STRAY` dung profile rieng va fallback skeleton asset.

## Co che dang thieu/uu tien tiep
- Chay `./gradlew.bat lwjgl3:run` sau moi lan doi asset de bat runtime missing texture.
- Them projectile that cho `SKELETON` thay vi damage truc tiep.
- Them co che player danh mob va mob drop item/loot.
- Nang cap mob spawn system thanh spawn theo thoi gian/chunk thay vi initial spawn luc vao game.
- Xu ly dropped item overflow khi dong inventory ma inventory day.
- Chuan hoa asset atlas va naming convention de tranh crash do doi/xoa file.

## Tien do worldgen MVP 2026-05-18
- Da tach logic tao the gioi khoi `World` sang package `worldgen`.
- Da implement biome noise cho forest/desert/snow va luu biome theo cot trong `World`.
- Da them decoration co ban: cay forest/snow, cactus desert, ice patch snow.
- Da them village/house structure placer voi dieu kien ground tuong doi phang.
- Da them `SpawnSafety` cho entity/structure spawn validation.
- Da thay spawn mob test trong `GameScreen` bang `BiomeMobSpawner.spawnInitialMobs(...)`.
- Da them block moi `snow`, `ice`, `sandstone`, `cactus` vao palette/registry.
- Verify: `./gradlew.bat classes` pass.

## Tien do cuoi ngay 2026-05-18
- Menu/New Game:
  - Man hinh New Game da dung background random tu `assets/stage`.
  - Da can lai setting/done button.
- Block breaking:
  - Da co hover cursor, crack animation, mining arm animation.
  - Da check visible block va chan pha block bi che.
  - Da chan block khong the pha nhu `bedrock`.
- Dropped item:
  - Da co dropped item entity tu block bi pha.
  - Da co physics + pickup/suction vao inventory.
- Inventory:
  - Da co hotbar, inventory panel, stack number font, click left/right de quan ly item.
  - Da can lai slot layout theo texture `images/gui_invrow/inventory.png`.
- Player:
  - Da tach package `entities/player`.
  - Da sua asset path sau khi xoa file `*_1.png`.
- Mob:
  - Da them nhieu loai mob passive/hostile.
  - Hostile aggro trong 8 block, passive chi patrol.
  - Da tach package `entities/mob` va chia nho file logic/render/profile/assets.
- Asset/runtime:
  - Da ra soat cac path asset trong Java.
  - Da them fallback cho HUD texture thieu frame.
- Verify:
  - `./gradlew.bat classes` pass sau refactor package `entities`.
  - Can chay tiep `./gradlew.bat lwjgl3:run` de verify runtime game sau khi asset thay doi.

## Quy tac lam viec voi Codex
- Khong doi kien truc lon neu chua duoc yeu cau.
- Khi them tinh nang moi, uu tien tao class/file rieng theo module phu hop; file integration nhu `GameScreen` chi nen goi API ngan (`update/render/dispose`) thay vi nhan toan bo logic moi.
- Moi thay doi phai bao gom:
  - File da sua.
  - Co che nao bi anh huong.
  - Cach verify trong game (input/scene expected).
- Uu tien fix theo blocker gameplay truoc (physics, input, state).

## Quy chuan comment trong code
- Chi comment `why` hoac trade-off; khong mo ta lai dong code hien ro nghia.
- O logic game loop/physics, comment ngan de ghi ro assumption.
- TODO phai co ngu canh:
  - Pham vi viec can lam.
  - Dieu kien hoan thanh.
  - Nguoi xu ly (neu co).
- Mau:
  - `// Why: resolve Y truoc de tranh ket goc khi roi xuong block`
  - `// TODO(lhung): bo sung collision canh trai/phai cho AABB truoc merge physics`

## Quy chuan comment PR/Review
- Dung muc do: `blocker`, `major`, `minor`, `nit`.
- Moi comment nen co:
  - Van de quan sat duoc.
  - Rui ro gameplay/bug.
  - De xuat sua cu the.
- Uu tien review vao: crash, collision sai, state sai, leak tai nguyen, pha lifecycle screen.

## Rule bat buoc cho screen va tai nguyen
- Khong goi `setScreen()` truc tiep trong module gameplay.
- Khong dispose `batch`/`assetManager` trong Screen.
- Neu can doi man, goi `game.getScreenRouter().request(...)`.

## Checklist khi nhan task
1. Nhac lai task + module bi anh huong (`world`, `player`, `physics`, `screen`, `assets`).
2. Doc file lien quan trong `core/src/main/java/com/main/game/...`.
3. Sua nho, dung scope.
4. Chay lai `lwjgl3:run` hoac test lien quan.
5. Bao cao ket qua va huong test thu cong trong game.

## Ghi chu team
- Merge vao `main` qua PR, tranh merge thang khi chua review.
- Uu tien xu ly blocker >24h (dac biet Physics dang block cac nhanh khac).
