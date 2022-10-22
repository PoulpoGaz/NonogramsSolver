package fr.poulpogaz.nonogramssolver;

import java.util.*;

public class Descriptor extends AbstractRegion {

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


        start = 0;
        end = cells.length;
        firstClueIndex = 0;
        lastClueIndex = clues.length;
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    @Override
    public void trySolve() {
        if (clues.length == 0) {
            fill(0, cells.length, Cell.CROSSED);
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

        shrink();
        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        crossZeroCells();

        List<Region> regions = split();

        if (regions.size() > 1) {
            for (Region r : regions) {
                r.trySolve();
            }
        } else {
            tryFill();
        }
    }

    protected void shrink() {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].isCrossed()) {
                start = i + 1;
            } else {
                break;
            }
        }

        for (int i = cells.length - 1; i >= 0; i--) {
            if (cells[i].isCrossed()) {
                end = i;
            } else {
                break;
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

        int firstClue = 0;
        for (int i = 0; i < clues.length - 1; i++) {
            if (clues[i].getMaxI() <= clues[i + 1].getMinI()) { // no intersection => new region
                Region r = new Region(this);
                r.setFirstClueIndex(firstClue);
                r.setLastClueIndex(i + 1);
                r.setStart(clues[firstClue].getMinI());
                r.setEnd(clues[i].getMaxI());

                regions.add(r);

                firstClue = i + 1;
            }
        }

        if (firstClue < clues.length) {
            Region r = new Region(this);
            r.setFirstClueIndex(firstClue);
            r.setLastClueIndex(clues.length);
            r.setStart(clues[firstClue].getMinI());
            r.setEnd(clues[clues.length - 1].getMaxI());

            regions.add(r);
        }

        return regions;
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
    @Override
    public int descriptorLength() {
        return descriptorLength;
    }

    /**
     * @return the available space
     */
    @Override
    public int size() {
        return cells.length;
    }

    @Override
    protected CellWrapper getCell(int index) {
        return cells[index];
    }

    @Override
    protected void setCell(int index, Cell cell) {
        cells[index].set(cell);
    }

    @Override
    protected Clue getClue(int index) {
        return clues[index];
    }

    @Override
    protected void setPossibility(int cell, int clueIndex, boolean possibility) {
        possibilities[cell][clueIndex] = possibility;
    }

    @Override
    protected boolean possibility(int cell, int clueIndex) {
        return possibilities[cell][clueIndex];
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

    public boolean[][] getPossibilities() {
        return possibilities;
    }

    public CellWrapper[] getCells() {
        return cells;
    }
}
