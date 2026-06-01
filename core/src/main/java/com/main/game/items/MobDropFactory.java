package com.main.game.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.main.game.entities.mob.Mob;
import com.main.game.inventory.ItemRegistry;
import com.main.game.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MobDropFactory {

    private static final float SKELETON_BONEMEAL_CHANCE = 0.5f;

    private MobDropFactory() {
    }

    public static List<HarvestEntry> createDrops(Mob mob, World world, Random random) {
        if (mob == null || world == null || random == null) {
            return Collections.emptyList();
        }
        int tileX = Math.max(0, Math.min(world.width - 1, (int) Math.floor(mob.getX() + mob.getWidth() / 2f)));
        int tileY = Math.max(0, Math.min(world.height - 1, (int) Math.floor(mob.getY() + mob.getHeight() / 2f)));
        int tileIdx = HarvestEntry.toTileIdx(tileX, tileY, world);

        List<HarvestEntry> entries = new ArrayList<>();
        for (String itemId : dropItemIdsForType(mob.getType(), random.nextFloat())) {
            TextureRegion texture = ItemRegistry.getTexture(itemId);
            entries.add(new HarvestEntry(
                tileIdx,
                itemId,
                texture,
                1,
                randomHorizontalSpeed(random),
                HarvestEntry.RANDOM_VERTICAL_SPEED
            ));
        }
        return entries;
    }

    static List<String> dropItemIdsForType(Mob.MobType type, float bonusRoll) {
        if (type == null) {
            return Collections.emptyList();
        }
        List<String> drops = new ArrayList<>();
        switch (type) {
            case COW:
                drops.add("raw_beef");
                break;
            case PIG:
                drops.add("raw_pork");
                break;
            case SHEEP:
                drops.add("raw_mutton");
                break;
            case CHICKEN:
                drops.add("raw_chicken");
                break;
            case ZOMBIE:
            case HUSK:
                drops.add("rotten_flesh");
                break;
            case SKELETON:
            case STRAY:
                drops.add("bone");
                if (bonusRoll < SKELETON_BONEMEAL_CHANCE) {
                    drops.add("bonemeal");
                }
                break;
            default:
                break;
        }
        return drops;
    }

    private static float randomHorizontalSpeed(Random random) {
        return -0.12f + random.nextFloat() * 0.24f;
    }
}
