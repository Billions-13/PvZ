package until;

import entity.Gardener;

public final class MovementController {

    private Double targetX;
    private Double targetY;

    private static final double SPEED = 220.0;
    private static final double ARRIVE_EPS = 2.0;

    public void setTarget(double x, double y) {
        targetX = x;
        targetY = y;
    }

    public void clearTarget() {
        targetX = null;
        targetY = null;
    }

    public boolean hasTarget() {
        return targetX != null && targetY != null;
    }

    public boolean updateClickMove(Gardener g, double dt, double minX, double minY, double maxX, double maxY) {
        if (!hasTarget() || dt <= 0) return false;

        double dx = targetX - g.getX();
        double dy = targetY - g.getY();
        double dist = Math.hypot(dx, dy);

        if (dist <= ARRIVE_EPS) {
            clearTarget();
            return true;
        }

        double vx = dx / dist * SPEED;
        double vy = dy / dist * SPEED;

        double nx = g.getX() + vx * dt;
        double ny = g.getY() + vy * dt;

        nx = Math.max(minX, Math.min(nx, maxX));
        ny = Math.max(minY, Math.min(ny, maxY));

        g.setX(nx);
        g.setY(ny);
        return false;
    }

    public boolean isArrived(Gardener g, double x, double y) {
        return Math.hypot(x - g.getX(), y - g.getY()) <= ARRIVE_EPS;
    }
}
