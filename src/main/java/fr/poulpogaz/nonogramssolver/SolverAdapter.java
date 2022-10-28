package fr.poulpogaz.nonogramssolver;

public abstract class SolverAdapter implements SolverListener {
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
    public void onSuccess(Nonogram n) {

    }

    @Override
    public void onFail(Nonogram n) {

    }
}
