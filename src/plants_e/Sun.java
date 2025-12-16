package plants_e;

public class Sun {

    private double x, y;
    private final int value;

    private boolean collected = false;

    private final boolean fromSky;
    private double fallSpeed = 80;
    private final double targetY;

    private boolean collecting = false;
    private double collectSpeed = 260;
    private double collectTargetY;

    public Sun(double x, double y, int value, boolean fromSky, double targetY) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.fromSky = fromSky;
        this.targetY = targetY;
        this.collectTargetY = y;
    }

    public void startCollect() {
        if (collected || collecting) return;
        collecting = true;
        collectTargetY = y - 140;
    }

    public void update(double deltaTime) {
        if (collected || deltaTime <= 0) return;

        if (collecting) {
            y -= collectSpeed * deltaTime;
            if (y <= collectTargetY) {
                collected = true;
            }
            return;
        }

        if (fromSky && y < targetY) {
            y += fallSpeed * deltaTime;
            if (y > targetY) y = targetY;
        }
    }

    public boolean isCollected() { return collected; }
    public boolean isCollecting() { return collecting; }
    public int getValue() { return value; }

    public double getX() { return x; }
    public double getY() { return y; }
}
