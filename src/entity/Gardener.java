package entity;

import until.Keybind;

public class Gardener {
    private double x;
    private double y;
    private int hp = 100;

    public Gardener(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(Keybind key, double dt, double minX, double minY, double maxX, double maxY) {
        double sp = 260.0;
        double dx = 0;
        double dy = 0;

        if (key.upPressed) dy -= sp * dt;
        if (key.downPressed) dy += sp * dt;
        if (key.leftPressed) dx -= sp * dt;
        if (key.rightPressed) dx += sp * dt;

        x = clamp(x + dx, minX, maxX);
        y = clamp(y + dy, minY, maxY);
    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    private double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    public void takeDamage(int dmg) {
        if (dmg <= 0) return;
        hp = Math.max(0, hp - dmg);
    }




    public boolean isDead() { return hp == 0; }
    public int getHp() { return hp; }
    public double getX() { return x; }
    public double getY() { return y; }
}
