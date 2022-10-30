package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Clue;
import fr.poulpogaz.nonogramssolver.Descriptor;

public class SolverUtils {

    /**
     * Returns true if a line of length line can be put from {@code i} (included) to {@code i + line} (excluded).
     * It means that if there is a cell at {@code i - 1} it returns false. Same things at {@code i + line}
     *
     * @return true if a line of length line can be put at the position i
     */
    public static boolean fit(Descriptor desc, int line, int i) {
        int end = desc.size();

        if (i < 0 ||
                i + line > end ||
                (i > 0 && desc.isFilled(i - 1)) ||
                (i + line < end && desc.isFilled(i + line))) {
            return false;
        } else {
            for (int j = 0; j < line; j++) {
                if (desc.isCrossed(i + j)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean fit(Descriptor desc, Clue clue, int i) {
        return fit(desc, clue.getLength(), i);
    }

    /**
     * Returns true if a line of length line can be put from {@code i} (included) to {@code i - line} (excluded).
     */
    public static boolean fitReverse(Descriptor desc, int line, int i) {
        return fit(desc, line, i - line + 1);
    }

    public static boolean fitReverse(Descriptor desc, Clue clue, int i) {
        return fitReverse(desc, clue.getLength(), i);
    }
}
