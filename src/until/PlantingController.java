package until;

import entity.Gardener;
import plants_e.Plant;
import plants_e.PlantFactory;
import plants_e.PlantPanel;
import plants_e.PlantType;

public final class PlantingController {

    private static final long PLANTING_NS = 200_000_000L;

    private PlantType pickedType;
    private boolean digMode;

    private Pending pending;

    private static final class Pending {
        final PlantType type;
        final int row;
        final int col;
        final double worldPX;
        final double worldPY;
        final double targetX;
        final double targetY;
        boolean planting;
        long untilNs;

        Pending(PlantType type, int row, int col, double worldPX, double worldPY, double targetX, double targetY) {
            this.type = type;
            this.row = row;
            this.col = col;
            this.worldPX = worldPX;
            this.worldPY = worldPY;
            this.targetX = targetX;
            this.targetY = targetY;
        }
    }

    public boolean isDigMode() { return digMode; }
    public PlantType getPickedType() { return pickedType; }
    public boolean hasPending() { return pending != null; }

    public void enterDigMode() {
        digMode = true;
        pickedType = null;
        pending = null;
    }

    public void exitDigMode() { digMode = false; }

    public void pickPlant(PlantType type) {
        digMode = false;
        pickedType = type;
        pending = null;
    }

    public void clearPick() { pickedType = null; }

    public void createPending(int row, int col, double worldPX, double worldPY, double targetX, double targetY, MovementController mv) {
        pending = new Pending(pickedType, row, col, worldPX, worldPY, targetX, targetY);
        pickedType = null;
        mv.setTarget(targetX, targetY);
    }

    public void cancelPending(PlantPanel panel, MovementController mv) {
        pending = null;
        if (mv != null) mv.clearTarget();
        if (panel != null) panel.setForcedAnim(PlantPanel.PlayerAnim.AUTO, 0);
    }

    public void update(double dt,
                       long nowNs,
                       Gardener gardener,
                       MovementController mv,
                       PlantPanel plantPanel,
                       PlantSelectBar selectBar,
                       GameWorld world,
                       PlantFactory plantFactory,
                       HudLayer hud,
                       double minX, double minY, double maxX, double maxY) {

        if (pending == null) return;

        if (!pending.planting) {
            plantPanel.setForcedAnim(PlantPanel.PlayerAnim.COLLECT, 0);

            boolean arrived = mv.updateClickMove(gardener, dt, minX, minY, maxX, maxY);
            if (!arrived) return;

            pending.planting = true;
            pending.untilNs = nowNs + PLANTING_NS;
            plantPanel.setForcedAnim(PlantPanel.PlayerAnim.PLANTING, pending.untilNs);
            return;
        }

        if (nowNs < pending.untilNs) return;

        int cost = selectBar.getCost(pending.type);
        if (!world.spendSun(cost)) {
            hud.showMessage("Not enough sun", 2.5);
            pending = null;
            plantPanel.setForcedAnim(PlantPanel.PlayerAnim.AUTO, 0);
            return;
        }

        Plant p = plantFactory.createPlant(pending.type, pending.row, pending.col, pending.worldPX, pending.worldPY);
        world.addPlant(p);
        selectBar.startCooldown(pending.type);

        pending = null;
        plantPanel.setForcedAnim(PlantPanel.PlayerAnim.AUTO, 0);
    }
}
