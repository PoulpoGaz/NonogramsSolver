package fr.poulpogaz.nonogramssolver.solver;

import java.util.Objects;

public class CellWrapper {

    private final int x;
    private final int y;

    private Cell content;

    private Descriptor row;
    private Descriptor column;

    private boolean hasChanged;

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

    public void set(Cell content) {
        if (this.content != content) {
            if (this.content != null && this.content != Cell.EMPTY) {
                throw new IllegalStateException("Changing cell at (%d; %d) from %s to %s".formatted(x, y, this.content, content));
            }

            this.content = content;
            hasChanged = true;

            if (row != null) {
                row.setHasChanged(true);
            }
            if (column != null) {
                column.setHasChanged(true);
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
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
