package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public abstract class SolverAdapter implements SolverListener {
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
    public void onSuccess(Nonogram n) {

    }

    @Override
    public void onFail(Nonogram n) {

    }
}
