package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.Nonogram;
import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Clue;
import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static fr.poulpogaz.nonogramssolver.Cell.*;
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

    public static void cellsEquals(Description input, Cell... expected) {
        cellsEquals(input.getCells(), expected);
    }

    public static void cellsEquals(CellWrapper[] input, Cell... expected) {
        assertEquals(expected.length, input.length);

        for (int i = 0; i < input.length; i++) {
            assertEquals(expected[i], input[i].get(), "At: " + i);
        }
    }

    public static void cellsEquals(Description input, CellWrapper... expected) {
        cellsEquals(input.getCells(), expected);
    }

    public static void cellsEquals(CellWrapper[] input, CellWrapper... expected) {
        assertEquals(expected.length, input.length);

        for (int i = 0; i < input.length; i++) {
            assertEquals(expected[i].get(), input[i].get(), "At: " + i);
        }
    }

    public static Cell[] parseCell(String str) {
        Cell[] cells = new Cell[str.length()];

        for (int i = 0; i < str.length(); i++) {
            cells[i] = new Cell();

            switch (str.charAt(i)) {
                case ' ', '_' -> cells[i].setEmpty();
                case 'F', '█' -> cells[i].setFilled();
                case 'X' -> cells[i].setCrossed();
            }
        }

        return cells;
    }

    public static Cell[] createEmptyCell(int length) {
        Cell[] cells = new Cell[length];

        for (int i = 0; i < length; i++) {
            cells[i] = new Cell();
        }

        return cells;
    }

    public static Description parse(String str, int[] clues) {
        Description desc = create(str.length(), asObjectClues(clues));

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case ' ', '_' -> desc.setCell(i, EMPTY);
                case 'F', '█' -> desc.setCell(i, FILLED);
                case 'X' -> desc.setCell(i, CROSSED);
            }
        }

        return desc;
    }

    public static Description createEmpty(int length, int[] clues) {
        Description desc = create(length, asObjectClues(clues));

        for (int i = 0; i < length; i++) {
            desc.setCell(i, EMPTY);
        }

        return desc;
    }

    private static Object[] asObjectClues(int[] clues) {
        Object[] c = new Object[clues.length * 2];

        for (int i = 0; i < clues.length; i++) {
            c[2 * i] = clues[i];
            c[2 * i + 1] = Color.BLACK;
        }

        return c;
    }


    /**
     * Max ten colors
     */
    public static Description parse(String str, Object[] clues) {
        Description desc = create(str.length(), clues);

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case ' ', '_' -> desc.setCell(i, EMPTY);
                case 'F', '█' -> desc.setCell(i, FILLED);
                case 'X' -> desc.setCell(i, CROSSED);
                default -> {
                    int color = str.charAt(i) - '0';
                    desc.setCell(i, FILLED, color);
                }
            }
        }

        return desc;
    }

    public static Description createEmpty(int length, Object[] clues) {
        Description desc = create(length, clues);

        for (int i = 0; i < length; i++) {
            desc.setCell(i, EMPTY);
        }

        return desc;
    }

    /**
     * @param clues an array containing a clue (int) then a color (Color or int)
     */
    private static Description create(int length, Object[] clues) {
        if (clues.length % 2 != 0) {
            throw new IllegalStateException();
        }

        Nonogram.Builder builder = new Nonogram.Builder();
        builder.setWidth(length);
        builder.setHeight(1);
        builder.setNumberOfClue(0, true, clues.length / 2);

        for (int i = 0; i < clues.length; i += 2) {
            Color color;
            if (clues[i + 1] instanceof Color col) {
                color = col;
            } else {
                color = new Color((int) clues[i + 1]);
            }

            builder.addClue(0, true, (int) clues[i], color);
        }

        for (int i = 0; i < length; i++) {
            builder.setNumberOfClue(i, false, 0);
        }

        Nonogram n = builder.build();
        CellWrapper[] wrappers = new CellWrapper[length];

        for (int i = 0; i < length; i++) {
            wrappers[i] = new CellWrapper(n, i, 0);
        }

        return new Description(true, 0, n.getRows()[0], wrappers);
    }

    public static void printRegions(List<Region> regions) {
        for (Region r : regions) {
            System.out.println(r);
            System.out.println();
        }
    }
}
