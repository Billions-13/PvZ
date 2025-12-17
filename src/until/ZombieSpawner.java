package until;

import Zombies.*;

public class ZombieSpawner {

    private final GameWorld world;

    private double timer;
    private int spawned;
    private int total;
    private double interval;

    public ZombieSpawner(GameWorld world) {
        this.world = world;
        setWave(1);
    }

    public void setWave(int wave) {
        spawned = 0;
        timer = 0;

        total = switch (wave) {
            case 1 -> 6;
            case 2 -> 10;
            case 3 -> 14;
            case 4 -> 18;
            default -> 0;
        };

        interval = switch (wave) {
            case 1 -> 2.2;
            case 2 -> 2.0;
            case 3 -> 1.8;
            case 4 -> 1.6;
            default -> 999;
        };
    }

    public boolean isWaveDone() {
        return spawned >= total;
    }

    public void update(double dt) {
        if (spawned >= total) return;

        timer += dt;
        if (timer < interval) return;
        timer = 0;

        int gridRows = 7;
        int row = 1 + (int)(Math.random() * (gridRows - 1));  // chỉ 1..6, không bao giờ 0
        double y = row * 80;       // hoặc TILE nếu bạn có hằng TILE


        double x = 14 * 80.0;

        ZombieType type = switch (world.getWave()) {
            case 1 -> ZombieType.NORMAL;
            case 2 -> (spawned % 2 == 0) ? ZombieType.NORMAL : ZombieType.CONE_HEAD;
            case 3 -> (spawned % 2 == 0) ? ZombieType.CONE_HEAD : ZombieType.BUCKET_HEAD;
            default -> (spawned % 4 == 0) ? ZombieType.FLAG : ZombieType.BUCKET_HEAD;
        };

        Zombie z = ZombieFactory.createZombie(type, row, x, y);
        world.addZombie(z);
        spawned++;
    }
}
