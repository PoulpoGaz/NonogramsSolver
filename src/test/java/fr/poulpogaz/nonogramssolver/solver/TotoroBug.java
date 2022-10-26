package fr.poulpogaz.nonogramssolver.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.Cell.*;
import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
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
        //regions.get(0).trySolve();
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

    @Test
    void totoroBug2() {
        CellWrapper[] wrappers = create(
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED, FILLED,
                EMPTY,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED

        );
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

        cellsEquals(wrappers,
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED, FILLED,
                EMPTY,
                CROSSED,
                EMPTY, EMPTY, EMPTY, FILLED, EMPTY, EMPTY, FILLED, EMPTY,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED
        );
    }
}
