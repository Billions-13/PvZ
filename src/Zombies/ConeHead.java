package Zombies;

public class ConeHead extends Zombie {

    private int coneHp = 370;
    private boolean coneBroken;

    public ConeHead(int row, double startX, double startY) {
        super(row, startX, startY, 190, 10, 4.7);
        normalAdvanceSprite = "/resources/img_Z/ConeheadZombie/ConeheadMove.gif";
        normalAttackSprite  = "/resources/img_Z/ConeheadZombie/ConeheadZombieAttack.gif";
        frozenAdvanceSprite = "/resources/img_Z/ConeheadZombie/frozenConeheadZombie.gif";
        frozenAttackSprite  = "/resources/img_Z/ConeheadZombie/frozenConeheadZombieAttack.gif";
        deadSprite          = "/resources/img_Z/NormalZombie/ZombieDeadImage.gif";
        headSprite          = "/resources/img_Z/NormalZombie/ZombieHead.gif";

    }

    @Override
    public void takeDamage(int dmg) {
        if (isDead() || dmg <= 0) return;
        if (getState() == ZombieState.HEAD || getState() == ZombieState.DEAD) return;

        if (!coneBroken) {
            coneHp -= dmg;
            if (coneHp <= 0) coneBroken = true;
            return;
        }

        super.takeDamage(dmg);
    }
}
