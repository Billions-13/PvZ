package until;

import plants_e.PlantPanel;
import plants_e.PlantType;
import plants_e.Sun;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class InputController {

    private final JComponent root;
    private final PlantPanel plantPanel;
    private final PlantSelectBar selectBar;
    private final HudLayer hud;
    private final GameWorld world;

    private final MovementController mv;
    private final PlantingController planting;
    private final CursorManager cursor;

    private final int maxcol, maxrow, block;

    public InputController(
            JComponent root,
            PlantPanel plantPanel,
            PlantSelectBar selectBar,
            HudLayer hud,
            GameWorld world,
            MovementController mv,
            PlantingController planting,
            CursorManager cursor,
            int maxcol,
            int maxrow,
            int block
    ) {
        this.root = root;
        this.plantPanel = plantPanel;
        this.selectBar = selectBar;
        this.hud = hud;
        this.world = world;
        this.mv = mv;
        this.planting = planting;
        this.cursor = cursor;
        this.maxcol = maxcol;
        this.maxrow = maxrow;
        this.block = block;
    }

    public void bind() {
        selectBar.setListener(type -> {
            if (type == null) {
                planting.enterDigMode();
                cursor.setDig(true);
                mv.clearTarget();
                selectBar.clearSelection();
                return;
            }

            if (!selectBar.canPick(type)) {
                hud.showMessage("Cooling down", 2.5);
                selectBar.clearSelection();
                return;
            }

            if (world.getSunPoints() < selectBar.getCost(type)) {
                hud.showMessage("Not enough sun", 2.5);
                selectBar.clearSelection();
                return;
            }

            planting.pickPlant(type);
        });

        plantPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                root.requestFocusInWindow();

                var pOnRoot = SwingUtilities.convertPoint(plantPanel, e.getPoint(), root);
                if (selectBar.getBounds().contains(pOnRoot)) return;

                Sun s = plantPanel.pickSunAt(e.getX(), e.getY());
                if (s != null && !s.isCollected() && !s.isCollecting()) {
                    s.startCollect();
                    plantPanel.setForcedAnim(
                            PlantPanel.PlayerAnim.COLLECT,
                            System.nanoTime() + 300_000_000L
                    );
                    return;
                }

                int tile = block * 2;

                int col = Math.max(0, Math.min(maxcol - 1, plantPanel.snapColFromMouse(e.getX())));
                int row = Math.max(0, Math.min(maxrow - 1, plantPanel.snapRowFromMouse(e.getY())));

                double worldPX = col * (double) tile;
                double worldPY = row * (double) tile;

                double centerX = worldPX + tile / 2.0;
                double centerY = worldPY + tile / 2.0;

                if (planting.isDigMode()) {
                    world.removePlantAt(row, col);
                    planting.exitDigMode();
                    cursor.setDig(false);
                    selectBar.clearSelection();
                    return;
                }

                PlantType picked = planting.getPickedType();
                if (picked != null) {
                    planting.createPending(row, col, worldPX, worldPY, centerX, centerY, mv);
                    selectBar.clearSelection();
                    return;
                }

                mv.setTarget(centerX, centerY);
            }
        });
    }
}
