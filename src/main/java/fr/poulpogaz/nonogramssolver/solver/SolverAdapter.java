package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public abstract class SolverAdapter implements SolverListener {
    @Override
    public void onLineSolved(Nonogram n, Description d) {

    }

    @Override
    public void onPassFinished(Nonogram n) {

    }

    @Override
    public void onSuccess(Nonogram n) {

    }

    @Override
    public void onFail(Nonogram n) {

    }
}
