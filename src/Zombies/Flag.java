package Zombies;

public class Flag extends Zombie {

    public Flag(int row, double startX, double startY) {
        super(row, startX, startY, 190, 10, 4.7);
        normalAdvanceSprite = "/resources/img_Z/FlagZombieImage/FlagZombie.gif";
        normalAttackSprite = "/resources/img_Z/FlagZombieImage/FlagZombieAttack.gif";
        frozenAdvanceSprite = "/resources/img_Z/FlagZombieImage/frozenFlagZombie.gif";
        frozenAttackSprite = "/resources/img_Z/FlagZombieImage/frozenFlagZombieAttack.gif";
        deadSprite = "/resources/img_Z/NormalZombieImage/ZombieDeadImage.gif";
        headSprite = "/resources/img_Z/NormalZombieImage/ZombieHead.gif";
    }
}
