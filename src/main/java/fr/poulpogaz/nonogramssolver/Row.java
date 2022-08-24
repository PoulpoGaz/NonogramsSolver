package fr.poulpogaz.nonogramssolver;

import java.util.*;

public class Row {

    private final int[] numbers;
    private final CellWrapper[] cells;

    /**
     * the sum of all numbers and the empty cell between them
     */
    private final int length;
    private final int maxNumber;

    public Row(int[] numbers, CellWrapper[] cells) {
        this.numbers = numbers;
        this.cells = cells;

        length = computeLength();
        maxNumber = getMaxNumber();
    }

    private int computeLength() {
        int l = numbers[numbers.length - 1];

        for (int i = 0; i < numbers.length - 1; i++) {
            l += numbers[i] + 1;
        }

        return l;
    }

    private int getMaxNumber() {
        int max = 0;

        for (int number : numbers) {
            max = Math.max(max, number);
        }

        return max;
    }

    /**
     *
     * @return true if it set at least one cell to filled or crossed
     */
    public boolean trySolve() {
        List<Region> regions = getRegions();

        if (regions.isEmpty()) { // the row is fully crossed
            return false;
        }
        boolean changed = false;

        List<Line> lines = unwrapLines(regions);

        fillRegionPossibilities(regions);
        movePossibilitiesToContains(regions);


        return changed;
    }


    private void fillRegionPossibilities(List<Region> regions) {
        int regionIndex = 0;
        int numberIndex = 0;
        int pos = 0; // position in the map

        while (numberIndex < numbers.length) {
            int number = numbers[numberIndex];
            Region region = regions.get(regionIndex);

            if (number > region.start + region.length - pos) { // number can't fill in remaining space
                regionIndex++;
                pos = regions.get(regionIndex).start;

            } else {
                region.addPossibility(numberIndex);

                // add to all remaining regions even if it is clear that it won't fit
                // eg: 1 2 3: '  X    X   '. The 2 will be added to the last region even
                // if it's impossible It will be removed when iterating in reverse order
                for (int i = regionIndex + 1; i < regions.size(); i++) {
                    Region r = regions.get(i);
                    if (r.length >= number) {
                        regions.get(i).addPossibility(numberIndex);
                    }
                }


                pos += number + 1; // don't forget empty space
                numberIndex++;
            }
        }

        // reverse order
        regionIndex = regions.size() - 1;
        numberIndex = numbers.length - 1;
        pos = cells.length - 1;

        while (numberIndex >= 0) {
            int number = numbers[numberIndex];
            Region region = regions.get(regionIndex);

            if (number > region.start + region.length - pos) { // number can't fill in remaining space
                regionIndex--;
                Region r = regions.get(regionIndex);
                pos = r.start + r.length - 1;

            } else {
                // remove the possibility to all next region
                for (int i = regionIndex + 1; i < regions.size(); i++) {
                    Region r = regions.get(i);

                    if (r.length >= number) {
                        regions.get(i).removePossibility(numberIndex);
                    }
                }


                pos += number + 1; // don't forget empty space
                numberIndex--;
            }
        }
    }


    private void movePossibilitiesToContains(List<Region> regions) {
        Set<Integer> toRemove = new HashSet<>();
        for (Region r : regions) {
            if (r.possibilities.size() == 1) {
                r.contains.addAll(r.possibilities); // add on item...
                toRemove.addAll(r.possibilities); // ...
            }
        }

        for (Region r : regions) {
            r.possibilities.removeAll(toRemove);
        }
    }

    private List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();

        int i = 0;
        while (i < cells.length) {
            Region next = createRegion(i);
            regions.add(next);
            i += next.length;
        }

        return regions;
    }

    private Region createRegion(int from) {
        List<Line> lines = new ArrayList<>();
        int lineStart = -1;
        int lineLength = 0;

        int i;
        for (i = from; i < cells.length && !cells[i].isCrossed(); i++) {
            if (cells[i].isFilled()) {
                lineLength++;

                if (lineStart < 0) {
                    lineStart = i;
                }
            } else if (cells[i].isEmpty()) {
                if (lineLength > 0) {
                    lines.add(new Line(lineStart, lineLength));
                    lineLength = 0;
                }

                lineStart = -1;
            }
        }

        if (lineLength >= 0) {
            lines.add(new Line(lineStart, lineLength));
        }

        Region region = new Region(from, i - from, lines);

        for (Line line : lines) {
            line.setRegion(region);
        }

        return region;
    }

    private List<Line> unwrapLines(List<Region> regions) {
        List<Line> lines = new ArrayList<>();

        for (Region region : regions) {
            lines.addAll(region.lines());
        }

        return lines;
    }

    public int length() {
        return cells.length;
    }

    public int nNumber() {
        return numbers.length;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public CellWrapper[] getCells() {
        return cells;
    }

    /**
     * Location in the row that contains no crossed cell
     */
    private static final class Region {
        private final int start;
        private final int length;
        private final List<Line> lines;

        // index of numbers that we are sure must be here
        private final Set<Integer> contains = new HashSet<>();

        // index of numbers that may be here
        private final Set<Integer> possibilities = new HashSet<>();

        /**
         * @param start  the start of the region
         * @param length the length of the region
         * @param lines  the lines in the region
         */
        private Region(int start, int length, List<Line> lines) {
            this.start = start;
            this.length = length;
            this.lines = lines;
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

    /**
     * 'length' filled cells in a region
     */
    private static final class Line {

        private Region region;
        private final int start;
        private final int length;

        /**
         * @param start  line start
         * @param length line length
         */
        private Line(int start, int length) {
            this.start = start;
            this.length = length;
        }

        public int start() {
            return start;
        }

        public int length() {
            return length;
        }

        public Region getRegion() {
            return region;
        }

        public void setRegion(Region region) {
            this.region = region;
        }

        @Override
        public String toString() {
            return "Line[" +
                    "start=" + start + ", " +
                    "length=" + length + ']';
        }
    }
}
