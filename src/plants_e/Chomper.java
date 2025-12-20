package plants_e;
public class Chomper extends Plant {

    private static final int default_health = 300;
    private static final int damage = 150;          // cắn rất đau
    private static final double biteCooldown = 7.0; // thời gian giữa hai cú cắn
    private static final double cooldown = 7.0;
    private static final int default_sunCost = 150;
    private static final String default_spritePath = "CHOMPER.gif";

    public Chomper(int row, int col, double positionX, double positionY) {
        super("Chomper",
                default_health  ,
                damage,
                biteCooldown,   // dùng chung làm attackSpeed
                cooldown,
                row,
                col,
                true,
                default_sunCost,
                positionX,
                positionY,
                PlantType.CHOMPER,
                0.0,
                EffectType.NONE,
                default_spritePath,
                null
        );
    }

    @Override
    public void onPlaced() {
    }

    @Override
    public void update(double currentTime) {
        if (!isAlive()) return;

        if (getState() == PlantState.SPAWNING) {
            setState(PlantState.IDLE);
        }

        // doAttack
        if (canAct(currentTime)) {
            doAttack();                 // BiteAttackBehavior sẽ gọi AttackHandler
            setLastActTime(currentTime);
        }
    }

    @Override
    public void onRemoved() {
}}
