package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public interface SolverListener {

    SolverListener EMPTY_LISTENER = new SolverListener() {
        @Override
        public void onLineSolved(Nonogram n, Description d) {

        }

        @Override
        public void onPassFinished(Nonogram n) {

        }

        @Override
        public void onFail(Nonogram n) {

        }

        @Override
        public void onSuccess(Nonogram n) {

        }
    };

    void onLineSolved(Nonogram n, Description d);

    void onPassFinished(Nonogram n);

    void onSuccess(Nonogram n);

    void onFail(Nonogram n);
}
