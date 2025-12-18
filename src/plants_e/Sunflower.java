package plants_e;
public class    Sunflower extends Plant {

    private static final int default_health = 300;
    private static final int default_sunCost = 50;
    private static final double cooldown = 7.5;
    private static final String default_spritePath = "SUNFLOWER.gif";

    private final SunProductionBehavior sunProductionBehavior;

    public Sunflower(int row, int col, double positionX, double positionY,
                     SunProductionBehavior sunProductionBehavior) {

        super("Sunflower",
                default_health,
                0,           // không gây damage
                0.0,         // không bắn
                cooldown,
                row,
                col,
                true,
                default_sunCost,
                positionX,
                positionY,
                PlantType.SUNFLOWER,
                0.0,
                EffectType.NONE,
                default_spritePath,
                null
        );

        if (sunProductionBehavior == null) {
            this.sunProductionBehavior = new IntervalSunProductionBehavior(35.0, 25);
        } else {
            this.sunProductionBehavior = sunProductionBehavior;
        }
    }

    public SunProductionBehavior getSunProductionBehavior() {
        return sunProductionBehavior;
    }


    @Override
    public void onPlaced() {
        System.out.println("Sunflower planted at row " + getRow() + ", col " + getCol());
    }

    @Override
    public void update(double currentTime) {
        if (!isAlive()) return;

        if (getState() == PlantState.SPAWNING) {
            setState(PlantState.IDLE);
        }
    }

    @Override
    public void onRemoved() {
        System.out.println("Sunflower removed.");
    }
}
