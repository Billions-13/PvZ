package Zombies;

public class Flag extends Zombie {

    public Flag(int row, double startX, double startY) {
        super(row, startX, startY, 190, 10, 4.7);

        normalAdvanceSprite = "/resources/img_Z/FlagZombie/Flag.gif";
        normalAttackSprite  = "/resources/img_Z/FlagZombie/FlagZombieAttack.gif";
        frozenAdvanceSprite = "/resources/img_Z/FlagZombie/frozenFlagZombie.gif";
        frozenAttackSprite  = "/resources/img_Z/FlagZombie/frozenFlagZombieAttack.gif";
        deadSprite          = "/resources/img_Z/NormalZombie/ZombieDeadImage.gif";
        headSprite          = "/resources/img_Z/NormalZombie/ZombieHead.gif";
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
    }
}
