package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Clue;
import fr.poulpogaz.nonogramssolver.solver.Description;

import java.util.ArrayList;
import java.util.List;

/**
 * A region is a part of a column/row that contains cell that shares the same possibilities
 */
public abstract class AbstractRegion {

    protected Description description;

    // included
    protected int start;
    // excluded
    protected int end;

    // included
    protected int firstClueIndex;
    // excluded
    protected int lastClueIndex;

    public AbstractRegion(Description description) {
        this.description = description;
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

        if (!description.hasContradiction()) {
            crossZeroCells();
            tryFill(lines);
        }
    }

    protected List<Line> createLines() {
        List<Line> lines = new ArrayList<>();

        int length = 0;
        int color = 0;
        for (int i = start; i < end; i++) {
            if (length == 0) {
                if (isFilled(i)) {
                    length = 1;
                    color = getColor(i);
                }
            } else if (isFilled(i, color)) {
                length++;
            } else {
                lines.add(new Line(i - length, i, color));
                length = 0;
            }
        }

        return lines;
    }

    protected void computePossibilities() {
        int minI = start;
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue c = getClue(i);

            while (!fit(c, minI)) {
                minI++;

                if (minI + c.getLength() > end) {
                    description.setContradiction();
                    return;
                }
            }

            c.setMinI(minI);
            minI += c.getLength();

            if (i + 1 < lastClueIndex && getClue(i + 1).getColor() == c.getColor()) {
                minI++;
            }
        }

        int maxI = end;
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            Clue c = getClue(i);

            while (!fitReverse(c, maxI - 1)) {
                maxI--;

                if (maxI - c.getLength() < 0) {
                    description.setContradiction();
                    return;
                }
            }

            c.setMaxI(maxI);
            maxI -= c.getLength();

            if (i - 1 >= firstClueIndex && getClue(i - 1).getColor() == c.getColor()) {
                maxI--;
            }
        }

        clearPossibilities();

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = getClue(i);

            int j = clue.getMinI();
            for (; j + clue.getLength() <= clue.getMaxI(); j++) {
                if (fit(clue, j)) {
                    markPossible(j, clue);
                }
            }
        }

        checkClues();
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
            setPossibility(j, clue, true);
        }
    }

    protected void optimizeCluesBoundWithOnePossibility() {
        if (description.hasContradiction()) {
            return;
        }

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

        int j = min;
        for (; j < firstIndex; j++) {
            if (Math.abs(j - firstIndex) >= maxDist) {
                setPossibility(j, clue, false);
            } else if (!isPossible(j, clue) || !fit(clue, j)) {
                setPossibility(j, clue, false);
            } else {
                break;
            }
        }

        clue.setMinI(j);

        j = max - 1;
        for (; j >= lastIndex + 1; j--) {
            if (Math.abs(j - lastIndex) >= maxDist) {
                setPossibility(j, clue, false);
            } else if (!isPossible(j, clue) || !fitReverse(clue, j)) {
                setPossibility(j, clue, false);
            } else {
                break;
            }
        }

        clue.setMaxI(j + 1);

       checkClues();
    }

    /**
     * For each line, we take the first clue that can represent this line,
     * and we remove all clues that are too far.
     * This is also done in reverse order
     * See TurtleBug 1 and 2 and TotoroBug 1.
     * Actually, I don't know why it works lol
     *
     * TODO: optimize this function, avoid using {@link #recalculateMinIMaxI()}
     */
    protected void comparePossibilitiesAndLines(List<Line> lines) {
        if (description.hasContradiction()) {
            return;
        }

        for (Line line : lines) {
            for (int j = firstClueIndex; j < lastClueIndex; j++) {
                Clue clue = getClue(j);

                if (clue.getColor() != line.color()) {
                    for (int k = line.start(); k < line.end(); k++) {
                        setPossibility(k, clue, false);
                    }
                }
            }
        }
        recalculateMinIMaxI();

        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            Clue firstClue = firstPossibility(line.start());

            if (firstClue == null) {
                continue;
            }

            int max = Math.min(Math.min(line.start() + firstClue.getLength(), end), firstClue.getMaxI());
            firstClue.setMaxI(max);

            for (int j = line.end(); j < firstClue.getMaxI(); j++) {
                if (isCrossed(j) || !isPossible(j, firstClue)) {
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
                if (isCrossed(j) || !isPossible(j, lastClue)) {
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

            drawBetween(clue.getMinI(), clue.getMaxI(), clue.getLength(), clue.getColor());
        }

        // check line length
        // for each line, we take the clue with the minimal length l,
        // and we try to draw a line between the minimum and maximum possible
        // of length l
        // see TurtleBug 1 and 3
        for (Line line : lines) {
            int minLength = minClueLength(line.start());

            if (minLength < 0) {
                description.setContradiction();
                return;
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

            drawBetween(minPossible, maxPossible + 1, minLength, line.color());
        }
    }

    // =======================================
    // * Starting here, some utility methods *
    // =======================================


    protected boolean fit(Clue clue, int i) {
        return SolverUtils.fit(description, clue, i);
    }

    protected boolean fitReverse(Clue clue, int i) {
        return SolverUtils.fitReverse(description, clue, i);
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
            if (isPossible(cell, i)) {
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
            if (isPossible(cell, i)) {
                return getClue(i);
            }
        }

        return null;
    }

    protected Clue lastPossibility(int cell) {
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (isPossible(cell, i)) {
                return getClue(i);
            }
        }

        return null;
    }

    protected int minClueLength(int cell) {
        int minLength = Integer.MAX_VALUE;
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (isPossible(cell, i)) {
                minLength = Math.min(getClueLength(i), minLength);
            }
        }

        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    protected int maxClueLength(int cell) {
        int maxLength = -1;
        for (int i = lastClueIndex - 1; i >= firstClueIndex; i--) {
            if (isPossible(cell, i)) {
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
            if (isPossible(cell, i)) {
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
    protected void drawBetween(int min, int max, int lineLength, int color) {
        int totalLength = max - min;
        if (totalLength < 2 * lineLength) {
            int s = totalLength - lineLength;

            draw(min + s, max - s, Cell.FILLED, color);
        }
    }

    protected void draw(int start, int end, int cellType) {
        draw(start, end, cellType, 0);
    }

    /**
     * Draw a line from start (inclusive) to end (exclusive)
     * @param start start of the line (inclusive)
     * @param end end of the line (exclusive)
     */
    protected void draw(int start, int end, int cellType, int cellColor) {
        for (int i = start; i < end; i++) {
            setCell(i, cellType, cellColor);
        }
    }


    /**
     * Debug method that checks if clue minI and maxI are correct
     */
    protected void checkClues() {
        if (description.hasContradiction()) {
            return;
        }

        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue c = getClue(i);

            if (c.getMaxI() - c.getMinI() < c.getLength()) {
                description.setContradiction();
                return;
            }

            if (!isPossible(c.getMinI(), c)) {
                description.setContradiction();
                return;
            }
            if (!isPossible(c.getMaxI() - 1, c)) {
                description.setContradiction();
                return;
            }

            for (int j = start; j < end; j++) {
                if (isPossible(j, c) && j < c.getMinI()) {
                    description.setContradiction();
                    return;
                } else if (isPossible(j, c) && j >= c.getMaxI()) {
                    description.setContradiction();
                    return;
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
                if (isPossible(j, clue)) {
                    minI = Math.min(minI, j);
                    maxI = Math.max(maxI, j);
                }
            }

            if (minI == Integer.MAX_VALUE) {
                description.setContradiction();
                return;
            }

            clue.setMinI(minI);
            clue.setMaxI(maxI + 1);
        }
    }

    /**
     * @return the available space
     */
    protected int size() {
        return start - end;
    }

    protected boolean isFilled(int i) {
        return description.isFilled(i);
    }

    protected boolean isFilled(int i, int color) {
        return description.isFilled(i, color);
    }

    protected boolean isCrossed(int i) {
        return description.isCrossed(i);
    }

    protected boolean isEmpty(int i) {
        return description.isEmpty(i);
    }

    protected int getClueLength(int i) {
        return description.getClueLength(i);
    }

    protected CellWrapper getCell(int index) {
        return description.getCell(index);
    }

    protected int getColor(int index) {
        return description.getCell(index).get().getColor();
    }

    protected void setCell(int index, int type) {
        description.setCell(index, type);
    }

    protected void setCell(int index, int type, int color) {
        description.setCell(index, type, color);
    }

    protected Clue getClue(int index) {
        return description.getClue(index);
    }



    protected void setPossibility(int cell, Clue clue, boolean possibility) {
        setPossibility(cell, clue.getIndex(), possibility);
    }

    protected abstract void setPossibility(int cell, int clueIndex, boolean possibility);

    protected boolean isPossible(int cell, Clue clue) {
        return isPossible(cell, clue.getIndex());
    }

    protected abstract boolean isPossible(int cell, int clueIndex);

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
                    if (isPossible(i, j)) {
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
