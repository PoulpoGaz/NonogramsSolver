package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Nonogram;

public class Clue {

    private final int length;
    private final int color;
    private final int index;
    private int minI;
    private int maxI;

    public Clue(Nonogram.Clue clue, int index) {
        this.length = clue.length();
        this.color = clue.color();
        this.index = index;
    }

    public Clue(int length, int color, int index) {
        this.length = length;
        this.color = color;
        this.index = index;
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

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Clue{" +
                "length=" + length +
                ", color=" + color +
                ", index=" + index +
                ", minI=" + minI +
                ", maxI=" + maxI +
                '}';
    }
}
