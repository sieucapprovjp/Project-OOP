package com.main.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.main.game.entities.mob.Mob;
import com.main.game.entities.player.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EntityManager — quản lý vòng đời toàn bộ entity trong GameScreen.
 *
 * Trách nhiệm:
 *  - Giữ danh sách tất cả entity (Player + Mob + mở rộng sau).
 *  - Gọi update() / render() theo đúng thứ tự mỗi frame.
 *  - Tự động dọn entity chết (isAlive == false) ra khỏi danh sách.
 *  - Cung cấp API thêm/xoá entity an toàn (tránh ConcurrentModification).
 *
 * Cách dùng trong GameScreen:
 * <pre>
 *   // Khởi tạo
 *   entityManager = new EntityManager();
 *   player = new Player(spawnX, spawnY, physicsEngine);
 *   entityManager.setPlayer(player);
 *   entityManager.addMob(new Mob(10, 5, MobType.ZOMBIE, player, physicsEngine));
 *
 *   // Trong render()
 *   entityManager.update(delta);
 *   batch.begin();
 *   entityManager.render(batch);
 *   batch.end();
 *
 *   // Khi thoát
 *   entityManager.dispose();
 * </pre>
 *
 * TODO(DUOC-ENTITY):
 *  - Thêm spawn queue để spawn mob an toàn giữa frame.
 *  - Kết hợp với World chunk để chỉ update entity trong chunk active.
 */
public class EntityManager {

    // ─── Danh sách entity ─────────────────────────────────────
    private Player          player;
    private final List<Mob>    mobs       = new ArrayList<>();
    private final List<Entity> allEntities= new ArrayList<>(); // player + mobs

    // Hàng chờ thêm/xoá an toàn trong lúc iterate
    private final List<Entity> toAdd    = new ArrayList<>();
    private final List<Entity> toRemove = new ArrayList<>();

    // ─── Quản lý Player ───────────────────────────────────────

    public void setPlayer(Player player) {
        if (this.player != null) allEntities.remove(this.player);
        this.player = player;
        allEntities.add(0, player); // Player luôn ở đầu danh sách
    }

    public Player getPlayer() { return player; }

    // ─── Quản lý Mob ──────────────────────────────────────────

    /** Thêm mob vào hàng chờ — sẽ được insert đầu frame tiếp theo */
    public void addMob(Mob mob) {
        toAdd.add(mob);
        mobs.add(mob);
    }

    /** Xoá mob (ví dụ khi bị kill bởi event bên ngoài) */
    public void removeMob(Mob mob) {
        toRemove.add(mob);
        mobs.remove(mob);
    }

    public List<Mob> getMobs() { return mobs; }

    // ─── Update ───────────────────────────────────────────────

    /**
     * Gọi mỗi frame từ GameScreen.render().
     * Thứ tự: flush queue -> update tất cả -> dọn entity chết.
     */
    public void update(float delta) {
        flushQueues();

        // Update player trước
        if (player != null) player.update(delta);

        // Update mob — dùng iterator để xoá an toàn
        Iterator<Entity> it = allEntities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();
            if (e == player) continue; // đã update ở trên
            e.update(delta);
            if (!e.isAlive()) {
                toRemove.add(e);
            }
        }

        cleanDead();
    }

    // ─── Render ───────────────────────────────────────────────

    /**
     * Gọi giữa batch.begin() và batch.end() trong GameScreen.
     * Render mob trước, player sau (player hiển thị trên mob).
     */
    public void render(SpriteBatch batch) {
        for (Entity e : allEntities) {
            if (e == player) continue;
            e.render(batch);
        }
        if (player != null) player.render(batch);
    }

    // ─── Dispose ──────────────────────────────────────────────

    /** Giải phóng tài nguyên tất cả entity. Gọi khi thoát GameScreen. */
    public void dispose() {
        for (Entity e : allEntities) e.dispose();
        allEntities.clear();
        mobs.clear();
        toAdd.clear();
        toRemove.clear();
        player = null;
    }

    // ─── Helpers ──────────────────────────────────────────────

    /** Đẩy entity từ hàng chờ vào danh sách chính */
    private void flushQueues() {
        for (Entity e : toAdd) {
            if (!allEntities.contains(e)) allEntities.add(e);
        }
        toAdd.clear();
    }

    /** Xoá entity chết khỏi allEntities và mobs */
    private void cleanDead() {
        allEntities.removeAll(toRemove);
        mobs.removeAll(toRemove);
        toRemove.clear();
    }

    // ─── Tiện ích query ───────────────────────────────────────

    /** Trả về số mob còn sống */
    public int aliveMobCount() {
        int count = 0;
        for (Mob m : mobs) if (m.isAlive()) count++;
        return count;
    }

    /** Kiểm tra game over */
    public boolean isPlayerDead() {
        return player == null || !player.isAlive();
    }
}
