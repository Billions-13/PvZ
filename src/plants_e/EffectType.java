package plants_e;
public enum EffectType {
    NONE("None"),
    SLOW("Slow"),
    BURN("Burn"),
    STUN("Stun"),
    FREEZE("Freeze"),
    SPLASH("Splash");

    private final String displayName;

    EffectType(String displayName) {
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
