package Tile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapLoader {
    private static final String MAP_CLASSPATH = "/resources/img_W/map.txt";
    private static final String MAP_FILE = "src/resources/img_W/map.txt";

    private final int maxCols;
    private final int maxRows;
    private final int[][] mapData;

    public MapLoader(int maxCols, int maxRows) {
        this.maxCols = maxCols;
        this.maxRows = maxRows;
        this.mapData = new int[maxCols][maxRows];
        loadMap();
    }

    private void loadMap() {
        InputStream is = getClass().getResourceAsStream(MAP_CLASSPATH);

        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                read(br);
                return;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        File f = new File(MAP_FILE);
        if (!f.exists()) throw new RuntimeException("Missing map.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            read(br);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void read(BufferedReader br) throws Exception {
        for (int r = 0; r < maxRows; r++) {
            String line = br.readLine();
            if (line == null) throw new RuntimeException("Invalid map rows at r=" + r);

            String[] parts = line.trim().split("\\s+");
            if (parts.length < maxCols) throw new RuntimeException("Invalid map cols at r=" + r);

            for (int c = 0; c < maxCols; c++) {
                mapData[c][r] = Integer.parseInt(parts[c]);
            }
        }
    }

    public int getTileId(int col, int row) {
        return mapData[col][row];
    }

    public int getMaxCols() { return maxCols; }
    public int getMaxRows() { return maxRows; }
}
