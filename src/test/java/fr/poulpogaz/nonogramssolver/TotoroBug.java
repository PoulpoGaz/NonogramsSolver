package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.Cell.*;
import static fr.poulpogaz.nonogramssolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TotoroBug {

    @Test
    void totoroBug() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED,
                FILLED,
                EMPTY, // must be crossed
                EMPTY,
                FILLED,
                CROSSED, CROSSED,
                EMPTY,
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED, CROSSED, CROSSED
        );
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
        regions.get(0).trySolve();
        printRegions(regions);

        cellsEquals(wrappers,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED,
                FILLED,
                CROSSED,
                EMPTY,
                FILLED,
                CROSSED, CROSSED,
                EMPTY,
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED, CROSSED, CROSSED
        );
    }
}
