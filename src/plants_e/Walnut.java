package plants_e;

public class Walnut extends Plant {

    private static final int MAX_HP = 4000;

    private static final String FULL = "walnut_full.gif";
    private static final String CRACK1 = "walnut_crack1.gif";
    private static final String CRACK2 = "walnut_crack2.gif";

    public Walnut(int row, int col, double x, double y) {
        super("Walnut", MAX_HP, 0, 0, 30,
                row, col, true, 50,
                x, y, PlantType.WALNUT, 0,
                EffectType.NONE, FULL, null);
    }

    @Override
    public void update(double time) {
        if (!isAlive()) return;

        double ratio = (double) health / MAX_HP;
        if (ratio < 0.33) setSpritePath(CRACK2);
        else if (ratio < 0.66) setSpritePath(CRACK1);
        else setSpritePath(FULL);
    }

    @Override public void onPlaced() {}
    @Override public void onRemoved() {}
}
