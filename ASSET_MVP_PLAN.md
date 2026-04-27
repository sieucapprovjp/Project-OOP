# Asset MVP Plan (Batch 1)

Nguon asset goc (chinh thuc):
`D:\Project OOP\organized_assets_en`

Luu y quan trong:
- Tu gio tro di chi su dung asset trong `organized_assets_en`.
- Khong su dung lai bat ky folder asset cu nao ngoai workspace.

Muc tieu batch 1: du tai nguyen de chay vertical slice (world + player + 1 mob + UI toi thieu).

## 1) Blocks MVP (VHUNG-BLOCKS)

Copy cac file nay vao `assets/mvp/tiles/` va doi ten theo cot `target`.

| source | target | note |
|---|---|---|
| `Tiles/grass.png` | `grass.png` | block surface |
| `Tiles/Cobblestone.png` | `stone.png` | tam dung cho stone |
| `Tiles/bedrock.png` | `bedrock.png` | bottom layer |
| `Tiles/sand.png` | `sand.png` | biome desert sau nay |
| `Tiles/wood.png` | `wood.png` | tree trunk |
| `Tiles/leaves.png` | `leaves.png` | tree leaves |
| `Tiles/woodenplanks.png` | `planks.png` | block xay dung |

Ghi chu:
- Hien tai chua thay file dirt ro rang trong bo asset nay. Batch 1 cho phep tam dung `stone.png` cho lop duoi grass.
- Nuoc (`water`) chua co tile ro rang; se bo sung batch 2.

## 2) Player MVP (DUOC-ENTITY)

Copy vao `assets/mvp/player/` va giu ten de de doi chieu:

- `Steve/Body2.png`
- `Steve/Body3.png`
- `Steve/Body4.png`
- `Steve/0_.png`
- `Steve/1_.png`

Mapping tam thoi:
- `idle`: `Body2.png`
- `walk`: `Body3.png`, `Body4.png`
- `jump/fall`: `0_.png` hoac `1_.png` (team chot sau khi test)

## 3) Mob MVP (DUOC-ENTITY)

Chon 1 mob de demo (de nghi: cow). Copy vao `assets/mvp/mob/cow/`:

- `Mob/cow1.png`
- `Mob/cow3.png`
- `Mob/cow4.png`
- `Mob/cow5.png`
- `Mob/cow6.png`
- `Mob/cow7.png`
- `Mob/cowLook.png`
- `Mob/cowHurt.png`

## 4) UI MVP (HUY-LEAD + DUOC-ENTITY)

Copy vao:
- `assets/mvp/ui/health/` tu `Health/health0.png` -> `Health/health20.png` (neu co du)
- `assets/mvp/ui/hunger/` tu folder `Hunger/` (2-3 frame toi thieu)

## 5) Quy uoc naming (bat buoc)

- Dung chu thuong, `snake_case`, khong dau, khong space.
- Vi du: `Cobblestone.png` -> `stone.png`, `cowLook.png` -> `cow_look.png`.

## 6) Definition of Done (batch 1)

- World render duoc it nhat 4 block: grass/stone/bedrock/wood.
- Player render duoc idle + walk animation co ban.
- Co 1 mob render va update duoc (cow).
- Khong con path co space trong folder `assets/mvp/`.
