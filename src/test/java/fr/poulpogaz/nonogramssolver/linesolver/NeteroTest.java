package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeteroTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void neteroBug() {
        // must be filled                                        here --> <--
        CellWrapper[] wrappers = parse("█XXXXXXXXXXXXX____________█____██____X█X████X█X██████X█XXXXX█████████XXX████████");
        assertEquals(80, wrappers.length);

        int[] clues = new int[] {1, 3, 2, 6, 1, 4, 1, 6, 1, 9, 8};

        Description description = new Description(false, 0, clues, wrappers);

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(wrappers, parse("█XXXXXXXXXXXXX____________█____███___X█X████X█X██████X█XXXXX█████████XXX████████"));
    }
}
