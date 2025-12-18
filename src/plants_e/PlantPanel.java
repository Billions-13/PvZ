package plants_e;

import Tile.Manager;
import Zombies.ZombieView;
import until.GameWorld;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;


import Zombies.Zombie;
import Zombies.ZombieState;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;


public class PlantPanel extends JPanel {

    public enum PlayerAnim { AUTO, COLLECT, PLANTING }

    private static final int TILE = 80;

    private final GameWorld world;
    private final Manager tileManager;
    private final WorldSync sync;

    private int camX, camY, camSX, camSY;

    // ===== GRID CONFIG (GAMEPLAY GRID) =====
    private static final int GRID_ROWS = 7;
    private static final int GRID_COLS = 12;


    private BufferedImage stand;
    private final BufferedImage[] run = new BufferedImage[3];
    private final BufferedImage[] collect = new BufferedImage[3];
    private BufferedImage planting;

    private int runIndex;
    private int collectIndex;
    private double animAcc;

    private volatile PlayerAnim forcedAnim = PlayerAnim.AUTO;
    private volatile long forcedUntilNs = 0;

    private double lastGX = Double.NaN;
    private double lastGY = Double.NaN;
    private long movingUntilNs;

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

            collect[0] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect1.png")));
            collect[1] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect2.png")));
            collect[2] = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/collect3.png")));

            planting = ImageIO.read(Objects.requireNonNull(getClass().getResource("/resources/img_W/planting.png")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sync = new WorldSync(this, world);
    }

    public void setCamera(int camX, int camY, int camSX, int camSY) {
        this.camX = camX;
        this.camY = camY;
        this.camSX = camSX;
        this.camSY = camSY;
    }

    public int getCamX() { return camX; }
    public int getCamY() { return camY; }

    public void setForcedAnim(PlayerAnim anim, long untilNs) {
        forcedAnim = anim == null ? PlayerAnim.AUTO : anim;
        forcedUntilNs = untilNs;
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
        return sync.pickSunAt(x, y);
    }

    public void render() {
        sync.syncAll();
        sync.renderAll(camX, camY, camSX, camSY);
        applyCameraToAllViews();
        repaint();
    }

    private final Map<String, Image> zombieImgCache = new HashMap<>();
    private Image zombieImg(String path) {
        return zombieImgCache.computeIfAbsent(path, p -> {
            var url = getClass().getResource(p);
            if (url == null) return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            return new ImageIcon(url).getImage();
        });
    }


    // ===== DRAW GAMEPLAY GRID (7x13) =====
    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(150, 70, 60)); // nâu
        g2.setStroke(new BasicStroke(1.25f));   // nét dày

        // Hàng ngang
        for (int r = 0; r <= GRID_ROWS; r++) {
            int y = r * TILE - camY + camSY;
            g2.drawLine(
                    -camX + camSX,
                    y,
                    GRID_COLS * TILE - camX + camSX,
                    y
            );
        }

        // Cột dọc
        for (int c = 0; c <= GRID_COLS; c++) {
            int x = c * TILE - camX + camSX;
            g2.drawLine(
                    x,
                    -camY + camSY,
                    x,
                    GRID_ROWS * TILE - camY + camSY
            );
        }
    }


    private void applyCameraToAllViews() {
        for (PlantView pv : sync.getPlantViews()) {
            JComponent c = pv.getLabel();
            Plant p = pv.getPlant();

            int w = c.getWidth();
            int h = c.getHeight();

            double wx = p.getCol() * (double) TILE + (TILE - w) / 2.0;
            double wy = p.getRow() * (double) TILE + (TILE - h) / 2.0;

            place(c, wx, wy);
        }

        for (ProjectileView pv : sync.getProjectileViews()) place(pv.getLabel(), pv.getProjectile().getX(), pv.getProjectile().getY());
        for (SunView sv : sync.getSunViews()) place(sv.getLabel(), sv.getSun().getX(), sv.getSun().getY());
        for (ZombieView zv : sync.getZombieViews()) place(zv.getLabel(),
                zv.getZombie().getX() * 2,
                zv.getZombie().getY() * 2);
    }

    private void place(JComponent c, double worldX, double worldY) {
        int sx = (int) Math.round(worldX - camX + camSX);
        int sy = (int) Math.round(worldY - camY + camSY);
        c.setLocation(sx, sy);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (tileManager != null) {
            tileManager.draw(g2, camX, camY, camSX, camSY);
            drawGrid(g2);

            for (Zombie z : new java.util.ArrayList<>(world.getZombies())) {
                if (z == null) continue;

                String sprite = switch (z.getState()) {
                    case ATTACK -> z.getAttackSprite();
                    case HEAD -> z.getHeadSprite();
                    case DEAD -> z.getDeadSprite();
                    default -> z.getAdvanceSprite();
                };

                Image imgZ = zombieImg(sprite);

                int zx = (int) Math.round(z.getX() - camX + camSX);
                int zy = (int) Math.round(z.getY() - camY + camSY);

                int ZW = 125;
                int ZH = 125;

                g2.drawImage(imgZ, zx - (ZW - 80) / 2, zy - (ZH - 80) / 2, ZW, ZH, this);

            }


        }

        long now = System.nanoTime();

        if (forcedAnim != PlayerAnim.AUTO && forcedUntilNs > 0 && now >= forcedUntilNs) {
            forcedAnim = PlayerAnim.AUTO;
            forcedUntilNs = 0;
        }

        double gx = world.getGardener().getX();
        double gy = world.getGardener().getY();

        if (!Double.isNaN(lastGX)) {
            double dist = Math.abs(gx - lastGX) + Math.abs(gy - lastGY);
            if (dist > 0.001) {
                movingUntilNs = now + HOLD_MOVING_NS;
                animAcc += dist;

                while (animAcc >= 20.0) {
                    animAcc -= 20.0;
                    runIndex = (runIndex + 1) % 3;
                    collectIndex = (collectIndex + 1) % 3;
                }
            }
        }

        boolean moving = now < movingUntilNs;

        BufferedImage img;
        if (forcedAnim == PlayerAnim.PLANTING) img = planting;
        else if (forcedAnim == PlayerAnim.COLLECT) img = collect[collectIndex];
        else img = moving ? run[runIndex] : stand;

        lastGX = gx;
        lastGY = gy;

        int sx = (int) Math.round(gx - camX + camSX);
        int sy = (int) Math.round(gy - camY + camSY);

        g2.drawImage(img, sx - 38, sy - 38, 75, 75, null);
    }
}