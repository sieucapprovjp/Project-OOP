package com.main.game.items;

import static org.junit.Assert.assertEquals;

import com.main.game.entities.mob.Mob;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class MobDropFactoryTest {

    @Test
    public void passiveMobsDropRawFood() {
        assertEquals(Collections.singletonList("raw_beef"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.COW, 1f));
        assertEquals(Collections.singletonList("raw_pork"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.PIG, 1f));
        assertEquals(Collections.singletonList("raw_mutton"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.SHEEP, 1f));
        assertEquals(Collections.singletonList("raw_chicken"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.CHICKEN, 1f));
    }

    @Test
    public void undeadMobsDropExpectedItems() {
        assertEquals(Collections.singletonList("rotten_flesh"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.ZOMBIE, 1f));
        assertEquals(Collections.singletonList("rotten_flesh"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.HUSK, 1f));
        assertEquals(Arrays.asList("bone", "bonemeal"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.SKELETON, 0.49f));
        assertEquals(Collections.singletonList("bone"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.SKELETON, 0.5f));
        assertEquals(Arrays.asList("bone", "bonemeal"),
            MobDropFactory.dropItemIdsForType(Mob.MobType.STRAY, 0f));
    }

    @Test
    public void mobsWithoutDropsReturnEmptyList() {
        assertEquals(Collections.emptyList(),
            MobDropFactory.dropItemIdsForType(Mob.MobType.DOG, 0f));
        assertEquals(Collections.emptyList(),
            MobDropFactory.dropItemIdsForType(null, 0f));
    }
}
