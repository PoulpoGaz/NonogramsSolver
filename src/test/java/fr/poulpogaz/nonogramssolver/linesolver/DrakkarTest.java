package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrakkarTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void drakkarBug1() {
        // must be filled                              here -->  <--
        Description description = parse("XXXX███X___X__█X███______█X_______█X█____XX______X█____█XXXX___XXXXX███X█XX",
                new int[] {3, 1, 1, 3, 4, 3, 1, 2, 1, 3, 3, 1});
        assertEquals(75, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(description, parseCell("XXXX███X___X__█X███____███X_______█X█____XX______X█____█XXXX___XXXXX███X█XX"));
    }
}
