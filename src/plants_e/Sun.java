package plants_e;
import entity.*;

public class Sun {

    private double x, y;
    private int value;
    private boolean collected = false;
    private boolean fromSky;      // true: rơi random, false: từ Sunflower
    private double fallSpeed = 80; // pixel/second
    private double targetY;       // y dừng lại (nếu từ trời)

    public Sun(double x, double y, int value, boolean fromSky, double targetY) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.fromSky = fromSky;
        this.targetY = targetY;
    }

    public void update(double deltaTime) {
        if (fromSky && y < targetY) {
            y += fallSpeed * deltaTime;
            if (y > targetY) y = targetY;
        }
    }

    public boolean isCollected() { return collected; }

    public void collect(Player player) {
        if (collected || player == null) return;
        collected = true;
        player.addSun(value);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
