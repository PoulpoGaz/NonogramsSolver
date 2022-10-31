package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Clue;
import fr.poulpogaz.nonogramssolver.Descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * A region is a part of a column/row that contains cell that shares the same possibilities
 */
public abstract class AbstractRegion {

    protected Descriptor descriptor;

    // included
    protected int start;
    // excluded
    protected int end;

    // included
    protected int firstClueIndex;
    // excluded
    protected int lastClueIndex;

    public AbstractRegion(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void trySolve() {
        if (firstClueIndex == lastClueIndex) {
            draw(start, start - end, Cell.CROSSED);
            return;
        }

        List<Line> lines = createLines();

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

    protected void computePossibilities() {
        int minI = start;
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue c = getClue(i);

            while (!fit(c.getLength(), minI)) {
                minI++;
            }

            c.setMinI(minI);
            minI += c.getLength() + 1;
        }

        int maxI = end;
        for (int i =  lastClueIndex - 1; i >= firstClueIndex; i--) {
            Clue c = getClue(i);

            while (!fitReverse(c.getLength(), maxI - 1)) {
                maxI--;
            }

            c.setMaxI(maxI);
            maxI -= (c.getLength() + 1);
        }

        clearPossibilities();

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int j = clue.getMinI();
            for (; j + clue.getLength() <= clue.getMaxI(); j++) {
                if (fit(clue.getLength(), j)) {
                    markPossible(j, clue);
                }
            }
        }

        checkClues();
    }

    /**
     * Compute possibilities. It doesn't take into account all information that gave the map.
     * For example, for 15 cells, clues 5 2 and a map looking like this: '  F    F    FF  ',
     * it won't realize that outside the first two F, it mustn't have a 5.
     */
    /*protected void computePossibilities() {
        checkClues();
    }*/

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
            setPossibility(j, clue, true);
        }
    }

    protected boolean checkClue(Clue clue, int i) {
        for (int j = i; j > i - clue.getLength(); j--) {
            if (!possibility(j, clue)) {
                return false;
            }
        }

        return true;
    }

    protected void optimizeCluesBoundWithOnePossibility() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            optimizeClueBoundWithOnePossibility(getClue(i));
        }

        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            optimizeClueBoundWithOnePossibility(getClue(i));
        }

        checkClues();
    }

    protected void optimizeClueBoundWithOnePossibility(Clue clue) {
        // find the first index (and last) that we are sure it is associated with the clue
        int firstIndex = -1;
        int lastIndex = -1;
        for (int j = clue.getMinI(); j < clue.getMaxI(); j++) {
            if (!isFilled(j)) {
                continue;
            }

            if (getUniquePossibility(j) != clue) {
                continue;
            }

            // this cell is associated with this clue
            if (firstIndex < 0) {
                firstIndex = j;
            }
            lastIndex = j;
        }

        if (firstIndex < 0) {
            return;
        }

        // remove other possibilities:
        // all possibilities that are too far are removed or separated by a crossed cell

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
                setPossibility(j, clue, false);
            }
        }

        // copied code...
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
                setPossibility(j, clue, false);
            }
        }
    }

    /**
     * For each line, we take the first clue that can represent this line,
     * and we remove all clues that are too far.
     * This is also done in reverse order
     * See TurtleBug 1 and 2 and TotoroBug 1.
     * Actually, I don't know why it works lol
     */
    protected void comparePossibilitiesAndLines(List<Line> lines) {
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            Clue firstClue = firstPossibility(line.start());

            if (firstClue == null) {
                continue;
            }

            int max = Math.min(Math.min(line.start() + firstClue.getLength(), end), firstClue.getMaxI());
            firstClue.setMaxI(max);

            for (int j = line.end(); j < firstClue.getMaxI(); j++) {
                if (isCrossed(j) || !possibility(j, firstClue)) {
                    firstClue.setMaxI(j);
                }
            }

            for (int j = firstClue.getMaxI(); j < end; j++) {
                for (int k = firstClueIndex; k <= firstClue.getIndex(); k++) {
                    setPossibility(j, k, false);
                }
            }

            recalculateMinIMaxI();
        }

        // reverse
        for (int i = lines.size() - 1; i >= 0; i--) {
            Line line = lines.get(i);

            Clue lastClue = lastPossibility(line.start());

            if (lastClue == null) {
                continue;
            }

            int min = Math.max(Math.max(line.end() - lastClue.getLength(), start), lastClue.getMinI());
            lastClue.setMinI(min);
            for (int j = line.start() - 1; j >= lastClue.getMinI(); j--) {
                if (isCrossed(j) || !possibility(j, lastClue)) {
                    lastClue.setMinI(j + 1);
                }
            }

            for (int j = lastClue.getMinI() - 1; j >= start; j--) {
                for (int k = lastClue.getIndex(); k < lastClueIndex; k++) {
                    setPossibility(j, k, false);
                }
            }

            recalculateMinIMaxI();
        }

        checkClues();
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

    protected void tryFill(List<Line> lines) {
        // simple space method
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            drawBetween(clue.getMinI(), clue.getMaxI(), clue.getLength());
        }

        // check line length
        // for each line, we take the clue with the minimal length l,
        // and we try to draw a line between the minimum and maximum possible
        // of length l
        // see TurtleBug 1 and 3
        for (Line line : lines) {
            int minLength = minClueLength(line.start());

            if (minLength < 0) {
                throw new IllegalStateException(); // should not happen!
            }

            // inclusive
            int minPossible = line.start();
            // inclusive...
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

            drawBetween(minPossible, maxPossible + 1, minLength);
        }
    }

    protected boolean fit(int line, int i) {
        return SolverUtils.fit(descriptor, line, i);
    }

    protected boolean fitReverse(int line, int i) {
        return SolverUtils.fitReverse(descriptor, line, i);
    }

    /**
     * Returns the unique possibility at {@code cell}. If there is two or more clues that
     * can match the position, it returns {@code null}
     *
     * @param cell the cell
     * @return the unique possibility or {@code null}
     */
    protected Clue getUniquePossibility(int cell) {
        int possibility = -1;

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                if (possibility == -1) {
                    possibility = i;
                } else {
                    return null;
                }
            }
        }

        if (possibility >= 0) {
            return getClue(possibility);
        } else {
            return null;
        }
    }

    protected Clue firstPossibility(int cell) {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                return getClue(i);
            }
        }

        return null;
    }

    protected Clue lastPossibility(int cell) {
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (possibility(cell, i)) {
                return getClue(i);
            }
        }

        return null;
    }

    protected int minClueLength(int cell) {
        int minLength = Integer.MAX_VALUE;
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (possibility(cell, i)) {
                minLength = Math.min(getClueLength(i), minLength);
            }
        }

        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    protected int maxClueLength(int cell) {
        int maxLength = -1;
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (possibility(cell, i)) {
                maxLength = Math.max(getClueLength(i), maxLength);
            }
        }

        return maxLength;
    }

    /**
     * @param cell the cell
     * @return true if the cell must be crossed
     */
    protected boolean haveZeroPossibility(int cell) {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            if (possibility(cell, i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Try to draw a part of a line of length 'lineLength' between 'min' and 'max'.
     * It draws where it is certain that cells are filled, which is in the center
     *
     * @param min min index (inclusive)
     * @param max max index (exclusive)
     * @param lineLength the length of the line
     */
    protected void drawBetween(int min, int max, int lineLength) {
        int totalLength = max - min;
        if (totalLength < 2 * lineLength) {
            int s = totalLength - lineLength;

            draw(min + s, max - s, Cell.FILLED);
        }
    }

    /**
     * Draw a line from start (inclusive) to end (exclusive)
     * @param start start of the line (inclusive)
     * @param end end of the line (exclusive)
     * @param cell of what the line is composed
     */
    protected void draw(int start, int end, Cell cell) {
        for (int i = start; i < end; i++) {
            setCell(i, cell);
        }
    }


    /**
     * Debug method that checks if clue minI and maxI are correct
     */
    protected void checkClues() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue c = getClue(i);

            if (!possibility(c.getMinI(), c)) {
                throw new IllegalStateException("Invalid minI for clue: " + c + "\n" + this);
            }
            if (!possibility(c.getMaxI() - 1, c)) {
                throw new IllegalStateException("Invalid maxI for clue: " + c + "\n" + this);
            }

            for (int j = start; j < end; j++) {
                if (possibility(j, c) && j < c.getMinI()) {
                    throw new IllegalStateException("Invalid minI for clue: " + c + ". at " + i + "\n" + this);
                } else if (possibility(j, c) && j >= c.getMaxI()) {
                    throw new IllegalStateException("Invalid maxI for clue: " + c + ". at " + i + "\n" + this);
                }
            }
        }
    }

    protected void recalculateMinIMaxI() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int minI = Integer.MAX_VALUE;
            int maxI = 0;

            for (int j = start; j < end; j++) {
                if (possibility(j, clue)) {
                    minI = Math.min(minI, j);
                    maxI = Math.max(maxI, j);
                }
            }

            clue.setMinI(minI);
            clue.setMaxI(maxI + 1);
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
        return descriptor.isFilled(i);
    }

    protected boolean isCrossed(int i) {
        return descriptor.isCrossed(i);
    }

    protected boolean isEmpty(int i) {
        return descriptor.isEmpty(i);
    }

    protected int getClueLength(int i) {
        return descriptor.getClueLength(i);
    }

    protected CellWrapper getCell(int index) {
        return descriptor.getCell(index);
    }

    protected void setCell(int index, Cell cell) {
        descriptor.setCell(index, cell);
    }

    protected Clue getClue(int index) {
        return descriptor.getClue(index);
    }



    protected void setPossibility(int cell, Clue clue, boolean possibility) {
        setPossibility(cell, clue.getIndex(), possibility);
    }

    protected abstract void setPossibility(int cell, int clueIndex, boolean possibility);

    protected boolean possibility(int cell, Clue clue) {
        return possibility(cell, clue.getIndex());
    }

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
