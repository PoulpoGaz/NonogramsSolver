package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.createEmpty;
import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompleteLineSolverTest {

    private static final CompleteLineSolver solver = new CompleteLineSolver();

    @Test
    void emptyTest() {
        CellWrapper[] wrappers = createEmpty(15);
        int[] clues = new int[] {2, 6, 2};

        Description description = new Description(false, 0, clues, wrappers);
        solver.setDescriptor(description);
        solver.init();
        solver.fillPossibilities(0, 0);

        assertEquals("++++++███++++++", printPossibilities());
    }


    @Test
    void filledTest() {
        CellWrapper[] wrappers = parse("____█________█");
        int[] clues = new int[] {2, 2, 2};

        Description description = new Description(false, 0, clues, wrappers);
        solver.setDescriptor(description);
        solver.init();
        solver.fillPossibilities(0, 0);

        assertEquals("++++█++++++X██", printPossibilities());
    }

    @Test
    void filledTest2() {
        CellWrapper[] wrappers = parse("█___X_████_XXXX");
        int[] clues = new int[] {2, 5};

        Description description = new Description(false, 0, clues, wrappers);
        solver.setDescriptor(description);
        solver.init();
        solver.fillPossibilities(0, 0);

        assertEquals("██XXX+████+XXXX", printPossibilities());
    }

    private String printPossibilities() {
        StringBuilder sb = new StringBuilder(solver.getPossibilities().length);

        for (int possibility : solver.getPossibilities()) {
            switch (possibility) {
                case CompleteLineSolver.UNKNOWN -> sb.append('?');
                case CompleteLineSolver.CROSSED -> sb.append('X');
                case CompleteLineSolver.FILLED -> sb.append('█');
                case CompleteLineSolver.CROSSED_OR_FILLED -> sb.append('+');
            }
        }

        return sb.toString();
    }
}
