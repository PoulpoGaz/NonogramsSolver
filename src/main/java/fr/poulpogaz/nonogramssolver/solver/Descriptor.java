package fr.poulpogaz.nonogramssolver.solver;

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

    private boolean changed = true;

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

         for (CellWrapper w : cells) {
            if (isRow) {
                w.setRow(this);
            } else {
                w.setColumn(this);
            }
        }


        start = 0;
        end = cells.length;
        firstClueIndex = 0;
        lastClueIndex = clues.length;
    }

    @Override
    public void trySolve() {
        if (!changed) {
            return;
        }
        changed = false;

        if (clues.length == 0) {
            draw(0, cells.length, Cell.CROSSED);
            return;
        }

        System.out.println("-------------------------------------");
        System.out.printf("Row: %b. Index: %d%n", isRow, index);
        System.out.println("Clues: " + Arrays.toString(clues));

        shrink();
        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        List<Line> lines = createLines();
        comparePossibilitiesAndLines(lines);
        crossZeroCells();

        List<Region> regions = split();

        if (regions.size() > 1) {
            for (Region r : regions) {
                r.trySolve();
            }
        } else {
            tryFill(lines);
        }
    }

    protected void shrink() {
        for (int i = 0; i < cells.length; i++) {
            if (isCrossed(i)) {
                start = i + 1;
            } else {
                break;
            }
        }

        for (int i = cells.length - 1; i >= 0; i--) {
            if (isCrossed(i)) {
                end = i;
            } else {
                break;
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

    public boolean isCompleted() {
        if (clues.length == 0) {
            for (CellWrapper cell : cells) {
                if (cell.isFilled()) {
                    return false;
                }
            }

            return true;
        } else {
            int clueIndex = 0;
            int count = 0;

            for (CellWrapper cell : cells) {
                if (clueIndex >= clues.length) {
                    if (cell.isFilled()) {
                        return false;
                    }
                } else {
                    if (cell.isFilled()) {
                        count++;

                        if (count > getClueLength(clueIndex)) { // invalid
                            return false;
                        }
                    } else if (count > 0) {
                        if (count != getClueLength(clueIndex)) {
                            return false;
                        }

                        clueIndex++;
                        count = 0;
                    }
                }
            }

            if (clueIndex == clues.length) {
                return true;
            } else {
                return getClueLength(clueIndex) == count;
            }
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

        int l = getClueLength(to - 1);

        for (int i = start; i < to - 1; i++) {
            l += getClueLength(i) + 1;
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

    public boolean isRow() {
        return isRow;
    }

    public int getIndex() {
        return index;
    }

    public void setHasChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean hasChanged() {
        return changed;
    }
}
