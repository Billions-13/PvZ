package plants_e;
import Zombies.Zombie;

public interface ProjectileWorld {
    Zombie findFirstZombieInPath(int row, double projectileX);
    void applySlowToZombie(Zombie zombie);
}
