package plants_e;
public enum PlantType {
    PEASHOOTER("Peashooter"),
    SUNFLOWER("Sunflower"),
    WALNUT("Walnut"),
    SNOWPEA("Snowpea"),
    CHOMPER("Chomper");

    private final String displayName;

    PlantType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
