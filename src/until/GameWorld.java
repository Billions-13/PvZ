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
    private int sunPoints = 150;


    private final GameAttackHandler attackHandler;
    private ZombieSpawner spawner;
    private Gardener gardener;

    private int wave = 1;
    private int zombiesKilled;
    private double gardenerHitTimer;

    private boolean win;
    private boolean lose;

    private double gameTime;
    private double skySunTimer;
    private double skySunInterval = 6.0;



    public GameWorld(GameAttackHandler attackHandler) {
        this.attackHandler = attackHandler;
    }

    public Gardener getGardener() {
        return gardener;
    }

    public void setSpawner(ZombieSpawner spawner) {
        this.spawner = spawner;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
    }

    public void addPlant(Plant p) {
        if (p == null) return;
        plants.add(p);
        p.onPlaced();
    }

    public boolean removePlantAt(int row, int col) {
        Iterator<Plant> it = plants.iterator();
        while (it.hasNext()) {
            Plant p = it.next();
            if (p.isAlive() && p.getRow() == row && p.getCol() == col) {
                p.onRemoved();
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void addZombie(Zombie z) {
        zombies.add(z);
    }

    public void addSun(Sun s) {
        suns.add(s);
    }

    public void update(double dt) {

        if (win || lose) return;
        gameTime += dt;

        double now = System.nanoTime() / 1e9;
        final int tile = 80;
        double fireLineX = 12 * tile;

        for (Plant p : plants) {

            if (p instanceof PeaShooter || p instanceof Snowpea) {
                boolean ok = false;
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getRow() == p.getRow() && z.getX() <= fireLineX) {
                        ok = true;
                        break;
                    }
                }
                p.setAttackEnabled(ok);
            } else {
                p.setAttackEnabled(true);
            }

            p.update(now);

            if (p instanceof Sunflower s) {
                SunProductionBehavior b = s.getSunProductionBehavior();
                if (b != null) b.updateSunProduction(s, now, this);
            }
        }



        Iterator<Sun> sit = suns.iterator();
        while (sit.hasNext()) {
            Sun s = sit.next();
            s.update(dt);
            if (s.getY() > 7 * tile) {
                sit.remove();
                continue;
            }

            if (s.isCollected()) {
                addSunPoints(s.getValue());
                sit.remove();
            }
        }

        if (gameTime >= 5.0) {
            skySunTimer += dt;
            if (skySunTimer >= skySunInterval) {
                skySunTimer = 0;

                int cols = 11;
                int rows = 7;

                int col = (int) (Math.random() * cols);
                int row = (int) (Math.random() * rows);

                double x = col * (double) tile + tile / 2.0;
                double targetY = row * (double) tile + tile / 2.0;
                double y = targetY - 220;

                suns.add(new Sun(x, y, 50, true, targetY));
            }
        }

        //Iterator<Zombie> zit = zombies.iterator();


        Iterator<Zombie> zit = zombies.iterator();
        while (zit.hasNext()) {
            Zombie z = zit.next();
            z.update(dt);

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

            if (gardener != null && z.isAlive()) {
                boolean touching = Math.abs(z.getX() - gardener.getX()) < 30 &&
                        Math.abs(z.getY() - gardener.getY()) < 30;

                if (touching) {
                    gardenerHitTimer += dt;
                    if (gardenerHitTimer >= 0.5) {
                        gardenerHitTimer = 0;
                        gardener.takeDamage(5);
                        if (gardener.isDead()) lose = true;
                    }
                }
            }

            if (z.getX() <= 2 * tile) lose = true;


            if (z.isDead()) {
                zombiesKilled++;
                zit.remove();
            }
        }

        attackHandler.updateProjectiles(this, dt);

        if (spawner != null) {
            spawner.update(dt);
        }

        if (zombies.isEmpty() && spawner != null && spawner.isWaveDone()) {
            wave++;
            if (wave > 4) {
                win = true;
            } else {
                spawner.setWave(wave);
            }
        }
    }

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

    public int getWave() { return wave; }
    public int getZombiesKilled() { return zombiesKilled; }
    public boolean isWin() { return win; }
    public boolean isLose() { return lose; }

    public List<Plant> getPlants() { return plants; }
    public List<Projectile> getProjectiles() { return attackHandler.getProjectilesSnapshot(); }
    public List<Sun> getSuns() { return suns; }
    public List<Zombie> getZombies() {return zombies;}


    public int getSunPoints() { return sunPoints; }
    public void addSunPoints(int v) { if (v > 0) sunPoints += v; }
    public boolean spendSun(int v) {
        if (v <= 0) return true;
        if (sunPoints < v) return false;
        sunPoints -= v;
        return true;
    }

}
