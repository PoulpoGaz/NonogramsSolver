package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClueIteratorTest {

    @Test
    void test() {
        int[] clues = new int[] {
                3, 2, 5
        };
        CellWrapper[] wrappers = new CellWrapper[15];

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        ClueIterator iterator = new ClueIterator(descriptor);

        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertEquals(0, iterator.getIndex());
        assertEquals(0, iterator.getMinI());
        assertEquals(15 - 5 - 1 - 2 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertEquals(1, iterator.getIndex());
        assertEquals(4, iterator.getMinI());
        assertEquals(15 - 5 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next());
        assertEquals(2, iterator.getIndex());
        assertEquals(7, iterator.getMinI());
        assertEquals(15, iterator.getMaxI());

        assertFalse(iterator.hasNext());
    }

    @Test
    void test2() {
        int[] clues = new int[] {
                10
        };
        CellWrapper[] wrappers = new CellWrapper[10];

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        ClueIterator iterator = new ClueIterator(descriptor);

        assertTrue(iterator.hasNext());
        assertEquals(10, iterator.next());
        assertEquals(0, iterator.getIndex());
        assertEquals(0, iterator.getMinI());
        assertEquals(10, iterator.getMaxI());

        assertFalse(iterator.hasNext());
    }

    @Test
    void test3() {
        int[] clues = new int[] {
                10
        };
        CellWrapper[] wrappers = new CellWrapper[25];

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        ClueIterator iterator = new ClueIterator(descriptor);

        assertTrue(iterator.hasNext());
        assertEquals(10, iterator.next());
        assertEquals(0, iterator.getIndex());
        assertEquals(0, iterator.getMinI());
        assertEquals(25, iterator.getMaxI());

        assertFalse(iterator.hasNext());
    }

    @Test
    void test4() {
        int[] clues = new int[] {
                3, 1, 1, 3, 2, 1
        };
        CellWrapper[] wrappers = new CellWrapper[25];

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        ClueIterator iterator = new ClueIterator(descriptor);

        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertEquals(0, iterator.getIndex());
        assertEquals(0, iterator.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1 - 1 - 1 - 1 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertEquals(1, iterator.getIndex());
        assertEquals(3 + 1, iterator.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1 - 1 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertEquals(2, iterator.getIndex());
        assertEquals(3 + 1 + 1 + 1, iterator.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next());
        assertEquals(3, iterator.getIndex());
        assertEquals(3 + 1 + 1 + 1 + 1 + 1, iterator.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertEquals(4, iterator.getIndex());
        assertEquals(3 + 1 + 1 + 1 + 1 + 1 + 3 + 1, iterator.getMinI());
        assertEquals(25 - 1 - 1, iterator.getMaxI());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertEquals(5, iterator.getIndex());
        assertEquals(3 + 1 + 1 + 1 + 1 + 1 + 3 + 1 + 2 + 1, iterator.getMinI());
        assertEquals(25, iterator.getMaxI());

        assertFalse(iterator.hasNext());
    }
}
