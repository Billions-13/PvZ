package plants_e;

import Tile.Manager;
import until.GameWorld;
import Zombies.Zombie;
import Zombies.ZombieView;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class PlantPanel extends JPanel {

    private final GameWorld world;
    private final Manager tileManager;

    private final List<PlantView> plantViews = new ArrayList<>();
    private final List<ProjectileView> projectileViews = new ArrayList<>();
    private final List<SunView> sunViews = new ArrayList<>();
    private final List<ZombieView> zombieViews = new ArrayList<>();

    private int camX, camY, camSX, camSY;

    private BufferedImage stand;
    private final BufferedImage[] run = new BufferedImage[3];

    private int runIndex;
    private double pixelAcc;

    private double lastGX = Double.NaN;
    private double lastGY = Double.NaN;

    private long movingUntilNs;

    private static final double MOVE_EPS = 0.001;
    private static final long HOLD_MOVING_NS = 120_000_000L;

    public PlantPanel(GameWorld world, Manager tileManager) {
        this.world = world;
        this.tileManager = tileManager;
        setLayout(null);
        setOpaque(true);

        try {
            stand = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/stand.png")));
            run[0] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/run1.png")));
            run[1] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/run2.png")));
            run[2] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/run3.png")));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load player sprites", e);
        }
    }

    public void setCamera(int camX, int camY, int camSX, int camSY) {
        this.camX = camX;
        this.camY = camY;
        this.camSX = camSX;
        this.camSY = camSY;
    }

    public void render() {
        syncPlants();
        syncProjectiles();
        syncSuns();
        syncZombies();

        for (PlantView pv : plantViews) pv.render();
        for (ProjectileView pv : projectileViews) pv.render();
        for (SunView sv : sunViews) sv.render();
        for (ZombieView zv : zombieViews) zv.render();

    }

    private void syncPlants() {
        Iterator<PlantView> it = plantViews.iterator();
        while (it.hasNext()) {
            PlantView pv = it.next();
            if (!pv.getPlant().isAlive()) {
                remove(pv.getLabel());
                it.remove();
            }
        }
        for (Plant p : world.getPlants()) {
            boolean exists = false;
            for (PlantView pv : plantViews) if (pv.getPlant() == p) { exists = true; break; }
            if (!exists) {
                PlantView pv = new PlantView(p);
                plantViews.add(pv);
                add(pv.getLabel());
            }
        }
    }

    private void syncProjectiles() {
        Iterator<ProjectileView> it = projectileViews.iterator();
        while (it.hasNext()) {
            ProjectileView pv = it.next();
            if (!pv.getProjectile().isActive()) {
                remove(pv.getLabel());
                it.remove();
            }
        }
        for (Projectile p : world.getProjectiles()) {
            boolean exists = false;
            for (ProjectileView pv : projectileViews) if (pv.getProjectile() == p) { exists = true; break; }
            if (!exists && p.isActive()) {
                ProjectileView pv = new ProjectileView(p);
                projectileViews.add(pv);
                add(pv.getLabel());
            }
        }
    }

    private void syncSuns() {
        Iterator<SunView> it = sunViews.iterator();
        while (it.hasNext()) {
            SunView sv = it.next();
            if (sv.getSun().isCollected()) {
                remove(sv.getLabel());
                it.remove();
            }
        }
        for (Sun s : world.getSuns()) {
            boolean exists = false;
            for (SunView sv : sunViews) if (sv.getSun() == s) { exists = true; break; }
            if (!exists) {
                SunView sv = new SunView(s);
                sunViews.add(sv);
                add(sv.getLabel());
            }
        }
    }

    private void syncZombies() {
        zombieViews.removeIf(zv -> {
            if (zv.getZombie().isDead()) {
                remove(zv.getLabel());
                return true;
            }
            return false;
        });
        for (Zombie z : world.getZombies()) {
            boolean exists = false;
            for (ZombieView zv : zombieViews) if (zv.getZombie() == z) { exists = true; break; }
            if (!exists) {
                ZombieView zv = new ZombieView(z);
                zombieViews.add(zv);
                add(zv.getLabel());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (tileManager != null) tileManager.draw(g2, camX, camY, camSX, camSY);

        if (world == null || world.getGardener() == null) return;

        double gx = world.getGardener().getX();
        double gy = world.getGardener().getY();

        int sx = (int) Math.round(gx - camX + camSX);
        int sy = (int) Math.round(gy - camY + camSY);

        long now = System.nanoTime();

        if (!Double.isNaN(lastGX)) {
            double dx = gx - lastGX;
            double dy = gy - lastGY;
            double dist = Math.abs(dx) + Math.abs(dy);

            if (dist > MOVE_EPS) {
                movingUntilNs = now + HOLD_MOVING_NS;
                pixelAcc += dist;

                while (pixelAcc >= 20.0) {
                    pixelAcc -= 20.0;
                    runIndex = (runIndex + 1) % 3;
                }
            }
        }

        boolean moving = now < movingUntilNs;

        BufferedImage img;
        if (moving) {
            img = run[runIndex];
        } else {
            pixelAcc = 0;
            runIndex = 0;
            img = stand;
        }

        lastGX = gx;
        lastGY = gy;

        int w = 75;
        int h = 75;

        g2.drawImage(img, sx - w / 2, sy - h / 2, w, h, null);
    }
}
