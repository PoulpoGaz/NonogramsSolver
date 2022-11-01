package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Descriptor;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrakkarBug {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void drakkarBug1() {
        // must be filled                              here -->  <--
        CellWrapper[] wrappers = parse("XXXX███X___X__█X███______█X_______█X█____XX______X█____█XXXX___XXXXX███X█XX");
        assertEquals(75, wrappers.length);

        int[] clues = new int[] {3, 1, 1, 3, 4, 3, 1, 2, 1, 3, 3, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(wrappers, parse("XXXX███X___X__█X███____███X_______█X█____XX______X█____█XXXX___XXXXX███X█XX"));
    }
}
