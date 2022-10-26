package fr.poulpogaz.nonogramssolver.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotoroBug {

    @Test
    void totoroBug() {
        CellWrapper[] wrappers = parse("_________________XX█__█XX_█X______X████████████████████████████████XXX");
        assertEquals(70, wrappers.length);

        int[] clues = new int[] {3, 1, 2, 2, 32};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        List<Region> regions = descriptor.split();
        //regions.get(0).trySolve();
        printRegions(regions);

        cellsEquals(wrappers, parse("_________________XX█X_█XX_█X______X████████████████████████████████XXX"));
    }

    @Test
    void totoroBug2() {
        // must be filled                                                  here --> <> <--
        CellWrapper[] wrappers = parse("█X_______________XXXXXXXXX_________███_X________X██X██XXXXXXXXXXXXXXX█");
        assertEquals(70, wrappers.length);

        int[] clues = new int[] {1, 1, 2, 8, 4, 1, 2, 2, 2, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        List<Region> regions = descriptor.split();
        regions.get(2).trySolve();

        cellsEquals(wrappers, parse("█X_______________XXXXXXXXX_________███_X___█__█_X██X██XXXXXXXXXXXXXXX█"));
    }
}
