package until;

import plants_e.*;

public class PlantInputHandler {

    private final PlantFactory factory;
    private final GameWorld world;

    public PlantInputHandler(PlantFactory factory, GameWorld world) {
        this.factory = factory;
        this.world = world;
    }

    public void plant(int type, int row, int col, double x, double y) {
        PlantType pt = switch (type) {
            case 2 -> PlantType.PEASHOOTER;
            case 3 -> PlantType.SNOWPEA;
            case 4 -> PlantType.WALNUT;
            case 5 -> PlantType.CHOMPER;
            default -> PlantType.SUNFLOWER;
        };
        Plant p = factory.createPlant(pt, row, col, x, y);
        world.addPlant(p);
    }
}
