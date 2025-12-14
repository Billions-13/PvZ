package plants_e;
public class PeaShooter extends Plant {

    private static final int default_health = 300;
    private static final int damage = 20;
    private static final double speed = 1.2;
    private static final double cooldown = 7.5;
    private static final int default_sunCost = 100;
    private static final String default_spritePath = "PEASHOOTER.gif";

    public PeaShooter(int row, int col, double positionX, double positionY) {
        super("PeaShooter",
                default_health,
                damage,
                speed,
                cooldown,
                row,
                col,
                true,
                default_sunCost,
                positionX,
                positionY,
                PlantType.PEASHOOTER,
                0.0,
                EffectType.NONE,
                default_spritePath,
                null   // AttackHandler + AttackBehavior sẽ gán trong PlantFactory
        );
    }

    @Override
    public void onPlaced() {
        System.out.println("Peashooter placed at row " + getRow() + ", col " + getCol());
    }

    @Override
    public void update(double currentTime) {
        if (!isAlive()) return;

        if (getState() == PlantState.SPAWNING) {
            setState(PlantState.IDLE);
        }

        if (canAct(currentTime)) {
            doAttack();
            setLastActTime(currentTime);
        }
    }

    @Override
    public void onRemoved() {
        System.out.println("Peashooter removed.");
    }
}
