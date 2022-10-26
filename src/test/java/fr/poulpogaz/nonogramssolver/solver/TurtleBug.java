package fr.poulpogaz.nonogramssolver.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.Cell.*;
import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
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

    @Test
    void turtleBug2() {
        CellWrapper[] wrappers = create(
                EMPTY, EMPTY, EMPTY,
                EMPTY, // must be filled
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED,
                CROSSED
        );
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {2, 2, 1, 2, 1, 1, 1, 1};

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
                EMPTY, EMPTY, EMPTY,
                FILLED, // filled
                FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED,
                FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED,
                CROSSED
        );
    }

    @Test
    void turtleBug3() {
        CellWrapper[] wrappers = create(
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY,
                CROSSED,
                FILLED,
                EMPTY, EMPTY,
                FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED,
                EMPTY,
                CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED, FILLED
        );
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {2, 2, 6, 2, 2, 1, 1, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        cellsEquals(wrappers,
                FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY,
                CROSSED,
                FILLED,
                EMPTY, CROSSED,
                FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED,
                EMPTY,
                CROSSED, CROSSED,
                FILLED,
                CROSSED,
                FILLED, FILLED
        );
    }

    @Test
    void turtleBug4() {
        CellWrapper[] wrappers = create(
                CROSSED,
                EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY,
                EMPTY, EMPTY, EMPTY, // must be crossed
                CROSSED, FILLED, CROSSED, FILLED, CROSSED, CROSSED, CROSSED, FILLED, CROSSED, CROSSED, FILLED, FILLED, CROSSED, CROSSED, CROSSED, FILLED
        );
        assertEquals(30, wrappers.length);

        int[] clues = new int[] {4, 2, 1, 1, 1, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        printRegions(descriptor.split());

        cellsEquals(wrappers,
                CROSSED,
                EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED,
                EMPTY,
                CROSSED, CROSSED, CROSSED, // must be crossed
                CROSSED, FILLED, CROSSED, FILLED, CROSSED, CROSSED, CROSSED, FILLED, CROSSED, CROSSED, FILLED, FILLED, CROSSED, CROSSED, CROSSED, FILLED
        );
    }
}
