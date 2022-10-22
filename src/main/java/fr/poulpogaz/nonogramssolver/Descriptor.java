package fr.poulpogaz.nonogramssolver;

import java.util.*;

public class Descriptor {

    /**
     * row if true, column if false
     */
    private final boolean isRow;
    private final int index;
    
    private final Clue[] clues;
    private final CellWrapper[] cells;

    /**
     * the sum of all numbers and the empty cell between them
     */
    private final int descriptorLength;
    private int maxClue = -1;

    /**
     * For each cell, contains an array of length the number of clue
     * containing true if the i-th clue can be present at the cell
     */
    private final boolean[][] possibilities;

    public Descriptor(boolean isRow, int index, int[] clues, CellWrapper[] cells) {
        this.isRow = isRow;
        this.index = index;
        this.cells = cells;

        this.clues = new Clue[clues.length];

        for (int i = 0; i < clues.length; i++) {
            this.clues[i] = new Clue(clues[i], i);
        }

        descriptorLength = length(0, clues.length);
        maxClue = getMaxClue();

        possibilities = new boolean[cells.length][clues.length];

        for (int i = 0; i < cells.length; i++) {
            possibilities[i] = new boolean[clues.length];
        }
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {
        if (clues.length == 0) {
            fill(0, cells.length, Cell.CROSSED);
            return false;
        }

        System.out.println("-------------------------------------");
        System.out.printf("Row: %b. Index: %d%n", isRow, index);
        System.out.println("Clues: " + Arrays.toString(clues));

        List<Line> lines = new ArrayList<>();

        int length = 0;
        for (int i = 0; i < cells.length; i++) {
            CellWrapper cell = cells[i];

            if (cell.isFilled()) {
                length++;
            } else if (length > 0) {
                lines.add(new Line(i - length, i));
                length = 0;
            }
        }

        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        crossZeroCells();
        List<Region> regions = split();

        for (Region r : regions) {
            r.trySolve();
        }

        return true;
    }

    protected void initClues() {
        int minI = 0;
        int maxI = cells.length - descriptorLength;

        for (int i = 0; i < clues.length; i++) {
            Clue c = clues[i];

            if (i == 0) {
                maxI += c.getLength();
            } else {
                maxI += c.getLength() + 1;
                minI += clues[i - 1].getLength() + 1;
            }

            c.setMinI(minI);
            c.setMaxI(maxI);
        }
    }

    /**
     * Compute possibilities. It doesn't take into account all information that gave the map.
     * For example, for 15 cells, clues 5 2 and a map looking like this: '  F    F    FF  ',
     * it won't realize that outside the first two F, it mustn't have a 5.
     */
    protected void computePossibilities() {
        Utils.fill(possibilities, false);

        for (Clue clue : clues) {
            int k = clue.getMinI();
            for (; k + clue.getLength() < clue.getMaxI(); k++) {
                boolean fit = fit(clue.getLength(), k);

                if (fit) {
                    for (int l = k; l < k + clue.getLength(); l++) {
                        possibilities[l][clue.getIndex()] = true;
                    }
                }
            }

            if (k < clue.getMaxI()) {
                boolean fit = fit(clue.getLength(), clue.getMaxI() - clue.getLength());

                if (fit) {
                    for (int l = k; l < clue.getMaxI(); l++) {
                        possibilities[l][clue.getIndex()] = true;
                    }
                }
            }
        }
    }

    protected void optimizeCluesBoundWithOnePossibility() {
        for (Clue clue : clues) {

            // find the first index that we are sure it is associated with the clue
            int firstIndex = -1;
            for (int i = clue.getMinI(); i < clue.getMaxI(); i++) {
                if (!cells[i].isFilled()) {
                    continue;
                }

                int p = getUniquePossibilityIndex(i);

                if (p != clue.getIndex()) {
                    continue;
                }

                // this cell is associated with this clue
                firstIndex = i;
                break;
            }

            if (firstIndex < 0) {
                continue;
            }

            // remove other possibilities:
            // all possibilities that are not glued to the cell
            // or too far are removed

            // moving to the left
            int min = clue.getMinI();
            int max = clue.getMaxI();

            boolean removeAll = false;
            for (int i = firstIndex - 1; i >= min; i--) {
                if (!removeAll) {
                    if (!possibilities[i][clue.getIndex()] || Math.abs(i - firstIndex) >= clue.getLength()) {
                        removeAll = true;
                        clue.setMinI(i + 1);
                    }
                }

                if (removeAll) {
                    possibilities[i][clue.getIndex()] = false;
                }
            }

            // moving to the right
            removeAll = false;
            for (int i = firstIndex + 1; i < max; i++) {
                if (!removeAll) {
                    if (!possibilities[i][clue.getIndex()] || Math.abs(i - firstIndex) >= clue.getLength()) {
                        removeAll = true;
                        clue.setMaxI(i);
                    }
                }

                if (removeAll) {
                    possibilities[i][clue.getIndex()] = false;
                }
            }
        }
    }

    /**
     * Cross every cell that have no possibility
     */
    protected void crossZeroCells() {
        for (int j = 0; j < possibilities.length; j++) {
            if (haveZeroPossibility(j)) {
                if (cells[j].isEmpty() || cells[j].isCrossed()) {
                    cells[j].set(Cell.CROSSED);
                } /*else {
                    throw new IllegalStateException();
                }*/
            }
        }
    }

    protected List<Region> split() {
        List<Region> regions = new ArrayList<>();

        Region current = null;
        for (int i = 0; i < cells.length; i++) {
            int first = firstPossibilityIndex(i);

            if (first >= 0) {
                if (current == null) {
                    current = new Region(this);
                    current.setStart(i);
                    current.setFirstClueIndex(first);
                }
            } else if (current != null) {
                current.setLastClueIndex(lastPossibilityIndex(i - 1) + 1);
                current.setEnd(i);
                regions.add(current);
                current = null;
            }
        }

        if (current != null) {
            current.setLastClueIndex(lastPossibilityIndex(cells.length - 1) + 1);
            current.setEnd(cells.length);
            regions.add(current);
        }

        return regions;
    }

    protected void tryFill() {
        // simple space technique
        /*clueIt.reset();

        while (clueIt.hasNext()) {
            int clue = clueIt.next();

            int minPossible = Integer.MAX_VALUE;
            int maxPossible = Integer.MIN_VALUE;

            for (int i = clueIt.getMinI(); i < clueIt.getMaxI(); i++) {
                if (possibilities[i][clueIt.getIndex()]) {
                    minPossible = Math.min(i, minPossible);
                    maxPossible = Math.max(i, maxPossible);
                }
            }

            int length = maxPossible - minPossible + 1;
            if (length < 2 * clue) {
                System.out.println("Filling...");
                System.out.printf("Clue: %d, between: %d and %d%n", clue, minPossible, maxPossible);

                int s = length - clue;

                for (int i = minPossible + s; i <= maxPossible - s; i++) {
                    cells[i].set(Cell.FILLED);
                }
            }
        }


        // glue
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].isFilled()) {
                int p = getUniquePossibility(i);

                if (p == -1) {
                    continue;
                }

                int right = distanceToWallRight(i);
                int left = distanceToWallLeft(i);

                System.out.printf("At i=%d. Dist to left: %d. Dist to right: %d%n", i, left, right);

                // -1 because I don't want to count the current cell
                if (right <= p - 1) {
                    int min = i - (p - right);
                    for (int j = i - 1; j >= min; j--) {
                        cells[j].set(Cell.FILLED);
                    }
                }

                if (left <= p - 1) {
                    int max = i + (p - left);

                    for (int j = i + 1; j < max; j++) {
                        cells[j].set(Cell.FILLED);
                    }
                }
            }
        }*/
    }

    /**
     * @return the distance from the cell and the nearest crossed cell on the right.
     * If there is no crossed cell. It returns the distance between the cell and the right of the nonogram.
     * The number is strictly superior to zero because the crossed cell is counted
     */
    private int distanceToWallRight(int cell) {
        for (int i = cell + 1; i < cells.length; i++) {
            if (cells[i].isCrossed()) {
                return i - cell;
            }
        }

        return cells.length - cell;
    }


    /**
     * @return the distance from the cell and the nearest crossed cell on the left.
     * If there is no crossed cell. It returns the distance between the cell and the left of the nonogram.
     * The number is strictly superior to zero because the crossed cell is counted
     */
    private int distanceToWallLeft(int cell) {
        for (int i = cell - 1; i >= 0; i--) {
            if (cells[i].isCrossed()) {
                return cell - i;
            }
        }

        return cell + 1;
    }

    /**
     * @return true if a line of length line can be put at the position i
     */
    private boolean fit(int line, int i) {
        if (i > 0 && cells[i - 1].isFilled()) {
            return false;
        } else if (i + line < cells.length && cells[i + line].isFilled()) {
            return false;
        } else {
            for (int j = 0; j < line; j++) {
                if (cells[i + j].isCrossed()) {
                    return false;
                }
            }

            return true;
        }
    }

    private int getUniquePossibilityIndex(int cell) {
        int possibility = -1;

        for (int i = 0; i < clues.length; i++) {
            if (possibilities[cell][i]) {
                if (possibility == -1) {
                    possibility = i;
                } else {
                    return -1;
                }
            }
        }

        return possibility;
    }

    private int firstPossibilityIndex(int cell) {
        for (int i = 0; i < clues.length; i++) {
            if (possibilities[cell][i]) {
                return i;
            }
        }

        return -1;
    }

    private int lastPossibilityIndex(int cell) {
        for (int i = clues.length - 1; i >= 0; i--) {
            if (possibilities[cell][i]) {
                return i;
            }
        }

        return -1;
    }

    private boolean haveZeroPossibility(int cell) {
        for (int i = 0; i < clues.length; i++) {
            if (possibilities[cell][i]) {
                return false;
            }
        }

        return true;
    }

    private void fill(int start, int end, Cell cell) {
        for (int i = start; i < end; i++) {
            cells[i].set(cell);
        }
    }

    private int getAvailableSpace() {
        int n = 0;

        for (CellWrapper cell : cells) {
            if (!cell.isCrossed()) {
                n++;
            }
        }

        return n;
    }

    private int length(int start, int to) {
        if (to <= start) {
            return 0;
        }

        int l = clues[to - 1].getLength();

        for (int i = start; i < to - 1; i++) {
            l += clues[i].getLength() + 1;
        }

        return l;
    }
    /**
     * @return the minimal number of cell needed to match the descriptor
     */
    public int descriptorLength() {
        return descriptorLength;
    }

    /**
     * @return the available space
     */
    public int size() {
        return cells.length;
    }

    public int nClues() {
        return clues.length;
    }

    /**
     * @return the clue with the highest number
     */
    public int getMaxClue() {
        if (maxClue < 0) {
            maxClue = 0;

            for (Clue clue : clues) {
                maxClue = Math.max(maxClue, clue.getLength());
            }
        }

        return maxClue;
    }

    public Clue[] getClues() {
        return clues;
    }

    public Clue getClue(int i) {
        return clues[i];
    }

    public boolean[][] getPossibilities() {
        return possibilities;
    }

    public CellWrapper[] getCells() {
        return cells;
    }
}
