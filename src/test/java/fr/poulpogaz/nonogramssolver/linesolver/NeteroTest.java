package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeteroTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void neteroBug() {
        // must be filled                                         here --> <--
        Description description = parse("█XXXXXXXXXXXXX____________█____██____X█X████X█X██████X█XXXXX█████████XXX████████",
                new int[] {1, 3, 2, 6, 1, 4, 1, 6, 1, 9, 8});
        assertEquals(80, description.size());

        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(description, parseCell("█XXXXXXXXXXXXX____________█____███___X█X████X█X██████X█XXXXX█████████XXX████████"));
    }
}
