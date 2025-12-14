package until;

import Zombies.Zombie;
import plants_e.Plant;

public class ZombiePlantCollision {

    public static boolean isColliding(Zombie z, Plant p) {
        if (!z.isAlive() || !p.isAlive()) return false;
        if (z.getRow() != p.getRow()) return false;

        double dx = Math.abs(z.getX() - p.getPositionX());
        return dx < 40;
    }
}
