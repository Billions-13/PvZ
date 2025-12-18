package plants_e;

public class Snowpea extends Plant {

    private static final int default_health = 300;
    private static final int damage = 20;
    private static final double speed = 1.2;
    private static final double cooldown = 7.5;
    private static final int default_sunCost = 175;

    public Snowpea(int row, int col, double positionX, double positionY) {
        super("Snowpea",
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
                PlantType.SNOWPEA,
                0.0,
                EffectType.SLOW,
                "",
                null
        );
    }

    @Override public void onPlaced() {}

    @Override
    public void update(double currentTime) {
        if (!isAlive()) return;
        if (getState() == PlantState.SPAWNING) setState(PlantState.IDLE);

        if (isTargeting() && canAct(currentTime)) {
            doAttack();
            setLastActTime(currentTime);
        }
    }

    @Override public void onRemoved() {}
}
