package fr.poulpogaz.nonogramssolver;

import java.util.Objects;

public class CellWrapper {

    private final int x;
    private final int y;

    private Cell content;

    private Descriptor row;
    private Descriptor column;

    private int status = Status.NO_CHANGE;

    public CellWrapper(Cell content, int x, int y) {
        this.content = Objects.requireNonNull(content);
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty() {
        return content == Cell.EMPTY;
    }

    public boolean isFilled() {
        return content == Cell.FILLED;
    }

    public boolean isCrossed() {
        return content == Cell.CROSSED;
    }

    public Cell get() {
        return content;
    }

    public void setForce(Cell content) {
        this.content = content;
    }

    public void set(Cell content) {
        if (this.content != content) {
            if (this.content != null && this.content != Cell.EMPTY) {
                // throw new IllegalStateException("Changing cell at (%d; %d) from %s to %s".formatted(x, y, this.content, content));
                setContradiction();

                if (row != null) {
                    row.setContradiction();
                }
                if (column != null) {
                    column.setContradiction();
                }
            } else {
                this.content = content;
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

    public Descriptor getRow() {
        return row;
    }

    public void setRow(Descriptor row) {
        this.row = row;
    }

    public Descriptor getColumn() {
        return column;
    }

    public void setColumn(Descriptor column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
