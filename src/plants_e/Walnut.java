package plants_e;

public class Walnut extends Plant {

    private static final int MAX_HP = 4000;

    private static final String FULL = "walnut.gif";
    private static final String CRACK1 = "Wallnut_cracked1.gif";
    private static final String CRACK2 = "Wallnut_cracked2.gif";

    private int biteCount;

    public Walnut(int row, int col, double x, double y) {
        super("Walnut", MAX_HP, 0, 0, 30,
                row, col, true, 50,
                x, y, PlantType.WALNUT, 0,
                EffectType.NONE, FULL, null);
    }

    @Override
    public void takeDamage(int damage) {
        if (damage <= 0 || !isAlive()) return;

        biteCount++;

        if (biteCount >= 80) {
            setAlive(false);
            return;
        }

        if (biteCount >= 60) setSpritePath(CRACK2);
        else if (biteCount >= 20) setSpritePath(CRACK1);
        else setSpritePath(FULL);

        super.takeDamage(damage);
    }

    @Override public void update(double time) {}
    @Override public void onPlaced() {}
    @Override public void onRemoved() {}
}
