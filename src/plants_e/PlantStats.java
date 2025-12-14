package plants_e;
/**
 * PlantStats gom "chỉ số mặc định" cho từng loại Plant.
 * Chỉ là DATA, không chứa logic game.
 */
public class PlantStats {

    private final String name;
    private final int health;
    private final int attackDamage;
    private final double attackSpeed;
    private final double coolDown;
    private final int sunCost;
    private final PlantType plantType;
    private final EffectType effectType;
    private final String spritePath;

    public PlantStats(String name,
                      int health,
                      int attackDamage,
                      double attackSpeed,
                      double coolDown,
                      int sunCost,
                      PlantType plantType,
                      EffectType effectType,
                      String spritePath) {

        this.name = name;
        this.health = Math.max(0, health);
        this.attackDamage = attackDamage;
        this.attackSpeed = Math.max(0.0, attackSpeed);
        this.coolDown = Math.max(0.0, coolDown);
        this.sunCost = Math.max(0, sunCost);
        this.plantType = plantType;
        this.effectType = effectType;
        this.spritePath = spritePath;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getAttackDamage() { return attackDamage; }
    public double getAttackSpeed() { return attackSpeed; }
    public double getCoolDown() { return coolDown; }
    public int getSunCost() { return sunCost; }
    public PlantType getPlantType() { return plantType; }
    public EffectType getEffectType() { return effectType; }
    public String getSpritePath() { return spritePath; }

    // Preset 5 cây chính – dùng cho UI/menu...
    public static PlantStats createPeashooterDefault() {
        return new PlantStats(
                "Peashooter", 100, 20,
                1.0, 1.5, 100,
                PlantType.PEASHOOTER,
                EffectType.NONE,
                "PlantsVsZombies_-_Copy_qTEU7zHTrG"
        );
    }

    public static PlantStats createSunflowerDefault() {
        return new PlantStats(
                "Sunflower", 300, 0,
                0.0, 7.5, 50,
                PlantType.SUNFLOWER,
                EffectType.NONE,
                "Sunflower.png"
        );
    }

    public static PlantStats createWalnutDefault() {
        return new PlantStats(
                "Walnut", 4000, 0,
                0.0, 30.0, 50,
                PlantType.WALNUT,
                EffectType.NONE,
                "Walnut.png"
        );
    }

    public static PlantStats createSnowpeaDefault() {
        return new PlantStats(
                "Snowpea", 300, 20,
                1.2, 7.5, 175,
                PlantType.SNOWPEA,
                EffectType.SLOW,
                "Snowpea.png"
        );
    }

    public static PlantStats createChomperDefault() {
        return new PlantStats(
                "Chomper", 300, 150,
                7.0, 7.0, 150,
                PlantType.CHOMPER,
                EffectType.NONE,
                "Chomper.png"
        );
    }
}
