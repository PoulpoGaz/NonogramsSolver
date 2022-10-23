package fr.poulpogaz.nonogramssolver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.Cell.*;
import static fr.poulpogaz.nonogramssolver.TestUtils.cellsEquals;
import static fr.poulpogaz.nonogramssolver.TestUtils.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GokuBug {

    @Test
    void gokuBug1() {
        CellWrapper[] wrappers = create(
                FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                EMPTY,
                FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED
        );
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{1, 2, 10, 2, 2, 1, 16};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = descriptor.split();

        Region r2 = regions.get(2);
        r2.trySolve();

        cellsEquals(wrappers,
                FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, // now crossed!!
                FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED
        );

    }

    @Test
    void gokuBug2() {
        CellWrapper[] wrappers = create(
                CROSSED, EMPTY,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                EMPTY, CROSSED, CROSSED, CROSSED,
                EMPTY, // must be crossed!
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED
        );
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{17, 2, 3, 2, 5, 2, 7};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = descriptor.split();

        Region r1 = regions.get(1);
        r1.trySolve();

        cellsEquals(wrappers,
                CROSSED, EMPTY,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                EMPTY, CROSSED, CROSSED, CROSSED,
                EMPTY, // must be crossed!
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED
        );

    }

    @Test
    void gokuBug3() {
        CellWrapper[] wrappers = create(
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                EMPTY, EMPTY, // must be filled!
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED
        );
        assertEquals(80, wrappers.length);

        int[] clues = new int[]{14, 20, 1, 14, 9, 8, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();

        List<Region> regions = descriptor.split();

        Region r3 = regions.get(3);
        r3.trySolve();

        cellsEquals(wrappers,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                FILLED, FILLED, // must be filled!
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED
        );
    }

    @Test
    void gokuBug4() {
        CellWrapper[] wrappers = create(
                FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED,
                CROSSED,
                EMPTY, EMPTY, // must be crossed
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY, // must be crossed
                CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED
        );
        assertEquals(80, wrappers.length);

        int[] clues = new int[] {2, 9, 1, 2, 2, 7, 2, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);
        assertTrue(descriptor.isCompleted());

        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.crossZeroCells();

        List<Region> regions = descriptor.split();
        System.out.println(regions);

        cellsEquals(wrappers,
                FILLED, FILLED,
                CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED,
                CROSSED,
                EMPTY, EMPTY,
                CROSSED,
                FILLED, FILLED,
                CROSSED,
                EMPTY, EMPTY, EMPTY, EMPTY,
                CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED,
                FILLED, FILLED, FILLED, FILLED, FILLED, FILLED, FILLED,
                CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED,
                FILLED, FILLED,
                CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED, CROSSED
        );
    }
}
