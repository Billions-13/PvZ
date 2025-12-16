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
    private double baseY;
    private double age;


    public Sun(double x, double y, int value, boolean fromSky, double targetY) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.fromSky = fromSky;
        this.targetY = targetY;
        this.collectTargetY = y;
        this.baseY = y;
        this.age = 0;

    }

    public void startCollect() {
        if (collected || collecting) return;
        collecting = true;
        collectTargetY = y - 140;
    }

    public void update(double deltaTime) {

        if (deltaTime <= 0) return;
        age += deltaTime;

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

            baseY = y;
            return;
        }


        if (!fromSky) {
            if (age < 1.5) {
                y = baseY + Math.sin(age * 10.0) * 5.0;
            } else {
                y = baseY;
            }
        }
    }

    public boolean isCollected() { return collected; }
    public boolean isCollecting() { return collecting; }
    public int getValue() { return value; }

    public double getX() { return x; }
    public double getY() { return y; }
}
