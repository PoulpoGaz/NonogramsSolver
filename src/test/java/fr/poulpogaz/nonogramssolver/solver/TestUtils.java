package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.solver.Cell;
import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Clue;
import fr.poulpogaz.nonogramssolver.solver.Region;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static fr.poulpogaz.nonogramssolver.solver.Cell.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static void arrayEquals(boolean[][] expected, boolean[][] current) {
        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected[i].length; j++) {
                Assertions.assertEquals(expected[i][j], current[i][j], "At (%d; %d)".formatted(i, j));
            }
        }
    }

    public static void clueEquals(int expectedMinI, int expectedMaxI, Clue clue) {
        assertEquals(expectedMinI, clue.getMinI());
        assertEquals(expectedMaxI, clue.getMaxI());
    }

    public static void cellsEquals(CellWrapper[] input, Cell... expected) {
        assertEquals(expected.length, input.length);

        for (int i = 0; i < input.length; i++) {
            assertEquals(expected[i], input[i].get(), "At: " + i);
        }
    }

    public static CellWrapper[] create(Cell... cells) {
        CellWrapper[] wrappers = new CellWrapper[cells.length];

        for (int i = 0; i < cells.length; i++) {
            wrappers[i] = new CellWrapper(cells[i], i, 0);
        }

        return wrappers;
    }

    public static CellWrapper[] createEmpty(int length) {
        CellWrapper[] wrappers = new CellWrapper[length];

        for (int i = 0; i < length; i++) {
            wrappers[i] = new CellWrapper(EMPTY, i, 0);
        }

        return wrappers;
    }

    public static void printRegions(List<Region> regions) {
        for (Region r : regions) {
            System.out.println(r);
            System.out.println();
        }
    }
}
