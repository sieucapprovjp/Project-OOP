package com.main.game.worldgen;

import com.main.game.entities.mob.Mob;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Quản lý weighted spawn table cho từng biome.
 * Đảm bảo weighted selection deterministic khi sử dùng cùng Random seed.
 *
 * Ví dụ:
 *  - FOREST: [COW(weight=20), PIG(weight=15), CHICKEN(weight=18), SHEEP(weight=12)]
 *  - CHERRY: [COW(weight=20), PIG(weight=15), CHICKEN(weight=18), SHEEP(weight=12)]
 *  - PLAINS: passive by day, passive + hostile by night through BiomeMobSpawner
 *  - DESERT: [HUSK(weight=15), SKELETON(weight=10)]
 *  - SNOW:   [STRAY(weight=16), SKELETON(weight=8)]
 */
public final class BiomeSpawnTable {

    private final Map<BiomeType, List<MobEntry>> biomeTables = new EnumMap<>(BiomeType.class);
    private final Map<BiomeType, List<MobEntry>> passiveTables = new EnumMap<>(BiomeType.class);
    private final Map<BiomeType, List<MobEntry>> hostileTables = new EnumMap<>(BiomeType.class);

    public BiomeSpawnTable() {
        initializeBiomes();
    }

    /**
     * Chọn mob type cho biome dựa trên weighted distribution.
     * Không phải random selection; dùng Random object để maintain seed consistency.
     */
    public Mob.MobType selectMobForBiome(BiomeType biome, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }

        BiomeType key = (biome != null) ? biome : BiomeType.FOREST;
        List<MobEntry> entries = biomeTables.getOrDefault(key, biomeTables.get(BiomeType.FOREST));
        return selectFromEntries(entries, random);
    }

    public Mob.MobType selectPassiveForBiome(BiomeType biome, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        BiomeType key = (biome != null) ? biome : BiomeType.FOREST;
        List<MobEntry> entries = passiveTables.getOrDefault(key, passiveTables.get(BiomeType.FOREST));
        return selectFromEntries(entries, random);
    }

    public Mob.MobType selectHostileForBiome(BiomeType biome, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        BiomeType key = (biome != null) ? biome : BiomeType.DESERT;
        List<MobEntry> entries = hostileTables.getOrDefault(key, hostileTables.get(BiomeType.DESERT));
        return selectFromEntries(entries, random);
    }

    private Mob.MobType selectFromEntries(List<MobEntry> entries, Random random) {
        if (entries == null || entries.isEmpty()) {
            return Mob.MobType.ZOMBIE; // Fallback
        }

        int totalWeight = 0;
        for (MobEntry e : entries) {
            if (e.weight > 0) {
                totalWeight += e.weight;
            }
        }
        if (totalWeight <= 0) {
            return entries.get(0).type;
        }

        int pick = random.nextInt(totalWeight);
        int acc = 0;
        for (MobEntry e : entries) {
            if (e.weight <= 0) continue;
            acc += e.weight;
            if (pick < acc) {
                return e.type;
            }
        }
        return entries.get(0).type;
    }

    private void initializeBiomes() {
        // FOREST: chỉ passive để giữ vùng spawn đầu game dễ thở hơn.
        List<MobEntry> forest = new ArrayList<>();
        forest.add(new MobEntry(Mob.MobType.COW, 20));
        forest.add(new MobEntry(Mob.MobType.PIG, 15));
        forest.add(new MobEntry(Mob.MobType.CHICKEN, 18));
        forest.add(new MobEntry(Mob.MobType.SHEEP, 12));
        biomeTables.put(BiomeType.FOREST, forest);
        passiveTables.put(BiomeType.FOREST, forest);
        biomeTables.put(BiomeType.CHERRY, new ArrayList<>(forest));
        passiveTables.put(BiomeType.CHERRY, new ArrayList<>(forest));

        List<MobEntry> plainsPassive = new ArrayList<>(forest);
        plainsPassive.add(new MobEntry(Mob.MobType.HORSE, 8));
        List<MobEntry> plainsHostile = new ArrayList<>();
        plainsHostile.add(new MobEntry(Mob.MobType.ZOMBIE, 14));
        plainsHostile.add(new MobEntry(Mob.MobType.SKELETON, 10));
        biomeTables.put(BiomeType.PLAINS, combined(plainsPassive, plainsHostile));
        passiveTables.put(BiomeType.PLAINS, plainsPassive);
        hostileTables.put(BiomeType.PLAINS, plainsHostile);

        // DESERT: chỉ hostile
        List<MobEntry> desert = new ArrayList<>();
        desert.add(new MobEntry(Mob.MobType.HUSK, 15));
        desert.add(new MobEntry(Mob.MobType.SKELETON, 10));
        biomeTables.put(BiomeType.DESERT, desert);
        hostileTables.put(BiomeType.DESERT, desert);

        // SNOW: chỉ hostile theo cùng rule với các biome nguy hiểm.
        List<MobEntry> snow = new ArrayList<>();
        snow.add(new MobEntry(Mob.MobType.STRAY, 16));
        snow.add(new MobEntry(Mob.MobType.SKELETON, 8));
        biomeTables.put(BiomeType.SNOW, snow);
        hostileTables.put(BiomeType.SNOW, snow);
    }

    private List<MobEntry> combined(List<MobEntry> first, List<MobEntry> second) {
        List<MobEntry> combined = new ArrayList<>(first);
        combined.addAll(second);
        return combined;
    }

    /**
     * Inner class lưu mob type + spawn weight.
     */
    private static final class MobEntry {
        final Mob.MobType type;
        final int weight;

        MobEntry(Mob.MobType type, int weight) {
            this.type = type;
            this.weight = weight;
        }
    }
}
