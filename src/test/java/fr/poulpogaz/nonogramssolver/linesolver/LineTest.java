package fr.poulpogaz.nonogramssolver.linesolver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LineTest {

    @Test
    void connectedTest() {
        Line line = new Line(5, 10, 0);
        Assertions.assertFalse(line.connected(0, 4));
        Assertions.assertFalse(line.connected(11, 20));
        Assertions.assertFalse(line.connected(100, 1000));

        Assertions.assertTrue(line.connected(0, 5));
        Assertions.assertTrue(line.connected(5, 6));
        Assertions.assertTrue(line.connected(5, 10));
        Assertions.assertTrue(line.connected(5, 11));
        Assertions.assertTrue(line.connected(0, 10));
        Assertions.assertTrue(line.connected(5, 10));
        Assertions.assertTrue(line.connected(7, 10));
        Assertions.assertTrue(line.connected(10, 20));
        Assertions.assertTrue(line.connected(0, 20));
        Assertions.assertTrue(line.connected(0, 7));
        Assertions.assertTrue(line.connected(7, 20));
        Assertions.assertTrue(line.connected(7, 8));
    }
}
