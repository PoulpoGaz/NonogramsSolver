package fr.poulpogaz.nonogramssolver.solver;

import fr.poulpogaz.nonogramssolver.Cell;
import fr.poulpogaz.nonogramssolver.Clue;
import fr.poulpogaz.nonogramssolver.Descriptor;

import java.util.Objects;

/**
 * A solver that gets all information from a line.
 */
public class CompleteLineSolver implements LineSolver {

    protected static final int UNKNOWN = 0;
    protected static final int CROSSED = 1;
    protected static final int FILLED = 2;
    protected static final int CROSSED_OR_FILLED = 3;

    private Descriptor descriptor;
    private int[] cluesPosition;
    private int[] possibilities;

    @Override
    public void trySolve(Descriptor descriptor) {
        setDescriptor(descriptor);

        init();
        fillPossibilities(0, 0);

        for (int i = 0; i < possibilities.length; i++) {
            if (possibilities[i] == FILLED) {
                descriptor.setCell(i, Cell.FILLED);
            } else if (possibilities[i] == CROSSED) {
                descriptor.setCell(i, Cell.CROSSED);
            }
        }

        cluesPosition = null;
        possibilities = null;
        this.descriptor = null;
    }

    protected void setDescriptor(Descriptor descriptor) {
        this.descriptor = Objects.requireNonNull(descriptor);
        cluesPosition = new int[descriptor.nClues()];
        possibilities = new int[descriptor.size()];
    }

    protected void init() {
        for (int i = 0; i < descriptor.size(); i++) {
            if (descriptor.isFilled(i)) {
                possibilities[i] = FILLED;
            } else if (descriptor.isCrossed(i)) {
                possibilities[i] = CROSSED;
            }
        }

        // copied from AbstractRegion#initClues
        int minI = 0;
        int maxI = descriptor.size() - descriptor.descriptorLength();

        for (int i = 0; i < descriptor.nClues(); i++) {
            Clue c = descriptor.getClue(i);

            if (i == 0) {
                maxI += c.getLength();
            } else {
                maxI += c.getLength() + 1;
                minI += descriptor.getClueLength(i - 1) + 1;
            }

            c.setMinI(minI);
            c.setMaxI(maxI);
        }
    }

    protected void fillPossibilities(int clueIndex, int pos) {
        Clue clue = descriptor.getClue(clueIndex);

        int max = clue.getMaxI() - clue.getLength();
        int limit = max;
        for (int i = pos; i <= limit; i++) {
            if (descriptor.isFilled(i) && limit == max) {
                limit = i;
            }

            if (fit(clue, i)) {
                cluesPosition[clueIndex] = i;

                if (clueIndex + 1 < descriptor.nClues()) {
                    fillPossibilities(clueIndex + 1, i + 1 + clue.getLength());
                } else {
                    boolean valid = true;
                    for (int j = i + clue.getLength(); j < descriptor.size(); j++) {
                        if (descriptor.isFilled(j)) {
                            valid = false;
                            break;
                        }
                    }

                    if (valid) {
                        merge();
                    }
                }
            }
        }
    }

    /**
     * Merge a valid arrangement of clues' position with possibilities
     */
    private void merge() {
        int min = 0;
        for (int i = 0; i < cluesPosition.length; i++) {
            Clue clue = descriptor.getClue(i);
            int pos = cluesPosition[i];

            for (int j = pos; j < pos + clue.getLength(); j++) {
                fill(j);
            }

            for (int j = min; j < pos; j++) {
                cross(j);
            }

            min = pos + clue.getLength();
        }

        for (int j = min; j < descriptor.size(); j++) {
            cross(j);
        }

        // printPossibilities();
    }

    private void fill(int j) {
        if (descriptor.isEmpty(j)) {
            if (possibilities[j] == CROSSED) {
                possibilities[j] = CROSSED_OR_FILLED;
            } else if (possibilities[j] == UNKNOWN) {
                possibilities[j] = FILLED;
            }
        }
    }

    private void cross(int j) {
        if (descriptor.isEmpty(j)) {
            if (possibilities[j] == FILLED) {
                possibilities[j] = CROSSED_OR_FILLED;
            } else if (possibilities[j] == UNKNOWN) {
                possibilities[j] = CROSSED;
            }
        }
    }

    /**
     * same as {@link AbstractRegion#fit(int, int)}
     */
    private boolean fit(Clue clue, int i) {
        int l = clue.getLength();

        if (i > 0 && descriptor.isFilled(i - 1)) {
            return false;
        } else if (i + l < descriptor.size() && descriptor.isFilled(i + l)) {
            return false;
        } else {
            for (int j = 0; j < l; j++) {
                if (descriptor.isCrossed(i + j)) {
                    return false;
                }
            }

            return true;
        }
    }

    protected void printPossibilities() {
        for (int possibility : possibilities) {
            switch (possibility) {
                case CompleteLineSolver.UNKNOWN -> throw new IllegalStateException();
                case CompleteLineSolver.CROSSED -> System.out.print("-");
                case CompleteLineSolver.FILLED -> System.out.print("#");
                case CompleteLineSolver.CROSSED_OR_FILLED -> System.out.print("+");
            }
        }
        System.out.println();
    }

    protected int[] getCluesPosition() {
        return cluesPosition;
    }

    protected int[] getPossibilities() {
        return possibilities;
    }
}
