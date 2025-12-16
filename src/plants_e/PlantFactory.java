package plants_e;
import java.util.HashMap;
import java.util.Map;

/**
 * PlantFactory chịu trách nhiệm tạo Plant:
 * - Gán AttackHandler & AttackBehavior cho cây tấn công được.
 * - Gán SunProductionBehavior cho Sunflower.
 * - Cung cấp PlantStats cho UI.
 */
public class PlantFactory {

    private final AttackHandler attackHandler;
    private final Map<PlantType, PlantStats> defaultStats = new HashMap<>();

    public PlantFactory(AttackHandler attackHandler) {
        this.attackHandler = attackHandler;
        initDefaultStats();
    }

    private void initDefaultStats() {
        defaultStats.put(PlantType.PEASHOOTER, PlantStats.createPeashooterDefault());
        defaultStats.put(PlantType.SUNFLOWER, PlantStats.createSunflowerDefault());
        defaultStats.put(PlantType.WALNUT,    PlantStats.createWalnutDefault());
        defaultStats.put(PlantType.SNOWPEA,   PlantStats.createSnowpeaDefault());
        defaultStats.put(PlantType.CHOMPER,   PlantStats.createChomperDefault());
    }

    public Plant createPlant(PlantType type,
                             int row,
                             int col,
                             double positionX,
                             double positionY) {

        if (type == null) {
            throw new IllegalArgumentException("PlantType must not be null");
        }

        Plant plant;

        if (type == PlantType.PEASHOOTER) {
            PeaShooter p = new PeaShooter(row, col, positionX, positionY);
            p.setAttackHandler(attackHandler);
            p.setAttackBehavior(new SingleProjectileAttackBehavior());
            plant = p;

        } else if (type == PlantType.SUNFLOWER) {
            Sunflower s = new Sunflower(
                    row, col, positionX, positionY,
                    new IntervalSunProductionBehavior(10.0, 50)
            );
            plant = s;


        } else if (type == PlantType.WALNUT) {
            plant = new Walnut(row, col, positionX, positionY);

        } else if (type == PlantType.SNOWPEA) {
            Snowpea s = new Snowpea(row, col, positionX, positionY);
            s.setAttackHandler(attackHandler);
            s.setAttackBehavior(new SingleProjectileAttackBehavior());
            plant = s;

        } else if (type == PlantType.CHOMPER) {
            Chomper c = new Chomper(row, col, positionX, positionY);
            c.setAttackHandler(attackHandler);
            c.setAttackBehavior(new BiteAttackBehavior());
            plant = c;

        } else {
            plant = null;
        }

        return plant;
    }

    public PlantStats getDefaultStats(PlantType type) {
        return defaultStats.get(type);
    }
}
