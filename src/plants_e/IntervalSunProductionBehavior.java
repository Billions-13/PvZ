package plants_e;

import until.GameWorld;

public class IntervalSunProductionBehavior implements SunProductionBehavior {

    private final double produceInterval;
    private final int sunAmount;

    public IntervalSunProductionBehavior(double produceInterval, int sunAmount) {
        this.produceInterval = Math.max(0.0, produceInterval);
        this.sunAmount = Math.max(0, sunAmount);
    }

    @Override
    public void updateSunProduction(Sunflower source, double currentTime, GameWorld world) {
        if (source == null || !source.isAlive() || world == null) return;

        if (source.getLastActTime() == 0.0) {
            source.setLastActTime(currentTime);
            return;
        }

        double elapsed = currentTime - source.getLastActTime();
        if (elapsed < produceInterval) return;


        double x = source.getPositionX();
        double y = source.getPositionY() - 40;

        world.addSun(new Sun(x, y, sunAmount, false, y));
        source.setLastActTime(currentTime);
    }
}
