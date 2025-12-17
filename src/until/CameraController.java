package until;

import entity.Gardener;

public class CameraController {

    public int camX, camY;
    public final int camSX, camSY;

    private final int worldW, worldH;
    private final int screenW, screenH;

    public CameraController(int screenW, int screenH, int tile, int worldW, int worldH) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.worldW = worldW;
        this.worldH = worldH;
        camSX = screenW / 2;
        camSY = screenH / 2;
    }

    public void follow(Gardener g) {
        camX = (int) g.getX();
        camY = (int) g.getY();

        camX = Math.max(camSX, Math.min(camX, worldW - camSX));
        camY = Math.max(camSY, Math.min(camY, worldH - camSY));
    }
}
