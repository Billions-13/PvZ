package until;

import Zombies.*;

public class ZombieSpawner {

    private final GameWorld world;

    private double timer;
    private int spawned;
    private int total;
    private double interval;

    private static final int TILE = 80;
    private static final int GRID_ROWS = 7; // 0..6
    private static final int SPAWN_SAFE_COL = 3; // tránh thua ngay vì GameWorld lose khi x <= 2*TILE
    private static final int GRID_COLS = 12; // 0..11 là trong lưới

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


        // ===== GRID CONFIG =====
        final int GRID_ROWS = 7;   // 0..6
        final int GRID_COLS = 12;  // 0..11

// ===== ROW: chỉ spawn trong 7 hàng lưới =====
        int row = (int) (Math.random() * GRID_ROWS); // 0..6
        double y = row * (double) TILE;

// ===== X: spawn sát MÉP PHẢI của lưới =====
// lưới kết thúc ở cột 11 → zombie đứng ngay ngoài cột đó
        double x = GRID_COLS * (double) TILE + 4;

// ===== TRỘN ĐỦ 4 LOẠI ZOMBIE =====
        ZombieType type = switch (world.getWave()) {
            case 1 -> (Math.random() < 0.85)
                    ? ZombieType.NORMAL
                    : ZombieType.FLAG;

            case 2 -> {
                double r = Math.random();
                yield (r < 0.55) ? ZombieType.NORMAL
                        : (r < 0.90) ? ZombieType.CONE_HEAD
                        : ZombieType.FLAG;
            }

            case 3 -> {
                double r = Math.random();
                yield (r < 0.45) ? ZombieType.CONE_HEAD
                        : (r < 0.85) ? ZombieType.BUCKET_HEAD
                        : ZombieType.FLAG;
            }

            default -> {
                double r = Math.random();
                yield (r < 0.35) ? ZombieType.NORMAL
                        : (r < 0.60) ? ZombieType.CONE_HEAD
                        : (r < 0.90) ? ZombieType.BUCKET_HEAD
                        : ZombieType.FLAG;
            }
        };


        Zombie z = ZombieFactory.createZombie(type, row, x, y);
        world.addZombie(z);
        spawned++;
    }
}
