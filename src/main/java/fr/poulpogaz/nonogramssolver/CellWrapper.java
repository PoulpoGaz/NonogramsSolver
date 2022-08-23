package fr.poulpogaz.nonogramssolver;

public class CellWrapper {

    private Cell content;

    public CellWrapper() {
    }

    public CellWrapper(Cell content) {
        this.content = content;
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

    public boolean isNull() {
        return content == null;
    }

    public Cell get() {
        return content;
    }

    public void set(Cell content) {
        this.content = content;
    }
}
