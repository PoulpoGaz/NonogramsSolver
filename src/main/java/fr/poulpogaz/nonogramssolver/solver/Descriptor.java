package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;

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

    private int status = Status.CHANGED;

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

         for (CellWrapper w : cells) {
            if (isRow) {
                w.setRow(this);
            } else {
                w.setColumn(this);
            }
        }
    }

    public boolean isFilled(int i) {
        return cells[i].isFilled();
    }

    public boolean isCrossed(int i) {
        return cells[i].isCrossed();
    }

    public boolean isEmpty(int i) {
        return cells[i].isEmpty();
    }

    public int getClueLength(int i) {
        return clues[i].getLength();
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

                        if (count > getClue(clueIndex).getLength()) { // invalid
                            return false;
                        }
                    } else if (count > 0) {
                        if (count != getClue(clueIndex).getLength()) {
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
                return getClue(clueIndex).getLength() == count;
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

        int l = getClue(to - 1).getLength();

        for (int i = start; i < to - 1; i++) {
            l += getClue(i).getLength() + 1;
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

    public CellWrapper getCell(int index) {
        return cells[index];
    }

    public void setCell(int index, Cell cell) {
        cells[index].set(cell);
    }

    public Clue getClue(int index) {
        return clues[index];
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

    public CellWrapper[] getCells() {
        return cells;
    }

    public boolean isRow() {
        return isRow;
    }

    public int getIndex() {
        return index;
    }

    public void setChanged() {
        status = status | Status.CHANGED;
    }

    public void setContradiction() {
        status = status | Status.CONTRADICTION;
    }

    public boolean hasChanged() {
        return Status.hasChanged(status);
    }

    public boolean hasContradiction() {
        return Status.hasContradiction(status);
    }

    public int getStatus() {
        return status;
    }

    public void resetStatus() {
        status = Status.NO_CHANGE;
    }
}
