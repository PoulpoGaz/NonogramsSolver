package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RegionTest {

    @Test
    void fillTest() {
        TestUtil t = new TestUtil(new Region(getFalseDescriptor(80), 0, 80));

        t.fill(10, 20);
        t.test(t.line(10, 20));

        t.fill(30, 40);
        t.test(t.line(10, 20), t.line(30, 40));

        t.fill(25, 27);
        t.test(t.line(10, 20), t.line(25, 27), t.line(30, 40));

        t.fill(20, 25);
        t.test(t.line(10, 27), t.line(30, 40));

        t.fill(0, 80);
        t.test(t.line(0, 80));
    }

    private Descriptor getFalseDescriptor(int size) {
        CellWrapper[] wrappers = new CellWrapper[size];

        for (int i = 0; i < size; i++) {
            wrappers[i] = new CellWrapper(Cell.EMPTY, 0, i);
        }

        return new Descriptor(new int[0], wrappers, false);
    }

    private static class TestUtil {

        private Region region;

        public TestUtil(Region region) {
            this.region = region;
        }

        public void fill(int from, int to) {
            region.fill(from, to);
        }

        public Line line(int from, int to) {
            return new Line(region, from, to);
        }

        public void test(Line... lines) {
            Assertions.assertEquals(List.of(lines), region.lines());
        }
    }
}
