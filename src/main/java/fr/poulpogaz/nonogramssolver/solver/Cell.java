package fr.poulpogaz.nonogramssolver.solver;

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
