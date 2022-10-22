package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fr.poulpogaz.nonogramssolver.Cell.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DescriptorTest {

    // *************
    // * InitClues *
    // *************

    @Test
    void initCluesTest1() {
        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {3, 2, 5};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();

        Clue clue = descriptor.getClues()[0];
        assertEquals(0, clue.getMinI());
        assertEquals(15 - 5 - 1 - 2 - 1, clue.getMaxI());

        clue = descriptor.getClues()[1];
        assertEquals(4, clue.getMinI());
        assertEquals(15 - 5 - 1, clue.getMaxI());

        clue = descriptor.getClues()[2];
        assertEquals(7, clue.getMinI());
        assertEquals(15, clue.getMaxI());
    }

    @Test
    void initCluesTest2() {
        CellWrapper[] wrappers = createEmpty(10);
        int[] clues = new int[] {10};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();

        Clue clue = descriptor.getClues()[0];
        assertEquals(0, clue.getMinI());
        assertEquals(10, clue.getMaxI());
    }

    @Test
    void initCluesTest3() {
        CellWrapper[] wrappers = createEmpty(25);
        int[] clues = new int[] {10};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();

        Clue clue = descriptor.getClues()[0];
        assertEquals(0, clue.getMinI());
        assertEquals(25, clue.getMaxI());
    }

    @Test
    void initCluesTest4() {
        CellWrapper[] wrappers = createEmpty(25);
        int[] clues = new int[] {3, 1, 1, 3, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();

        Clue clue = descriptor.getClues()[0];
        assertEquals(0, clue.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1 - 1 - 1 - 1 - 1, clue.getMaxI());

        clue = descriptor.getClues()[1];
        assertEquals(3 + 1, clue.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1 - 1 - 1, clue.getMaxI());

        clue = descriptor.getClues()[2];
        assertEquals(3 + 1 + 1 + 1, clue.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1 - 3 - 1, clue.getMaxI());

        clue = descriptor.getClues()[3];
        assertEquals(3 + 1 + 1 + 1 + 1 + 1, clue.getMinI());
        assertEquals(25 - 1 - 1 - 2 - 1, clue.getMaxI());


        clue = descriptor.getClues()[4];
        assertEquals(3 + 1 + 1 + 1 + 1 + 1 + 3 + 1, clue.getMinI());
        assertEquals(25 - 1 - 1, clue.getMaxI());

        clue = descriptor.getClues()[5];
        assertEquals(3 + 1 + 1 + 1 + 1 + 1 + 3 + 1 + 2 + 1, clue.getMinI());
        assertEquals(25, clue.getMaxI());
    }


    // ************************
    // * ComputePossibilities *
    // ************************

    @Test
    void possibilitiesTest() {
        // 15 | 2 6 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();

        // cell then clue (2, 6, 2)
        boolean[][] expected = new boolean[][] {
                //              2     6      2
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  true,  false},
                new boolean[] {true,  true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  true},
                new boolean[] {false, true,  true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(0, 5, descriptor.getClue(0));
        clueEquals(3, 12, descriptor.getClue(1));
        clueEquals(10, 15, descriptor.getClue(2));
    }


    @Test
    void possibilitiesTest2() {
        // 15 | 5 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {false, true},
                new boolean[] {false, true},
                new boolean[] {false, true},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(0, 12, descriptor.getClue(0));
        clueEquals(6, 15, descriptor.getClue(1));
    }

    @Test
    void possibilitiesTest3() {
        // 15 | 2 2 2 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 2, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             2      2      2      2
                new boolean[] {true,  false, false, false},
                new boolean[] {true,  false, false, false},
                new boolean[] {true,  false, false, false},
                new boolean[] {true,  true,  false, false},
                new boolean[] {true,  true,  false, false},
                new boolean[] {true,  true,  false, false},
                new boolean[] {false, true,  true,  false},
                new boolean[] {false, true,  true,  false},
                new boolean[] {false, true,  true,  false},
                new boolean[] {false, false, true,  true},
                new boolean[] {false, false, true,  true},
                new boolean[] {false, false, true,  true},
                new boolean[] {false, false, false, true},
                new boolean[] {false, false, false, true},
                new boolean[] {false, false, false, true},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(0, 6, descriptor.getClue(0));
        clueEquals(3, 9, descriptor.getClue(1));
        clueEquals(6, 12, descriptor.getClue(2));
        clueEquals(9, 15, descriptor.getClue(3));
    }




    @Test
    void possibilitiesTestWithCell() {
        // 15 | 2 6 2

        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY,
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY,
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();

        // cell then clue (2, 6, 2)
        boolean[][] expected = new boolean[][] {
                //              2     6      2
                new boolean[] {false, false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, false},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(1, 4, descriptor.getClue(0));
        clueEquals(4, 11, descriptor.getClue(1));
        clueEquals(11, 14, descriptor.getClue(2));
    }


    @Test
    void possibilitiesTestWithCell2() {
        // 15 | 5 2

        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {true,  true},
                new boolean[] {false, false},
                new boolean[] {false, true},
                new boolean[] {false, true},
                new boolean[] {false, false},
                new boolean[] {false, false},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(0, 10, descriptor.getClue(0));
        clueEquals(6, 13, descriptor.getClue(1));
    }

    @Test
    void possibilitiesTestWithCell3() {
        // 15 | 5 2 2

        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, FILLED, EMPTY, FILLED, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2      2
                new boolean[] {false, false, false},
                new boolean[] {false, false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {false, false, false},
                new boolean[] {false, false, false},
                new boolean[] {false, true, false},
                new boolean[] {false, true, false},
                new boolean[] {false, false, false},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, false},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(2, 7, descriptor.getClue(0));
        clueEquals(9, 11, descriptor.getClue(1));
        clueEquals(12, 14, descriptor.getClue(2));
    }



    // ******************************************
    // * optimizeCluesBoundWithOnePossibilities *
    // ******************************************

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest() {
        // 15 | 5 2

        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {true,  false},
                new boolean[] {false, false},
                new boolean[] {false, false},
                new boolean[] {false, false},
                new boolean[] {false, false},
                new boolean[] {false, true},
                new boolean[] {false, true},
                new boolean[] {false, false},
                new boolean[] {false, false},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        assertEquals(0, descriptor.getClues()[0].getMinI());
        assertEquals(7, descriptor.getClues()[0].getMaxI());
        assertEquals(11, descriptor.getClues()[1].getMinI());
        assertEquals(13, descriptor.getClues()[1].getMaxI());
    }

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest2() {
        // 15 | 5 2 2

        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, FILLED, EMPTY, FILLED, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2      2
                new boolean[] {false, false, false},
                new boolean[] {false, false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {false, false, false},
                new boolean[] {false, false, false},
                new boolean[] {false, true, false},
                new boolean[] {false, true, false},
                new boolean[] {false, false, false},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, false},
        };

        arrayEquals(expected, descriptor.getPossibilities());
        clueEquals(2, 7, descriptor.getClue(0));
        clueEquals(9, 11, descriptor.getClue(1));
        clueEquals(12, 14, descriptor.getClue(2));
    }



    // *********
    // * split *
    // *********

    @Test
    void splitTest1() {
        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = descriptor.split();

        assertEquals(1, regions.size());
        assertEquals(new Region(descriptor, 0, 15, 0, 3), regions.get(0));
    }

    @Test
    void splitTest2() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = descriptor.split();

        assertEquals(2, regions.size());
        assertEquals(new Region(descriptor, 0, 7, 0, 1), regions.get(0));
        assertEquals(new Region(descriptor, 11, 13, 1, 2), regions.get(1));
    }

    @Test
    void splitTest3() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, FILLED, FILLED, EMPTY, FILLED, FILLED, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, FILLED, EMPTY, EMPTY
        );
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = descriptor.split();

        assertEquals(3, regions.size());
        assertEquals(new Region(descriptor, 2, 7, 0, 1), regions.get(0));
        assertEquals(new Region(descriptor, 9, 11, 1, 2), regions.get(1));
        assertEquals(new Region(descriptor, 12, 14, 2, 3), regions.get(2));
    }












    @Test
    void completeTest1() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, CROSSED, EMPTY, FILLED
        );
        int[] clues = new int[] {2, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = descriptor.split();
        regions.get(1).trySolve();

        cellsEquals(wrappers, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, CROSSED, FILLED, FILLED);
    }

    @Test
    void completeTest2() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, CROSSED
        );
        int[] clues = new int[] {3, 1, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = descriptor.split();
        System.out.println(regions);


    }











    private void arrayEquals(boolean[][] expected, boolean[][] current) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                Assertions.assertEquals(expected[i][j], current[i][j], "At (%d; %d)".formatted(i, j));
            }
        }
    }

    private void clueEquals(int expectedMinI, int expectedMaxI, Clue clue) {
        assertEquals(expectedMinI, clue.getMinI());
        assertEquals(expectedMaxI, clue.getMaxI());
    }

    private void cellsEquals(CellWrapper[] input, Cell... expected) {
        for (int i = 0; i < input.length; i++) {
            assertEquals(expected[i], input[i].get());
        }
    }

    private CellWrapper[] create(Cell... cells) {
        CellWrapper[] wrappers = new CellWrapper[cells.length];

        for (int i = 0; i < cells.length; i++) {
            wrappers[i] = new CellWrapper(cells[i], i, 0);
        }

        return wrappers;
    }

    private CellWrapper[] createEmpty(int length) {
        CellWrapper[] wrappers = new CellWrapper[length];

        for (int i = 0; i < length; i++) {
            wrappers[i] = new CellWrapper(EMPTY, i, 0);
        }

        return wrappers;
    }
}
