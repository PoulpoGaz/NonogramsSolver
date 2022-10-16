package fr.poulpogaz.nonogramssolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public Descriptor(boolean isRow, int index, int[] clues, CellWrapper[] cells) {
        this.isRow = isRow;
        this.index = index;
        this.clues = clues;
        this.cells = cells;

        descriptorLength = length(0, clues.length);
        maxClue = getMaxClue();
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {
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

            Possibilities[] possibilities = new Possibilities[cells.length];

            for (int i = 0; i < cells.length; i++) {
                possibilities[i] = new Possibilities();
            }

            System.out.println("-------------------------------------");
            System.out.printf("Row: %b. Index: %d%n", isRow, index);
            System.out.println("Clues: " + Arrays.toString(clues));
            int i = 0;
            int remaining = descriptorLength;

            for (int k = 0; k < clues.length; k++) {
                int clue = clues[k];
                remaining = Math.max(remaining - clue - 1, 0);

                int max = cells.length - remaining;

                if (k + 1 < clues.length) {
                    max--;
                }

                System.out.printf("Clue: %d. i=%d. max=%d (remaining=%d)%n", clue, i, max, remaining);
                int j = i;
                for (; j + clue < max; j++) {
                    boolean fit = fit(clue, j);

                    System.out.printf("Fit: %b. At %d%n", fit, j);
                    if (fit) {
                        for (int l = j; l < j + clue; l++) {
                            possibilities[l].add(clue);
                        }
                    }
                }

                if (j < max) {
                    boolean fit = fit(clue, max - clue);

                    if (fit) {
                        for (int l = j; l < max; l++) {
                            possibilities[l].add(clue);
                        }
                    }
                }

                i += clue + 1;
            }

            for (int j = 0; j < possibilities.length; j++) {
                Possibilities p = possibilities[j];
                if (p.isEmpty()) {
                    if (cells[j].isEmpty() || cells[j].isCrossed()) {
                        cells[j].set(Cell.CROSSED);
                    }
                }
            }
        }

        return true;
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

    private static class Possibilities extends ArrayList<Integer> {

    }
}
