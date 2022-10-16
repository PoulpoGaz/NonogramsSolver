package fr.poulpogaz.nonogramssolver;

public class Descriptor {

    /**
     * row if true, column if false
     */
    private final boolean isRow;
    
    private final int[] clues;
    private final CellWrapper[] cells;

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
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {

        return true;
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
