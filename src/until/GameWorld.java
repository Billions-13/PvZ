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
    private final ChomperChewController chomperChew = new ChomperChewController(4.5);


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


    public boolean hasPlantAt(int row, int col) {
        for (Plant p : plants) {
            if (p != null && p.isAlive() && p.getRow() == row && p.getCol() == col) return true;
        }
        return false;
    }


    public int removePlantAt(int row, int col) {
        Iterator<Plant> it = plants.iterator();
        while (it.hasNext()) {
            Plant p = it.next();
            if (p.isAlive() && p.getRow() == row && p.getCol() == col) {
                int refund = p.getSunCost();
                p.onRemoved();
                it.remove();
                addSunPoints(refund);
                return refund;
            }
        }
        return 0;
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

        Iterator<Plant> pit = plants.iterator();
        while (pit.hasNext()) {
            Plant p = pit.next();

            if (!p.isAlive()) {
                chomperChew.clear(p);
                p.onRemoved();
                pit.remove();
                continue;
            }


            Zombie target = findClosestZombieAhead(p.getRow(), p.getPositionX());
            boolean hasTarget = target != null;

            p.setTargeting(hasTarget);

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

            if (p.getPlantType() == PlantType.CHOMPER) {

                p.setAttackEnabled(false);

                if (chomperChew.isChewing(p, now)) {
                    p.setSpritePath("chomper-eating-zombie.gif");
                } else {
                    Zombie bite = findClosestZombieAhead(p.getRow(), p.getPositionX());
                    if (bite != null && Math.abs(bite.getX() - p.getPositionX()) < 70) {
                        p.setSpritePath("chomper-eating-zombie.gif");
                        bite.takeDamage(bite.getHp());
                        chomperChew.startChew(p, now);
                    } else {
                        boolean anySameRow = false;
                        for (Zombie z : zombies) {
                            if (z.isAlive() && z.getRow() == p.getRow()) {
                                anySameRow = true;
                                break;
                            }
                        }
                        if (!anySameRow) p.setSpritePath("CHOMPER.gif");
                        else p.setSpritePath("CHOMPER.gif");
                    }
                }
            }


            p.update(now);

            if (p instanceof Sunflower s) {
                SunProductionBehavior b = s.getSunProductionBehavior();
                if (b != null) b.updateSunProduction(s, now, this);
            }
            if (!p.isAlive()) {
                chomperChew.clear(p);
                p.onRemoved();
                pit.remove();
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

                int col = 1 + (int) (Math.random() * (cols - 1));
                int row = 1 + (int) (Math.random() * (rows - 1));


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
// Ghìm zombie đúng hàng lưới (center tile) để tránh lệch row khi render.
            Plant victim = (z.isAlive() ? findBiteTarget(z) : null);

            if (victim != null) {
                z.setState(ZombieState.ATTACK);
                if (z.canAttackNow()) {
                    victim.takeDamage(z.getDamage());
                }
            } else if (z.isAlive() && z.getState() == ZombieState.ATTACK) {
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

            if (z.getX() <= 0) lose = true;


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

    private Zombie findClosestZombieAhead(int row, double plantX) {
        Zombie best = null;
        double bestDx = Double.MAX_VALUE;

        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            if (z.getRow() != row) continue;
            if (z.getX() < plantX) continue;

            double dx = z.getX() - plantX;
            if (dx < bestDx) {
                bestDx = dx;
                best = z;
            }
        }
        return best;
    }

    private Plant findBiteTarget(Zombie z) {
        Plant best = null;
        double bestDx = Double.MAX_VALUE;

        for (Plant p : plants) {
            if (p == null || !p.isAlive()) continue;
            if (p.getRow() != z.getRow()) continue;

            double dx = Math.abs(z.getX() - p.getPositionX());
            if (dx <= 20 && dx < bestDx) {
                bestDx = dx;
                best = p;
            }
        }
        return best;
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