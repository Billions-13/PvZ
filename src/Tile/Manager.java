package Tile;

import java.awt.*;

public class Manager {

    private final TileSet tileSet;
    private final MapLoader mapLoader;
    private final MapRenderer mapRenderer;
    private int frame;

    public Manager(int maxCols, int maxRows, int blockSize, int screenWidth, int screenHeight) {
        tileSet = new TileSet();
        mapLoader = new MapLoader(maxCols, maxRows);
        mapRenderer = new MapRenderer(
                tileSet,
                mapLoader,
                maxCols,
                maxRows,
                blockSize,
                screenWidth,
                screenHeight
        );
    }
    public int getFrame() {
        return frame;
    }

    public void update() {
        frame++;
        if (frame >= tileSet.getTileCount() * 5) frame = 0;
    }


    public void draw(Graphics2D g2, int playerX, int playerY, int playerScreenX, int playerScreenY) {
        frame++;
        mapRenderer.draw(g2, playerX, playerY, playerScreenX, playerScreenY);
    }
}
