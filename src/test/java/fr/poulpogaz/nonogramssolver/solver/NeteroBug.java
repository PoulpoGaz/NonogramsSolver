package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.CellWrapper;
import fr.poulpogaz.nonogramssolver.Descriptor;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeteroBug {

    private static final DefaultLineSolver solver = new DefaultLineSolver();

    @Test
    void neteroBug() {
        // must be filled                                        here --> <--
        CellWrapper[] wrappers = parse("█XXXXXXXXXXXXX____________█____██____X█X████X█X██████X█XXXXX█████████XXX████████");
        assertEquals(80, wrappers.length);

        int[] clues = new int[] {1, 3, 2, 6, 1, 4, 1, 6, 1, 9, 8};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        solver.setDescriptor(descriptor);
        solver.computePossibilities();
        solver.optimizeCluesBoundWithOnePossibility();
        System.out.println(solver);
        solver.comparePossibilitiesAndLines(solver.createLines());
        solver.crossZeroCells();

        printRegions(solver.split());

        for (Region r : solver.split()) {
            r.trySolve();
        }

        cellsEquals(wrappers, parse("█XXXXXXXXXXXXX____________█____███___X█X████X█X██████X█XXXXX█████████XXX████████"));
    }
}
