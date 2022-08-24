package fr.poulpogaz.nonogramssolver;

public interface CellListener {

    void valueChange(CellWrapper wrapper, Cell oldValue);
}
