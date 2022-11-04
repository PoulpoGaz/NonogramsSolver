package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.createEmpty;
import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompleteLineSolverTest {

    private static final CompleteLineSolver solver = new CompleteLineSolver();

    @Test
    void emptyTest() {
        Description description= createEmpty(15, new int[] {2, 6, 2});

        solver.setDescriptor(description);
        solver.init();
        solver.fillPossibilities(0, 0);

        assertEquals("++++++███++++++", printPossibilities());
    }


    @Test
    void filledTest() {
        Description description = parse("____█________█", new int[] {2, 2, 2});

        solver.setDescriptor(description);
        solver.init();
        solver.fillPossibilities(0, 0);

        assertEquals("++++█++++++X██", printPossibilities());
    }

    @Test
    void filledTest2() {
        Description description = parse("█___X_████_XXXX", new int[] {2, 5});

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
