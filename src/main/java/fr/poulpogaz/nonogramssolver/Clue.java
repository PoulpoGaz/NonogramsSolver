package fr.poulpogaz.nonogramssolver;

public class Clue {

    private final int length;
    private final int index;
    private int minI;
    private int maxI;

    private boolean completed;

    public Clue(int length, int index) {
        this.length = length;
        this.index = index;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getLength() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    /**
     * inclusive
     */
    public int getMinI() {
        return minI;
    }

    public void setMinI(int minI) {
        this.minI = minI;
    }

    /**
     * exclusive
     */
    public int getMaxI() {
        return maxI;
    }

    public void setMaxI(int maxI) {
        this.maxI = maxI;
    }

    @Override
    public String toString() {
        return "Clue{" +
                "length=" + length +
                ", index=" + index +
                ", minI=" + minI +
                ", maxI=" + maxI +
                ", completed=" + completed +
                '}';
    }
}
