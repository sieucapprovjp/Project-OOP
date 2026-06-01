package com.main.game.worldgen;

import com.main.game.entities.Mob;
import java.util.Random;

/**
 * Simple test để verify BiomeSpawnTable weighted selection.
 */
public class BiomeSpawnTableTest {

    public static void main(String[] args) {
        testWeightedSelection();
        testBiomeDistribution();
    }

    private static void testWeightedSelection() {
        System.out.println("=== Test Weighted Selection ===");
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random rand = new Random(12345L);

        // Spawn 20 mobs cho FOREST biome
        int[] forestCounts = new int[5]; // COW, PIG, CHICKEN, SHEEP, ZOMBIE
        for (int i = 0; i < 20; i++) {
            Mob.MobType type = table.selectMobForBiome(BiomeType.FOREST, rand);
            System.out.println("FOREST spawn " + (i + 1) + ": " + type);
            countMob(type, forestCounts);
        }
        System.out.println("Forest counts: COW=" + forestCounts[0] + " PIG=" + forestCounts[1] 
            + " CHICKEN=" + forestCounts[2] + " SHEEP=" + forestCounts[3] + " ZOMBIE=" + forestCounts[4]);
    }

    private static void testBiomeDistribution() {
        System.out.println("\n=== Test Biome Distribution ===");
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random rand = new Random(54321L);

        // Desert: HUSK, SKELETON chỉ
        Mob.MobType[] desertSpawns = new Mob.MobType[10];
        for (int i = 0; i < 10; i++) {
            desertSpawns[i] = table.selectMobForBiome(BiomeType.DESERT, rand);
            System.out.println("DESERT spawn " + (i + 1) + ": " + desertSpawns[i]);
        }

        // Snow: STRAY, SHEEP chỉ
        Mob.MobType[] snowSpawns = new Mob.MobType[10];
        rand = new Random(54321L);
        for (int i = 0; i < 10; i++) {
            snowSpawns[i] = table.selectMobForBiome(BiomeType.SNOW, rand);
            System.out.println("SNOW spawn " + (i + 1) + ": " + snowSpawns[i]);
        }
    }

    private static void countMob(Mob.MobType type, int[] counts) {
        switch (type) {
            case COW: counts[0]++; break;
            case PIG: counts[1]++; break;
            case CHICKEN: counts[2]++; break;
            case SHEEP: counts[3]++; break;
            case ZOMBIE: counts[4]++; break;
        }
    }
}
