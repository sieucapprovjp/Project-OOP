# Team TODO List (Foundation)

> Cap nhat lan cuoi: 2026-05-02
> Trang thai: CHUA MERGE — moi nguoi dang code tren branch rieng.

## Branch Map

| Thanh vien | Branch | Commit cuoi | Trang thai |
|------------|--------|-------------|------------|
| Huy (Lead) | `huy` / `main` | `last commit` | 🟢 Base code on dinh |
| Kien       | `kien` | `update noise terrain, culling, getspawnpoint` | 🟢 Dang active |
| Duoc       | `Duoc` | `Sot jump do chua co ground` | 🟡 Can review |
| Viet Hung  | `Hung` (remote only) | `version1` | 🟡 Can review |
| Lam Hung   | *(chua co branch rieng)* | — | 🔴 Chua bat dau |

---

## Huy (Leader) - Core & DevOps — branch `huy`/`main`
- [x] Chuan hoa cau truc package: `{screens,world,blocks,entities,physics,utils,navigation}`.
- [x] Tao `MainGame` — entry point, quan ly `SpriteBatch` + `AssetManager` shared.
- [x] Tao `ScreenRouter` + `BaseScreen` lifecycle (`onEnter`/`onExit`/`dispose`).
- [x] Tao `GameScreen`, `StateScreen` (MENU/PAUSE/GAME_OVER) voi phim test transition.
- [x] Tao `Entity` abstract class (position, velocity, bounds, state flags).
- [x] Tao `AbstractBlock` abstract class (solid, breakable, hardness, blockId, bounds).
- [x] Tao `SimpleBlock` extends `AbstractBlock`.
- [x] Tao `PhysicsEngine` co ban (gravity + velocity + ground detection don gian).
- [x] Tao `Constants` class tap trung cau hinh game.
- [x] Tao `BlockPalette` load texture cho 8 loai block (grass/stone/bedrock/sand/wood/leaves/planks/dirt).
- [x] Tich hop `BlockPalette.dispose()` vao `MainGame.dispose()` — hien dang leak texture.
- [ ] Setup template cho PR/Issue va quy trinh review (main chi merge qua PR).
- [ ] **MERGE PLAN**: Xac dinh thu tu merge cac branch de giam conflict.

## Kien - World & Camera — branch `kien`
- [x] Implement `World.generate(seed)` voi Fractal/Value Noise 1D + cosine interpolation.
  > Terrain co bedrock/stone/dirt/grass/sand, cay ngau nhien (trunk + leaves).
- [x] Implement `World.render(batch, camera)` voi frustum culling.
- [x] Camera follow player voi lerp smoothing + clamp world boundary.
  > Implement trong `GameScreen.update()`.
- [x] Them `getSpawnPoint()` — quet tu tren xuong tim block solid.
- [ ] Nang cap len Perlin/Simplex noise de terrain tu nhien hon.
- [ ] Them cave generation (2D noise voi threshold).
- [ ] Tach world theo chunk (`CHUNK_SIZE = 16`) de toi uu streaming.
- [ ] Seed lay tu save/game config thay vi hardcode `1337L`.

## Duoc - Entity & Input — branch `Duoc`
- [x] Tao `Player` class ke thua `Entity` voi 4 state: IDLE/RUN/JUMP/FALL.
- [x] Xu ly input (A/D/LEFT/RIGHT di chuyen, SPACE/UP nhay).
- [x] Player animation rig: body parts rieng (head/body/arms/legs/boots), xoay theo state.
  > Run cycle 4-frame stepped, jump pose, idle pose.
- [x] Xu ly asset Steve: body, head, arms, legs, boots — trai/phai.
- [x] Don dep `organized_assets_en` (xoa khoi branch de giam kich thuoc repo).
- [ ] ⚠️ **Van de**: Commit cuoi la "Sot jump do chua co ground" — jump chua hoat dong dung.
  > Can physics ground detection tu branch `kien` hoac `huy` de fix.
- [ ] Tao mob co AI don gian (patrol/chase) — bat dau voi Cow.
- [ ] Tao `MobManager` de quan ly nhieu entity.
- [ ] Them health/damage system cho Player.

## Viet Hung - Blocks & Assets — branch `Hung`
- [x] Tao block type classes cu the: `OreBlocks`, `NatureBlocks`, `StoneBlocks`, `WoodBlocks`, `UtilityBlocks`.
  > Mo rong tu `SimpleBlock`, phan loai block theo nhom.
- [x] Tao `TextureManager` class de quan ly texture tap trung.
- [x] Tao `DemoBlockViewer` de test render cac block.
- [x] Cap nhat `BlockPalette` (refactor, nhieu block hon).
- [ ] Chuyen sang `TextureAtlas` that su (thay vi load tung file).
- [ ] Thong nhat naming convention asset: chu thuong, snake_case, khong dau.
- [ ] Dam bao block API tuong thich voi `World.generate()` va `PhysicsEngine`.

## Lam Hung - Physics — *(chua co branch)*
- [~] Ground detection co ban da co trong `PhysicsEngine` (branch `huy`).
  > Chi resolve phia duoi (ground). Chua co collision ngang va tran.
- [ ] ⚠️ **Can tao branch** va bat dau code.
- [ ] Hoan thien collision AABB day du (kiem tra ca 4 phia: tren/duoi/trai/phai).
- [ ] Tach resolve collision theo 2 truc X/Y de tranh ket goc block.
- [ ] Reset velocity phu hop khi va cham (dung tuong -> `velocity.x = 0`).
- [ ] Viet test scenario thu cong (di ngang, roi tu cao, cham canh block).

---

## Leader (Huy) - Quan ly chung
- [x] Chot Definition of Done — da co `ASSET_MVP_PLAN.md`, `SCREEN_LIFECYCLE.md`.
- [ ] Theo doi blocker hang ngay, xu ly dependency bi tre > 24h.
- [ ] Duyet PR theo tieu chi: dung scope, build pass, khong pha module khac.
- [ ] Chot milestone "vertical slice": world + player + collision + 1 mob.

---

## Tong ket tien do theo branch

| Module | Branch | Da lam | Con lai | Trang thai |
|--------|--------|--------|---------|------------|
| Core & DevOps (Huy) | `huy`/`main` | 10 | 3 | 🟢 Base vung |
| World & Camera (Kien) | `kien` | 4 | 4 | 🟡 Nua duong |
| Entity & Input (Duoc) | `Duoc` | 5 | 4 | 🟡 Bi block boi physics |
| Blocks & Assets (V.Hung) | `Hung` | 4 | 3 | 🟡 Nua duong |
| Physics (L.Hung) | *(chua co)* | 1 | 5 | 🔴 Chua bat dau |
| Quan ly chung (Huy) | — | 1 | 3 | 🟠 Can lam |

---

## ⚠️ Blockers & Dependencies

1. **Duoc bi block boi Physics**: Player jump khong hoat dong vi thieu ground detection day du.
   → Lam Hung can uu tien collision, hoac Huy ho tro tam.
2. **Lam Hung chua co branch**: Can tao branch `lhung` va bat dau code physics ngay.
3. **Merge order de xuat**:
   - `kien` -> `main` (World + Camera, it conflict)
   - `Hung` -> `main` (Blocks, can resolve BlockPalette conflict voi kien)
   - `Duoc` -> `main` (Player, can resolve Player.java + assets conflict)
   - Lam Hung code physics tren `main` sau khi merge xong.

## 🎯 Uu tien tiep theo

1. 🔴 **Lam Hung**: Tao branch, implement AABB collision day du.
2. 🟠 **Huy**: Len ke hoach merge, bat dau voi branch `kien`.
3. 🟡 **Duoc**: Chuan bi mob (Cow) trong khi cho physics fix.
4. 🟡 **V.Hung**: Dam bao block types tuong thich API hien tai truoc khi merge.
