package fr.poulpogaz.nonogramssolver;

public class Status {

    public static final int NO_CHANGE = 0;
    public static final int CHANGED = 1;
    public static final int CONTRADICTION = 2;

    public static boolean hasChanged(int status) {
        return (status & CHANGED) != 0;
    }

    public static boolean hasContradiction(int status) {
        return (status & CONTRADICTION) != 0;
    }
}
