package com.main.game.inventory;

import java.util.HashMap;
import java.util.Map;

public final class ArmorRegistry {

    public static final class ArmorDefinition {
        private final String itemId;
        private final ArmorSlot slot;
        private final String material;
        private final int defensePoints;
        private final int maxDurability;
        private final String textureName;

        private ArmorDefinition(String itemId, ArmorSlot slot, String material,
                                int defensePoints, int maxDurability, String textureName) {
            this.itemId = itemId;
            this.slot = slot;
            this.material = material;
            this.defensePoints = defensePoints;
            this.maxDurability = maxDurability;
            this.textureName = textureName;
        }

        public String getItemId() {
            return itemId;
        }

        public ArmorSlot getSlot() {
            return slot;
        }

        public String getMaterial() {
            return material;
        }

        public int getDefensePoints() {
            return defensePoints;
        }

        public int getMaxDurability() {
            return maxDurability;
        }

        public String getTextureName() {
            return textureName;
        }
    }

    private static final Map<String, ArmorDefinition> ARMOR = new HashMap<>();

    static {
        register("leather_cap", ArmorSlot.HELMET, "leather", 1, 45, "tools/stone/leather_cap");
        register("leather_chestplate", ArmorSlot.CHESTPLATE, "leather", 3, 45, "tools/stone/leather_chestplate");
        register("leather_pants", ArmorSlot.LEGGINGS, "leather", 2, 45, "tools/stone/leather_pants");
        register("leather_boots", ArmorSlot.BOOTS, "leather", 1, 45, "tools/stone/leather_boots");
        registerMaterial("copper", 2, 5, 4, 1, 60);
        registerMaterial("iron", 2, 6, 5, 2, 75);
        registerMaterial("gold", 2, 5, 3, 1, 24);
        registerMaterial("diamond", 3, 8, 6, 3, 110);
        registerMaterial("netherite", 3, 8, 6, 3, 140);
    }

    private ArmorRegistry() {
    }

    public static boolean isArmor(String itemId) {
        return itemId != null && ARMOR.containsKey(itemId);
    }

    public static ArmorDefinition get(String itemId) {
        return itemId == null ? null : ARMOR.get(itemId);
    }

    public static ArmorSlot getSlot(String itemId) {
        ArmorDefinition armor = get(itemId);
        return armor == null ? null : armor.getSlot();
    }

    public static int getDefensePoints(String itemId) {
        ArmorDefinition armor = get(itemId);
        return armor == null ? 0 : armor.getDefensePoints();
    }

    public static int getMaxDurability(String itemId) {
        ArmorDefinition armor = get(itemId);
        return armor == null ? 0 : armor.getMaxDurability();
    }

    public static String getTextureName(String itemId) {
        ArmorDefinition armor = get(itemId);
        return armor == null ? null : armor.getTextureName();
    }

    private static void registerMaterial(String material, int helmetDefense, int chestplateDefense,
                                         int leggingsDefense, int bootsDefense, int maxDurability) {
        register(material + "_helmet", ArmorSlot.HELMET, material, helmetDefense, maxDurability);
        register(material + "_chestplate", ArmorSlot.CHESTPLATE, material, chestplateDefense, maxDurability);
        register(material + "_leggings", ArmorSlot.LEGGINGS, material, leggingsDefense, maxDurability);
        register(material + "_boots", ArmorSlot.BOOTS, material, bootsDefense, maxDurability);
    }

    private static void register(String itemId, ArmorSlot slot, String material,
                                 int defensePoints, int maxDurability) {
        register(itemId, slot, material, defensePoints, maxDurability, "tools/" + material + "/" + itemId);
    }

    private static void register(String itemId, ArmorSlot slot, String material,
                                 int defensePoints, int maxDurability, String textureName) {
        ARMOR.put(itemId, new ArmorDefinition(itemId, slot, material, defensePoints,
            maxDurability, textureName));
    }
}
