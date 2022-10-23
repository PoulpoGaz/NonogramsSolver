package fr.poulpogaz.nonogramssolver;

/**
 * A region is a part of a column/row that contains cell that shares the same possibilities
 */
public abstract class AbstractRegion {

    // included
    protected int start;
    // excluded
    protected int end;

    // included
    protected int firstClueIndex;
    // excluded
    protected int lastClueIndex;

    public void trySolve() {
        if (firstClueIndex == lastClueIndex) {
            fill(start, start - end, Cell.CROSSED);
            return;
        }

        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        crossZeroCells();
        tryFill();
    }

    protected void initClues() {
        int minI = start;
        int maxI = end - descriptorLength();

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue c = getClue(i);

            if (i == firstClueIndex) {
                maxI += c.getLength();
            } else {
                maxI += c.getLength() + 1;
                minI += getClue(i - 1).getLength() + 1;
            }

            c.setMinI(minI);
            c.setMaxI(maxI);
        }
    }

    /**
     * Compute possibilities. It doesn't take into account all information that gave the map.
     * For example, for 15 cells, clues 5 2 and a map looking like this: '  F    F    FF  ',
     * it won't realize that outside the first two F, it mustn't have a 5.
     */
    protected void computePossibilities() {
        clearPossibilitiesLocal();

        int forceMin = -1;
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int k = Math.max(clue.getMinI(), forceMin);
            for (; k + clue.getLength() < clue.getMaxI(); k++) {
                if (fit(clue.getLength(), k)) {
                    for (int l = k; l < k + clue.getLength(); l++) {
                        setPossibility(l, clue.getIndex(), true);
                    }
                }
            }

            if (k < clue.getMaxI()) {
                if (fit(clue.getLength(), clue.getMaxI() - clue.getLength())) {
                    for (int l = k; l < clue.getMaxI(); l++) {
                        setPossibility(l, clue.getIndex(), true);
                    }
                }
            }


            // recompute minI and maxI
            forceMin = -1;
            for (k = clue.getMinI(); k < clue.getMaxI() && !possibility(k, i); k++) {
                clue.setMinI(k + 1);
                forceMin = k + 3;
            }

            for (k = clue.getMaxI() - 1; k >= clue.getMinI() && !possibility(k, i); k--) {
                clue.setMaxI(k);
            }
        }
    }

    protected void clearPossibilitiesLocal() {
        for (int i = start; i < end; i++) {
            for (int j = firstClueIndex; j < lastClueIndex; j++) {
                setPossibility(i, j, false);
            }
        }
    }

    protected void optimizeCluesBoundWithOnePossibility() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            // find the first index (and last) that we are sure it is associated with the clue
            int firstIndex = -1;
            int lastIndex = -1;
            for (int j = clue.getMinI(); j < clue.getMaxI(); j++) {
                if (!getCell(j).isFilled()) {
                    continue;
                }

                int p = getUniquePossibilityIndex(j);

                if (p != clue.getIndex()) {
                    continue;
                }

                // this cell is associated with this clue
                if (firstIndex < 0) {
                    firstIndex = j;
                }
                lastIndex = j;
            }

            if (firstIndex < 0) {
                continue;
            }

            // remove other possibilities:
            // all possibilities that are not glued to the cell
            // or too far are removed

            // moving to the left
            int min = clue.getMinI();
            int max = clue.getMaxI();

            int maxDist = clue.getLength() - (lastIndex - firstIndex);

            boolean removeAll = false;
            for (int j = firstIndex - 1; j >= min; j--) {
                if (!removeAll) {
                    if (!possibility(j, clue.getIndex()) || Math.abs(j - firstIndex) >= maxDist) {
                        removeAll = true;
                        clue.setMinI(j + 1);
                    }
                }

                if (removeAll) {
                    setPossibility(j, clue.getIndex(), false);
                }
            }

            // moving to the right
            removeAll = false;
            for (int j = lastIndex + 1; j < max; j++) {
                if (!removeAll) {
                    if (!possibility(j, clue.getIndex()) || Math.abs(j - lastIndex) >= maxDist) {
                        removeAll = true;
                        clue.setMaxI(j);
                    }
                }

                if (removeAll) {
                    setPossibility(j, clue.getIndex(), false);
                }
            }
        }
    }

    /**
     * Cross every cell that have no possibility
     */
    protected void crossZeroCells() {
        for (int j = start; j < end; j++) {
            if (haveZeroPossibility(j)) {
                if (getCell(j).isEmpty() || getCell(j).isCrossed()) {
                    getCell(j).set(Cell.CROSSED);
                }
            }
        }
    }

    protected void tryFill() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int minPossible = Integer.MAX_VALUE;
            int maxPossible = Integer.MIN_VALUE;

            for (int j = clue.getMinI(); j < clue.getMaxI(); j++) {
                if (possibility(j, i)) {
                    minPossible = Math.min(j, minPossible);
                    maxPossible = Math.max(j, maxPossible);
                }
            }

            if (minPossible < start || minPossible >= end || maxPossible < start || maxPossible >= end) {
                continue;
            }

            int length = maxPossible - minPossible + 1;
            if (length < 2 * clue.getLength()) {
                System.out.println("Filling...");
                System.out.printf("Clue: %d, between: %d and %d%n", clue.getLength(), minPossible, maxPossible);

                int s = length - clue.getLength();

                for (int j = minPossible + s; j <= maxPossible - s; j++) {
                    setCell(j, Cell.FILLED);
                }
            }
        }
    }

    /**
     * @return true if a line of length line can be put at the position i
     */
    protected boolean fit(int line, int i) {
        if (i > 0 && getCell(i - 1).isFilled()) {
            return false;
        } else if (i + line < size() && getCell(i + line).isFilled()) {
            return false;
        } else {
            for (int j = 0; j < line; j++) {
                if (getCell(i + j).isCrossed()) {
                    return false;
                }
            }

            return true;
        }
    }

    protected int getUniquePossibilityIndex(int cell) {
        int possibility = -1;

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                if (possibility == -1) {
                    possibility = i;
                } else {
                    return -1;
                }
            }
        }

        return possibility;
    }

    protected int firstPossibilityIndex(int cell) {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                return i;
            }
        }

        return -1;
    }

    protected int lastPossibilityIndex(int cell) {
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (possibility(cell, i)) {
                return i;
            }
        }

        return -1;
    }

    protected boolean haveZeroPossibility(int cell) {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                return false;
            }
        }

        return true;
    }

    protected void fill(int start, int end, Cell cell) {
        for (int i = start; i < end; i++) {
            setCell(i, cell);
        }
    }

    /**
     * @return the minimal number of cell needed to match the descriptor
     */
    protected int descriptorLength() {
        int length = 0;

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            length += getClue(i).getLength();

            if (i + 1 < lastClueIndex) {
                length++;
            }
        }

        return length;
    }

    /**
     * @return the available space
     */
    protected int size() {
        return start - end;
    }

    protected abstract CellWrapper getCell(int index);

    protected abstract void setCell(int index, Cell cell);

    protected abstract Clue getClue(int index);

    protected abstract void setPossibility(int cell, int clueIndex, boolean possibility);

    protected abstract boolean possibility(int cell, int clueIndex);
}
