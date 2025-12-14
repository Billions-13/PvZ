package until;

import Zombies.Zombie;
import entity.Gardener;

public class ZombieGardenerCollision {

    public static boolean isColliding(Zombie z, Gardener g) {
        if (!z.isAlive() || g.isDead()) return false;

        double dx = Math.abs(z.getX() - g.getX());
        double dy = Math.abs(z.getY() - g.getY());

        return dx < 30 && dy < 30;
    }
}
