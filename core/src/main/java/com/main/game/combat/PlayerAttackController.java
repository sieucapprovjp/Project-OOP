package com.main.game.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.main.game.entities.EntityManager;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import com.main.game.inventory.ToolRegistry;

public class PlayerAttackController {

    private static final float ATTACK_COOLDOWN = 0.6f;
    private static final float ATTACK_REACH = 2.2f;
    private static final int BASE_ATTACK_DAMAGE = 2;
    private static final float KNOCKBACK_X = 3.0f;
    private static final float KNOCKBACK_Y = 1.2f;

    private final Vector2 mouseWorld = new Vector2();
    private float cooldownTimer = 0f;
    private MobDeathListener mobDeathListener;

    public void setMobDeathListener(MobDeathListener mobDeathListener) {
        this.mobDeathListener = mobDeathListener;
    }

    public boolean update(float delta, Player player, EntityManager entityManager,
                          OrthographicCamera camera, Viewport viewport, boolean inputBlocked,
                          String heldItemId) {
        if (cooldownTimer > 0f) {
            cooldownTimer -= delta;
        }
        if (inputBlocked || player == null || entityManager == null || camera == null || viewport == null || !player.isAlive()) {
            return false;
        }
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || cooldownTimer > 0f) {
            return false;
        }

        Mob target = findTarget(player, entityManager, camera, viewport);
        if (target == null) {
            return false;
        }

        float direction = target.getX() + target.getWidth() / 2f >= player.getX() + player.getWidth() / 2f ? 1f : -1f;
        if (target.takeDamage(getDamage(player, heldItemId))) {
            boolean killed = !target.isAlive();
            target.onPlayerHit(player);
            target.applyKnockback(direction * KNOCKBACK_X, KNOCKBACK_Y);
            if (killed && mobDeathListener != null) {
                mobDeathListener.onMobKilled(target);
            }
            cooldownTimer = ATTACK_COOLDOWN;
            return true;
        }
        return false;
    }

    private int getDamage(Player player, String heldItemId) {
        int damage = ToolRegistry.getAttackDamage(heldItemId, BASE_ATTACK_DAMAGE);
        boolean falling = !player.isOnGround() && player.getVelocity().y < 0f;
        return falling ? Math.round(damage * 1.5f) : damage;
    }

    private Mob findTarget(Player player, EntityManager entityManager, OrthographicCamera camera, Viewport viewport) {
        float centerX = player.getX() + player.getWidth() / 2f;
        float centerY = player.getY() + player.getHeight() / 2f;
        camera.update();
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouseWorld);

        Mob nearest = null;
        float nearestDst2 = Float.MAX_VALUE;
        for (Mob mob : entityManager.getMobs()) {
            if (mob == null || !mob.isAlive() || !mob.getBounds().contains(mouseWorld)
                || !isWithinReach(mob, centerX, centerY)) {
                continue;
            }
            float dx = (mob.getX() + mob.getWidth() / 2f) - centerX;
            float dy = (mob.getY() + mob.getHeight() / 2f) - (player.getY() + player.getHeight() / 2f);
            float dst2 = dx * dx + dy * dy;
            if (dst2 < nearestDst2) {
                nearestDst2 = dst2;
                nearest = mob;
            }
        }
        return nearest;
    }

    private boolean isWithinReach(Mob mob, float centerX, float centerY) {
        Rectangle bounds = mob.getBounds();
        float closestX = Math.max(bounds.x, Math.min(centerX, bounds.x + bounds.width));
        float closestY = Math.max(bounds.y, Math.min(centerY, bounds.y + bounds.height));
        float dx = closestX - centerX;
        float dy = closestY - centerY;
        return dx * dx + dy * dy <= ATTACK_REACH * ATTACK_REACH;
    }
}
