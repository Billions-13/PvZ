package plants_e;
public class Snowpea extends Plant {

    private static final int default_health = 300;
    private static final int damage = 20;
    private static final double speed = 1.2;
    private static final double cooldown = 7.5;
    private static final int default_sunCost = 175;
    private static final String default_spritePath = "Snowpea_idle.gif";

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
                EffectType.SLOW,   // khác Peashooter: có hiệu ứng làm chậm
                default_spritePath,
                null               // handler + behavior set từ PlantFactory
        );
    }

    @Override
    public void onPlaced() {
        System.out.println("Snowpea placed at row " + getRow() + ", col " + getCol());
    }

    @Override
    public void update(double currentTime) {
        if (!isAlive()) return;

        if (getState() == PlantState.SPAWNING) {
            setState(PlantState.IDLE);
        }

        if (isTargeting() && canAct(currentTime)) {
            doAttack();
            setLastActTime(currentTime);
        }
    }

    @Override
    public void onRemoved() {
        System.out.println("Snowpea removed.");
    }
}
