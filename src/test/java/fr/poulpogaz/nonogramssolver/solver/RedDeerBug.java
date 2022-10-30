package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Descriptor;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedDeerBug {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void redDeerBug() {
        // must be filled                                                   here --> <--
        CellWrapper[] wrappers = parse("XXXX██████XXXXXXXXX████XX████XXXXXXXXXXX______█████████_______XX_");
        assertEquals(65, wrappers.length);

        int[] clues = new int[] {6, 4, 4, 15, 1, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        solver.setDescriptor(descriptor);
        solver.shrink();
        solver.initClues2();
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(wrappers, parse("XXXX██████XXXXXXXXX████XX████XXXXXXXXXXX_____██████████_______XX_"));
    }
}
