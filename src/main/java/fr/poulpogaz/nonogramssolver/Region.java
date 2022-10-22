package fr.poulpogaz.nonogramssolver;

/**
 * A region is a part of a column/row that contains cell that shares the same possibilities
 */
public class Region extends AbstractRegion {

    private final Descriptor descriptor;

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

    @Override
    protected CellWrapper getCell(int index) {
        return descriptor.getCell(index);
    }

    @Override
    protected void setCell(int index, Cell cell) {
        descriptor.setCell(index, cell);
    }

    @Override
    protected Clue getClue(int index) {
        return descriptor.getClue(index);
    }

    @Override
    protected void setPossibility(int cell, int clueIndex, boolean possibility) {
        descriptor.setPossibility(cell, clueIndex, possibility);
    }

    @Override
    protected boolean possibility(int cell, int clueIndex) {
        return descriptor.possibility(cell, clueIndex);
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
