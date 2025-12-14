package plants_e;
import Zombies.Zombie;

public class Projectile {

    private final int row;
    private final double speed;
    private final int damage;
    private final ProjectileType type;

    private double x;
    private double y;
    private boolean active = true;

    public Projectile(int row, double x, double y, double speed, int damage, ProjectileType type) {
        this.row = row;
        this.x = x;
        this.y = y;
        this.speed = Math.max(0.0, speed);
        this.damage = Math.max(0, damage);
        this.type = type;
    }

    public void update(ProjectileWorld world, double deltaTime) {
        if (!active || world == null || deltaTime <= 0) return;

        x += speed * deltaTime;

        Zombie hit = world.findFirstZombieInPath(row, x);
        if (hit != null && hit.isAlive()) {
            hit.takeDamage(damage);
            if (type == ProjectileType.ICE_PEA) {
                world.applySlowToZombie(hit);
            }
            active = false;
        }
    }

    public boolean isActive() { return active; }
    public int getRow() { return row; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getDamage() { return damage; }
    public ProjectileType getType() { return type; }
}
