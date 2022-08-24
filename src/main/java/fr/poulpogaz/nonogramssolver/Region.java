package fr.poulpogaz.nonogramssolver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Location in the row that contains no crossed cell
 */
public class Region {

    private final Descriptor descriptor;

    private final int start;
    private final int length;

    /**
     * Contains all lines ordered by {@link Line#start()}.
     * Lines are not connected to each others
     */
    private final List<Line> lines = new ArrayList<>();

    // index of numbers that we are sure must be here
    private final Set<Integer> contains;

    // index of numbers that may be here
    private final Set<Integer> possibilities;

    /**
     * @param start  the start of the region
     * @param length the length of the region
     */
    public Region(Descriptor descriptor, int start, int length) {
        this.descriptor = descriptor;
        this.start = start;
        this.length = length;
        this.possibilities = Arrays.stream(descriptor.getClues())
                .boxed()
                .collect(Collectors.toCollection(HashSet::new));
        //noinspection unchecked
        this.contains = (Set<Integer>) ((HashSet<Integer>) possibilities).clone();
    }

    /**
     * This method must only update lines and possibilities if and only if the cell is not filled
     */
    public void fill(int from, int to) {
        if (from < start || to < start || from + to > start + length) {
            throw new IllegalArgumentException("from/to outside of valid range: " + from + " - " + to);
        }

        boolean update = false;
        for (int i = from; i < to; i++) {
            CellWrapper w = descriptor.getCells()[i];

            if (!w.isFilled()) {
                w.set(Cell.FILLED);
                update = true;
            }
        }

        if (!update) {
            return;
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

    public void removePossibility(int index) {
        possibilities.remove(index);
    }

    public void addPossibility(int index) {
        possibilities.add(index);
    }

    public int start() {
        return start;
    }

    public int length() {
        return length;
    }

    public List<Line> lines() {
        return lines;
    }



    @Override
    public String toString() {
        return "Region[" +
                "start=" + start + ", " +
                "length=" + length + ", " +
                "lines=" + lines + ']';
    }
}