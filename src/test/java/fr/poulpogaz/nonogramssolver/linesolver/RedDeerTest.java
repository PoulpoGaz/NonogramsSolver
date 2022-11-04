package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedDeerTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void redDeerBug() {
        // must be filled                                                    here --> <--
        Description description = parse("XXXX██████XXXXXXXXX████XX████XXXXXXXXXXX______█████████_______XX_",
                new int[] {6, 4, 4, 15, 1, 1});
        assertEquals(65, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(description, parseCell("XXXX██████XXXXXXXXX████XX████XXXXXXXXXXX_____██████████_______XX_"));
    }
}
