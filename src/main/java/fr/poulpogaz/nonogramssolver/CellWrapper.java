package fr.poulpogaz.nonogramssolver;

public class CellWrapper {

    private final int x;
    private final int y;

    private Cell content;

    public CellWrapper(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellWrapper(Cell content, int x, int y) {
        this.content = content;
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

    public boolean isNull() {
        return content == null;
    }

    public Cell get() {
        return content;
    }

    public void set(Cell content) {
        this.content = content;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
