package fr.poulpogaz.nonogramssolver.linesolver;

import fr.poulpogaz.nonogramssolver.solver.CellWrapper;
import fr.poulpogaz.nonogramssolver.solver.Description;
import org.junit.jupiter.api.Test;

import static fr.poulpogaz.nonogramssolver.linesolver.TestUtils.parse;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolverUtilsTest {

    @Test
    void fit() {
        CellWrapper[] wrappers = parse("__________"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertTrue(SolverUtils.fit(description, 5, 0));
        assertTrue(SolverUtils.fit(description, 5, 5));
        assertFalse(SolverUtils.fit(description, 5, 6));
    }

    @Test
    void fit2() {
        CellWrapper[] wrappers = parse("█_________"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertFalse(SolverUtils.fit(description, 5, 1));
        assertTrue(SolverUtils.fit(description, 5, 5));
        assertFalse(SolverUtils.fit(description, 5, 6));
    }

    @Test
    void fit3() {
        CellWrapper[] wrappers = parse("______X___"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertTrue(SolverUtils.fit(description, 5, 0));
        assertTrue(SolverUtils.fit(description, 5, 1));
        assertFalse(SolverUtils.fit(description, 5, 2));
        assertFalse(SolverUtils.fit(description, 5, 5));
    }


    @Test
    void fitReverse() {
        CellWrapper[] wrappers = parse("__________"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertFalse(SolverUtils.fitReverse(description, 5, 0));
        assertTrue(SolverUtils.fitReverse(description, 5, 5));
        assertTrue(SolverUtils.fitReverse(description, 5, 6));
        assertFalse(SolverUtils.fitReverse(description, 5, 10));
    }

    @Test
    void fitReverse2() {
        CellWrapper[] wrappers = parse("█_________"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertTrue(SolverUtils.fitReverse(description, 5, 4));
        assertFalse(SolverUtils.fitReverse(description, 5, 5));
    }

    @Test
    void fitReverse3() {
        CellWrapper[] wrappers = parse("______X___"); // 10

        Description description = new Description(false, 0, new int[0], wrappers);
        assertFalse(SolverUtils.fitReverse(description, 5, 7));
        assertFalse(SolverUtils.fitReverse(description, 5, 6));
        assertTrue(SolverUtils.fitReverse(description, 5, 5));
    }
}
