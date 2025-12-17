package until;

import Tile.Manager;
import entity.Gardener;
import plants_e.GameAttackHandler;
import plants_e.PlantFactory;
import plants_e.PlantPanel;

import javax.swing.*;

public class GamePanel extends JPanel {

    private final int fps = 60;
    private final CursorManager cursor;

    public final int block = 40;
    public final int maxcol = 45;
    public final int maxrow = 30;
    public final int mwidth = 900;
    public final int mheight = 600;

    private final Keybind key = new Keybind();
    private final JLayeredPane layers = new JLayeredPane();

    private final GameAttackHandler attackHandler = new GameAttackHandler();
    private final GameWorld world = new GameWorld(attackHandler);
    private final ZombieSpawner spawner = new ZombieSpawner(world);

    private final PlantFactory plantFactory = new PlantFactory(attackHandler);

    private final Gardener gardener = new Gardener(400, 300);

    private final Manager tileManager = new Manager(maxcol, maxrow, block, mwidth, mheight);
    private final PlantPanel plantPanel = new PlantPanel(world, tileManager);

    private final HudLayer hud = new HudLayer(world, 900, 40);
    private final GardenerLayer gardenerLayer = new GardenerLayer(gardener, 900, 560);
    private final PlantSelectBar selectBar = new PlantSelectBar();

    private final MovementController movement = new MovementController();
    private final PlantingController planting = new PlantingController();

    private final CameraController camera = new CameraController(
            mwidth,
            mheight,
            block,
            maxcol * block * 2,
            maxrow * block * 2
    );

    private final InputController input;
    private final GameLoop loop;

    public GamePanel() {
        setLayout(null);

        cursor = new CursorManager(this);

        setFocusable(true);
        addKeyListener(key);

        world.setSpawner(spawner);
        world.setGardener(gardener);

        layers.setBounds(0, 0, 900, 600);
        layers.setLayout(null);
        add(layers);
//từ đoạn này
        hud.setBounds(0, 0, 900, 40);
        plantPanel.setBounds(0, 40, 900, 560);
        gardenerLayer.setBounds(0, 40, 900, 560);
        selectBar.setBounds(10, 50, PlantSelectBar.W, PlantSelectBar.H);

        layers.add(plantPanel, Integer.valueOf(0));
        layers.add(gardenerLayer, Integer.valueOf(1));
        layers.add(selectBar, Integer.valueOf(2));
        layers.add(hud, Integer.valueOf(3));

//đến đoạn này là ổn định tình trạng bar
        input = new InputController(
                this,
                plantPanel,
                selectBar,
                hud,
                world,
                movement,
                planting,
                cursor,
                maxcol,
                maxrow,
                block
        );
        input.bind();

        loop = new GameLoop(fps, () -> {
            double dt = 1.0 / fps;
            tick(dt);
            hud.repaint();
            plantPanel.render();
            gardenerLayer.repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public void start() {
        loop.start();
    }

    private void tick(double dt) {
        selectBar.tick(dt);

        int worldW = maxcol * block * 2;
        int worldH = maxrow * block * 2;

        long nowNs = System.nanoTime();

        if (planting.hasPending()) {
            planting.update(
                    dt, nowNs,
                    gardener, movement,
                    plantPanel, selectBar,
                    world, plantFactory, hud,
                    0, 0, worldW, worldH
            );
        } else {
            gardener.update(key, dt, 0, 0, worldW, worldH);
            movement.updateClickMove(gardener, dt, 0, 0, worldW, worldH);
        }

        camera.follow(gardener);
        plantPanel.setCamera(camera.camX, camera.camY, camera.camSX, camera.camSY);

        world.update(dt);
        hud.setGardenerHp(gardener.getHp());
    }
}
