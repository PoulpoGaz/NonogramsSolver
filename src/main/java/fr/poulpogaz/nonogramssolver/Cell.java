package fr.poulpogaz.nonogramssolver;

/**
 * A cell cna be empty, filled for a particular color or crossed
 */
public class Cell {

    public static final int EMPTY = 0;
    public static final int FILLED = 1;
    public static final int CROSSED = 2;

    private int type;
    private int color;

    /**
     * Creates an empty cell
     */
    public Cell() {
        type = EMPTY;
    }

    public Cell(Cell cell) {
        set(cell);
    }

    public void set(Cell cell) {
        set(cell.type, cell.color);
    }

    public void set(int type, int color) {
        this.type = type;
        this.color = color;
    }

    public void setEmpty() {
        type = EMPTY;
    }

    public void setFilled() {
        setFilled(0);
    }

    public void setFilled(int color) {
        type = FILLED;
        this.color = color;
    }

    public void setCrossed() {
        type = CROSSED;
    }

    public boolean isEmpty() {
        return type == EMPTY;
    }

    public boolean isFilled() {
        return type == FILLED;
    }

    public boolean isCrossed() {
        return type == CROSSED;
    }

    public int getColor() {
        return color;
    }

    public int getType() {
        return type;
    }

    public char getChar() {
        return switch (type) {
            case CROSSED -> 'X';
            case EMPTY -> ' ';
            case FILLED -> '█';
            default -> throw new IllegalStateException();
        };
    };
}
