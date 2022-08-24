package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RegionTest {

    @Test
    void fillTest() {
        TestUtil t = new TestUtil(new Region(getFalseDescriptor(80), 0, 80));

        t.fill(10, 20);
        t.lineTest(t.line(10, 20));

        t.fill(30, 40);
        t.lineTest(t.line(10, 20), t.line(30, 40));

        t.fill(25, 27);
        t.lineTest(t.line(10, 20), t.line(25, 27), t.line(30, 40));

        t.fill(20, 25);
        t.lineTest(t.line(10, 27), t.line(30, 40));

        t.fill(0, 80);
        t.lineTest(t.line(0, 80));
    }

    @Test
    void splitTestNoLine() {
        TestUtil t = new TestUtil(new Region(getFalseDescriptor(80), 0, 80));

        TestUtil t2 = t.split(40, 50);
        Assertions.assertNotNull(t2);
        t2.checkSize(50, 80);
        t.checkSize(0, 40);

        Assertions.assertNull(t.split(20, 40));
        t.checkSize(0, 20);

        Assertions.assertNull(t.split(0, 10));
        t.checkSize(10, 20);

        Assertions.assertNull(t.split(10, 20));
        Assertions.assertNull(t.region);
    }

    @Test
    void splitTestWithLine() {
        TestUtil t = new TestUtil(new Region(getFalseDescriptor(80), 0, 80));
        t.fill(10, 20);
        t.fill(40, 50);

        TestUtil t2 = t.split(60, 70);
        Assertions.assertNotNull(t2);
        t2.checkSize(70, 80);
        t.checkSize(0, 60);
        t.lineTest(t.line(10, 20), t.line(40, 50));
        t2.lineTest();


        t2 = t.split(25, 35);
        Assertions.assertNotNull(t2);
        t2.checkSize(35, 60);
        t.checkSize(0, 25);
        t.lineTest(t.line(10, 20));
        t2.lineTest(t2.line(40, 50));
    }

    private static Descriptor getFalseDescriptor(int size) {
        CellWrapper[] wrappers = new CellWrapper[size];

        for (int i = 0; i < size; i++) {
            wrappers[i] = new CellWrapper(Cell.EMPTY, 0, i);
        }

        return new Descriptor(new int[0], wrappers, false);
    }


    private static void lineTest(Region region, Line... lines) {
        Assertions.assertEquals(List.of(lines), region.lines());

        CellWrapper[] wrappers = region.getDescriptor().getCells();

        int lineI = 0;
        Line curr = lines.length == 0 ? null : lines[lineI];

        for (int i = region.start(); i < region.end(); i++) {
            if (curr != null && i >= curr.start() && i < curr.end()) {
                Assertions.assertTrue(wrappers[i].isFilled());
            } else {
                Assertions.assertFalse(wrappers[i].isFilled());

                if (curr != null && curr.end() <= i && lineI + 1 < lines.length) {
                    lineI++;
                    curr = lines[lineI];
                }
            }
        }
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

        public TestUtil split(int from, int to) {
            int oldStart = region.start();
            int oldEnd = region.end();

            Region[] newRegions = region.split(from, to);

            for (int i = oldStart; i < oldEnd; i++) {
                CellWrapper w = region.getDescriptor().getCells()[i];
                if (i >= from && i < to) {
                    Assertions.assertTrue(w.isCrossed());
                } else {
                    Assertions.assertFalse(w.isCrossed());
                }
            }

            if (newRegions.length == 0) {
                region = null;
                return null;
            } else if (newRegions.length == 1) {
                region = newRegions[0];
                return null;
            } else {
                region = newRegions[0];
                return new TestUtil(newRegions[1]);
            }
        }

        public void lineTest(Line... lines) {
            RegionTest.lineTest(region, lines);
        }

        public void checkSize(int start, int end) {
            Assertions.assertEquals(start, region.start());
            Assertions.assertEquals(end, region.end());
        }
    }
}
