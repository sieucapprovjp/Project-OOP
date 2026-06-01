package com.main.game.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ToolRegistry {

    public enum ToolType {
        PICKAXE,
        AXE,
        SHOVEL,
        SWORD,
        HOE
    }

    public enum ToolMaterial {
        WOOD,
        STONE,
        COPPER,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE
    }

    public static final class ToolDefinition {
        private final String itemId;
        private final ToolType type;
        private final ToolMaterial material;
        private final String textureName;
        private final String heldTextureName;
        private final float miningMultiplier;
        private final int attackDamage;
        private final int maxDurability;

        private ToolDefinition(String itemId, ToolType type, ToolMaterial material,
                               String textureName, String heldTextureName, float miningMultiplier,
                               int attackDamage, int maxDurability) {
            this.itemId = itemId;
            this.type = type;
            this.material = material;
            this.textureName = textureName;
            this.heldTextureName = heldTextureName;
            this.miningMultiplier = miningMultiplier;
            this.attackDamage = attackDamage;
            this.maxDurability = maxDurability;
        }

        public String getItemId() {
            return itemId;
        }

        public ToolType getType() {
            return type;
        }

        public ToolMaterial getMaterial() {
            return material;
        }

        public String getTextureName() {
            return textureName;
        }

        public String getHeldTextureName() {
            return heldTextureName;
        }

        public float getMiningMultiplier() {
            return miningMultiplier;
        }

        public int getAttackDamage() {
            return attackDamage;
        }

        public int getMaxDurability() {
            return maxDurability;
        }
    }

    private static final Map<String, ToolDefinition> TOOLS = new HashMap<>();
    private static final Set<String> PICKAXE_BLOCKS = Set.of(
        "stone", "deepslate", "sandstone",
        "coal_ore", "iron_ore", "gold_ore", "diamond_ore", "copper_ore",
        "lapis_ore", "redstone_ore", "emerald_ore", "quartz_ore", "nether_quartz",
        "deepslate_co", "deepslate_io", "deepslate_go", "deepslate_do",
        "deepslate_copper", "ore_lapis_deepslate", "deepslate_ro", "deepslate_eo"
    );
    private static final Set<String> AXE_BLOCKS = Set.of(
        "wood", "planks", "leaves",
        "natural_wood",
        "desert_oak_leaves", "desert_oak_leaves_2",
        "spruce_log", "natural_spruce_log", "spruce_planks", "spruce_leaves",
        "cherry_log", "natural_cherry_log", "cherry_planks",
        "cherry_leaves", "cherry_leaves_2", "cherry_leaves_5", "cherry_leaves_6"
    );
    private static final Set<String> SHOVEL_BLOCKS = Set.of("dirt", "grass", "sand", "snow");

    static {
        register("wood_pickaxe", ToolType.PICKAXE, ToolMaterial.WOOD, "tools/wood/wood_pickaxe", 1.5f, 3, 30);
        register("wood_axe", ToolType.AXE, ToolMaterial.WOOD, "tools/wood/wood_axe_v1", "tools/wood/wood_axe_v2", 1.5f, 4, 30);
        register("wood_shovel", ToolType.SHOVEL, ToolMaterial.WOOD, "tools/wood/wood_shovel", 1.5f, 2, 30);
        register("wood_sword", ToolType.SWORD, ToolMaterial.WOOD, "tools/wood/wood_sword", 1.0f, 4, 30);
        register("wood_hoe", ToolType.HOE, ToolMaterial.WOOD, "tools/wood/wood_hoe", 1.0f, 2, 30);

        register("stone_pickaxe", ToolType.PICKAXE, ToolMaterial.STONE, "tools/stone/stone_pickaxe", 2.0f, 4, 45);
        register("stone_axe", ToolType.AXE, ToolMaterial.STONE, "tools/stone/stone_axe_v1", "tools/stone/stone_axe_v2", 2.0f, 5, 45);
        register("stone_shovel", ToolType.SHOVEL, ToolMaterial.STONE, "tools/stone/stone_shovel", 2.0f, 3, 45);
        register("stone_sword", ToolType.SWORD, ToolMaterial.STONE, "tools/stone/stone_sword", 1.0f, 6, 45);
        register("stone_hoe", ToolType.HOE, ToolMaterial.STONE, "tools/stone/stone_hoe", 1.0f, 3, 45);

        register("copper_pickaxe", ToolType.PICKAXE, ToolMaterial.COPPER, "tools/copper/copper_pickaxe", 2.5f, 4, 60);
        register("copper_axe", ToolType.AXE, ToolMaterial.COPPER, "tools/copper/copper_axe_v1", "tools/copper/copper_axe_v2", 2.5f, 5, 60);
        register("copper_shovel", ToolType.SHOVEL, ToolMaterial.COPPER, "tools/copper/copper_shovel", 2.5f, 3, 60);
        register("copper_sword", ToolType.SWORD, ToolMaterial.COPPER, "tools/copper/copper_sword", 1.0f, 6, 60);
        register("copper_hoe", ToolType.HOE, ToolMaterial.COPPER, "tools/copper/copper_hoe", 1.0f, 3, 60);

        register("iron_pickaxe", ToolType.PICKAXE, ToolMaterial.IRON, "tools/iron/iron_pickaxe", 3.0f, 5, 75);
        register("iron_axe", ToolType.AXE, ToolMaterial.IRON, "tools/iron/iron_axe_v1", "tools/iron/iron_axe_v2", 3.0f, 6, 75);
        register("iron_shovel", ToolType.SHOVEL, ToolMaterial.IRON, "tools/iron/iron_shovel", 3.0f, 4, 75);
        register("iron_sword", ToolType.SWORD, ToolMaterial.IRON, "tools/iron/iron_sword", 1.0f, 7, 75);
        register("iron_hoe", ToolType.HOE, ToolMaterial.IRON, "tools/iron/iron_hoe", 1.0f, 4, 75);

        register("gold_pickaxe", ToolType.PICKAXE, ToolMaterial.GOLD, "tools/gold/gold_pickaxe", 3.5f, 4, 24);
        register("gold_axe", ToolType.AXE, ToolMaterial.GOLD, "tools/gold/gold_axe_v1", "tools/gold/gold_axe_v2", 3.5f, 5, 24);
        register("gold_shovel", ToolType.SHOVEL, ToolMaterial.GOLD, "tools/gold/gold_shovel", 3.5f, 3, 24);
        register("gold_sword", ToolType.SWORD, ToolMaterial.GOLD, "tools/gold/gold_sword", 1.0f, 5, 24);
        register("gold_hoe", ToolType.HOE, ToolMaterial.GOLD, "tools/gold/gold__hoe", 1.0f, 3, 24);

        register("diamond_pickaxe", ToolType.PICKAXE, ToolMaterial.DIAMOND, "tools/diamond/diamond_pickaxe", 4.0f, 6, 110);
        register("diamond_axe", ToolType.AXE, ToolMaterial.DIAMOND, "tools/diamond/diamond_axe_v1", "tools/diamond/diamond_axe_v2", 4.0f, 7, 110);
        register("diamond_shovel", ToolType.SHOVEL, ToolMaterial.DIAMOND, "tools/diamond/diamond_shovel", 4.0f, 5, 110);
        register("diamond_sword", ToolType.SWORD, ToolMaterial.DIAMOND, "tools/diamond/diamond_sword", 1.0f, 8, 110);
        register("diamond_hoe", ToolType.HOE, ToolMaterial.DIAMOND, "tools/diamond/diamond_hoe", 1.0f, 5, 110);

        register("netherite_pickaxe", ToolType.PICKAXE, ToolMaterial.NETHERITE, "tools/netherite/netherite_pickaxe", 4.5f, 6, 140);
        register("netherite_axe", ToolType.AXE, ToolMaterial.NETHERITE, "tools/netherite/netherite_axe_v1", "tools/netherite/netherite_axe_v2", 4.5f, 8, 140);
        register("netherite_shovel", ToolType.SHOVEL, ToolMaterial.NETHERITE, "tools/netherite/netherite_shovel", 4.5f, 6, 140);
        register("netherite_sword", ToolType.SWORD, ToolMaterial.NETHERITE, "tools/netherite/netherite_sword", 1.0f, 9, 140);
        register("netherite_hoe", ToolType.HOE, ToolMaterial.NETHERITE, "tools/netherite/netherite_hoe", 1.0f, 6, 140);
    }

    private ToolRegistry() {
    }

    public static boolean isTool(String itemId) {
        return itemId != null && TOOLS.containsKey(itemId);
    }

    public static ToolDefinition get(String itemId) {
        return itemId == null ? null : TOOLS.get(itemId);
    }

    public static float getMiningMultiplier(String itemId, String blockId) {
        ToolDefinition tool = get(itemId);
        if (tool == null || blockId == null || !isEffectiveOn(tool.getType(), blockId)) {
            return 1f;
        }
        return Math.max(1f, tool.getMiningMultiplier());
    }

    public static int getAttackDamage(String itemId, int baseDamage) {
        ToolDefinition tool = get(itemId);
        return tool == null ? baseDamage : Math.max(baseDamage, tool.getAttackDamage());
    }

    public static int getMaxDurability(String itemId) {
        ToolDefinition tool = get(itemId);
        return tool == null ? 0 : tool.getMaxDurability();
    }

    public static int getHarvestLevel(String itemId) {
        ToolDefinition tool = get(itemId);
        return tool == null ? 0 : harvestLevel(tool.getMaterial());
    }

    public static boolean isPickaxe(String itemId) {
        ToolDefinition tool = get(itemId);
        return tool != null && tool.getType() == ToolType.PICKAXE;
    }

    public static boolean isSword(String itemId) {
        ToolDefinition tool = get(itemId);
        return tool != null && tool.getType() == ToolType.SWORD;
    }

    private static void register(String itemId, ToolType type, ToolMaterial material,
                                 String textureName, float miningMultiplier, int attackDamage,
                                 int maxDurability) {
        register(itemId, type, material, textureName, textureName, miningMultiplier, attackDamage, maxDurability);
    }

    private static void register(String itemId, ToolType type, ToolMaterial material,
                                 String textureName, String heldTextureName, float miningMultiplier,
                                 int attackDamage, int maxDurability) {
        TOOLS.put(itemId, new ToolDefinition(itemId, type, material, textureName, heldTextureName,
            miningMultiplier, attackDamage, maxDurability));
    }

    private static boolean isEffectiveOn(ToolType type, String blockId) {
        if (type == ToolType.PICKAXE) return PICKAXE_BLOCKS.contains(blockId);
        if (type == ToolType.AXE) return AXE_BLOCKS.contains(blockId);
        if (type == ToolType.SHOVEL) return SHOVEL_BLOCKS.contains(blockId);
        return false;
    }

    private static int harvestLevel(ToolMaterial material) {
        if (material == ToolMaterial.WOOD) return 1;
        if (material == ToolMaterial.STONE || material == ToolMaterial.COPPER || material == ToolMaterial.GOLD) return 2;
        if (material == ToolMaterial.IRON) return 3;
        if (material == ToolMaterial.DIAMOND) return 4;
        if (material == ToolMaterial.NETHERITE) return 5;
        return 0;
    }
}
