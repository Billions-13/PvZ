package until;

import plants_e.*;
import Zombies.*;
import entity.*;

import java.util.*;

public class GameWorld implements ProjectileWorld {

    private final List<Plant> plants = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Sun> suns = new ArrayList<>();

    private final GameAttackHandler attackHandler;
    private ZombieSpawner spawner;
    private Gardener gardener;

    private int wave = 1;
    private int zombiesKilled;
    private boolean win;
    private boolean lose;

    public GameWorld(GameAttackHandler attackHandler) {
        this.attackHandler = attackHandler;
    }

    public Gardener getGardener() {
        return gardener;
    }


    /* ================= SETUP ================= */

    public void setSpawner(ZombieSpawner spawner) {
        this.spawner = spawner;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
    }

    /* ================= ADD / REMOVE ================= */

    public void addPlant(Plant p) {
        if (p == null) return;
        plants.add(p);
        p.onPlaced();
    }

    public void addZombie(Zombie z) {
        zombies.add(z);
    }

    public void addSun(Sun s) {
        suns.add(s);
    }

    /* ================= UPDATE ================= */

    public void update(double dt) {
        if (win || lose) return;

        double now = System.nanoTime() / 1e9;

        // Update plant
        for (Plant p : plants) {
            p.update(now);
            if (p instanceof Sunflower s) {
                SunProductionBehavior b = s.getSunProductionBehavior();
                if (b != null) b.updateSunProduction(s, now, this);
            }
        }

        // Update zombie
        Iterator<Zombie> zit = zombies.iterator();
        while (zit.hasNext()) {
            Zombie z = zit.next();
            z.update(dt);

            // Zombie ăn plant
            boolean hitPlant = false;
            for (Plant p : plants) {
                if (z.isAlive() && p.isAlive()
                        && z.getRow() == p.getRow()
                        && z.getX() <= p.getPositionX() + 20
                        && z.getX() >= p.getPositionX() - 10) {
                    z.setState(ZombieState.ATTACK);
                    hitPlant = true;
                    if (z.canAttackNow()) {
                        p.takeDamage(z.getDamage());
                    }
                    break;
                }
            }

            if (!hitPlant && z.isAlive() && z.getState() == ZombieState.ATTACK) {
                z.setState(ZombieState.ADVANCE);
            }
            // Zombie ăn gardener → thua
            if (gardener != null && z.isAlive()) {
                if (Math.abs(z.getX() - gardener.getX()) < 30 &&
                        Math.abs(z.getY() - gardener.getY()) < 30) {
                    gardener.takeDamage(z.getDamage());
                    if (gardener.isDead()) lose = true;
                }
            }

            // Zombie tới nhà → thua
            if (z.getX() < 0) lose = true;

            if (z.isDead()) {
                zombiesKilled++;
                zit.remove();
            }
        }

        // Update projectile
        attackHandler.updateProjectiles(this, dt);

        // Spawn zombie
        if (spawner != null) {
            spawner.update(dt);
        }

        // Qua wave
        if (zombies.isEmpty() && spawner != null && spawner.isWaveDone()) {
            wave++;
            if (wave > 4) {
                win = true;
            } else {
                spawner.setWave(wave);
            }
        }
    }

    /* ================= PROJECTILE WORLD ================= */

    @Override
    public Zombie findFirstZombieInPath(int row, double projectileX) {
        for (Zombie z : zombies) {
            if (z.getRow() == row && z.getX() <= projectileX && z.isAlive()) {
                return z;
            }
        }
        return null;
    }

    @Override
    public void applySlowToZombie(Zombie zombie) {
        zombie.slow(3.0);
    }

    /* ================= GETTER ================= */

    public int getWave() { return wave; }
    public int getZombiesKilled() { return zombiesKilled; }
    public boolean isWin() { return win; }
    public boolean isLose() { return lose; }

    public List<Plant> getPlants() { return plants; }
    public List<Projectile> getProjectiles() { return attackHandler.getProjectilesSnapshot(); }
    public List<Sun> getSuns() { return suns; }
    public List<Zombie> getZombies() {return zombies;}

}
