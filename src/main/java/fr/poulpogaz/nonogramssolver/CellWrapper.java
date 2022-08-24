package fr.poulpogaz.nonogramssolver;

import java.util.ArrayList;
import java.util.List;

public class CellWrapper {

    private final List<CellListener> listeners = new ArrayList<>();
    private final int x;
    private final int y;

    private Cell content;

    public CellWrapper(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellWrapper(Cell content, int x, int y) {
        this.content = content;
        this.x = x;
        this.y = y;
    }

    public boolean isEmpty() {
        return content == Cell.EMPTY;
    }

    public boolean isFilled() {
        return content == Cell.FILLED;
    }

    public boolean isCrossed() {
        return content == Cell.CROSSED;
    }

    public boolean isNull() {
        return content == null;
    }

    public Cell get() {
        return content;
    }

    public void set(Cell content) {
        if (content != this.content) {
            Cell old = this.content;
            this.content = content;
            fireListeners(old);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addListener(CellListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CellListener listener) {
        listeners.remove(listener);
    }

    private void fireListeners(Cell oldValue) {
        for (CellListener listener : listeners) {
            listener.valueChange(this, oldValue);
        }
    }
}
