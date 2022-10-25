package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.Cell.*;
import static fr.poulpogaz.nonogramssolver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurtleBug {

    @Test
    void turtleBug() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, EMPTY, EMPTY,
                EMPTY, // must be filled
                FILLED,
                CROSSED, CROSSED, CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY,
                FILLED, FILLED,
                EMPTY, EMPTY,
                CROSSED,
                EMPTY, EMPTY,
                FILLED,
                EMPTY, EMPTY
        );
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {3, 2, 2, 3, 4, 3};

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
                EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, // filled!
                FILLED,
                CROSSED, CROSSED, CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY,
                FILLED, FILLED,
                EMPTY, EMPTY,
                CROSSED,
                EMPTY, EMPTY,
                FILLED,
                EMPTY, EMPTY
        );
    }
}
