package fr.poulpogaz.nonogramssolver.linesolver;

/**
 * 'length' filled cells in a region
 */
public class Line {

    private int start;
    private int end;

    /**
     * @param start line start, inclusive
     * @param end line end, exclusive
     */
    public Line(int start, int end) {
        this.start = start;
        this.end = end;

        if (end <= start) {
            throw new IllegalArgumentException();
        }
    }

    public boolean connected(int start2, int end2) {
        if (start2 == end2) {
            throw new IllegalArgumentException();
        }

        if (start < start2) {
            return start2 <= end;
        } else {
            return start <= end2;
        }
    }

    /**
     * included
     */
    public int start() {
        return start;
    }

    /**
     * excluded
     */
    public int end() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (start != line.start) return false;
        return end == line.end;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        return "Line[" +
                "start=" + start + ", " +
                "end=" + end + ']';
    }
}