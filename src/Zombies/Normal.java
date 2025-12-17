package Zombies;

public class Normal extends Zombie {

    public Normal(int row, double startX, double startY) {
        super(row, startX, startY, 190, 10, 4.7);
        normalAdvanceSprite = "/resources/img_Z/NormalZombie/ZombieAdvanceImage.gif";
        normalAttackSprite = "/resources/img_Z/NormalZombie/ZombieAttackImage.gif";
        frozenAdvanceSprite = normalAdvanceSprite;
        frozenAttackSprite =  normalAttackSprite;
        deadSprite = "/resources/img_Z/NormalZombie/ZombieDeadImage.gif";
        headSprite = "/resources/img_Z/NormalZombie/ZombieHead.gif";
    }
}
