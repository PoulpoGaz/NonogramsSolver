package fr.poulpogaz.nonogramssolver;

import java.util.ArrayList;
import java.util.List;

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

        List<Line> lines = createLines();

        initClues();
        computePossibilities();
        optimizeCluesBoundWithOnePossibility();
        comparePossibilitiesAndLines(lines);
        crossZeroCells();
        tryFill(lines);
    }

    protected List<Line> createLines() {
        List<Line> lines = new ArrayList<>();

        int length = 0;
        for (int i = start; i < end; i++) {
            if (isFilled(i)) {
                length++;
            } else if (length > 0) {
                lines.add(new Line(i - length, i));
                length = 0;
            }
        }

        return lines;
    }

    /**
     * Set minI and maxI for all clues without checking the map
     */
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
            for (; j + clue.getLength() <= clue.getMaxI(); j++) {
                if (fit(clue.getLength(), j)) {
                    markPossible(j, clue);
                }
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

    protected void comparePossibilitiesAndLines(List<Line> lines) {
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);

            int p = firstPossibilityIndex(line.start());

            if (p < 0) {
                continue;
            }

            Clue firstClue = getClue(p);

            int max = Math.min(line.start() + firstClue.getLength(), end);
            int maxLineEnd = -1;
            for (int j = line.end(); j < max; j++) {
                maxLineEnd = j;

                if (isCrossed(j)) {
                    break;
                }
            }

            if (maxLineEnd >= 0) {
                for (int j = maxLineEnd + 1; j < end; j++) {
                    setPossibility(j, firstClue.getIndex(), false);
                }
            }
        }

        // reverse
        for (int i = lines.size() - 1; i >= 0; i--) {
            Line line = lines.get(i);

            int p = lastPossibilityIndex(line.start());

            if (p < 0) {
                continue;
            }

            Clue lastClue = getClue(p);

            int min = Math.max(line.end() - lastClue.getLength(), start);
            int minLineEnd = -1;
            for (int j = line.start(); j >= min; j--) {
                minLineEnd = j;

                if (isCrossed(j)) {
                    break;
                }
            }

            if (minLineEnd >= 0) {
                for (int j = minLineEnd - 1; j >= start; j--) {
                    setPossibility(j, lastClue.getIndex(), false);
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
        } else if (i + line > end || (i + line < end && isFilled(i + line))) {
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

    protected boolean fitReverse(int line, int i) {
        return fit(line, i - line);
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

    protected void tryFill(List<Line> lines) {
        // simple space method
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

        // check line length
        for (Line line : lines) {
            int minLength = Integer.MAX_VALUE;

            for (int i = firstClueIndex; i < lastClueIndex; i++) {
                minLength = Math.min(minLength, getClueLength(i));
            }

            if (minLength == Integer.MAX_VALUE) {
                continue;
            }

            int minPossible = line.start();
            int maxPossible = line.end() - 1;

            for (int i = line.start() - 1; i >= start; i--) {
                if (line.end() - i <= minLength && !isCrossed(i)) {
                    minPossible = i;
                } else {
                    break;
                }
            }

            for (int i = line.end(); i < end; i++) {
                if (i - line.start() <= minLength && !isCrossed(i)) {
                    maxPossible = i;
                } else {
                    break;
                }

            }

            int length = maxPossible + 1 - minPossible;
            if (length < 2 * minLength) {
                int s = length - minLength;

                try {
                    fill(minPossible + s, maxPossible - s, Cell.FILLED);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(this);
                    throw e;
                }
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
        String indent = "    ";

        StringBuilder sb = new StringBuilder();
        sb.append("Region[").append(System.lineSeparator());
        sb.append(indent).append("start=").append(start)
                .append(", end=").append(end)
                .append(", firstClueIndex=").append(firstClueIndex)
                .append(", lastClueIndex=").append(lastClueIndex)
                .append(System.lineSeparator());

        sb.append(indent).append("Clues=[");
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            sb.append(getClue(i));

            if (i + 1 < lastClueIndex) {
                sb.append(", ");
            }
        }
        sb.append(']').append(System.lineSeparator());


        sb.append(indent).append("Cells=[");
        for (int i = start; i < end; i++) {
            sb.append(getCell(i).get().getChar());
        }
        sb.append(']').append(System.lineSeparator());


        if (firstClueIndex != lastClueIndex) {
            sb.append(indent).append("Possibilities={").append(System.lineSeparator());
            for (int i = start; i < end; i++) {
                sb.append(indent).append(indent)
                        .append('\'').append(getCell(i).get().getChar()).append("', ")
                        .append("{");

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
            sb.append(indent).append('}').append(System.lineSeparator());
        }

        sb.append(']').append(System.lineSeparator());

        return sb.toString();
    }
}
