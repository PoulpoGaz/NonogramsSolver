package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Descriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotoroBug {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void totoroBug() {
        // must be crossed                          here --> <-- -->      <--
        CellWrapper[] wrappers = parse("_________________XX█__█XX_█X______X████████████████████████████████XXX");
        // must be filled                            here --> <-> <--
        assertEquals(70, wrappers.length);

        int[] clues = new int[] {3, 1, 2, 2, 32};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        solver.setDescriptor(descriptor);
        solver.shrink();
        solver.initClues();
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        for (Region r : regions) {
            r.trySolve();
        }

        cellsEquals(wrappers, parse("_________________XX█X██XX██XXXXXXXX████████████████████████████████XXX"));
    }

    @Test
    void totoroBug2() {
        // must be filled                                                  here --> <> <--
        CellWrapper[] wrappers = parse("█X_______________XXXXXXXXX_________███_X________X██X██XXXXXXXXXXXXXXX█");
        assertEquals(70, wrappers.length);

        int[] clues = new int[] {1, 1, 2, 8, 4, 1, 2, 2, 2, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        solver.setDescriptor(descriptor);
        solver.shrink();
        solver.initClues();
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        List<Region> regions = solver.split();
        regions.get(2).trySolve();

        cellsEquals(wrappers, parse("█X_______________XXXXXXXXX_________███_X___█__█_X██X██XXXXXXXXXXXXXXX█"));
    }
}
