package com.main.game.worldgen;

import com.main.game.entities.mob.Mob;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

/**
 * Unit tests to verify BiomeSpawnTable weighted selection.
 */
public class BiomeSpawnTableTest {

    @Test
    public void testWeightedSelection() {
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random rand = new Random(12345L);

        int cow=0,pig=0,chicken=0,sheep=0;
        for (int i = 0; i < 20; i++) {
            Mob.MobType type = table.selectMobForBiome(BiomeType.FOREST, rand);
            switch (type) {
                case COW: cow++; break;
                case PIG: pig++; break;
                case CHICKEN: chicken++; break;
                case SHEEP: sheep++; break;
                default: fail("Unexpected mob in FOREST: " + type);
            }
        }
        assertEquals(20, cow+pig+chicken+sheep);
        assertTrue("At least one passive should spawn", cow+pig+chicken+sheep > 0);
    }

    @Test
    public void testBiomeDistribution() {
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random rand = new Random(54321L);

        for (int i = 0; i < 10; i++) {
            Mob.MobType t = table.selectMobForBiome(BiomeType.DESERT, rand);
            assertTrue("DESERT should only spawn HUSK or SKELETON", t == Mob.MobType.HUSK || t == Mob.MobType.SKELETON);
        }

        rand = new Random(54321L);
        for (int i = 0; i < 10; i++) {
            Mob.MobType t = table.selectMobForBiome(BiomeType.SNOW, rand);
            assertTrue("SNOW should only spawn STRAY or SKELETON", t == Mob.MobType.STRAY || t == Mob.MobType.SKELETON);
        }
    }

    @Test
    public void testCherryOnlySpawnsPassiveMobs() {
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random rand = new Random(24680L);

        for (int i = 0; i < 30; i++) {
            Mob.MobType type = table.selectMobForBiome(BiomeType.CHERRY, rand);
            assertTrue("CHERRY should only spawn passive mobs", isPassive(type));
        }
    }

    @Test
    public void testPlainsSupportsPassiveAndHostileSelection() {
        BiomeSpawnTable table = new BiomeSpawnTable();
        Random passiveRand = new Random(1122L);
        Random hostileRand = new Random(3344L);

        for (int i = 0; i < 30; i++) {
            assertTrue("PLAINS passive selection should only return passive mobs",
                isPassive(table.selectPassiveForBiome(BiomeType.PLAINS, passiveRand)));
            assertTrue("PLAINS hostile selection should only return hostile mobs",
                isHostile(table.selectHostileForBiome(BiomeType.PLAINS, hostileRand)));
        }
    }

    private boolean isPassive(Mob.MobType type) {
        return type == Mob.MobType.COW
            || type == Mob.MobType.PIG
            || type == Mob.MobType.CHICKEN
            || type == Mob.MobType.SHEEP
            || type == Mob.MobType.HORSE;
    }

    private boolean isHostile(Mob.MobType type) {
        return type == Mob.MobType.ZOMBIE
            || type == Mob.MobType.HUSK
            || type == Mob.MobType.SKELETON
            || type == Mob.MobType.STRAY
            || type == Mob.MobType.PILLAGER
            || type == Mob.MobType.VINDICATOR
            || type == Mob.MobType.EVOKER
            || type == Mob.MobType.RAVAGER;
    }
}
