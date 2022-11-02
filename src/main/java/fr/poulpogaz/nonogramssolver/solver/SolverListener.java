package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public interface SolverListener {

    int LINE_SOLVING = 0;
    int CONTRADICTION = 1;
    int RECURSION = 2;


    SolverListener EMPTY_LISTENER = new SolverListener() {
        @Override
        public void onLineSolved(Nonogram n, Description d, int mode) {

        }

        @Override
        public void onPassFinished(Nonogram n, int mode) {

        }

        @Override
        public void onContradiction(Nonogram n, boolean found) {

        }

        @Override
        public void onFail(Nonogram n) {

        }

        @Override
        public void onSuccess(Nonogram n) {

        }
    };

    /**
     * Call when {@link fr.poulpogaz.nonogramssolver.linesolver.LineSolver#trySolve(Description)} is used
     * @param n the nonogram that is being solved
     * @param d the description which was solved
     * @param mode one of {@link #LINE_SOLVING}, {@link #CONTRADICTION} or {@link #RECURSION}.
     *             It tells for which algorithm the line solver was used.
     */
    void onLineSolved(Nonogram n, Description d, int mode);

    void onPassFinished(Nonogram n, int mode);

    /**
     * Call when {@link NonogramSolver} finished testing a cell to find a contradiction. However, it is not
     * call when after testing a cell, the nonogram was solved!
     * @param n the nonogram that is being solved
     * @param found if a contradiction has been found
     */
    void onContradiction(Nonogram n, boolean found);

    void onSuccess(Nonogram n);

    void onFail(Nonogram n);
}
