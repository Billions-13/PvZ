package Tile;

import java.awt.*;

public class MapRenderer {
    private final TileSet tileSet;
    private final MapLoader mapLoader;
    private final int maxCols;
    private final int maxRows;
    private final int blockSize;
    private final int screenWidth;
    private final int screenHeight;

    private int frame;

    public MapRenderer(TileSet tileSet, MapLoader mapLoader,
                       int maxCols, int maxRows, int blockSize,
                       int screenWidth, int screenHeight) {
        this.tileSet = tileSet;
        this.mapLoader = mapLoader;
        this.maxCols = maxCols;
        this.maxRows = maxRows;
        this.blockSize = blockSize;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void draw(Graphics2D g2, int playerX, int playerY, int playerScreenX, int playerScreenY) {
        frame++;

        for (int r = 0; r < maxRows; r++) {
            for (int c = 0; c < maxCols; c++) {
                int worldX = c * blockSize * 2;
                int worldY = r * blockSize * 2;
                int screenX = worldX - playerX + playerScreenX;
                int screenY = worldY - playerY + playerScreenY;

                if (screenX + blockSize * 2 > 0 && screenX < screenWidth &&
                        screenY + blockSize * 2 > 0 && screenY < screenHeight) {

                    int tileId = mapLoader.getTileId(c, r);

                    /*if (tileId == 0 || tileId == 1 || tileId == 2) {
                        int anim = (frame / 20) % 4;
                        tileId = switch (anim) {
                            case 0 -> 0;
                            case 1 -> 2;
                            case 2 -> 1;
                            default -> 2;
                        };
                    }*/

                   /* if (tileId == 0 || tileId == 1) {
                        int t = (frame / 20) & 1;   // chậm, nhẹ
                        tileId = (t == 0) ? 0 : 1;
                    }*/

                    if (tileId == 0 || tileId == 1) {
                        long t = System.currentTimeMillis();
                        int anim = (int) ((t / 450) & 1); // 450ms đổi 1 lần, rất nhẹ và ổn định
                        tileId = anim == 0 ? 0 : 1;
                    }



                    Tile tile = tileSet.getTile(tileId);
                    g2.drawImage(tile.getImage(), screenX, screenY,
                            blockSize * 2, blockSize * 2, null);
                }
            }
        }
    }
}
