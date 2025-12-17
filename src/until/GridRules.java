package until;

public final class GridRules {
    public static final int FORBIDDEN_ROW = 0;
    public static final int FORBIDDEN_COL = 0;

    private GridRules() {}

    public static boolean canPlacePlant(int row, int col) {
        return row != FORBIDDEN_ROW && col != FORBIDDEN_COL;
    }

    public static int zombieSpawnRow(int gridRows, int spawnedIndex) {
        int minRow = 2;
        int maxRow = gridRows - 1;
        int range = maxRow - minRow + 1;
        return minRow + (spawnedIndex % range);
    }
}
