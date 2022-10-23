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
                minI += getClueLength(i - 1) + 1;
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
        clearPossibilities();

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int j = clue.getMinI();
            for (; j + clue.getLength() < clue.getMaxI(); j++) {
                if (fit(clue.getLength(), j)) {
                    markPossible(j, clue);
                }
            }

            if (fit(clue.getLength(), clue.getMaxI() - clue.getLength())) {
                markPossible(j, clue);
            }


            // recompute minI and maxI
            for (j = clue.getMinI(); j < clue.getMaxI() && !possibility(j, i); j++) {
                clue.setMinI(j + 1);
            }

            // update if necessary next clue's minI
            // it may happen if the space between current clue's minI and next clue's minI is insufficient
            if (i + 1 < lastClueIndex) {
                Clue next = getClue(i + 1);

                next.setMinI(Math.max(clue.getMinI() + clue.getLength() + 1, next.getMinI()));
            }

            for (j = clue.getMaxI() - 1; j >= clue.getMinI() && !possibility(j, i); j--) {
                clue.setMaxI(j);
            }
        }

        // update max
        for (int i = lastClueIndex - 1; i > 0; i--) {
            Clue clue = getClue(i);
            Clue previous = getClue(i - 1);

            if (clue.getMaxI() - previous.getMaxI() < clue.getLength()) {
                int last = previous.getMaxI();
                previous.setMaxI(clue.getMaxI() - clue.getLength() - 1);

                for (int j = previous.getMaxI(); j < last; j++) {
                    setPossibility(j, i - 1, false);
                }
            }
        }
    }

    protected void clearPossibilities() {
        for (int i = start; i < end; i++) {
            for (int j = firstClueIndex; j < lastClueIndex; j++) {
                setPossibility(i, j, false);
            }
        }
    }

    /**
     * Mark the cell from i to i + clue.getLength() as possible for the specified clue
     */
    protected void markPossible(int i, Clue clue) {
        for (int j = i; j < i + clue.getLength(); j++) {
            setPossibility(j, clue.getIndex(), true);
        }
    }

    protected void optimizeCluesBoundWithOnePossibility() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            // find the first index (and last) that we are sure it is associated with the clue
            int firstIndex = -1;
            int lastIndex = -1;
            for (int j = clue.getMinI(); j < clue.getMaxI(); j++) {
                if (!isFilled(j)) {
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
                if (isEmpty(j)) {
                    getCell(j).set(Cell.CROSSED);
                }
            }
        }
    }

    /**
     * @return true if a line of length line can be put at the position i
     */
    protected boolean fit(int line, int i) {
        if (i > 0 && isFilled(i - 1)) {
            return false;
        } else if (i + line < size() && isFilled(i + line)) {
            return false;
        } else {
            for (int j = 0; j < line; j++) {
                if (isCrossed(i + j)) {
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
                int s = length - clue.getLength();

                fill(minPossible + s, maxPossible - s + 1, Cell.FILLED);
            }
        }
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
            length += getClueLength(i);

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

    protected boolean isFilled(int i) {
        return getCell(i).isFilled();
    }

    protected boolean isCrossed(int i) {
        return getCell(i).isCrossed();
    }

    protected boolean isEmpty(int i) {
        return getCell(i).isEmpty();
    }

    protected int getClueLength(int i) {
        return getClue(i).getLength();
    }

    protected abstract CellWrapper getCell(int index);

    protected abstract void setCell(int index, Cell cell);

    protected abstract Clue getClue(int index);

    protected abstract void setPossibility(int cell, int clueIndex, boolean possibility);

    protected abstract boolean possibility(int cell, int clueIndex);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("start=").append(start)
                .append(", end=").append(end)
                .append(", firstClueIndex=").append(firstClueIndex)
                .append(", lastClueIndex=").append(lastClueIndex)
                .append(System.lineSeparator());

        sb.append("Clues=[");
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            sb.append(getClue(i));

            if (i + 1 < lastClueIndex) {
                sb.append(", ");
            }
        }
        sb.append(']').append(System.lineSeparator());


        sb.append("Cells=[");
        for (int i = start; i < end; i++) {
            sb.append(getCell(i).get().getChar());
        }
        sb.append(']').append(System.lineSeparator());


        if (firstClueIndex != lastClueIndex) {
            sb.append("Possibilities={").append(System.lineSeparator());
            for (int i = start; i < end; i++) {
                sb.append("    {");

                for (int j = firstClueIndex; j < lastClueIndex; j++) {
                    if (possibility(i, j)) {
                        sb.append("true");

                        if (j + 1 < lastClueIndex) {
                            sb.append(",  ");
                        }
                    } else {
                        sb.append("false");

                        if (j + 1 < lastClueIndex) {
                            sb.append(", ");
                        }
                    }
                }

                sb.append('}');

                if (i + 1 < end) {
                    sb.append(",");
                }

                sb.append(System.lineSeparator());
            }
            sb.append('}');
        }

        return sb.toString();
    }
}
