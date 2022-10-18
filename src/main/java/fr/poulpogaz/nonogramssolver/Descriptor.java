package fr.poulpogaz.nonogramssolver;

import java.util.*;

public class Descriptor {

    /**
     * row if true, column if false
     */
    private final boolean isRow;
    private final int index;
    
    private final int[] clues;
    private final CellWrapper[] cells;

    /**
     * the sum of all numbers and the empty cell between them
     */
    private final int descriptorLength;
    private int maxClue = -1;

    private int step = 0;

    /**
     * For each cell, contains an array of length the number of clue
     * containing true if the i-th clue can be present at the cell
     */
    private final boolean[][] possibilities;
    private final ClueIterator clueIt;

    public Descriptor(boolean isRow, int index, int[] clues, CellWrapper[] cells) {
        this.isRow = isRow;
        this.index = index;
        this.clues = clues;
        this.cells = cells;

        descriptorLength = length(0, clues.length);
        maxClue = getMaxClue();

        possibilities = new boolean[cells.length][clues.length];

        for (int i = 0; i < cells.length; i++) {
            possibilities[i] = new boolean[clues.length];
        }

        clueIt = new ClueIterator(this);
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {
        Utils.fill(possibilities, false);
        int available = getAvailableSpace();
        int empty = available - descriptorLength;

        if (step == 0) {
            if (empty < maxClue) {
                int i = 0;
                for (int clue : clues) {

                    for (int k = i + empty; k < i + clue; k++) {
                        cells[k].set(Cell.FILLED);
                    }

                    i += clue + 1;
                }
            }

            step++;
        } else {
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

            computePossibilities();
            tryCross();
            tryFill();
        }

        return true;
    }

    private void computePossibilities() {
        System.out.println("-------------------------------------");
        System.out.printf("Row: %b. Index: %d%n", isRow, index);
        System.out.println("Clues: " + Arrays.toString(clues));

        clueIt.reset();

        while (clueIt.hasNext()) {
            int clue = clueIt.next();

            System.out.printf("Clue: %d. i=%d. max=%d%n", clue, clueIt.getMinI(), clueIt.getMaxI());
            int k = clueIt.getMinI();
            for (; k + clue < clueIt.getMaxI(); k++) {
                boolean fit = fit(clue, k);

                System.out.printf("Fit: %b. At %d%n", fit, k);
                if (fit) {
                    for (int l = k; l < k + clue; l++) {
                        possibilities[l][clueIt.getIndex()] = true;
                    }
                }
            }

            if (k < clueIt.getMaxI()) {
                boolean fit = fit(clue, clueIt.getMaxI() - clue);

                if (fit) {
                    for (int l = k; l < clueIt.getMaxI(); l++) {
                        possibilities[l][clueIt.getIndex()] = true;
                    }
                }
            }
        }
    }

    private void tryCross() {
        for (int j = 0; j < possibilities.length; j++) {
            if (haveZeroPossibility(j)) {
                if (cells[j].isEmpty() || cells[j].isCrossed()) {
                    cells[j].set(Cell.CROSSED);
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }

    private void tryFill() {

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

    private boolean haveZeroPossibility(int cell) {
        for (int i = 0; i < clues.length; i++) {
            if (possibilities[cell][i]) {
                return false;
            }
        }

        return true;
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

        int l = clues[to - 1];

        for (int i = start; i < to - 1; i++) {
            l += clues[i] + 1;
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
            for (int number : clues) {
                maxClue = Math.max(maxClue, number);
            }
        }

        return maxClue;
    }

    public int[] getClues() {
        return clues;
    }

    public CellWrapper[] getCells() {
        return cells;
    }
}
