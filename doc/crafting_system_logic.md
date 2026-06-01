# Hệ thống Crafting — Paper Minecraft

## Tổng quan

```
Hệ thống crafting gồm 4 phần chính:
  1. Crafting Table / 2x2 grid  → Check Recipe + Output Craft
  2. Furnace / Smoker / Blast    → Process Furnace Burn
  3. Grindstone                  → Grindstone Repair
  4. Recipe Book (ECG)           → Crafting Guide dạng GUI

Logic xử lý: gui_invrow sprite (dùng clone theo costume)
Data recipe:  _Recipes list
Data furnace: _INSIDE list + _RefData
```

---

## 1. Cấu trúc dữ liệu Recipe — `_Recipes`

Mỗi recipe chiếm **6 slots** trong list `_Recipes`:

```
_Recipes[i+0] = ""              separator
_Recipes[i+1] = name            tên recipe (vd: "Crafting Table")
_Recipes[i+2] = blockID         output item ID
_Recipes[i+3] = count           số lượng output
_Recipes[i+4] = recipe_id       chuỗi nguyên liệu (9 ký tự, mỗi ký tự = item ID)
_Recipes[i+5] = recipe_type     loại recipe (0=shapeless, 1=shaped, ...)
```

### Thêm recipe — `Add Recipe`

```python
def Add_Recipe(name, blockID, count, recipe_id, recipe_type):
    _Recipes.add("")           # separator
    _Recipes.add(name)
    _Recipes.add(blockID)
    _Recipes.add(count)
    _Recipes.add(recipe_id)
    _Recipes.add(recipe_type)
```

---

## 2. Slots Crafting Grid — `_INV`

Crafting slots dùng `_INV` với index cố định:

| Slot | `_INV` index ID | Item ID | Count |
|------|-----------------|---------|-------|
| Craft 1 (top-left)     | 37 | _INV[73] | _INV[74] |
| Craft 2 (top-mid)      | 38 | _INV[75] | _INV[76] |
| Craft 3 (top-right)    | 39 | _INV[77] | _INV[78] |
| Craft 4 (mid-left)     | 40 | _INV[79] | _INV[80] |
| Craft 5 (center)       | 41 | _INV[81] | _INV[82] |
| Craft 6 (mid-right)    | 42 | _INV[83] | _INV[84] |
| Craft 7 (bot-left)     | 43 | _INV[85] | _INV[86] |
| Craft 8 (bot-mid)      | 44 | _INV[87] | _INV[88] |
| Craft 9 (bot-right)    | 45 | _INV[89] | _INV[90] |
| **Output**             | **46** | **_INV[91]** | **_INV[92]** |
---

## 3. `Check Recipe(dir)` — So khớp pattern

Chạy mỗi khi grid thay đổi.

### Bước 1: Tìm bounding box của items trong grid

```python
x = 2   # cột trái nhất có item (0-based)
y = 9   # hàng trên nhất có item
CraftMul = 99999  # số lượng nhỏ nhất của bất kỳ nguyên liệu nào

for i in range(9):
    if _INV[i*2 + 81] != "#":         # slot có item
        if (i mod 3) < x: x = i mod 3
        if i < y:          y = i
        if _INV[i*2+82] < CraftMul: CraftMul = _INV[i*2+82]

y = floor(y / 3)   # convert sang row index
```

### Bước 2: Đọc pattern từ grid (normalize về góc trên-trái)

```python
# Đọc từ vị trí (x, y) theo hướng dir (+1 = normal, -1 = mirrored)
match = ""
i = x + y * 3

for row in range(3 - y):
    if dir > 0:                   # normal
        for col in range(3 - x):
            match += _INV[i*2 + 81]
            i += 1
    else:                         # mirrored (shapeless check)
        i += (3 - x)
        for col in range(3 - x):
            i -= 1
            match += _INV[i*2 + 81]
        i += (3 - x)
    
    # Padding hàng với "#" cho đủ 3 cột
    for _ in range(x):
        match += "#"

# Padding các hàng trống ở trên
for _ in range(y):
    match += "###"
```

### Bước 3: So sánh với `_Recipes`

```python
x2 = 10   # index tìm thấy (10 = chưa tìm thấy)

for i in range(0, len(_Recipes), 6):
    if _Recipes[i+4] == match:    # match pattern
        x2 = i + 2                # lưu vị trí output
        break

if x2 < 99999:
    if dir > 0:
        call Check_Recipe(-1)     # thử mirror nếu không thấy
    else:
        call Output_Craft()       # đặt output vào slot 46
```

---

## 4. `Output Craft` — Đặt kết quả vào slot output

```python
def Output_Craft():
    ix   = _Recipes[x2 - 2]      # output item ID
    qty  = _Recipes[x2 - 1]      # số lượng output
    
    call Get_Durability(ix)       # kiểm tra có phải tool không
    
    if dur_iv == 0:               # item thường (không có durability)
        call Get_Stack_Limit(ix)
        if maxStack == 1:
            y2 = qty
        else:
            y2 = qty * CraftMul   # nhân với số lượng nguyên liệu ít nhất

    else:                         # tool (có durability)
        y2 = dur_iv               # output durability = max durability

    # Xử lý đặc biệt theo _UnderCursor (loại bàn craft)
    if _UnderCursor == 317:       # Crafting Table
        _INV[slot*2-1] = ix
        _INV[slot*2]   = y2
    elif _UnderCursor == 458:     # Anvil
        ...
    elif _UnderCursor != 575:     # Không phải Stonecutter
        call Set_Inventory_Item(46, ix, y2)
    
    # Recipe có dùng bow → output count = 1
    if match contains 310:        # 310 = bow
        call Set_Inventory_Item(46, ix, 1)
    
    CraftMul = qty
    x2 = 99999   # reset
```

---

## 5. `remove Craft(count)` — Tiêu thụ nguyên liệu khi craft

Chạy khi player click lấy item output:

```python
def remove_Craft(count):
    for slot in range(37, 46):    # duyệt 9 slot craft
        invID = _InvPos[slot*3 - 2]
        qty   = _INV[invID * 2]
        item  = _INV[invID*2 - 1]

        # Các block đặc biệt không bị xóa
        if _UnderCursor in [317, 575, 1184]:   # Crafting Table / Stonecutter / Loom
            clear slot

        # Craft bằng Smithing Table (costume 11): tool bị xóa
        elif costume == 11 AND tool_flag == 1:
            clear slot

        # Craft bằng Anvil (458)
        elif _UnderCursor == 458:
            if item not in [520, 590, ...]:    # item không dùng được → clear
                clear slot
            elif qty - 1 == 0: clear slot
            else: qty -= 1

        # Bucket → trả về empty bucket sau khi dùng
        elif item == 168:
            _INV[slot] = 92   # empty bucket

        # Sword + ingredient → trả về ingredient đặc biệt
        elif item in [702, 1167, 150, 154, 158, 130, 709, 1327]:
            clear slot

        # Glass bottle → trả về bottle
        elif item == 559:
            _INV[slot] = 422  # glass bottle

        # Craft thường
        else:
            if costume == 11:                  # Smithing Table
                remaining = qty - (count/CraftMul * bmatch)
            else:
                remaining = qty - (count/CraftMul)
            
            if remaining <= 0: clear slot
            else: qty = remaining

    # Advancement triggers
    if costume == 13: check_advancement(86)    # Loom
    if costume == 11: check_advancement(74,17) # Smithing Table
    if costume == 8:  check_advancement(9)     # Crafting Table

    # XP cost (Anvil)
    if _Mode == "av": XP_LEVEL -= 1
    if costume == 14 AND XP_LEVEL > 1: XP_LEVEL -= 1
```

---

## 6. `Process Furnace Burn` — Lò nung

Xử lý 3 loại lò: **Furnace (48/49)**, **Smoker (303/304)**, **Blast Furnace (455/456)**

### Cấu trúc `_INSIDE` cho lò nung

```
_INSIDE[ref + 0] = tileIdx
_INSIDE[ref + 1] = loại lò
_INSIDE[ref + 2] = slot nhiên liệu count
_INSIDE[ref + 3] = slot nhiên liệu ID       ← đang đốt
_INSIDE[ref + 4] = slot nhiên liệu count
_INSIDE[ref + 5] = slot nguyên liệu ID      ← đang nung
_INSIDE[ref + 6] = slot nguyên liệu count
_INSIDE[ref + 7] = slot output ID
_INSIDE[ref + 8] = slot output count
```

### Logic đốt (phase 1 — kiểm tra nhiên liệu)

```python
if tile == furnace_off OR timer > burn_end_time:
    fuel_id = _INSIDE[ref + 3]
    smelt_time = _BLOCK_DATA[fuel_id * DMUL + 13]   # thời gian cháy của nhiên liệu
    
    if fuel_id == "#" OR smelt_time == 0:
        if tile == furnace_on:
            # Tắt lò
            _LEVEL[tileIdx] = furnace_off
            call Deactivate_Tile(refIdx)
            call Add_To_Light_Mod(tileIdx)
    else:
        # Bật lò
        _LEVEL[tileIdx] = furnace_on
        _RefData[ref + 5] = smelt_time
        _RefData[ref + 3] = timer + smelt_time   # burn end
        
        # Tiêu thụ nhiên liệu
        if fuel_id == 82:                        # lava bucket → empty bucket
            Set_Inside(ref, 1, 92, 1)
        elif tool_flag == 1:                     # tool → xóa
            Set_Inside(ref, 1, "", 0)
        else:
            Set_Inside(ref, 1, "", fuel_count - 1)
```

### Logic nung (phase 2 — output)

```python
if tile == furnace_on:
    input_id   = _INSIDE[ref + 5]
    output_id  = _BLOCK_DATA[input_id * DMUL + 12]  # kết quả nung
    
    if input_id == "#" OR output_id == 0:
        refuel_timer = timer + 10                    # không có gì để nung
    else:
        out_slot_id    = _INSIDE[ref + 7]
        out_slot_count = _INSIDE[ref + 8]
        
        if (out_slot_id == "#" OR out_slot_id == output_id) AND out_slot_count < 64:
            if timer > cook_end_time:
                # Output xong 1 item
                Set_Inside(ref, 3, output_id, out_slot_count + 1)
                Set_Inside(ref, 2, "", input_count - 1)
                cook_end_time = timer + 10           # 10 giây/item (furnace)
                                                     # 5 giây/item (smoker/blast)
```

**Thời gian nung:**
- Furnace: **10 giây/item**
- Smoker: **5 giây/item** (chỉ food)
- Blast Furnace: **5 giây/item** (chỉ ore/metal, kiểm tra `_Smeltable` list)

---

## 7. `Grindstone Repair` — Sửa chữa tool

```python
def Grindstone_Repair():
    # Không sửa được: bow, elytra, trident, fishing rod, shield
    if item in [310, 608, 110]: stop

    call Get_Durability(slot_95)
    
    # Lấy tên loại tool (vd: "sword", "pickaxe")
    tool_name = ""
    repeat:
        tool_name += char(BLOCK_DATA[item * DMUL + 2])
        until name extracted

    if _INV[89] == "#" OR _INV[95] == "#":
        # Chỉ 1 item → trả về item đã xóa enchant
        output = round(BLOCK_DATA item / DMUL)
        Set_Inventory_Item(46, output, _INV[96])
    
    else:
        # 2 item cùng loại → cộng durability
        if BLOCK_DATA[_INV[89] * DMUL + 2] contains tool_name:
            call Get_Durability(_INV[95])
            current = _INV[96]
            other   = _INV[90]
            
            if current + other > dur_max:
                Set_Inventory_Item(46, item, dur_max)
            else:
                Set_Inventory_Item(46, item, current + other)
```

---

## 8. Các loại bàn craft (costume GUI)

| Costume # | Loại bàn | Đặc điểm |
|-----------|----------|-----------|
| 2  | Crafting (2x2)  | Pocket crafting, không cần bàn |
| 8  | Crafting Table  | 3x3 grid đầy đủ |
| 10 | Stonecutter     | Chọn 1 recipe từ list |
| 11 | Smithing Table  | Trim/upgrade armor, tool hao durability |
| 12 | Loom            | Pattern banner |
| 13 | Cartography     | Sao chép bản đồ |
| 14 | Anvil           | Rename, combine, tốn XP |
| 20 | Enchanting Table| Bảng phù phép |

---

## Tóm tắt nhanh

```
Crafting:
  grid 3x3 → normalize bounding box → match với _Recipes[i+4]
  match → Output_Craft() → _INV[91/92]
  click output → remove_Craft() → trừ nguyên liệu × CraftMul

Recipe format:
  _Recipes: [sep, name, outputID, count, pattern9chars, type]

Furnace:
  phase1: kiểm tra nhiên liệu → bật/tắt lò → tiêu thụ fuel
  phase2: kiểm tra input → 10s/5s → ghi output vào slot

Grindstone:
  1 item → xóa enchant, trả lại item
  2 item cùng loại → cộng durability (capped tại max)
  tốn XP khi dùng Anvil: XP_LEVEL -= 1 mỗi lần craft
```
