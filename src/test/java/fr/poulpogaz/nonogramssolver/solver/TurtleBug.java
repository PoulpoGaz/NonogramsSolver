package fr.poulpogaz.nonogramssolver.solver;

import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurtleBug {

    @Test
    void turtleBug() {
        // must be filled           here --> <--
        CellWrapper[] wrappers = parse("_____█XXX______X██X███X__██__X__█__");
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

        cellsEquals(wrappers, parse("____██XXX______X██X███X__██__X__█__"));
    }

    @Test
    void turtleBug2() {
        // must be filled          here --> <--
        CellWrapper[] wrappers = parse("____█X__________X█X██XX█X█XXXXX█X█X");
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

        cellsEquals(wrappers, parse("___██X__________X█X██XX█X█XXXXX█X█X"));
    }

    @Test
    void turtleBug3() {
        // must be crossed                            here --> <---> <--
        CellWrapper[] wrappers = parse("██X██X___________█_X█__█XXXX_XX█X██");
        assertEquals(35, wrappers.length);

        int[] clues = new int[] {2, 2, 6, 2, 2, 1, 1, 2};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        cellsEquals(wrappers, parse("██X██X___________█_X█_X█XXXX_XX█X██"));
    }

    @Test
    void turtleBug4() {
        // must be crossed                 here -->   <--
        CellWrapper[] wrappers = parse("X___█____█____X█X█XXX█XX██XXX█");
        assertEquals(30, wrappers.length);

        int[] clues = new int[] {4, 2, 1, 1, 1, 2, 1};

        Descriptor descriptor = new Descriptor(false, 0, clues, wrappers);

        descriptor.shrink();
        descriptor.initClues();
        descriptor.computePossibilities();
        descriptor.optimizeCluesBoundWithOnePossibility();
        descriptor.comparePossibilitiesAndLines(descriptor.createLines());
        descriptor.crossZeroCells();

        cellsEquals(wrappers, parse("X___█____█_XXXX█X█XXX█XX██XXX█"));
    }
}
