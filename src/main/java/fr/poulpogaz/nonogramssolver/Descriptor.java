package fr.poulpogaz.nonogramssolver;

import java.util.*;

public class Descriptor implements CellListener {

    /**
     * row if true, column if false
     */
    private final boolean isRow;
    
    private final int[] clues;
    private final CellWrapper[] cells;

    private final List<Region> regions = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();

    /**
     * the sum of all numbers and the empty cell between them
     */
    private final int descriptorLength;
    private int maxClue;

    public Descriptor(int[] clues, CellWrapper[] cells, boolean isRow) {
        this.isRow = isRow;
        this.clues = clues;
        this.cells = cells;

        descriptorLength = length(0, clues.length);
        maxClue = getMaxClue();

        regions.add(new Region(this, 0, cells.length - 1));

        for (CellWrapper wrapper : cells) {
            wrapper.addListener(this);
        }
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {
        if (regions.isEmpty()) { // the row is fully crossed
            return false;
        }

        return true;
    }


    @Override
    public void valueChange(CellWrapper wrapper, Cell oldValue) {
        if (oldValue == Cell.CROSSED || oldValue == Cell.FILLED) { // this means that a contradiction has been found
            throw new IllegalStateException();
        }

        /*int n;
        if (isRow) {
            n = wrapper.getX();
        } else {
            n = wrapper.getY();
        }
        Region region = Objects.requireNonNull(getRegion(n));

        if (wrapper.isFilled()) { // add a new line, merge two lines or continue a line
            // region.fill(n);
            
        } else if (wrapper.isCrossed()) { // split region
            //region.split(n);
        }*/
    }

    /**
     * @return the region that contains the n-th cell
     */
    private Region getRegion(int n) {
        for (Region region : regions) {
            if (region.start() <= n) {
                if (n < region.start() + region.end()) {
                    return region;
                } else {
                    return null; // outside
                }
            }
        }

        return null;
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
