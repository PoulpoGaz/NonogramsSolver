package fr.poulpogaz.nonogramssolver;

public class Row {

    private final int[] numbers;
    private final CellWrapper[] cells;

    public Row(int[] numbers, CellWrapper[] cells) {
        this.numbers = numbers;
        this.cells = cells;
    }

    public int length() {
        return cells.length;
    }

    public int nNumber() {
        return numbers.length;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public CellWrapper[] getCells() {
        return cells;
    }
}
