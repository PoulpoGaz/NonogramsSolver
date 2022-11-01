package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Descriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultLineSolverTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    // ************************
    // * ComputePossibilities *
    // ************************

    @Test
    void computePossibilitiesTest1() {
        // 15 | 2 6 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(0, 5, descriptor.getClue(0));
        clueEquals(3, 12, descriptor.getClue(1));
        clueEquals(10, 15, descriptor.getClue(2));
    }


    @Test
    void computePossibilitiesTest2() {
        // 15 | 5 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(0, 12, descriptor.getClue(0));
        clueEquals(6, 15, descriptor.getClue(1));
    }

    @Test
    void computePossibilitiesTest3() {
        // 15 | 2 2 2 2

        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 2, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(0, 6, descriptor.getClue(0));
        clueEquals(3, 9, descriptor.getClue(1));
        clueEquals(6, 12, descriptor.getClue(2));
        clueEquals(9, 15, descriptor.getClue(3));
    }




    @Test
    void computePossibilitiesWithCellTest1() {
        // 15 | 2 6 2

        CellWrapper[] wrappers = parse("__█____█____█__");
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(1, 4, descriptor.getClue(0));
        clueEquals(4, 11, descriptor.getClue(1));
        clueEquals(11, 14, descriptor.getClue(2));
    }


    @Test
    void computePossibilitiesWithCellTest2() {
        // 15 | 5 2

        CellWrapper[] wrappers = parse("__█___█____██__");
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(0, 10, descriptor.getClue(0));
        clueEquals(6, 13, descriptor.getClue(1));
    }

    @Test
    void computePossibilitiesWithCellTest3() {
        // 15 | 5 2 2

        CellWrapper[] wrappers = parse("__██_██___█_█__");
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

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

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(2, 7, descriptor.getClue(0));
        clueEquals(9, 11, descriptor.getClue(1));
        clueEquals(12, 14, descriptor.getClue(2));
    }

    @Test
    void computePossibilitiesWithCellTest4() {
        // 15 | 2 6 2

        CellWrapper[] wrappers = parse("__█X___█__X_█XX");
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();

        // cell then clue (2, 6, 2)
        boolean[][] expected = new boolean[][] {
                //              2     6      2
                new boolean[] {false, false, false},
                new boolean[] {true,  false, false},
                new boolean[] {true,  false, false},
                new boolean[] {false, false, false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, true,  false},
                new boolean[] {false, false,  false},
                new boolean[] {false, false, true},
                new boolean[] {false, false, true},
                new boolean[] {false, false, false},
                new boolean[] {false, false, false},
        };

        arrayEquals(expected, solver.getPossibilities());
        clueEquals(1, 3, descriptor.getClue(0));
        clueEquals(4, 10, descriptor.getClue(1));
        clueEquals(11, 13, descriptor.getClue(2));
    }



    // ******************************************
    // * optimizeCluesBoundWithOnePossibilities *
    // ******************************************

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest() {
        // 15 | 5 2

        CellWrapper[] wrappers = parse("__█___█____██__");
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

        // cell then clue (5, 2)
        boolean[][] expected = new boolean[][] {
                //             5      2
                new boolean[] {false,  false},
                new boolean[] {false,  false},
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

        arrayEquals(expected, solver.getPossibilities());
        assertEquals(2, descriptor.getClues()[0].getMinI());
        assertEquals(7, descriptor.getClues()[0].getMaxI());
        assertEquals(11, descriptor.getClues()[1].getMinI());
        assertEquals(13, descriptor.getClues()[1].getMaxI());
    }

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest2() {
        // 15 | 5 2 2

        CellWrapper[] wrappers = parse("__██_██___█_█__");
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

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

        arrayEquals(expected, solver.getPossibilities());
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
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = solver.split();

        assertEquals(1, regions.size());
        assertEquals(new Region(solver, 0, 15, 0, 3), regions.get(0));
    }

    @Test
    void splitTest2() {
        CellWrapper[] wrappers = parse("__█___█____██__");
        int[] clues = new int[] {5, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = solver.split();

        assertEquals(2, regions.size());
        assertEquals(new Region(solver, 2, 7, 0, 1), regions.get(0));
        assertEquals(new Region(solver, 11, 13, 1, 2), regions.get(1));
    }

    @Test
    void splitTest3() {
        CellWrapper[] wrappers = parse("__██_██___█_█__");
        int[] clues = new int[] {5, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = solver.split();

        assertEquals(3, regions.size());
        assertEquals(new Region(solver, 2, 7, 0, 1), regions.get(0));
        assertEquals(new Region(solver, 9, 11, 1, 2), regions.get(1));
        assertEquals(new Region(solver, 12, 14, 2, 3), regions.get(2));
    }












    @Test
    void completeTest1() {
        CellWrapper[] wrappers = parse("_____█______X_█");
        int[] clues = new int[] {2, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = solver.split();
        regions.get(1).trySolve();

        cellsEquals(wrappers, parse("_____█______X██"));
    }

    @Test
    void completeTest2() {
        CellWrapper[] wrappers = parse("X_____███______");
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.tryFill(List.of());
        cellsEquals(wrappers, parse("X_____████_____"));
    }








    @Test
    void isCompletedTest1() {
        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertFalse(descriptor.isCompleted());
    }

    @Test
    void isCompletedTest2() {
        CellWrapper[] wrappers = parse("_██X██████__██_");
        int[] clues = new int[] {2, 6, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertTrue(descriptor.isCompleted());
    }

    @Test
    void isCompletedTest3() {
        CellWrapper[] wrappers = parse("X█XXXX█XXX_██_█");
        int[] clues = new int[] {1, 1, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertTrue(descriptor.isCompleted());
    }

    @Test
    void isCompletedTest4() {
        CellWrapper[] wrappers = parse("__█____________");
        int[] clues = new int[] {};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertFalse(descriptor.isCompleted());
    }



    @Test
    void drawBetween() {
        CellWrapper[] wrappers = createEmpty(10);
        int[] clues = new int[] {};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.drawBetween(0, 10, 5);
        cellsEquals(wrappers, createEmpty(10));

        solver.drawBetween(0, 10, 6);
        cellsEquals(wrappers, parse("____██____"));
    }

    @Test
    void drawBetween2() {
        CellWrapper[] wrappers = createEmpty(20);
        int[] clues = new int[] {};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.drawBetween(3, 10, 5);
        cellsEquals(wrappers, parse("_____███____________"));

        solver.drawBetween(6, 15, 8);
        cellsEquals(wrappers, parse("_____█████████______"));
    }
}
