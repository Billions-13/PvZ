package plants_e;
import Zombies.Zombie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * GameAttackHandler là hiện thực AttackHandler:
 * - Nhận yêu cầu tấn công từ Plant.
 * - Tạo Projectile tương ứng và quản lý danh sách projectile đang bay.
 * - Log combat (fire / hit).
 */
public class GameAttackHandler implements AttackHandler {

    private static final double DEFAULT_PROJECTILE_SPEED = 300.0;

    private final List<String> combatLog = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();      // vẫn giữ nếu sau này cần
    private final List<Projectile> projectiles = new ArrayList<>();

    public void addZombie(Zombie zombie) {
        if (zombie != null) {
            zombies.add(zombie);
        }
    }

    @Override
    public void handleAttack(Plant source) {
        if (source == null || !source.isAlive()) return;

        // Xác định loại đạn: Snowpea -> ICE_PEA, còn lại -> PEA
        ProjectileType projectileType =
                source.getSpecialEffect() == EffectType.SLOW
                        ? ProjectileType.ICE_PEA
                        : ProjectileType.PEA;

        Projectile projectile = new Projectile(
                source.getRow(),
                source.getPositionX(),
                source.getPositionY(),
                DEFAULT_PROJECTILE_SPEED,
                source.getAttackDamage(),
                projectileType
        );

        projectiles.add(projectile);

        String log = "[FIRE] " + source.getPlantType().getDisplayName()
                + " at (row=" + source.getRow()
                + ", col=" + source.getCol()
                + ") fires " + projectileType
                + " projectile with damage=" + source.getAttackDamage()
                + ", effect=" + source.getSpecialEffect();
        combatLog.add(log);
    }

    /**
     * Gọi mỗi frame/tick từ game loop:
     * - Cập nhật vị trí projectile.
     * - Khi đạn va trúng zombie (Projectile tự gọi takeDamage),
     *   handler log lại sự kiện HIT rồi remove projectile.
     */
    public void updateProjectiles(ProjectileWorld world, double deltaTime) {
        if (world == null) return;

        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();

            if (!p.isActive()) {
                it.remove();
                continue;
            }

            p.update(world, deltaTime);

            if (!p.isActive()) {
                String log = "[HIT] projectile " + p.getType()
                        + " in row " + p.getRow()
                        + " deals damage=" + p.getDamage();
                combatLog.add(log);
                it.remove();
            }
        }
    }

    public List<String> getCombatLogSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(combatLog));
    }
    public List<Projectile> getProjectilesSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(projectiles));
    }
    public void clearCombatLog() {
        combatLog.clear();
    }
}
