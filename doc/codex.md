# codex.md

## File Purpose
- Record a concise summary of completed project features.
- Provide a quick progress snapshot before starting new work.
- Canonical path for future sessions: `doc/codex.md`.
- Project root for all paths below: repository root.
- Future Codex sessions should read this file first to understand the current system state before planning or implementing new work.
- This file does not replace `README.md`, is not a detailed design document, and should not store unfinished plans.

## Working Rules For Codex

### Scope And Architecture
- Do not make large architecture changes unless explicitly requested.
- When adding a new feature, prefer creating a focused class/file in the appropriate module.
- Integration files such as `GameScreen` should only call short APIs such as `update`, `render`, and `dispose`; they should not absorb full feature logic.
- Keep changes small and within the requested scope.
- Prioritize gameplay blockers first, especially physics, input, and state bugs.

### Required Change Report
- Every change report must include the files changed.
- Every change report must explain which mechanism or behavior was affected.
- Every change report must include how to verify the change in game, including input steps or scene setup and the expected result.

### Code Comment Rules
- Comments should explain why a decision exists or what trade-off is being made.
- Do not write comments that merely restate obvious code behavior.
- In game loop or physics logic, use short comments to document assumptions.
- TODO comments must include:
  - The scope of work.
  - The completion condition.
  - The owner when known.
- Example:
  ```java
  // Why: resolve Y before X to avoid corner snagging while falling onto blocks.
  // TODO(lhung): add left/right AABB collision before merging the physics branch.
  ```

### PR And Review Comment Rules
- Use severity levels: `blocker`, `major`, `minor`, and `nit`.
- Each review comment should include:
  - The observed issue.
  - The gameplay or bug risk.
  - A concrete fix suggestion.
- Prioritize review focus on crashes, incorrect collision, incorrect state, resource leaks, and broken screen lifecycle.

### Screen And Resource Rules
- Do not call `setScreen()` directly from gameplay modules.
- Do not dispose the shared `SpriteBatch` or `AssetManager` inside a `Screen`.
- To change screens, call `game.getScreenRouter().request(...)`.

### Task Checklist
- Restate the task and the affected module, such as world, player, physics, screen, or assets.
- Read the related files under `core/src/main/java/com/main/game/...` before editing.
- Make a small, scoped change.
- Run `lwjgl3:run` or the relevant tests when applicable.
- Report the result and include manual in-game verification steps.

## Completed Features

### Screens And Navigation
- Separate screens exist for loading, menu, mode select, game, pause, game over, and settings.
- `ScreenRouter` handles safe screen transitions through request/flush.
- `MainGame` owns shared resources such as `SpriteBatch` and `AssetManager`.
- Related files:
  - `core/src/main/java/com/main/game/MainGame.java`
  - `core/src/main/java/com/main/game/GameState.java`
  - `core/src/main/java/com/main/game/navigation/ScreenRouter.java`
  - `core/src/main/java/com/main/game/navigation/ScreenId.java`
  - `core/src/main/java/com/main/game/screens/BaseScreen.java`
  - `core/src/main/java/com/main/game/screens/LoadingScreen.java`
  - `core/src/main/java/com/main/game/screens/MenuScreen.java`
  - `core/src/main/java/com/main/game/screens/ModeSelectScreen.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/screens/StateScreen.java`
  - `core/src/main/java/com/main/game/screens/SettingsScreen.java`

### Finite World And Camera
- The world is finite with `WORLD_WIDTH = 400` and `WORLD_HEIGHT = 128`.
- Chunks are used as internal storage/rendering structures, without assuming an infinite world.
- The camera follows the player and is clamped inside world bounds.
- Camera zoom is closer to the player at `0.5f`.
- Related files:
  - `core/src/main/java/com/main/game/utils/Constants.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/world/Chunk.java`
  - `core/src/main/java/com/main/game/ui/GameCameraController.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### World Generation
- World generation logic has been moved into the `worldgen` package.
- Basic biomes are implemented: forest, desert, and snow.
- Biomes support surface, filler, and deep layers.
- Basic trees, cactus, ice/snow patches, and simple house/village structures are implemented.
- Spawn safety helpers exist for players and mobs.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/WorldGenerator.java`
  - `core/src/main/java/com/main/game/worldgen/WorldNoise.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeType.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeProfile.java`
  - `core/src/main/java/com/main/game/worldgen/StructurePlacer.java`
  - `core/src/main/java/com/main/game/worldgen/SpawnSafety.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java`
  - `core/src/main/java/com/main/game/world/World.java`

### Cave System V1
- Cave V1 is implemented in the `worldgen/cave` package.
- The cave module includes `CaveGenerator`, `CaveCarver`, and `OreVeinPlacer`.
- Caves are generated only inside finite world bounds.
- Cave carving avoids the surface, spawn area, bedrock/deep boundary, and non-stone blocks.
- Ore veins are placed after carving and only replace remaining `stone` or `deepslate`.
- Generation remains deterministic from the world seed.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/cave/CaveGenerator.java`
  - `core/src/main/java/com/main/game/worldgen/cave/CaveCarver.java`
  - `core/src/main/java/com/main/game/worldgen/cave/OreVeinPlacer.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `doc/CAVE_IMPLEMENT_PLAN.md`

### Deepslate And Ores
- A `deepslate` layer is implemented for deeper underground areas.
- Bedrock remains at the bottom of the world instead of filling the whole deep area.
- Normal ores are implemented: coal, iron, gold, diamond, copper, lapis, redstone, and emerald.
- Deepslate ore variants are implemented for the cave/ore generator.
- Ore and deepslate texture lookup uses real assets when available and falls back safely when assets are missing.
- Related files:
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/worldgen/cave/OreVeinPlacer.java`
  - `core/src/main/java/com/main/game/world/BlockPalette.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/blocks/types/OreBlocks.java`
  - `core/src/main/java/com/main/game/blocks/types/StoneBlocks.java`
  - `assets/Ores/`

### Spawn Safety
- Fixed player spawning inside caves, dirt, stone, or bedrock.
- The first spawn uses a temporary initial spawn platform.
- The platform is removed afterward, and future respawns return to normal surface spawning.
- Player spawn space is cleared so the player does not get stuck in blocks after spawn/respawn.
- Related files:
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/world/SpawnSafetyController.java`
  - `core/src/main/java/com/main/game/worldgen/SpawnSafety.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/entities/player/Player.java`

### Player And Physics
- Player movement supports left/right movement, jump, fall, hurt, death, and respawn.
- Physics supports gravity, ground detection, and X/Y axis collision resolution.
- Player rendering is separated into its own renderer and includes basic animation.
- Related files:
  - `core/src/main/java/com/main/game/entities/player/Player.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `core/src/main/java/com/main/game/entities/Entity.java`
  - `core/src/main/java/com/main/game/entities/EntityState.java`
  - `core/src/main/java/com/main/game/physics/PhysicsEngine.java`

### Mobs And Combat
- Mobs are grouped as tamed, passive, and hostile.
- Multiple passive and hostile mob types are implemented with their own size, health, speed, and damage profiles.
- Mobs support patrol, chase, attack, panic, line-of-sight checks, and knockback when hit.
- The player can perform left-click melee attacks.
- Combat includes cooldown, reach checks, falling critical hits, and tool-based damage.
- Related files:
  - `core/src/main/java/com/main/game/combat/PlayerAttackController.java`
  - `core/src/main/java/com/main/game/entities/EntityManager.java`
  - `core/src/main/java/com/main/game/entities/mob/Mob.java`
  - `core/src/main/java/com/main/game/entities/mob/MobAllegiance.java`
  - `core/src/main/java/com/main/game/entities/mob/MobBrain.java`
  - `core/src/main/java/com/main/game/entities/mob/MobProfile.java`
  - `core/src/main/java/com/main/game/entities/mob/MobRenderer.java`
  - `core/src/main/java/com/main/game/entities/mob/MobAssetPack.java`
  - `core/src/main/java/com/main/game/entities/mob/MobMovementHelper.java`
  - `core/src/main/java/com/main/game/entities/mob/MobSightHelper.java`
  - `core/src/main/java/com/main/game/worldgen/BiomeMobSpawner.java`

### Block Breaking And Dropped Items
- The player can mine blocks with left-click.
- Block hover cursor and crack overlay are implemented while mining.
- Fully covered blocks cannot be mined directly.
- Bedrock is unbreakable.
- Broken blocks can spawn dropped items.
- Dropped items have physics, pickup delay, player suction, and inventory pickup.
- Related files:
  - `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakRules.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakListener.java`
  - `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - `core/src/main/java/com/main/game/items/DroppedItem.java`
  - `core/src/main/java/com/main/game/items/DroppedItemManager.java`
  - `core/src/main/java/com/main/game/items/HarvestEntry.java`

### Inventory And Hotbar
- A 9-slot hotbar and main inventory are implemented.
- Inventory opens with `E`.
- Left/right click supports pick, place, swap, stack, and split-stack behavior.
- Item textures and stack counts render in the hotbar/inventory.
- Tool stack size is `1`; block stack size defaults to `64`.
- Starter inventory has been removed; the player no longer receives test items at spawn.
- Related files:
  - `core/src/main/java/com/main/game/inventory/Inventory.java`
  - `core/src/main/java/com/main/game/inventory/InventoryController.java`
  - `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`
  - `core/src/main/java/com/main/game/inventory/InventoryLayout.java`
  - `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `assets/images/gui_invrow/inventory.png`

### Crafting System V1
- Player 2x2 crafting is available from the inventory.
- Crafting table 3x3 crafting opens when pressing `E` while the cursor is over a reachable `crafting_table`.
- Players can move inventory items into crafting slots and take the output result.
- Crafting inputs are returned to inventory when closing crafting when inventory space allows it.
- Implemented recipes include planks, sticks, crafting table, furnace, chest, wood tools, stone tools, copper tools, iron tools, gold tools, and diamond tools.
- Stone now drops `cobblestone`, and stone tool recipes use `cobblestone`.
- Related files:
  - `core/src/main/java/com/main/game/crafting/CraftingController.java`
  - `core/src/main/java/com/main/game/crafting/CraftingGrid.java`
  - `core/src/main/java/com/main/game/crafting/CraftingMode.java`
  - `core/src/main/java/com/main/game/crafting/CraftingRecipe.java`
  - `core/src/main/java/com/main/game/crafting/CraftingMatch.java`
  - `core/src/main/java/com/main/game/crafting/RecipeRegistry.java`
  - `core/src/main/java/com/main/game/utilityblock/craftingtable/CraftingTableInteractionController.java`
  - `core/src/main/java/com/main/game/inventory/InventoryLayout.java`
  - `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`

### Shared Slot UI
- Slot item rendering is centralized in `ItemSlotRenderer`.
- Inventory, crafting, furnace, and chest all use the same item icon, stack count, durability bar, and carried-stack rendering path.
- Left/right slot click behavior is centralized in `ItemSlotInteractionController`.
- `ItemSlotAccess` lets each GUI keep its own slot mapping and special take slots, such as crafting results and furnace output.
- Related files:
  - `core/src/main/java/com/main/game/inventory/ItemSlotRenderer.java`
  - `core/src/main/java/com/main/game/inventory/ItemSlotInteractionController.java`
  - `core/src/main/java/com/main/game/inventory/ItemSlotAccess.java`
  - `core/src/main/java/com/main/game/inventory/InventoryInteractionHandler.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceInteractionHandler.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestInteractionHandler.java`

### Tool System V1
- `ToolRegistry` is implemented as the central tool metadata API.
- Tool types are registered: pickaxe, axe, shovel, sword, and hoe.
- Tool materials are registered: wood, stone, copper, iron, gold, diamond, and netherite.
- Tool textures load through metadata before falling back to block textures.
- Axes support two texture variants:
  - `*_axe_v1` for hotbar/inventory rendering.
  - `*_axe_v2` for held-in-hand rendering.
- Related files:
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `assets/tools/wood/`
  - `assets/tools/stone/`
  - `assets/tools/copper/`
  - `assets/tools/iron/`
  - `assets/tools/gold/`
  - `assets/tools/diamond/`
  - `assets/tools/netherite/`

### Tool Usage And Durability
- Pickaxes mine stone, deepslate, sandstone, ores, and deepslate ores faster.
- Axes chop wood, planks, and leaves faster.
- Shovels dig dirt, grass, sand, and snow faster.
- Swords are for combat and do not speed up mining.
- Hoes are registered but do not have farming behavior yet.
- Tools have durability and show a durability bar under their icon.
- Tools lose durability when used and disappear when durability reaches zero.
- Related files:
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ItemStack.java`
  - `core/src/main/java/com/main/game/inventory/InventoryRenderer.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreaker.java`
  - `core/src/main/java/com/main/game/combat/PlayerAttackController.java`

### Held Item Rendering
- The player can hold tools and blocks in hand.
- Held items follow the player's facing direction.
- Held items are aligned so tools are gripped by the handle instead of the sprite center.
- Held items work correctly with jump/fall animation.
- Mining/placement swing is reused to make item usage visible.
- Related files:
  - `core/src/main/java/com/main/game/entities/player/Player.java`
  - `core/src/main/java/com/main/game/entities/player/PlayerRenderer.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/ToolRegistry.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### Utility Blocks
- Utility block interaction is grouped under the `utilityblock` package.
- `UtilityBlockInteractionController` provides shared reachable-hover checks for utility blocks.
- Crafting table, furnace, and chest each keep their own focused subpackage.
- `GameScreen` wires utility blocks through short calls for open, update, render, drop, clear, and dispose behavior.
- Related files:
  - `core/src/main/java/com/main/game/utilityblock/UtilityBlockInteractionController.java`
  - `core/src/main/java/com/main/game/utilityblock/craftingtable/CraftingTableInteractionController.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceInteractionController.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestInteractionController.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/ui/GameHudRenderer.java`

### Ore Drops And Furnace V1
- Ore drops now distinguish direct resource ores from raw metal ores.
- Coal, diamond, lapis, redstone, and emerald ores drop their resource item directly.
- Iron, gold, and copper ores drop `raw_iron`, `raw_gold`, and `raw_copper`.
- Deepslate ore variants drop the equivalent items as their normal ore variants.
- Furnace is a placeable utility block crafted from 8 `cobblestone`.
- Pressing `E` while the cursor is over a reachable furnace opens the furnace GUI.
- Furnace state is stored per tile with one input slot, one fuel slot, one output slot, burn timer, and cook progress.
- Fuel supports `coal`, `wood`, `planks`, and `stick`.
- Smelting supports `raw_iron -> iron_ingot`, `raw_gold -> gold_ingot`, and `raw_copper -> copper_ingot`.
- Furnace GUI uses provided progress arrow and flame sprites; flame animation was corrected to burn down from the full flame frames.
- Breaking a furnace drops its stored input, fuel, output, and the furnace block item.
- Related files:
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceState.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceManager.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceRenderer.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceLayout.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceInteractionHandler.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FurnaceInteractionController.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/FuelRegistry.java`
  - `core/src/main/java/com/main/game/utilityblock/furnace/SmeltingRecipeRegistry.java`
  - `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - `assets/util_block/furnace_off.png`
  - `assets/util_block/furnace_lit.png`
  - `assets/util_block/gui/furnace.png`
  - `assets/util_block/process/`

### Chest V1
- Chest is a single-block utility block with 27 storage slots.
- Chest uses `chest_closed.png` in-world with no open animation or lid state.
- Chest is crafted from 8 `planks` in a 3x3 ring recipe.
- Pressing `E` while the cursor is over a reachable chest opens the chest GUI.
- Chest contents persist per placed tile while the game screen is alive.
- Adjacent chests stay separate; no double chest behavior exists.
- Breaking a chest drops stored contents and then the chest block item.
- Related files:
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestState.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestManager.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestRenderer.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestLayout.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestInteractionHandler.java`
  - `core/src/main/java/com/main/game/utilityblock/chest/ChestInteractionController.java`
  - `assets/util_block/chest_closed.png`
  - `assets/util_block/gui/chest.png`

### Block Metadata Registry
- Block metadata is centralized in `BlockRegistry` and `BlockDefinition`.
- Metadata covers block hardness, texture name, palette fallback, solidity, breakability, placeability, drop item, harvest level, and ore flags.
- `WorldBlockFactory`, `BlockHarvestRules`, `BlockDropFactory`, and `ItemRegistry` read block behavior from the registry instead of maintaining separate if-chains.
- Registered drops include `stone -> cobblestone`, direct ore resources, and raw metal ore drops.
- Related files:
  - `core/src/main/java/com/main/game/blocks/metadata/BlockDefinition.java`
  - `core/src/main/java/com/main/game/blocks/metadata/BlockRegistry.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/interaction/BlockHarvestRules.java`
  - `core/src/main/java/com/main/game/items/BlockDropFactory.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`

### Block Placement V1
- `BlockPlacementController` is implemented.
- Placement uses the cursor tile as the source of truth.
- Placement overlay appears only when the cursor is over a valid empty tile.
- Right-click places the selected block at the overlay tile.
- Only normal 1x1 placeable blocks are supported.
- Tools, bedrock, out-of-reach blocks, player-overlapping blocks, occupied tiles, and floating unsupported blocks cannot be placed.
- Successful placement creates the block through `WorldBlockFactory`, writes it into the world, decrements the stack, and clears the slot when the stack reaches zero.
- Related files:
  - `core/src/main/java/com/main/game/interaction/BlockPlacementController.java`
  - `core/src/main/java/com/main/game/interaction/BlockBreakOverlay.java`
  - `core/src/main/java/com/main/game/worldgen/WorldBlockFactory.java`
  - `core/src/main/java/com/main/game/world/World.java`
  - `core/src/main/java/com/main/game/inventory/ItemRegistry.java`
  - `core/src/main/java/com/main/game/inventory/Inventory.java`
  - `core/src/main/java/com/main/game/screens/GameScreen.java`

### GameScreen Refactor
- Some responsibilities have been moved out of `GameScreen`.
- `GameHudRenderer` handles HUD rendering.
- `GameCameraController` handles camera follow/clamping.
- `SpawnSafetyController` handles initial spawn and respawn safety.
- Related files:
  - `core/src/main/java/com/main/game/screens/GameScreen.java`
  - `core/src/main/java/com/main/game/ui/GameHudRenderer.java`
  - `core/src/main/java/com/main/game/ui/GameCameraController.java`
  - `core/src/main/java/com/main/game/ui/GameOverlayRenderer.java`
  - `core/src/main/java/com/main/game/world/SpawnSafetyController.java`

### Git And Docs Cleanup
- A temporary test branch was created to validate the merge before updating `main`.
- Validated work was merged into `main`.
- `main` pull/conflict issues were resolved as requested.
- Unneeded local-only commits were removed as requested.
- A `doc/` folder was created and documentation/spec files were moved there, except `README.md`.
- Related files:
  - `README.md`
  - `doc/codex.md`
  - `doc/CAVE_IMPLEMENT_PLAN.md`
  - `doc/World_Generation_&_Cave_System.md`
  - `doc/TODO_TEAM.md`
  - `doc/PLAN_THIS_WEEK.md`
  - `doc/OUTLINE_CHUC_NANG.md`
  - `doc/newgame_logic.md`

## Latest Verification
- `.\gradlew.bat classes` passed.
- `.\gradlew.bat test` passed.
- `git diff --check` passed.
- Manual gameplay verification was reported working for crafting, furnace, chest, block metadata behavior, and shared slot interaction/rendering.
