package until;

import Tile.Manager;
import plants_e.*;
import entity.Gardener;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {

    private Thread thread;
    private final int fps = 60;
    private PlantType pickedType = null;

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

    // ===== [NEW] pending placement: click icon -> click tile -> run -> planting 1s -> spawn plant -> reset =====
    private static final class PendingPlant {
        final PlantType type;
        final int row;
        final int col;
        final double px;
        final double py;
        boolean plantingStarted;
        double plantingLeft;

        PendingPlant(PlantType type, int row, int col, double px, double py) {
            this.type = type;
            this.row = row;
            this.col = col;
            this.px = px;
            this.py = py;
        }
    }

    private PendingPlant pending = null;

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

        selectBar.setListener(type -> pickedType = type);

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

                // ===== [NEW] nếu đã chọn plant -> tạo pending + bắt player chạy tới ô bằng collect anim =====
                if (pickedType != null) {
                    pending = new PendingPlant(pickedType, row, col, px, py);

                    targetX = col * tile + tile / 2.0;
                    targetY = row * tile + tile / 2.0;

                    plantPanel.setPlayerAnimMode(PlantPanel.AnimMode.COLLECT);
                    return;
                }

                // ===== click move bình thường =====
                targetX = col * tile + tile / 2.0;
                targetY = row * tile + tile / 2.0;
            }
        });
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

            // nếu đang pending mà bạn bấm WASD thì coi như hủy chạy theo click (không spawn cây)
            if (pending != null && !pending.plantingStarted) {
                pending = null;
                plantPanel.setPlayerAnimMode(PlantPanel.AnimMode.AUTO);
            }
        }

        // ===== [NEW] đang planting thì khóa movement 1s =====
        if (pending != null && pending.plantingStarted) {
            pending.plantingLeft -= dt;
            if (pending.plantingLeft <= 0) {
                plantInput.plant(pending.type, pending.row, pending.col, pending.px, pending.py);

                pending = null;
                pickedType = null;

                plantPanel.setPlayerAnimMode(PlantPanel.AnimMode.AUTO);
            }
        } else {
            // movement bình thường (click-move hoặc WASD)
            boolean clickMoving = targetX != null && targetY != null && !keyboardMoving;

            if (clickMoving) {
                double dx = targetX - gardener.getX();
                double dy = targetY - gardener.getY();
                double dist = Math.hypot(dx, dy);

                if (dist < 2.0) {
                    targetX = null;
                    targetY = null;

                    // ===== [NEW] nếu tới nơi và đang pending -> bắt đầu planting 1s =====
                    if (pending != null && !pending.plantingStarted) {
                        pending.plantingStarted = true;
                        pending.plantingLeft = 1.0;
                        plantPanel.setPlayerAnimMode(PlantPanel.AnimMode.PLANTING);
                    }
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
