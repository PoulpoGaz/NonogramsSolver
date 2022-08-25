package fr.poulpogaz.nonogramssolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Location in the row that contains no crossed cell
 */
public class Region {

    private final Descriptor descriptor;
    private Region previous;
    private Region next;

    private int start;
    private int end;

    /**
     * Contains all lines ordered by {@link Line#start()}.
     * Lines are not connected to each others
     */
    private final List<Line> lines = new ArrayList<>();

    private PossibleClue[] clues;

    /**
     * @param start  the start of the region
     * @param end the length of the region
     */
    public Region(Descriptor descriptor, int start, int end) {
        this.descriptor = descriptor;
        this.start = start;
        this.end = end;
        this.clues = Arrays.stream(descriptor.getClues())
                .mapToObj(PossibleClue::new)
                .toArray(PossibleClue[]::new);
    }

    /**
     * This method must only update lines and possibilities if and only if the cell is not filled
     */
    public void fill(int from, int to) {
        if (from < start || to < start || to > end) {
            throw new IllegalArgumentException("from/to outside of valid range: " + from + " - " + to);
        }

        boolean update = false;
        for (int i = from; i < to; i++) {
            CellWrapper w = descriptor.getCells()[i];

            if (!w.isFilled()) {
                w.setNoFire(Cell.FILLED);
                update = true;
            }
        }

        if (!update) {
            return;
        } else {
            for (int i = from; i < to; i++) {
                descriptor.getCells()[i].fire();
            }
        }

        // index of the first line that is connected to the newLine
        int i = 0;
        boolean merge = false;

        for (; i < lines.size(); i++) {
            Line line = lines.get(i);

            if (line.connected(from, to)) { // merge two or more lines
                merge = true;
                break;
            } else if (line.start() > to) {
                merge = false;
                break;
            }
        }

        if (merge) {
            int min = Math.min(from, lines.get(i).start());
            int max = lines.get(i).end();

            lines.remove(i);
            while (i < lines.size() && lines.get(i).connected(from, to)) {
                max = lines.get(i).end();
                lines.remove(i);
            }

            lines.add(i, new Line(this, min, Math.max(to, max)));
        } else {
            lines.add(i, new Line(this, from, to));
        }
    }

    public Region[] split(int from, int to) {
        if (from < start || to < start || to > end) {
            throw new IllegalArgumentException("from/to outside of valid range: " + from + " - " + to);
        }

        for (int i = from; i < to; i++) {
            CellWrapper w = descriptor.getCells()[i];

            if (w.isEmpty() || w.isCrossed()) {
                w.set(Cell.CROSSED);
            } else {
                throw new IllegalStateException("Attempt to replace a filled cell by a crossed cell");
            }
        }

        if (from == start && to == end) {
            if (previous != null) {
                previous.next = next;
            }

            if (next != null) {
                next.previous = previous;
            }

            return new Region[0];
        } else if (from == start) {
            start = to;
            return new Region[] {this};
        } else if (end == to) {
            end = from;
            return new Region[] {this};
        } else {
            // lines of the 2nd region
            Region newRegion = new Region(descriptor, to, end);
            end = from;

            boolean removeAdd = false;
            int i;
            for (i = 0; i < lines.size(); i++) {
                Line line = lines.get(i);

                if (!removeAdd) {
                    if (line.start() >= newRegion.start()) {
                        removeAdd = true;
                    }
                }

                if (removeAdd) {
                    lines.remove(i);
                    newRegion.lines.add(line);
                }
            }

            newRegion.previous = this;
            newRegion.next = next;

            if (next != null) {
                next.previous = newRegion;
            }

            next = newRegion;

            return new Region[] {this, newRegion};
        }
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public Region next() {
        return next;
    }

    public Region previous() {
        return previous;
    }

    public List<Line> lines() {
        return lines;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return "Region[" +
                "start=" + start + ", " +
                "end=" + end + ", " +
                "lines=" + lines + ']';
    }

    private static class PossibleClue {

        private final int clue;

        /**
         * false if the region may contain the clue
         * true if the region contain the clue
         */
        private boolean contains;

        public PossibleClue(int clue) {
            this.clue = clue;
        }
    }
}