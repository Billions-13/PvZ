package Tile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TileSet {

    public static final int GRASS_1  = 0;
    public static final int GRASS_2  = 1;
    public static final int GRASS_1P = 2;
    public static final int GRASS_2B = 3;
    public static final int HOUSE_1  = 4;
    public static final int HOUSE_2  = 5;
    public static final int GRASS_A  = 6;
    public static final int GRASS_B  = 7;

    private static final int TILE_COUNT = 8;
    private final Tile[] tiles = new Tile[TILE_COUNT];

    public TileSet() {
        loadTiles();
    }

    private void loadTiles() {
        try {
            tiles[GRASS_1]  = tile("grass1.png");
            tiles[GRASS_2]  = tile("grass2.png");
            tiles[GRASS_1P] = tile("grass1p.png");

            // duplicate grass để map không bị lỗi id cũ
            tiles[GRASS_2B] = tile("grass2.png");

            tiles[HOUSE_1] = tile("tile_house.png");
            tiles[HOUSE_2] = tile("tile_house2.png");

            // thay thế tree cũ bằng grass
            tiles[GRASS_A] = tile("grass1.png");
            tiles[GRASS_B] = tile("grass2.png");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load tiles", e);
        }
    }

    private Tile tile(String fileName) throws IOException {
        Tile t = new Tile();

        InputStream is = getClass().getResourceAsStream("/resources/img_W/" + fileName);
        if (is != null) {
            t.setImage(ImageIO.read(is));
            return t;
        }

        File f = new File("src/resources/img_W/" + fileName);
        if (f.exists()) {
            t.setImage(ImageIO.read(f));
            return t;
        }

        throw new RuntimeException("Missing tile image: " + fileName);
    }

    public Tile getTile(int index) {
        if (index < 0 || index >= TILE_COUNT)
            throw new IndexOutOfBoundsException("Tile index: " + index);
        return tiles[index];
    }

    public int getTileCount() {
        return TILE_COUNT;
    }
}
