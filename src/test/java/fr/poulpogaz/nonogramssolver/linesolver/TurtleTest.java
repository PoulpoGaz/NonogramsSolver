package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurtleTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();
    
    @Test
    void turtleBug() {
        // must be filled           here --> <--
        CellWrapper[] wrappers = parse("_____█XXX______X██X███X__██__X__█__");
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {3, 2, 2, 3, 4, 3};

        Description description = new Description(false, 0, clues, wrappers);

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        regions.get(0).trySolve();

        cellsEquals(wrappers, parse("____██XXX______X██X███X__██__X__█__"));
    }

    @Test
    void turtleBug2() {
        // must be filled          here --> <--
        CellWrapper[] wrappers = parse("____█X__________X█X██XX█X█XXXXX█X█X");
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {2, 2, 1, 2, 1, 1, 1, 1};

        Description description = new Description(false, 0, clues, wrappers);

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        regions.get(0).trySolve();

        cellsEquals(wrappers, parse("___██X__________X█X██XX█X█XXXXX█X█X"));
    }

    @Test
    void turtleBug3() {
        // must be crossed                            here --> <---> <--
        CellWrapper[] wrappers = parse("██X██X___________█_X█__█XXXX_XX█X██");
        // must be filled                 here -->  <--   --> <--
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {2, 2, 6, 2, 2, 1, 1, 2};

        Description description = new Description(false, 0, clues, wrappers);

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (AbstractRegion r : solver.split()) {
            r.trySolve();
        }
        cellsEquals(wrappers, parse("██X██X____██_____█_X██X█XXXXXXX█X██"));
    }

    @Test
    void turtleBug4() {
        // must be crossed                 here -->   <--
        CellWrapper[] wrappers = parse("X___█____█____X█X█XXX█XX██XXX█");
        assertEquals(30, wrappers.length);

        int[] clues = new int[] {4, 2, 1, 1, 1, 2, 1};

        Description description = new Description(false, 0, clues, wrappers);

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        cellsEquals(wrappers, parse("X___█____█_XXXX█X█XXX█XX██XXX█"));
    }
}
