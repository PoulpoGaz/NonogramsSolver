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
}
