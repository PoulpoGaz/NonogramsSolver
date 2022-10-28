package fr.poulpogaz.nonogramssolver;

public enum Cell {

    EMPTY(' '),
    FILLED('█'),
    CROSSED('X');

    private final char c;

    Cell(char c) {
        this.c = c;
    }

    public char getChar() {
        return c;
    }

    public static Cell valueOf(char c) {
        return switch (c) {
            case '_', ' ' -> Cell.EMPTY;
            case '█', 'F' -> Cell.FILLED;
            case 'X' -> Cell.CROSSED;
            default -> throw new IllegalArgumentException();
        };
    }
}
