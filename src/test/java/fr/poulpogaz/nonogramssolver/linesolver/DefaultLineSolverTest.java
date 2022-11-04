package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
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
        Description description = createEmpty(15, new int[] {2, 6, 2});
        solver.setDescriptor(description);
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
        clueEquals(0, 5, description.getClue(0));
        clueEquals(3, 12, description.getClue(1));
        clueEquals(10, 15, description.getClue(2));
    }


    @Test
    void computePossibilitiesTest2() {
        Description description = createEmpty(15, new int[] {5, 2});
        solver.setDescriptor(description);
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
        clueEquals(0, 12, description.getClue(0));
        clueEquals(6, 15, description.getClue(1));
    }

    @Test
    void computePossibilitiesTest3() {
        Description description = createEmpty(15, new int[] {2, 2, 2, 2});
        solver.setDescriptor(description);
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
        clueEquals(0, 6, description.getClue(0));
        clueEquals(3, 9, description.getClue(1));
        clueEquals(6, 12, description.getClue(2));
        clueEquals(9, 15, description.getClue(3));
    }




    @Test
    void computePossibilitiesWithCellTest1() {
        // 15
        Description description = parse("__█____█____█__",  new int[] {2, 6, 2});
        solver.setDescriptor(description);
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
        clueEquals(1, 4, description.getClue(0));
        clueEquals(4, 11, description.getClue(1));
        clueEquals(11, 14, description.getClue(2));
    }


    @Test
    void computePossibilitiesWithCellTest2() {
        // 15
        Description description = parse("__█___█____██__", new int[] {5, 2});
        solver.setDescriptor(description);
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
        clueEquals(0, 10, description.getClue(0));
        clueEquals(6, 13, description.getClue(1));
    }

    @Test
    void computePossibilitiesWithCellTest3() {
        // 15
        Description description = parse("__██_██___█_█__", new int[] {5, 2, 2});
        solver.setDescriptor(description);
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
        clueEquals(2, 7, description.getClue(0));
        clueEquals(9, 11, description.getClue(1));
        clueEquals(12, 14, description.getClue(2));
    }

    @Test
    void computePossibilitiesWithCellTest4() {
        // 15
        Description description = parse("__█X___█__X_█XX", new int[] {2, 6, 2});
        solver.setDescriptor(description);
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
        clueEquals(1, 3, description.getClue(0));
        clueEquals(4, 10, description.getClue(1));
        clueEquals(11, 13, description.getClue(2));
    }



    // ******************************************
    // * optimizeCluesBoundWithOnePossibilities *
    // ******************************************

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest() {
        // 15
        Description description = parse("__█___█____██__", new int[] {5, 2});
        solver.setDescriptor(description);
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
        assertEquals(2, description.getClues()[0].getMinI());
        assertEquals(7, description.getClues()[0].getMaxI());
        assertEquals(11, description.getClues()[1].getMinI());
        assertEquals(13, description.getClues()[1].getMaxI());
    }

    @Test
    void optimizeCluesBoundWithOnePossibilitiesTest2() {
        // 15
        Description description = parse("__██_██___█_█__", new int[] {5, 2, 2});
        solver.setDescriptor(description);
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
        clueEquals(2, 7, description.getClue(0));
        clueEquals(9, 11, description.getClue(1));
        clueEquals(12, 14, description.getClue(2));
    }



    // *********
    // * split *
    // *********

    @Test
    void splitTest1() {
        Description description = createEmpty(15, new int[] {2, 6, 2});
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = solver.split();

        assertEquals(1, regions.size());
        assertEquals(new Region(solver, 0, 15, 0, 3), regions.get(0));
    }

    @Test
    void splitTest2() {
        Description description = parse("__█___█____██__", new int[] {5, 2});
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        List<Region> regions = solver.split();

        assertEquals(2, regions.size());
        assertEquals(new Region(solver, 2, 7, 0, 1), regions.get(0));
        assertEquals(new Region(solver, 11, 13, 1, 2), regions.get(1));
    }

    @Test
    void splitTest3() {
        Description description = parse("__██_██___█_█__", new int[] {5, 2, 2});
        solver.setDescriptor(description);
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
        Description description = parse("_____█______X_█", new int[] {2, 2, 2});
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = solver.split();
        regions.get(1).trySolve();

        cellsEquals(description, parseCell("_____█______X██"));
    }

    @Test
    void completeTest2() {
        Description description = parse("X_____███______", new int[] {2, 6, 2});
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.tryFill(List.of());
        cellsEquals(description, parseCell("X_____████_____"));
    }








    @Test
    void isCompletedTest1() {
        Description description = createEmpty(15, new int[] {2, 6, 2});
        assertFalse(description.isCompleted());
    }

    @Test
    void isCompletedTest2() {
        Description description = parse("_██X██████__██_", new int[] {2, 6, 2});
        assertTrue(description.isCompleted());
    }

    @Test
    void isCompletedTest3() {
        Description description = parse("X█XXXX█XXX_██_█", new int[] {1, 1, 2, 1});
        assertTrue(description.isCompleted());
    }

    @Test
    void isCompletedTest4() {
        Description description = parse("__█____________", new int[0]);
        assertFalse(description.isCompleted());
    }



    @Test
    void drawBetween() {
        Description description = createEmpty(10, new int[0]);
        solver.setDescriptor(description);
        solver.drawBetween(0, 10, 5, 0);
        cellsEquals(description, createEmptyCell(10));

        solver.drawBetween(0, 10, 6, 0);
        cellsEquals(description, parseCell("____██____"));
    }

    @Test
    void drawBetween2() {
        Description description = createEmpty(20, new int[0]);

        solver.setDescriptor(description);
        solver.drawBetween(3, 10, 5, 0);
        cellsEquals(description, parseCell("_____███____________"));

        solver.drawBetween(6, 15, 8, 0);
        cellsEquals(description, parseCell("_____█████████______"));
    }
}
