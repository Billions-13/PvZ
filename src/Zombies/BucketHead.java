package Zombies;

public class BucketHead extends Zombie {

    private int bucketHp = 1100;
    private boolean bucketBroken;

    public BucketHead(int row, double startX, double startY) {
        super(row, startX, startY, 190, 10, 4.7);
        normalAdvanceSprite = "/resources/img_Z/BucketheadZombie/Buckethead.gif";
        normalAttackSprite  = "/resources/img_Z/BucketheadZombie/BucketheadZombieAttack.gif";
        frozenAdvanceSprite = "/resources/img_Z/BucketheadZombie/frozenBucketheadZombie.gif";
        frozenAttackSprite  = "/resources/img_Z/BucketheadZombie/frozenBucketheadZombieAttack.gif";
        deadSprite          = "/resources/img_Z/NormalZombie/ZombieDeadImage.gif";
        headSprite          = "/resources/img_Z/NormalZombie/ZombieHead.gif";

    }

    @Override
    public void takeDamage(int dmg) {
        if (isDead() || dmg <= 0) return;
        if (getState() == ZombieState.HEAD || getState() == ZombieState.DEAD) return;

        if (!bucketBroken) {
            bucketHp -= dmg;
            if (bucketHp <= 0) bucketBroken = true;
            return;
        }

        super.takeDamage(dmg);
    }
}
