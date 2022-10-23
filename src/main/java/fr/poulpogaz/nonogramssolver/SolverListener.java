package fr.poulpogaz.nonogramssolver;

import java.util.function.Consumer;

public interface SolverListener {

    SolverListener EMPTY_LISTENER = new SolverListener() {
        @Override
        public void onColumnTrySolve(Nonogram n, Descriptor d) {

        }

        @Override
        public void onRowTrySolve(Nonogram n, Descriptor d) {

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


    /**
     * Call by {@link Nonogram#solve(SolverListener)} after a call of {@link Descriptor#trySolve()}.
     * The descriptor is of course a column
     */
    void onColumnTrySolve(Nonogram n, Descriptor d);

    /**
     * Call by {@link Nonogram#solve(SolverListener)} after a call of {@link Descriptor#trySolve()}.
     * The descriptor is of course a row
     */
    void onRowTrySolve(Nonogram n, Descriptor d);

    /**
     * Call by {@link Nonogram#solve(SolverListener)} after that all columns and rows are processed
     */
    void onPassFinished(Nonogram n);

    void onSuccess(Nonogram n);

    void onFail(Nonogram n);
}
