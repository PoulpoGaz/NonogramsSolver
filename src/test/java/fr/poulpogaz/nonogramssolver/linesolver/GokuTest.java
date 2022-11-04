package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GokuTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();
    
    @Test
    void gokuBug1() {
        // must be crossed                                                                  here --> <--
        Description description = parse("█XX██XXXXXXXXXXXXX__________________________________________█XXX████████████████",
                new int[] {1, 2, 10, 2, 2, 1, 16});
        assertEquals(80, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(description, parseCell("█XX██XXXXXXXXXXXXX_________________________________________X█XXX████████████████"));

    }

    @Test
    void gokuBug2() {
        // must be crossed                             here --> <--
        Description description = parse("X_████████████████_XXX_██X____██X____________________________XXXXXXXXXXXXXXXXXXX",
                new int[] {17, 2, 3, 2, 5, 2, 7});
        assertEquals(80, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(description, parseCell("X_████████████████_XXXX██X____██X____________________________XXXXXXXXXXXXXXXXXXX"));
    }

    @Test
    void gokuBug3() {
        //                                                             must be filled here -->  <--
        Description description = parse("██████████████XX████████████████████X______██████████__██XXX█████████X████████X█",
                new int[]{14, 20, 1, 14, 9, 8, 1});
        assertEquals(80, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = solver.split();

        Region r3 = regions.get(3);
        r3.trySolve();

        cellsEquals(description, parseCell("██████████████XX████████████████████X______██████████████XXX█████████X████████X█"));
    }

    @Test
    void gokuBug4() {
        // must be crossed                       here -->  <-->    <--
        Description description = parse("██XX█████████X█X__X██X____XXXX██XXX███████X██XXXXX██XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                new int[] {2, 9, 1, 2, 2, 7, 2, 2});
        assertEquals(80, description.size());

        assertTrue(description.isCompleted());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(description, parseCell("██XX█████████X█XXXX██XXXXXXXXX██XXX███████X██XXXXX██XXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
    }
}
