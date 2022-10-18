package fr.poulpogaz.nonogramssolver;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ClueIterator implements Iterator<Integer> {

    private final Descriptor descriptor;

    private int index = 0;

    private int minI;
    private int maxI;

    public ClueIterator(Descriptor descriptor) {
        this.descriptor = descriptor;
        reset();
    }

    public void reset() {
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < descriptor.nClues();
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        int clue = descriptor.getClues()[index];

        if (index == 0) {
            minI = 0;
            maxI = descriptor.size() - (descriptor.descriptorLength() - clue);
        } else {
            int lastClue = descriptor.getClues()[index - 1];

            minI += lastClue + 1;
            maxI += clue + 1;
        }

        index++;

        return clue;
    }

    /**
     * Undefined behavior before call of {@link #next()}
     */
    public int getIndex() {
        return index - 1;
    }

    /**
     * Undefined behavior before call of {@link #next()}
     */
    public int getMinI() {
        return minI;
    }

    /**
     * Undefined behavior before call of {@link #next()}
     */
    public int getMaxI() {
        return maxI;
    }
}