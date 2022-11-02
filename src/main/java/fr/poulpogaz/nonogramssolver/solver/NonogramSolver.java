package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.Nonogram;
import fr.poulpogaz.nonogramssolver.linesolver.DefaultLineSolver;
import fr.poulpogaz.nonogramssolver.linesolver.LineSolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.PriorityQueue;
import java.util.Stack;

public class NonogramSolver {

    private static final Logger LOGGER = LogManager.getLogger(NonogramSolver.class);

    private static final int SOLVED = 0;
    private static final int NOT_SOLVED = 1;
    private static final int CONTRADICTION = 2;

    private final LineSolver lineSolver = new DefaultLineSolver();
    private Nonogram nonogram;
    private SolverListener listener;

    private CellWrapper[][] cells;
    private Description[] rows;
    private Description[] columns;

    public NonogramSolver() {

    }

    public boolean solve(Nonogram nonogram, SolverListener listener, boolean contradiction, boolean recursive) {
        initSolver(nonogram, listener);

        try {
            Stack<Guess> guesses = new Stack<>();

            while (true) {
                int ret = solveWithLineSolver(guesses.size() > 0 ? SolverListener.RECURSION : SolverListener.LINE_SOLVING);

                if (ret == NOT_SOLVED) { // new guess
                    if (contradiction && guesses.isEmpty() && solveContradiction()) {
                        return true;
                    }
                    if (!recursive) {
                        return false;
                    }

                    Guess g = guess();

                    if (g == null) {
                        undoGuess(guesses);

                        if (guesses.isEmpty()) {
                            return false;
                        }
                    } else {
                        guesses.push(g);
                    }

                    LOGGER.debug("Guess stack size: {}", guesses.size());
                } else if (ret == CONTRADICTION) {
                    undoGuess(guesses);

                    if (guesses.isEmpty()) {
                        return false;
                    }
                } else if (ret == SOLVED) {
                    return true;
                }
            }

        } finally {
            LOGGER.debug("Solved? {}", isSolved());
            cleanSolver();
        }
    }

    private void initSolver(Nonogram nonogram, SolverListener listener) {
        this.nonogram = nonogram;
        this.listener = listener;

        cells = new CellWrapper[height()][width()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                cells[y][x] = new CellWrapper(nonogram, x, y);
            }
        }

        rows = new Description[height()];
        for (int y = 0; y < height(); y++) {
            rows[y] = new Description(true, y, nonogram.getRows()[y], cells[y]);
        }

        columns = new Description[width()];
        for (int x = 0; x < width(); x++) {
            CellWrapper[] wrappers = new CellWrapper[height()];

            for (int y = 0; y < height(); y++) {
                wrappers[y] = cells[y][x];
            }

            columns[x] = new Description(false, x, nonogram.getColumns()[x], wrappers);
        }
    }


    private int solveWithLineSolver(int mode) {
        PriorityQueue<Description> descriptions = new PriorityQueue<>(this::descriptionComparator);
        fillDescription(descriptions);

        while (!isSolved()) {
            boolean changed = false;

            while (!descriptions.isEmpty()) {
                Description desc = descriptions.poll();

                lineSolver.trySolve(desc);

                if (desc.hasContradiction()) {
                    return CONTRADICTION;
                }

                if (desc.hasChanged()) {
                    listener.onLineSolved(nonogram, desc, mode);
                    changed = true;
                }
            }

            listener.onPassFinished(nonogram, mode);

            if (!changed) {
                return NOT_SOLVED;
            } else {
                fillDescription(descriptions);
            }
        }

        return SOLVED;
    }

    private int descriptionComparator(Description a, Description b) {
        double sumA = (double) (a.descriptionLength() + a.countSolved()) / (2 * a.size());
        double sumB = (double) (b.descriptionLength() + b.countSolved()) / (2 * b.size());

        return Double.compare(sumA, sumB);
    }

    private void fillDescription(PriorityQueue<Description> descriptions) {
        for (Description row : rows) {
            if (row.hasChanged()) {
                descriptions.offer(row);
            }
        }

        for (Description col : columns) {
            if (col.hasChanged()) {
                descriptions.offer(col);
            }
        }
    }



    private boolean solveContradiction() {
        LOGGER.debug("Solving contradictions");

        PriorityQueue<Contradiction> queue = new PriorityQueue<>(this::contradictionComparator);
        fillContradiction(queue);

        boolean foundAContradiction;
        do {
            foundAContradiction = false;

            while (!queue.isEmpty()) {
                Contradiction c = queue.poll();

                if (!nonogram.isEmpty(c.x(), c.y())) { // not empty -> continue!
                    continue;
                }

                int ret = contradictionAt(c.x(), c.y());

                if (ret == CONTRADICTION) {
                    foundAContradiction = true;
                } else if (ret == SOLVED) {
                    return true;
                }

                listener.onContradiction(nonogram, ret == CONTRADICTION);

                ret = solveWithLineSolver(SolverListener.LINE_SOLVING);

                if (ret == CONTRADICTION) {
                    return false;
                } else if (ret == SOLVED) {
                    return true;
                }
            }

            if (foundAContradiction) {
                LOGGER.debug("Refilling contradictions");
                fillContradiction(queue);
            }

        } while (foundAContradiction);

        return false;
    }

    private int contradictionComparator(Contradiction a, Contradiction b) {
        int n = countAdjacentCellSolved(a.x(), a.y()) +
                rows[a.y()].countSolved() +
                columns[a.x()].countSolved();

        int n2 = countAdjacentCellSolved(b.x(), b.y()) +
                rows[b.y()].countSolved() +
                columns[b.x()].countSolved();

        return Integer.compare(n, n2);
    }

    private void fillContradiction(PriorityQueue<Contradiction> queue) {
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (cells[y][x].isEmpty()) {
                    queue.offer(new Contradiction(x, y));
                }
            }
        }
    }

    private int contradictionAt(int x, int y) {
        Cell[][] copy = copy();

        set(Cell.FILLED, x, y);

        int ret = solveWithLineSolver(SolverListener.CONTRADICTION);
        if (ret == NOT_SOLVED) {
            set(copy);
            set(Cell.CROSSED, x, y);

            ret = solveWithLineSolver(SolverListener.CONTRADICTION);
            if (ret == NOT_SOLVED) {
                set(copy);
            } else if (ret == CONTRADICTION) {
                set(copy);
                set(Cell.FILLED, x, y);
            }

        } else if (ret == CONTRADICTION) {
            set(copy);
            set(Cell.CROSSED, x, y);
        }

        return ret;
    }


    private Guess guess() {
        int xGuess = -1;
        int yGuess = -1;

        // bad guessing strategy
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (cells[y][x].isEmpty()) {
                    if (xGuess < 0) {
                        xGuess = x;
                        yGuess = y;
                    }

                    if (filled(x - 1, y) || filled(x + 1, y) || filled(x, y - 1) || filled(x, y + 1)) {
                        xGuess = x;
                        yGuess = y;
                        break;
                    }
                }
            }
        }

        if (xGuess < 0) {
            LOGGER.debug("Cannot guess: no empty cell");
            return null;
        }

        LOGGER.debug("Guessing {} at {} {}", Cell.FILLED, xGuess, yGuess);
        Cell[][] copy = copy();
        copy[yGuess][xGuess] = Cell.FILLED;

        set(Cell.FILLED, xGuess, yGuess);

        return new Guess(xGuess, yGuess, copy);
    }

    private void undoGuess(Stack<Guess> guesses) {
        while (!guesses.isEmpty()) {
            Guess g = guesses.peek();

            if (g.guess() == Cell.FILLED) {
                undoGuess(g);
                break;
            }

            guesses.pop();
        }
    }

    private void undoGuess(Guess guess) {
        LOGGER.debug("Undo guess: {} at ({}; {})", guess.guess(), guess.x(), guess.y());
        set(guess.cells());

        for (Description row : rows) {
            row.resetStatus();
        }

        for (Description cols : columns) {
            cols.resetStatus();
        }

        cells[guess.y()][guess.x()].setForce(Cell.CROSSED);
        cells[guess.y()][guess.x()].setChanged();
        columns[guess.x()].setChanged();
        rows[guess.y()].setChanged();

        guess.cells()[guess.y()][guess.x()] = Cell.CROSSED;
    }


    private int countAdjacentCellSolved(int x, int y) {
        int n = 0;

        if (x > 0) {
            if (!cells[y][x - 1].isEmpty()) {
                n++;
            }
        }

        if (x + 1 < width()) {
            if (!cells[y][x + 1].isEmpty()) {
                n++;
            }
        }


        if (y > 0) {
            if (!cells[y - 1][x].isEmpty()) {
                n++;
            }
        }

        if (y + 1 < height()) {
            if (!cells[y + 1][x].isEmpty()) {
                n++;
            }
        }

        return n;
    }

    private boolean filled(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return false;
        }

        return cells[y][x].isFilled();
    }

    private boolean crossed(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            return false;
        }

        return cells[y][x].isCrossed();
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
        this.listener = null;
    }

    private boolean isSolved() {
        for (Description col : columns) {
            if (!col.isCompleted()) {
                return false;
            }
        }

        for (Description row : rows) {
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

    private int width() {
        return nonogram.getWidth();
    }

    private int height() {
        return nonogram.getHeight();
    }

    private record Contradiction(int x, int y) {}
}
