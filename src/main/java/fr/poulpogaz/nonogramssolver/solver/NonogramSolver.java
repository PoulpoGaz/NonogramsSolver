package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.Nonogram;
import fr.poulpogaz.nonogramssolver.linesolver.DefaultLineSolver;
import fr.poulpogaz.nonogramssolver.linesolver.LineSolver;

import java.util.Stack;

public class NonogramSolver {

    private static final int SOLVED = 0;
    private static final int NOT_SOLVED = 1;
    private static final int CONTRADICTION = 2;

    private Nonogram nonogram;

    private CellWrapper[][] cells;
    private Descriptor[] rows;
    private Descriptor[] columns;

    public NonogramSolver() {

    }

    public boolean solve(Nonogram nonogram, SolverListener listener, boolean contradiction, boolean recursive) {
        initSolver(nonogram);

        try {
            Stack<Guess> guesses = new Stack<>();

            LineSolver solver = new DefaultLineSolver();
            while (true) {
                int ret = solveWithLineSolver(solver);

                if (ret == NOT_SOLVED) { // new guess
                    /*if (solveContradiction(solver)) {
                        return true;
                    }*/
                    Guess g = guess();

                    guesses.push(g);
                } else if (ret == CONTRADICTION) {
                    System.out.println("Undo guess");
                    while (!guesses.isEmpty()) {
                        Guess g = guesses.peek();

                        if (g.guess() == Cell.FILLED) {
                            undoGuess(g);
                            break;
                        }

                        guesses.pop();
                    }

                    if (guesses.isEmpty()) {
                        return false;
                    }
                } else if (ret == SOLVED) {
                    return true;
                }
            }

        } finally {
            cleanSolver();
        }
    }

    private void initSolver(Nonogram nonogram) {
        this.nonogram = nonogram;

        cells = new CellWrapper[height()][width()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                cells[y][x] = new CellWrapper(nonogram, x, y);
            }
        }

        rows = new Descriptor[height()];
        for (int y = 0; y < height(); y++) {
            rows[y] = new Descriptor(true, y, nonogram.getRows()[y], cells[y]);
        }

        columns = new Descriptor[width()];
        for (int x = 0; x < width(); x++) {
            CellWrapper[] wrappers = new CellWrapper[height()];

            for (int y = 0; y < height(); y++) {
                wrappers[y] = cells[y][x];
            }

            columns[x] = new Descriptor(false, x, nonogram.getColumns()[x], wrappers);
        }
    }


    private int solveWithLineSolver(LineSolver solver) {
        while (!isSolved()) {
            boolean changed = false;
            for (Descriptor col : columns) {
                if (col.hasChanged()) {
                    solver.trySolve(col);

                    if (col.hasContradiction()) {
                        return CONTRADICTION;
                    }

                    changed = true;
                }
            }

            for (Descriptor row : rows) {
                if (row.hasChanged()) {
                    solver.trySolve(row);

                    if (row.hasContradiction()) {
                        return CONTRADICTION;
                    }

                    changed = true;
                }
            }

            if (!changed) {
                return NOT_SOLVED;
            }
        }

        return SOLVED;
    }

    private boolean solveContradiction(LineSolver solver) {
        System.out.println("Solving contradiction");

        boolean foundAContradiction;
        do {
            foundAContradiction = false;

            for (int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    int ret = contradictionAt(solver, x, y);

                    if (ret == CONTRADICTION) {
                        foundAContradiction = true;
                    } else if (ret == SOLVED) {
                        return true;
                    }
                }
            }

            if (foundAContradiction) {
                System.out.println("redo");
            }

        } while (foundAContradiction);

        return false;
    }

    private int contradictionAt(LineSolver solver, int x, int y) {
        Cell[][] copy = copy();

        set(Cell.FILLED, x, y);

        int ret = solveWithLineSolver(solver);
        if (ret == NOT_SOLVED) {
            set(copy);
            set(Cell.CROSSED, x, y);

            ret = solveWithLineSolver(solver);
            if (ret == NOT_SOLVED) {
                set(copy);
            } else if (ret == CONTRADICTION) {
                // something weird is happening
            }

        } else if (ret == CONTRADICTION) {
            set(copy);
            set(Cell.CROSSED, x, y);
        }

        return ret;
    }



    private Guess guess() {
        System.out.println("Guessing");
        int xFirst = -1;
        int yFirst = -1;

        // bad guessing strategy
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (cells[y][x].isEmpty()) {
                    xFirst = x;
                    yFirst = y;

                    if (filled(x - 1, y) || filled(x + 1, y) || filled(x, y - 1) || filled(x, y + 1)) {
                        Cell[][] copy = copy();
                        copy[y][x] = Cell.FILLED;

                        set(Cell.FILLED, x, y);

                        return new Guess(x, y, copy);
                    }
                }
            }
        }

        if (xFirst < 0) {
            return null;
        }

        Cell[][] copy = copy();
        copy[yFirst][xFirst] = Cell.FILLED;

        set(Cell.FILLED, xFirst, yFirst);

        return new Guess(xFirst, yFirst, copy);
    }

    private boolean filled(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return false;
        }

        return cells[y][x].isFilled();
    }

    private void undoGuess(Guess guess) {
        //System.out.printf("Undo guess %s at (%d; %d)%n", Cell.FILLED, guess.x(), guess.y());
        set(guess.cells());

        for (Descriptor row : rows) {
            row.resetStatus();
        }

        for (Descriptor cols : columns) {
            cols.resetStatus();
        }

        cells[guess.y()][guess.x()].setForce(Cell.CROSSED);
        cells[guess.y()][guess.x()].setChanged();
        columns[guess.x()].setChanged();
        rows[guess.y()].setChanged();

        guess.cells()[guess.y()][guess.x()] = Cell.CROSSED;
    }

    private void set(Cell[][] cells) {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                this.cells[y][x].setForce(cells[y][x]);
                this.cells[y][x].resetStatus();
            }
        }
    }

    private void set(Cell cell, int x, int y) {
        cells[y][x].setForce(cell);
        cells[y][x].setChanged();
        columns[x].setChanged();
        rows[y].setChanged();
    }
    

    private void cleanSolver() {
        this.nonogram = null;
        this.cells = null;
        this.columns = null;
        this.rows = null;
    }

    private boolean isSolved() {
        for (Descriptor col : columns) {
            if (!col.isCompleted()) {
                return false;
            }
        }

        for (Descriptor row : rows) {
            if (!row.isCompleted()) {
                return false;
            }
        }

        return true;
    }

    public Cell[][] copy() {
        Cell[][] cells = new Cell[height()][width()];

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                cells[y][x] = this.cells[y][x].get();
            }
        }

        return cells;
    }

    private int height() {
        return nonogram.getHeight();
    }

    private int width() {
        return nonogram.getWidth();
    }
}
