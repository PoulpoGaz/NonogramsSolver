package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public class Description {

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
    private final int descriptionLength;
    private int maxClue = -1;

    private int status = Status.CHANGED;

    public Description(boolean isRow, int index, Nonogram.Clue[] clues, CellWrapper[] cells) {
        this.isRow = isRow;
        this.index = index;
        this.cells = cells;
        this.clues = new Clue[clues.length];

        for (int i = 0; i < clues.length; i++) {
            this.clues[i] = new Clue(clues[i], i);
        }

        descriptionLength = length(0, clues.length);
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

    public boolean isFilled(int i, int color) {
        return cells[i].isFilled(color);
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
            int pos = 0;
            for (int i = 0; i < clues.length; i++) {
                Clue clue = clues[i];
                pos = skipNotFilled(pos);

                if (pos < 0) {
                    return false;
                }

                int length = lineLength(pos, clue.getColor());

                if (length != clue.getLength()) {
                    return false;
                }
                pos += length;
            }

            return skipNotFilled(pos) == -1;
        }
    }

    private int skipNotFilled(int i) {
        for (; i < cells.length; i++) {
            if (cells[i].isFilled()) {
                return i;
            }
        }

        return -1;
    }

    private int lineLength(int pos, int color) {
        int length = 0;
        for (; pos < cells.length; pos++) {
            if (cells[pos].isFilled(color)) {
                length++;
            } else {
                break;
            }
        }

        return length;
    }


    public int countSolved() {
        int n = 0;

        for (CellWrapper cell : cells) {
            if (!cell.isEmpty()) {
                n++;
            }
        }

        return n;
    }

    public int countChanged() {
        int n = 0;

        for (CellWrapper cell : cells) {
            if (cell.hasChanged()) {
                n++;
            }
        }

        return n;
    }

    public double solvedRate() {
        return (double) countSolved() / cells.length;
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
     * @return the minimal number of cell needed to match the description
     */
    public int descriptionLength() {
        return descriptionLength;
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

    public void setCell(int index, int type) {
        cells[index].set(type, 0);
    }

    public void setCell(int index, int type, int color) {
        cells[index].set(type, color);
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
