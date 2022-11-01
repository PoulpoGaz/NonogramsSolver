package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.Nonogram;

import java.util.Objects;

public class CellWrapper {

    private final int x;
    private final int y;
    private final Nonogram nonogram;

    private Description row;
    private Description column;

    private int status = Status.NO_CHANGE;

    public CellWrapper(Nonogram nonogram, int x, int y) {
        this.nonogram = Objects.requireNonNull(nonogram);
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty() {
        return nonogram.isEmpty(x, y);
    }

    public boolean isFilled() {
        return nonogram.isFilled(x, y);
    }

    public boolean isCrossed() {
        return nonogram.isCrossed(x, y);
    }

    public Cell get() {
        return nonogram.get(x, y);
    }

    public void setForce(Cell cell) {
        nonogram.set(cell, x, y);
    }

    public void set(Cell cell) {
        Cell current = nonogram.get(x, y);

        if (current != cell) {
            if (current != null && current != Cell.EMPTY) {
                // throw new IllegalStateException("Changing cell at (%d; %d) from %s to %s".formatted(x, y, this.cell, cell));
                setContradiction();

                if (row != null) {
                    row.setContradiction();
                }
                if (column != null) {
                    column.setContradiction();
                }
            } else {
                nonogram.set(cell, x, y);
                setChanged();

                if (row != null) {
                    row.setChanged();
                }
                if (column != null) {
                    column.setChanged();
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public void resetStatus() {
        status = Status.NO_CHANGE;
    }

    public Description getRow() {
        return row;
    }

    public void setRow(Description row) {
        this.row = row;
    }

    public Description getColumn() {
        return column;
    }

    public void setColumn(Description column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return nonogram.get(x, y).toString();
    }
}
