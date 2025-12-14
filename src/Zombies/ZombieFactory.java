package Zombies;

public class ZombieFactory {

    private ZombieFactory() {}

    public static Zombie createZombie(
            ZombieType type,
            int row,
            double startX,
            double startY
    ) {
        return switch (type) {
            case NORMAL -> new Normal(row, startX, startY);
            case CONE_HEAD -> new ConeHead(row, startX, startY);
            case BUCKET_HEAD -> new BucketHead(row, startX, startY);
            case FLAG -> new Flag(row, startX, startY);
        };
    }
}
