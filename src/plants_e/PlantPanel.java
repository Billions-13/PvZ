// plants_e/PlantPanel.java
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

    public enum PlayerAnim { AUTO, COLLECT, PLANTING }

    private final GameWorld world;
    private final Manager tileManager;

    private final List<PlantView> plantViews = new ArrayList<>();
    private final List<ProjectileView> projectileViews = new ArrayList<>();
    private final List<SunView> sunViews = new ArrayList<>();
    private final List<ZombieView> zombieViews = new ArrayList<>();

    private int camX, camY, camSX, camSY;

    private BufferedImage stand;
    private final BufferedImage[] run = new BufferedImage[3];

    private BufferedImage planting;
    private final BufferedImage[] collect = new BufferedImage[3];

    private int runIndex;
    private int collectIndex;
    private double pixelAcc;

    private double lastGX = Double.NaN;
    private double lastGY = Double.NaN;

    private long movingUntilNs;

    private static final double MOVE_EPS = 0.001;
    private static final long HOLD_MOVING_NS = 120_000_000L;

    private static final int TILE = 80;

    private static final boolean DRAW_GRID = true;
    private static final Color GRID_COLOR = new Color(139, 90, 43, 170);
    private static final Stroke GRID_STROKE = new BasicStroke(2.2f);

    private volatile PlayerAnim forcedAnim = PlayerAnim.AUTO;
    private volatile long forcedUntilNs = 0;

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

            collect[0] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect1.png")));
            collect[1] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect2.png")));
            collect[2] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect3.png")));

            planting = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/planting.png")));
        } catch (Exception e) {
            throw new RuntimeException("Cannot load player sprites", e);
        }
    }

    public void setForcedAnim(PlayerAnim anim, long untilNs) {
        forcedAnim = anim == null ? PlayerAnim.AUTO : anim;
        forcedUntilNs = untilNs;
    }

    public void setCamera(int camX, int camY, int camSX, int camSY) {
        this.camX = camX;
        this.camY = camY;
        this.camSX = camSX;
        this.camSY = camSY;
    }

    public int snapColFromMouse(int mx) {
        int worldX = camX - camSX + mx;
        if (worldX < 0) return 0;
        return worldX / TILE;
    }

    public int snapRowFromMouse(int my) {
        int worldY = camY - camSY + my;
        if (worldY < 0) return 0;
        return worldY / TILE;
    }

    public Sun pickSunAt(int x, int y) {
        for (int i = sunViews.size() - 1; i >= 0; i--) {
            JLabel c = sunViews.get(i).getLabel();
            if (c.getBounds().contains(x, y)) return sunViews.get(i).getSun();
        }
        return null;
    }


    public void render() {
        syncPlants();
        syncProjectiles();
        syncSuns();
        syncZombies();

        for (PlantView pv : plantViews) pv.render();
        for (ProjectileView pv : projectileViews) pv.render(camX, camY, camSX, camSY);
        for (SunView sv : sunViews) sv.render(camX, camY, camSX, camSY);
        for (ZombieView zv : zombieViews) zv.render(camX, camY, camSX, camSY);
        applyCameraToAllViews();
    }


    private void syncPlants() {
        Set<Plant> aliveInWorld = new HashSet<>(world.getPlants());

        boolean changed = false;

        Iterator<PlantView> it = plantViews.iterator();
        while (it.hasNext()) {
            PlantView pv = it.next();
            Plant p = pv.getPlant();
            if (!p.isAlive() || !aliveInWorld.contains(p)) {
                remove(pv.getLabel());
                it.remove();
                changed = true;
            }
        }

        for (Plant p : world.getPlants()) {
            boolean exists = false;
            for (PlantView pv : plantViews) if (pv.getPlant() == p) { exists = true; break; }
            if (!exists) {
                PlantView pv = new PlantView(p);
                plantViews.add(pv);
                add(pv.getLabel());
                changed = true;
            }
        }

        if (changed) {
            revalidate();
            repaint();
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

    private void applyCameraToAllViews() {
        for (PlantView pv : plantViews) {
            JComponent c = pv.getLabel();
            Plant p = pv.getPlant();

            int w = c.getWidth();
            int h = c.getHeight();

            double wx = p.getCol() * (double) TILE + (TILE - w) / 2.0;
            double wy = p.getRow() * (double) TILE + (TILE - h) / 2.0;

            placeTopLeftWorld(c, wx, wy);
        }

        for (ProjectileView pv : projectileViews) {
            JLabel c = pv.getLabel();
            Projectile p = pv.getProjectile();
            placeTopLeftWorld(c, p.getX(), p.getY());
        }

        for (SunView sv : sunViews) {
            JLabel c = sv.getLabel();
            Sun s = sv.getSun();
            placeTopLeftWorld(c, s.getX(), s.getY());
        }

        for (ZombieView zv : zombieViews) {
            JLabel c = zv.getLabel();
            Zombie z = zv.getZombie();
            placeTopLeftWorld(c, z.getX(), z.getY());
        }
    }

    private void placeTopLeftWorld(JComponent c, double worldX, double worldY) {
        int sx = (int) Math.round(worldX - camX + camSX);
        int sy = (int) Math.round(worldY - camY + camSY);
        c.setLocation(sx, sy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (tileManager != null) tileManager.draw(g2, camX, camY, camSX, camSY);
        if (DRAW_GRID) drawGrid(g2);

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
                    collectIndex = (collectIndex + 1) % 3;
                }
            }
        }

        boolean moving = now < movingUntilNs;

        PlayerAnim anim = forcedAnim;
        if (anim != PlayerAnim.AUTO && forcedUntilNs > 0 && now >= forcedUntilNs) {
            forcedAnim = PlayerAnim.AUTO;
            forcedUntilNs = 0;
            anim = PlayerAnim.AUTO;
        }

        BufferedImage img;
        if (anim == PlayerAnim.PLANTING) {
            img = planting;
        } else if (anim == PlayerAnim.COLLECT) {
            img = collect[collectIndex];
        } else {
            img = moving ? run[runIndex] : stand;
            if (!moving) {
                pixelAcc = 0;
                runIndex = 0;
                collectIndex = 0;
            }
        }

        lastGX = gx;
        lastGY = gy;

        int w = 75;
        int h = 75;

        g2.drawImage(img, sx - w / 2, sy - h / 2, w, h, null);
    }

    private void drawGrid(Graphics2D g2) {
        Stroke oldS = g2.getStroke();
        Color oldC = g2.getColor();

        g2.setStroke(GRID_STROKE);
        g2.setColor(GRID_COLOR);

        int cols = 11;
        int rows = 7;

        int leftWorld = 0;
        int topWorld = 0;

        for (int c = 0; c <= cols; c++) {
            int wx = leftWorld + c * TILE;
            int sx = wx - camX + camSX;
            g2.drawLine(sx, -camY + camSY, sx, rows * TILE - camY + camSY);
        }

        for (int r = 0; r <= rows; r++) {
            int wy = topWorld + r * TILE;
            int sy = wy - camY + camSY;
            g2.drawLine(-camX + camSX, sy, cols * TILE - camX + camSX, sy);
        }

        g2.setColor(oldC);
        g2.setStroke(oldS);
    }
}
