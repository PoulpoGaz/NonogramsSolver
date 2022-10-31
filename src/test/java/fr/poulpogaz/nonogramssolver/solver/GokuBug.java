package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Descriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GokuBug {

    private static final DefaultLineSolver solver = new DefaultLineSolver();
    
    @Test
    void gokuBug1() {
        CellWrapper[] wrappers = parse("█XX██XXXXXXXXXXXXX__________________________________________█XXX████████████████");
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{1, 2, 10, 2, 2, 1, 16};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(wrappers, parse("█XX██XXXXXXXXXXXXX_________________________________________X█XXX████████████████"));

    }

    @Test
    void gokuBug2() {
        // must be crossed                            here --> <--
        CellWrapper[] wrappers = parse("X_████████████████_XXX_██X____██X____________________________XXXXXXXXXXXXXXXXXXX");
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{17, 2, 3, 2, 5, 2, 7};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(wrappers, parse("X_████████████████_XXXX██X____██X____________________________XXXXXXXXXXXXXXXXXXX"));
    }

    @Test
    void gokuBug3() {
        //                                                            must be filled here -->  <--
        CellWrapper[] wrappers = parse("██████████████XX████████████████████X______██████████__██XXX█████████X████████X█");
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{14, 20, 1, 14, 9, 8, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = solver.split();

        Region r3 = regions.get(3);
        r3.trySolve();

        cellsEquals(wrappers, parse("██████████████XX████████████████████X______██████████████XXX█████████X████████X█"));
    }

    @Test
    void gokuBug4() {
        // must be crossed                      here -->  <-->    <--
        CellWrapper[] wrappers = parse("██XX█████████X█X__X██X____XXXX██XXX███████X██XXXXX██XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        assertEquals(80, wrappers.length);

        int[] clues = new int[] {2, 9, 1, 2, 2, 7, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertTrue(descriptor.isCompleted());

        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.crossZeroCells();

        cellsEquals(wrappers, parse("██XX█████████X█XXXX██XXXXXXXXX██XXX███████X██XXXXX██XXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
    }
}
