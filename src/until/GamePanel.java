package until;

import Tile.Manager;
import plants_e.*;
import entity.Gardener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {

    private Thread thread;
    private final int fps = 60;
    private PlantType pickedType = null;

    private boolean digMode = false;
    private Cursor digCursor;
    private final Cursor normalCursor = Cursor.getDefaultCursor();

    public final int block = 40;
    public final int maxcol = 45;
    public final int maxrow = 30;
    public final int mwidth = 900;
    public final int mheight = 600;

    private final Keybind key = new Keybind();

    private final GameAttackHandler attackHandler = new GameAttackHandler();
    private final GameWorld world = new GameWorld(attackHandler);
    private final ZombieSpawner spawner = new ZombieSpawner(world);

    private final PlantFactory plantFactory = new PlantFactory(attackHandler);
    private final PlantInputHandler plantInput = new PlantInputHandler(plantFactory, world);

    private final Gardener gardener = new Gardener(400, 300);

    private final Manager tileManager = new Manager(maxcol, maxrow, block, mwidth, mheight);
    private final PlantPanel plantPanel = new PlantPanel(world, tileManager);
    private final HudLayer hud = new HudLayer(world, 900, 40);

    private final GardenerLayer gardenerLayer = new GardenerLayer(gardener, 900, 560);

    private final PlantSelectBar selectBar = new PlantSelectBar();

    private int camX, camY, camSX, camSY;

    private Double targetX = null;
    private Double targetY = null;
    private static final double CLICK_MOVE_SPEED = 220.0;

    public GamePanel() {
        setLayout(null);
        setFocusable(true);
        addKeyListener(key);

        world.setSpawner(spawner);
        world.setGardener(gardener);

        hud.setBounds(0, 0, 900, 40);
        plantPanel.setBounds(0, 40, 900, 560);

        add(hud);
        add(plantPanel);

        gardenerLayer.setBounds(0, 40, 900, 560);
        add(gardenerLayer);

        selectBar.setBounds(10, 40 + 10, PlantSelectBar.W, PlantSelectBar.H);
        add(selectBar);
        setComponentZOrder(selectBar, 0);

        digCursor = buildDigCursor();

        selectBar.setListener(type -> {
            if (type == null) {
                pickedType = null;
                digMode = true;
                plantPanel.setCursor(digCursor);
            } else {
                digMode = false;
                pickedType = type;
                plantPanel.setCursor(normalCursor);
            }
        });

        plantPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                int gx = e.getX() + plantPanel.getX();
                int gy = e.getY() + plantPanel.getY();
                if (selectBar.getBounds().contains(gx, gy)) return;

                int tile = block * 2;
                int col = Math.max(0, Math.min(maxcol - 1, plantPanel.snapColFromMouse(e.getX())));
                int row = Math.max(0, Math.min(maxrow - 1, plantPanel.snapRowFromMouse(e.getY())));
                double px = col * tile;
                double py = row * tile;

                if (digMode) {
                    world.removePlantAt(row, col);
                    digMode = false;
                    plantPanel.setCursor(normalCursor);
                    selectBar.clearSelection();
                    return;
                }

                if (pickedType != null) {
                    plantInput.plant(pickedType, row, col, px, py);
                    pickedType = null;
                    selectBar.clearSelection();
                    return;
                }

                targetX = col * tile + tile / 2.0;
                targetY = row * tile + tile / 2.0;
            }
        });
    }

    private Cursor buildDigCursor() {
        try {
            BufferedImage raw = ImageIO.read(Objects.requireNonNull(
                    getClass().getResource("/resources/img_P/dig.png")
            ));
            Image img = raw.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            BufferedImage cur = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = cur.createGraphics();
            g2.drawImage(img, 0, 0, null);
            g2.dispose();
            return Toolkit.getDefaultToolkit().createCustomCursor(cur, new Point(0, 0), "dig");
        } catch (Exception ex) {
            return normalCursor;
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        double acc = 0;
        double step = 1.0 / fps;

        while (thread != null) {
            long now = System.nanoTime();
            acc += (now - last) / 1e9;
            last = now;

            while (acc >= step) {
                update(step);
                acc -= step;
            }

            hud.repaint();
            plantPanel.render();
            gardenerLayer.repaint();
        }
    }

    private void update(double dt) {
        boolean keyboardMoving = key.upPressed || key.downPressed || key.leftPressed || key.rightPressed;
        if (keyboardMoving) {
            targetX = null;
            targetY = null;
        }

        if (targetX != null && targetY != null && !keyboardMoving) {
            double dx = targetX - gardener.getX();
            double dy = targetY - gardener.getY();
            double dist = Math.hypot(dx, dy);

            if (dist < 2.0) {
                targetX = null;
                targetY = null;
            } else {
                double vx = dx / dist * CLICK_MOVE_SPEED;
                double vy = dy / dist * CLICK_MOVE_SPEED;

                double nx = gardener.getX() + vx * dt;
                double ny = gardener.getY() + vy * dt;

                int worldW = maxcol * block * 2;
                int worldH = maxrow * block * 2;

                nx = Math.max(0, Math.min(nx, worldW));
                ny = Math.max(0, Math.min(ny, worldH));

                gardener.setX(nx);
                gardener.setY(ny);
            }
        } else {
            gardener.update(key, dt, 0, 0, maxcol * block * 2, maxrow * block * 2);
        }

        camSX = mwidth / 2;
        camSY = (mheight - 40) / 2;

        int worldW = maxcol * block * 2;
        int worldH = maxrow * block * 2;

        camX = (int) gardener.getX();
        camY = (int) gardener.getY();

        camX = Math.max(camSX, Math.min(camX, worldW - (mwidth - camSX)));
        camY = Math.max(camSY, Math.min(camY, worldH - ((mheight - 40) - camSY)));

        plantPanel.setCamera(camX, camY, camSX, camSY);

        world.update(dt);

        if (key.fPressed) {
            plantInput.plant(key.selectedPlant, 2, 2, 300, 300);
            key.fPressed = false;
        }

        hud.setGardenerHp(gardener.getHp());
    }
}
