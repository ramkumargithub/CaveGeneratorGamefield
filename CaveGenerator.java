import java.util.*;

/**
 * CaveGenerator
 *
 * Usage:
 *   java CaveGenerator <width> <height> <numWalls> <numCollapsingSpots>
 *
 * Example:
 *   java CaveGenerator 40 20 10 25
 *
 * This generates a play field per the assignment:
 * - Start '>' on left edge (random row)
 * - Finish '<' on right edge (random row)
 * - Walls 'W' (count = numWalls), each a contiguous segment length 1-5, horizontal or vertical
 * - Collapsing spots 'C' (count = numCollapsingSpots) placed after start/finish/walls
 * - Neighbor hollowness values '1'..'9' on empty cells around C
 *
 * Notes:
 * - If there is not enough empty space to place all collapsing spots, program exits with error.
 * - The generator tries to place walls reasonably; if it cannot place the specified number
 *   due to space constraints, it will fail with a helpful error.
 */
public class CaveGenerator {

    private final int width;
    private final int height;
    private final int numWalls;
    private final int numCollapsing;
    private final char[][] grid;
    private final int[][] hollow; // hollowness counts, for display as digits
    private final Random rng;

    // Constants for display
    private static final char EMPTY = '.';
    private static final char WALL = 'W';
    private static final char COLLAPSE = 'C';
    private static final char START = '>';
    private static final char FINISH = '<';

    public CaveGenerator(int width, int height, int numWalls, int numCollapsing, long seed) {
        if (width < 3 || height < 3) throw new IllegalArgumentException("Width and height must be >= 3");
        this.width = width;
        this.height = height;
        this.numWalls = Math.max(0, numWalls);
        this.numCollapsing = Math.max(0, numCollapsing);
        this.grid = new char[height][width];
        this.hollow = new int[height][width];
        this.rng = (seed == 0L) ? new Random() : new Random(seed);
        // initialize grid to empty
        for (int r = 0; r < height; r++) Arrays.fill(this.grid[r], EMPTY);
    }

    public void generate() {
        placeStartAndFinish();
        placeWalls();
        placeCollapsingSpots();
    }

    private void placeStartAndFinish() {
        // Start on left edge (column 0), random row
        int startRow = rng.nextInt(height);
        grid[startRow][0] = START;

        // Finish on right edge (column width-1), random row
        int finishRow;
        // Ensure finish not on same cell as start if width==1 (but width>=3 by validation)
        do {
            finishRow = rng.nextInt(height);
        } while (finishRow == startRow && width == 1);
        grid[finishRow][width - 1] = FINISH;
    }

    private void placeWalls() {
        // Try to place the requested number of wall segments.
        // Each wall: length random 1..5, orientation random horizontal/vertical.
        // Must fit in grid and not overwrite START/FINISH.
        final int MAX_TRIES_PER_WALL = 500; // prevents infinite loops on dense grids

        for (int w = 0; w < numWalls; w++) {
            boolean placed = false;
            for (int attempt = 0; attempt < MAX_TRIES_PER_WALL; attempt++) {
                boolean horizontal = rng.nextBoolean();
                int length = rng.nextInt(5) + 1; // 1..5

                if (horizontal) {
                    int r = rng.nextInt(height);
                    int maxStartC = width - length;
                    if (maxStartC < 0) continue; // wall too long for grid width
                    int c = rng.nextInt(maxStartC + 1);
                    if (canPlaceWall(r, c, length, true)) {
                        applyWall(r, c, length, true);
                        placed = true;
                        break;
                    }
                } else { // vertical
                    int c = rng.nextInt(width);
                    int maxStartR = height - length;
                    if (maxStartR < 0) continue;
                    int r = rng.nextInt(maxStartR + 1);
                    if (canPlaceWall(r, c, length, false)) {
                        applyWall(r, c, length, false);
                        placed = true;
                        break;
                    }
                }
            }
            if (!placed) {
                throw new IllegalStateException(String.format(
                    "Failed to place wall #%d after many attempts. Grid may be too dense for requested walls.", w + 1));
            }
        }
    }

    private boolean canPlaceWall(int r, int c, int length, boolean horizontal) {
        for (int i = 0; i < length; i++) {
            int rr = r + (horizontal ? 0 : i);
            int cc = c + (horizontal ? i : 0);
            char cur = grid[rr][cc];
            // must not overwrite start or finish; it's ok to overlap other walls per spec? we avoid placing over any non-empty to be safe
            if (cur != EMPTY) return false;
        }
        return true;
    }

    private void applyWall(int r, int c, int length, boolean horizontal) {
        for (int i = 0; i < length; i++) {
            int rr = r + (horizontal ? 0 : i);
            int cc = c + (horizontal ? i : 0);
            grid[rr][cc] = WALL;
        }
    }

    private void placeCollapsingSpots() {
        // Find all empty cells ('.') where we can place collapsing spots.
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == EMPTY) empties.add(new int[]{r, c});
            }
        }
        if (empties.size() < numCollapsing) {
            throw new IllegalStateException(String.format(
                "Not enough empty spaces (%d) to place %d collapsing spots. Aborting.", empties.size(), numCollapsing));
        }

        // Shuffle empties and pick first numCollapsing locations
        Collections.shuffle(empties, rng);
        for (int i = 0; i < numCollapsing; i++) {
            int[] pos = empties.get(i);
            int r = pos[0], c = pos[1];
            grid[r][c] = COLLAPSE;
        }

        // Build hollowness numbers from collapsing spots
        computeHollowness();
    }

    private void computeHollowness() {
        // Reset hollow
        for (int r = 0; r < height; r++) Arrays.fill(hollow[r], 0);

        int[] d = {-1, 0, 1};
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == COLLAPSE) {
                    for (int dr : d) {
                        for (int dc : d) {
                            if (dr == 0 && dc == 0) continue;
                            int nr = r + dr, nc = c + dc;
                            if (nr >= 0 && nr < height && nc >= 0 && nc < width) {
                                // Only increment hollowness for cells that are not walls and not collapsing spots.
                                if (grid[nr][nc] != WALL && grid[nr][nc] != COLLAPSE) {
                                    hollow[nr][nc] = Math.min(9, hollow[nr][nc] + 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void printField() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                char out;
                char cell = grid[r][c];
                if (cell == START || cell == FINISH || cell == WALL || cell == COLLAPSE) {
                    out = cell;
                } else {
                    int val = hollow[r][c];
                    if (val > 0) out = (char) ('0' + val);
                    else out = EMPTY;
                }
                sb.append(out);
                if (c < width - 1) sb.append(' ');
            }
            sb.append('\n');
        }
        System.out.print(sb.toString());
    }

    // Utility: print grid to console with metadata summary
    private void printSummary() {
        int countW = 0, countC = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == WALL) countW++;
                if (grid[r][c] == COLLAPSE) countC++;
            }
        }
        System.out.printf("Generated field %dx%d with %d walls (total wall cells: %d) and %d collapsing spots.%n",
                width, height, numWalls, countW, countC);
    }

    public static void main(String[] args) {
        if (args.length < 4 || args.length > 5) {
            System.err.println("Usage: java CaveGenerator <width> <height> <numWalls> <numCollapsingSpots> [randomSeed]");
            System.exit(1);
        }

        int width = parsePositiveInt(args[0], "width");
        int height = parsePositiveInt(args[1], "height");
        int numWalls = parseNonNegativeInt(args[2], "numWalls");
        int numCollapsing = parseNonNegativeInt(args[3], "numCollapsingSpots");
        long seed = 0L;
        if (args.length == 5) {
            try {
                seed = Long.parseLong(args[4]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid seed; using random seed.");
                seed = 0L;
            }
        }

        CaveGenerator gen = new CaveGenerator(width, height, numWalls, numCollapsing, seed);
        try {
            gen.generate();
            gen.printSummary();
            gen.printField();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.exit(2);
        }
    }

    private static int parsePositiveInt(String s, String name) {
        try {
            int v = Integer.parseInt(s);
            if (v <= 0) throw new IllegalArgumentException(name + " must be > 0");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be integer > 0");
        }
    }

    private static int parseNonNegativeInt(String s, String name) {
        try {
            int v = Integer.parseInt(s);
            if (v < 0) throw new IllegalArgumentException(name + " must be >= 0");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be integer >= 0");
        }
    }
}
