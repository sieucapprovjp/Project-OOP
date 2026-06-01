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

        int cow=0,pig=0,chicken=0,sheep=0,zombie=0;
        for (int i = 0; i < 20; i++) {
            Mob.MobType type = table.selectMobForBiome(BiomeType.FOREST, rand);
            switch (type) {
                case COW: cow++; break;
                case PIG: pig++; break;
                case CHICKEN: chicken++; break;
                case SHEEP: sheep++; break;
                case ZOMBIE: zombie++; break;
                default: fail("Unexpected mob in FOREST: " + type);
            }
        }
        assertEquals(20, cow+pig+chicken+sheep+zombie);
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
            assertTrue("SNOW should only spawn STRAY or SHEEP", t == Mob.MobType.STRAY || t == Mob.MobType.SHEEP);
        }
    }
}
