package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotoroTest {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void totoroBug() {
        // must be crossed                           here --> <-- -->      <--
        Description description = parse("_________________XX█__█XX_█X______X████████████████████████████████XXX",
                new int[] {3, 1, 2, 2, 32});
        // must be filled                            here --> <-> <--
        assertEquals(70, description.size());
        
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        for (Region r : regions) {
            r.trySolve();
        }

        cellsEquals(description, parseCell("_________________XX█X██XX██XXXXXXXX████████████████████████████████XXX"));
    }

    @Test
    void totoroBug2() {
        // must be filled                                                   here --> <> <--
        Description description = parse("█X_______________XXXXXXXXX_________███_X________X██X██XXXXXXXXXXXXXXX█",
                new int[] {1, 1, 2, 8, 4, 1, 2, 2, 2, 2, 1});
        assertEquals(70, description.size());
        
        solver.setDescriptor(description);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        regions.get(3).trySolve();

        cellsEquals(description, parseCell("█X_______________XXXXXXXXX_________███_X___█__█_X██X██XXXXXXXXXXXXXXX█"));
    }
}
