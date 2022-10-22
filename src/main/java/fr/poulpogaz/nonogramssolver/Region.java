package fr.poulpogaz.nonogramssolver;

/**
 * A region is a part of a column/row that contains cell that shares the same possibilities
 */
public class Region {

    private final Descriptor descriptor;
    private int start;
    private int end;
    private int firstClueIndex;
    private int lastClueIndex;

    public Region(Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Region(Descriptor descriptor, int start, int end, int firstClueIndex, int lastClueIndex) {
        this.descriptor = descriptor;
        this.start = start;
        this.end = end;
        this.firstClueIndex = firstClueIndex;
        this.lastClueIndex = lastClueIndex;
    }

    public void trySolve() {
        for (int i = firstClueIndex; i < lastClueIndex; i++) {
            Clue clue = descriptor.getClue(i);

            int minPossible = Integer.MAX_VALUE;
            int maxPossible = Integer.MIN_VALUE;

            for (int j = clue.getMinI(); j < clue.getMaxI(); j++) {
                if (descriptor.getPossibilities()[j][i]) {
                    minPossible = Math.min(j, minPossible);
                    maxPossible = Math.max(j, maxPossible);
                }
            }

            int length = maxPossible - minPossible + 1;
            if (length < 2 * clue.getLength()) {
                System.out.println("Filling...");
                System.out.printf("Clue: %d, between: %d and %d%n", clue.getLength(), minPossible, maxPossible);

                int s = length - clue.getLength();

                for (int j = minPossible + s; j <= maxPossible - s; j++) {
                    descriptor.getCells()[j].set(Cell.FILLED);
                }
            }
        }
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * included
     */
    public int getFirstClueIndex() {
        return firstClueIndex;
    }

    public void setFirstClueIndex(int firstClueIndex) {
        this.firstClueIndex = firstClueIndex;
    }

    /**
     * excluded
     */
    public int getLastClueIndex() {
        return lastClueIndex;
    }

    public void setLastClueIndex(int lastClueIndex) {
        this.lastClueIndex = lastClueIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region region)) return false;

        if (start != region.start) return false;
        if (end != region.end) return false;
        if (firstClueIndex != region.firstClueIndex) return false;
        return lastClueIndex == region.lastClueIndex;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + firstClueIndex;
        result = 31 * result + lastClueIndex;
        return result;
    }

    @Override
    public String toString() {
        return "Region{" +
                "start=" + start +
                ", end=" + end +
                ", firstClueIndex=" + firstClueIndex +
                ", lastClueIndex=" + lastClueIndex +
                '}';
    }
}
